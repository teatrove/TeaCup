package com.go.teacup.intellij.lang.tea.highlighting;

import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.go.teacup.intellij.lang.tea.TeaBundle;
import com.go.teacup.intellij.lang.tea.TeaSupportLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Map;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 9:38:25 AM
 */
public class TeaColorsAndFontsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] ATTRS;

    static {
      ATTRS = new AttributesDescriptor[]{
        new AttributesDescriptor(TeaBundle.message("tea.keyword"), TeaHighlighter.TEA_KEYWORD),
        new AttributesDescriptor(TeaBundle.message("tea.string"), TeaHighlighter.TEA_STRING),
        new AttributesDescriptor(TeaBundle.message("tea.valid.string.escape"), TeaHighlighter.TEA_VALID_STRING_ESCAPE),
        new AttributesDescriptor(TeaBundle.message("tea.invalid.string.escape"), TeaHighlighter.TEA_INVALID_STRING_ESCAPE),
        new AttributesDescriptor(TeaBundle.message("tea.number"), TeaHighlighter.TEA_NUMBER),
//        new AttributesDescriptor(TeaBundle.message("tea.regexp"), TeaHighlighter.TEA_REGEXP),
        new AttributesDescriptor(TeaBundle.message("tea.linecomment"), TeaHighlighter.TEA_LINE_COMMENT),
        new AttributesDescriptor(TeaBundle.message("tea.blockcomment"), TeaHighlighter.TEA_BLOCK_COMMENT),
//        new AttributesDescriptor(TeaBundle.message("tea.doccomment"), TeaHighlighter.TEA_DOC_COMMENT),
        new AttributesDescriptor(TeaBundle.message("tea.operation"), TeaHighlighter.TEA_OPERATION_SIGN),
        new AttributesDescriptor(TeaBundle.message("tea.parens"), TeaHighlighter.TEA_PARENTHS),
        new AttributesDescriptor(TeaBundle.message("tea.brackets"), TeaHighlighter.TEA_BRACKETS),
        new AttributesDescriptor(TeaBundle.message("tea.braces"), TeaHighlighter.TEA_BRACES),
        new AttributesDescriptor(TeaBundle.message("tea.comma"), TeaHighlighter.TEA_COMMA),
        new AttributesDescriptor(TeaBundle.message("tea.dot"), TeaHighlighter.TEA_DOT),
        new AttributesDescriptor(TeaBundle.message("tea.semicolon"), TeaHighlighter.TEA_SEMICOLON),
        new AttributesDescriptor(TeaBundle.message("tea.badcharacter"), TeaHighlighter.TEA_BAD_CHARACTER),
        new AttributesDescriptor(TeaBundle.message("tea.scripting.background"), TeaHighlighter.TEA_SCRIPTING_BACKGROUND),
        new AttributesDescriptor(TeaBundle.message("tea.scripting.foreground"), TeaHighlighter.TEA_SCRIPTING_FOREGROUND),
//        new AttributesDescriptor(TeaBundle.message("tea.docmarkup"), TeaHighlighter.TEA_DOC_MARKUP),
//        new AttributesDescriptor(TeaBundle.message("tea.doctag"), TeaHighlighter.TEA_DOC_TAG),
      };
    }

    private static final ColorDescriptor[] COLORS = new ColorDescriptor[0];

    @NotNull
    public String getDisplayName() {
      //noinspection HardCodedStringLiteral
      return "Tea";
    }

    public Icon getIcon() {
      return TeaSupportLoader.TEA.getIcon();
    }

    @NotNull
    public AttributesDescriptor[] getAttributeDescriptors() {
      return ATTRS;
    }

    @NotNull
    public ColorDescriptor[] getColorDescriptors() {
      return COLORS;
    }

    @NotNull
    public SyntaxHighlighter getHighlighter() {
      return SyntaxHighlighter.PROVIDER.create(TeaSupportLoader.TEA, null, null);
    }

    @NotNull
    public String getDemoText() {
      return "<% \n" +
              "import com.example.tea\n" +
              "\n" +
              "/*\n" +
              " * This is an example Tea template.\n" +
              " */\n" +
              "template exampleTemplate(String a, int[] arr)\n" +
              "\n" +
              "  define boolean b = true\n" +
              "  c = 123 as Integer\n" +
              "  \n" +
              "  // This is a comment\n" +
              "  list = #(\"a\",\"b\",\"c\")\n" +
              "  map  = ##(\"a\",\"1\",\n" +
              "            \"b\", \"2\")\n" +
              "\n" +
              "  call anotherTemplate(list, map)\n" +
              "%>\n" +
              "<html>\n" +
              "  <head>\n" +
              "  </head>\n" +
              "  <body>\n" +
              "  <%\n" +
              "    'String Valid Escape: \\t'\n" +
              "    \"String Invalid Escape: \\\"\n" +
              "  %>\n" +
              "  <%foreach(i in 1..10 reverse){%>\n" +
              "  <li><%i%>\n" +
              "  <%}%>\n" +
              "  </body>\n" +
              "</html>";
    }

    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
      return null;
    }
}
