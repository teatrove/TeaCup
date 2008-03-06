package com.go.teacup.intellij.lang.tea.psi;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 11:27:52 AM
 */
public interface TeaForEachStatement extends TeaLoopStatement {
    TeaVariable getDeclarationStatement();
    TeaExpression getVariableExpression();

    TeaExpression getCollectionExpression();
    boolean isReverse();
}
