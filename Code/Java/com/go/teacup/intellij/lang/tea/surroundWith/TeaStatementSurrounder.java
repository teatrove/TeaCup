package com.go.teacup.intellij.lang.tea.surroundWith;

import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.editor.Editor;
import com.intellij.util.IncorrectOperationException;
import com.go.teacup.intellij.lang.tea.psi.impl.TeaChangeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NonNls;

/**
 * User: JACKSBRR
 * Created: Apr 20, 2007 4:10:38 PM
 */
public abstract class TeaStatementSurrounder implements Surrounder {
  public boolean isApplicable(@NotNull PsiElement[] elements) {
    return true;
  }

  @Nullable
  public TextRange surroundElements(@NotNull Project project, @NotNull Editor editor, @NotNull PsiElement[] elements)
    throws IncorrectOperationException {
    ASTNode node = TeaChangeUtil.createStatementFromText(project, getStatementTemplate());
    PsiElement container = elements [0].getParent();
    container.getNode().addChild(node, elements [0].getNode());
    final ASTNode insertBeforeNode = getInsertBeforeNode(node);
    for (int i=0; i<elements.length; i++) {
      final ASTNode childNode = elements[i].getNode();
      container.getNode().removeChild(childNode);
      insertBeforeNode.getTreeParent().addChild(childNode.copyElement(), insertBeforeNode);
    }

    final CodeStyleManager csManager = CodeStyleManager.getInstance(project);
    csManager.reformat(node.getPsi());

    return getSurroundSelectionRange(node);
  }

  protected abstract @NonNls
  String getStatementTemplate();
  protected abstract ASTNode getInsertBeforeNode(final ASTNode statementNode);
  protected abstract TextRange getSurroundSelectionRange(final ASTNode statementNode);
}