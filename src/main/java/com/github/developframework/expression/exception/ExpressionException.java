package com.github.developframework.expression.exception;

import develop.toolkit.exception.FormatRuntimeException;

/**
 * 表达式异常
 * @author qiuzhenhao
 */
public class ExpressionException extends FormatRuntimeException {

    public ExpressionException(String message) {
        super(message);
    }

    public ExpressionException(String format, Object... objs) {
        super(format, objs);
    }
}
