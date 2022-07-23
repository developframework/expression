module expression.test {

    requires expression;
    requires org.apache.commons.lang3;
    requires lombok;
    requires junit;

    exports test;

    opens test to org.apache.commons.lang3;
}