package com.github.developframework.expression;

import lombok.Getter;
import lombok.NonNull;

/**
 * 对象表达式
 * 示例： abc
 *
 * @author qiuzhenhao
 */
public class ObjectExpression extends Expression {

    /* 属性名称 */
    @Getter
    private String propertyName;

    protected ObjectExpression(@NonNull String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String toString() {
        if (parentExpression == EmptyExpression.INSTANCE) {
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
        if(this == obj) {
            return true;
        }
        if (obj instanceof ObjectExpression) {
            ObjectExpression otherExpression = (ObjectExpression) obj;
            if(propertyName.equals(otherExpression.getPropertyName())) {
                return parentExpression.equals(otherExpression.getParentExpression());
            }
        }
        return false;
    }
}
