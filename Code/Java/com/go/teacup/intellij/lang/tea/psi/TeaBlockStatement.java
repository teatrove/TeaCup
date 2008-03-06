package com.go.teacup.intellij.lang.tea.psi;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 4:39:03 PM
 */
public interface TeaBlockStatement extends TeaStatement {
    TeaStatement[] getStatements();
}
