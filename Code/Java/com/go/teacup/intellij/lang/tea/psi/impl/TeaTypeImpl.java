package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.psi.TeaType;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 2:57:35 PM
 */
public class TeaTypeImpl extends TeaElementImpl implements TeaType {
    public TeaTypeImpl(ASTNode node) {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof TeaElementVisitor) {
          ((TeaElementVisitor)visitor).visitTeaType(this);
        }
        else {
          visitor.visitElement(this);
        }
    }
}
