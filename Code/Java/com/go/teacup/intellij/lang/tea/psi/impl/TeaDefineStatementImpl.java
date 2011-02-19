package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.psi.TeaDefineStatement;
import com.go.teacup.intellij.lang.tea.psi.TeaExpression;
import com.go.teacup.intellij.lang.tea.psi.TeaType;
import com.go.teacup.intellij.lang.tea.psi.TeaVariable;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 8:18:47 PM
 */
public class TeaDefineStatementImpl extends TeaStatementImpl implements TeaDefineStatement {
    public TeaDefineStatementImpl(ASTNode node) {
        super(node);
    }

    public TeaType getType() {
        final ASTNode childNode = getNode().findChildByType(TeaElementTypes.TYPE);
        return childNode == null ? null : (TeaType)childNode.getPsi();
    }

    public TeaVariable getVariable() {
        final ASTNode childNode = getNode().findChildByType(TeaElementTypes.VARIABLE);
        return childNode == null ? null : (TeaVariable)childNode.getPsi();
    }

    public TeaExpression getVariableExpression() {
      ASTNode child = getNode().getFirstChildNode();
      while (child != null) {
//        if (child.getElementType() == TeaTokenTypes.IN_KEYWORD) return null;
        if (TeaElementTypes.EXPRESSIONS.contains(child.getElementType())) {
          return (TeaExpression)child.getPsi();
        }
        child = child.getTreeNext();
      }
      return null;
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState resolveState, PsiElement lastParent, @NotNull PsiElement place) {
//        if (lastParent != null) {
          final TeaVariable var = getVariable();
          if (var != null) return processor.execute(var, resolveState);
//          else {
//            if (!processor.execute(getVariableExpression(), null)) return false;
//          }
//        }
        return true;
    }
}
