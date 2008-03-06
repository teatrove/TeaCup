package com.go.teacup.intellij.lang.tea.surroundWith;

import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.lang.surroundWith.SurroundDescriptor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;
import com.go.teacup.intellij.lang.tea.psi.TeaExpression;
import com.go.teacup.intellij.lang.tea.psi.TeaCallExpression;
import org.jetbrains.annotations.NotNull;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 11:12:22 AM
 */
public class TeaExpressionSurroundDescriptor implements SurroundDescriptor {
    private static final Surrounder[] SURROUNDERS = {
      new TeaWithParenthesesSurrounder()
    };

    @NotNull
    public PsiElement[] getElementsToSurround(PsiFile file, int startOffset, int endOffset) {
      final TeaExpression expr = findExpressionInRange(file, startOffset, endOffset);
      if (expr == null) return PsiElement.EMPTY_ARRAY;
      return new PsiElement[] {expr};
    }

    @NotNull
    public Surrounder[] getSurrounders() {
      return SURROUNDERS;
    }

    private TeaExpression findExpressionInRange(PsiFile file, int startOffset, int endOffset) {
      PsiElement element1 = file.findElementAt(startOffset);
      PsiElement element2 = file.findElementAt(endOffset - 1);
      if (element1 instanceof PsiWhiteSpace) {
        startOffset = element1.getTextRange().getEndOffset();
      }
      if (element2 instanceof PsiWhiteSpace) {
        endOffset = element2.getTextRange().getStartOffset();
      }
      TeaExpression expression = PsiTreeUtil.findElementOfClassAtRange(file, startOffset, endOffset, TeaExpression.class);
      if (expression == null || expression.getTextRange().getEndOffset() != endOffset) return null;
      if (expression instanceof PsiReferenceExpression && expression.getParent() instanceof TeaCallExpression) return null;
      return expression;
    }
}
