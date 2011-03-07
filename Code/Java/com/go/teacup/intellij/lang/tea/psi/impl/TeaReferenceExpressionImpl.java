package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.index.TeaIndex;
import com.go.teacup.intellij.lang.tea.index.TeaSymbolUtil;
import com.go.teacup.intellij.lang.tea.lexer.TeaTokenTypes;
import com.go.teacup.intellij.lang.tea.parser.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.psi.*;
import com.go.teacup.intellij.lang.tea.psi.resolve.ResolveProcessor;
import com.go.teacup.intellij.lang.tea.psi.resolve.TeaResolveUtil;
import com.go.teacup.intellij.lang.tea.psi.resolve.VariantsProcessor;
import com.go.teacup.intellij.lang.tea.psi.resolve.WalkUpResolveProcessor;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 4:36:05 PM
 */
public class TeaReferenceExpressionImpl extends TeaExpressionImpl implements TeaReferenceExpression {
    @NonNls
    private static final String PROTOTYPE_FIELD_NAME = "prototype";

    public TeaReferenceExpressionImpl(final ASTNode node) {
      super(node);
    }

    @Nullable public TeaExpression getQualifier() {
      final ASTNode[] nodes = getNode().getChildren(TeaElementTypes.EXPRESSIONS);
      return (TeaExpression)(nodes.length == 1 ? nodes[0].getPsi() : null);
    }

    @Nullable
    public String getReferencedName() {
      final ASTNode nameElement = getNameElement();
      return nameElement != null ? nameElement.getText() : null;
    }

    @Nullable
    public PsiElement getReferenceNameElement() {
      final ASTNode element = getNameElement();
      return element != null ? element.getPsi():null;
    }

    public PsiElement getElement() {
      return this;
    }

    public PsiReference getReference() {
      return this;
    }

    public TextRange getRangeInElement() {
      final ASTNode nameElement = getNameElement();
      final int startOffset = nameElement != null ? nameElement.getStartOffset() : getNode().getTextRange().getEndOffset();
      return new TextRange(startOffset - getNode().getStartOffset(), getTextLength());
    }

    private ASTNode getNameElement() {
        return this.getNode().findChildByType(TeaTokenTypes.IDENTIFIER);
    }

    public PsiElement resolve() {
      final ResolveResult[] resolveResults = multiResolve(true);

      return resolveResults.length == 0 || resolveResults.length > 1 ? null:resolveResults[0].getElement();
    }

    public String getCanonicalText() {
      return null;
    }

    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
      TeaChangeUtil.doIdentifierReplacement(this,getNameElement().getPsi(), newElementName);
      return this;
    }

    public PsiElement bindToElement(PsiElement element) throws IncorrectOperationException {
      final ASTNode nameElement = TeaChangeUtil.createNameIdentifier(getProject(), ((TeaNamedElement)element).getName());
      getNode().replaceChild(getNameElement(), nameElement);
      return this;
    }

    public boolean isReferenceTo(PsiElement element) {
      if (element instanceof PsiNamedElement || element instanceof XmlAttributeValue) {
        final String referencedName = getReferencedName();

        if (referencedName != null &&
            element instanceof TeaDefinitionExpression &&
            referencedName.equals(((TeaDefinitionExpression)element).getName())
           ) {
          final TeaExpression expression = ((TeaDefinitionExpression)element).getExpression();
          if (expression instanceof TeaReferenceExpression) {
            final TeaReferenceExpression jsReferenceExpression = (TeaReferenceExpression)expression;
            final TeaExpression qualifier = jsReferenceExpression.getQualifier();
            final TeaExpression myQualifier = getQualifier();

            return (myQualifier != null ||
                    (qualifier == myQualifier ||
                     "window".equals(qualifier.getText())
                    ));
          } else {
            return true;
          }
        }
        return TeaResolveUtil.isReferenceTo(this, referencedName, element);
      }
      return false;
    }

    public Object[] getVariants() {
      final TeaExpression qualifier = getQualifier();
      int[] nameIds = null;
      final PsiFile psiFile = getContainingFile();
      final TeaIndex index = TeaIndex.getInstance(psiFile.getProject());

//      if (qualifier instanceof TeaThisExpression) {
//        // We need to resolve ns mapping for 'this', which function was the constructor of the object
//        TeaFunction parentContainer = PsiTreeUtil.getParentOfType(this, TeaFunction.class);
//
//        if (parentContainer instanceof TeaFunctionExpression) {
//          TeaElement qualifyingExpression = null;
//
//          while(parentContainer instanceof TeaFunctionExpression) {
//            final PsiElement parentContainerParent = parentContainer.getParent();
//
//            if (parentContainerParent instanceof TeaAssignmentStatement) {
//              final TeaElement functionExpressionName = ((TeaDefinitionExpression)((TeaAssignmentStatement)parentContainerParent).getLOperand()).getExpression();
//              qualifyingExpression = functionExpressionName;
//
//              if (functionExpressionName instanceof TeaReferenceExpression) {
//                final TeaExpression functionExpressionNameQualifier = ((TeaReferenceExpression)functionExpressionName).getQualifier();
//
//                if (functionExpressionNameQualifier instanceof TeaThisExpression) {
//                  parentContainer = PsiTreeUtil.getParentOfType(functionExpressionName, TeaFunction.class);
//                  continue;
//                } else if (functionExpressionNameQualifier instanceof TeaReferenceExpression) {
//                  final String functionExpressionNameQualifierText = ((TeaReferenceExpression)functionExpressionNameQualifier).getReferencedName();
//
//                  if (PROTOTYPE_FIELD_NAME.equals(functionExpressionNameQualifierText)) {
//                    qualifyingExpression = ((TeaReferenceExpression)functionExpressionNameQualifier).getQualifier();
//                  }
//                }
//              }
//            } else if (parentContainerParent instanceof TeaProperty) {
//              final TeaElement element =
//                PsiTreeUtil.getParentOfType(parentContainerParent, TeaVariable.class, TeaAssignmentStatement.class, TeaArgumentList.class);
//              if (element instanceof TeaVariable) {
//                nameIds = TeaSymbolUtil.buildNameIndexArray(element, null, index);
//              } else if (element instanceof TeaAssignmentStatement) {
//                qualifyingExpression = ((TeaDefinitionExpression)((TeaAssignmentStatement)element).getLOperand()).getExpression();
//              } else if (element instanceof TeaArgumentList) {
//                for(TeaExpression expr: ((TeaArgumentList)element).getArguments()) {
//                  if (expr instanceof TeaReferenceExpression) {
//                    qualifyingExpression = expr; break;
//                  }
//                }
//              }
//            } else if (parentContainerParent instanceof TeaNewExpression) {
//              final TeaElement element =
//                PsiTreeUtil.getParentOfType(parentContainerParent, TeaVariable.class, TeaAssignmentStatement.class, TeaArgumentList.class);
//
//              if (element instanceof TeaVariable) {
//                nameIds = TeaSymbolUtil.buildNameIndexArray(element, null, index);
//              } else if (element instanceof TeaAssignmentStatement) {
//                qualifyingExpression = ((TeaDefinitionExpression)((TeaAssignmentStatement)element).getLOperand()).getExpression();
//              }
//            } else if (parentContainerParent instanceof TeaReferenceExpression) {
//              parentContainer = PsiTreeUtil.getParentOfType(parentContainerParent, TeaFunction.class);
//              continue;
//            }
//
//            break;
//          }
//
//          if (qualifyingExpression instanceof TeaReferenceExpression) {
//            final String functionExpressionNameQualifierText = ((TeaReferenceExpression)qualifyingExpression).getReferencedName();
//
//            if (PROTOTYPE_FIELD_NAME.equals(functionExpressionNameQualifierText)) {
//              qualifyingExpression = ((TeaReferenceExpression)qualifyingExpression).getQualifier();
//            }
//            nameIds = TeaSymbolUtil.buildNameIndexArray(qualifyingExpression, null, index);
//          }
//        } else if (parentContainer != null) {
//          nameIds = new int[] { index.getIndexOf( parentContainer.getName() ) };
//        }
//
//        if (nameIds== null) nameIds = new int[] { index.getIndexOf( "" ) };
//      } else {
        nameIds = TeaSymbolUtil.buildNameIndexArray(qualifier, null, index);
//      }

      final VariantsProcessor processor = new VariantsProcessor(
        nameIds,
        getContainingFile(),
        false,
        this
      );

      if (qualifier == null) {
        TeaResolveUtil.treeWalkUp(processor, this, this, this);
      }

      TeaIndex.getInstance(getProject()).processAllSymbols(processor);

      return processor.getResult();
    }

    public boolean isSoft() {
      return false;
    }

    public void accept(PsiElementVisitor visitor) {
      if (visitor instanceof TeaElementVisitor) {
        ((TeaElementVisitor)visitor).visitTeaReferenceExpression(this);
      }
      else {
        visitor.visitElement(this);
      }
    }

    @NotNull
    public ResolveResult[] multiResolve(final boolean incompleteCode) {
      final String referencedName = getReferencedName();
      if (referencedName == null) return ResolveResult.EMPTY_ARRAY;

      final PsiElement parent = getParent();
      final TeaExpression qualifier = getQualifier();
      if (parent instanceof TeaDefinitionExpression && qualifier != null) {
        return new ResolveResult[] { new TeaResolveUtil.MyResolveResult( (TeaElement)parent) };
      }

      boolean doLocalLookup = qualifier == null;
      final PsiFile containingFile = getContainingFile();

      if (doLocalLookup) {
        final ResolveProcessor processor = new ResolveProcessor(referencedName);
        final TeaElement jsElement = TeaResolveUtil.treeWalkUp(processor, this, this, this);

        if (jsElement != null) {
          return new ResolveResult[]{new TeaResolveUtil.MyResolveResult(jsElement)};
        }
      }

      final WalkUpResolveProcessor processor = new WalkUpResolveProcessor(
        getText(),
        TeaSymbolUtil.buildNameIndexArray(this, null, TeaIndex.getInstance(containingFile.getProject())),
        containingFile,
        false,
        this
      );

      return TeaResolveUtil.resolve(containingFile, processor);
    }
}
