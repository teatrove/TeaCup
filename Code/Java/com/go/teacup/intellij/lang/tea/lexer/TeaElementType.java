/*
 *  Revision Information:
 *  $Id$
 *  $Author$
 *  $DateTime$
 *
 * Copyright ©2011 ESPN.com and Disney Interactive Media Group.  All rights reserved.
 */
package com.go.teacup.intellij.lang.tea.lexer;

import com.go.teacup.intellij.lang.tea.TeaFileType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:Brian.R.Jackson@espn3.com">Brian R. Jackson</a>
 * @version $Change$
 */
public class TeaElementType extends IElementType {

    private String debugName = null;

    public TeaElementType(@NotNull @NonNls String debugName) {
        super(debugName, TeaFileType.TEA_LANGUAGE);
        this.debugName = debugName;
    }

    @Override
    public String toString() {
        return debugName;
    }

}
