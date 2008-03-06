package com.go.teacup.intellij.lang.tea;

import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.Language;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 2:48:24 PM
 */
public final class TeaElementTypes {
    private TeaElementTypes(){}

    public static final IFileElementType FILE = new IFileElementType(Language.findInstance(TeaLanguage.class));
    public static final IElementType IMPORT_STATEMENT = new TeaElementType("IMPORT_STATEMENT");
    public static final IElementType TEMPLATE_DECLARATION = new TeaElementType("TEMPLATE_DECLARATION");
    public static final IElementType PARAMETER_LIST = new TeaElementType("PARAMETER_LIST");
    public static final IElementType TYPE_DECLARATION = new TeaElementType("TYPE_DECLARATION");
    public static final IElementType FORMAL_PARAMETER = new TeaElementType("FORMAL_PARAMETER");
    public static final IElementType VARIABLE = new TeaElementType("VARIABLE");
    public static final IElementType TYPE = new TeaElementType("TYPE");
    public static final IElementType ARGUMENT_LIST = new TeaElementType("ARGUMENT_LIST");
    public static final IElementType SUBSTITUTION_PARAMETER = new TeaElementType("SUBSTITUTION_PARAMETER");
    public static final IElementType EMBEDDED_SCRIPT = new TeaElementType("EMBEDDED_SCRIPT");
    public static final IElementType PLAIN_TEXT = new TeaElementType("PLAIN_TEXT");

    // Statements
    public static final IElementType BLOCK = new TeaElementType("BLOCK");
    public static final IElementType EXPRESSION_STATEMENT = new TeaElementType("EXPRESSION_STATEMENT");
    public static final IElementType DEFINE_STATEMENT = new TeaElementType("DEFINE_STATEMENT");
    public static final IElementType EMPTY_STATEMENT = new TeaElementType("EMPTY_STATEMENT");
    public static final IElementType IF_STATEMENT = new TeaElementType("IF_STATEMENT");
    public static final IElementType BREAK_STATEMENT = new TeaElementType("BREAK_STATEMENT");
    public static final IElementType FOREACH_STATEMENT = new TeaElementType("FOREACH_STATEMENT");
    public static final IElementType CALL_STATEMENT = new TeaElementType("CALL_STATEMENT");
    public static final IElementType SUBSTITUTION_STATEMENT = new TeaElementType("SUBSTITUTION_STATEMENT");
    public static final IElementType ASSIGNMENT_STATEMENT = new TeaElementType("ASSIGNMENT_STATEMENT");

    public static final TokenSet STATEMENTS =
            TokenSet.create(
                    BLOCK,
                    EXPRESSION_STATEMENT,
                    DEFINE_STATEMENT,
                    EMPTY_STATEMENT,
                    IF_STATEMENT,
                    BREAK_STATEMENT,
                    FOREACH_STATEMENT,
                    CALL_STATEMENT,
                    SUBSTITUTION_STATEMENT,
                    ASSIGNMENT_STATEMENT
            );

    public static final TokenSet SOURCE_ELEMENTS =
            TokenSet.orSet(
                    STATEMENTS,
                    TokenSet.create(TEMPLATE_DECLARATION)
            );
    
    // Expressions
    public static final IElementType OR_EXPRESSION = new TeaElementType("OR_EXPRESSION");
    public static final IElementType AND_EXPRESSION = new TeaElementType("AND_EXPRESSION");
    public static final IElementType EQUALITY_EXPRESSION = new TeaElementType("EQUALITY_EXPRESSION");
    public static final IElementType RELATIONAL_EXPRESSION = new TeaElementType("RELATIONAL_EXPRESSION");
    public static final IElementType CONCATENATE_EXPRESSION = new TeaElementType("CONCATENATE_EXPRESSION");
//    public static final IElementType LOOKUP_EXPRESSION = new TeaElementType("LOOKUP_EXPRESSION");
    public static final IElementType LOOKUP = new TeaElementType("LOOKUP");
    public static final IElementType FACTOR = new TeaElementType("FACTOR");
    public static final IElementType ARRAY_LITERAL_EXPRESSION = new TeaElementType("ARRAY_LITERAL_EXPRESSION");
    public static final IElementType FUNCTION_CALL_EXPRESSION = new TeaElementType("FUNCTION_CALL_EXPRESSION");
    public static final IElementType TEMPLATE_CALL_EXPRESSION = new TeaElementType("TEMPLATE_CALL_EXPRESSION");
    public static final IElementType BINARY_EXPRESSION = new TeaElementType("BINARY_EXPRESSION");
    public static final IElementType PREFIX_EXPRESSION = new TeaElementType("PREFIX_EXPRESSION");
    public static final IElementType REFERENCE_EXPRESSION = new TeaElementType("REFERENCE_EXPRESSION");
    public static final IElementType INDEXED_PROPERTY_ACCESS_EXPRESSION = new TeaElementType("INDEXED_PROPERTY_ACCESS_EXPRESSION");
    public static final IElementType LITERAL_EXPRESSION = new TeaElementType("LITERAL_EXPRESSION");
    public static final IElementType PARENTHESIZED_EXPRESSION = new TeaElementType("PARENTHESIZED_EXPRESSION");
    public static final IElementType DEFINITION_EXPRESSION = new TeaElementType("DEFINITION_EXPRESSION");

    public static final TokenSet EXPRESSIONS =
            TokenSet.create(
                    OR_EXPRESSION,
                    AND_EXPRESSION,
                    EQUALITY_EXPRESSION,
                    RELATIONAL_EXPRESSION,
                    CONCATENATE_EXPRESSION,
//                    LOOKUP_EXPRESSION,
                    LOOKUP,
                    FACTOR,
                    ARRAY_LITERAL_EXPRESSION,
                    FUNCTION_CALL_EXPRESSION,
                    TEMPLATE_CALL_EXPRESSION,
                    BINARY_EXPRESSION,
                    PREFIX_EXPRESSION,
                    REFERENCE_EXPRESSION,
                    INDEXED_PROPERTY_ACCESS_EXPRESSION,
                    LITERAL_EXPRESSION,
                    PARENTHESIZED_EXPRESSION,
                    DEFINITION_EXPRESSION
            );
}
