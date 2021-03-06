package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.lexer.TeaTokenTypes;
import com.go.teacup.intellij.lang.tea.parser.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.psi.TeaExpression;
import com.go.teacup.intellij.lang.tea.psi.TeaReferenceExpression;
import com.go.teacup.intellij.lang.tea.psi.TeaType;
import com.go.teacup.intellij.lang.tea.psi.TeaVariable;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.util.Icons;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 2:47:16 PM
 */
public class TeaVariableImpl extends TeaElementImpl implements TeaVariable {
    public TeaVariableImpl(final ASTNode node) {
      super(node);
    }

    public boolean hasInitializer() {
      return getInitializer() != null;
    }

    public TeaExpression getInitializer() {
      final ASTNode[] initializer = getNode().getChildren(TeaElementTypes.EXPRESSIONS);
      return (TeaExpression)(initializer.length == 1 ? initializer[0].getPsi() : null);
    }

    public String getName() {
      final ASTNode name = findNameIdentifier();
      return name != null ? name.getText() : "";
    }

    public ASTNode findNameIdentifier() {
        final TeaReferenceExpression nameExpression = findNameExpression();
        if(nameExpression == null) {
            return getNode().findChildByType(TeaTokenTypes.IDENTIFIER);
        }
        final PsiElement referenceNameElement = nameExpression.getReferenceNameElement();
        if(referenceNameElement == null) {
            return getNode().findChildByType(TeaTokenTypes.IDENTIFIER);
        }
        return referenceNameElement.getNode();
    }

    @Nullable
    public TeaReferenceExpression findNameExpression() {
        final ASTNode childByType = getNode().findChildByType(TeaElementTypes.REFERENCE_EXPRESSION);
        if(childByType == null) {
            return null;
        }
        return (TeaReferenceExpression) childByType.getPsi();
    }

    public void setInitializer(TeaExpression expr) throws IncorrectOperationException {
      throw new UnsupportedOperationException("TODO: implement");
    }

    @Nullable
    public TeaType getType() {
      final ASTNode parent = getNode().getTreeParent();
      //TODO: only handles explicit type definitions
        final ASTNode childByType = parent.findChildByType(TeaElementTypes.TYPE);

        if(childByType == null) {
            return null;
        }

        return (TeaType) childByType.getPsi();
    }

    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
      final ASTNode nameElement = TeaChangeUtil.createNameIdentifier(getProject(), name);
      getNode().replaceChild(getNode().getFirstChildNode(), nameElement);
      return this;
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
      if (visitor instanceof TeaElementVisitor) {
        ((TeaElementVisitor)visitor).visitTeaVariable(this);
      }
      else {
        visitor.visitElement(this);
      }
    }

    public int getTextOffset() {
      final ASTNode name = findNameIdentifier();
      return name != null ? name.getStartOffset() : super.getTextOffset();
    }

//    public boolean isConst() {
//      final ASTNode parent = getNode().getTreeParent();
//      return parent.getElementType() == TeaElementTypes.DEFINE_STATEMENT && parent.getFirstChildNode().getElementType() == TeaTokenTypes.CONST_KEYWORD;
//    }

    public Icon getIcon(int flags) {
      return Icons.VARIABLE_ICON;
    }
}
