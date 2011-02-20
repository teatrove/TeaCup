package com.go.teacup.intellij.lang.tea.refactoring;

import com.go.teacup.intellij.lang.tea.lexer.TeaLexer;
import com.go.teacup.intellij.lang.tea.lexer.TeaTokenTypes;
import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;

/**
 * User: JACKSBRR
 * Created: Apr 20, 2007 3:48:33 PM
 */
public class TeaNamesValidator implements NamesValidator {
    private Lexer lexer = new TeaLexer();

  public boolean isKeyword(String name, Project project) {
    lexer.start(name, 0, name.length());
    return TeaTokenTypes.KEYWORDS.contains( lexer.getTokenType() ) &&
      lexer.getTokenEnd() == name.length();
  }

  public boolean isIdentifier(String name, Project project) {
    lexer.start(name, 0, name.length());
    return lexer.getTokenType() == TeaTokenTypes.IDENTIFIER &&
      lexer.getTokenEnd() == name.length();
  }}
