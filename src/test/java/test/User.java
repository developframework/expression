package test;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class User {

    private final String name;

    private final int age;

    public String say() {
        return "Hi";
    }
}
