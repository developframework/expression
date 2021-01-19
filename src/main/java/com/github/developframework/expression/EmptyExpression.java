package com.github.developframework.expression;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 空表达式
 *
 * @author qiuzhenhao
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyExpression extends Expression {

    public static final EmptyExpression INSTANCE = new EmptyExpression();

    @Override
    public String toString() {
        return "";
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
