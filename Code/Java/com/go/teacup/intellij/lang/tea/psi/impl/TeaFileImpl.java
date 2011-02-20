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
import com.go.teacup.intellij.lang.tea.psi.TeaFile;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.PsiManagerEx;
import com.intellij.psi.impl.file.impl.FileManagerImpl;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * TODO: Add documentation for TeaFileImpl
 *
 * @author <a href="mailto:Brian.R.Jackson@espn3.com">Brian R. Jackson</a>
 * @version $Change$
 */
public class TeaFileImpl extends TeaFileBaseImpl implements TeaFile {

    public TeaFileImpl(FileViewProvider fileViewProvider) {
        super(fileViewProvider, TeaFileType.TEA_FILE_TYPE.getLanguage());
    }

    public GlobalSearchScope getFileResolveScope() {
        final PsiElement context = getContext();
        if(context instanceof TeaFile) {
            return context.getResolveScope();
        }

        final VirtualFile vFile = getOriginalFile().getVirtualFile();
        if(vFile == null) {
            return GlobalSearchScope.allScope(getProject());
        }

        final GlobalSearchScope baseScope = ((FileManagerImpl)((PsiManagerEx)getManager()).getFileManager()).getDefaultResolveScope(vFile);

        return baseScope;
    }

}
