package test;

import com.github.developframework.expression.ArrayExpression;
import com.github.developframework.expression.Expression;

/**
 * @author qiushui on 2022-07-22.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println(ArrayExpression.isArrayExpression("users[1][1]"));
        final Expression expression = Expression.parse("users[1][1]");
    }
}
