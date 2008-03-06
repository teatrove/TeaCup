package com.go.teacup.intellij.lang.tea.psi;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 11:31:03 AM
 */
public interface TeaBreakStatement extends TeaStatement {
    TeaStatement getStatementToBreak();
}
