package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.TeaSupportLoader;
import com.go.teacup.intellij.lang.tea.psi.*;
import com.go.teacup.intellij.lang.tea.psi.util.TeaUtils;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 4:38:00 PM
 */
public class TeaChangeUtil {
    @NonNls
    private static final String DUMMY = "dummy.";

    private TeaChangeUtil() {}

    public static PsiFileFactory getPsiFileFactory(Project project) {
        return PsiFileFactory.getInstance(PsiManager.getInstance(project).getProject());
    }

    public static ASTNode createNameIdentifier(Project project, String name) {
      final PsiFile dummyFile = getPsiFileFactory(project).createFileFromText(DUMMY + TeaSupportLoader.TEA.getDefaultExtension(), "<%"+name + "%>");
      final TeaExpressionStatement expressionStatement = (TeaExpressionStatement)dummyFile.getFirstChild().getChildren()[0];
      final TeaReferenceExpressionImpl refExpression = (TeaReferenceExpressionImpl)expressionStatement.getFirstChild();

      return refExpression.getNode().getFirstChildNode();
    }

    public static ASTNode createExpressionFromText(Project project, @NonNls String text) {
      ParserDefinition def = LanguageParserDefinitions.INSTANCE.forLanguage(TeaSupportLoader.TEA.getLanguage());
      assert def != null;
      final PsiFile dummyFile = getPsiFileFactory(project).createFileFromText(DUMMY + TeaSupportLoader.TEA.getDefaultExtension(), "<%"+text+"%>");
      final TeaExpressionStatement expressionStatement = (TeaExpressionStatement) dummyFile.getFirstChild().getChildren()[0];
      final TeaExpression expr = (TeaExpression) expressionStatement.getFirstChild();
      return expr.getNode();
    }

    public static ASTNode createStatementFromText(Project project, @NonNls String text) {
        ParserDefinition def = LanguageParserDefinitions.INSTANCE.forLanguage(TeaSupportLoader.TEA.getLanguage());
      assert def != null;
      final PsiFile dummyFile = getPsiFileFactory(project).createFileFromText(DUMMY + TeaSupportLoader.TEA.getDefaultExtension(), "<%"+text+"%>");
      final TeaSourceElement stmt = (TeaSourceElement) dummyFile.getFirstChild().getChildren()[0];
      return stmt.getNode();
    }

    public static ASTNode createTeaTreeFromText(Project project, @NonNls String text) {
        ParserDefinition def = LanguageParserDefinitions.INSTANCE.forLanguage(TeaSupportLoader.TEA.getLanguage());
      assert def != null;
      final PsiFile dummyFile = getPsiFileFactory(project).createFileFromText(DUMMY + TeaSupportLoader.TEA.getDefaultExtension(), "<%"+text+"%>");
      final PsiElement element = dummyFile.getFirstChild().getChildren()[0];
      if (element != null) return element.getNode();
      return null;
    }

    public static TeaExpression replaceExpression(TeaExpression oldExpr, TeaExpression newExpr) {
      if (TeaUtils.isNeedParenthesis(oldExpr, newExpr)) {
        ASTNode parenthesized = createExpressionFromText(oldExpr.getProject(), "(a)");
        final TeaParenthesizedExpression parenthPsi = (TeaParenthesizedExpression)parenthesized.getPsi();
        parenthesized.replaceChild(parenthPsi.getInnerExpression().getNode(), newExpr.getNode().copyElement());
        oldExpr.getParent().getNode().replaceChild(oldExpr.getNode(), parenthesized);
        return parenthPsi;
      } else {
        final ASTNode newNode = newExpr.getNode().copyElement();
        oldExpr.getParent().getNode().replaceChild(oldExpr.getNode(), newNode);
        return (TeaExpression)newNode.getPsi();
      }
    }

    public static TeaStatement replaceStatement(TeaStatement oldStatement, TeaStatement newStatement) {
      final ASTNode newNode = newStatement.getNode().copyElement();
      oldStatement.getParent().getNode().replaceChild(oldStatement.getNode(), newNode);
      return (TeaStatement)newNode.getPsi();
    }

    public static void doIdentifierReplacement(PsiElement parent,PsiElement identifier, String name) {
      final ASTNode nameElement = TeaChangeUtil.createNameIdentifier(parent.getProject(), name);
      parent.getNode().replaceChild(identifier.getNode(), nameElement);
    }

    public static PsiElement doAddBefore(final PsiElement jsElement, final PsiElement element, final PsiElement anchor) throws
                                                                                                                        IncorrectOperationException {
      if (!TeaChangeUtil.isStatementOrComment(element) && !(element instanceof PsiWhiteSpace)) {
        throw new UnsupportedOperationException("js statement or whitespace expected");
      }

      final ASTNode elementNode = element.getNode();
      if (elementNode == null) throw new IncorrectOperationException("node should not be null");
      ASTNode copiedElementNode = elementNode.copyElement();
      final ASTNode parentNode = jsElement.getNode();
      ASTNode anchorNode = anchor != null ? anchor.getNode(): null;

      anchorNode = insertWhitespaceIfNeeded(anchorNode, elementNode, parentNode, anchorNode);

      parentNode.addChild(copiedElementNode, anchorNode != null ? anchorNode:null);

      return copiedElementNode.getPsi();
    }

    private static ASTNode insertWhitespaceIfNeeded(ASTNode anchorNode,
                                                    final ASTNode elementNode,
                                                    final ASTNode parentNode,
                                                    final ASTNode insertionPlaceNode) throws IncorrectOperationException {
      ParserDefinition parserDef = LanguageParserDefinitions.INSTANCE.forLanguage(parentNode.getPsi().getLanguage());
      final TokenSet comments = parserDef.getCommentTokens();
      final TokenSet whitespaces = parserDef.getWhitespaceTokens();

      if (anchorNode != null &&
          ( ( !whitespaces.contains(anchorNode.getElementType()) &&
            !whitespaces.contains(elementNode.getElementType())
            ) ||
            comments.contains(anchorNode.getElementType()) ||
            comments.contains(elementNode.getElementType())
         )
      ) {
        String commentString = " ";
        if (comments.contains(anchorNode.getElementType()) ||
            comments.contains(elementNode.getElementType())
            ) {
          commentString = "\n";
        }

        final ASTNode wsNode = PsiParserFacade.SERVICE.getInstance(parentNode.getPsi().getProject()).createWhiteSpaceFromText(commentString).getNode();
        parentNode.addChild(
          wsNode,
          insertionPlaceNode
        );
        anchorNode = wsNode;
      }
      return anchorNode;
    }

    public static boolean isStatementContainer(final PsiElement jsElement) {
      return jsElement instanceof TeaBlockStatement /*|| jsElement instanceof TeaEmbeddedContentImpl*/;
    }

    public static boolean isStatementOrComment(final PsiElement jsElement) {
      return jsElement instanceof TeaSourceElement || jsElement instanceof PsiComment;
    }

    public static PsiElement doAddAfter(final PsiElement jsElement, final PsiElement element, final PsiElement anchor) {
      if (!TeaChangeUtil.isStatementOrComment(element) && !(element instanceof PsiWhiteSpace)) {
        throw new UnsupportedOperationException("js statement or whitespace expected");
      }

      try {
        final ASTNode parentNode = jsElement.getNode();
        final ASTNode node = element.getNode();
        ASTNode anchorNode = anchor != null ? anchor.getNode(): parentNode.getLastChildNode();
        anchorNode = insertWhitespaceIfNeeded(anchorNode, node, parentNode,anchorNode != null ? anchorNode.getTreeNext(): null);

        final ASTNode nodeCopy = node.copyElement();

        if (anchor == null) {
          parentNode.addChild(nodeCopy);
        } else {
          parentNode.addChild(nodeCopy, anchorNode.getTreeNext());
        }

        final ASTNode nextAfter = nodeCopy.getTreeNext();
        insertWhitespaceIfNeeded(nextAfter, node, parentNode,nextAfter);

        return nodeCopy.getPsi();
      }
      catch (IncorrectOperationException e) {
        throw new RuntimeException(e);
      }
    }

    public static PsiElement doAddRangeBefore(PsiElement parent, PsiElement first, final PsiElement last, final PsiElement anchor) throws IncorrectOperationException {
      final PsiElement resultElement;
      PsiElement psiElement = resultElement = doAddBefore(parent, first, anchor);

      while(first != last) {
        first = first.getNextSibling();
        if (first == null) break;
        psiElement = doAddAfter(parent, first, psiElement);
      }

      return resultElement;
    }

    public static PsiElement doAddRangeAfter(final PsiElement jsElement, PsiElement first, final PsiElement last, final PsiElement anchor) {
      final PsiElement resultElement;
      PsiElement psiElement = resultElement = doAddAfter(jsElement, first, anchor);

      while(first != last) {
        first = first.getNextSibling();
        if (first == null) break;
        psiElement = doAddAfter(jsElement, first, psiElement);
      }

      return resultElement;
    }

    public static boolean isBlockStatementContainer(final TeaElement jsElement) {
      return jsElement instanceof TeaIfStatement || jsElement instanceof TeaLoopStatement;
    }

    public static PsiElement blockDoAddRangeBefore(final PsiElement first,
                                                   final PsiElement last,
                                                   final @NotNull PsiElement anchor) throws IncorrectOperationException {
      BlockAddContext addContext = new BlockAddContext(anchor) {
        PsiElement doAddElement(PsiElement... element) throws IncorrectOperationException {
          return newlyAddedBlock.addRangeBefore(element[0],element[1], codeBlockAnchor);
        }
      };

      return addContext.doAddElement(first, last);
    }

    public static PsiElement blockDoAddRangeAfter(final PsiElement first,
                                                  final PsiElement last,
                                                  final @NotNull PsiElement anchor) throws IncorrectOperationException {
      BlockAddContext addContext = new BlockAddContext(anchor) {
        PsiElement doAddElement(PsiElement... element) throws IncorrectOperationException {
          return newlyAddedBlock.addRangeAfter(element[0],element[1], codeBlockAnchor);
        }
      };

      return addContext.doAddElement(first, last);
    }

    public static PsiElement blockDoAddAfter(final PsiElement element, final @NotNull PsiElement anchor)
      throws IncorrectOperationException {
      BlockAddContext addContext = new BlockAddContext(anchor) {
        PsiElement doAddElement(PsiElement... element) throws IncorrectOperationException {
          return newlyAddedBlock.addAfter(element[0], codeBlockAnchor);
        }
      };

      return addContext.doAddElement(element);
    }

    public static PsiElement blockDoAddBefore(final PsiElement element, final @NotNull PsiElement anchor)
      throws IncorrectOperationException {
      BlockAddContext addContext = new BlockAddContext(anchor) {
        PsiElement doAddElement(PsiElement... element) throws IncorrectOperationException {
          return newlyAddedBlock.addBefore(element[0], codeBlockAnchor);
        }
      };

      return addContext.doAddElement(element);
    }

    abstract static class BlockAddContext {
      final TeaBlockStatement newlyAddedBlock;
      final PsiElement codeBlockAnchor;

      BlockAddContext(final @NotNull PsiElement _anchor) throws IncorrectOperationException {
        final ASTNode codeBlockNode = TeaChangeUtil.createStatementFromText(_anchor.getProject(), "{ a }");

        newlyAddedBlock = (TeaBlockStatement)_anchor.replace(((TeaBlockStatement)codeBlockNode.getPsi()));

        final TeaStatement artificiallyAddedBlockAnchor = newlyAddedBlock.getStatements()[0];
        codeBlockAnchor = newlyAddedBlock.addBefore(_anchor, artificiallyAddedBlockAnchor);
        artificiallyAddedBlockAnchor.delete();
      }

      abstract PsiElement doAddElement(PsiElement... element) throws IncorrectOperationException;
    }
}
