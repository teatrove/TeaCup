package com.go.teacup.intellij.lang.tea.lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 1:15:16 PM
 */
public final class TeaTokenTypes {

    private TeaTokenTypes() {}

    public static final IElementType IDENTIFIER = new TeaElementType("IDENTIFIER");
    public static final IElementType FQN = new TeaElementType("FQN");
    public static final IElementType WHITE_SPACE = TokenType.WHITE_SPACE;
    public static final IElementType SCRIPT_WHITE_SPACE = new TeaElementType("SCRIPT_WHITE_SPACE");
    public static final IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;
    public static final IElementType PLAIN_TEXT = new TeaElementType("PLAIN_TEXT");
    
    public static final IElementType SEMANTIC_LINEFEED = new TeaElementType("SEMANTIC_LINEFEED");

    public static final IElementType END_OF_LINE_COMMENT = new TeaElementType("END_OF_LINE_COMMENT");
    public static final IElementType C_STYLE_COMMENT = new TeaElementType("C_STYLE_COMMENT");
//    public static final IElementType XML_STYLE_COMMENT = new TeaElementType("XML_STYLE_COMMENT");
//    public static final IElementType DOC_COMMENT = new TeaElementType("DOC_COMMENT");


    // Keywords
    public static final IElementType AND_KEYWORD = new TeaElementType("AND_KEYWORD");
    public static final IElementType AS_KEYWORD = new TeaElementType("AS_KEYWORD");
    public static final IElementType BREAK_KEYWORD = new TeaElementType("BREAK_KEYWORD");
    public static final IElementType CALL_KEYWORD = new TeaElementType("CALL_KEYWORD");
    public static final IElementType DEFINE_KEYWORD = new TeaElementType("DEFINE_KEYWORD");
    public static final IElementType ELSE_KEYWORD = new TeaElementType("ELSE_KEYWORD");
    public static final IElementType FOREACH_KEYWORD = new TeaElementType("FOREACH_KEYWORD");
    public static final IElementType IF_KEYWORD = new TeaElementType("IF_KEYWORD");
    public static final IElementType IMPORT_KEYWORD = new TeaElementType("IMPORT_KEYWORD");
    public static final IElementType IN_KEYWORD = new TeaElementType("IN_KEYWORD");
    public static final IElementType NOT_KEYWORD = new TeaElementType("NOT_KEYWORD");
    public static final IElementType OR_KEYWORD = new TeaElementType("OR_KEYWORD");
    public static final IElementType REVERSE_KEYWORD = new TeaElementType("REVERSE_KEYWORD");
    public static final IElementType TEMPLATE_KEYWORD = new TeaElementType("TEMPLATE_KEYWORD");

    // Hardcoded literals
    public static final IElementType TRUE_KEYWORD = new TeaElementType("TRUE_KEYWORD");
    public static final IElementType FALSE_KEYWORD = new TeaElementType("FALSE_KEYWORD");
    public static final IElementType NULL_KEYWORD = new TeaElementType("NULL_KEYWORD");

    // New array operators
    public static final IElementType HASH = new TeaElementType("HASH");
    public static final IElementType HASHHASH = new TeaElementType("HASHHASH");

    public static final IElementType ISA = new TeaElementType("ISA");

    public static final TokenSet KEYWORDS =
            TokenSet.create(
                    AND_KEYWORD,
                    AS_KEYWORD,
                    BREAK_KEYWORD,
                    CALL_KEYWORD,
                    DEFINE_KEYWORD,
                    ELSE_KEYWORD,
                    FOREACH_KEYWORD,
                    IF_KEYWORD,
                    IMPORT_KEYWORD,
                    IN_KEYWORD,
                    NOT_KEYWORD,
                    OR_KEYWORD,
                    REVERSE_KEYWORD,
                    TEMPLATE_KEYWORD,
                    TRUE_KEYWORD,
                    FALSE_KEYWORD,
                    NULL_KEYWORD,
                    HASH,
                    HASHHASH,
                    ISA
            );
    
    // Literals
    public static final IElementType NUMERIC_LITERAL = new TeaElementType("NUMERIC_LITERAL");
    public static final IElementType SINGLE_QUOTE_STRING_LITERAL = new TeaElementType("SINGLE_QUOTE_STRING_LITERAL");
    public static final IElementType STRING_LITERAL = new TeaElementType("STRING_LITERAL");

    // Separators
    public static final IElementType LPAR = new TeaElementType("LPAR");
    public static final IElementType RPAR = new TeaElementType("RPAR");
    public static final IElementType LBRACE = new TeaElementType("LBRACE");
    public static final IElementType RBRACE = new TeaElementType("RBRACE");
    public static final IElementType LBRACKET = new TeaElementType("LBRACKET");
    public static final IElementType RBRACKET = new TeaElementType("RBRACKET");
    public static final IElementType SEMICOLON = new TeaElementType("SEMICOLON");
    public static final IElementType COMMA = new TeaElementType("COMMA");
    public static final IElementType DOT = new TeaElementType("DOT");
    public static final IElementType LSCRIPT = new TeaElementType("LSCRIPT");
    public static final IElementType RSCRIPT = new TeaElementType("RSCRIPT");

    // Operators
    public static final IElementType EQ = new TeaElementType("EQ");
    public static final IElementType DOTDOT = new TeaElementType("DOTDOT");
    public static final IElementType ELLIPSIS = new TeaElementType("ELLIPSIS");
    public static final IElementType EQEQ = new TeaElementType("EQEQ");
    public static final IElementType NE = new TeaElementType("NE");
    public static final IElementType LT = new TeaElementType("LT");
    public static final IElementType GT = new TeaElementType("GT");
    public static final IElementType LE = new TeaElementType("LE");
    public static final IElementType GE = new TeaElementType("GE");
    public static final IElementType PLUS = new TeaElementType("PLUS");
    public static final IElementType MINUS = new TeaElementType("MINUS");
    public static final IElementType MULT = new TeaElementType("MULT");
    public static final IElementType DIV = new TeaElementType("DIV");
    public static final IElementType PERC = new TeaElementType("PERC");
    public static final IElementType AND = new TeaElementType("AND");

    public static final TokenSet OPERATIONS =
            TokenSet.create(
                    EQ,
                    HASH,
                    HASHHASH,
                    DOTDOT,
                    ELLIPSIS,
                    EQEQ,
                    NE,
                    LT,
                    GT,
                    LE,
                    GE,
                    PLUS,
                    MINUS,
                    DIV,
                    PERC,
                    AND
            );

    public static final TokenSet ASSIGNMENT_OPERATIONS =
            TokenSet.create(
                    EQ
            );

    public static final TokenSet EQUALITY_OPERATIONS =
            TokenSet.create(
                    EQEQ, NE
            );

    public static final TokenSet RELATIONAL_OPERATIONS =
            TokenSet.create(
                    LT, GT, LE, GE, ISA
            );

    public static final TokenSet ADDITIVE_OPERATIONS =
            TokenSet.create(
                    PLUS, MINUS
            );

    public static final TokenSet MULTIPLICATIVE_OPERATIONS =
            TokenSet.create(
                    MULT, DIV, PERC
            );

    public static final TokenSet UNARY_OPERATIONS =
            TokenSet.create(
                    NOT_KEYWORD, MINUS
            );

    public static final TokenSet COMMENTS =
            TokenSet.create(
                    END_OF_LINE_COMMENT, C_STYLE_COMMENT
            );
}
