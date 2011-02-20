package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.parser.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.psi.TeaParameter;
import com.go.teacup.intellij.lang.tea.psi.TeaParameterList;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.TokenSet;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 3:41:15 PM
 */
public class TeaParameterListImpl extends TeaElementImpl implements TeaParameterList {
    private static final TokenSet PARAMETER_FILTER = TokenSet.create(TeaElementTypes.FORMAL_PARAMETER);

    public TeaParameterListImpl(final ASTNode node) {
      super(node);
    }

    public TeaParameter[] getParameters() {
      final ASTNode[] nodes = getNode().getChildren(PARAMETER_FILTER);
      final TeaParameter[] params = new TeaParameter[nodes.length];
      for (int i = 0; i < params.length; i++) {
        params[i] = (TeaParameter)nodes[i].getPsi();
      }
      return params;
    }

    public void accept(PsiElementVisitor visitor) {
      if (visitor instanceof TeaElementVisitor) {
        ((TeaElementVisitor)visitor).visitTeaParameterList(this);
      }
      else {
        visitor.visitElement(this);
      }
    }
}
