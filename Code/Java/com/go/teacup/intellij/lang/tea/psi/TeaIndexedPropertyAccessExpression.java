package com.go.teacup.intellij.lang.tea.psi;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 4:45:18 PM
 */
public interface TeaIndexedPropertyAccessExpression extends TeaExpression {
    TeaExpression getQualifier();
    TeaExpression getIndexExpression();
}
