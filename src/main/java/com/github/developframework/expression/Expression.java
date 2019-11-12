package com.github.developframework.expression;

import com.github.developframework.expression.exception.ExpressionException;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 表达式抽象基类
 *
 * @author qiuzhenhao
 */
public abstract class Expression {

    /* 父表达式对象 */
    @Getter
    protected Expression parentExpression = EmptyExpression.INSTANCE;

    public void setParentExpression(Expression parentExpression) {
        if(parentExpression == null) {
            throw new ExpressionException("can't set null to parent expression.");
        }
        this.parentExpression = parentExpression;
    }

    /**
     * 判断是否有父表达式
     *
     * @return 判断结果
     */
    public boolean hasParentExpression() {
        return parentExpression != EmptyExpression.INSTANCE;
    }

    /**
     * 返回表达式树
     *
     * @return 表达式树
     */
    public Expression[] expressionTree() {
        List<Expression> expressionTree = new LinkedList<>();
        Expression tempExpression = this;
        while (tempExpression != EmptyExpression.INSTANCE) {
            expressionTree.add(tempExpression);
            tempExpression = tempExpression.parentExpression;
        }
        Collections.reverse(expressionTree);
        return expressionTree.toArray(new Expression[0]);
    }

    /**
     * 将表达式字符串解析成表达式对象
     *
     * @param expressionValue 表达式字符串
     * @return 表达式对象
     */
    public static Expression parse(String expressionValue) {
        if (StringUtils.isNotEmpty(expressionValue)) {
            if (expressionValue.contains(".")) {
                String[] expressionFragments = split(expressionValue);
                Expression rootExpression = parseSingle(expressionFragments[0]);
                for (int i = 1; i < expressionFragments.length; i++) {
                    Expression childExpression = parseSingle(expressionFragments[i]);
                    childExpression.setParentExpression(rootExpression);
                    rootExpression = childExpression;
                }
                return rootExpression;
            } else {
                return parseSingle(expressionValue);
            }
        }
        return EmptyExpression.INSTANCE;
    }

    /**
     * 切分表达式
     *
     * @param expressionValue 表达式字符串
     * @return 切分结果
     */
    public static String[] split(String expressionValue) {
        if (!expressionValue.contains("(")) {
            return expressionValue.split("\\.");
        }
        List<String> parts = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        int inBracketLevel = 0;
        for (int i = 0; i < expressionValue.length(); i++) {
            final char ch = expressionValue.charAt(i);
            switch (ch) {
                case '(':
                    inBracketLevel++;
                    break;
                case ')':
                    inBracketLevel--;
                    break;
                case '.': {
                    if (inBracketLevel == 0) {
                        parts.add(sb.toString());
                        sb.setLength(0);
                        inBracketLevel = 0;
                    } else {
                        sb.append(ch);
                    }
                }
                continue;
            }
            sb.append(ch);
        }
        if (sb.length() > 0) {
            parts.add(sb.toString());
        }
        return parts.toArray(String[]::new);
    }

    /**
     * 解析单项
     *
     * @param singleExpressionValue 单项表达式字符串
     * @return 单项表达式对象
     */
    private static Expression parseSingle(String singleExpressionValue) {
        if (StringUtils.isEmpty(singleExpressionValue)) {
            return EmptyExpression.INSTANCE;
        } else if (ArrayExpression.isArrayExpression(singleExpressionValue)) {
            return new ArrayExpression(singleExpressionValue);
        } else if (MethodExpression.isMethodExpression(singleExpressionValue)) {
            return new MethodExpression(singleExpressionValue);
        } else {
            return new ObjectExpression(singleExpressionValue);
        }
    }

    /**
     * 复制表达式对象
     * @param expression 表达式对象
     * @return 新的表达式对象
     */
    public static Expression copy(Expression expression) {
        Expression newExpression;
        if(expression instanceof ObjectExpression) {
            newExpression = new ObjectExpression(((ObjectExpression) expression).getPropertyName());
        } else if(expression instanceof ArrayExpression){
            ArrayExpression arrayExpression = (ArrayExpression) expression;
            newExpression = new ArrayExpression(arrayExpression.getPropertyName(), arrayExpression.getIndex());
        } else {
            newExpression = EmptyExpression.INSTANCE;
        }
        if (expression.getParentExpression() != EmptyExpression.INSTANCE) {
            newExpression.setParentExpression(copy(expression.getParentExpression()));
        } else {
            newExpression.setParentExpression(EmptyExpression.INSTANCE);
        }
        return newExpression;
    }

    /**
     * 连接表达式
     *
     * @param parentExpression     父表达式对象
     * @param childExpressionValue 子表达式字符串
     * @return 新的表达式对象
     */
    public static Expression concat(Expression parentExpression, String childExpressionValue) {
        Expression childExpression = parse(childExpressionValue);
        return concat(parentExpression, childExpression);
    }

    /**
     * 连接表达式
     * @param parentExpression 父表达式对象
     * @param childExpression 子表达式对象
     * @return 新的表达式对象
     */
    public static Expression concat(Expression parentExpression, Expression childExpression) {
        if (childExpression == EmptyExpression.INSTANCE) {
            return parentExpression;
        }
        if (parentExpression == EmptyExpression.INSTANCE) {
            return childExpression;
        } else {
            Expression newExpression = copy(childExpression);
            if (newExpression.getParentExpression() == EmptyExpression.INSTANCE) {
                newExpression.setParentExpression(parentExpression);
            } else {
                newExpression.getParentExpression().setParentExpression(parentExpression);
            }
            return newExpression;
        }
    }

}
