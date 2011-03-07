package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.parser.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.psi.*;
import com.go.teacup.intellij.lang.tea.psi.resolve.ResolveProcessor;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 3:25:22 PM
 */
public class TeaAssignmentStatementImpl extends TeaStatementImpl implements TeaAssignmentStatement {
    public TeaAssignmentStatementImpl(final ASTNode node) {
      super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
      if (visitor instanceof TeaElementVisitor) {
        ((TeaElementVisitor)visitor).visitTeaAssignmentStatement(this);
      }
      else {
        visitor.visitElement(this);
      }
    }

    public TeaType getType() {
        final ASTNode childNode = getNode().findChildByType(TeaElementTypes.TYPE);
        return childNode == null ? null : (TeaType)childNode.getPsi();
    }

    public TeaVariable getVariable() {
        final ASTNode childNode = getNode().findChildByType(TeaElementTypes.VARIABLE);
        return childNode == null ? null : (TeaVariable)childNode.getPsi();
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState resolveState, PsiElement lastParent, @NotNull PsiElement place) {
        if (lastParent != null) {
          return true;
        }

        TeaExpression lValue = getVariable().findNameExpression();
        if (lValue instanceof TeaReferenceExpression) {
          String refName = processor instanceof ResolveProcessor ? ((ResolveProcessor) processor).getName() : null;
          if (isDeclarationAssignment((TeaReferenceExpression) lValue, refName)) {
            if (!processor.execute(lValue, ResolveState.initial())) return false;
          }
        }

        return true;
    }

    private static boolean isDeclarationAssignment(@NotNull TeaReferenceExpression lRefExpr, @Nullable String nameHint) {
        if (nameHint == null || nameHint.equals(lRefExpr.getReferencedName())) {
          final PsiElement target = lRefExpr.resolve(); //this is NOT quadratic since the next statement will prevent from further processing declarations upstream
          if (!(target instanceof PsiVariable)) {
            return true;
          }
        }
        return false;
    }

}
