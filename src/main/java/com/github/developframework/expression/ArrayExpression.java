package com.github.developframework.expression;

import com.github.developframework.expression.exception.ExpressionParseException;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;


/**
 * 数组表达式
 * 示例： abc[i]
 * @author qiuzhenhao
 */
public class ArrayExpression extends Expression{

    /* 属性名称 */
    @Getter
    private String propertyName;
    /* 索引 */
    @Getter
    private int index;

    /**
     * 构造方法：根据参数创建数组表达式对象
     * @param propertyName 属性名称
     * @param index 索引
     */
    public ArrayExpression(@NonNull String propertyName, int index) {
        this.propertyName = propertyName;
        this.index = index;
    }

    /**
     * 构造方法：根据表达式字符串创建数组表达式对象
     * @param expressionValue 表达式字符串
     */
    public ArrayExpression(@NonNull String expressionValue) {
        if (!isArrayExpression(expressionValue)) {
            throw new ExpressionParseException("The Expression \"%s\" is not a array type expression.", expressionValue);
        }
        this.propertyName = StringUtils.substringBefore(expressionValue, "[");
        try {
            this.index = new Integer(StringUtils.substringBetween(expressionValue, "[", "]")).intValue();
        } catch(NumberFormatException e) {
            throw new ExpressionParseException("The Expression \"%s\": index is not a number.", expressionValue);
        }
    }

    /**
     * 检测expressionValue是否是数组型表达式
     * @param expressionValue 表达式字符串
     * @return 检测结果
     */
    public final static boolean isArrayExpression(String expressionValue) {
        return expressionValue.matches("^\\w*(\\[\\w+\\])+$");
    }

    /**
     * 从ObjectExpression转化
     * @param objectExpression 对象表达式
     * @param index 索引
     * @return 数组表达式
     */
    public static final ArrayExpression fromObject(ObjectExpression objectExpression, int index) {
        ArrayExpression arrayExpression = new ArrayExpression(objectExpression.getPropertyName(), index);
        arrayExpression.setParentExpression(objectExpression.getParentExpression());
        return arrayExpression;
    }

    /**
     * 判断是否有属性名称
     * @return
     */
    public boolean hasPropertyName() {
        return StringUtils.isNotBlank(propertyName);
    }

    @Override
    public String toString() {
        if(parentExpression == Expression.EMPTY_EXPRESSION) {
            return propertyName + "[" + index + "]";
        }
        return parentExpression + "." + propertyName + "[" + index + "]";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        if(this.hasParentExpression()) {
            hash = hash * 31 + parentExpression.hashCode();
        }
        hash = hash * 31 + propertyName.hashCode();
        hash = hash * 31 + index;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArrayExpression) {
            ArrayExpression otherExpression = (ArrayExpression) obj;
            if(propertyName.equals(otherExpression.getPropertyName()) && index == otherExpression.getIndex()) {
                return parentExpression.equals(otherExpression.getParentExpression());
            }
        }
        return false;
    }
}
