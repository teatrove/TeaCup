package com.go.teacup.intellij.lang.tea.parsing;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.go.teacup.intellij.lang.tea.TeaTokenTypes;
import com.go.teacup.intellij.lang.tea.TeaElementTypes;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 2:32:55 PM
 */
public class ParsingUtil {
    private ParsingUtil() {}

    public static void checkMatches(final PsiBuilder builder, final IElementType token, final String message) {
        if(builder.getTokenType() == token) {
            builder.advanceLexer();
        }
        else {
            builder.error(message);
        }
    }

    public static boolean parseIdentifier(PsiBuilder builder) {
        if(builder.getTokenType() == TeaTokenTypes.IDENTIFIER) {
            buildTokenElement(TeaElementTypes.REFERENCE_EXPRESSION, builder);
            return true;
        } else {
            return false;
        }
    }

    protected static void buildTokenElement(IElementType type, PsiBuilder builder) {
        final PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer();
        marker.done(type);
    }
}
