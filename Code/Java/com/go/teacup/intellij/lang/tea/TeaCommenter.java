package com.go.teacup.intellij.lang.tea;

import com.intellij.lang.Commenter;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 11:09:15 AM
 */
public class TeaCommenter implements Commenter {
    public String getLineCommentPrefix() {
      return "//";
    }

    public boolean isLineCommentPrefixOnZeroColumn() {
      return false;
    }

    public String getBlockCommentPrefix() {
      return "/*";
    }

    public String getBlockCommentSuffix() {
      return "*/";
    }

    public String getCommentedBlockCommentPrefix() {
        return null;
    }

    public String getCommentedBlockCommentSuffix() {
        return null;
    }
}
