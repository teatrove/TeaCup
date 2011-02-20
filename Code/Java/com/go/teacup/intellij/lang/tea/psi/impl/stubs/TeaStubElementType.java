package com.go.teacup.intellij.lang.tea.psi.impl.stubs;

import com.go.teacup.intellij.lang.tea.TeaFileTypeLoader;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 1:16:21 PM
 */
public abstract class TeaStubElementType<StubT extends StubElement, PsiT extends PsiElement> extends IStubElementType<StubT, PsiT> {
    protected TeaStubElementType(@NotNull @NonNls final String debugName) {
        super(debugName, TeaFileTypeLoader.TEA.getLanguage());
    }

    public String getExternalId() {
      return "tea." + toString();
    }

    public String toString() {
        return super.toString() + " :: TeaStubElementType{}";
    }

    @Override
    public abstract PsiT createPsi(StubT stub);
}
