package com.go.teacup.intellij.lang.tea.refactoring;

import com.go.teacup.intellij.lang.tea.lexer.TeaLexer;
import com.go.teacup.intellij.lang.tea.lexer.TeaTokenTypes;
import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;

/**
 * User: JACKSBRR
 * Created: Apr 20, 2007 3:48:33 PM
 */
public class TeaNamesValidator implements NamesValidator {
    private Lexer lexer = new TeaLexer();

    public boolean isKeyword(String name, Project project) {
        lexer.start(createDummyTemplate(name));
        
        return !skipDummyHeader()
                && TeaTokenTypes.KEYWORDS.contains(lexer.getTokenType())
                && (lexer.getTokenEnd()-lexer.getTokenStart()) == name.length();
    }

    public boolean isIdentifier(String name, Project project) {
        lexer.start(createDummyTemplate(name));
        return !skipDummyHeader()
                && lexer.getTokenType() == TeaTokenTypes.IDENTIFIER
                && (lexer.getTokenEnd()-lexer.getTokenStart()) == name.length();

    }

    private boolean skipDummyHeader() {
        IElementType tokenType = lexer.getTokenType();
        if(tokenType != TeaTokenTypes.LSCRIPT) {
            return true;
        }
        lexer.advance();
        tokenType = lexer.getTokenType();
        if(tokenType != TeaTokenTypes.SCRIPT_WHITE_SPACE) {
            return true;
        }
        lexer.advance();
        tokenType = lexer.getTokenType();
        if(tokenType != TeaTokenTypes.TEMPLATE_KEYWORD) {
            return true;
        }
        lexer.advance();
        tokenType = lexer.getTokenType();
        if(tokenType != TeaTokenTypes.SCRIPT_WHITE_SPACE) {
            return true;
        }
        lexer.advance();
        tokenType = lexer.getTokenType();
        if(tokenType != TeaTokenTypes.IDENTIFIER) {
            return true;
        }
        lexer.advance();
        tokenType = lexer.getTokenType();
        if(tokenType != TeaTokenTypes.LPAR) {
            return true;
        }
        lexer.advance();
        tokenType = lexer.getTokenType();
        if(tokenType != TeaTokenTypes.RPAR) {
            return true;
        }
        lexer.advance();
        tokenType = lexer.getTokenType();
        if(tokenType != TeaTokenTypes.SCRIPT_WHITE_SPACE) {
            return true;
        }

        lexer.advance();
        return false;
    }

    private String createDummyTemplate(String body) {
        return "<% template dummy()\n"+ body +"\n%>";
    }
}
