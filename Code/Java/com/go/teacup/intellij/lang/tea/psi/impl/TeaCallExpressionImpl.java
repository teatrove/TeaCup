package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.parser.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.psi.TeaArgumentList;
import com.go.teacup.intellij.lang.tea.psi.TeaCallExpression;
import com.go.teacup.intellij.lang.tea.psi.TeaExpression;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 3:33:55 PM
 */
public class TeaCallExpressionImpl extends TeaStatementImpl implements TeaCallExpression {
    public TeaCallExpressionImpl(final ASTNode node) {
      super(node);
    }

    public TeaExpression getMethodExpression() {
      final ASTNode[] nodes = getNode().getChildren(TeaElementTypes.EXPRESSIONS);
      return (TeaExpression)(nodes.length == 1 ? nodes[0].getPsi() : null);
    }

    public TeaArgumentList getArgumentList() {
        final ASTNode childByType = getNode().findChildByType(TeaElementTypes.ARGUMENT_LIST);
        return childByType == null ? null : (TeaArgumentList) childByType.getPsi();
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
      if (visitor instanceof TeaElementVisitor) {
        ((TeaElementVisitor)visitor).visitTeaCallExpression(this);
      }
      else {
        visitor.visitElement(this);
      }
    }
}
