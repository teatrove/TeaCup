package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.TeaTokenTypes;
import com.go.teacup.intellij.lang.tea.psi.TeaParameter;
import com.go.teacup.intellij.lang.tea.psi.TeaParameterList;
import com.go.teacup.intellij.lang.tea.psi.TeaSourceElement;
import com.go.teacup.intellij.lang.tea.psi.TeaTemplate;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.Icons;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 3:38:48 PM
 */
public class TeaTemplateImpl extends TeaElementImpl implements TeaTemplate {
    public TeaTemplateImpl(final ASTNode node) {
      super(node);
    }

    public TeaParameterList getParameterList() {
      final ASTNode childByType = getNode().findChildByType(TeaElementTypes.PARAMETER_LIST);
      return childByType!= null ? (TeaParameterList)childByType.getPsi():null;
    }

    public TeaSourceElement[] getBody() {
      final ASTNode[] children = getNode().getChildren(TeaElementTypes.SOURCE_ELEMENTS);
      if (children.length == 0) return TeaSourceElement.EMPTY_ARRAY;
      TeaSourceElement[] result = new TeaSourceElement[children.length];
      for (int i = 0; i < children.length; i++) {
        result[i] = (TeaSourceElement)children[i].getPsi();
      }
      return result;
    }

    public boolean hasSubstitutionParameter() {
        return getNode().findChildByType(TeaElementTypes.SUBSTITUTION_PARAMETER) != null;
    }

    public PsiElement setName(String name) throws IncorrectOperationException {
      final ASTNode newNameElement = TeaChangeUtil.createNameIdentifier(getProject(), name);
      final ASTNode nameIdentifier = findNameIdentifier();
      nameIdentifier.getTreeParent().replaceChild(nameIdentifier, newNameElement);
      return this;
    }

    @Override
    public String getName() {
      final ASTNode name = findNameIdentifier();
      return name != null ? name.getText() : null;
    }

    public ASTNode findNameIdentifier() {
      return getNode().findChildByType(TeaTokenTypes.IDENTIFIER);
    }

    @Override
    public int getTextOffset() {
      final ASTNode name = findNameIdentifier();
      return name != null ? name.getStartOffset() : super.getTextOffset();
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState resolveState,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
      if (lastParent != null && lastParent.getParent() == this) {
        final TeaParameter[] params = getParameterList().getParameters();
        for (TeaParameter param : params) {
          if (!processor.execute(param, resolveState)) return false;
        }
      }

      return processor.execute(this, resolveState);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
      if (visitor instanceof TeaElementVisitor) {
        ((TeaElementVisitor)visitor).visitTeaTemplateDeclaration(this);
      }
      else {
        visitor.visitElement(this);
      }
    }
    @Override
    public Icon getIcon(int flags) {
      return Icons.METHOD_ICON;
    }
}
