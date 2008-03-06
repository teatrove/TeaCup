package com.go.teacup.intellij.lang.tea;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 6:12:55 PM
 */
public class TeaLexer extends FlexAdapter {

    public TeaLexer() {
        super(new _TeaLexer((Reader)null));
    }
}
