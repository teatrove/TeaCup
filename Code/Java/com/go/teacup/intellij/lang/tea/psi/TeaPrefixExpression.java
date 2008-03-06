package com.go.teacup.intellij.lang.tea.psi;

import com.intellij.psi.tree.IElementType;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 11:26:30 AM
 */
public interface TeaPrefixExpression extends TeaExpression {
    TeaExpression getExpression();
    IElementType getOperationSign();
}
