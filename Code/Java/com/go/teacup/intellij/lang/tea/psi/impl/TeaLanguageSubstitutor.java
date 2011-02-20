package com.go.teacup.intellij.lang.tea.psi.impl;

import com.intellij.psi.LanguageSubstitutor;
import com.intellij.lang.Language;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.project.Project;
import com.go.teacup.intellij.lang.tea.TeaFileTypeLoader;
import org.jetbrains.annotations.NotNull;

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
 * <p>Copyright �2009 ESPN.com and Disney Interactive Media Group.  All rights reserved.</p>
 *
 * @author <a href="mailto:Brian.R.Jackson@espn3.com">Brian R. Jackson</a>
 * @version $Change$
 */
public class TeaLanguageSubstitutor extends LanguageSubstitutor {
    public Language getLanguage(@NotNull VirtualFile file, @NotNull Project project) {
        return TeaFileTypeLoader.TEA.getLanguage();
    }
}
