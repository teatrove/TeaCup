/*
 *  Revision Information:
 *  $Id$
 *  $Author$
 *  $DateTime$
 *
 * Copyright ©2011 ESPN.com and Disney Interactive Media Group.  All rights reserved.
 */
package com.go.teacup.intellij.lang.tea.psi;

import com.intellij.psi.PsiPackage;

/**
 * TODO: Add documentation for TeaImportDirective
 *
 * @author <a href="mailto:Brian.R.Jackson@espn3.com">Brian R. Jackson</a>
 * @version $Change$
 */
public interface TeaImportDirective extends TeaElement {
    PsiPackage getPackage();
}
