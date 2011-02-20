/*
 *  Revision Information:
 *  $Id$
 *  $Author$
 *  $DateTime$
 *
 * Copyright ©2011 ESPN.com and Disney Interactive Media Group.  All rights reserved.
 */
package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.TeaFileType;
import com.go.teacup.intellij.lang.tea.psi.TeaFileBase;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.tree.IFileElementType;
import org.jetbrains.annotations.NotNull;

/**
 * TODO: Add documentation for TeaFileBaseImpl
 *
 * @author <a href="mailto:Brian.R.Jackson@espn3.com">Brian R. Jackson</a>
 * @version $Change$
 */
public abstract class TeaFileBaseImpl extends PsiFileBase implements TeaFileBase {

    protected TeaFileBaseImpl(FileViewProvider fileViewProvider, @NotNull Language language) {
        super(fileViewProvider, language);
    }

    public TeaFileBaseImpl(IFileElementType root, IFileElementType root1, FileViewProvider provider) {
        this(provider, root.getLanguage());
        init(root, root1);
    }

    @NotNull
    public FileType getFileType() {
        return TeaFileType.TEA_FILE_TYPE;
    }

    @Override
    public String toString() {
        return "Tea template";
    }
}
