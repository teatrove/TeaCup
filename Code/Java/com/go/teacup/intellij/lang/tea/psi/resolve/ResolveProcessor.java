package com.go.teacup.intellij.lang.tea.psi.resolve;

import com.go.teacup.intellij.lang.tea.psi.TeaElement;
import com.go.teacup.intellij.lang.tea.psi.TeaNamedElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.scope.PsiScopeProcessor;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 5:06:26 PM
 */
public class ResolveProcessor implements PsiScopeProcessor {
    private String name;
    private TeaElement result = null;

    public ResolveProcessor(String name) {
        this.name = name;
    }

    public TeaElement getResult() {
        return result;
    }

    public boolean execute(PsiElement element, PsiSubstitutor substitutor) {
        if (element instanceof TeaNamedElement) {
            if (name.equals(((TeaNamedElement) element).getName())) {
                result = (TeaElement) element;
                return false;
            }
        }

        return true;
    }

    public <T> T getHint(Class<T> hintClass) {
        return null;
    }

    public void handleEvent(Event event, Object associated) {
    }
}
