package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.parser.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.psi.TeaDefinitionExpression;
import com.go.teacup.intellij.lang.tea.psi.TeaExpression;
import com.go.teacup.intellij.lang.tea.psi.TeaReferenceExpression;
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
 * Created: Apr 16, 2007 2:29:51 PM
 */
public class TeaDefinitionExpressionImpl extends TeaExpressionImpl implements TeaDefinitionExpression {
  public TeaDefinitionExpressionImpl(final ASTNode node) {
    super(node);
  }

  public TeaExpression getExpression() {
    final ASTNode[] nodes = getNode().getChildren(TeaElementTypes.EXPRESSIONS);
    return (TeaExpression)(nodes.length == 1 ? nodes[0].getPsi() : null);
  }

  public String getName() {
    final TeaExpression expression = getExpression();
    if (expression instanceof TeaReferenceExpression) {
      return ((TeaReferenceExpression)expression).getReferencedName();
    }
    return null;
  }

  public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
    final TeaExpression expression = getExpression();
    if (expression instanceof TeaReferenceExpression) {
      return ((TeaReferenceExpression)expression).handleElementRename(name);
    }
    return null;
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TeaElementVisitor) {
      ((TeaElementVisitor)visitor).visitTeaDefinitionExpression(this);
    }
    else {
      visitor.visitElement(this);
    }
  }

  public Icon getIcon(int flags) {
    return Icons.VARIABLE_ICON;
  }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState resolveState, PsiElement lastParent, @NotNull PsiElement place) {
        final TeaExpression expression = getExpression();
        return expression instanceof TeaReferenceExpression && processor.execute(expression, resolveState);
    }
}
