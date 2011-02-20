package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.parser.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.psi.TeaExpression;
import com.go.teacup.intellij.lang.tea.psi.TeaExpressionStatement;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 3:48:03 PM
 */
public class TeaExpressionStatementImpl extends TeaStatementImpl implements TeaExpressionStatement {
    public TeaExpressionStatementImpl(final ASTNode node) {
      super(node);
    }

    public TeaExpression getExpression() {
      final ASTNode[] expr = getNode().getChildren(TeaElementTypes.EXPRESSIONS);
      return (TeaExpression)(expr.length == 1 ? expr[0].getPsi() : null);
    }

    public void accept(PsiElementVisitor visitor) {
      if (visitor instanceof TeaElementVisitor) {
        ((TeaElementVisitor)visitor).visitTeaExpressionStatement(this);
      }
      else {
        visitor.visitElement(this);
      }
    }
}
