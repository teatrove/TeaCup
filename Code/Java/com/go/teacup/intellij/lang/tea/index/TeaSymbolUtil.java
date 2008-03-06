package com.go.teacup.intellij.lang.tea.index;

import com.go.teacup.intellij.lang.tea.psi.*;
import com.go.teacup.intellij.lang.tea.psi.resolve.ResolveProcessor;
import com.go.teacup.intellij.lang.tea.psi.resolve.TeaResolveUtil;
import com.go.teacup.intellij.lang.tea.validation.TeaElementVisitor;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlTag;
import gnu.trove.TIntArrayList;
import org.jetbrains.annotations.NonNls;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 11:33:52 AM
 */
public class TeaSymbolUtil {
    @NonNls
    private static final String PROTOTYPE_FIELD_NAME = "prototype";

    public static void visitSymbols(final PsiFile file, TeaNamespace namespace, final TeaSymbolProcessor symbolVisitor) {
        file.acceptChildren(new TeaSymbolVisitor(namespace, symbolVisitor, file.getProject()));
    }

    private static TeaElement findNameComponent(TeaElement expr) {
        if (expr instanceof TeaReferenceExpression) return expr;
        TeaElement current = expr;

        while (expr != null) {
            if (expr instanceof TeaReferenceExpression) {
                return expr;
//            } else if (expr instanceof TeaAssignmentStatement) {
//                final TeaExpression _lOperand = ((TeaAssignmentStatement) expr).getVariable();
//                if (!(_lOperand instanceof TeaDefinitionExpression)) break;
//                final TeaExpression lOperand = ((TeaDefinitionExpression) _lOperand).getExpression();
//
//                if (lOperand instanceof TeaReferenceExpression) {
//                    expr = lOperand;
//                    continue;
//                }
            } else if (expr instanceof TeaVariable) {
                return expr;
            } else {
                current = expr;
            }

            if (current != null) {
                final PsiElement parent = current.getParent();
                if (!(parent instanceof TeaElement)) break;
                if (parent instanceof TeaStatement) break;
                expr = (TeaElement) parent;
            }
        }

        return null;
    }

    public static int[] buildNameIndexArray(final TeaElement _expr, TeaNamespace contextNamespace, final TeaIndex index) {
        final TIntArrayList nameComponents = new TIntArrayList();

        TeaElement nameComponent = findNameComponent(_expr);
        TeaReferenceExpression expr = null;

        if (nameComponent instanceof TeaVariable) {
            String varName = ((TeaVariable) nameComponent).getName();
            if (varName != null) {
                nameComponents.add(index.getIndexOf(varName));
            }
        } else if (nameComponent instanceof TeaReferenceExpression) {
            expr = (TeaReferenceExpression) nameComponent;
        }

        if (expr != null) {
            final TeaReferenceExpression expr1 = expr;
            visitReferenceExpressionComponentsInRootFirstOrder(
                    expr,
                    contextNamespace,
                    new ReferenceExpressionProcessor() {
                        public void processNamespace(TeaNamespace ns) {
                            nameComponents.add(ns.getNameId());
                        }

                        public void processExpression(TeaReferenceExpression expr) {
                            nameComponents.add(index.getIndexOf(expr.getReferencedName()));
                        }

                        public void processUnresolvedThis() {
                            nameComponents.add(index.getIndexOf(""));
                        }

                        public boolean isTopLevel(final TeaReferenceExpression expression) {
                            return expr1 == expression;
                        }
                    }
            );
        }

        return nameComponents.toNativeArray();
    }

    interface ReferenceExpressionProcessor {
        void processNamespace(TeaNamespace ns);

        void processExpression(TeaReferenceExpression expr);

        void processUnresolvedThis();

        boolean isTopLevel(final TeaReferenceExpression expression);
    }

//    private static void visitNamespaceComponentsInRootFirstOrder(TeaNamespace namespace, ReferenceExpressionProcessor processor) {
//      final TeaNamespace parentNs = namespace.getParent();
//      if (parentNs != null) visitNamespaceComponentsInRootFirstOrder(parentNs, processor);
//      processor.processNamespace(namespace);
//    }

    private static void visitReferenceExpressionComponentsInRootFirstOrder(TeaReferenceExpression expr, final TeaNamespace contextNamespace, ReferenceExpressionProcessor processor) {
        final TeaExpression qualifier = expr.getQualifier();

        if (qualifier instanceof TeaReferenceExpression) {
            visitReferenceExpressionComponentsInRootFirstOrder((TeaReferenceExpression) qualifier, contextNamespace, processor);
        }

//      if (qualifier instanceof TeaThisExpression) {
//        if (contextNamespace != null) {
//          visitNamespaceComponentsInRootFirstOrder(contextNamespace, processor);
//        } else {
//          processor.processUnresolvedThis();
//        }
//      }

        final String refName = expr.getReferencedName();

        if (refName != null &&
                (!refName.equals(PROTOTYPE_FIELD_NAME) ||
                        processor.isTopLevel(expr)
                )
                ) {
            processor.processExpression(expr);
        }
    }

    private static class TeaSymbolVisitor extends TeaElementVisitor implements ReferenceExpressionProcessor {
        private final TeaSymbolProcessor mySymbolVisitor;
        private TeaNamespace myThisNamespace;
        private TeaNamespace myNamespace;
        private final TeaNamespace myFileNamespace;
        //      private boolean myInsideWithStatement;
        private TeaTemplate myFunction;
        private TeaIndex myIndex;
        private TeaTypeEvaluateManager myTypeEvaluateManager;

        private TeaNamespace currentNamespace;
        private TeaElement topExpression;

        public TeaSymbolVisitor(TeaNamespace namespace, final TeaSymbolProcessor symbolVisitor, Project project) {
            mySymbolVisitor = symbolVisitor;
            myThisNamespace = myFileNamespace = myNamespace = namespace;
            myIndex = TeaIndex.getInstance(project);
            myTypeEvaluateManager = TeaTypeEvaluateManager.getInstance(project);
        }

        public void processNamespace(TeaNamespace ns) {
            if (ns.getNameId() == -1) currentNamespace = myFileNamespace;
            else currentNamespace = currentNamespace.getChildNamespace(ns.getNameId());
        }

        public void processExpression(TeaReferenceExpression expr) {
            if (expr != topExpression) {
                final int referencedNamedId = myIndex.getIndexOf(expr.getReferencedName());
                currentNamespace = ((currentNamespace != null) ? currentNamespace : myFileNamespace).getChildNamespace(referencedNamedId);
            }
        }

        public void processUnresolvedThis() {
            currentNamespace = myFileNamespace.getChildNamespace(myIndex.getIndexOf(""));
        }

        public boolean isTopLevel(final TeaReferenceExpression expression) {
            return expression == topExpression;
        }

        private TeaNamespace findNamespace(TeaElement _expr, TeaNamespace contextNamespace) {
            TeaElement nameComponent = findNameComponent(_expr);
            TeaReferenceExpression expr = null;

            if (nameComponent instanceof TeaVariable) {
                return myFileNamespace;
            }

            if (nameComponent instanceof TeaReferenceExpression) {
                expr = (TeaReferenceExpression) nameComponent;
            }

            if (expr != null) {
                currentNamespace = null;
                topExpression = _expr;
                visitReferenceExpressionComponentsInRootFirstOrder(expr, contextNamespace, this);
                topExpression = null;
                if (currentNamespace != null) return currentNamespace;
            }

            return myFileNamespace;
        }

        public void visitTeaTemplateDeclaration(final TeaTemplate node) {
            final String name = node.getName();

            if (name != null) {
                int nameId = myIndex.getIndexOf(name);
                if (node.getParent() instanceof PsiFile) { // global function declaration
                    mySymbolVisitor.processTemplate(myNamespace, nameId, node);
                }
                processFunctionBody(myNamespace.getChildNamespace(nameId), node);
            }
        }

//      public void visitTeaVarStatement(final TeaVarStatement node) {
//        node.acceptChildren(this);
//      }

        public void visitTeaVariable(final TeaVariable node) {
            final int nameId = myIndex.getIndexOf(node.getName());
            if (myFunction == null) {
                mySymbolVisitor.processVariable(myNamespace, nameId, node);
            }

            final TeaExpression initializer = node.getInitializer();
            if (initializer != null) {
                visitWithNamespace(myNamespace.getChildNamespace(nameId), initializer, false);
            }
        }

        private void processReferenceExpression(TeaReferenceExpression element, TeaExpression rOperand) {
            final PsiElement parent = element.getParent();
            if (!(parent instanceof TeaNamedElement)) return; // some wrong tree
            TeaNamespace namespace = findNamespace(element, myThisNamespace);

//        if (namespace.getParent() == null && myInsideWithStatement) {
//          namespace = namespace.getChildNamespace( myIndex.getIndexOf( "" ));
//        }

            final String name = element.getReferencedName();
            final int nameId = myIndex.getIndexOf(name);

            try {
                if (element.getQualifier() == null && myFunction != null) {
                    final TeaElement jsElement = TeaResolveUtil.treeWalkUp(
                            new ResolveProcessor(name),
                            element,
                            element.getParent(),
                            element
                    );

                    if (jsElement != null) return;
                }

                mySymbolVisitor.processDefinition(
                        namespace, nameId, (TeaNamedElement) parent
                );
            }
            finally {
                if (rOperand != null) {
                    visitWithNamespace(getNestedNsWithName(name, namespace), rOperand, true);
                }
            }
        }

        public void visitTeaDefinitionExpression(final TeaDefinitionExpression node) {
        }

        public void visitTeaBinaryExpression(final TeaBinaryExpression node) {
        }

        public void visitXmlDocument(XmlDocument element) {
            element.acceptChildren(this);
        }

        public void visitXmlTag(XmlTag element) {
            String name = element.getAttributeValue("name");
            if (name != null) {
                TeaNamespace ns = myNamespace;
                if (ns == myFileNamespace) ns = myFileNamespace.getChildNamespace(myIndex.getIndexOf("document"));
                int nameId = myIndex.getIndexOf(name);
                mySymbolVisitor.processTag(ns, nameId, element, "name");
                visitWithNamespace(ns.getChildNamespace(nameId), element, true);
            } else {
                name = element.getAttributeValue("id");
                if (name != null) {
                    int nameId = myIndex.getIndexOf(name);
                    mySymbolVisitor.processTag(myNamespace, nameId, element, "id");
                }

                //name = element.getAttributeValue("class");
                //if (name != null) {
                //  int nameId = myIndex.getIndexOf(name);
                //  mySymbolVisitor.processTag(myNamespace, nameId, element, "class");
                //}

                element.acceptChildren(this);
            }
        }

        public void visitTeaElement(TeaElement element) {
            element.acceptChildren(this);
        }

//        public void visitTeaObjectLiteralExpression(final TeaObjectLiteralExpression node) {
//            TeaNamespace namespace = myNamespace;
//
//            if (namespace.getParent() == null) {
//                // Find some ns that is used for extending
//                TeaNamespace candidateNs = null;
//
//                final PsiElement parent = node.getParent();
//                if (parent instanceof TeaArgumentList) {
//                    for (TeaExpression expr : ((TeaArgumentList) parent).getArguments()) {
//                        if (expr instanceof TeaReferenceExpression) {
//                            candidateNs = findNsForExpr(expr);
//                            break;
//                        }
//                    }
//                }
//
//                if (candidateNs == null) candidateNs = namespace.getChildNamespace(myIndex.getIndexOf(""));
//                namespace = candidateNs;
//            }
//
//            visitWithNamespace(namespace, node, true);
//        }

        private TeaNamespace findNsForExpr(final TeaExpression expr) {
            TeaNamespace candidateNs = findNamespace(expr, null);

            if (candidateNs != null) {
                String name = null;
                if (expr instanceof TeaReferenceExpression)
                    name = ((TeaReferenceExpression) expr).getReferencedName();

                if (name != null) candidateNs = getNestedNsWithName(name, candidateNs);
            }
            return candidateNs;
        }

        private TeaNamespace getNestedNsWithName(final String name, TeaNamespace candidateNs) {
            if (!PROTOTYPE_FIELD_NAME.equals(name)) {
                candidateNs = candidateNs.getChildNamespace(myIndex.getIndexOf(name));
            }
            return candidateNs;
        }

        private void visitWithNamespace(final TeaNamespace namespace, final PsiElement node, boolean fromChildren) {
            final TeaNamespace previousNamespace = myNamespace;
            try {
                myNamespace = namespace;
                if (fromChildren) node.acceptChildren(this);
                else node.accept(this);
            } finally {
                myNamespace = previousNamespace;
            }
        }

        public void visitTeaProperty(final TeaProperty node) {
            final TeaExpression value = node.getValue();

            String name = node.getName();
            final int nameId = myIndex.getIndexOf(name);

            if (value instanceof TeaTemplate) {
                final TeaTemplate function = (TeaTemplate) value;
                mySymbolVisitor.processTemplate(myNamespace, nameId, function);
                final TeaNamespace childNs = nameId != -1 ? myNamespace.getChildNamespace(nameId) : myNamespace;
                processFunctionBody(childNs, function);
            } else if (value != null) {
                mySymbolVisitor.processProperty(myNamespace, nameId, node);
            }
        }

        public void visitTeaCallExpression(final TeaCallExpression node) {
            final TeaExpression methodExpression = node.getMethodExpression();

            if (methodExpression instanceof TeaReferenceExpression /*&&
            !(node instanceof TeaNewExpression)*/
                    ) {
                final TeaReferenceExpression referenceExpression = (TeaReferenceExpression) methodExpression;
                final TeaExpression qualifier = referenceExpression.getQualifier();

                if ("call".equals(referenceExpression.getReferencedName()) &&
                        qualifier != null &&
                        myFunction != null
                        ) {
                    final TeaExpression[] jsExpressions = node.getArgumentList().getArguments();

                    if (jsExpressions.length == 1) {
//              for(TeaExpression expr: jsExpressions) {
//                if (expr instanceof TeaThisExpression) {
//                  final TeaNamespace namespace = findNsForExpr(qualifier);
//                  //System.out.println(
//                  //  myThisNamespace.getQualifiedName(myIndex) + "," + myFunction.getName() + "," + qualifier.getText() + "," +
//                  //                   namespace.getQualifiedName(myIndex)
//                  //);
//                  myTypeEvaluateManager.setBaseType(myThisNamespace,
//                    myFunction.getName(), namespace,
//                    qualifier.getText()
//                  );
//                  break;
//                }
//              }
                    }
                } else if (qualifier == null && myFunction == null) {
                    final TeaExpression[] jsExpressions = node.getArgumentList().getArguments();
                    if (jsExpressions.length == 2 &&
                            jsExpressions[0] instanceof TeaReferenceExpression &&
                            jsExpressions[1] instanceof TeaReferenceExpression
                            ) {
                        final TeaNamespace namespace = findNsForExpr(jsExpressions[0]);
                        final TeaNamespace namespace2 = findNsForExpr(jsExpressions[1]);
                        //System.out.println(
                        //  namespace.getQualifiedName(myIndex) + "," + jsExpressions[0].getText() + "," + jsExpressions[1].getText() + "," +
                        //                   namespace2.getQualifiedName(myIndex)
                        //);
                        myTypeEvaluateManager.setBaseType(namespace,
                                jsExpressions[0].getText(), namespace2,
                                jsExpressions[1].getText()
                        );
                    }
                }
            }

            super.visitTeaCallExpression(node);
        }

//        public void visitTeaAssignmentStatement(final TeaAssignmentStatement node) {
//            TeaExpression _lOperand = node.getLOperand();
//            if (_lOperand instanceof TeaDefinitionExpression)
//                _lOperand = ((TeaDefinitionExpression) _lOperand).getExpression();
//
//            if (_lOperand instanceof TeaReferenceExpression) {
//                final TeaReferenceExpression lOperand = (TeaReferenceExpression) _lOperand;
//                final TeaExpression rOperand = node.getROperand();
//                final TeaExpression lqualifier = lOperand.getQualifier();
//
//                if (rOperand instanceof TeaTemplate) {
//                    final TeaNamespace namespace = findNamespace(lOperand, myThisNamespace);
//                    final int nameId = myIndex.getIndexOf(lOperand.getReferencedName());
//                    final TeaNamespace childNamespace = namespace.getChildNamespace(nameId);
//
//                    if (lqualifier instanceof TeaReferenceExpression) {
//                        final TeaTemplate function = (TeaTemplate) rOperand;
//                        mySymbolVisitor.processTemplate(namespace, nameId, function);
//                        processFunctionBody(childNamespace, function);
//                    } else {
//                        mySymbolVisitor.processTemplate(namespace, nameId, (TeaTemplate) rOperand);
//                        processFunctionBody(childNamespace, (TeaTemplate) rOperand);
//                    }
////                } else if (rOperand instanceof TeaObjectLiteralExpression) {
////                    TeaObjectLiteralExpression literalExpr = (TeaObjectLiteralExpression) rOperand;
////
////                    if (PROTOTYPE_FIELD_NAME.equals(lOperand.getReferencedName()) &&
////                            lqualifier instanceof TeaReferenceExpression) {
////                        final TeaNamespace namespace = findNamespace(lOperand, null);
////
////                        for (TeaProperty prop : literalExpr.getProperties()) {
////                            final TeaExpression expression = prop.getValue();
////                            final int nameId = myIndex.getIndexOf(prop.getName());
////
////                            if (expression instanceof TeaTemplate) {
////                                mySymbolVisitor.processTemplate(namespace, nameId, (TeaTemplate) expression);
////                                processFunctionBody(namespace.getChildNamespace(nameId), (TeaTemplate) expression);
////                            } else if (expression != null) {
////                                mySymbolVisitor.processProperty(namespace, nameId, prop);
////                            }
////                        }
////                    } else {
////                        processReferenceExpression(lOperand, rOperand);
////                    }
//                } else if (rOperand instanceof TeaCallExpression) {
////            if (PROTOTYPE_FIELD_NAME.equals(lOperand.getReferencedName()) &&
////                lqualifier instanceof TeaReferenceExpression) {
////
////              TeaExpression methodExpression = null;
////              if (rOperand instanceof TeaNewExpression) {
////                methodExpression = ((TeaNewExpression)rOperand).getMethodExpression();
////              } else {
////                for(TeaExpression expr:((TeaCallExpression)rOperand).getArgumentList().getArguments()) {
////                  if (expr instanceof TeaNewExpression) {
////                    methodExpression = ((TeaNewExpression)expr).getMethodExpression();
////                    break;
////                  }
////                }
////              }
////
////              if (methodExpression instanceof TeaReferenceExpression) {
////                final TeaNamespace subTypeNS = findNamespace(lOperand, myThisNamespace);
////                final String superType = methodExpression.getText();
////                TeaNamespace superNs = findNamespace(methodExpression,null);
////                superNs = superNs.getChildNamespace( myIndex.getIndexOf(((TeaReferenceExpression)methodExpression).getReferencedName()) );
////
////                myTypeEvaluateManager.setBaseType(
////                  subTypeNS,
////                  lqualifier.getText(),
////                  superNs,
////                  superType
////                );
////              }
////            }
//                    processReferenceExpression(lOperand, rOperand);
//                } else if (!(rOperand instanceof TeaLiteralExpression) || !"null".equals(rOperand.getText())) {
//                    processReferenceExpression(lOperand, rOperand);
//                }
//            } else {
////          int a = 1;
//            }
//        }

//      public void visitTeaTemplateExpression(final TeaTemplateExpression node) {
//        processFunctionBody(myNamespace,node.getFunction());
//      }

        private void processFunctionBody(final TeaNamespace namespace, final TeaTemplate node) {
            ProgressManager.getInstance().checkCanceled();
            final TeaNamespace previousNamespace = myNamespace;
            final TeaTemplate previousFunction = myFunction;
            final TeaNamespace previousThisNamespace = myThisNamespace;

            myThisNamespace = namespace;

//        if (node instanceof TeaTemplateExpression) {
//          final PsiElement parentElement = node.getParent();
//          if (parentElement instanceof TeaAssignmentStatement) {
//            final TeaExpression expression = ((TeaDefinitionExpression)((TeaAssignmentStatement)parentElement).getLOperand()).getExpression();
//
//            if (expression instanceof TeaReferenceExpression) {
//              final TeaExpression qualifier = ((TeaReferenceExpression)expression).getQualifier();
//
//              if (qualifier instanceof TeaReferenceExpression) {
//                final String referencedName = ((TeaReferenceExpression)qualifier).getReferencedName();
//
//                if (PROTOTYPE_FIELD_NAME.equals(referencedName)) {
//                  myThisNamespace = myThisNamespace.getParent();
//                }
//              }
//              if (qualifier instanceof TeaThisExpression) {
//                myThisNamespace = myThisNamespace.getParent();
//              }
//            }
//          } else if (parentElement instanceof TeaProperty ||
//                     parentElement instanceof TeaReferenceExpression
//                    ) {
//            myThisNamespace = myThisNamespace.getParent();
//          }
//        }

            myNamespace = namespace;
            myFunction = node;
            try {
                for (TeaSourceElement srcElement : node.getBody()) {
                    srcElement.acceptChildren(this);
                }
            } finally {
                myNamespace = previousNamespace;
                myFunction = previousFunction;
                myThisNamespace = previousThisNamespace;
            }
        }
    }
}
