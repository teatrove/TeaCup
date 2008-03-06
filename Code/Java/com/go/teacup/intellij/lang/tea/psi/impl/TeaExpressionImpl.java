package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.psi.TeaExpression;
import com.intellij.lang.ASTNode;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 4:36:49 PM
 */
public class TeaExpressionImpl extends TeaElementImpl implements TeaExpression {
    public TeaExpressionImpl(final ASTNode node) {
      super(node);
    }

    public TeaExpression replace(TeaExpression newExpr) {
      return TeaChangeUtil.replaceExpression(this, newExpr);
    }
}
