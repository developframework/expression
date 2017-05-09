package com.github.developframework.expression.exception;

/**
 * 表达式解析异常
 * @author qiuzhenhao
 * @date 2017/5/6
 */
public class ExpressionParseException extends ExpressionException{

    public ExpressionParseException(String format, Object... objs) {
        super(String.format(format, objs));
    }
}
