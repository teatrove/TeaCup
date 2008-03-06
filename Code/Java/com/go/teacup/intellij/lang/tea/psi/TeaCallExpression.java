package com.go.teacup.intellij.lang.tea.psi;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 4:45:43 PM
 */
public interface TeaCallExpression extends TeaExpression {
    TeaExpression getMethodExpression();
    TeaArgumentList getArgumentList();
}
