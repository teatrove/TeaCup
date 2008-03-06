package com.go.teacup.intellij.lang.tea.psi;

import com.go.teacup.intellij.lang.tea.TeaSupportLoader;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.scope.PsiScopeProcessor;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 4:49:51 PM
 */
public class TeaFile extends PsiFileBase implements TeaElement {

    public TeaFile(FileViewProvider viewProvider) {
        super(viewProvider, TeaSupportLoader.TEA.getLanguage());
    }

    public FileType getFileType() {
        return TeaSupportLoader.TEA;
    }

    public String toString() {
        return "TeaFile{name="+getName()+"}";
    }


    public boolean processDeclarations(PsiScopeProcessor processor,
                                       PsiSubstitutor substitutor,
                                       PsiElement lastParent,
                                       PsiElement place) {
        final PsiElement[] children = getChildren();
        for (PsiElement child : children) {
            if(child == lastParent) break;
            if(!child.processDeclarations(processor, substitutor, lastParent, place)) return false;
        }
        return true;
    }


    @Override
    public void accept(PsiElementVisitor visitor) {
        if(visitor instanceof TeaElementVisitor) {
            ((TeaElementVisitor)visitor).visitTeaElement(this);
        } else {
            super.accept(visitor);
        }
    }
}
