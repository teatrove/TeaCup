package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.psi.TeaIndexedPropertyAccessExpression;
import com.go.teacup.intellij.lang.tea.psi.TeaExpression;
import com.go.teacup.intellij.lang.tea.TeaTokenTypes;
import com.go.teacup.intellij.lang.tea.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElementVisitor;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 4:34:35 PM
 */
public class TeaIndexedPropertyAccessExpressionImpl extends TeaExpressionImpl implements TeaIndexedPropertyAccessExpression {
    public TeaIndexedPropertyAccessExpressionImpl(final ASTNode node) {
      super(node);
    }

    public TeaExpression getQualifier() {
      ASTNode child = getNode().getFirstChildNode();
      while (child != null) {
        final IElementType type = child.getElementType();
        if (type == TeaTokenTypes.LBRACKET) return null;
        if (TeaElementTypes.EXPRESSIONS.contains(type)) return (TeaExpression)child.getPsi();
        child = child.getTreeNext();
      }
      return null;
    }

    public TeaExpression getIndexExpression() {
      ASTNode child = getNode().getFirstChildNode();
      boolean bracketPassed = false;
      while (child != null) {
        final IElementType type = child.getElementType();
        if (type == TeaTokenTypes.LBRACKET) {
          bracketPassed = true;
        }
        if (bracketPassed && TeaElementTypes.EXPRESSIONS.contains(type)) return (TeaExpression)child.getPsi();
        child = child.getTreeNext();
      }
      return null;
    }

    public void accept(PsiElementVisitor visitor) {
      if (visitor instanceof TeaElementVisitor) {
        ((TeaElementVisitor)visitor).visitTeaIndexedPropertyAccessExpression(this);
      }
      else {
        visitor.visitElement(this);
      }
    }
}
