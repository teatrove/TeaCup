package com.go.teacup.intellij.lang.tea.psi.util;

import static com.go.teacup.intellij.lang.tea.TeaElementTypes.*;
import static com.go.teacup.intellij.lang.tea.TeaTokenTypes.*;
import com.go.teacup.intellij.lang.tea.psi.*;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTagChild;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.jsp.JspFile;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 4:43:10 PM
 */
public class TeaUtils {
    public static boolean isLHSExpression(TeaExpression expr) {
      if (expr instanceof TeaDefinitionExpression) {
        expr = ((TeaDefinitionExpression)expr).getExpression();
      }

      if (expr instanceof TeaReferenceExpression) {
        return true;
      }

      if (expr instanceof TeaParenthesizedExpression) {
        return isLHSExpression(((TeaParenthesizedExpression)expr).getInnerExpression());
      }

      if (expr instanceof TeaIndexedPropertyAccessExpression) {
        return true;
      }

      if (expr instanceof TeaCallExpression) {
        return true;
      }

      return false;
    }

    public static boolean isNeedParenthesis(TeaExpression oldExpr, TeaExpression newExpr) {
      int priority = getExpressionPrecedence(newExpr);
      final PsiElement parent = oldExpr.getParent();
      if (!(parent instanceof TeaExpression)) return false;
      int parentPriority = getExpressionPrecedence((TeaExpression)parent);
      if (priority < parentPriority) return true;
      if (priority == parentPriority && parent instanceof TeaBinaryExpression) {
        final IElementType operationSign = ((TeaBinaryExpression)parent).getOperationSign();
        if (oldExpr != ((TeaBinaryExpression)parent).getROperand()) return false;
//        if (!ASSOC_OPERATIONS.contains(operationSign)) return true;

        return (((TeaBinaryExpression)newExpr).getOperationSign() != operationSign);
      }

      return false;
    }

    private static int getExpressionPrecedence(TeaExpression expr) {
      IElementType i = expr.getNode().getElementType();
      /*if (i == ASSIGNMENT_EXPRESSION) {
        return 0;
      }
      else if (i == CONDITIONAL_EXPRESSION) {
        return 1;
      }
      else*/ if (i == BINARY_EXPRESSION) {
        {
          IElementType opType = ((TeaBinaryExpression)expr).getOperationSign();
          if (opType == OR_KEYWORD) {
            return 2;
          }
          else if (opType == AND_KEYWORD) {
            return 3;
          }
//          else if (opType == OR) {
//            return 4;
//          }
//          else if (opType == XOR) {
//            return 5;
//          }
          else if (opType == AND) {
            return 6;
          }
          else if (EQUALITY_OPERATIONS.contains(opType)) {
            return 7;
          }
          else if (RELATIONAL_OPERATIONS.contains(opType)) {
            return 8;
          }
//          else if (SHIFT_OPERATIONS.contains(opType)) {
//            return 9;
//          }
          else if (ADDITIVE_OPERATIONS.contains(opType)) {
            return 10;
          }
          else if (MULTIPLICATIVE_OPERATIONS.contains(opType)) {
            return 11;
          }
        }

        return 8;
      }
      else if (i == PREFIX_EXPRESSION) {
        return 12;
      }
//      else if (i == POSTFIX_EXPRESSION) {
//        return 13;
//      }

      return 14;
    }

    public static PsiElement findStatementAnchor(final TeaReferenceExpression referenceExpression, final PsiFile file) {
      PsiElement anchor = PsiTreeUtil.getParentOfType(referenceExpression, TeaStatement.class);

      if (file instanceof XmlFile) {
        final XmlAttributeValue attributeValue = PsiTreeUtil.getParentOfType(referenceExpression, XmlAttributeValue.class);

        if (attributeValue != null) {
          XmlFile root = ((XmlFile)file);
          if (root instanceof JspFile) {
            root = (XmlFile)((JspFile)root).getBaseLanguageRoot();
          }

          final XmlTag tag = root.getDocument().getRootTag();

          if (tag != null) {
            final XmlTag headTag = tag.findFirstSubTag("head");

            if (headTag != null) {
              final XmlTag scriptTag = headTag.findFirstSubTag("script");

              if (scriptTag != null) {
                PsiElement statementInScript = PsiTreeUtil.getChildOfType(scriptTag, TeaStatement.class);
                if (statementInScript != null) anchor = statementInScript;
                else {
                  final XmlTagChild tagChild = PsiTreeUtil.getChildOfType(scriptTag, XmlTagChild.class);
                  if (tagChild != null) anchor = tagChild;
                }
              }
            }
          }
        }
      }
      return anchor;
    }
}
