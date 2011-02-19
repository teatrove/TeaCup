package com.go.teacup.intellij.lang.tea;

import com.go.teacup.intellij.lang.tea.psi.impl.stubs.TeaStubElementType;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 1:15:16 PM
 */
public final class TeaTokenTypes {
    private TeaTokenTypes(){}

    public static final IElementType IDENTIFIER = new TeaStubElementType("IDENTIFIER");
    public static final IElementType FQN = new TeaStubElementType("FQN");
    public static final IElementType WHITE_SPACE = TokenType.WHITE_SPACE;
    public static final IElementType SCRIPT_WHITE_SPACE = new TeaStubElementType("SCRIPT_WHITE_SPACE");
    public static final IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;
    public static final IElementType PLAIN_TEXT = new TeaStubElementType("PLAIN_TEXT");
    
    public static final IElementType SEMANTIC_LINEFEED = new TeaStubElementType("SEMANTIC_LINEFEED");

    public static final IElementType END_OF_LINE_COMMENT = new TeaStubElementType("END_OF_LINE_COMMENT");
    public static final IElementType C_STYLE_COMMENT = new TeaStubElementType("C_STYLE_COMMENT");
//    public static final IElementType XML_STYLE_COMMENT = new TeaStubElementType("XML_STYLE_COMMENT");
//    public static final IElementType DOC_COMMENT = new TeaStubElementType("DOC_COMMENT");


    // Keywords
    public static final IElementType AND_KEYWORD = new TeaStubElementType("AND_KEYWORD");
    public static final IElementType AS_KEYWORD = new TeaStubElementType("AS_KEYWORD");
    public static final IElementType BREAK_KEYWORD = new TeaStubElementType("BREAK_KEYWORD");
    public static final IElementType CALL_KEYWORD = new TeaStubElementType("CALL_KEYWORD");
    public static final IElementType DEFINE_KEYWORD = new TeaStubElementType("DEFINE_KEYWORD");
    public static final IElementType ELSE_KEYWORD = new TeaStubElementType("ELSE_KEYWORD");
    public static final IElementType FOREACH_KEYWORD = new TeaStubElementType("FOREACH_KEYWORD");
    public static final IElementType IF_KEYWORD = new TeaStubElementType("IF_KEYWORD");
    public static final IElementType IMPORT_KEYWORD = new TeaStubElementType("IMPORT_KEYWORD");
    public static final IElementType IN_KEYWORD = new TeaStubElementType("IN_KEYWORD");
    public static final IElementType NOT_KEYWORD = new TeaStubElementType("NOT_KEYWORD");
    public static final IElementType OR_KEYWORD = new TeaStubElementType("OR_KEYWORD");
    public static final IElementType REVERSE_KEYWORD = new TeaStubElementType("REVERSE_KEYWORD");
    public static final IElementType TEMPLATE_KEYWORD = new TeaStubElementType("TEMPLATE_KEYWORD");

    // Hardcoded literals
    public static final IElementType TRUE_KEYWORD = new TeaStubElementType("TRUE_KEYWORD");
    public static final IElementType FALSE_KEYWORD = new TeaStubElementType("FALSE_KEYWORD");
    public static final IElementType NULL_KEYWORD = new TeaStubElementType("NULL_KEYWORD");

    // New array operators
    public static final IElementType HASH = new TeaStubElementType("HASH");
    public static final IElementType HASHHASH = new TeaStubElementType("HASHHASH");

    public static final IElementType ISA = new TeaStubElementType("ISA");

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
    public static final IElementType NUMERIC_LITERAL = new TeaStubElementType("NUMERIC_LITERAL");
    public static final IElementType SINGLE_QUOTE_STRING_LITERAL = new TeaStubElementType("SINGLE_QUOTE_STRING_LITERAL");
    public static final IElementType STRING_LITERAL = new TeaStubElementType("STRING_LITERAL");

    // Separators
    public static final IElementType LPAR = new TeaStubElementType("LPAR");
    public static final IElementType RPAR = new TeaStubElementType("RPAR");
    public static final IElementType LBRACE = new TeaStubElementType("LBRACE");
    public static final IElementType RBRACE = new TeaStubElementType("RBRACE");
    public static final IElementType LBRACKET = new TeaStubElementType("LBRACKET");
    public static final IElementType RBRACKET = new TeaStubElementType("RBRACKET");
    public static final IElementType SEMICOLON = new TeaStubElementType("SEMICOLON");
    public static final IElementType COMMA = new TeaStubElementType("COMMA");
    public static final IElementType DOT = new TeaStubElementType("DOT");
    public static final IElementType LSCRIPT = new TeaStubElementType("LSCRIPT");
    public static final IElementType RSCRIPT = new TeaStubElementType("RSCRIPT");

    // Operators
    public static final IElementType EQ = new TeaStubElementType("EQ");
    public static final IElementType DOTDOT = new TeaStubElementType("DOTDOT");
    public static final IElementType ELLIPSIS = new TeaStubElementType("ELLIPSIS");
    public static final IElementType EQEQ = new TeaStubElementType("EQEQ");
    public static final IElementType NE = new TeaStubElementType("NE");
    public static final IElementType LT = new TeaStubElementType("LT");
    public static final IElementType GT = new TeaStubElementType("GT");
    public static final IElementType LE = new TeaStubElementType("LE");
    public static final IElementType GE = new TeaStubElementType("GE");
    public static final IElementType PLUS = new TeaStubElementType("PLUS");
    public static final IElementType MINUS = new TeaStubElementType("MINUS");
    public static final IElementType MULT = new TeaStubElementType("MULT");
    public static final IElementType DIV = new TeaStubElementType("DIV");
    public static final IElementType PERC = new TeaStubElementType("PERC");
    public static final IElementType AND = new TeaStubElementType("AND");

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
