package com.go.teacup.intellij.lang.tea.psi;

import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 4:35:13 PM
 */
public interface TeaReferenceExpression extends TeaExpression, PsiPolyVariantReference {
    @Nullable TeaExpression getQualifier();
    @Nullable
    String getReferencedName();
    @Nullable
    PsiElement getReferenceNameElement();
}
