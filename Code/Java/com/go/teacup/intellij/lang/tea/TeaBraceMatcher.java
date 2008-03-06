package com.go.teacup.intellij.lang.tea;

import com.intellij.lang.PairedBraceMatcher;
import com.intellij.lang.BracePair;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 11:07:57 AM
 */
public class TeaBraceMatcher implements PairedBraceMatcher {
    private static final BracePair[] PAIRS = new BracePair[] {
            new BracePair('(',TeaTokenTypes.LPAR, ')', TeaTokenTypes.RPAR, false),
            new BracePair('[',TeaTokenTypes.LBRACKET, ']', TeaTokenTypes.RBRACKET, false),
            new BracePair('{',TeaTokenTypes.LBRACE, '}', TeaTokenTypes.RBRACE, true)
    };

    public BracePair[] getPairs() {
        return PAIRS;
    }
}
