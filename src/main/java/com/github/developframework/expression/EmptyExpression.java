package com.github.developframework.expression;

/**
 * 空表达式
 * @author qiuzhenhao
 * @date 2017/5/8
 */
public class EmptyExpression extends Expression{

    @Override
    public String toString() {
        return "";
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
