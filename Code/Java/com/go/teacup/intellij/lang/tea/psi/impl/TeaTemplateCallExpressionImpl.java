package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.parser.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.psi.TeaBlockStatement;
import com.go.teacup.intellij.lang.tea.psi.TeaTemplateCallExpression;
import com.intellij.lang.ASTNode;

/**
 * User: JACKSBRR
 * Created: Apr 20, 2007 2:56:23 PM
 */
public class TeaTemplateCallExpressionImpl extends TeaCallExpressionImpl implements TeaTemplateCallExpression {
    public TeaTemplateCallExpressionImpl(ASTNode node) {
        super(node);
    }

    public TeaBlockStatement getSubstitutionBlock() {
        final ASTNode block = getNode().findChildByType(TeaElementTypes.BLOCK);
        return (TeaBlockStatement) (block == null ? null : block.getPsi());
    }
}
