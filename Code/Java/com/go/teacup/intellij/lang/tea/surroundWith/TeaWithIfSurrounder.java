package com.go.teacup.intellij.lang.tea.surroundWith;

import com.go.teacup.intellij.lang.tea.TeaBundle;
import com.go.teacup.intellij.lang.tea.psi.TeaIfStatement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NonNls;

/**
 * User: JACKSBRR
 * Created: Apr 20, 2007 4:09:31 PM
 */
public class TeaWithIfSurrounder extends TeaStatementSurrounder {
  public String getTemplateDescription() {
    return TeaBundle.message("tea.surround.with.if");
  }

  @NonNls
  protected String getStatementTemplate() {
    return "if(a) { }";
  }

  protected ASTNode getInsertBeforeNode(final ASTNode statementNode) {
    TeaIfStatement stmt = (TeaIfStatement) statementNode.getPsi();
    return stmt.getThen().getNode().getLastChildNode();
  }

  protected TextRange getSurroundSelectionRange(final ASTNode statementNode) {
    TeaIfStatement stmt = (TeaIfStatement) statementNode.getPsi();
    ASTNode conditionNode = stmt.getCondition().getNode();
    int offset = conditionNode.getStartOffset();
    stmt.getNode().removeChild(conditionNode);

    return new TextRange(offset, offset);
  }
}