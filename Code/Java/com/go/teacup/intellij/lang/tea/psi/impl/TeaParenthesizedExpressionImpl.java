package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.parser.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.psi.TeaExpression;
import com.go.teacup.intellij.lang.tea.psi.TeaParenthesizedExpression;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 4:08:21 PM
 */
public class TeaParenthesizedExpressionImpl extends TeaExpressionImpl implements TeaParenthesizedExpression {
    public TeaParenthesizedExpressionImpl(final ASTNode node) {
      super(node);
    }

    public TeaExpression getInnerExpression() {
      final ASTNode[] nodes = getNode().getChildren(TeaElementTypes.EXPRESSIONS);
      return (TeaExpression)(nodes.length == 1 ? nodes[0].getPsi() : null);
    }

    public void accept(PsiElementVisitor visitor) {
      if (visitor instanceof TeaElementVisitor) {
        ((TeaElementVisitor)visitor).visitTeaParenthesizedExpression(this);
      }
      else {
        visitor.visitElement(this);
      }
    }
}
