package com.go.teacup.intellij.lang.tea.index;

import com.go.teacup.intellij.lang.tea.psi.TeaNamedElement;
import com.intellij.psi.PsiElement;

import java.io.IOException;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 5:09:11 PM
 */
public interface TeaNamedElementProxy extends TeaNamedElement {
    void write(SerializationContext context) throws IOException;
    void enumerateNames(final SerializationContext context);
    int getNameId();

    enum NamedItemType {
      Definition, Variable, Template, Property, FunctionProperty, FunctionExpression,AttributeValue
    }

    String getName();

    PsiElement getElement();
    NamedItemType getType();
}
