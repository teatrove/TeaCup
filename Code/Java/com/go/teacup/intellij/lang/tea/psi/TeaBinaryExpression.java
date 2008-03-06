package com.go.teacup.intellij.lang.tea.psi;

import com.intellij.psi.tree.IElementType;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 6:05:44 PM
 */
public interface TeaBinaryExpression extends TeaExpression {
    TeaExpression getLOperand();
    TeaExpression getROperand();
    IElementType getOperationSign();
}
