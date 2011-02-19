package com.go.teacup.intellij.lang.tea;

import com.go.teacup.intellij.lang.tea.psi.impl.stubs.TeaStubElementType;
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
    public static final IElementType IMPORT_STATEMENT = new TeaStubElementType("IMPORT_STATEMENT");
    public static final IElementType TEMPLATE_DECLARATION = new TeaStubElementType("TEMPLATE_DECLARATION");
    public static final IElementType PARAMETER_LIST = new TeaStubElementType("PARAMETER_LIST");
    public static final IElementType TYPE_DECLARATION = new TeaStubElementType("TYPE_DECLARATION");
    public static final IElementType FORMAL_PARAMETER = new TeaStubElementType("FORMAL_PARAMETER");
    public static final IElementType VARIABLE = new TeaStubElementType("VARIABLE");
    public static final IElementType TYPE = new TeaStubElementType("TYPE");
    public static final IElementType ARGUMENT_LIST = new TeaStubElementType("ARGUMENT_LIST");
    public static final IElementType SUBSTITUTION_PARAMETER = new TeaStubElementType("SUBSTITUTION_PARAMETER");
    public static final IElementType EMBEDDED_SCRIPT = new TeaStubElementType("EMBEDDED_SCRIPT");
    public static final IElementType PLAIN_TEXT = new TeaStubElementType("PLAIN_TEXT");

    // Statements
    public static final IElementType BLOCK = new TeaStubElementType("BLOCK");
    public static final IElementType EXPRESSION_STATEMENT = new TeaStubElementType("EXPRESSION_STATEMENT");
    public static final IElementType DEFINE_STATEMENT = new TeaStubElementType("DEFINE_STATEMENT");
    public static final IElementType EMPTY_STATEMENT = new TeaStubElementType("EMPTY_STATEMENT");
    public static final IElementType IF_STATEMENT = new TeaStubElementType("IF_STATEMENT");
    public static final IElementType BREAK_STATEMENT = new TeaStubElementType("BREAK_STATEMENT");
    public static final IElementType FOREACH_STATEMENT = new TeaStubElementType("FOREACH_STATEMENT");
    public static final IElementType CALL_STATEMENT = new TeaStubElementType("CALL_STATEMENT");
    public static final IElementType SUBSTITUTION_STATEMENT = new TeaStubElementType("SUBSTITUTION_STATEMENT");
    public static final IElementType ASSIGNMENT_STATEMENT = new TeaStubElementType("ASSIGNMENT_STATEMENT");

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
    public static final IElementType OR_EXPRESSION = new TeaStubElementType("OR_EXPRESSION");
    public static final IElementType AND_EXPRESSION = new TeaStubElementType("AND_EXPRESSION");
    public static final IElementType EQUALITY_EXPRESSION = new TeaStubElementType("EQUALITY_EXPRESSION");
    public static final IElementType RELATIONAL_EXPRESSION = new TeaStubElementType("RELATIONAL_EXPRESSION");
    public static final IElementType CONCATENATE_EXPRESSION = new TeaStubElementType("CONCATENATE_EXPRESSION");
//    public static final IElementType LOOKUP_EXPRESSION = new TeaStubElementType("LOOKUP_EXPRESSION");
    public static final IElementType LOOKUP = new TeaStubElementType("LOOKUP");
    public static final IElementType FACTOR = new TeaStubElementType("FACTOR");
    public static final IElementType ARRAY_LITERAL_EXPRESSION = new TeaStubElementType("ARRAY_LITERAL_EXPRESSION");
    public static final IElementType FUNCTION_CALL_EXPRESSION = new TeaStubElementType("FUNCTION_CALL_EXPRESSION");
    public static final IElementType TEMPLATE_CALL_EXPRESSION = new TeaStubElementType("TEMPLATE_CALL_EXPRESSION");
    public static final IElementType BINARY_EXPRESSION = new TeaStubElementType("BINARY_EXPRESSION");
    public static final IElementType PREFIX_EXPRESSION = new TeaStubElementType("PREFIX_EXPRESSION");
    public static final IElementType REFERENCE_EXPRESSION = new TeaStubElementType("REFERENCE_EXPRESSION");
    public static final IElementType INDEXED_PROPERTY_ACCESS_EXPRESSION = new TeaStubElementType("INDEXED_PROPERTY_ACCESS_EXPRESSION");
    public static final IElementType LITERAL_EXPRESSION = new TeaStubElementType("LITERAL_EXPRESSION");
    public static final IElementType PARENTHESIZED_EXPRESSION = new TeaStubElementType("PARENTHESIZED_EXPRESSION");
    public static final IElementType DEFINITION_EXPRESSION = new TeaStubElementType("DEFINITION_EXPRESSION");

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
