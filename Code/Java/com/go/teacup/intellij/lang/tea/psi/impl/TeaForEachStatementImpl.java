package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.psi.TeaForEachStatement;
import com.go.teacup.intellij.lang.tea.psi.TeaVariable;
import com.go.teacup.intellij.lang.tea.psi.TeaExpression;
import com.go.teacup.intellij.lang.tea.psi.TeaStatement;
import com.go.teacup.intellij.lang.tea.TeaTokenTypes;
import com.go.teacup.intellij.lang.tea.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 3:52:24 PM
 */
public class TeaForEachStatementImpl extends TeaStatementImpl implements TeaForEachStatement {
    public TeaForEachStatementImpl(final ASTNode node) {
      super(node);
    }

    public TeaVariable getDeclarationStatement() {
      final ASTNode childNode = getNode().findChildByType(TeaElementTypes.VARIABLE);
      return childNode == null ? null : (TeaVariable)childNode.getPsi();
    }

    public TeaExpression getVariableExpression() {
      ASTNode child = getNode().getFirstChildNode();
      while (child != null) {
        if (child.getElementType() == TeaTokenTypes.IN_KEYWORD) return null;
        if (TeaElementTypes.EXPRESSIONS.contains(child.getElementType())) {
          return (TeaExpression)child.getPsi();
        }
        child = child.getTreeNext();
      }
      return null;
    }

    public TeaExpression getCollectionExpression() {
      ASTNode child = getNode().getFirstChildNode();
      boolean inPassed = false;
      while (child != null) {
        if (child.getElementType() == TeaTokenTypes.IN_KEYWORD) {
          inPassed = true;
        }
        if (inPassed && TeaElementTypes.EXPRESSIONS.contains(child.getElementType())) {
          return (TeaExpression)child.getPsi();
        }
        child = child.getTreeNext();
      }

      return null;
    }

    public boolean isReverse() {
        return getNode().findChildByType(TeaTokenTypes.REVERSE_KEYWORD) != null;
    }

    public TeaStatement getBody() {
      ASTNode child = getNode().getFirstChildNode();
      boolean passedRParen = false;
      while (child != null) {
        if (child.getElementType() == TeaTokenTypes.RPAR) {
          passedRParen = true;
        }
        else if (passedRParen && TeaElementTypes.STATEMENTS.contains(child.getElementType())) {
          return (TeaStatement)child.getPsi();
        }
        child = child.getTreeNext();
      }

      return null;
    }

    public boolean processDeclarations(PsiScopeProcessor processor,
                                       PsiSubstitutor substitutor,
                                       PsiElement lastParent,
                                       PsiElement place) {
      if (lastParent != null) {
        final TeaVariable var = getDeclarationStatement();
        if (var != null) return processor.execute(var, substitutor);
        else {
          if (!processor.execute(getVariableExpression(), null)) return false;
        }
      }
      return true;
    }

    public void accept(PsiElementVisitor visitor) {
      if (visitor instanceof TeaElementVisitor) {
        ((TeaElementVisitor)visitor).visitTeaForEachStatement(this);
      }
      else {
        visitor.visitElement(this);
      }
    }
}
