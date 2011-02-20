package com.go.teacup.intellij.lang.tea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.go.teacup.intellij.lang.tea.psi.TeaElement;
import com.go.teacup.intellij.lang.tea.TeaFileTypeLoader;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 10:19:41 AM
 */
public class TeaElementImpl extends ASTWrapperPsiElement implements TeaElement {
    @NonNls
    private static final String IMPL = "Impl";


    public TeaElementImpl(final ASTNode node) {
        super(node);
    }

    @NotNull
    public Language getLanguage() {
        return TeaFileTypeLoader.TEA.getLanguage();
    }
    
}
