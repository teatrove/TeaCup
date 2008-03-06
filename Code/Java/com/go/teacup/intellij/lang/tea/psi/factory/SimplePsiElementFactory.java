package com.go.teacup.intellij.lang.tea.psi.factory;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 11:19:10 AM
 */
public interface SimplePsiElementFactory {
    PsiElement create(ASTNode node);
}
