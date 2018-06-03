package com.github.developframework.expression.exception;

/**
 * 表达式解析异常
 * @author qiuzhenhao
 */
public class ExpressionParseException extends ExpressionException{

    public ExpressionParseException(String format, Object... objs) {
        super(format, objs);
    }
}
