package com.go.teacup.intellij.lang.tea.validation;

import com.go.teacup.intellij.lang.tea.TeaBundle;
import com.go.teacup.intellij.lang.tea.TeaElementType;
import com.go.teacup.intellij.lang.tea.psi.*;
import com.go.teacup.intellij.lang.tea.psi.resolve.ResolveProcessor;
import com.go.teacup.intellij.lang.tea.psi.resolve.TeaResolveUtil;
import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 11:11:14 AM
 */
public class TeaAnnotatingVisitor extends TeaElementVisitor implements Annotator {
    private AnnotationHolder holder;


    public void annotate(PsiElement psiElement, AnnotationHolder holder) {
        this.holder = holder;
        psiElement.accept(this);
    }


    @Override public void visitTeaTemplateDeclaration(final TeaTemplate node) {
      final String name = node.getName();
      if (name == null) return;
      final ASTNode nameIdentifier = node.findNameIdentifier();

        // TODO: duplication declaration annotation is incorrect
//      checkForDuplicateDeclaration(name, node, nameIdentifier);
    }

    private void checkForDuplicateDeclaration(final String name, final PsiElement decl, final ASTNode nameIdentifier) {
      final ResolveProcessor processor = new ResolveProcessor(name) {
        public boolean execute(PsiElement element, PsiSubstitutor substitutor) {
          if (element == decl) return true;
          if (!decl.getClass().isInstance(element)) return true;
          if (decl instanceof TeaParameter && decl.getParent() != element.getParent()) return false;
          return super.execute(element, substitutor);
        }

        public <T> T getHint(Class<T> hintClass) {
          return null;
        }

        public void handleEvent(PsiScopeProcessor.Event event, Object associated) {
        }
      };

      final PsiElement scope = PsiTreeUtil.getNonStrictParentOfType(decl, TeaTemplate.class, TeaFile.class);
      TeaResolveUtil.treeWalkUp(processor, decl, null, decl, scope);

      if (processor.getResult() != null) {
        holder.createWarningAnnotation(nameIdentifier, TeaBundle.message("tea.validation.message.duplicate.declaration"));
      }
    }


    public void visitTeaCallExpression(final TeaCallExpression node) {
      final TeaExpression methodExpression = node.getMethodExpression();

      if (methodExpression instanceof TeaLiteralExpression) {
        holder.createErrorAnnotation(methodExpression, TeaBundle.message("tea.parser.message.expected.template.name"));
      }
    }

    public void visitTeaReferenceExpression(final TeaReferenceExpression node) {
      if (!(node.getParent() instanceof TeaCallExpression) && node.getQualifier() == null) {
        if ("arguments".equals(node.getText())) {
          if (PsiTreeUtil.getParentOfType(node,TeaTemplate.class) == null) {
            holder.createErrorAnnotation(node, TeaBundle.message("tea.validation.message.arguments.out.of.function"));
          }
        }
      }
    }

//    public void visitTeaAssignmentStatement(final TeaAssignmentStatement statement) {
//      TeaExpression lExpr = statement.getLOperand();
//      if (lExpr instanceof TeaDefinitionExpression) lExpr = ((TeaDefinitionExpression)lExpr).getExpression();
//
//      if (lExpr instanceof TeaReferenceExpression && ((TeaReferenceExpression)lExpr).getQualifier() == null) {
//        PsiElement resolved = ((TeaReferenceExpression)lExpr).resolve();
//        if (resolved instanceof TeaVariable && ((TeaVariable)resolved).isConst()) {
//          holder.createErrorAnnotation(lExpr, TeaBundle.message("tea.validation.message.assignment.to.const"));
//        }
//      }
//
//      if (!TeaUtils.isLHSExpression(lExpr)) {
//        holder.createErrorAnnotation(lExpr, TeaBundle.message("tea.validation.message.must.be.lvalue"));
//      }
//    }

    public void visitTeaVariable(final TeaVariable var) {
//      if (var.isConst() && var.getInitializer() == null) {
//        myHolder.createWarningAnnotation(var, TeaBundle.message("javascript.validation.message.const.variable.without.initializer."));
//      }

      final ASTNode nameIdentifier = var.findNameIdentifier();
      final ASTNode next = nameIdentifier != null ? nameIdentifier.getTreeNext():null;
      final String name = nameIdentifier != null ? nameIdentifier.getText():null;

      // Actully skip outer language elements
      if (name != null &&
          ( next == null ||
            next.getElementType() instanceof TeaElementType ||
            next.getPsi() instanceof PsiWhiteSpace)
         ) {
          // TODO: duplication declaration annotation is incorrect
//        checkForDuplicateDeclaration(name, var, nameIdentifier);
      }
    }

//    public void visitTeaContinueStatement(final TeaContinueStatement node) {
//      if (node.getStatementToContinue() == null) {
//        myHolder.createErrorAnnotation(node, TeaBundle.message("javascript.validation.message.continue.without.target"));
//      }
//    }

    public void visitTeaBreakStatement(final TeaBreakStatement node) {
      if (node.getStatementToBreak() == null) {
        holder.createErrorAnnotation(node, TeaBundle.message("tea.validation.message.break.without.target"));
      }
    }

//    public void visitTeaReturnStatement(final TeaReturnStatement node) {
//      if (PsiTreeUtil.getParentOfType(node, TeaFunction.class, XmlTagChild.class) == null) {
//        myHolder.createErrorAnnotation(node, TeaBundle.message("tea.validation.message.return.outside.function.definition"));
//      }
//    }

//    public void visitTeaLabeledStatement(final TeaLabeledStatement node) {
//      final String label = node.getLabel();
//      if (label != null) {
//        PsiElement run = node.getParent();
//        while(run != null) {
//          if (run instanceof TeaLabeledStatement) {
//            if (label.equals(((TeaLabeledStatement)run).getLabel())) {
//              myHolder.createErrorAnnotation(node.getLabelIdentifier(), TeaBundle.message("javascript.validation.message.duplicate.label"));
//              break;
//            }
//          }
//
//          if (run instanceof TeaFunction) break;
//          run = run.getParent();
//        }
//      }
//    }
}
