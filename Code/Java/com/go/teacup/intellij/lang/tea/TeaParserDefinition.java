package com.go.teacup.intellij.lang.tea;

import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageUtil;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.go.teacup.intellij.lang.tea.psi.factory.SimplePsiElementFactory;
import com.go.teacup.intellij.lang.tea.psi.TeaFile;
import com.go.teacup.intellij.lang.tea.psi.impl.*;
import com.go.teacup.intellij.lang.tea.parsing.TeaParser;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 11:06:58 AM
 */
public class TeaParserDefinition implements ParserDefinition {
    private static final Logger LOG = Logger.getInstance("#"+TeaParserDefinition.class.getName());

    SimplePsiElementFactory factory;

    @NotNull
    public Lexer createLexer(Project project) {
        return new TeaParsingLexer();
    }

    public PsiParser createParser(Project project) {
        return new TeaParser();
    }

    public IFileElementType getFileNodeType() {
        return TeaElementTypes.FILE;
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return TokenSet.create(TeaTokenTypes.WHITE_SPACE, TeaTokenTypes.SCRIPT_WHITE_SPACE);
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return TeaTokenTypes.COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return  TokenSet.create(TeaTokenTypes.STRING_LITERAL, TeaTokenTypes.SINGLE_QUOTE_STRING_LITERAL);
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        final IElementType type = node.getElementType();
        if (type == TeaElementTypes.TEMPLATE_DECLARATION) {
          return new TeaTemplateImpl(node);
        }
        else if (type == TeaElementTypes.PARAMETER_LIST) {
          return new TeaParameterListImpl(node);
        }
        else if (type == TeaElementTypes.VARIABLE) {
          return new TeaVariableImpl(node);
        }
        else if(type == TeaElementTypes.TYPE) {
            return new TeaTypeImpl(node);
        }
        else if (type == TeaElementTypes.FORMAL_PARAMETER) {
          return new TeaParameterImpl(node);
        }
        else if (type == TeaElementTypes.ARGUMENT_LIST) {
          return new TeaArgumentListImpl(node);
        }
        else if (type == TeaElementTypes.BLOCK) {
          return new TeaBlockStatementImpl(node);
        }
        else if (type == TeaElementTypes.EXPRESSION_STATEMENT) {
          return new TeaExpressionStatementImpl(node);
        }
        else if(type == TeaElementTypes.DEFINE_STATEMENT) {
            return new TeaDefineStatementImpl(node);
        }
        else if (type == TeaElementTypes.EMPTY_STATEMENT) {
          return new TeaEmptyStatementImpl(node);
        }
        else if (type == TeaElementTypes.IF_STATEMENT) {
          return new TeaIfStatementImpl(node);
        }
        else if (type == TeaElementTypes.BREAK_STATEMENT) {
          return new TeaBreakStatementImpl(node);
        }
        else if (type == TeaElementTypes.FOREACH_STATEMENT) {
          return new TeaForEachStatementImpl(node);
        }
        else if (type == TeaElementTypes.LITERAL_EXPRESSION) {
          return new TeaLiteralExpressionImpl(node);
        }
        else if (type == TeaElementTypes.REFERENCE_EXPRESSION) {
          return new TeaReferenceExpressionImpl(node);
        }
        else if (type == TeaElementTypes.DEFINITION_EXPRESSION) {
          return new TeaDefinitionExpressionImpl(node);
        }
        else if (type == TeaElementTypes.PARENTHESIZED_EXPRESSION) {
          return new TeaParenthesizedExpressionImpl(node);
        }
        else if (type == TeaElementTypes.ARRAY_LITERAL_EXPRESSION) {
          return new TeaArrayLiteralExpressionImpl(node);
        }
//        else if (type == TeaElementTypes.MAP_LITERAL_EXPRESSION) {
//          return new TeaMapLiteralExpressionImpl(node);
//        }
//        else if (type == TeaElementTypes.PROPERTY) {
//          return new TeaPropertyImpl(node);
//        }
        else if (type == TeaElementTypes.BINARY_EXPRESSION) {
          return new TeaBinaryExpressionImpl(node);
        }
//        else if (type == TeaElementTypes.LOOKUP_EXPRESSION) {
//          return new TeaLookupExpressionImpl(node);
//        }
//        else if (type == TeaElementTypes.LOOKUP) {
//          return new TeaLookupImpl(node);
//        }
//        else if (type == TeaElementTypes.FACTOR) {
//          return new TeaFactorImpl(node);
//        }
        else if (type == TeaElementTypes.ASSIGNMENT_STATEMENT) {
          return new TeaAssignmentStatementImpl(node);
        }
        else if (type == TeaElementTypes.PREFIX_EXPRESSION) {
          return new TeaPrefixExpressionImpl(node);
        }
        else if (type == TeaElementTypes.INDEXED_PROPERTY_ACCESS_EXPRESSION) {
          return new TeaIndexedPropertyAccessExpressionImpl(node);
        }
        else if (type == TeaElementTypes.FUNCTION_CALL_EXPRESSION) {
          return new TeaCallExpressionImpl(node);
        }
        else if (type == TeaElementTypes.TEMPLATE_CALL_EXPRESSION) {
          return new TeaTemplateCallExpressionImpl(node);
        }
        else if(type == TeaElementTypes.PLAIN_TEXT) {
            return new TeaPlainTextImpl(type, node.getChars());
        }
        else if (type == TeaElementTypes.SUBSTITUTION_STATEMENT) {
          return new TeaSubstitutionStatementImpl(node);
        }

        //TODO: create PsiElement instances here!!
        return PsiUtil.NULL_PSI_ELEMENT;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new TeaFile(viewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        final Lexer lexer = createLexer(left.getPsi().getProject());
        return LanguageUtil.canStickTokensTogetherByLexer(left, right, lexer);
    }
}
