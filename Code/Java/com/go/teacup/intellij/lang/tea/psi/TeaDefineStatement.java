package com.go.teacup.intellij.lang.tea.psi;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 8:17:57 PM
 */
public interface TeaDefineStatement extends TeaStatement {
    TeaType getType();
    TeaVariable getVariable();
}
