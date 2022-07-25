package test;

import com.github.developframework.expression.ArrayExpression;
import com.github.developframework.expression.Expression;
import com.github.developframework.expression.ExpressionUtils;
import com.github.developframework.expression.MethodExpression;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author qiushui on 2022-07-22.
 */
public class JunitTest {

    @Test
    public void testRegex() {
        Assert.assertTrue(ArrayExpression.isArrayExpression("abc[0][1][2]"));
        Assert.assertTrue(MethodExpression.isMethodExpression("method(abc[0][1][2],xyz)"));
    }

    @Test
    public void testEqual() {
        final String[] expressionValues = new String[]{
                null,
                "",
                "user.name",
                "user.array[0][1][2]",
                "user.say(a,b[0])"
        };
        for (String expressionValue : expressionValues) {
            Expression expression1 = Expression.parse(expressionValue);
            Expression expression2 = Expression.parse(expressionValue);
            Assert.assertEquals(expression1, expression2);
        }
    }

    @Test
    public void testEntity() {
        User user = new User("a", 20);
        String name = ExpressionUtils.getValue(Map.of("user", user), "user.name", String.class);
        Assert.assertEquals(name, "a");
    }

    @Test
    public void testArray() {
        List<String[][]> list = new ArrayList<>();
        list.add(
                new String[][]{
                        new String[]{"b"},
                        null
                }
        );
        Map<String, List<String[][]>> map = Map.of("list", list);
        String item1 = ExpressionUtils.getValue(map, "list[0][0][0]", String.class);
        String item2 = ExpressionUtils.getValue(map, "list[0][1][0]", String.class);
        Assert.assertEquals(item1, "b");
        Assert.assertNull(item2);
    }

    @Test
    public void testMethod() {
        List<User> users = List.of(new User("a", 20));
        String say = ExpressionUtils.getValue(Map.of("data", Map.of("users", users)), "data.users[0].say()", String.class);
        Assert.assertEquals(say, "Hi");
    }

}
