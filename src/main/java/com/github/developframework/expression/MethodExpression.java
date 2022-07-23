package com.github.developframework.expression;

import com.github.developframework.expression.exception.ExpressionParseException;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 方法表达式
 * 示例： a.abc(x, y)
 *
 * @author qiushui
 */
@Getter
public class MethodExpression extends Expression {

    /* 方法参数 */
    private final Expression[] arguments;

    protected MethodExpression(String expressionValue) {
        if (!isMethodExpression(expressionValue)) {
            throw new ExpressionParseException("The Expression \"%s\" is not a method expression.", expressionValue);
        }
        this.name = StringUtils.substringBefore(expressionValue, "(");
        final String argumentString = expressionValue.substring(expressionValue.indexOf("(") + 1, expressionValue.lastIndexOf(")"));
        this.arguments = Stream
                .of(argumentString.split(","))
                .map(Expression::parse)
                .filter(e -> e instanceof ObjectExpression || e instanceof ArrayExpression)
                .toArray(Expression[]::new);
        this.expressionValue = forExpressionValue(this.name, this.arguments);
    }

    protected MethodExpression(String methodName, Expression[] arguments) {
        this.name = methodName;
        this.arguments = arguments;
        this.expressionValue = forExpressionValue(this.name, this.arguments);
    }

    private String forExpressionValue(String methodName, Expression[] arguments) {
        return methodName + Stream.of(arguments).map(Expression::toString).collect(Collectors.joining(", ", "(", ")"));
    }


    /**
     * 检测expressionValue是否是方法型表达式
     *
     * @param expressionValue 表达式字符串
     * @return 检测结果
     */
    public static boolean isMethodExpression(String expressionValue) {
        return expressionValue != null && expressionValue.matches("^\\w+\\((\\w+(\\[\\d+])*(,\\w+(\\[\\d+])*)*)?\\)$");
    }
}
