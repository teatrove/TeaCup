package com.go.teacup.intellij.lang.tea;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.options.colors.ColorSettingsPages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 10:45:02 AM
 */
public class TeaSupportLoader extends FileTypeFactory {
    public static final LanguageFileType TEA = new TeaFileType();


//    public void initComponent() {
//        ApplicationManager.getApplication().runWriteAction(
//                new Runnable() {
//                    public void run() {
//                        FileTypeManager.getInstance().registerFileType(TEA, new String[]{TEA.getDefaultExtension()});
//                        LanguageParserDefinitions.INSTANCE.
//                        ColorSettingsPages instance = ColorSettingsPages.getInstance();
//                        if (instance != null) {
//                            instance.registerPage(new TeaColorsAndFontsPage());
//                        }
//
//                    }
//                }
//        );
//    }
//
//    public void disposeComponent() {
//    }
//
//    @NotNull
//    public String getComponentName() {
//        return "Tea support loader";
//    }

    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(TEA, TEA.getDefaultExtension());
    }

}
