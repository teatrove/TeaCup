package com.go.teacup.intellij.lang.tea.index;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiFile;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.Iconable;
import com.go.teacup.intellij.lang.tea.psi.TeaNamedElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 12:14:24 PM
 */
public class TeaItemPresentation implements ItemPresentation {
    private TeaNamedElement myElement;
    private TeaNamespace myNamespace;

    public TeaItemPresentation(final TeaNamedElement elementProxy, final TeaNamespace namespace) {
      this.myElement = elementProxy;
      this.myNamespace = namespace;
    }

    public String getPresentableText() {
      return myElement.getName();
    }

    @Nullable
    public String getLocationString() {
      final PsiFile psiFile = myElement.getContainingFile();
      if (myNamespace != null) {
        StringBuilder presentation = new StringBuilder();
        TeaIndex index = TeaIndex.getInstance(psiFile.getProject());
        String location = myNamespace.getNameId() != -1 ? index.getStringByIndex( myNamespace.getNameId()):null;

        if (location != null && location.length() > 0) {
          TeaNamespace ns = myNamespace.getParent();
          if (location.equals("prototype")) {
            location = index.getStringByIndex(ns.getNameId());
            ns = ns.getParent();
          }
          presentation.append(location);

          while(ns != null && ns.getNameId() != -1) {
            final String name = index.getStringByIndex(ns.getNameId());
            presentation.insert(0,name);
            presentation.insert(name.length(), '.');
            ns = ns.getParent();
          }

          presentation.append('(').append(psiFile.getName()).append(')');
          return presentation.toString();
        }
      }
      return psiFile.getName();
    }

    @Nullable
    public Icon getIcon(boolean open) {
      return myElement.getIcon( open ? Iconable.ICON_FLAG_OPEN: Iconable.ICON_FLAG_CLOSED );
    }

    @Nullable
    public TextAttributesKey getTextAttributesKey() {
      return null;
    }
}
