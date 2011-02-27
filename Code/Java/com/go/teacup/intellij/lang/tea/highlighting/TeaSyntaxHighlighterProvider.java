package com.go.teacup.intellij.lang.tea.highlighting;

import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

/**
 * <p></p>
 * <p/>
 * <p>
 * <div style="font-weight:bold">Revision Information:</div>
 * <ul>
 * <li>$Id$</li>
 * <li>$Author$</li>
 * <li>$DateTime$</li>
 * </ul>
 * </p>
 * <p/>
 * <p>Copyright 2009 ESPN.com and Disney Interactive Media Group.  All rights reserved.</p>
 *
 * @author <a href="mailto:Brian.R.Jackson@espn3.com">Brian R. Jackson</a>
 * @version $Change$
 */
public class TeaSyntaxHighlighterProvider implements SyntaxHighlighterProvider {
    public SyntaxHighlighter create(FileType fileType, @Nullable final Project project, @Nullable final VirtualFile virtualFile) {
        return SyntaxHighlighterFactory.getSyntaxHighlighter(((LanguageFileType) fileType).getLanguage(), project, virtualFile);
    }
}
