package com.go.teacup.intellij.lang.tea;

import com.intellij.lang.Language;
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

    public static final TeaFileType TEA_FILE_TYPE = new TeaFileType();
    public static final Language TEA_LANGUAGE = TEA_FILE_TYPE.getLanguage();
    @NonNls public static final String DEFAULT_EXTENSION = "tea";

    private TeaFileType() {
        super(new TeaLanguage());
    }

    @NotNull
    @NonNls
    public String getName() {
        return getLanguage().getID();
    }

    @NotNull
    @NonNls
    public String getDescription() {
        return "Tea Files";
    }

    @NotNull
    @NonNls
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Nullable
    public Icon getIcon() {
        return IconLoader.getIcon("/fileTypes/jsp.png");
    }


    public boolean isJVMDebuggingSupported() {
        return true;
    }

//    @Override
//    public EditorHighlighter getEditorHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile, @NotNull EditorColorsScheme colors) {
//        return new TeaEditorHighlighter(colors, project, virtualFile);
//    }
}
