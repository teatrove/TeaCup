package com.go.teacup.intellij.lang.tea;

import com.intellij.psi.tree.IElementType;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 1:16:21 PM
 */
public class TeaElementType extends IElementType {
    public TeaElementType(String debugName) {
        super(debugName, TeaSupportLoader.TEA.getLanguage());
    }


    public String toString() {
        return super.toString() + " :: TeaElementType{}";
    }
}
