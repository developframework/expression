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
 * @author qiushui
 */
@SuppressWarnings("unchecked")
public abstract class ExpressionUtils {

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
        if (instance == null) {
            return null;
        }
        if (expression == null || expression == EmptyExpression.INSTANCE) {
            return instance;
        }
        Object value = instance;
        for (Expression singleExpression : expression.expressionTree()) {
            if (value == null) {
                break;
            } else if (singleExpression instanceof ObjectExpression) {
                value = getValueFromObjectOrMap(value, singleExpression.getName());
            } else if (singleExpression instanceof ArrayExpression) {
                value = getValueFromArray(value, (ArrayExpression) singleExpression);
            } else if (singleExpression instanceof MethodExpression) {
                value = getValueFromMethod(instance, value, (MethodExpression) singleExpression);
            }
        }
        return value;
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
            arrayObject = getValueFromObjectOrMap(instance, arrayExpression.getName());
        }
        for (int i : arrayExpression.getIndexArray()) {
            if (arrayObject == null) {
                break;
            } else {
                Class<?> clazz = arrayObject.getClass();
                if (clazz.isArray()) {
                    arrayObject = ((Object[]) arrayObject)[i];
                } else if (List.class.isAssignableFrom(clazz)) {
                    arrayObject = ((List) arrayObject).get(i);
                } else if (Set.class.isAssignableFrom(clazz)) {
                    ArrayList arrayList = new ArrayList<>((Set) arrayObject);
                    arrayList.sort(Comparator.comparingInt(Object::hashCode));
                    arrayObject = arrayList.get(i);
                } else {
                    throw new ExpressionException("The instance \"%s\" type \"%s\" is not array or List/Set", instance.toString(), clazz);
                }
            }
        }
        return arrayObject;
    }

    private static Object getValueFromMethod(Object rootInstance, Object instance, MethodExpression methodExpression) {
        final Object[] arguments = Stream
                .of(methodExpression.getArguments())
                .map(argumentExpression -> getValue(rootInstance, argumentExpression))
                .toArray(Object[]::new);
        try {
            return MethodUtils.invokeMethod(instance, true, methodExpression.getName(), arguments);
        } catch (Exception e) {
            throw new ExpressionException("%s invoke failed: %s", methodExpression.getName(), e.getMessage());
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
