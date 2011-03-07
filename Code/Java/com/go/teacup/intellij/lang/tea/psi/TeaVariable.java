package com.go.teacup.intellij.lang.tea.psi;

import com.intellij.lang.ASTNode;
import com.intellij.util.IncorrectOperationException;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 5:56:12 PM
 */
public interface TeaVariable extends TeaNamedElement {
    boolean hasInitializer();
    TeaExpression getInitializer();
    void setInitializer(TeaExpression expr) throws IncorrectOperationException;
    TeaType getType();
//    boolean isConst();
    ASTNode findNameIdentifier();

    TeaReferenceExpression findNameExpression();
}
