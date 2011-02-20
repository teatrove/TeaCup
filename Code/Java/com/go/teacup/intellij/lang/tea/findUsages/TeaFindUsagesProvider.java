package com.go.teacup.intellij.lang.tea.findUsages;

import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.go.teacup.intellij.lang.tea.psi.*;
import com.go.teacup.intellij.lang.tea.TeaBundle;
import com.go.teacup.intellij.lang.tea.index.TeaNamedElementProxy;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 11:09:03 AM
 */
public class TeaFindUsagesProvider implements FindUsagesProvider {

    public boolean canFindUsagesFor(PsiElement psiElement) {
        return psiElement instanceof PsiNamedElement;
    }

    @Nullable
    public String getHelpId(PsiElement psiElement) {
        return null;
    }


    @NotNull
    public String getType(PsiElement element) {
        if (element instanceof TeaNamedElementProxy) element = ((TeaNamedElementProxy)element).getElement();
        if(element instanceof TeaTemplate) return TeaBundle.message("tea.language.term.template");
        if(element instanceof TeaParameter) return TeaBundle.message("tea.language.term.parameter");
        if(element instanceof TeaProperty) return TeaBundle.message("tea.language.term.property");
        if(element instanceof TeaVariable) return TeaBundle.message("tea.language.term.variable");
        if(element instanceof TeaType) return TeaBundle.message("tea.language.term.type");
        return "";
    }


    @NotNull
    public String getDescriptiveName(PsiElement element) {
        String name = ((PsiNamedElement)element).getName();
        return name != null ? name:"";
    }


    @NotNull
    public String getNodeText(PsiElement element, boolean useFullName) {
        return getDescriptiveName(element); 
    }

    //TODO mayHaveReferences
    
    @Nullable
    public WordsScanner getWordsScanner() {
        return new TeaWordsScanner();
    }
}
