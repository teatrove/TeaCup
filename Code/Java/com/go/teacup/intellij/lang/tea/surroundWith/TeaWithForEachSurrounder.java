package com.go.teacup.intellij.lang.tea.surroundWith;

import com.go.teacup.intellij.lang.tea.TeaBundle;
import com.go.teacup.intellij.lang.tea.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.TeaTokenTypes;
import com.go.teacup.intellij.lang.tea.psi.TeaForEachStatement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiWhiteSpace;

/**
 * User: JACKSBRR
 * Created: Apr 20, 2007 4:09:42 PM
 */
public class TeaWithForEachSurrounder extends TeaStatementSurrounder {
    public String getTemplateDescription() {
      return TeaBundle.message("tea.surround.with.foreach");
    }

    protected String getStatementTemplate() {
      return "foreach(i in #(0)) { }";
    }

    protected ASTNode getInsertBeforeNode(final ASTNode statementNode) {
      TeaForEachStatement forStatement = (TeaForEachStatement) statementNode.getPsi();
      return forStatement.getBody().getLastChild().getNode();
    }

    protected TextRange getSurroundSelectionRange(final ASTNode statementNode) {
      for(ASTNode childNode: statementNode.getChildren(null)) {
        if (childNode.getElementType() == TeaTokenTypes.IN_KEYWORD ||
            childNode.getPsi() instanceof PsiWhiteSpace ||
            TeaElementTypes.EXPRESSIONS.contains(childNode.getElementType())) {
          statementNode.removeChild(childNode);
        }
        else if (childNode.getElementType() == TeaTokenTypes.RPAR) {
          int offset = childNode.getStartOffset();
          return new TextRange(offset, offset);
        }
      }
      return null;
    }
}
