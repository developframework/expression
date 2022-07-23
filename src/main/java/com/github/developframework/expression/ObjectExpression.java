package com.github.developframework.expression;

/**
 * 对象表达式
 * 示例： abc
 *
 * @author qiushui
 */
public class ObjectExpression extends Expression {

    protected ObjectExpression(String expressionValue) {
        this.expressionValue = expressionValue;
        this.name = expressionValue;
    }

    /**
     * 检测expressionValue是否是对象型表达式
     *
     * @param expressionValue 表达式字符串
     * @return 检测结果
     */
    public static boolean isObjectExpression(String expressionValue) {
        return expressionValue != null && expressionValue.matches("^\\w+$");
    }

}
