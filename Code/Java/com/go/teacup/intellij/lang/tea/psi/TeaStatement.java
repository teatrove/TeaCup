package com.go.teacup.intellij.lang.tea.psi;

import com.intellij.psi.PsiStatement;
import com.intellij.util.IncorrectOperationException;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 4:38:52 PM
 */
public interface TeaStatement extends PsiStatement, TeaSourceElement {
    TeaStatement addStatementBefore(TeaStatement toAdd) throws IncorrectOperationException;
    TeaStatement addStatementAfter(TeaStatement toAdd) throws IncorrectOperationException;
    TeaStatement replace(TeaStatement with);
}
