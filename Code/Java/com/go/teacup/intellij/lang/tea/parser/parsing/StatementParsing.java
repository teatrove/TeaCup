package com.go.teacup.intellij.lang.tea.parser.parsing;

import com.go.teacup.intellij.lang.tea.TeaBundle;
import com.go.teacup.intellij.lang.tea.lexer.TeaTokenTypes;
import com.go.teacup.intellij.lang.tea.parser.TeaElementTypes;
import com.intellij.lang.PsiBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 2:32:42 PM
 */
public class StatementParsing {
    private static final Logger LOG = Logger.getInstance("#"+StatementParsing.class.getName());
    private StatementParsing() {}

    public static void parseSourceElement(PsiBuilder builder) {
        if(builder.getTokenType() == TeaTokenTypes.LSCRIPT) {
            TemplateParsing.parseEmbeddedScript(builder);
        }
        else if(builder.getTokenType() == TeaTokenTypes.PLAIN_TEXT) {
            final PsiBuilder.Marker plainText = builder.mark();
            builder.advanceLexer();
            plainText.done(TeaElementTypes.PLAIN_TEXT);
        }
    }

    public static void parseEmbeddedScriptBody(final PsiBuilder builder) {
        if(builder.getTokenType() == TeaTokenTypes.TEMPLATE_KEYWORD) {
            TemplateParsing.parseTemplateDeclaration(builder);
        }
        else if(builder.getTokenType() == TeaTokenTypes.IMPORT_KEYWORD) {
            StatementParsing.parseImportStatement(builder);
        }
        else {
            parseStatement(builder);
        }
    }

    private static void parseStatement(PsiBuilder builder) {
        final IElementType firstToken = builder.getTokenType();

        if(firstToken == null) return;

        if(firstToken == TeaTokenTypes.LBRACE) {
            parseBlock(builder);
            return;
        }

        if(firstToken == TeaTokenTypes.DEFINE_KEYWORD) {
            parseDefineStatement(builder);
            return;
        }

        if(firstToken == TeaTokenTypes.SEMICOLON) {
            parseEmptyStatement(builder);
            return;
        }

//        if(firstToken == TeaTokenTypes.CALL_KEYWORD) {
//            parseCallStatement(builder);
//            return;
//        }

        if(firstToken == TeaTokenTypes.IF_KEYWORD) {
            parseIfStatement(builder);
            return;
        }

        if(firstToken == TeaTokenTypes.FOREACH_KEYWORD) {
            parseIterationStatement(builder);
            return;
        }

        if(firstToken == TeaTokenTypes.ELLIPSIS) {
            parseSubstitutionStatement(builder);
            return;
        }
        
//        if(firstToken == TeaTokenTypes.IDENTIFIER) {
//        }

        if(firstToken == TeaTokenTypes.CALL_KEYWORD) {
            parseCallStatement(builder, false);
            return;
        }

        if(firstToken == TeaTokenTypes.BREAK_KEYWORD) {
            parseBreakStatement(builder);
            return;
        }

        if(firstToken == TeaTokenTypes.RSCRIPT) {
            parsePlainText(builder);
            return;
        }

//        if(firstToken == TeaTokenTypes.TEMPLATE_KEYWORD) {
//            TemplateParsing.parseTemplateDeclaration(builder);
//            return;
//        }

        if(firstToken != TeaTokenTypes.LBRACE && firstToken != TeaTokenTypes.TEMPLATE_KEYWORD) {
            // This is backwards from grammar, but necessary because parseCallExpression
            // needs to rollback due to logic in parseFactor
            if(parseCallStatement(builder, true) ||
               parseAssignmentStatement(builder)) {
                checkForSemicolon(builder);
                return;
            } else {
                // Try expression statement
                final PsiBuilder.Marker exprStatement = builder.mark();
                if(ExpressionParsing.parseExpressionOptional(builder)) {
                    checkForSemicolon(builder);
                    exprStatement.done(TeaElementTypes.EXPRESSION_STATEMENT);
                    return;
                }
                else {
                    exprStatement.drop();
                }
            }
        }

        builder.advanceLexer();
        builder.error("statement expected");
    }

    private static void parsePlainText(PsiBuilder builder) {
        LOG.assertTrue(builder.getTokenType() == TeaTokenTypes.RSCRIPT);
        // Dont include the %> in the plain text element.
        builder.advanceLexer();
        final PsiBuilder.Marker plainText = builder.mark();
        if(builder.getTokenType() != TeaTokenTypes.PLAIN_TEXT && !builder.eof()) {
            builder.error("plain text expected");
            plainText.drop();
            return;
        }

        builder.advanceLexer();
        if(builder.getTokenType() != TeaTokenTypes.LSCRIPT && !builder.eof()) {
            builder.error(TeaBundle.message("tea.parser.message.expected.lscript"));
            plainText.drop();
            return;
        }

        builder.advanceLexer();
        plainText.done(TeaElementTypes.PLAIN_TEXT);
    }

    private static void parseImportStatement(PsiBuilder builder) {
        final IElementType declType = builder.getTokenType();
        LOG.assertTrue(declType == TeaTokenTypes.IMPORT_KEYWORD);
        final PsiBuilder.Marker define = builder.mark();
        builder.advanceLexer();
        ExpressionParsing.parseName(builder);
        checkForSemicolon(builder);
        define.done(TeaElementTypes.IMPORT_STATEMENT);
    }


    private static boolean parseCallStatement(PsiBuilder builder, boolean tryAgain) {
        final PsiBuilder.Marker callStatement = builder.mark();
        if(ExpressionParsing.parseCallExpression(builder)) {
            checkForSemicolon(builder);
            callStatement.done(TeaElementTypes.CALL_STATEMENT);
            return true;
        }
        else {
            if(tryAgain) {
                callStatement.rollbackTo(); // give parseAssignmentStatement a chance
            } else {
                callStatement.drop();
            }
            return false;
        }
    }

    private static boolean parseAssignmentStatement(PsiBuilder builder) {
//        if (builder.getTokenType() != TeaTokenTypes.IDENTIFIER) {
//          builder.error(TeaBundle.message("tea.parser.message.expected.variable.name"));
//          builder.advanceLexer();
//          return false;
//        }
        final PsiBuilder.Marker assignmentStatement = builder.mark();
        final PsiBuilder.Marker var = builder.mark();
        builder.advanceLexer();
        if (builder.getTokenType() == TeaTokenTypes.EQ) {
          builder.advanceLexer();
            if (!ExpressionParsing.parseAssignmentExpression(builder)) {
              builder.error(TeaBundle.message("tea.parser.message.expected.expression"));
            }
        } else {
            var.rollbackTo(); // give expression parsing a chance
            assignmentStatement.rollbackTo(); // give expression parsing a chance
            return false;
        }

        if(builder.getTokenType() == TeaTokenTypes.AS_KEYWORD) {
            builder.advanceLexer();
            if(!ExpressionParsing.parseType(builder)) {
                builder.error(TeaBundle.message("tea.parser.message.expected.type"));
            }
        }

        var.done(TeaElementTypes.VARIABLE);
        assignmentStatement.done(TeaElementTypes.ASSIGNMENT_STATEMENT);
        return true;

//        final PsiBuilder.Marker assignStatement = builder.mark();
//        if(ExpressionParsing.parseAssignmentExpressionOptional(builder)) {
//            checkForSemicolon(builder);
////            assignStatement.done(TeaElementTypes.ASSIGNMENT_STATEMENT);
//            assignStatement.drop();
//            return true;
//        }
//        else {
//            assignStatement.rollbackTo(); // Allow expression parsing a try
//            return false;
//        }
    }

//    private static void parseCallStatement(PsiBuilder builder) {
//        final IElementType callType = builder.getTokenType();
//        LOG.assertTrue(callType == TeaTokenTypes.CALL_KEYWORD);
//        final PsiBuilder.Marker call = builder.mark();
//        builder.advanceLexer();
//        if (!ExpressionParsing.parseMemberExpression(builder, true)) {
//            builder.error("expression expected");
//        }
//
//        checkForSemicolon(builder);
//        call.done(TeaElementTypes.CALL_STATEMENT);
//    }

    private static void parseBreakStatement(PsiBuilder builder) {
        LOG.assertTrue(builder.getTokenType() == TeaTokenTypes.BREAK_KEYWORD);
        final PsiBuilder.Marker statement = builder.mark();
        builder.advanceLexer();

        // Labels not supported

        checkForSemicolon(builder);
        statement.done(TeaElementTypes.BREAK_STATEMENT);
    }

    private static void parseIterationStatement(PsiBuilder builder) {
        final IElementType tokenType = builder.getTokenType();
        if(tokenType == TeaTokenTypes.FOREACH_KEYWORD) {
            parseForeachStatement(builder);
        } else {
            LOG.error("Unknown iteration statement");
        }
    }

    private static void parseForeachStatement(PsiBuilder builder) {
        LOG.assertTrue(builder.getTokenType() == TeaTokenTypes.FOREACH_KEYWORD);
        final PsiBuilder.Marker foreachStatement = builder.mark();
        builder.advanceLexer();
        ParsingUtil.checkMatches(builder, TeaTokenTypes.LPAR, "( expected");
        if (builder.getTokenType() != TeaTokenTypes.IDENTIFIER) {
            builder.error(TeaBundle.message("tea.parser.message.expected.variable.name"));
            builder.advanceLexer();
        } else {
            final PsiBuilder.Marker var = builder.mark();
            builder.advanceLexer();
            var.done(TeaElementTypes.VARIABLE);
        }
//        final PsiBuilder.Marker variable = builder.mark();
////        final PsiBuilder.Marker definitionExpression = builder.mark();
//        if(!ParsingUtil.parseIdentifier(builder)) {
//            builder.error("variable expected");
////            definitionExpression.drop();
//            variable.drop();
//        } else {
////            definitionExpression.done(TeaElementTypes.DEFINITION_EXPRESSION);
//            variable.done(TeaElementTypes.VARIABLE);
//        }


        if(builder.getTokenType() == TeaTokenTypes.IN_KEYWORD) {
            builder.advanceLexer();
        } else {
            builder.error(TeaBundle.message("tea.parser.message.expected.forloop.in"));
        }
        ExpressionParsing.parseExpression(builder);
        if(builder.getTokenType() == TeaTokenTypes.DOTDOT) {
            builder.advanceLexer();
        }
        ExpressionParsing.parseExpressionOptional(builder);

        if(builder.getTokenType() == TeaTokenTypes.REVERSE_KEYWORD) {
            builder.advanceLexer();
        }
        ParsingUtil.checkMatches(builder, TeaTokenTypes.RPAR, ") expected");

        parseStatement(builder);
        
        foreachStatement.done(TeaElementTypes.FOREACH_STATEMENT);
    }

    private static void parseSubstitutionStatement(PsiBuilder builder) {
        LOG.assertTrue(builder.getTokenType() == TeaTokenTypes.ELLIPSIS);
        final PsiBuilder.Marker substitutionStatement = builder.mark();
        builder.advanceLexer();
        substitutionStatement.done(TeaElementTypes.SUBSTITUTION_STATEMENT);
    }

    private static void parseIfStatement(PsiBuilder builder) {
        LOG.assertTrue(builder.getTokenType() == TeaTokenTypes.IF_KEYWORD);
        final PsiBuilder.Marker ifStatement = builder.mark();
        builder.advanceLexer();

        ParsingUtil.checkMatches(builder, TeaTokenTypes.LPAR, "( expected");
        ExpressionParsing.parseExpression(builder);

        // handle empty expression inside
//        while(builder.getTokenType() == TeaTokenTypes.OR_KEYWORD || builder.getTokenType() == TeaTokenTypes.EQEQ) {
//            builder.advanceLexer();
//        }

        ParsingUtil.checkMatches(builder, TeaTokenTypes.RPAR, ") expected");

        parseStatement(builder);

        if(builder.getTokenType() == TeaTokenTypes.ELSE_KEYWORD) {
            builder.advanceLexer();
            parseStatement(builder);
        }

        ifStatement.done(TeaElementTypes.IF_STATEMENT);
    }

    private static void parseEmptyStatement(PsiBuilder builder) {
        LOG.assertTrue(builder.getTokenType() == TeaTokenTypes.SEMICOLON);
        final PsiBuilder.Marker statement = builder.mark();
        builder.advanceLexer();
        statement.done(TeaElementTypes.EMPTY_STATEMENT);
    }

    private static void parseDefineStatement(PsiBuilder builder) {
        final IElementType declType = builder.getTokenType();
        LOG.assertTrue(declType == TeaTokenTypes.DEFINE_KEYWORD);
        final PsiBuilder.Marker define = builder.mark();
        builder.advanceLexer();
        parseVarDeclaration(builder);
        checkForSemicolon(builder);
        define.done(TeaElementTypes.DEFINE_STATEMENT);
    }

    private static void checkForSemicolon(PsiBuilder builder) {
        if(builder.getTokenType() == TeaTokenTypes.SEMICOLON) {
            builder.advanceLexer();
        }
    }

    private static void parseVarDeclaration(PsiBuilder builder) {
//        final PsiBuilder.Marker typeDecl = builder.mark();
        if(!ExpressionParsing.parseType(builder)) { 
            builder.error(TeaBundle.message("tea.parser.message.expected.type"));
//            typeDecl.drop();
            return;
        }

        if (builder.getTokenType() != TeaTokenTypes.IDENTIFIER) {
          builder.error(TeaBundle.message("tea.parser.message.expected.variable.name"));
          builder.advanceLexer();
          return;
        }

        final PsiBuilder.Marker variable = builder.mark();
//        final PsiBuilder.Marker assignment = builder.mark();
        builder.advanceLexer();
//        if(!ExpressionParsing.parseIdentifier(builder)) {
//            builder.error(TeaBundle.message("tea.parser.message.expected.name"));
//            variable.drop();
////            assignment.drop();
////            typeDecl.drop();
//            return;
//        }

        if(builder.getTokenType() == TeaTokenTypes.EQ) {
            builder.advanceLexer();
//            assignment.rollbackTo();
            if(!ExpressionParsing.parseAssignmentExpression(builder)) {
                builder.error(TeaBundle.message("tea.parser.message.expected.expression"));
            }
//        } else {
//            assignment.drop();
        }
        variable.done(TeaElementTypes.VARIABLE);
//        typeDecl.done(TeaElementTypes.TYPE_DECLARATION);
    }

    
    public static void parseBlock(PsiBuilder builder) {
        parseBlockOrTemplateBody(builder, false);
    }

    public static void parseTemplateBody(PsiBuilder builder) {
        parseBlockOrTemplateBody(builder, true);
    }

    private static void parseBlockOrTemplateBody(PsiBuilder builder, boolean templateBody) {
        if(!templateBody && builder.getTokenType() != TeaTokenTypes.LBRACE) {
            builder.error("{ expected");
            return;
        }

        final PsiBuilder.Marker block = builder.mark();
        if(builder.getTokenType() == TeaTokenTypes.LBRACE) {
            builder.advanceLexer();
        }
        while(builder.getTokenType() != TeaTokenTypes.RBRACE) {
            if(builder.eof()) {
                if(!templateBody) {
                    builder.error("missing }");
                }
                block.done(TeaElementTypes.BLOCK);
                return;
            }

            parseStatement(builder);
        }

        builder.advanceLexer();
        block.done(TeaElementTypes.BLOCK);
    }
}
