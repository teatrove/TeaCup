package com.go.teacup.intellij.lang.tea.parser.parsing;

import com.go.teacup.intellij.lang.tea.TeaBundle;
import com.go.teacup.intellij.lang.tea.lexer.TeaTokenTypes;
import com.go.teacup.intellij.lang.tea.parser.TeaElementTypes;
import com.intellij.lang.PsiBuilder;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 3:11:10 PM
 */
public class TemplateParsing {

    public static void parseTemplateDeclaration(final PsiBuilder builder) {
        final PsiBuilder.Marker templateMarker = builder.mark();
        if(builder.getTokenType() == TeaTokenTypes.TEMPLATE_KEYWORD) {
            builder.advanceLexer();
        }

        // Template name
        if(builder.getTokenType() == TeaTokenTypes.IDENTIFIER) {
            builder.advanceLexer();
        }
        else {
            builder.error(TeaBundle.message("tea.parser.message.expected.template.name"));
        }

        parseParameterList(builder);

        parseSubstitutionParameter(builder);

        StatementParsing.parseTemplateBody(builder);

        templateMarker.done(TeaElementTypes.TEMPLATE_DECLARATION);
    }

    private static void parseSubstitutionParameter(PsiBuilder builder) {
        final PsiBuilder.Marker subParameter = builder.mark();
        if(builder.getTokenType() != TeaTokenTypes.LBRACE) {
            subParameter.drop();
            return;
        }

        builder.advanceLexer();
        if(builder.getTokenType() != TeaTokenTypes.ELLIPSIS) {
            builder.error(TeaBundle.message("tea.parser.message.expected.ellipsis"));
            subParameter.drop();
            return;
        }

        builder.advanceLexer();

        if(builder.getTokenType() != TeaTokenTypes.RBRACE) {
            builder.error(TeaBundle.message("tea.parser.message.expected.rbrace"));
            subParameter.drop();
            return;
        }

        subParameter.done(TeaElementTypes.SUBSTITUTION_PARAMETER);
    }

    private static void parseParameterList(final PsiBuilder builder) {
        final PsiBuilder.Marker parameterList;
        if(builder.getTokenType() != TeaTokenTypes.LPAR) {
            builder.error(TeaBundle.message("tea.parser.message.expected.lparen"));
            parameterList = builder.mark(); // To have non-empty parameters list at all time.
            parameterList.done(TeaElementTypes.PARAMETER_LIST);
            return;
        }
        else {
            parameterList = builder.mark();
            builder.advanceLexer();
        }

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

            final PsiBuilder.Marker parameter = builder.mark();
            if(!ExpressionParsing.parseType(builder)) {
                builder.error(TeaBundle.message("tea.parser.message.expected.type"));
                parameter.rollbackTo();
            }

//            if(ParsingUtil.parseIdentifier(builder)) {
            if (builder.getTokenType() == TeaTokenTypes.IDENTIFIER) {
                builder.advanceLexer();
                parameter.done(TeaElementTypes.FORMAL_PARAMETER);
            } else {
                builder.error(TeaBundle.message("tea.parser.message.expected.formal.parameter.name"));
                parameter.rollbackTo();
            }
        }

        if(builder.getTokenType() == TeaTokenTypes.RPAR) {
            builder.advanceLexer();
        }

        parameterList.done(TeaElementTypes.PARAMETER_LIST);
    }

    public static void parseEmbeddedScript(PsiBuilder builder) {
        final PsiBuilder.Marker scriptToken = builder.mark();
        if(builder.getTokenType() == TeaTokenTypes.LSCRIPT) {
            builder.advanceLexer();
        }

        while(builder.getTokenType() != TeaTokenTypes.RSCRIPT) {
            if(builder.eof()) {
//                builder.error(TeaBundle.message("tea.parser.message.expected.rscript"));
                scriptToken.done(TeaElementTypes.EMBEDDED_SCRIPT);
                return;
            }

            StatementParsing.parseEmbeddedScriptBody(builder);
        }
        builder.advanceLexer();
        scriptToken.done(TeaElementTypes.EMBEDDED_SCRIPT);
    }

}
