package com.go.teacup.intellij.lang.tea;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.options.colors.ColorSettingsPages;
import org.jetbrains.annotations.NotNull;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 10:45:02 AM
 */
public class TeaSupportLoader implements ApplicationComponent {
    public static final LanguageFileType TEA = new TeaFileType();


    public void initComponent() {
        ApplicationManager.getApplication().runWriteAction(
                new Runnable() {
                    public void run() {
                        FileTypeManager.getInstance().registerFileType(TEA, new String[]{TEA.getDefaultExtension()});
                        ColorSettingsPages instance = ColorSettingsPages.getInstance();
                        if (instance != null) {
                            instance.registerPage(new TeaColorsAndFontsPage());
                        }
                        
                    }
                }
        );
    }

    public void disposeComponent() {
    }

    @NotNull
    public String getComponentName() {
        return "Tea support loader";
    }
}
