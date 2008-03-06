package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.psi.TeaBinaryExpression;
import com.go.teacup.intellij.lang.tea.psi.TeaExpression;
import com.go.teacup.intellij.lang.tea.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.TeaTokenTypes;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.lang.ASTNode;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 3:26:02 PM
 */
public class TeaBinaryExpressionImpl extends TeaExpressionImpl implements TeaBinaryExpression {
    private static final Logger LOG = Logger.getInstance("#"+TeaBinaryExpressionImpl.class.getName());
    private static final TokenSet BINARY_OPERATIONS = TokenSet.orSet(TeaTokenTypes.OPERATIONS, TeaTokenTypes.RELATIONAL_OPERATIONS);

    public TeaBinaryExpressionImpl(final ASTNode node) {
      super(node);
    }

    public TeaExpression getLOperand() {
      final ASTNode[] nodes = getNode().getChildren(TeaElementTypes.EXPRESSIONS);
      LOG.assertTrue(nodes.length >= 1);
      return (TeaExpression)nodes[0].getPsi();
    }

    public TeaExpression getROperand() {
      final ASTNode[] nodes = getNode().getChildren(TeaElementTypes.EXPRESSIONS);
      return nodes.length == 2 ? (TeaExpression)nodes[1].getPsi() : null;
    }

    public IElementType getOperationSign() {
      final ASTNode operationASTNode = getOperationASTNode();
      return operationASTNode != null ? operationASTNode.getElementType():null;
    }

    private ASTNode getOperationASTNode() {
      final ASTNode[] nodes = getNode().getChildren(BINARY_OPERATIONS);
      return nodes.length == 1 ? nodes[0]:null;
    }

    public void accept(PsiElementVisitor visitor) {
      if (visitor instanceof TeaElementVisitor) {
        ((TeaElementVisitor)visitor).visitTeaBinaryExpression(this);
      }
      else {
        visitor.visitElement(this);
      }
    }
}
