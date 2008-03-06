package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.psi.TeaSubstitutionStatement;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 4:37:20 PM
 */
public class TeaSubstitutionStatementImpl extends TeaStatementImpl implements TeaSubstitutionStatement {
    public TeaSubstitutionStatementImpl(final ASTNode node) {
      super(node);
    }

    public void accept(PsiElementVisitor visitor) {
      if (visitor instanceof TeaElementVisitor) {
        ((TeaElementVisitor)visitor).visitTeaSubstitutionStatement(this);
      }
      else {
        visitor.visitElement(this);
      }
    }
}
