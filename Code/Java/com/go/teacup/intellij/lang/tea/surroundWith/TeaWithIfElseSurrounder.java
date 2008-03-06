package com.go.teacup.intellij.lang.tea.surroundWith;

import com.go.teacup.intellij.lang.tea.TeaBundle;

/**
 * User: JACKSBRR
 * Created: Apr 20, 2007 4:09:51 PM
 */
public class TeaWithIfElseSurrounder extends TeaWithIfSurrounder {
  public String getTemplateDescription() {
    return TeaBundle.message("tea.surround.with.if.else");
  }

  protected String getStatementTemplate() {
    return "if (a) { } else { }";
  }
}
