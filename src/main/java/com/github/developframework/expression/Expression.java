package com.github.developframework.expression;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 表达式抽象基类
 * @author qiuzhenhao
 * @date 2017/5/6
 */
public abstract class Expression {

    /* 父表达式对象 */
    @Getter
    @Setter
    protected Expression parentExpression;

    /**
     * 判断是否有父表达式
     * @return 判断结果
     */
    public boolean hasParentExpression() {
        return parentExpression != null;
    }

    /**
     * 返回表达式树
     * @return 表达式树
     */
    public Expression[] expressionTree() {
        List<Expression> expressionTree = new LinkedList<>();
        Expression tempExression = this;
        while(tempExression != null) {
            expressionTree.add(tempExression);
            tempExression = tempExression.parentExpression;
        }
        Collections.reverse(expressionTree);
        return expressionTree.toArray(new Expression[expressionTree.size()]);
    }

    /**
     * 将表达式字符串解析成表达式对象
     * @param expressionValue 表达式字符串
     * @return 表达式对象
     */
    public final static Expression parse(String expressionValue) {
        if (StringUtils.isNotBlank(expressionValue)) {
            if(expressionValue.contains(".")) {
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
        return null;
    }

    /**
     * 解析单项
     * @param singleExpressionValue 单项表达式字符串
     * @return 单项表达式对象
     */
    private final static Expression parseSingle(String singleExpressionValue) {
        if(ArrayExpression.isArrayExpression(singleExpressionValue)) {
            return new ArrayExpression(singleExpressionValue);
        }
        return new ObjectExpression(singleExpressionValue);
    }
}
