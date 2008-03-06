package com.go.teacup.intellij.lang.tea.index;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.go.teacup.intellij.lang.tea.psi.TeaNamedElement;
import org.jetbrains.annotations.NonNls;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 5:01:46 PM
 */
public interface TeaSymbolProcessor {
    void processTemplate(TeaNamespace namespace, final int nameId, TeaNamedElement function);
    void processVariable(TeaNamespace namespace, final int nameId, TeaNamedElement variable);
    boolean acceptsFile(PsiFile file);
    PsiFile getBaseFile();

    void processProperty(final TeaNamespace namespace, final int nameId, final TeaNamedElement property);
    void processDefinition(final TeaNamespace namespace, final int nameId, final TeaNamedElement refExpr);

    int getRequiredNameId();
    void processTag(TeaNamespace namespace, final int nameId, PsiNamedElement namedElement, @NonNls final String attrName);
}
