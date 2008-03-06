package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.psi.TeaParameter;
import com.go.teacup.intellij.lang.tea.psi.TeaTemplate;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.util.Icons;

import javax.swing.*;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 3:42:50 PM
 */
public class TeaParameterImpl extends TeaVariableImpl implements TeaParameter {
    public TeaParameterImpl(final ASTNode node) {
      super(node);
    }

    public TeaTemplate getDeclaringTemplate() {
      return (TeaTemplate)getNode().getTreeParent().getTreeParent().getPsi();
    }

    public void accept(PsiElementVisitor visitor) {
      if (visitor instanceof TeaElementVisitor) {
        ((TeaElementVisitor)visitor).visitTeaParameter(this);
      }
      else {
        visitor.visitElement(this);
      }
    }

    public Icon getIcon(int flags) {
      return Icons.PARAMETER_ICON;
    }
}
