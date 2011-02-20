package com.go.teacup.intellij.lang.tea.psi.resolve;

import com.go.teacup.intellij.lang.tea.index.TeaNamedElementProxy;
import com.go.teacup.intellij.lang.tea.index.TeaNamespace;
import com.go.teacup.intellij.lang.tea.index.TeaTypeEvaluateManager;
import com.go.teacup.intellij.lang.tea.lexer.TeaTokenTypes;
import com.go.teacup.intellij.lang.tea.psi.*;
import com.go.teacup.intellij.lang.tea.psi.util.TeaLookupUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.Processor;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;
import org.jetbrains.annotations.NonNls;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 5:19:48 PM
 */
public class VariantsProcessor extends BaseTeaSymbolProcessor implements PsiScopeProcessor {
    private TIntObjectHashMap<PsiNamedElement> myNames2CandidatesMap = new TIntObjectHashMap<PsiNamedElement>();
    private int myThisFileNameListCount;
    private List<Object> myNamesList = new ArrayList<Object>();

    private TIntObjectHashMap<Object> myPartialMatchNamesMap = new TIntObjectHashMap<Object>();
    private TIntObjectHashMap<Object> myPartialMatchNamesMapFromSameFile = new TIntObjectHashMap<Object>();
    //private Set<PsiFile> myTargetFiles;
    private final boolean hasSomeSmartnessAvailable;
    private final boolean hasSomeInfoAvailable;
    private int[][] myNameIdsArray;
    private boolean myProcessOnlyProperties;
    @NonNls
    private static final String ARRAY_TYPE_NAME = "Array";
    @NonNls private static final String STRING_TYPE_NAME = "String";
    @NonNls private static final String NUMBER_TYPE_NAME = "Number";
    private boolean myDefinitelyGlobalReference;

    public VariantsProcessor(int[] nameIds, PsiFile targetFile, boolean skipDclsInTargetFile, PsiElement context) {
      super(targetFile.getOriginalFile() != null ? targetFile.getOriginalFile():targetFile,skipDclsInTargetFile, context);

      myProcessOnlyProperties = context instanceof TeaProperty;
      hasSomeInfoAvailable = nameIds != null && nameIds.length > 0;
      List<int[]> possibleNameComponents = new ArrayList<int[]>(1);
      myCurrentFile = targetFile.getOriginalFile();
      //myTargetFiles = new HashSet<PsiFile>();

      boolean allTypesResolved = false;

      final Set<String> visitedTypes = new HashSet<String>();

      if (context instanceof TeaReferenceExpression) {
        final TeaReferenceExpression refExpr = (TeaReferenceExpression)context;
        TeaExpression qualifier = refExpr.getQualifier();

        if (qualifier != null) {
          allTypesResolved = doEvalForExpr(qualifier, targetFile.getProject(), possibleNameComponents, visitedTypes);
        }
      }

      if (hasSomeInfoAvailable && !allTypesResolved) {
        possibleNameComponents.add(nameIds);

        StringBuilder builder = new StringBuilder();
        for (int nameId : nameIds) {
          if (builder.length() > 0) builder.append('.');
          builder.append(myIndex.getStringByIndex(nameId));
        }

        addDescendants(
          TeaTypeEvaluateManager.getInstance( targetFile.getProject() ),
          builder.toString(),
          possibleNameComponents,
          visitedTypes
        );
      }

      myNameIdsArray = new int[possibleNameComponents.size()][];
      possibleNameComponents.toArray(myNameIdsArray);

      hasSomeSmartnessAvailable = myNameIdsArray != null && myNameIdsArray.length > 0;

      if (!myProcessOnlyProperties) {
        if (nameIds != null &&
            ( nameIds.length == 0 ||
              (nameIds.length == 1 && nameIds[0] == myWindowIndex)
            )/* &&
            PsiTreeUtil.getParentOfType(context, TeaWithStatement.class) == null*/
           ) {
          myDefinitelyGlobalReference = true;
        }
      }
    }

    private boolean doEvalForExpr(TeaExpression rawqualifier, final Project project, final List<int[]> possibleNameIds,Set<String> visitedTypes) {
      boolean allTypesResolved = true;

        if (rawqualifier instanceof TeaCallExpression) {
        rawqualifier = ((TeaCallExpression)rawqualifier).getMethodExpression();
      }

      final TeaTypeEvaluateManager typeEvaluateManager = TeaTypeEvaluateManager.getInstance(project);

      if (rawqualifier instanceof TeaReferenceExpression) {
        TeaReferenceExpression qualifier = ((TeaReferenceExpression)rawqualifier);
        final TextRange textRange = qualifier.getTextRange();
        PsiFile targetFile = rawqualifier.getContainingFile();
        final PsiFile originalFile = targetFile.getOriginalFile();

        if (!targetFile.isPhysical() && originalFile != null) {
          PsiElement startElement = originalFile.findElementAt(qualifier.getTextOffset());

          do {
            qualifier = PsiTreeUtil.getParentOfType(
              startElement,
              TeaReferenceExpression.class
            );
            if (qualifier == null) break;
            startElement = qualifier;
          } while(!textRange.equals(qualifier.getTextRange()));
        }

        final ResolveResult[] resolveResults = qualifier.multiResolve(false);

        for(ResolveResult r:resolveResults) {
          PsiElement psiElement = r.getElement();
          if (psiElement == qualifier.getParent()) continue;
          String type = typeEvaluateManager.getElementType((TeaNamedElement)psiElement);

          if (type == null) {
            if (psiElement instanceof TeaVariable) {
              final TeaExpression expression = ((TeaVariable)psiElement).getInitializer();
              if (expression != null) {
                allTypesResolved &= doEvalForExpr(expression, project, possibleNameIds, visitedTypes);
              } else if (psiElement instanceof TeaParameter) {
                PsiElement prevSlibling = psiElement.getPrevSibling();

                if (prevSlibling instanceof PsiWhiteSpace) prevSlibling = prevSlibling.getPrevSibling();

                if (prevSlibling instanceof PsiComment &&
                    ((PsiComment)prevSlibling).getTokenType() == TeaTokenTypes.C_STYLE_COMMENT) {
                  String s = prevSlibling.getText();
                  s = s.substring(2,s.length() - 2);

                  if (s.length() > 0) addType(s, possibleNameIds, typeEvaluateManager,visitedTypes);
                }
              }
            } else {
              if (psiElement instanceof TeaNamedElementProxy &&
                  psiElement.getContainingFile() == myTargetFile
                 ) {
                psiElement = ((TeaNamedElementProxy)psiElement).getElement();
              }
              if (psiElement instanceof TeaDefinitionExpression) {
                type = psiElement.getText();

                if (!visitedTypes.contains(type)) {
                  visitedTypes.add(type);
//                  final PsiElement parentElement = psiElement.getParent();
//                  if (parentElement instanceof TeaAssignmentStatement) {
//                    TeaExpression expr = ((TeaAssignmentStatement)parentElement).getROperand();
//                    while(expr instanceof TeaAssignmentStatement) expr = ((TeaAssignmentStatement)expr).getROperand();
//                    allTypesResolved &= doEvalForExpr(expr, project, possibleNameIds, visitedTypes);
//                  } else {
//                    allTypesResolved = false;
//                  }
                }
              } else if (psiElement instanceof XmlToken) {
                addType("HTMLElement", possibleNameIds, typeEvaluateManager,visitedTypes);
              } else if (psiElement instanceof TeaNamedElement) {
                addType(((TeaNamedElement)psiElement).getName(), possibleNameIds, typeEvaluateManager,visitedTypes);
              }
              allTypesResolved = false;
            }
            //myTargetFiles.add(psiElement.getContainingFile());
          } else if (!visitedTypes.contains(type)) {
            if (typeEvaluateManager.isArrayType(type)) type = ARRAY_TYPE_NAME;
            addType(type,possibleNameIds, typeEvaluateManager, visitedTypes);
          }
        }

        if ("$".equals(qualifier.getText())) {
          addType("HTMLElement", possibleNameIds, typeEvaluateManager,visitedTypes);
        }

        /*if (originalQualifier instanceof TeaNewExpression) {
          possibleNameIds.add(
            TeaSymbolUtil.buildNameIndexArray(qualifier,null, myIndex)
          );
          addDescendants(typeEvaluateManager, qualifier.getText(), possibleNameIds, visitedTypes);
        }*/
//      } else if (rawqualifier instanceof TeaArrayLiteralExpression) {
//        addType(ARRAY_TYPE_NAME, possibleNameIds, typeEvaluateManager, visitedTypes);
      } else if (rawqualifier instanceof TeaLiteralExpression) {
        final String text = rawqualifier.getText();

        addType(
          StringUtil.startsWithChar(text,'"') || StringUtil.startsWithChar(text,'\'')? STRING_TYPE_NAME:NUMBER_TYPE_NAME,
          possibleNameIds, typeEvaluateManager, visitedTypes
        );
      } else {
        allTypesResolved = false;
      }

      return allTypesResolved;
    }


    private void addType(String type, final List<int[]> possibleNameIds,
                         final TeaTypeEvaluateManager typeEvaluateManager, Set<String> visitedTypes) {
      type = typeEvaluateManager.getInstanceNameByType(type);
      if (visitedTypes.contains(type)) return;
      visitedTypes.add(type);
      possibleNameIds.add(new int[] { myIndex.getIndexOf( type ) });
      addDescendants(typeEvaluateManager, type, possibleNameIds, visitedTypes);
    }

    private void addDescendants(final TeaTypeEvaluateManager typeEvaluateManager,
                                final String type, final List<int[]> possibleNameIds,
                                final Set<String> visitedTypes) {
      typeEvaluateManager.iterateTypeHierarchy(
        type,
        new Processor<TeaNamespace>() {
          public boolean process(final TeaNamespace element) {
            final String qname = element.getQualifiedName(myIndex);
            if (!visitedTypes.contains(qname)) {
              visitedTypes.add(qname);
              possibleNameIds.add( element.getIndices() );
            }
            return true;
          }
        }
      );
    }

    public Object[] getResult() {
      int resultCount = myNamesList.size() + myPartialMatchNamesMap.size() + myPartialMatchNamesMapFromSameFile.size();
      Object[] results = new Object[resultCount];
      for(int i = 0; i < myNamesList.size(); ++i) {
        results[i] = myNamesList.get( i );
      }

      int offset = myNamesList.size();
      TIntObjectIterator<Object> values = myPartialMatchNamesMapFromSameFile.iterator();
      for(int i = 0; i < myPartialMatchNamesMapFromSameFile.size(); ++i) {
        values.advance();
        results[i + offset] = values.value();
      }

      offset += myPartialMatchNamesMapFromSameFile.size();
      values = myPartialMatchNamesMap.iterator();
      for(int i = 0; i < myPartialMatchNamesMap.size(); ++i) {
        values.advance();
        results[i + offset] = values.value();
      }

      return results;
    }

    public boolean execute(PsiElement element, ResolveState resolveState) {
      if (element instanceof TeaNamedElement) {
        final TeaNamedElement namedElement = (TeaNamedElement)element;

        addCompleteMatch(namedElement, myIndex.getIndexOf(namedElement.getName()));
      }

      return true;
    }

    public <T> T getHint(Key<T> hintKey) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T> T getHint(Class<T> hintClass) {
      return null;
    }

    public void handleEvent(Event event, Object associated) {
    }

    public void processTemplate(TeaNamespace namespace, final int nameId, TeaNamedElement function) {
      if (myCurrentFile != myTargetFile ||
          myDefinitelyGlobalReference ||
          ( ( function instanceof TeaNamedElementProxy &&
            ((TeaNamedElementProxy)function).getType() != TeaNamedElementProxy.NamedItemType.Template
          )/* ||
            function instanceof TeaFunctionExpression*/
          )
         ) {
        doAdd(namespace, nameId, function);
      }
    }

    public void processProperty(TeaNamespace namespace, final int nameId, TeaNamedElement property) {
      doAdd(namespace, nameId, property);
    }

    public void processDefinition(final TeaNamespace namespace, final int nameId, final TeaNamedElement refExpr) {
      doAdd(namespace, nameId, refExpr);
    }

    public int getRequiredNameId() {
      return -1;
    }

    public void processTag(TeaNamespace namespace, final int nameId, PsiNamedElement namedElement, final String attrName) {
      doAdd(namespace, nameId, namedElement);
    }

    public void processVariable(TeaNamespace namespace, final int nameId, TeaNamedElement variable) {
      if (myCurrentFile != myTargetFile ||
          myDefinitelyGlobalReference
        ) {
        doAdd(namespace, nameId, variable);
      }
    }

    public PsiFile getBaseFile() {
      return myTargetFile;
    }

    private void doAdd(TeaNamespace namespace, final int nameId, final PsiNamedElement element) {
      final String name = nameId != -1 ? myIndex.getStringByIndex( nameId ):null;

      if (name != null && name.length() > 0 &&
          name.charAt(0) == '_' &&
          !(myTargetFile instanceof TeaFile)
        ) {
        // no interest in html, jsp for private symbols
        return;
      }

      if (myProcessOnlyProperties) {
        if (element instanceof TeaNamedElementProxy) {
          final TeaNamedElementProxy.NamedItemType type = ((TeaNamedElementProxy)element).getType();

          if (type != TeaNamedElementProxy.NamedItemType.Property &&
              type != TeaNamedElementProxy.NamedItemType.Definition
             ) {
            return; // no interest when complete existing property name
          }
        }
      }

      final MatchType matchType = isAcceptableQualifiedItem(namespace);

      if ( matchType == MatchType.PARTIAL ) {
        addPartialMatch(element, nameId);
      } else if (matchType == MatchType.COMPLETE) {
        addCompleteMatch(element, nameId);
      }
    }

    enum LookupPriority {
      HIGHEST,
      HIGH,
      HIGHER,
      NORMAL
    }

    private Object addLookupValue(PsiElement element, LookupPriority priority) {
      if (element instanceof TeaNamedElementProxy &&
          element.getContainingFile() == myTargetFile
        ) { // expand proxies from completion target file, since they will be invalidated by typing
        element = ((TeaNamedElementProxy)element).getElement();
      }

      return TeaLookupUtil.getInstance().createPrioritizedLookupItem(
        element,
        element instanceof PsiNamedElement ? ((PsiNamedElement)element).getName():element.getText(),
        priority == LookupPriority.HIGHEST?3:
          (priority == LookupPriority.HIGH ? 2:
             (priority == LookupPriority.HIGHER ? 1:0))
      );
    }


    protected boolean isFromRelevantFileOrDirectory() {
      return super.isFromRelevantFileOrDirectory(); // || myTargetFiles.contains(myCurrentFile);
    }

    private void addCompleteMatch(final PsiNamedElement element, int nameId) {
      if (!doAdd(element, nameId)) {
        boolean removedFromPartialNames = false;
        final PsiNamedElement el = myNames2CandidatesMap.get(nameId);

        if (el != null) {
          removedFromPartialNames = myPartialMatchNamesMapFromSameFile.remove(nameId) != null ||
                                    myPartialMatchNamesMap.remove(nameId) != null;
        }
        if (!removedFromPartialNames) return;
      }

      if (isFromRelevantFileOrDirectory()) {
        final Object o = addLookupValue(element, LookupPriority.HIGHEST);
        if (o != null) myNamesList.add(myThisFileNameListCount++, o);
        else myNames2CandidatesMap.remove(nameId);
      } else {
        final Object o = addLookupValue(element, LookupPriority.HIGH);
        if (o != null) myNamesList.add(o);
        else myNames2CandidatesMap.remove(nameId);
      }
    }

    private boolean doAdd(PsiNamedElement element, int nameId) {
      if (nameId == -1 || myNames2CandidatesMap.get(nameId) != null) return false;
      myNames2CandidatesMap.put( nameId, element );
      return true;
    }

    private void addPartialMatch(final PsiNamedElement element, int nameId) {
      if (!doAdd(element, nameId)) return;

      final TIntObjectHashMap<Object> targetNamesMap;
      final LookupPriority priority;

      if (isFromRelevantFileOrDirectory()) {
        priority = hasSomeSmartnessAvailable ? LookupPriority.HIGHER: LookupPriority.HIGHEST;
        targetNamesMap = myPartialMatchNamesMapFromSameFile;
      } else {
        priority = hasSomeSmartnessAvailable ? LookupPriority.NORMAL: LookupPriority.HIGH;

        targetNamesMap = myPartialMatchNamesMap;
      }

      final Object o = addLookupValue(element,priority);

      if (o != null) targetNamesMap.put(nameId, o);
      else myNames2CandidatesMap.remove(nameId);
    }

    private MatchType isAcceptableQualifiedItem(TeaNamespace namespace) {
      if (!hasSomeInfoAvailable || myDefinitelyGlobalReference) {
        return isGlobalNS(namespace) ? MatchType.COMPLETE : (myDefinitelyGlobalReference? MatchType.NOMATCH:MatchType.PARTIAL);
      }

      if (namespace.getParent() == null) return MatchType.NOMATCH;

      for(int[] myNameComponents: myNameIdsArray) {
        boolean completeMatch = true;
        TeaNamespace currentNs = namespace;

        for(int i = myNameComponents.length - 1; i >= 0; --i) {
          if (myNameComponents[i] == currentNs.getNameId()) {
            currentNs = currentNs.getParent();
            if (currentNs != null) continue;
          }
          completeMatch = false;
          break;
        }

        if (completeMatch && currentNs != namespace) {
          return MatchType.COMPLETE;
        }
      }

      return MatchType.PARTIAL;
    }
}
