package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.psi.TeaEmptyStatement;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 3:35:59 PM
 */
public class TeaEmptyStatementImpl extends TeaStatementImpl implements TeaEmptyStatement {
    public TeaEmptyStatementImpl(final ASTNode node) {
      super(node);
    }

    public void accept(PsiElementVisitor visitor) {
      if (visitor instanceof TeaElementVisitor) {
        ((TeaElementVisitor)visitor).visitTeaEmptyStatement(this);
      }
      else {
        visitor.visitElement(this);
      }
    }
}
