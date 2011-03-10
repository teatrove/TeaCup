package com.go.teacup.intellij.lang.tea.validation;

import com.go.teacup.intellij.lang.tea.psi.*;
import com.intellij.psi.PsiElementVisitor;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 11:11:43 AM
 */
public class TeaElementVisitor extends PsiElementVisitor {

    public void visitTeaCodeRegion(TeaCodeRegion codeRegion) {
        visitTeaElement(codeRegion);
    }

    public void visitTeaCallExpression(final TeaCallExpression node) {
      visitTeaExpression(node);
    }

    public void visitTeaIndexedPropertyAccessExpression(final TeaIndexedPropertyAccessExpression node) {
      visitTeaExpression(node);
    }

    public void visitTeaPrefixExpression(final TeaPrefixExpression node) {
      visitTeaExpression(node);
    }

    public void visitTeaAssignmentStatement(final TeaAssignmentStatement node) {
      visitTeaStatement(node);
    }

    public void visitTeaBinaryExpression(final TeaBinaryExpression node) {
      visitTeaExpression(node);
    }

    public void visitTeaProperty(final TeaProperty node) {
      visitTeaElement(node);
    }

    public void visitTeaArrayLiteralExpression(final TeaArrayLiteralExpression node) {
      visitTeaExpression(node);
    }

    public void visitTeaParenthesizedExpression(final TeaParenthesizedExpression node) {
      visitTeaExpression(node);
    }

    public void visitTeaReferenceExpression(final TeaReferenceExpression node) {
      visitTeaExpression(node);
    }

    public void visitTeaDefinitionExpression(final TeaDefinitionExpression node) {
      visitTeaExpression(node);
    }

    public void visitTeaLiteralExpression(final TeaLiteralExpression node) {
      visitTeaExpression(node);
    }

    public void visitTeaForEachStatement(final TeaForEachStatement node) {
      visitTeaStatement(node);
    }

    public void visitTeaBreakStatement(final TeaBreakStatement node) {
      visitTeaStatement(node);
    }

    public void visitTeaIfStatement(final TeaIfStatement node) {
      visitTeaStatement(node);
    }

    public void visitTeaEmptyStatement(final TeaEmptyStatement node) {
      visitTeaStatement(node);
    }

    public void visitTeaExpressionStatement(final TeaExpressionStatement node) {
      visitTeaStatement(node);
    }

    public void visitTeaBlock(final TeaBlockStatement node) {
      visitTeaStatement(node);
    }

    public void visitTeaArgumentList(final TeaArgumentList node) {
      visitTeaElement(node);
    }

    public void visitTeaParameter(final TeaParameter node) {
      visitTeaVariable(node);
    }

    public void visitTeaVariable(final TeaVariable node) {
      visitTeaElement(node);
    }

    public void visitTeaType(final TeaType node) {
      visitTeaElement(node);
    }

    public void visitTeaParameterList(final TeaParameterList node) {
      visitTeaElement(node);
    }

    public void visitTeaElement(final TeaElement node) {
      visitElement(node);
    }

    public void visitTeaSourceElement(final TeaElement node) {
      visitTeaElement(node);
    }

    public void visitTeaTemplateDeclaration(final TeaTemplate node) {
      visitTeaSourceElement(node);
    }

    public void visitTeaStatement(final TeaStatement node) {
      visitTeaSourceElement(node);
    }

    public void visitTeaExpression(final TeaExpression node) {
      visitTeaElement(node);
    }

    public void visitTeaSubstitutionStatement(TeaSubstitutionStatement node) {
        visitTeaStatement(node);
    }
}
