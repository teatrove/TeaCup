package com.go.teacup.intellij.lang.tea.psi;

import com.go.teacup.intellij.lang.tea.TeaSupportLoader;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 4:49:51 PM
 */
public class TeaFile extends PsiFileBase implements TeaElement {

    public TeaFile(FileViewProvider viewProvider) {
        super(viewProvider, TeaSupportLoader.TEA.getLanguage());
    }

    @NotNull
    public FileType getFileType() {
        return TeaSupportLoader.TEA;
    }

    @Override
    public String toString() {
        return "TeaFile{name="+getName()+"}";
    }


    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState resolveState,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        final PsiElement[] children = getChildren();
        for (PsiElement child : children) {
            if(child == lastParent) break;
            if(!child.processDeclarations(processor, resolveState, lastParent, place)) return false;
        }
        return true;
    }


    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if(visitor instanceof TeaElementVisitor) {
            ((TeaElementVisitor)visitor).visitTeaElement(this);
        } else {
            super.accept(visitor);
        }
    }
}
