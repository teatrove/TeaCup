package com.go.teacup.intellij.lang.tea.psi;

import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.Nullable;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 6:04:47 PM
 */
public interface TeaProperty extends TeaNamedElement {
    @Nullable String getName();
    @Nullable TeaExpression getValue();
}
