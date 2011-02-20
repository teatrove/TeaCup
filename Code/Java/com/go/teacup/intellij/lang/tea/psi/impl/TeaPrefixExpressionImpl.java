package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.lexer.TeaTokenTypes;
import com.go.teacup.intellij.lang.tea.parser.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.psi.TeaExpression;
import com.go.teacup.intellij.lang.tea.psi.TeaPrefixExpression;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.IElementType;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 4:33:14 PM
 */
public class TeaPrefixExpressionImpl extends TeaExpressionImpl implements TeaPrefixExpression {
    public TeaPrefixExpressionImpl(final ASTNode node) {
      super(node);
    }

    public TeaExpression getExpression() {
      final ASTNode[] nodes = getNode().getChildren(TeaElementTypes.EXPRESSIONS);
      return (TeaExpression)(nodes.length == 1 ? nodes[0].getPsi() : null);
    }

    public IElementType getOperationSign() {
      final ASTNode[] nodes = getNode().getChildren(TeaTokenTypes.OPERATIONS);
      return nodes.length == 1 ? nodes[0].getElementType() : null;
    }

    public void accept(PsiElementVisitor visitor) {
      if (visitor instanceof TeaElementVisitor) {
        ((TeaElementVisitor)visitor).visitTeaPrefixExpression(this);
      }
      else {
        visitor.visitElement(this);
      }
    }
}
