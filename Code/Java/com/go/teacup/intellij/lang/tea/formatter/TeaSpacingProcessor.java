package com.go.teacup.intellij.lang.tea.formatter;

import com.go.teacup.intellij.lang.tea.TeaNodeVisitor;
import com.go.teacup.intellij.lang.tea.TeaTokenTypes;
import com.go.teacup.intellij.lang.tea.TeaElementTypes;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 5:41:59 PM
 */
public class TeaSpacingProcessor extends TeaNodeVisitor {
    private final CodeStyleSettings settings;
    private Spacing result;
    private final IElementType type1;
    private final IElementType type2;

    private ASTNode parent, child1, child2;

    public TeaSpacingProcessor(final ASTNode parent, final ASTNode child1, final ASTNode child2, CodeStyleSettings settings) {
        this.parent = parent;
        this.child1 = child1;
        this.child2 = child2;
        this.settings = settings;
        type1 = child1.getElementType();
        type2 = child2.getElementType();
        visit(parent);
    }

    public Spacing getResult() {
        return result;
    }

    //TODO All visitXXX methods
    public void visitEmbeddedContent(final ASTNode node) {
    if (TeaElementTypes.SOURCE_ELEMENTS.contains(type1) ||
        TeaElementTypes.SOURCE_ELEMENTS.contains(type2) && type1 != TeaTokenTypes.DOT ||
        type2 == TeaTokenTypes.RBRACE) {
      result = Spacing.createSpacing(0, 0, 1, settings.KEEP_LINE_BREAKS, settings.KEEP_BLANK_LINES_IN_CODE);
    }
  }

  public void visitParameterList(final ASTNode node) {
    if (type1 == TeaTokenTypes.LPAR && type2 == TeaTokenTypes.RPAR) {
      setSingleSpace(false);
    }
    else if (type1 == TeaTokenTypes.LPAR || type2 == TeaTokenTypes.RPAR) {
      setSingleSpace(settings.SPACE_WITHIN_METHOD_PARENTHESES);
    }
    else if (type1 == TeaTokenTypes.COMMA) {
      setSingleSpace(settings.SPACE_AFTER_COMMA);
    }
    else if (type2 == TeaTokenTypes.COMMA) {
      setSingleSpace(settings.SPACE_BEFORE_COMMA);
    }
  }

  public void visitBlock(final ASTNode node) {
    if (TeaElementTypes.SOURCE_ELEMENTS.contains(type1) || TeaElementTypes.SOURCE_ELEMENTS.contains(type2) ||
        type2 == TeaTokenTypes.RBRACE) {
      if ( (type1 == TeaTokenTypes.BAD_CHARACTER && TeaElementTypes.SOURCE_ELEMENTS.contains(type2)) ||
           (type2 == TeaTokenTypes.BAD_CHARACTER && TeaElementTypes.SOURCE_ELEMENTS.contains(type1))
         ) {
        result = Spacing.getReadOnlySpacing();
      } else {
        result = Spacing.createSpacing(0, 0, 1, settings.KEEP_LINE_BREAKS, settings.KEEP_BLANK_LINES_IN_CODE);
      }
    }
  }

  public void visitFile(final ASTNode node) {
    if (TeaElementTypes.SOURCE_ELEMENTS.contains(type1) || TeaElementTypes.SOURCE_ELEMENTS.contains(type2)) {
      result = Spacing.createSpacing(0, 0, 1, settings.KEEP_LINE_BREAKS, settings.KEEP_BLANK_LINES_IN_CODE);
    }
  }

  public void visitTemplateDeclaration(final ASTNode node) {
    if (type1 == TeaTokenTypes.TEMPLATE_KEYWORD && type2 == TeaTokenTypes.IDENTIFIER) {
      setSingleSpace(true);
    }
    else if (type1 == TeaTokenTypes.IDENTIFIER && type2 == TeaElementTypes.PARAMETER_LIST) {
      setSingleSpace(settings.SPACE_BEFORE_METHOD_PARENTHESES);
    }
    else if (type1 == TeaElementTypes.PARAMETER_LIST) {
      setBraceSpace(settings.SPACE_BEFORE_METHOD_LBRACE, settings.METHOD_BRACE_STYLE, child1.getTextRange());
    }
  }


  public void visitFunctionExpression(final ASTNode node) {
    visitTemplateDeclaration(node);
  }

  public void visitReferenceExpression(final ASTNode node) {
//    if (type1 == TeaTokenTypes.NEW_KEYWORD) {
//      setSingleSpace(true);
//    }
//    else {
      setSingleSpace(false); // a.b should not have spaces before and after dot
//    }
  }

  public void visitIfStatement(final ASTNode node) {
    if (type1 == TeaTokenTypes.IF_KEYWORD && type2 == TeaTokenTypes.LPAR) {
      setSingleSpace(settings.SPACE_BEFORE_IF_PARENTHESES);
    }
    else if (type1 == TeaTokenTypes.LPAR || type2 == TeaTokenTypes.RPAR) {
      setSingleSpace(settings.SPACE_WITHIN_IF_PARENTHESES);
    }
    else if (type1 == TeaTokenTypes.RPAR && type2 == TeaElementTypes.BLOCK) {
      TextRange dependentRange = new TextRange(parent.getStartOffset(), child1.getTextRange().getEndOffset());
      setBraceSpace(settings.SPACE_BEFORE_IF_LBRACE, settings.BRACE_STYLE, dependentRange);
    }
    else if (type2 == TeaTokenTypes.ELSE_KEYWORD) {
      setLineBreakSpace(settings.ELSE_ON_NEW_LINE);
    }
    else if (type1 == TeaTokenTypes.ELSE_KEYWORD && type2 == TeaElementTypes.BLOCK) {
      setBraceSpace(settings.SPACE_BEFORE_ELSE_LBRACE, settings.BRACE_STYLE, null);
    }
  }

  public void visitCallExpression(final ASTNode node) {
    if (type2 == TeaElementTypes.ARGUMENT_LIST) {
      setSingleSpace(settings.SPACE_BEFORE_METHOD_CALL_PARENTHESES);
    }
  }

  public void visitNewExpression(final ASTNode node) {
    /*if (type1 == TeaTokenTypes.NEW_KEYWORD) {
      setSingleSpace(true);
    }
    else*/ if (type2 == TeaElementTypes.ARGUMENT_LIST) {
      setSingleSpace(settings.SPACE_BEFORE_METHOD_CALL_PARENTHESES);
    }
  }

  public void visitForStatement(final ASTNode node) {
    if (type1 == TeaTokenTypes.SEMICOLON) {
      setSingleSpace(true);
    }
    else if (type2 == TeaTokenTypes.SEMICOLON) {
      setSingleSpace(settings.SPACE_BEFORE_SEMICOLON);
    }

    if (type1 == TeaTokenTypes.FOREACH_KEYWORD && type2 == TeaTokenTypes.LPAR) {
      setSingleSpace(settings.SPACE_BEFORE_FOR_PARENTHESES);
    }
    else if (type1 == TeaTokenTypes.RPAR && type2 == TeaElementTypes.BLOCK) {
      TextRange dependentRange = new TextRange(parent.getStartOffset(), child1.getTextRange().getEndOffset());
      setBraceSpace(settings.SPACE_BEFORE_FOR_LBRACE, settings.BRACE_STYLE, dependentRange);
    }
    else if (type1 == TeaTokenTypes.LPAR || type2 == TeaTokenTypes.RPAR) {
      setSingleSpace(settings.SPACE_WITHIN_FOR_PARENTHESES);
    }
  }

//  public void visitDoWhileStatement(final ASTNode node) {
//    if (type2 == TeaTokenTypes.WHILE_KEYWORD) {
//      if (settings.WHILE_ON_NEW_LINE) {
//        result = Spacing.createSpacing(0, 0, 1, settings.KEEP_LINE_BREAKS, settings.KEEP_BLANK_LINES_IN_CODE);
//      }
//      else {
//        result = Spacing.createSpacing(1, 1,  0, settings.KEEP_LINE_BREAKS, settings.KEEP_BLANK_LINES_IN_CODE);
//      }
//    } else if (type2 == TeaTokenTypes.LPAR) {
//      setSingleSpace(settings.SPACE_BEFORE_WHILE_PARENTHESES);
//    } else if (type1 == TeaTokenTypes.LPAR || type2 == TeaTokenTypes.RPAR) {
//      setSingleSpace(settings.SPACE_WITHIN_WHILE_PARENTHESES);
//    }
//  }

  public void visitForEachStatement(final ASTNode node) {
    /*if (type1 == TeaTokenTypes.VAR_KEYWORD || type2 == TeaTokenTypes.VAR_KEYWORD) {
      setSingleSpace(true);
    }
    else*/ if (type1 == TeaTokenTypes.FOREACH_KEYWORD && type2 == TeaTokenTypes.LPAR) {
      setSingleSpace(settings.SPACE_BEFORE_FOR_PARENTHESES);
    }
    else if (type1 == TeaTokenTypes.RPAR && type2 == TeaElementTypes.BLOCK) {
      TextRange dependentRange = new TextRange(parent.getStartOffset(), child1.getTextRange().getEndOffset());
      setBraceSpace(settings.SPACE_BEFORE_FOR_LBRACE, settings.BRACE_STYLE, dependentRange);
    }
    else if (type1 == TeaTokenTypes.LPAR || type2 == TeaTokenTypes.RPAR) {
      setSingleSpace(settings.SPACE_WITHIN_FOR_PARENTHESES);
    }
  }

//  public void visitWhileStatement(final ASTNode node) {
//    if (type1 == TeaTokenTypes.WHILE_KEYWORD && type2 == TeaTokenTypes.LPAR) {
//      setSingleSpace(settings.SPACE_BEFORE_WHILE_PARENTHESES);
//    }
//    else if (type1 == TeaTokenTypes.RPAR && type2 == TeaElementTypes.BLOCK) {
//      TextRange dependentRange = new TextRange(parent.getStartOffset(), child1.getTextRange().getEndOffset());
//      setBraceSpace(settings.SPACE_BEFORE_WHILE_LBRACE, settings.BRACE_STYLE, dependentRange);
//    }
//    else if (type1 == TeaTokenTypes.LPAR || type2 == TeaTokenTypes.RPAR) {
//      setSingleSpace(settings.SPACE_WITHIN_WHILE_PARENTHESES);
//    }
//  }

//  public void visitWithStatement(final ASTNode node) {
//    if (type1 == TeaTokenTypes.WITH_KEYWORD && type2 == TeaTokenTypes.LPAR) {
//      setSingleSpace(settings.SPACE_BEFORE_WHILE_PARENTHESES);
//    }
//    else if (type1 == TeaTokenTypes.RPAR && type2 == TeaElementTypes.BLOCK) {
//      TextRange dependentRange = new TextRange(parent.getStartOffset(), child1.getTextRange().getEndOffset());
//      setBraceSpace(settings.SPACE_BEFORE_WHILE_LBRACE, settings.BRACE_STYLE, dependentRange);
//    }
//    else if (type1 == TeaTokenTypes.LPAR || type2 == TeaTokenTypes.RPAR) {
//      setSingleSpace(settings.SPACE_WITHIN_WHILE_PARENTHESES);
//    }
//  }

//  public void visitTryStatement(final ASTNode node) {
//    if (type1 == TeaTokenTypes.TRY_KEYWORD && type2 == TeaElementTypes.BLOCK) {
//      setBraceSpace(settings.SPACE_BEFORE_TRY_LBRACE, settings.BRACE_STYLE, null);
//    }
//    else if (type2 == TeaElementTypes.CATCH_BLOCK) {
//      setLineBreakSpace(settings.CATCH_ON_NEW_LINE);
//    }
//    else if (type2 == TeaTokenTypes.FINALLY_KEYWORD) {
//      setLineBreakSpace(settings.FINALLY_ON_NEW_LINE);
//    }
//    else if (type1 == TeaTokenTypes.FINALLY_KEYWORD) {
//      setBraceSpace(settings.SPACE_BEFORE_FINALLY_LBRACE, settings.BRACE_STYLE, null);
//    }
//  }
//
//  public void visitCatchBlock(final ASTNode node) {
//    if (type1 == TeaTokenTypes.LPAR || type2 == TeaTokenTypes.RPAR) {
//      setSingleSpace(settings.SPACE_WITHIN_CATCH_PARENTHESES);
//    }
//    if (type2 == TeaElementTypes.BLOCK) {
//      TextRange dependentRange = new TextRange(parent.getStartOffset(), myChild2.getTextRange().getStartOffset());
//      setBraceSpace(settings.SPACE_BEFORE_CATCH_LBRACE, settings.BRACE_STYLE, dependentRange);
//    }
//  }
//
//  public void visitSwitchStatement(final ASTNode node) {
//    if (type1 == TeaTokenTypes.SWITCH_KEYWORD && type2 == TeaTokenTypes.LPAR) {
//      setSingleSpace(settings.SPACE_BEFORE_SWITCH_PARENTHESES);
//    }
//    else if (type1 == TeaTokenTypes.RPAR) {
//      TextRange dependentRange = new TextRange(parent.getStartOffset(), child1.getTextRange().getEndOffset());
//      setBraceSpace(settings.SPACE_BEFORE_SWITCH_LBRACE, settings.BRACE_STYLE, dependentRange);
//    }
//    else if (type1 == TeaTokenTypes.LPAR || type2 == TeaTokenTypes.RPAR) {
//      setSingleSpace(settings.SPACE_WITHIN_SWITCH_PARENTHESES);
//    }
//  }

  public void visitArgumentList(final ASTNode node) {
    if (type1 == TeaTokenTypes.LPAR || type2 == TeaTokenTypes.RPAR) {
      setSingleSpace(false);
    }
    else if (type1 == TeaTokenTypes.COMMA) {
      setSingleSpace(settings.SPACE_AFTER_COMMA);
    }
    else if (type2 == TeaTokenTypes.COMMA) {
      setSingleSpace(settings.SPACE_BEFORE_COMMA);
    }
  }

  public void visitStatement(final ASTNode node) {
    if (type2 == TeaTokenTypes.SEMICOLON) {
      setSingleSpace(false);
    }
  }

//  public void visitVarStatement(final ASTNode node) {
//    if (type1 == TeaTokenTypes.VAR_KEYWORD) {
//      setSingleSpace(true);
//    }
//  }

  public void visitVariable(final ASTNode node) {
    if (type1 == TeaTokenTypes.EQ || type2 == TeaTokenTypes.EQ) { // Initializer
      setSingleSpace(settings.SPACE_AROUND_ASSIGNMENT_OPERATORS);
    }
  }

  public void visitBinaryExpression(final ASTNode node) {
    IElementType opSign = null;
    if (TeaTokenTypes.OPERATIONS.contains(type1)) {
      opSign = type1;
    }
    else if (TeaTokenTypes.OPERATIONS.contains(type2)) {
      opSign = type2;
    }

    if (opSign != null) {
      setSingleSpace(getSpaceAroundOption(opSign));
    }
  }

//  public void visitConditionalExpression(final ASTNode node) {
//    if (type1 == TeaTokenTypes.QUEST) {
//      setSingleSpace(settings.SPACE_AFTER_QUEST);
//    } else if (type2 == TeaTokenTypes.QUEST) {
//      setSingleSpace(settings.SPACE_BEFORE_QUEST);
//    } else if (type1 == TeaTokenTypes.COLON) {
//      setSingleSpace(settings.SPACE_AFTER_COLON);
//    } else if (type2 == TeaTokenTypes.COLON) {
//      setSingleSpace(settings.SPACE_BEFORE_COLON);
//    }
//  }

  private boolean getSpaceAroundOption(final IElementType opSign) {
    boolean option = false;
    if (TeaTokenTypes.ADDITIVE_OPERATIONS.contains(opSign)) {
      option = settings.SPACE_AROUND_ADDITIVE_OPERATORS;
    }
    else if (TeaTokenTypes.MULTIPLICATIVE_OPERATIONS.contains(opSign)) {
      option = settings.SPACE_AROUND_MULTIPLICATIVE_OPERATORS;
    }
    else if (TeaTokenTypes.ASSIGNMENT_OPERATIONS.contains(opSign)) {
      option = settings.SPACE_AROUND_ASSIGNMENT_OPERATORS;
    }
    else if (TeaTokenTypes.EQUALITY_OPERATIONS.contains(opSign)) {
      option = settings.SPACE_AROUND_EQUALITY_OPERATORS;
    }
    else if (TeaTokenTypes.RELATIONAL_OPERATIONS.contains(opSign)) {
      option = settings.SPACE_AROUND_RELATIONAL_OPERATORS;
    }
//    else if (TeaTokenTypes.SHIFT_OPERATIONS.contains(opSign)) {
//      option = settings.SPACE_AROUND_BITWISE_OPERATORS;
//    }
    else if (opSign == TeaTokenTypes.AND_KEYWORD || opSign == TeaTokenTypes.OR_KEYWORD) {
      option = settings.SPACE_AROUND_LOGICAL_OPERATORS;
    }
    else if (/*opSign == TeaTokenTypes.OR || */opSign == TeaTokenTypes.AND/* || opSign == TeaTokenTypes.XOR*/) {
      option = settings.SPACE_AROUND_BITWISE_OPERATORS;
    }
    return option;
  }

  private void setSingleSpace(boolean needSpace) {
    final int spaces = needSpace ? 1 : 0;
    result = Spacing.createSpacing(spaces, spaces, 0, settings.KEEP_LINE_BREAKS, settings.KEEP_BLANK_LINES_IN_CODE);
  }

  private void setBraceSpace(boolean needSpaceSetting, int braceStyleSetting, TextRange textRange) {
    int spaces = needSpaceSetting ? 1 : 0;
    if (braceStyleSetting == CodeStyleSettings.NEXT_LINE_IF_WRAPPED && textRange != null) {
      result = Spacing.createDependentLFSpacing(spaces, spaces, textRange, settings.KEEP_LINE_BREAKS, settings.KEEP_BLANK_LINES_IN_CODE);
    }
    else {
      int lineBreaks = braceStyleSetting == CodeStyleSettings.END_OF_LINE ? 0 : 1;
      result = Spacing.createSpacing(spaces, spaces, lineBreaks, settings.KEEP_LINE_BREAKS, settings.KEEP_BLANK_LINES_IN_CODE);
    }
  }

  private void setLineBreakSpace(final boolean needLineBreak) {
    final int breaks = needLineBreak ? 1 : 0;
    result = Spacing.createSpacing(1, 1, breaks, settings.KEEP_LINE_BREAKS, settings.KEEP_BLANK_LINES_IN_CODE);
  }
}
