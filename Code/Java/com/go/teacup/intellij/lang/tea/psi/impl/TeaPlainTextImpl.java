package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.psi.TeaPlainText;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 10:18:56 AM
 */
public class TeaPlainTextImpl extends TeaElementImpl implements TeaPlainText {
    private IElementType tokenType;

    public TeaPlainTextImpl(ASTNode node, IElementType tokenType) {
        super(node);
        this.tokenType = tokenType;
    }


    @Nullable
    public List<Pair<PsiElement, TextRange>> getInjectedPsi() {
        return InjectedLanguageUtil.getInjectedPsiFiles(this, null);
    }

    public void fixText(String string) {
        // nothing to see here...
    }

    public IElementType getTokenType() {
        return tokenType;
    }
}
