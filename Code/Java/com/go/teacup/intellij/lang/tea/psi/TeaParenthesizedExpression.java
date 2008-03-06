package com.go.teacup.intellij.lang.tea.psi;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 4:42:16 PM
 */
public interface TeaParenthesizedExpression extends TeaExpression {
    TeaExpression getInnerExpression();
}
