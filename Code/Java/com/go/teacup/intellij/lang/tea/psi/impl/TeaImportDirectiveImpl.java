/*
 *  Revision Information:
 *  $Id$
 *  $Author$
 *  $DateTime$
 *
 * Copyright ©2011 ESPN.com and Disney Interactive Media Group.  All rights reserved.
 */
package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.parser.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.psi.TeaImportDirective;
import com.intellij.lang.ASTNode;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * TODO: Add documentation for TeaImportDirectiveImpl
 *
 * @author <a href="mailto:Brian.R.Jackson@espn3.com">Brian R. Jackson</a>
 * @version $Change$
 */
public class TeaImportDirectiveImpl extends TeaElementImpl implements TeaImportDirective {

    public TeaImportDirectiveImpl(final ASTNode node) {
        super(node);
    }

    public PsiPackage getPackage() {
        ASTNode packageName = getNode().findChildByType(TeaElementTypes.REFERENCE_EXPRESSION);
        return JavaPsiFacade.getInstance(getProject()).findPackage(packageName.getText());
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        if(PsiTreeUtil.isAncestor(this, place, false)) {
            return true;
        }
        PsiPackage importedPackage = getPackage();
        return processor.execute(importedPackage, state);
    }
}
