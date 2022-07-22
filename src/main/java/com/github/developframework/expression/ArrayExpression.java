package com.github.developframework.expression;

import com.github.developframework.expression.exception.ExpressionParseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;


/**
 * 数组表达式
 * 示例： abc[i]
 *
 * @author qiuzhenhao
 */
@Getter
@RequiredArgsConstructor
public class ArrayExpression extends Expression {

    /* 属性名称 */
    private final String propertyName;
    /* 索引 */
    private final int[] index;

    /**
     * 构造方法：根据表达式字符串创建数组表达式对象
     *
     * @param expressionValue 表达式字符串
     */
    protected ArrayExpression(String expressionValue) {
        if (!isArrayExpression(expressionValue)) {
            throw new ExpressionParseException("The expression \"%s\" is not a array type expression.", expressionValue);
        }
        final int bracketStart = expressionValue.indexOf("[");
        this.propertyName = expressionValue.substring(0, bracketStart);
        StringBuilder sb = new StringBuilder();
        List<Integer> indexs = new LinkedList<>();
        for (char c : expressionValue.substring(bracketStart).toCharArray()) {
            switch (c) {
                case '[':
                    sb.setLength(0);
                    break;
                case ']':
                    indexs.add(Integer.parseInt(sb.toString()));
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        this.index = indexs.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * 判断是否有属性名称
     */
    public boolean hasPropertyName() {
        return !propertyName.isEmpty();
    }

    @Override
    public String toString() {
        return (parentExpression == EmptyExpression.INSTANCE ? "" : parentExpression + ".") + propertyName + "[" + index + "]";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        if (this.hasParentExpression()) {
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
            if (propertyName.equals(otherExpression.getPropertyName()) && index == otherExpression.getIndex()) {
                return parentExpression.equals(otherExpression.getParentExpression());
            }
        }
        return false;
    }

    /**
     * 检测expressionValue是否是数组型表达式
     *
     * @param expressionValue 表达式字符串
     * @return 检测结果
     */
    public static boolean isArrayExpression(String expressionValue) {
        return expressionValue.matches("^\\w*(\\[\\d+])+$");
    }

    /**
     * 从ObjectExpression转化
     *
     * @param objectExpression 对象表达式
     * @param index            索引
     * @return 数组表达式
     */
    public static ArrayExpression fromObject(ObjectExpression objectExpression, int index) {
        ArrayExpression arrayExpression = new ArrayExpression(objectExpression.getPropertyName(), index);
        arrayExpression.setParentExpression(objectExpression.getParentExpression());
        return arrayExpression;
    }
}
