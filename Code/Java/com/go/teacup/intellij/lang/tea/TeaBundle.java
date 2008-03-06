package com.go.teacup.intellij.lang.tea;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

/**
 * User: JACKSBRR
 * Created: Apr 4, 2007 9:24:05 AM
 */
public class TeaBundle {
    private static Reference<ResourceBundle> ourBundle;

    @NonNls
    private static final String BUNDLE = "com.go.teacup.intellij.lang.tea.TeaMessageBundle";

    private TeaBundle() {
    }

    public static String message(@NonNls @PropertyKey(resourceBundle = "com.go.teacup.intellij.lang.tea.TeaMessageBundle")String key, Object... params) {
      return CommonBundle.message(getBundle(), key, params);
    }

    private static ResourceBundle getBundle() {
      ResourceBundle bundle = null;
      if (ourBundle != null) bundle = ourBundle.get();
      if (bundle == null) {
        bundle = ResourceBundle.getBundle(BUNDLE);
        ourBundle = new SoftReference<ResourceBundle>(bundle);
      }
      return bundle;
    }

}
