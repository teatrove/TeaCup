package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.psi.TeaPlainText;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.impl.source.tree.PsiCommentImpl;
import com.intellij.psi.impl.source.tree.injected.CommentLiteralEscaper;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 10:18:56 AM
 */
public class TeaPlainTextImpl extends PsiCommentImpl implements TeaPlainText {
//    private IElementType tokenType;

    public TeaPlainTextImpl(IElementType type, CharSequence text) {
        super(type, text);
    }


//    @Nullable
//    public List<Pair<PsiElement, TextRange>> getInjectedPsi() {
//        return InjectedLanguageUtil.getInjectedPsiFiles(this);
//    }
//
//    public void processInjectedPsi(@NotNull InjectedPsiVisitor visitor) {
//        InjectedLanguageUtil.enumerate(this, visitor);
//    }
//
//    public PsiLanguageInjectionHost updateText(@NotNull String text) {
//        throw new UnsupportedOperationException("TeaPlainTextImpl#updateText is not implemented");
//    }

//    @NotNull
//    public LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
//        return new TeaPlainLiteralTextEscaper(this);
//    }

//    public IElementType getTokenType() {
//        return tokenType;
//    }
}
