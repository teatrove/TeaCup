/*
 *  Revision Information:
 *  $Id$
 *  $Author$
 *  $DateTime$
 *
 * Copyright ©2010 ESPN.com and Disney Interactive Media Group.  All rights reserved.
 */
package com.go.teacup.intellij.lang.tea.psi.impl;

import com.intellij.lang.CodeDocumentationAwareCommenter;
import com.intellij.lang.Commenter;
import com.intellij.lang.LanguageCommenters;
import com.intellij.openapi.util.ProperTextRange;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.impl.source.tree.PsiCommentImpl;
import org.jetbrains.annotations.NotNull;

/**
 * TODO: Add documentation for TeaPlainLiteralTextEscaper
 *
 * @author <a href="mailto:Brian.R.Jackson@espn3.com">Brian R. Jackson</a>
 * @version $Change$
 */
public class TeaPlainLiteralTextEscaper extends LiteralTextEscaper<TeaPlainTextImpl> {
    public TeaPlainLiteralTextEscaper(TeaPlainTextImpl host) {
      super(host);
    }

    public boolean decode(@NotNull final TextRange rangeInsideHost, @NotNull StringBuilder outChars) {
      ProperTextRange.assertProperRange(rangeInsideHost);
      outChars.append(myHost.getText(), rangeInsideHost.getStartOffset(), rangeInsideHost.getEndOffset());
      return true;
    }

    public int getOffsetInHost(int offsetInDecoded, @NotNull final TextRange rangeInsideHost) {
      int offset = offsetInDecoded + rangeInsideHost.getStartOffset();
      if (offset < rangeInsideHost.getStartOffset()) offset = rangeInsideHost.getStartOffset();
      if (offset > rangeInsideHost.getEndOffset()) offset = rangeInsideHost.getEndOffset();
      return offset;
    }

    public boolean isOneLine() {
      final Commenter commenter = LanguageCommenters.INSTANCE.forLanguage(myHost.getLanguage());
      if (commenter instanceof CodeDocumentationAwareCommenter) {
        return myHost.getTokenType() == ((CodeDocumentationAwareCommenter) commenter).getLineCommentTokenType();
      }
      return false;
    }
}
