package com.github.developframework.expression;

/**
 * 对象表达式
 * 示例： abc
 *
 * @author qiuzhenhao
 */
public class ObjectExpression extends Expression {

    protected ObjectExpression(String expressionValue) {
        super(expressionValue);
        this.propertyName = expressionValue;
    }

}
