package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.psi.TeaPlainText;
import com.intellij.lang.ASTNode;
import com.intellij.psi.impl.source.tree.PsiCommentImpl;
import com.intellij.psi.tree.IElementType;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 10:18:56 AM
 */
public class TeaPlainTextImpl extends TeaElementImpl implements TeaPlainText {

    public TeaPlainTextImpl(ASTNode node) {
        super(node);
    }
}
