package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.psi.TeaBreakStatement;
import com.go.teacup.intellij.lang.tea.psi.TeaStatement;
import com.go.teacup.intellij.lang.tea.psi.TeaLoopStatement;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 3:31:37 PM
 */
public class TeaBreakStatementImpl extends TeaStatementImpl implements TeaBreakStatement {
    public TeaBreakStatementImpl(final ASTNode node) {
      super(node);
    }

    public void accept(PsiElementVisitor visitor) {
      if (visitor instanceof TeaElementVisitor) {
        ((TeaElementVisitor)visitor).visitTeaBreakStatement(this);
      }
      else {
        visitor.visitElement(this);
      }
    }

    public TeaStatement getStatementToBreak() {
        return PsiTreeUtil.getParentOfType(this, TeaLoopStatement.class);
    }
}
