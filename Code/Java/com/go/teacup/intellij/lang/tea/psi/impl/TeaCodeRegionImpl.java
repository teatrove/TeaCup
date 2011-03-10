/*
 *  Revision Information:
 *  $Id$
 *  $Author$
 *  $DateTime$
 *
 * Copyright ©2011 ESPN.com and Disney Interactive Media Group.  All rights reserved.
 */
package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.psi.TeaCodeRegion;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * TODO: Add documentation for TeaCodeRegionImpl
 *
 * @author <a href="mailto:Brian.R.Jackson@espn3.com">Brian R. Jackson</a>
 * @version $Change$
 */
public class TeaCodeRegionImpl extends TeaElementImpl implements TeaCodeRegion {
    public TeaCodeRegionImpl(final ASTNode node) {
        super(node);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof TeaElementVisitor) {
          ((TeaElementVisitor)visitor).visitTeaCodeRegion(this);
        }
        else {
          visitor.visitElement(this);
        }
    }
}
