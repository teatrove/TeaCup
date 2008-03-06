package com.go.teacup.intellij.lang.tea.surroundWith;

import com.intellij.lang.surroundWith.SurroundDescriptor;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.go.teacup.intellij.lang.tea.psi.TeaBlockStatement;
import com.go.teacup.intellij.lang.tea.psi.TeaStatement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 11:12:29 AM
 */
public class TeaStatementsSurroundDescriptor implements SurroundDescriptor {
    private static final Surrounder[] SURROUNDERS = {
      new TeaWithBlockSurrounder(),
      new TeaWithIfSurrounder(),
      new TeaWithIfElseSurrounder(),
      new TeaWithForEachSurrounder()
    };

    @NotNull
    public PsiElement[] getElementsToSurround(PsiFile file, int startOffset, int endOffset) {
      final PsiElement[] statements = findStatementsInRange(file, startOffset, endOffset);
      if (statements == null) return PsiElement.EMPTY_ARRAY;
      return statements;
    }

    @NotNull
    public Surrounder[] getSurrounders() {
      return SURROUNDERS;
    }

    private PsiElement[] findStatementsInRange(PsiFile file, int startOffset, int endOffset) {
      PsiElement element1 = file.findElementAt(startOffset);
      PsiElement element2 = file.findElementAt(endOffset - 1);
      if (element1 instanceof PsiWhiteSpace) {
        startOffset = element1.getTextRange().getEndOffset();
        element1 = file.findElementAt(startOffset);
      }
      if (element2 instanceof PsiWhiteSpace) {
        endOffset = element2.getTextRange().getStartOffset();
        element2 = file.findElementAt(endOffset - 1);
      }
      if (element1 == null || element2 == null) return null;

      PsiElement parent = PsiTreeUtil.findCommonParent(element1, element2);
      while (true) {
        if (parent instanceof TeaBlockStatement) break;
        if (parent instanceof PsiStatement) {
          parent = parent.getParent();
          break;
        }
        if (parent instanceof PsiFile) return null;
        parent = parent.getParent();
      }


      while (!element1.getParent().equals(parent)) {
        element1 = element1.getParent();
      }
      if (startOffset != element1.getTextRange().getStartOffset()) return null;

      while (!element2.getParent().equals(parent)) {
        element2 = element2.getParent();
      }
      if (endOffset != element2.getTextRange().getEndOffset()) return null;

      PsiElement[] children = parent.getChildren();
      ArrayList<PsiElement> array = new ArrayList<PsiElement>();
      boolean flag = false;
      for (PsiElement child : children) {
        if (child.equals(element1)) {
          flag = true;
        }
        if (flag && !(child instanceof PsiWhiteSpace)) {
          array.add(child);
        }
        if (child.equals(element2)) {
          break;
        }
      }

      for (PsiElement element : array) {
        if (!(element instanceof TeaStatement
                || element instanceof PsiWhiteSpace
              || element instanceof PsiComment)) {
          return null;
        }
      }

      return array.toArray(new PsiElement[array.size()]);
    }
}
