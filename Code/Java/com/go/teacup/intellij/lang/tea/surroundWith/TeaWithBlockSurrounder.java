package com.go.teacup.intellij.lang.tea.surroundWith;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;

/**
 * User: JACKSBRR
 * Created: Apr 20, 2007 4:09:26 PM
 */
public class TeaWithBlockSurrounder extends TeaStatementSurrounder {
    public String getTemplateDescription() {
      return "{ }";
    }

    protected String getStatementTemplate() {
      return "{ }";
    }

    protected ASTNode getInsertBeforeNode(final ASTNode statementNode) {
      return statementNode.getLastChildNode();
    }

    protected TextRange getSurroundSelectionRange(final ASTNode statementNode) {
      int endOffset = statementNode.getTextRange().getEndOffset();
      return new TextRange(endOffset, endOffset);
    }
}
