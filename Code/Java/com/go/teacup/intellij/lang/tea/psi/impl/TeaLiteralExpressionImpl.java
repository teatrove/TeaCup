package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.psi.TeaLiteralExpression;
import com.go.teacup.intellij.lang.tea.psi.resolve.WalkUpResolveProcessor;
import com.go.teacup.intellij.lang.tea.psi.resolve.VariantsProcessor;
import com.go.teacup.intellij.lang.tea.psi.resolve.TeaResolveUtil;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.go.teacup.intellij.lang.tea.index.TeaIndex;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.ArrayList;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 3:55:34 PM
 */
public class TeaLiteralExpressionImpl extends TeaExpressionImpl implements TeaLiteralExpression {
    private String referenceText;
    private PsiReference[] references;

    public TeaLiteralExpressionImpl(final ASTNode node) {
      super(node);
    }

    @NotNull
    public PsiReference[] getReferences() {
      String text = getText();
      if (references != null &&
          referenceText != null &&
          referenceText.equals(text)) {
        return references;
      }

      if (!StringUtil.startsWithChar(text,'"') && !StringUtil.startsWithChar(text,'\'')) {
        referenceText = text;
        references = PsiReference.EMPTY_ARRAY;
      }

      String value = StringUtil.stripQuotesAroundValue(text);
      List<PsiReference> refs = new ArrayList<PsiReference>(1);
      int lastPos = 0;
      int dotPos = value.indexOf('.');

      while(dotPos != -1) {
        final String s = value.substring(lastPos,dotPos).trim();

        if (s.length() > 0) {
          refs.add( new MyPsiReference(s,lastPos + 1) );
        }

        lastPos = dotPos + 1;
        dotPos = value.indexOf('.',lastPos);
      }

      final String s = value.substring(lastPos).trim();

      if (s.length() > 0) {
        refs.add( new MyPsiReference(s,lastPos + 1) );
      }

      referenceText = text;
      references = refs.toArray(new PsiReference[refs.size()]);
      return references;
    }

    public void accept(PsiElementVisitor visitor) {
      if (visitor instanceof TeaElementVisitor) {
        ((TeaElementVisitor)visitor).visitTeaLiteralExpression(this);
      }
      else {
        visitor.visitElement(this);
      }
    }

    private class MyPsiReference implements PsiPolyVariantReference {
      private String myText;
      private int myOffset;

      MyPsiReference(final String s, final int i) {
        myText = s;
        myOffset = i;
      }

      public PsiElement getElement() {
        return TeaLiteralExpressionImpl.this;
      }

      public TextRange getRangeInElement() {
        return new TextRange(myOffset,myOffset + myText.length());
      }

      @Nullable
      public PsiElement resolve() {
        final ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement():null;
      }

      public String getCanonicalText() {
        return myText;
      }

      public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        int sizeChange = newElementName.length() - myText.length();
        boolean found = false;
        String newLiteralText = referenceText.substring(0,myOffset) + newElementName + referenceText.substring(myOffset + myText.length());
        final ASTNode expressionFromText = TeaChangeUtil.createExpressionFromText(getProject(), newLiteralText);
        if (expressionFromText.getPsi() instanceof TeaLiteralExpression) {
          getNode().replaceChild(
            getNode().getFirstChildNode(),
            expressionFromText.getFirstChildNode()
          );
        }
        myText = newElementName;

        for (final PsiReference reference : references) {
          if (reference == this) {
            found = true;
          }
          else if (found) {
            ((MyPsiReference)reference).myOffset += sizeChange;
          }
        }
        return null;
      }

      public PsiElement bindToElement(PsiElement element) throws IncorrectOperationException {
        return null;
      }

      public boolean isReferenceTo(PsiElement element) {
        if (element instanceof PsiNamedElement || element instanceof XmlAttributeValue)
          return TeaResolveUtil.isReferenceTo(this, myText, element);
        return false;
      }

      public Object[] getVariants() {
        final VariantsProcessor processor = new VariantsProcessor(
          null,
          getContainingFile(),
          false,
          TeaLiteralExpressionImpl.this
        );
        TeaIndex.getInstance(getProject()).processAllSymbols(processor);

        return processor.getResult();
      }

      public boolean isSoft() {
        return true;
      }

      @NotNull
      public ResolveResult[] multiResolve(final boolean incompleteCode) {
        final PsiFile psiFile = getContainingFile();
        final WalkUpResolveProcessor processor = new WalkUpResolveProcessor(
          myText,
          new int[]{ TeaIndex.getInstance(psiFile.getProject()).getIndexOf( myText) },
          psiFile,
          false,
          TeaLiteralExpressionImpl.this
        );

        return TeaResolveUtil.resolve(psiFile, processor);
      }
    }
}
