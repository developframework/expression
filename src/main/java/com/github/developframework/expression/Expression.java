package com.github.developframework.expression;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 表达式抽象基类
 *
 * @author qiuzhenhao
 * @date 2017/5/6
 */
public abstract class Expression {

    public static final EmptyExpression EMPTY_EXPRESSION = new EmptyExpression();

    /* 父表达式对象 */
    @Getter
    @Setter
    protected Expression parentExpression;

    /**
     * 判断是否有父表达式
     *
     * @return 判断结果
     */
    public boolean hasParentExpression() {
        return parentExpression != null;
    }

    /**
     * 返回表达式树
     *
     * @return 表达式树
     */
    public Expression[] expressionTree() {
        List<Expression> expressionTree = new LinkedList<>();
        Expression tempExpression = this;
        while (tempExpression != null) {
            expressionTree.add(tempExpression);
            tempExpression = tempExpression.parentExpression;
        }
        Collections.reverse(expressionTree);
        return expressionTree.toArray(new Expression[expressionTree.size()]);
    }

    /**
     * 将表达式字符串解析成表达式对象
     *
     * @param expressionValue 表达式字符串
     * @return 表达式对象
     */
    public static final Expression parse(String expressionValue) {
        if (StringUtils.isNotBlank(expressionValue)) {
            if (expressionValue.contains(".")) {
                String[] expressionFragments = expressionValue.split("\\.");
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
        return EMPTY_EXPRESSION;
    }

    /**
     * 解析单项
     *
     * @param singleExpressionValue 单项表达式字符串
     * @return 单项表达式对象
     */
    private static final Expression parseSingle(String singleExpressionValue) {
        if (StringUtils.isBlank(singleExpressionValue)) {
            return EMPTY_EXPRESSION;
        } else if (ArrayExpression.isArrayExpression(singleExpressionValue)) {
            return new ArrayExpression(singleExpressionValue);
        } else {
            return new ObjectExpression(singleExpressionValue);
        }
    }

    /**
     * 复制表达式对象
     * @param expression 表达式对象
     * @return 新的表达式对象
     */
    public static final Expression copy(Expression expression) {
        Expression newExpression;
        if(expression instanceof ObjectExpression) {
            newExpression = new ObjectExpression(((ObjectExpression) expression).getPropertyName());
        } else if(expression instanceof ArrayExpression){
            ArrayExpression arrayExpression = (ArrayExpression) expression;
            newExpression = new ArrayExpression(arrayExpression.getPropertyName(), arrayExpression.getIndex());
        } else {
            newExpression = EMPTY_EXPRESSION;
        }
        if(expression.getParentExpression() != null) {
            newExpression.setParentExpression(copy(expression.getParentExpression()));
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
    public static final Expression concat(Expression parentExpression, String childExpressionValue) {
        Expression newExpression = parse(childExpressionValue);
        if(parentExpression != EMPTY_EXPRESSION) {
            newExpression.setParentExpression(parentExpression);
        }
        return newExpression;
    }

    /**
     * 连接表达式
     * @param parentExpression 父表达式对象
     * @param childExpression 子表达式对象
     * @return 新的表达式对象
     */
    public static final Expression concat(Expression parentExpression, Expression childExpression) {
        Expression newExpression = copy(childExpression);
        if(parentExpression != EMPTY_EXPRESSION) {
            newExpression.setParentExpression(parentExpression);
        }
        return newExpression;
    }

}
