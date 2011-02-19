package com.go.teacup.intellij.lang.tea.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.navigation.NavigationItem;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiNamedElement;
import com.go.teacup.intellij.lang.tea.psi.TeaElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 5:58:33 PM
 */
public class TeaStructureViewElement implements StructureViewTreeElement {
    private TeaElement value;

    public TeaStructureViewElement(TeaElement element) {
        this.value = element;
    }


    public TeaElement getValue() {
        return value;
    }


    public void navigate(boolean requestFocus) {
        ((NavigationItem)value).navigate(requestFocus);
    }

    public boolean canNavigate() {
        return ((NavigationItem)value).canNavigate();
    }

    public boolean canNavigateToSource() {
        return ((NavigationItem)value).canNavigateToSource();
    }


    public StructureViewTreeElement[] getChildren() {
        return new StructureViewTreeElement[0]; //TODO
    }


    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            public String getPresentableText() {
//                if(value instanceof TeaObjectLiteralExpression) {
//                    if(value.getParent() instanceof TeaAssignmentStatement) {
//                        return ((TeaAssignmentStatement)value.getParent()).getLOperand().getText();
//                    } else {
//                        return "prototype";
//                    }
//                }
                return ((PsiNamedElement)value).getName();
            }

            @Nullable
            public String getLocationString() {
                return null;
            }

            public Icon getIcon(boolean open) {
                return value.getIcon(Iconable.ICON_FLAG_OPEN);
            }

            @Nullable
            public TextAttributesKey getTextAttributesKey() {
                return null;
            }
        };
    }
}
