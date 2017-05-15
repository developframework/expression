package com.github.developframework.expression.exception;

/**
 * 表达式异常
 * @author qiuzhenhao
 */
public class ExpressionException extends RuntimeException{

    public ExpressionException(String message) {
        super(message);
    }

    public ExpressionException(String format, Object... objs) {
        super(String.format(format, objs));
    }
}
