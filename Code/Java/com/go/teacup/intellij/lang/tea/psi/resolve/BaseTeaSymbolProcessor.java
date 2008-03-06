package com.go.teacup.intellij.lang.tea.psi.resolve;

import com.go.teacup.intellij.lang.tea.index.TeaSymbolProcessor;
import com.go.teacup.intellij.lang.tea.index.TeaIndex;
import com.go.teacup.intellij.lang.tea.index.TeaNamespace;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiElement;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 5:03:06 PM
 */
abstract class BaseTeaSymbolProcessor implements TeaSymbolProcessor {
    protected final PsiFile myTargetFile;
    protected PsiFile myCurrentFile;
    protected final boolean mySkipDclsInTargetFile;
    protected final PsiElement myContext;
    protected final TeaIndex myIndex;
    protected final int myWindowIndex;
    protected final int myFunctionIndex;

    protected BaseTeaSymbolProcessor(final PsiFile targetFile,
                                    final boolean skipDclsInTargetFile,
                                    final PsiElement context) {
      myTargetFile = targetFile;
      mySkipDclsInTargetFile = skipDclsInTargetFile;
      myContext = context;

      myIndex = TeaIndex.getInstance(targetFile.getProject());

      myWindowIndex = myIndex.getIndexOf("window");
      myFunctionIndex = myIndex.getIndexOf("Template");
    }

    protected final boolean isGlobalNS(final TeaNamespace namespace) {
      final int nameId = namespace.getNameId();
      return nameId == -1 ||
             ( ( nameId == myWindowIndex ||
                 nameId == myFunctionIndex
               ) &&
               namespace.getParent().getParent() == null
             );
    }

    public boolean acceptsFile(PsiFile file) {
      if (mySkipDclsInTargetFile && file == myTargetFile) return false;
      myCurrentFile = file;
      return true;
    }

    protected boolean isFromRelevantFileOrDirectory() {
      final PsiFile psiFile = myCurrentFile;

      return myTargetFile == psiFile;
             //(myTargetFile != null &&
             // psiFile != null &&
             // myTargetFile.getContainingDirectory() == psiFile.getContainingDirectory()
             //);
    }

    enum MatchType {
      COMPLETE, PARTIAL, NOMATCH
    }
}
