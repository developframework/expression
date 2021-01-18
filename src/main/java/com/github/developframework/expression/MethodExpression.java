package com.github.developframework.expression;

import com.github.developframework.expression.exception.ExpressionParseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

/**
 * 方法表达式
 * 示例： a.abc(x, y)
 *
 * @author qiuzhenhao
 */
@Getter
@RequiredArgsConstructor
public class MethodExpression extends Expression {

    /* 方法名称 */
    private final String methodName;

    /* 方法参数 */
    private final Expression[] arguments;

    protected MethodExpression(String expressionValue) {
        if (!isMethodExpression(expressionValue)) {
            throw new ExpressionParseException("The Expression \"%s\" is not a method expression.", expressionValue);
        }
        this.methodName = StringUtils.substringBefore(expressionValue, "(");
        String argumentString = expressionValue.substring(expressionValue.indexOf("(") + 1, expressionValue.lastIndexOf(")"));
        this.arguments = Stream
                .of(argumentString.split("\\s*,\\s*"))
                .map(Expression::parse)
                .filter(e -> e != EmptyExpression.INSTANCE)
                .toArray(Expression[]::new);
    }

    @Override
    public String toString() {
        if (parentExpression == EmptyExpression.INSTANCE) {
            return methodName;
        }
        return parentExpression + "." + methodName + "(" + StringUtils.join(arguments, ",") + ")";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        if (this.hasParentExpression()) {
            hash = hash * 31 + parentExpression.hashCode();
        }
        hash = hash * 31 + methodName.hashCode();
        for (Expression argument : arguments) {
            hash = hash * 31 + argument.hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MethodExpression) {
            MethodExpression otherExpression = (MethodExpression) obj;
            if (parentExpression.equals(otherExpression.getParentExpression()) && methodName.equals(otherExpression.methodName) && arguments.length == otherExpression.arguments.length) {
                for (int i = 0; i < arguments.length; i++) {
                    if (!arguments[i].equals(otherExpression.arguments[i])) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 检测expressionValue是否是方法型表达式
     *
     * @param expressionValue 表达式字符串
     * @return 检测结果
     */
    public static boolean isMethodExpression(String expressionValue) {
        return expressionValue.matches("^\\w+\\((.+(\\s*,\\s*.+)*)?\\)$");
    }
}
