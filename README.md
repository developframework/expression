# Expression说明文档

## 1. 概述

用于方便的从对象、数组、Map等结构中获取值

### 1.1. 引用

```xml
<dependency>
	<groupId>com.github.developframework</groupId>
	<artifactId>expression</artifactId>
	<version>${version.expression}</version>
</dependency>
```

### 1.2. 依赖项

- commons-lang3.jar
- lombok.jar

## 2. 简单示例

### 2.1. 表达式对象
```java
Expression expression = Expression.parse("user.name");
```

把字符串`user.name` 解析成一个表达式对象。

可以解析的表达式字符串有：

| 表达式字符串     | 描述                                                         |
| ---------------- | ------------------------------------------------------------ |
| `user.name`      | 从根对象（根对象是对象或Map）取得user实例的name属性          |
| `users[0].name`  | 从根对象（根对象是对象或Map）取得users数组实例的第1个元素的name属性 |
| `[0].name`       | 从根对象（根对象是数组）取得第1个元素的name属性              |
| `user.say(name)` | 从根对象（根对象是对象或Map）调用user实例的say方法，并入参name |
| `array[0][0]`    | 支持多维数组 @since 1.6.0                                    |

### 2.2. 取值

例如有如下结构的User实体类：

```java
@Data
public class User {

    private String name;

    private String[] emails;

    public User(String name, String[] emails) {
        this.name = name;
        this.emails = emails;
    }
}
```

有如下的数据结构关系：

```java
User peter = new User("Peter", new String[]{"peter@163.com", "peter@qq.com"});
User tom = new User("Tom", new String[]{"tom@163.com", "tom@qq.com"});
User[] users = new User[]{peter, tom};
Map<String, Object> map = new HashMap<>();
map.put("users", users);
```

说明：peter,tom两个User对象存放于users数组对象中，而users数组对象存放于map中，键值为"users"。

那么假如要从map中取得peter的第二个email值。普通java代码如下：

```java
String peterSecondEmail = ((User[])map.get("users"))[0].getMails()[1];
```

列为表达式：`users[0].emails[1]`

```java
String expressionValue = "users[0].emails[1]";
String peterSecondEmail = ExpressionUtils.getValue(map, expressionValue, String.class);
System.out.println(peterSecondEmail);
```

