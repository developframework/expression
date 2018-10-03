package com.github.developframework.expression;

import com.github.developframework.expression.exception.ExpressionException;
import develop.toolkit.utils.JavaBeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 表达式取值工具
 *
 * @author qiuzhenhao
 */
public final class ExpressionUtils {

    private ExpressionUtils() {
        throw new AssertionError("No " + getClass().getName() + " instances for you!");
    }

    /**
     * 获取值
     *
     * @param instance        实例
     * @param expressionValue 表达式字符串
     * @return
     */
    public static final Object getValue(Object instance, String expressionValue) {
        return getValue(instance, Expression.parse(expressionValue));
    }

    /**
     * 获取值
     *
     * @param instance        实例
     * @param expressionValue 表达式字符串
     * @param targetClass     目标类型
     * @param <T>
     * @return
     */
    public static final <T> T getValue(Object instance, String expressionValue, Class<T> targetClass) {
        return getValue(instance, Expression.parse(expressionValue), targetClass);
    }

    /**
     * 获取值
     *
     * @param instance    实例
     * @param expression  表达式
     * @param targetClass 目标类型
     * @param <T>
     * @return
     */
    public static final <T> T getValue(Object instance, Expression expression, Class<T> targetClass) {
        return (T) getValue(instance, expression);
    }

    /**
     * 获取值
     *
     * @param instance   实例
     * @param expression 表达式
     * @return 值
     */
    public static final Object getValue(Object instance, Expression expression) {
        Objects.requireNonNull(instance);
        if (expression == null) {
            throw new ExpressionException("expression is null.");
        }
        if (expression == Expression.EMPTY_EXPRESSION) {
            return instance;
        }
        Expression[] expressionTree = expression.expressionTree();
        Object tempObject = instance;
        for (Expression singleExpression : expressionTree) {
            if (tempObject == null) {
                return null;
            }
            if (singleExpression instanceof ObjectExpression) {
                tempObject = getValueFromObjectOrMap(tempObject, ((ObjectExpression) singleExpression).getPropertyName());
            } else if (singleExpression instanceof ArrayExpression) {
                tempObject = getValueFromArray(tempObject, (ArrayExpression) singleExpression);
            } else {
                // 空表达式 无操作
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
    private static final Object getValueFromObjectOrMap(Object instance, String propertyName) {
        Class<?> clazz = instance.getClass();
        if (Map.class.isAssignableFrom(clazz)) {
            return ((Map) instance).get(propertyName);
        }
        Field field = getDeclaredField(clazz, propertyName);
        return getFieldValue(field, instance);
    }

    /**
     * 从数组中获取值
     *
     * @param instance        实例
     * @param arrayExpression 表达式
     * @return 值
     */
    private static final Object getValueFromArray(Object instance, ArrayExpression arrayExpression) {
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
            throw new ExpressionException("The instance \"%s\" type is not array or List/Set.", instance.toString());
        }
    }

    private static Field getDeclaredField(final Class<?> clazz, String propertyName) {
        Class<?> temp = clazz;
        while (temp != Object.class) {
            try {
                Field field = temp.getDeclaredField(propertyName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                temp = temp.getSuperclass();
            }
        }
        throw new ExpressionException("No such field \"%s\" in class \"%s\".", propertyName, clazz.getName());
    }

    private static Object getFieldValue(Field field, Object instance) {
        String getterMethodName = JavaBeanUtils.getGetterMethodName(field.getName(), field.getType());
        Class<?> instanceClass = instance.getClass();
        try {
            Method getterMethod = instanceClass.getMethod(getterMethodName, new Class<?>[0]);
            return getterMethod.invoke(instance, new Class<?>[0]);
        } catch (NoSuchMethodException e) {
            try {
                return field.get(instance);
            } catch (IllegalAccessException e1) {
                throw new ExpressionException("Illegal access field \"%s\" in class \"%s\".", field.getName(), instanceClass.getName());
            }
        } catch (Exception e) {
            throw new ExpressionException("%s invoke failed.", getterMethodName);
        }
    }
}
