package com.go.teacup.intellij.lang.tea;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.psi.tree.TokenSet;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 6:10:00 PM
 */
public class TeaWordsScanner extends DefaultWordsScanner {
    /**
     * Creates a new instance of the words scanner.
     *
     */
    public TeaWordsScanner() {
        super(  new TeaLexer(),
                TokenSet.create(TeaTokenTypes.IDENTIFIER),
                TeaTokenTypes.COMMENTS,
                TokenSet.create(TeaTokenTypes.STRING_LITERAL)
        );
    }
}
