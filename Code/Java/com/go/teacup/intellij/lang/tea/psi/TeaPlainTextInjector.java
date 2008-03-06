package com.go.teacup.intellij.lang.tea.psi;

import com.go.teacup.intellij.lang.tea.TeaElementTypes;
import com.intellij.lang.Language;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 9:26:29 AM
 */
public class TeaPlainTextInjector implements LanguageInjector {
    private static final Logger LOG = Logger.getInstance("#"+ TeaPlainTextInjector.class.getName());
    private Project project;


    public TeaPlainTextInjector(Project project) {
        this.project = project;
    }

    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost host, @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {
        if (host instanceof PsiComment && ((PsiComment)host).getTokenType() == TeaElementTypes.PLAIN_TEXT) {
            PsiComment node = (PsiComment) host;
            String text = node.getText();

            // Find the contentType call to decide which language
            PsiFile psiFile = node.getContainingFile();
            String contentType = findContentType(psiFile);


            Language language = findLanguageByMimeType(contentType);

            // TODO: figure out how to replace a language registration when contentType changes
            if(language != null) {
                injectionPlacesRegistrar.addPlace(language, new TextRange(0, text.length()), null, null);
            }

//            final Language language = ((LanguageFileType) FileTypeManager.getInstance().getFileTypeByExtension("html")).getLanguage();
//            LOG.warn("injecting HTML into '"+text+"'");
//            injectionPlacesRegistrar.addPlace(language, new TextRange(0, text.length()), "", "");
        }
    }

    private String findContentType(PsiFile psiFile) {
        String contentType = "text/html";
        if(psiFile.getNode() != null) {
            final PsiElement[] functionCalls =
                    PsiTreeUtil.collectElements(psiFile,
                        new PsiElementFilter() {
                            public boolean isAccepted(PsiElement psiElement) {
                                return psiElement != null &&
                                        psiElement.getNode() != null &&
                                        psiElement.getNode().getElementType() != null &&
                                        psiElement.getNode().getElementType() == TeaElementTypes.FUNCTION_CALL_EXPRESSION;
                            }
                        }
                    );

            for (PsiElement functionCall : functionCalls) {
                //Find the argument list and find the reference before it
                final TeaArgumentList args = PsiTreeUtil.getChildOfType(functionCall, TeaArgumentList.class);
                TeaReferenceExpression ref = null;
                if (args != null) {
                    ref = (TeaReferenceExpression) args.getPrevSibling();
                }
                if (ref != null) {
                    if("setContentType".equals(ref.getReferencedName())) {
                        TeaLiteralExpression literal = PsiTreeUtil.getChildOfType(args, TeaLiteralExpression.class);
                        if (literal != null) {
                            contentType = literal.getText().replaceAll("\"", "");
                        }
                        break;
                    }
                }
            }
        }
        return contentType;
    }

    private Language findLanguageByMimeType(String contentType) {
        Language language = null;
        outer: for (FileType fileType : FileTypeManager.getInstance().getRegisteredFileTypes()) {
            if(fileType instanceof LanguageFileType) {
                for (String mimeType : ((LanguageFileType) fileType).getLanguage().getMimeTypes()) {
                    if(contentType.equals(mimeType)) {
                        language = ((LanguageFileType) fileType).getLanguage();
                        break outer;
                    }
                }
            }
        }
        return language;
    }
}
