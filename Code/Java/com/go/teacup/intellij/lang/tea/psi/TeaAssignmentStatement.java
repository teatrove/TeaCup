package com.go.teacup.intellij.lang.tea.psi;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 6:05:30 PM
 */
public interface TeaAssignmentStatement extends TeaStatement {
    TeaType getType();
    TeaVariable getVariable();
}
