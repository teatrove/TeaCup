package com.go.teacup.intellij.lang.tea;

import com.go.teacup.intellij.lang.tea.highlighting.TeaHighlighter;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import org.jetbrains.annotations.NotNull;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 10:50:25 AM
 */
public class TeaLanguage extends Language {

    public TeaLanguage() {
        super("Tea", "text/tea", "application/tea");

        SyntaxHighlighterFactory.LANGUAGE_FACTORY.addExplicitExtension(this, new SingleLazyInstanceSyntaxHighlighterFactory() {
            @NotNull
            @Override
            protected SyntaxHighlighter createHighlighter() {
                return new TeaHighlighter();
            }
        });

        
    }

}
