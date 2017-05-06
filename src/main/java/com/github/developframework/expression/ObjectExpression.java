package com.github.developframework.expression;

import lombok.Getter;

/**
 * 对象表达式
 * 示例： abc
 * @author qiuzhenhao
 * @date 2017/5/6
 */
public class ObjectExpression extends Expression{

    /* 属性名称 */
    @Getter
    private String propertyName;

    public ObjectExpression(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String toString() {
        if(parentExpression == null) {
            return propertyName;
        }
        return parentExpression + "." + propertyName;
    }
}
