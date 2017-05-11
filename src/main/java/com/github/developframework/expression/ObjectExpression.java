package com.github.developframework.expression;

import lombok.Getter;

/**
 * 对象表达式
 * 示例： abc
 *
 * @author qiuzhenhao
 * @date 2017/5/6
 */
public class ObjectExpression extends Expression {

    /* 属性名称 */
    @Getter
    private String propertyName;

    public ObjectExpression(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String toString() {
        if (parentExpression == null) {
            return propertyName;
        }
        return parentExpression + "." + propertyName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        if(this.hasParentExpression()) {
            hash = hash * 31 + parentExpression.hashCode();
        }
        hash = hash * 31 + propertyName.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ObjectExpression) {
            ObjectExpression otherExpression = (ObjectExpression) obj;
            if(propertyName.equals(otherExpression.getPropertyName())) {
                if(this.hasParentExpression() && otherExpression.hasParentExpression()) {
                    return parentExpression.equals(otherExpression.getParentExpression());
                }
                return !this.hasParentExpression() && !this.hasParentExpression();
            }
        }
        return false;
    }
}
