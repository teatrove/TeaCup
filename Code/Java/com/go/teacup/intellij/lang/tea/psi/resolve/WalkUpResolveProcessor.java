package com.go.teacup.intellij.lang.tea.psi.resolve;

import com.go.teacup.intellij.lang.tea.index.TeaNamedElementProxy;
import com.go.teacup.intellij.lang.tea.index.TeaNamespace;
import com.go.teacup.intellij.lang.tea.psi.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 5:03:46 PM
 */
public class WalkUpResolveProcessor extends BaseTeaSymbolProcessor {
    private String myText;
    protected int[] myNameIds;
    private int myFilePartialResultsCount;
    private List<ResolveResult> myPartialMatchResults;
    private int myFileCompleteResultsCount;
    private List<ResolveResult> myCompleteMatchResults;
    private boolean myDefinitelyGlobalReference;
    private boolean myDefinitelyNonglobalReference;

    public WalkUpResolveProcessor(String text,
                              int[] nameIds,
                              PsiFile targetFile,
                              boolean skipDclsInTargetFile,
                              PsiElement context
                              ) {
      super(targetFile,skipDclsInTargetFile,context);
      myText = text;
      myNameIds = nameIds;

      if (myNameIds.length == 1 &&
          myContext instanceof TeaReferenceExpression &&
          ((TeaReferenceExpression)myContext).getQualifier() == null /*&&
          PsiTreeUtil.getParentOfType(myContext, TeaWithStatement.class) == null*/
         ) {
        myDefinitelyGlobalReference = true;
      }

      if (myNameIds.length > 2 ||
          ( myNameIds.length == 2 &&
            myIndex.getStringByIndex(myNameIds[0]).length() > 0 &&
            myNameIds[0] != myWindowIndex
          )
        ) {
        myDefinitelyNonglobalReference = true;
      }
    }

    public void processTemplate(TeaNamespace namespace, final int nameId, final TeaNamedElement function) {
      doQualifiedCheck(namespace, nameId, function);
    }

    protected MatchType isAcceptableQualifiedItem(TeaNamespace namespace, final int nameId) {
      boolean partialMatch = myNameIds[myNameIds.length - 1] == nameId;
      if (partialMatch) {
        int i;
        for(i = myNameIds.length - 2; i >= 0; --i) {
          if (namespace == null) break;
          if (namespace.getNameId() != myNameIds[i]) break;
          namespace = namespace.getParent();
        }
        if (i < 0 && isGlobalNS(namespace)) return MatchType.COMPLETE;
      }

      return  partialMatch ? MatchType.PARTIAL: MatchType.NOMATCH;
    }

    private void doQualifiedCheck(final TeaNamespace namespace, int nameId, final PsiNamedElement element) {
      final MatchType matchType = isAcceptableQualifiedItem(namespace, nameId);

      if ( matchType == MatchType.PARTIAL ) {
        if (myDefinitelyGlobalReference &&
            ( ( element instanceof TeaNamedElementProxy &&
               ( ((TeaNamedElementProxy)element).getType() == TeaNamedElementProxy.NamedItemType.FunctionProperty ||
                 ((TeaNamedElementProxy)element).getType() == TeaNamedElementProxy.NamedItemType.Property ||
                 ( ((TeaNamedElementProxy)element).getType() == TeaNamedElementProxy.NamedItemType.Definition && !isGlobalNS(namespace))
               )
              ) ||
              ( /*element instanceof TeaFunctionExpression ||*/
                element instanceof TeaProperty ||
                ( element instanceof TeaDefinitionExpression && !isGlobalNS(namespace))
              )
            )
          ) {
          return; // nonqualified item could not be resolved into property
        }

        if (myDefinitelyNonglobalReference &&
              ( element instanceof TeaNamedElementProxy &&
                ( ((TeaNamedElementProxy)element).getType() == TeaNamedElementProxy.NamedItemType.Variable ||
                  ((TeaNamedElementProxy)element).getType() == TeaNamedElementProxy.NamedItemType.Template
                )
              ) ||
              ( element instanceof TeaVariable ||
                ( element instanceof TeaTemplate /*&& !(element instanceof TeaFunctionExpression)*/)
              )
          ) {
          return; // qualified item could not be resolved into function/variable
        }
        addPartialResult(element);
      } else if (matchType == MatchType.COMPLETE) {
        addCompleteResult(element);
      }
    }

    private void addCompleteResult(PsiElement element) {
      if (myCompleteMatchResults == null) myCompleteMatchResults = new ArrayList<ResolveResult>(1);
      final TeaResolveUtil.MyResolveResult o = new TeaResolveUtil.MyResolveResult(element);
      if (isFromRelevantFileOrDirectory()) {
        myCompleteMatchResults.add(myFileCompleteResultsCount++, o);
      } else {
        myCompleteMatchResults.add( o );
      }
    }

    private void addPartialResult(PsiElement element) {
      if (myPartialMatchResults == null) myPartialMatchResults = new ArrayList<ResolveResult>(1);
      final TeaResolveUtil.MyResolveResult o = new TeaResolveUtil.MyResolveResult(element);

      if (isFromRelevantFileOrDirectory()) {
        myPartialMatchResults.add(myFilePartialResultsCount++, o);
      } else {
        myPartialMatchResults.add( o );
      }
    }

    public void processProperty(TeaNamespace namespace, final int nameId, TeaNamedElement property) {
      doQualifiedCheck(namespace, nameId, property);
    }

    public void processVariable(TeaNamespace namespace, final int nameId, TeaNamedElement variable) {
      if (shouldProcessVariable(nameId)) {
        addCompleteResult(variable);
      }
    }

    public PsiFile getBaseFile() {
      return myTargetFile;
    }

    public void processDefinition(final TeaNamespace namespace, final int nameId, final TeaNamedElement refExpr) {
      //if(myCurrentFile != myTargetFile || !mySkipDclsInTargetFile)
      doQualifiedCheck(namespace, nameId, refExpr);
    }

    @Nullable
    public int getRequiredNameId() {
      return myNameIds[myNameIds.length - 1];
    }

    public void processTag(TeaNamespace namespace, final int nameId, PsiNamedElement namedElement, final String attrName) {
      doQualifiedCheck(namespace, nameId, namedElement);
    }

    protected boolean shouldProcessVariable(final int nameId) {
      return ( !myDefinitelyNonglobalReference &&
                 myNameIds[myNameIds.length - 1] == nameId
             );
    }

    public ResolveResult[] getResults() {
      int resultCount = 0;
      if (myCompleteMatchResults != null) resultCount += myCompleteMatchResults.size();
      if (myPartialMatchResults != null) resultCount += myPartialMatchResults.size();

      final ResolveResult[] result = resultCount != 0 ? new ResolveResult[resultCount]:ResolveResult.EMPTY_ARRAY;

      if (myCompleteMatchResults != null) {
        for(int i = 0; i < myCompleteMatchResults.size(); ++i) {
          result[i] = myCompleteMatchResults.get( i );
        }
      }

      if (myPartialMatchResults != null) {
        int offset = myCompleteMatchResults != null ? myCompleteMatchResults.size():0;
        for(int i = 0; i < myPartialMatchResults.size(); ++i) {
          result[offset + i] = myPartialMatchResults.get( i );
        }
      }

      return result;
    }

    public String getText() {
      return myText;
    }
}
