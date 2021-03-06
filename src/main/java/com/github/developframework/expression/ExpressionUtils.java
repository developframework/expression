package com.github.developframework.expression;

import com.github.developframework.expression.exception.ExpressionException;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

/**
 * 表达式取值工具
 *
 * @author qiuzhenhao
 */
@SuppressWarnings("unchecked")
public final class ExpressionUtils {

    private ExpressionUtils() {
        throw new AssertionError("No " + getClass().getName() + " instances for you!");
    }

    /**
     * 获取值
     *
     * @param instance        实例
     * @param expressionValue 表达式字符串
     * @return 值
     */
    public static Object getValue(Object instance, String expressionValue) {
        return getValue(instance, Expression.parse(expressionValue));
    }

    /**
     * 获取值
     *
     * @param instance        实例
     * @param expressionValue 表达式字符串
     * @param targetClass     目标类型
     * @param <T>             值类型
     * @return 值
     */
    public static <T> T getValue(Object instance, String expressionValue, Class<T> targetClass) {
        return getValue(instance, Expression.parse(expressionValue), targetClass);
    }

    /**
     * 获取值
     *
     * @param instance    实例
     * @param expression  表达式
     * @param targetClass 目标类型
     * @param <T> 值类型
     * @return 值
     */
    public static <T> T getValue(Object instance, Expression expression, Class<T> targetClass) {
        return (T) getValue(instance, expression);
    }

    /**
     * 获取值
     *
     * @param instance   实例
     * @param expression 表达式
     * @return 值
     */
    public static Object getValue(Object instance, Expression expression) {
        if (expression == null) {
            throw new ExpressionException("expression is null");
        }
        if (expression == EmptyExpression.INSTANCE) {
            return instance;
        }
        Object tempObject = instance;
        for (Expression singleExpression : expression.expressionTree()) {
            if (tempObject == null) {
                return null;
            } else if (singleExpression instanceof ObjectExpression) {
                tempObject = getValueFromObjectOrMap(tempObject, ((ObjectExpression) singleExpression).getPropertyName());
            } else if (singleExpression instanceof ArrayExpression) {
                tempObject = getValueFromArray(tempObject, (ArrayExpression) singleExpression);
            } else if (singleExpression instanceof MethodExpression) {
                tempObject = getValueFromMethod(instance, tempObject, (MethodExpression) singleExpression);
            }
        }
        return tempObject;
    }

    /**
     * 从对象或Map中获取值
     *
     * @param instance     实例
     * @param propertyName 属性名称
     * @return 值
     */
    @SuppressWarnings("rawtypes")
    private static Object getValueFromObjectOrMap(Object instance, String propertyName) {
        Class<?> clazz = instance.getClass();
        if (Map.class.isAssignableFrom(clazz)) {
            return ((Map) instance).get(propertyName);
        }
        return getFieldValue(getDeclaredField(clazz, propertyName), instance);
    }

    /**
     * 从数组中获取值
     *
     * @param instance        实例
     * @param arrayExpression 表达式
     * @return 值
     */
    @SuppressWarnings("rawtypes")
    private static Object getValueFromArray(Object instance, ArrayExpression arrayExpression) {
        Object arrayObject = instance;
        if (arrayExpression.hasPropertyName()) {
            arrayObject = getValueFromObjectOrMap(instance, arrayExpression.getPropertyName());
        }
        if (arrayObject == null) {
            return null;
        }
        Class<?> clazz = arrayObject.getClass();
        if (clazz.isArray()) {
            return ((Object[]) arrayObject)[arrayExpression.getIndex()];
        } else if (List.class.isAssignableFrom(clazz)) {
            return ((List) arrayObject).get(arrayExpression.getIndex());
        } else if (Set.class.isAssignableFrom(clazz)) {
            ArrayList arrayList = new ArrayList<>((Set) arrayObject);
            arrayList.sort(Comparator.comparingInt(Object::hashCode));
            return arrayList.get(arrayExpression.getIndex());
        } else {
            throw new ExpressionException("The instance \"%s\" type is not array or List/Set", instance.toString());
        }
    }

    private static Object getValueFromMethod(Object rootInstance, Object instance, MethodExpression methodExpression) {
        final Object[] arguments = Stream
                .of(methodExpression.getArguments())
                .map(argumentExpression -> getValue(rootInstance, argumentExpression))
                .toArray(Object[]::new);
        try {
            return MethodUtils.invokeMethod(instance, true, methodExpression.getMethodName(), arguments);
        } catch (Exception e) {
            throw new ExpressionException("%s invoke failed: %s", methodExpression.getMethodName(), e.getMessage());
        }
    }

    private static Field getDeclaredField(final Class<?> clazz, String propertyName) {
        Class<?> temp = clazz;
        do {
            Field field = FieldUtils.getDeclaredField(temp, propertyName, true);
            if (field != null) {
                return field;
            } else {
                temp = temp.getSuperclass();
            }
        } while (temp != Object.class);
        throw new ExpressionException("No such field \"%s\" in class \"%s\"", propertyName, clazz.getName());
    }

    private static Object getFieldValue(Field field, Object instance) {
        String getterMethodName = getGetterMethodName(field.getName(), field.getType());
        try {
            return MethodUtils.invokeMethod(instance, true, getterMethodName);
        } catch (NoSuchMethodException e) {
            try {
                return field.get(instance);
            } catch (IllegalAccessException e1) {
                throw new ExpressionException("Illegal access field \"%s\" in class \"%s\"", field.getName(), instance.getClass().getName());
            }
        } catch (Exception e) {
            throw new ExpressionException("%s invoke failed.", getterMethodName);
        }
    }

    private static String getGetterMethodName(String property, Class<?> javaType) {
        StringBuilder sb = new StringBuilder();
        sb.append(property);
        if (Character.isLowerCase(sb.charAt(0))) {
            if (sb.length() == 1 || !Character.isUpperCase(sb.charAt(1))) {
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            }
        }
        if (javaType == boolean.class || javaType == Boolean.class) {
            sb.insert(0, "is");
        } else {
            sb.insert(0, "get");
        }
        return sb.toString();
    }
}
