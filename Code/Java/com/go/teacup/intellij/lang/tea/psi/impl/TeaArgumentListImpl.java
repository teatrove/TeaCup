package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.psi.TeaArgumentList;
import com.go.teacup.intellij.lang.tea.psi.TeaExpression;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.go.teacup.intellij.lang.tea.TeaElementTypes;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 3:24:28 PM
 */
public class TeaArgumentListImpl extends TeaElementImpl implements TeaArgumentList {
    public TeaArgumentListImpl(final ASTNode node) {
      super(node);
    }

    public TeaExpression[] getArguments() {
      final ASTNode[] nodes = getNode().getChildren(TeaElementTypes.EXPRESSIONS);
      final TeaExpression[] exprs = new TeaExpression[nodes.length];
      for (int i = 0; i < exprs.length; i++) {
        exprs[i] = (TeaExpression)nodes[i].getPsi();
      }
      return exprs;
    }

    public void accept(PsiElementVisitor visitor) {
      if (visitor instanceof TeaElementVisitor) {
        ((TeaElementVisitor)visitor).visitTeaArgumentList(this);
      }
      else {
        visitor.visitElement(this);
      }
    }
}
