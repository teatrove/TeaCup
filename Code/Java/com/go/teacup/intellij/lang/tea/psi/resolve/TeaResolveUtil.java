package com.go.teacup.intellij.lang.tea.psi.resolve;

import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Comparing;
import com.go.teacup.intellij.lang.tea.psi.*;
import com.go.teacup.intellij.lang.tea.index.TeaIndex;
import com.go.teacup.intellij.lang.tea.index.TeaNamedElementProxy;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 4:51:24 PM
 */
public class TeaResolveUtil {
    private TeaResolveUtil() {}

    private static final Key<CachedValue<PsiElement[]>> ourFileElementsValueKey = Key.create("file.elements");

    @Nullable
    public static TeaElement treeWalkUp(PsiScopeProcessor processor, PsiElement elt, PsiElement lastParent, PsiElement place) {
      return treeWalkUp(processor, elt, lastParent, place, null);
    }

    @Nullable
    public static TeaElement treeWalkUp(PsiScopeProcessor processor, PsiElement elt, PsiElement lastParent, PsiElement place, PsiElement terminatingParent) {
      if (elt == null) return null;

      PsiElement parentElement = elt.getContext();
      if (parentElement instanceof TeaDefinitionExpression) {
        parentElement = parentElement.getParent().getParent(); // when walking a| = b, start from enclosing statement
      } else if (parentElement instanceof TeaVariable /*&& !(elt instanceof TeaObjectLiteralExpression)*/) {
        // when walking from variable init start from enclosing statement (function expr / object literal could reference that could reference
        // var in this case, better to check for any scope change)
        parentElement = parentElement.getParent();
      }

      int index = -1;
      PsiElement[] children = PsiElement.EMPTY_ARRAY;

      if (parentElement instanceof TeaFile) {
        children = getChildren(parentElement);
        index = 0;
        for(PsiElement el:children) {
          if (el == elt) break;
          ++index;
        }
      }

      PsiElement cur = elt;
      do {
        if (!cur.processDeclarations(processor, ResolveState.initial(), cur == elt ? lastParent : null, place)) {
          if (processor instanceof ResolveProcessor) {
            return ((ResolveProcessor)processor).getResult();
          }
        }
        if(cur instanceof PsiFile) break;
        if (terminatingParent == cur) {
          return null;
        }
        if (cur instanceof TeaStatement && parentElement instanceof TeaIfStatement) {
          // Do not try to resolve variables from then branch in else branch.
          break;
        }

        if (index == -1) cur = cur.getPrevSibling();
        else {
          if (index != 0) cur = children[--index];
          else cur = null;
        }
      } while (cur != null);

      final TeaElement func = processFunctionDeclarations(processor, parentElement);
      if (func != null) return func;

      if (parentElement instanceof TeaFile) return null;

      return treeWalkUp(processor, parentElement, elt, place, terminatingParent);
    }

    @NotNull
    private static PsiElement[] getChildren(final PsiElement element) {
      CachedValue<PsiElement[]> value = element.getUserData(ourFileElementsValueKey);

      if (value == null) {
        value = element.getManager().getCachedValuesManager().createCachedValue(new CachedValueProvider<PsiElement[]>() {
          public CachedValueProvider.Result<PsiElement[]> compute() {
            return new Result<PsiElement[]>(element.getChildren(),element);
          }
        }, false);
        element.putUserData(ourFileElementsValueKey, value);
      }

      return value.getValue();
    }

    private static PsiElement[] EMPTY_PSI_ELEMENT_ARRAY = new PsiElement[0];

    @Nullable
    private static TeaElement processFunctionDeclarations(final @NotNull PsiScopeProcessor processor, final @Nullable PsiElement context) {
      PsiElement[] children = context instanceof TeaFile ? getChildren(context) : EMPTY_PSI_ELEMENT_ARRAY;

      if (context != null) {
        int index = children.length - 1;
        PsiElement cur = index >= 0 ? children[index]:context.getLastChild();

        while (cur != null) {
          if (cur instanceof TeaTemplate) {
            if (!processor.execute(cur, ResolveState.initial())) {
              if (processor instanceof ResolveProcessor) {
                return ((ResolveProcessor)processor).getResult();
              }
            }
          }

          if (index == -1) {
            cur = cur.getPrevSibling();
          } else {
            if (index != 0) cur = children[--index];
            else cur = null;
          }
        }
      }
      return null;
    }

    public static boolean isReferenceTo(final PsiPolyVariantReference reference,
                                        final String referencedName, PsiElement _element) {
      String elementName = null;
      if (_element instanceof PsiNamedElement) elementName = ((PsiNamedElement)_element).getName();
      else if (_element instanceof XmlAttributeValue) elementName = ((XmlAttributeValue)_element).getValue();

      if (Comparing.equal(referencedName, elementName)) {
        PsiElement element = _element;
        if (element instanceof TeaNamedElementProxy) element =((TeaNamedElementProxy)element).getElement();
        final ResolveResult[] resolveResults = reference.multiResolve(true);

        for (ResolveResult r:resolveResults) {
          PsiElement psiElement = r.getElement();
          if (psiElement instanceof TeaNamedElementProxy) psiElement = ((TeaNamedElementProxy)psiElement).getElement();

          if (psiElement == element ||
              ((element instanceof TeaProperty || element instanceof XmlAttributeValue) &&
               psiElement != null &&
               psiElement.getParent() == element)
             ) {
            return true;
          }

//          if (psiElement instanceof TeaFunctionExpression) {
//            final ASTNode nameIdentifier = ((TeaFunctionExpression)psiElement).getFunction().findNameIdentifier();
//            if (nameIdentifier != null && nameIdentifier.getTreeParent().getPsi() == element) return true;
//          }
        }
      }
      return false;
    }

    private static final Key<CachedValue<HashMap<String,ResolveResult[]>>> MY_RESOLVED_CACHED_KEY = Key.create("AllResolvedKey");

    public static ResolveResult[] resolve(final PsiFile file, WalkUpResolveProcessor processor) {
      if (file == null) return ResolveResult.EMPTY_ARRAY;

      CachedValue<HashMap<String,ResolveResult[]>> cache = file.getUserData(MY_RESOLVED_CACHED_KEY);
      if (cache == null) {
        cache = file.getManager().getCachedValuesManager().createCachedValue(
          new CachedValueProvider<HashMap<String,ResolveResult[]>>() {
            public Result<HashMap<String, ResolveResult[]>> compute() {
              return new Result<HashMap<String,ResolveResult[]>>(
                new HashMap<String, ResolveResult[]>(),
                PsiModificationTracker.MODIFICATION_COUNT
              );
            }
          }, false
        );
        file.putUserData(MY_RESOLVED_CACHED_KEY,cache);
      }

      final HashMap<String, ResolveResult[]> resultsMap = cache.getValue();
      final String text = processor.getText();
      ResolveResult[] results = resultsMap.get(text);
      if (results != null) return results;

      TeaIndex.getInstance(file.getProject()).processAllSymbols( processor );

      results = processor.getResults();
      resultsMap.put(text,results);
      return results;
    }

    public static void clearResolveCaches(final PsiFile file) {
      file.putUserData(MY_RESOLVED_CACHED_KEY, null);
    }

    public static class MyResolveResult implements ResolveResult {
      private final PsiElement myFunction;

      public MyResolveResult(final PsiElement function) {
        myFunction = function;
      }

      public PsiElement getElement() {
        //if (myFunction instanceof TeaNamedElementProxy) return ((TeaNamedElementProxy)myFunction).getElement();
        return myFunction;
      }

      public boolean isValidResult() { return true; }
    }
}
