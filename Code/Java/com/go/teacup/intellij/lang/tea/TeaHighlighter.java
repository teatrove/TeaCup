package com.go.teacup.intellij.lang.tea;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.StringEscapesTokenTypes;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 11:07:05 AM
 */
public class TeaHighlighter extends SyntaxHighlighterBase {
    private static Map<IElementType, TextAttributesKey> keys1;
    private static Map<IElementType, TextAttributesKey> keys2;


    @NotNull
    public Lexer getHighlightingLexer() {
        return new TeaLexer();
    }
    static final TextAttributesKey TEA_KEYWORD = TextAttributesKey.createTextAttributesKey(
                                                  "TEA.KEYWORD",
                                                  HighlighterColors.JAVA_KEYWORD.getDefaultAttributes()
                                                );

    static final TextAttributesKey TEA_STRING = TextAttributesKey.createTextAttributesKey(
                                                 "TEA.STRING",
                                                 HighlighterColors.JAVA_STRING.getDefaultAttributes()
                                               );

    static final TextAttributesKey TEA_NUMBER = TextAttributesKey.createTextAttributesKey(
                                                 "TEA.NUMBER",
                                                 HighlighterColors.JAVA_NUMBER.getDefaultAttributes()
                                               );

//    static final TextAttributesKey TEA_REGEXP = TextAttributesKey.createTextAttributesKey(
//                                                 "TEA.REGEXP",
//                                                 new TextAttributes(Color.blue.brighter(), null, null, null, Font.PLAIN)
//                                               );

    static final TextAttributesKey TEA_LINE_COMMENT = TextAttributesKey.createTextAttributesKey(
                                                       "TEA.LINE_COMMENT",
                                                       HighlighterColors.JAVA_LINE_COMMENT.getDefaultAttributes()
                                                     );

    static final TextAttributesKey TEA_BLOCK_COMMENT = TextAttributesKey.createTextAttributesKey(
                                                        "TEA.BLOCK_COMMENT",
                                                        HighlighterColors.JAVA_BLOCK_COMMENT.getDefaultAttributes()
                                                      );

//    static final TextAttributesKey TEA_DOC_COMMENT = TextAttributesKey.createTextAttributesKey(
//                                                      "TEA.DOC_COMMENT",
//                                                      HighlighterColors.JAVA_DOC_COMMENT.getDefaultAttributes()
//                                                    );

    static final TextAttributesKey TEA_OPERATION_SIGN = TextAttributesKey.createTextAttributesKey(
                                                         "TEA.OPERATION_SIGN",
                                                         HighlighterColors.JAVA_OPERATION_SIGN.getDefaultAttributes()
                                                       );

    static final TextAttributesKey TEA_PARENTHS = TextAttributesKey.createTextAttributesKey(
                                                   "TEA.PARENTHS",
                                                   HighlighterColors.JAVA_PARENTHS.getDefaultAttributes()
                                                 );

    static final TextAttributesKey TEA_BRACKETS = TextAttributesKey.createTextAttributesKey(
                                                   "TEA.BRACKETS",
                                                   HighlighterColors.JAVA_BRACKETS.getDefaultAttributes()
                                                 );

    static final TextAttributesKey TEA_BRACES = TextAttributesKey.createTextAttributesKey(
                                                 "TEA.BRACES",
                                                 HighlighterColors.JAVA_BRACES.getDefaultAttributes()
                                               );

    static final TextAttributesKey TEA_COMMA = TextAttributesKey.createTextAttributesKey(
                                                "TEA.COMMA",
                                                HighlighterColors.JAVA_COMMA.getDefaultAttributes()
                                              );

    static final TextAttributesKey TEA_DOT = TextAttributesKey.createTextAttributesKey(
                                              "TEA.DOT",
                                              HighlighterColors.JAVA_DOT.getDefaultAttributes()
                                            );

    static final TextAttributesKey TEA_SEMICOLON = TextAttributesKey.createTextAttributesKey(
                                                    "TEA.SEMICOLON",
                                                    HighlighterColors.JAVA_SEMICOLON.getDefaultAttributes()
                                                  );

    static final TextAttributesKey TEA_BAD_CHARACTER = TextAttributesKey.createTextAttributesKey(
                                                    "TEA.BADCHARACTER",
                                                    HighlighterColors.BAD_CHARACTER.getDefaultAttributes()
                                                  );
//    static final TextAttributesKey TEA_DOC_TAG = TextAttributesKey.createTextAttributesKey(
//                                                      "TEA.DOC_TAG",
//                                                      HighlighterColors.JAVA_DOC_TAG.getDefaultAttributes()
//                                                    );
    static final TextAttributesKey TEA_DOC_MARKUP = TextAttributesKey.createTextAttributesKey(
                                                      "TEA.DOC_MARKUP",
                                                      HighlighterColors.JAVA_DOC_MARKUP.getDefaultAttributes()
                                                    );
    static final TextAttributesKey TEA_VALID_STRING_ESCAPE = TextAttributesKey.createTextAttributesKey(
                                                      "TEA.VALID_STRING_ESCAPE",
                                                      HighlighterColors.JAVA_VALID_STRING_ESCAPE.getDefaultAttributes()
                                                    );
    static final TextAttributesKey TEA_INVALID_STRING_ESCAPE = TextAttributesKey.createTextAttributesKey(
                                                      "TEA.INVALID_STRING_ESCAPE",
                                                      HighlighterColors.JAVA_INVALID_STRING_ESCAPE.getDefaultAttributes()
                                                    );

    static final TextAttributesKey TEA_SCRIPTING_BACKGROUND = TextAttributesKey.createTextAttributesKey(
                                                      "TEA.SCRIPTING_BACKGROUND",
                                                      HighlighterColors.JSP_SCRIPTING_BACKGROUND.getDefaultAttributes()
                                                    );
    static final TextAttributesKey TEA_SCRIPTING_FOREGROUND = TextAttributesKey.createTextAttributesKey(
                                                      "TEA.SCRIPTING_FOREGROUND",
                                                      HighlighterColors.XML_TAG_NAME.getDefaultAttributes()
                                                    );
//    static final TextAttributesKey TEA_DEBUG = TextAttributesKey.createTextAttributesKey(
//                                                      "TEA.DEBUG",
//                                                      new TextAttributes(
//                                                              null,
//                                                              Color.RED,
//                                                              null,
//                                                              null,
//                                                              Font.PLAIN
//                                                      )
//                                                    );

    static {
      keys1 = new HashMap<IElementType, TextAttributesKey>();
      keys2 = new HashMap<IElementType, TextAttributesKey>();

      fillMap(keys1, TeaTokenTypes.KEYWORDS, TEA_KEYWORD);
      fillMap(keys1, TeaTokenTypes.OPERATIONS, TEA_OPERATION_SIGN);

      keys1.put(StringEscapesTokenTypes.VALID_STRING_ESCAPE_TOKEN, TEA_VALID_STRING_ESCAPE);
      keys1.put(StringEscapesTokenTypes.INVALID_CHARACTER_ESCAPE_TOKEN, TEA_INVALID_STRING_ESCAPE);
      keys1.put(StringEscapesTokenTypes.INVALID_UNICODE_ESCAPE_TOKEN, TEA_INVALID_STRING_ESCAPE);

      keys1.put(TeaTokenTypes.NUMERIC_LITERAL, TEA_NUMBER);
      keys1.put(TeaTokenTypes.STRING_LITERAL, TEA_STRING);
      keys1.put(TeaTokenTypes.SINGLE_QUOTE_STRING_LITERAL, TEA_STRING);

      keys1.put(TeaTokenTypes.LPAR, TEA_PARENTHS);
      keys1.put(TeaTokenTypes.RPAR, TEA_PARENTHS);

      keys1.put(TeaTokenTypes.LBRACE, TEA_BRACES);
      keys1.put(TeaTokenTypes.RBRACE, TEA_BRACES);

      keys1.put(TeaTokenTypes.LBRACKET, TEA_BRACKETS);
      keys1.put(TeaTokenTypes.RBRACKET, TEA_BRACKETS);

      keys1.put(TeaTokenTypes.COMMA, TEA_COMMA);
      keys1.put(TeaTokenTypes.DOT, TEA_DOT);
      keys1.put(TeaTokenTypes.SEMICOLON, TEA_SEMICOLON);

      keys1.put(TeaTokenTypes.C_STYLE_COMMENT, TEA_BLOCK_COMMENT);
      keys1.put(TeaTokenTypes.END_OF_LINE_COMMENT, TEA_LINE_COMMENT);
      keys1.put(TeaTokenTypes.BAD_CHARACTER, TEA_BAD_CHARACTER);

//      keys1.put(JavaDocTokenType.DOC_TAG_NAME, TEA_DOC_COMMENT);
//      keys2.put(JavaDocTokenType.DOC_TAG_NAME, TEA_DOC_TAG);

      keys1.put(TeaTokenTypes.LSCRIPT, TEA_SCRIPTING_FOREGROUND);
      keys1.put(TeaTokenTypes.RSCRIPT, TEA_SCRIPTING_FOREGROUND);
      keys1.put(TeaTokenTypes.SCRIPT_WHITE_SPACE, TEA_SCRIPTING_BACKGROUND);
      keys1.put(TeaTokenTypes.IDENTIFIER, TEA_SCRIPTING_BACKGROUND);


//        keys1.put(TeaElementTypes.REFERENCE_EXPRESSION, TEA_DEBUG);

//      IElementType[] javadoc = IElementType.enumerate(new IElementType.Predicate() {
//        public boolean matches(IElementType type) {
//          return type instanceof IJavaDocElementType;
//        }
//      });
//
//      for (IElementType type : javadoc) {
//        keys1.put(type, TEA_DOC_COMMENT);
//      }
    }

    @NotNull
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        final TextAttributesKey[] keys = pack(keys1.get(tokenType), keys2.get(tokenType));
        return keys.length > 0 ? pack(keys, TEA_SCRIPTING_BACKGROUND) : keys;
    }

    public Map<IElementType, TextAttributesKey> getKeys1() {
        //noinspection unchecked
        return (Map<IElementType, TextAttributesKey>)((HashMap)keys1).clone();
    }

    public Map<IElementType, TextAttributesKey> getKeys2() {
        //noinspection unchecked
        return (Map<IElementType, TextAttributesKey>)((HashMap)keys2).clone();
    }

//    public static void registerHtmlMarkup(IElementType[] htmlTokens, IElementType[] htmlTokens2) {
//      for (IElementType idx : htmlTokens) {
//        keys1.put(idx, TEA_DOC_COMMENT);
//        keys2.put(idx, TEA_DOC_MARKUP);
//      }
//
//      for (IElementType idx : htmlTokens2) {
//        keys1.put(idx, TEA_DOC_COMMENT);
//      }
//    }
}
