package com.github.developframework.expression;

import org.apache.commons.lang3.StringUtils;

/**
 * 空表达式
 *
 * @author qiushui
 */
public class EmptyExpression extends Expression {

    public static final EmptyExpression INSTANCE = new EmptyExpression();

    private EmptyExpression() {
        expressionValue = "";
    }

    /**
     * 检测expressionValue是否是空表达式
     *
     * @param expressionValue 表达式字符串
     * @return 检测结果
     */
    public static boolean isEmptyExpression(String expressionValue) {
        return StringUtils.isEmpty(expressionValue);
    }
}
