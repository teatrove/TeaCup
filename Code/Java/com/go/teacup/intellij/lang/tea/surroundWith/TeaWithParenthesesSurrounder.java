package com.go.teacup.intellij.lang.tea.surroundWith;

import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.editor.Editor;
import com.intellij.util.IncorrectOperationException;
import com.go.teacup.intellij.lang.tea.psi.TeaExpression;
import com.go.teacup.intellij.lang.tea.psi.impl.TeaChangeUtil;
import com.go.teacup.intellij.lang.tea.TeaBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: JACKSBRR
 * Created: Apr 20, 2007 4:06:58 PM
 */
public class TeaWithParenthesesSurrounder implements Surrounder {
    public String getTemplateDescription() {
      return TeaBundle.message("tea.surround.with.parenthesis");
    }

    public boolean isApplicable(@NotNull PsiElement[] elements) {
      return true;
    }

    @Nullable
    public TextRange surroundElements(@NotNull Project project, @NotNull Editor editor, @NotNull PsiElement[] elements)
      throws IncorrectOperationException {
      TeaExpression expr = (TeaExpression) elements [0];
      ASTNode parenthExprNode = TeaChangeUtil.createExpressionFromText(project, "(" + expr.getText() + ")");
      expr.getNode().getTreeParent().replaceChild(expr.getNode(), parenthExprNode);
      int offset = parenthExprNode.getTextRange().getEndOffset();
      return new TextRange(offset, offset);
    }
}
