package com.go.teacup.intellij.lang.tea.psi;

/**
 * User: JACKSBRR
 * Created: Apr 20, 2007 2:55:12 PM
 */
public interface TeaTemplateCallExpression extends TeaCallExpression {
    TeaBlockStatement getSubstitutionBlock();
}
