package com.go.teacup.intellij.lang.tea;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 10:48:47 AM
 */
public class TeaFileType extends LanguageFileType {


    public TeaFileType() {
        super(new TeaLanguage());
    }

    @NotNull
    @NonNls
    public String getName() {
        return getLanguage().getID();
    }

    @NotNull
    public String getDescription() {
        return getLanguage().getID();
    }

    @NotNull
    @NonNls
    public String getDefaultExtension() {
        return "tea";
    }

    @Nullable
    public Icon getIcon() {
        return IconLoader.getIcon("/fileTypes/jsp.png");
    }


    public boolean isJVMDebuggingSupported() {
        return true;
    }
    
}
