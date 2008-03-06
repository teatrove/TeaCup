package com.go.teacup.intellij.lang.tea.psi.factory;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

import java.util.HashMap;
import java.util.Map;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 11:19:56 AM
 */
public class MappedSimplePsiElementFactory implements SimplePsiElementFactory {

    private static final Map MAP = new HashMap(){{
//        put(TeaElementTypes.TEMPLATE_DECLARATION, new TeaTemplateImpl());
    }};


    public PsiElement create(ASTNode node) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
