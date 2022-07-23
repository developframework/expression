package com.github.developframework.expression;

import com.github.developframework.expression.exception.ExpressionParseException;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * 数组表达式
 * 示例： abc[i]
 *
 * @author qiuzhenhao
 */
@Getter
public class ArrayExpression extends Expression {

    /* 索引 */
    private final int[] indexArray;

    /**
     * 构造方法：根据表达式字符串创建数组表达式对象
     *
     * @param expressionValue 表达式字符串
     */
    protected ArrayExpression(String expressionValue) {
        super(expressionValue);
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
        this.indexArray = indexs.stream().mapToInt(Integer::intValue).toArray();
    }

    protected ArrayExpression(String propertyName, int[] indexArray) {
        super(propertyName + IntStream.of(indexArray).mapToObj(i -> "[" + i + "]").collect(Collectors.joining()));
        this.propertyName = propertyName;
        this.indexArray = indexArray;
    }

    /**
     * 判断是否有属性名称
     */
    public boolean hasPropertyName() {
        return !propertyName.isEmpty();
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
     * @param indexArray       索引
     * @return 数组表达式
     */
    public static ArrayExpression fromObject(ObjectExpression objectExpression, int[] indexArray) {
        ArrayExpression arrayExpression = new ArrayExpression(objectExpression.getPropertyName(), indexArray);
        arrayExpression.setParentExpression(objectExpression.getParentExpression());
        return arrayExpression;
    }
}
