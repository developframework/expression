package com.github.developframework.expression;

/**
 * 空表达式
 *
 * @author qiuzhenhao
 */
public class EmptyExpression extends Expression {

    public static final EmptyExpression INSTANCE = new EmptyExpression();

    public EmptyExpression() {
        super("");
    }
}
