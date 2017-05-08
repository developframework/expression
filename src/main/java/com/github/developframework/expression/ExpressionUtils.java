package com.github.developframework.expression;

import com.github.developframework.expression.exception.ExpressionException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * 表达式取值工具
 *
 * @author qiuzhenhao
 * @date 2017/5/6
 */
public final class ExpressionUtils {

    private ExpressionUtils() {
        throw new AssertionError("No " + getClass().getName() + " instances for you!");
    }

    /**
     * 获取值
     * @param instance 实例
     * @param expressionValue 表达式字符串
     * @return
     */
    public static final Object getValue(Object instance, String expressionValue) {
        return getValue(instance, Expression.parse(expressionValue));
    }

    /**
     * 获取值
     * @param instance 实例
     * @param expressionValue 表达式字符串
     * @param targetClass 目标类型
     * @param <T>
     * @return
     */
    public static final <T> T getValue(Object instance, String expressionValue, Class<T> targetClass) {
        return (T) getValue(instance, Expression.parse(expressionValue));
    }

    /**
     * 获取值
     * @param instance 实例
     * @param expression 表达式
     * @param targetClass 目标类型
     * @param <T>
     * @return
     */
    public static final <T> T getValue(Object instance, Expression expression, Class<T> targetClass) {
        return (T) getValue(instance, expression);
    }

    /**
     * 获取值
     * @param instance 实例
     * @param expression 表达式
     * @return 值
     */
    public static final Object getValue(Object instance, Expression expression) {
        Expression[] expressionTree = expression.expressionTree();
        Object tempObject = instance;
        for (Expression singleExpression : expressionTree) {
            if (singleExpression instanceof ArrayExpression) {
                tempObject = getValueFromArray(tempObject, (ArrayExpression) singleExpression);
            } else {
                tempObject = getValueFromObjectOrMap(tempObject, ((ObjectExpression) singleExpression).getPropertyName());
            }
        }
        return tempObject;
    }

    /**
     * 从对象或Map中获取值
     *
     * @param instance 实例
     * @param propertyName 属性名称
     * @return 值
     */
    private static final Object getValueFromObjectOrMap(Object instance, String propertyName) {
        Class<?> clazz = instance.getClass();
        if(Map.class.isAssignableFrom(clazz)) {
            return ((Map) instance).get(propertyName);
        }
        try {
            Field field = clazz.getDeclaredField(propertyName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (NoSuchFieldException e) {
            throw new ExpressionException("No such field \"%s\" in class \"%s\".", propertyName, clazz.getName());
        } catch (IllegalAccessException e) {
            throw new ExpressionException("Illegal access field \"%s\" in class \"%s\".", propertyName, clazz.getName());
        }
    }

    /**
     * 从数组中获取值
     *
     * @param instance 实例
     * @param arrayExpression 表达式
     * @return 值
     */
    private static final Object getValueFromArray(Object instance, ArrayExpression arrayExpression) {
        Object arrayObject = instance;
        if(arrayExpression.hasPropertyName()) {
            arrayObject = getValueFromObjectOrMap(instance, arrayExpression.getPropertyName());
        }
        Class<?> clazz = arrayObject.getClass();
        if (clazz.isArray()) {
            return ((Object[]) arrayObject)[arrayExpression.getIndex()];
        } else if (List.class.isAssignableFrom(clazz)) {
            return ((List) arrayObject).get(arrayExpression.getIndex());
        } else {
            throw new ExpressionException("The instance \"%s\" type is not array or List.", instance.toString());
        }
    }
}
