package com.go.teacup.intellij.lang.tea;

import com.intellij.lexer.FlexAdapter;
import com.intellij.psi.tree.IElementType;

import java.io.Reader;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 2:22:14 PM
 */
public class TeaParsingLexer extends FlexAdapter {
    private boolean onBreak = false;
    private boolean onSemanticLineFeed = false;

    private final static int ON_BREAK = 3;
    private final static int ON_SEMANTIC_LF = 4;

    public TeaParsingLexer() {
        super(new _TeaLexer((Reader)null));
    }

    @Override
    public void advance() {
        super.advance();
    }
    
    public void advanceOld2() {
        if(!onSemanticLineFeed) {
            super.advance();
            final IElementType type = getTokenType();

            if(type == TeaTokenTypes.WHITE_SPACE) {
                boolean hasLineFeed = false;
                for(int i = super.getTokenStart(); i < super.getTokenEnd(); i++) {
                    if(getBufferSequence().charAt(i) == '\n') {
                        hasLineFeed = true;
                        break;
                    }
                }

                if(hasLineFeed) {
                    onSemanticLineFeed = true;
                }
            }

//            onBreak = (type == TeaTokenTypes.BREAK_KEYWORD);
        }
        else {
            onSemanticLineFeed = false;
//            onBreak = false;
        }
    }

    public void advanceOld() {
        if(!onSemanticLineFeed) {
            super.advance();
            final IElementType type = getTokenType();

            if(onBreak && type == TeaTokenTypes.WHITE_SPACE) {
                boolean hasLineFeed = false;
                for(int i = super.getTokenStart(); i < super.getTokenEnd(); i++) {
                    if(getBufferSequence().charAt(i) == '\n') {
                        hasLineFeed = true;
                        break;
                    }
                }

                if(hasLineFeed) {
                    onSemanticLineFeed = true;
                }
            }

            onBreak = (type == TeaTokenTypes.BREAK_KEYWORD);
        }
        else {
            onSemanticLineFeed = false;
            onBreak = false;
        }
    }


    @Override
    public IElementType getTokenType() {
        return onSemanticLineFeed ? TeaTokenTypes.SEMANTIC_LINEFEED : super.getTokenType();
    }

    @Override
    public int getTokenStart() {
        return super.getTokenStart();
    }

    @Override
    public int getTokenEnd() {
        return onSemanticLineFeed ? super.getTokenStart() : super.getTokenEnd();
    }

    @Override
    public int getState() {
        if(onSemanticLineFeed) return ON_SEMANTIC_LF;
        if(onBreak) return ON_BREAK;
        return super.getState();
    }
}
