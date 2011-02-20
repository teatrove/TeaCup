package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.lexer.TeaTokenTypes;
import com.go.teacup.intellij.lang.tea.parser.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.psi.TeaArrayLiteralExpression;
import com.go.teacup.intellij.lang.tea.psi.TeaExpression;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.IElementType;

import java.util.ArrayList;
import java.util.List;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 4:11:23 PM
 */
public class TeaArrayLiteralExpressionImpl extends TeaExpressionImpl implements TeaArrayLiteralExpression {
    public TeaArrayLiteralExpressionImpl(final ASTNode node) {
      super(node);
    }

    public TeaExpression[] getExpressions() {
      List<TeaExpression> result = new ArrayList<TeaExpression>();
      ASTNode child = getNode().getFirstChildNode();
      boolean wasExpression = false;
      while (child != null) {
        final IElementType type = child.getElementType();
        if (TeaElementTypes.EXPRESSIONS.contains(type)) {
          result.add((TeaExpression)child.getPsi());
          wasExpression = true;
        }
        else if (type == TeaTokenTypes.COMMA) {
          if (wasExpression) {
            wasExpression = false;
          }
          else {
            result.add(null); // Skipped expression like [a,,b]
          }
        }
        child = child.getTreeNext();
      }

      return result.toArray(new TeaExpression[result.size()]);
    }

    public void accept(PsiElementVisitor visitor) {
      if (visitor instanceof TeaElementVisitor) {
        ((TeaElementVisitor)visitor).visitTeaArrayLiteralExpression(this);
      }
      else {
        visitor.visitElement(this);
      }
    }
}
