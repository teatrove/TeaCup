package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.psi.TeaBlockStatement;
import com.go.teacup.intellij.lang.tea.psi.TeaStatement;
import com.go.teacup.intellij.lang.tea.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 3:28:26 PM
 */
public class TeaBlockStatementImpl extends TeaStatementImpl implements TeaBlockStatement {
    public TeaBlockStatementImpl(final ASTNode node) {
      super(node);
    }

    public TeaStatement[] getStatements() {
      final ASTNode[] nodes = getNode().getChildren(TeaElementTypes.STATEMENTS);
      final TeaStatement[] statements = new TeaStatement[nodes.length];
      for (int i = 0; i < statements.length; i++) {
        statements[i] = (TeaStatement)nodes[i].getPsi();
      }
      return statements;
    }

    public void accept(PsiElementVisitor visitor) {
      if (visitor instanceof TeaElementVisitor) {
        ((TeaElementVisitor)visitor).visitTeaBlock(this);
      }
      else {
        visitor.visitElement(this);
      }
    }
}
