package com.go.teacup.intellij.lang.tea.parsing;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 2:31:20 PM
 */
public class TeaParser implements PsiParser {
    @NotNull
    public ASTNode parse(IElementType root, PsiBuilder builder) {
        final PsiBuilder.Marker rootMarker = builder.mark();
        while(!builder.eof()) {
            StatementParsing.parseSourceElement(builder);
        }
        rootMarker.done(root);
        return builder.getTreeBuilt();
    }
}
