package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.parser.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.psi.TeaExpression;
import com.go.teacup.intellij.lang.tea.psi.TeaIfStatement;
import com.go.teacup.intellij.lang.tea.psi.TeaStatement;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 3:49:10 PM
 */
public class TeaIfStatementImpl extends TeaStatementImpl implements TeaIfStatement {
    public TeaIfStatementImpl(final ASTNode node) {
      super(node);
    }

    public TeaExpression getCondition() {
      final ASTNode[] condition = getNode().getChildren(TeaElementTypes.EXPRESSIONS);
      return (TeaExpression)(condition.length == 1 ? condition[0].getPsi() : null);
    }

    public TeaStatement getThen() {
      final ASTNode[] nodes = getNode().getChildren(TeaElementTypes.STATEMENTS);
      return (TeaStatement)(nodes.length > 0 ? nodes[0].getPsi() : null);
    }

    public TeaStatement getElse() {
      final ASTNode[] nodes = getNode().getChildren(TeaElementTypes.STATEMENTS);
      return (TeaStatement)(nodes.length == 2 ? nodes[1].getPsi() : null);
    }

    public void setThen(TeaStatement statement) {
      throw new UnsupportedOperationException("TODO: implement");
    }

    public void setElse(TeaStatement statement) {
      throw new UnsupportedOperationException("TODO: implement");
    }

    public void setCondition(TeaExpression expr) {
      throw new UnsupportedOperationException("TODO: implement");
    }

    public void accept(PsiElementVisitor visitor) {
      if (visitor instanceof TeaElementVisitor) {
        ((TeaElementVisitor)visitor).visitTeaIfStatement(this);
      }
      else {
        visitor.visitElement(this);
      }
    }
}
