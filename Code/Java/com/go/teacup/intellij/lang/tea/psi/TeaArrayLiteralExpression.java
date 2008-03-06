package com.go.teacup.intellij.lang.tea.psi;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 4:10:41 PM
 */
public interface TeaArrayLiteralExpression extends TeaExpression {
    /**
     * @return nulls stand in the returned array for skipped values. This for [,1,] array of 3 elements to be returned with first and last
     * elements nulled
     */
    TeaExpression[] getExpressions();
}
