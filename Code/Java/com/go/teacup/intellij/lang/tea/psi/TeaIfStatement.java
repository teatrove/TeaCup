package com.go.teacup.intellij.lang.tea.psi;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 4:40:40 PM
 */
public interface TeaIfStatement extends TeaStatement {
    TeaExpression getCondition();
    TeaStatement getThen();
    TeaStatement getElse();

    void setThen(TeaStatement statement);
    void setElse(TeaStatement statement);
    void setCondition(TeaExpression expr);
}
