package test;

import com.github.developframework.expression.ExpressionUtils;

import java.util.Map;

public class Main {

    public static void main(String[] args) {
        User user = new User("xxx");
        Integer[][] array = new Integer[2][2];
        array[0][0] = 1;
        array[0][1] = 2;
        array[1][0] = 3;
        array[1][1] = 4;
        Integer[] list = new Integer[]{1, 2, 3};
        Object value = ExpressionUtils.getValue(
                Map.of(
                        "user", user,
                        "info1", "fafaffn",
                        "info2", "aoprjqprj",
                        "array", array,
                        "list", list
                ),
                "array[1][1]");
        System.out.println(value);
    }

}
