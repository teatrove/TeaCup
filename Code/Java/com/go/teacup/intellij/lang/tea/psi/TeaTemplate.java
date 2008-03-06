package com.go.teacup.intellij.lang.tea.psi;

import com.intellij.psi.PsiNamedElement;
import com.intellij.lang.ASTNode;
import com.intellij.pom.Navigatable;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 5:53:43 PM
 */
public interface TeaTemplate extends TeaNamedElement, TeaSourceElement, Navigatable {
    TeaParameterList getParameterList();
    TeaSourceElement[] getBody();
    boolean hasSubstitutionParameter();

    ASTNode findNameIdentifier();
}
