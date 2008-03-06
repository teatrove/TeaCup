package com.go.teacup.intellij.lang.tea.parsing;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.openapi.diagnostic.Logger;
import com.go.teacup.intellij.lang.tea.TeaTokenTypes;
import com.go.teacup.intellij.lang.tea.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.TeaBundle;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 3:11:21 PM
 */
public class ExpressionParsing {
    private static final Logger LOG = Logger.getInstance("#"+ExpressionParsing.class.getName());

    private static boolean parseLiteral(PsiBuilder builder) {
        final IElementType firstToken = builder.getTokenType();
        if(firstToken == TeaTokenTypes.NUMERIC_LITERAL ||
                  firstToken == TeaTokenTypes.STRING_LITERAL ||
                  firstToken == TeaTokenTypes.NULL_KEYWORD ||
                  firstToken == TeaTokenTypes.FALSE_KEYWORD ||
                  firstToken == TeaTokenTypes.TRUE_KEYWORD)
        {
            String errorMessage = validateLiteral(builder);
            ParsingUtil.buildTokenElement(TeaElementTypes.LITERAL_EXPRESSION, builder);
            if(errorMessage != null) {
                builder.error(errorMessage);
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean parseIdentifier(PsiBuilder builder) {
        final IElementType firstToken = builder.getTokenType();
        if(firstToken == TeaTokenTypes.IDENTIFIER) {
            ParsingUtil.buildTokenElement(TeaElementTypes.REFERENCE_EXPRESSION, builder);
            return true;
        } else {
            return false;
        }
    }

    private static boolean parsePrimaryExpression(PsiBuilder builder) {
        final IElementType firstToken = builder.getTokenType();
        if(firstToken == TeaTokenTypes.IDENTIFIER) {
            ParsingUtil.buildTokenElement(TeaElementTypes.REFERENCE_EXPRESSION, builder);
            return true;
        } else if(firstToken == TeaTokenTypes.NUMERIC_LITERAL ||
                  firstToken == TeaTokenTypes.STRING_LITERAL ||
                  firstToken == TeaTokenTypes.NULL_KEYWORD ||
                  firstToken == TeaTokenTypes.FALSE_KEYWORD ||
                  firstToken == TeaTokenTypes.TRUE_KEYWORD)
        {
            String errorMessage = validateLiteral(builder);
            ParsingUtil.buildTokenElement(TeaElementTypes.LITERAL_EXPRESSION, builder);
            if(errorMessage != null) {
                builder.error(errorMessage);
            }
            return true;
        } else if( firstToken == TeaTokenTypes.LPAR) {
            parseParenthesizedExpression(builder);
            return true;
//        } else if(firstToken == TeaTokenTypes.LBRACKET) {
//            parseArrayLiteralExpression(builder);
//            return true;
//        } else if(firstToken == TeaTokenTypes.LBRACE) {
//            parseObjectLiteralExpression(builder);
//            return true;
//        } else if(firstToken == TeaTokenTypes.FUNCTION_KEYWORD) {
//            FunctionParsing.parseFunctionExpression(builder);
//            return true;
        } else {
            return false;
        }
    }

    private static String validateLiteral(PsiBuilder builder) {
        final IElementType ttype = builder.getTokenType();
        if(ttype == TeaTokenTypes.STRING_LITERAL) {
            final String ttext = builder.getTokenText();
            assert ttext != null;

            if(lastSymbolEscaped(ttext) ||
                    ttext.startsWith("\"") && (!ttext.endsWith("\"") || ttext.length() == 1 ) ||
                    ttext.startsWith("\'") && (!ttext.endsWith("\'") || ttext.length() == 1))
            {
                return TeaBundle.message("tea.parser.message.unclosed.string.literal");
            }
        }

        return null;
    }

    private static boolean lastSymbolEscaped(String text) {
        boolean escapes = false;
        boolean escaped = true;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if(escapes) {
                escapes = false;
                escaped = true;
                continue;
            }
            if(c == '\\') {
                escapes = true;
            }
            escaped = false;
        }
        return escapes || escaped;
    }

    public static boolean parseAssignmentExpressionOptional(PsiBuilder builder) {
        PsiBuilder.Marker expr = builder.mark();
        if(!parseAssignmentExpression(builder)) {
            expr.drop();
            return false;
        }

        expr.drop();

        return true;
    }

    public static boolean parseExpressionOptional(PsiBuilder builder) {
        PsiBuilder.Marker expr = builder.mark();
        if(!parseORExpression(builder)) {
            expr.drop();
            return false;
        }

        expr.drop();

        return true;
    }

    private static boolean parseParenthesizedExpression(PsiBuilder builder) {
//        LOG.assertTrue(builder.getTokenType() == TeaTokenTypes.LPAR);
        final PsiBuilder.Marker expr = builder.mark();
        if (builder.getTokenType() != TeaTokenTypes.LPAR) {
            expr.drop();
            return false;
        }

        builder.advanceLexer();
        parseExpression(builder);
        ParsingUtil.checkMatches(builder, TeaTokenTypes.RPAR, TeaBundle.message("tea.parser.message.expected.rparen"));
        expr.done(TeaElementTypes.PARENTHESIZED_EXPRESSION);
        return true;

    }


    public static boolean parseAssignmentExpression(PsiBuilder builder) {
        if (!parseORExpression(builder)) {
          return false;
        }

        
        return true;

//        if(!ParsingUtil.parseIdentifier(builder)) {
////        if(!parseORExpression(builder)) {
//            definitionExpr.drop();
//            expr.drop();
//            return false;
//        }

//        if(TeaTokenTypes.ASSIGNMENT_OPERATIONS.contains(builder.getTokenType())) {
//            definitionExpr.done(TeaElementTypes.DEFINITION_EXPRESSION);
//            builder.advanceLexer();
//            if(!parseORExpression(builder)) { // Tea does not allow nested assignment
//                builder.error(TeaBundle.message("tea.parser.message.expected.expression"));
//            }
//
//            if(builder.getTokenType() == TeaTokenTypes.AS_KEYWORD) {
//                builder.advanceLexer();
//
//                if(!parseType(builder)) {
////                if(builder.getTokenType() == TeaTokenTypes.IDENTIFIER ||
////                   builder.getTokenType() == TeaTokenTypes.FQN)
////                {
////                    builder.advanceLexer();
////                } else {
//                    builder.error(TeaBundle.message("tea.parser.message.expected.type"));
//                }
//            }
//
//            expr.done(TeaElementTypes.ASSIGNMENT_STATEMENT);
//        } else {
//            definitionExpr.drop();
//            expr.drop();
//        }
//        return true;
    }

    private static boolean parseORExpression(PsiBuilder builder) {
        PsiBuilder.Marker expr = builder.mark();
        if(!parseANDExpression(builder)) {
            expr.drop();
            return false;
        }

        while(builder.getTokenType() == TeaTokenTypes.OR_KEYWORD) {
            builder.advanceLexer();
            if(!parseANDExpression(builder)) {
                builder.error(TeaBundle.message("tea.parser.message.expected.expression"));
            }
            expr.done(TeaElementTypes.BINARY_EXPRESSION);
            expr = expr.precede();
        }

        expr.drop();
        return true;
    }

    private static boolean parseANDExpression(PsiBuilder builder) {
        PsiBuilder.Marker expr = builder.mark();
        if(!parseEqualityExpression(builder)) {
            expr.drop();
            return false;
        }

        while(builder.getTokenType() == TeaTokenTypes.AND_KEYWORD) {
            builder.advanceLexer();
            if(!parseEqualityExpression(builder)) {
                builder.error(TeaBundle.message("tea.parser.message.expected.expression"));
            }
            expr.done(TeaElementTypes.BINARY_EXPRESSION);
            expr = expr.precede();
        }

        expr.drop();
        return true;
    }

    private static boolean parseEqualityExpression(PsiBuilder builder) {
        PsiBuilder.Marker expr = builder.mark();
        if(!parseRelationalExpression(builder)) {
            expr.drop();
            return false;
        }

        while(TeaTokenTypes.EQUALITY_OPERATIONS.contains(builder.getTokenType())) {
            builder.advanceLexer();
            if(!parseRelationalExpression(builder)) {
                builder.error(TeaBundle.message("tea.parser.message.expected.expression"));
            }
            expr.done(TeaElementTypes.BINARY_EXPRESSION);
            expr = expr.precede();
        }

        expr.drop();
        return true;
    }

    private static boolean parseRelationalExpression(PsiBuilder builder) {
        PsiBuilder.Marker expr = builder.mark();
        if(!parseConcatenateExpression(builder)) {
            expr.drop();
            return false;
        }

        while(TeaTokenTypes.RELATIONAL_OPERATIONS.contains(builder.getTokenType())) {
            boolean isa = builder.getTokenType() == TeaTokenTypes.ISA;

            builder.advanceLexer();
            
            if(isa && !parseType(builder)) {
                builder.error(TeaBundle.message("tea.parser.message.expected.type"));
            } else if(!isa && !parseConcatenateExpression(builder)) {
                builder.error(TeaBundle.message("tea.parser.message.expected.expression"));
            }
            expr.done(TeaElementTypes.BINARY_EXPRESSION);
            expr = expr.precede();
        }

        expr.drop();
        return true;
    }

    private static boolean parseConcatenateExpression(PsiBuilder builder) {
        PsiBuilder.Marker expr = builder.mark();
        if(!parseAdditiveExpression(builder)) {
            expr.drop();
            return false;
        }

        while(builder.getTokenType() == TeaTokenTypes.AND) {
            builder.advanceLexer();
            if(!parseAdditiveExpression(builder)) {
                builder.error(TeaBundle.message("tea.parser.message.expected.expression"));
            }
            expr.done(TeaElementTypes.BINARY_EXPRESSION);
            expr = expr.precede();
        }

        expr.drop();
        return true;
    }

    private static boolean parseAdditiveExpression(PsiBuilder builder) {
        PsiBuilder.Marker expr = builder.mark();
        if(!parseMultiplicativeExpression(builder)) {
            expr.drop();
            return false;
        }

        while(TeaTokenTypes.ADDITIVE_OPERATIONS.contains(builder.getTokenType())) {
            builder.advanceLexer();
            if(!parseMultiplicativeExpression(builder)) {
                builder.error(TeaBundle.message("tea.parser.message.expected.expression"));
            }
            expr.done(TeaElementTypes.BINARY_EXPRESSION);
            expr = expr.precede();
        }

        expr.drop();
        return true;
    }

    private static boolean parseMultiplicativeExpression(PsiBuilder builder) {
        PsiBuilder.Marker expr = builder.mark();
        if(!parseUnaryExpression(builder)) {
            expr.drop();
            return false;
        }

        while(TeaTokenTypes.MULTIPLICATIVE_OPERATIONS.contains(builder.getTokenType())) {
            builder.advanceLexer();
            if(!parseUnaryExpression(builder)) {
                builder.error(TeaBundle.message("tea.parser.message.expected.expression"));
            }
            expr.done(TeaElementTypes.BINARY_EXPRESSION);
            expr = expr.precede();
        }

        expr.drop();
        return true;
    }

    private static boolean parseUnaryExpression(PsiBuilder builder) {
        final PsiBuilder.Marker expr = builder.mark();
//        if(!parseExpression(builder)) {
//            expr.drop();
//            return false;
//        }
        
        final IElementType tokenType = builder.getTokenType();
        if(TeaTokenTypes.UNARY_OPERATIONS.contains(tokenType)) {
            builder.advanceLexer();
            if(!parseUnaryExpression(builder)) {
                builder.error(TeaBundle.message("tea.parser.message.expected.expression"));
            }
            expr.done(TeaElementTypes.PREFIX_EXPRESSION);
        } else if(!parseLookupExpression(builder)) {
            expr.drop();
            return false;
        } else {
            expr.drop();
        }
        return true;
    }

    public static boolean parseLookupExpression(PsiBuilder builder) {
        PsiBuilder.Marker expr = builder.mark();
        if(!parseFactor(builder)) {
            expr.drop();
            return false;
        }

        // Lookup is optional
        while(true) {
            final IElementType tokenType = builder.getTokenType();

            // Member lookup
            if(tokenType == TeaTokenTypes.DOT) {
                builder.advanceLexer();
                ParsingUtil.checkMatches(builder, TeaTokenTypes.IDENTIFIER, TeaBundle.message("tea.parser.message.expected.name"));
                expr.done(TeaElementTypes.REFERENCE_EXPRESSION);
                expr = expr.precede();

            // Array lookup
            } else if(tokenType == TeaTokenTypes.LBRACKET) {
                builder.advanceLexer();
                parseExpression(builder);
                ParsingUtil.checkMatches(builder, TeaTokenTypes.RBRACKET, TeaBundle.message("tea.parser.message.expected.rbracket"));
                expr.done(TeaElementTypes.INDEXED_PROPERTY_ACCESS_EXPRESSION);
                expr = expr.precede();
            } else {
                expr.drop();
                break;
            }
        }

        return true;
    }

    private static boolean parseFactor(PsiBuilder builder) {
//        PsiBuilder.Marker expr = builder.mark();
        if(!parseNewArrayExpression(builder) &&
           !parseParenthesizedExpression(builder) &&
           !parseLiteral(builder) &&
           !parseCallExpression(builder) &&
           !parseIdentifier(builder)) {
//            expr.drop();
            return false;
        }

//        expr.done(TeaElementTypes.FACTOR);
        return true;
    }

    private static boolean parseNewArrayExpression(PsiBuilder builder) {
        PsiBuilder.Marker expr = builder.mark();
        if ( builder.getTokenType() != TeaTokenTypes.HASH && // new indexed array
             builder.getTokenType() != TeaTokenTypes.HASHHASH // new associative array
           )
        {
            expr.drop();
            return false;
        }

        builder.advanceLexer();

        if(builder.getTokenType() == TeaTokenTypes.LPAR) {
            parseArgumentList(builder);
        } else {
            expr.drop();
            return false;
        }

        expr.done(TeaElementTypes.ARRAY_LITERAL_EXPRESSION);
        return true;
    }

    public static boolean parseCallExpression(PsiBuilder builder) {
        PsiBuilder.Marker expr = builder.mark();
//        if(!parseMemberExpression(builder, true)) {
//            expr.drop();
//            return false;
//        }
        
        // call keyword is optional
        boolean templateCall = false;
        if(builder.getTokenType() == TeaTokenTypes.CALL_KEYWORD) {
            builder.advanceLexer();
            templateCall = true;
        }

        if(!parseName(builder)) {
            expr.drop();
            return false;
        }
//        if( builder.getTokenType() == TeaTokenTypes.IDENTIFIER ||
//            builder.getTokenType() == TeaTokenTypes.FQN) {
//
//            ParsingUtil.buildTokenElement(TeaElementTypes.REFERENCE_EXPRESSION, builder);
//        } else {
//            expr.drop();
//            return false;
//        }

        if(builder.getTokenType() == TeaTokenTypes.LPAR) {
            parseArgumentList(builder);
        } else if(templateCall) {
            builder.error(TeaBundle.message("tea.parser.message.expected.lparen"));
            expr.drop();
            return false;
        } else {
            expr.rollbackTo(); // Give parseIdentifier() a chance
            return false;
        }

        if(templateCall && builder.getTokenType() == TeaTokenTypes.LBRACE) {
            // The block is optional (depends on substitution param on template)
            StatementParsing.parseBlock(builder);
        }

        expr.done(templateCall ? TeaElementTypes.TEMPLATE_CALL_EXPRESSION : TeaElementTypes.FUNCTION_CALL_EXPRESSION);
        return true;
    }

    public static boolean parseName(PsiBuilder builder) {
        PsiBuilder.Marker expr = builder.mark();
        if(!ParsingUtil.parseIdentifier(builder)) {
            expr.drop();
            return false;
        }

        while(true) {
            final IElementType tokenType = builder.getTokenType();
            if(tokenType == TeaTokenTypes.DOT) {
                builder.advanceLexer();
                ParsingUtil.checkMatches(builder, TeaTokenTypes.IDENTIFIER, TeaBundle.message("tea.parser.message.expected.name"));
                expr.done(TeaElementTypes.REFERENCE_EXPRESSION);
                expr = expr.precede();
            } else {
                break;
            }
        }

        expr.done(TeaElementTypes.REFERENCE_EXPRESSION);

        return true;
    }

    public static boolean parseType(PsiBuilder builder) {
        PsiBuilder.Marker expr = builder.mark();
        if(!parseName(builder)) {
            expr.drop();
            return false;
        }

        if(builder.getTokenType() == TeaTokenTypes.LBRACKET) {
            builder.advanceLexer();

            if(builder.getTokenType() == TeaTokenTypes.RBRACKET) {
                builder.advanceLexer();
            } else {
                builder.error(TeaBundle.message("tea.parser.message.expected.rbracket"));
                expr.drop();
                return false;
            }
        }

        expr.done(TeaElementTypes.TYPE);
        return true;
    }

//    private static boolean parseMemberExpression(PsiBuilder builder, boolean allowCallSyntax) {
//        PsiBuilder.Marker expr = builder.mark();
//        if(!parsePrimaryExpression(builder)) {
//            expr.drop();
//            return false;
//        }
//
//        while(true) {
//            final IElementType tokenType = builder.getTokenType();
//            if(tokenType == TeaTokenTypes.DOT) {
//                builder.advanceLexer();
//                checkMatches(builder, TeaTokenTypes.IDENTIFIER, TeaBundle.message("tea.parser.message.expected.name"));
//                expr.done(TeaElementTypes.REFERENCE_EXPRESSION);
//                expr = expr.precede();
//            } else if(tokenType == TeaTokenTypes.LBRACKET) {
//                builder.advanceLexer();
//                parseExpression(builder);
//                checkMatches(builder, TeaTokenTypes.RBRACKET, TeaBundle.message("tea.parser.message.expected.rbracket"));
//                expr.done(TeaElementTypes.INDEXED_PROPERTY_ACCESS_EXPRESSION);
//                expr = expr.precede();
//            } else if(allowCallSyntax && tokenType == TeaTokenTypes.LPAR) {
//                parseArgumentList(builder);
//                expr.done(TeaElementTypes.FUNCTION_CALL_EXPRESSION);
//                expr = expr.precede();
//            } else {
//                expr.drop();
//                break;
//            }
//        }
//
//        return true;
//    }

    private static void parseArgumentList(PsiBuilder builder) {
        LOG.assertTrue(builder.getTokenType() == TeaTokenTypes.LPAR);
        final PsiBuilder.Marker arglist = builder.mark();
        builder.advanceLexer();
        boolean first = true;
        while(builder.getTokenType() != TeaTokenTypes.RPAR) {
            if(first) {
                first = false;
            } else {
                if(builder.getTokenType() == TeaTokenTypes.COMMA) {
                    builder.advanceLexer();
                } else {
                    builder.error(TeaBundle.message("tea.parser.message.expected.comma.or.rparen"));
                    break;
                }
            }
            if(!parseORExpression(builder)) { // Assignment is not allowed in Tea arg lists
                builder.error(TeaBundle.message("tea.parser.message.expected.expression"));
            }
        }

        ParsingUtil.checkMatches(builder, TeaTokenTypes.RPAR, TeaBundle.message("tea.parser.message.expected.rparen"));
        arglist.done(TeaElementTypes.ARGUMENT_LIST);
    }

    public static void parseExpression(PsiBuilder builder) {
        if(!parseExpressionOptional(builder)) {
            builder.error(TeaBundle.message("tea.parser.message.expected.expression"));
        }
    }
    
}
