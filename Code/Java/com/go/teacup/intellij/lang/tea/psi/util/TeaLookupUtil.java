package com.go.teacup.intellij.lang.tea.psi.util;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 11:19:40 AM
 */
public abstract class TeaLookupUtil implements ApplicationComponent {
  @NotNull
  @NonNls
  public String getComponentName() {
    return "Tea.LookupUtil";
  }

  public void initComponent() {}

  public void disposeComponent() {
  }

  @Nullable
  public abstract Object createPrioritizedLookupItem(PsiElement value, String name, int priority);

  public static TeaLookupUtil getInstance() {
    return ApplicationManager.getApplication().getComponent(TeaLookupUtil.class);
  }
}
