package com.go.teacup.intellij.lang.tea.highlighting;

import com.go.teacup.intellij.lang.tea.lexer.TeaTokenTypes;
import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 11:07:57 AM
 */
public class TeaBraceMatcher implements PairedBraceMatcher {
    private static final BracePair[] PAIRS = new BracePair[] {
            new BracePair(TeaTokenTypes.LPAR, TeaTokenTypes.RPAR, false),
            new BracePair(TeaTokenTypes.LBRACKET, TeaTokenTypes.RBRACKET, false),
            new BracePair(TeaTokenTypes.LBRACE, TeaTokenTypes.RBRACE, true)
    };

    public BracePair[] getPairs() {
        return PAIRS;
    }

    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType tokenType) {
    return TeaTokenTypes.WHITE_SPACE  == tokenType
            || TeaTokenTypes.COMMENTS.contains(tokenType)
            || tokenType == TeaTokenTypes.SEMICOLON
            || tokenType == TeaTokenTypes.COMMA
            || tokenType == TeaTokenTypes.RPAR
            || tokenType == TeaTokenTypes.RBRACKET
            || tokenType == TeaTokenTypes.RBRACE
            || null == tokenType;    }

    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
