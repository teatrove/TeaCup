package com.go.teacup.intellij.lang.tea;

import com.go.teacup.intellij.lang.tea.psi.TeaElement;
import com.go.teacup.intellij.lang.tea.structureView.TeaStructureViewModel;
import com.go.teacup.intellij.lang.tea.validation.TeaAnnotatingVisitor;
import com.go.teacup.intellij.lang.tea.findUsages.TeaFindUsagesProvider;
import com.go.teacup.intellij.lang.tea.folding.TeaFoldingBuilder;
import com.go.teacup.intellij.lang.tea.formatter.blocks.TeaBlock;
import com.go.teacup.intellij.lang.tea.formatter.TeaFormattingModel;
import com.go.teacup.intellij.lang.tea.surroundWith.TeaExpressionSurroundDescriptor;
import com.go.teacup.intellij.lang.tea.surroundWith.TeaStatementsSurroundDescriptor;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.formatting.FormattingModel;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.lang.*;
import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.surroundWith.SurroundDescriptor;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 10:50:25 AM
 */
public class TeaLanguage extends Language {
    private static final TeaAnnotatingVisitor ANNOTATOR = new TeaAnnotatingVisitor();
    private static final SurroundDescriptor[] SURROUND_DESCRIPTORS = new SurroundDescriptor[] {
            new TeaExpressionSurroundDescriptor(),
            new TeaStatementsSurroundDescriptor()
    };


    public TeaLanguage() {
        super("Tea", "text/tea", "application/tea");
    }


    public ParserDefinition getParserDefinition() {
        return new TeaParserDefinition();
    }

    @NotNull
    public SyntaxHighlighter getSyntaxHighlighter(Project project, final VirtualFile virtualFile) {
        return new TeaHighlighter();
    }

    public FoldingBuilder getFoldingBuilder() {
        return new TeaFoldingBuilder();
    }

    public PairedBraceMatcher getPairedBraceMatcher() {
        return new TeaBraceMatcher();
    }

    public Annotator getAnnotator() {
        return ANNOTATOR;
    }

    public StructureViewBuilder getStructureViewBuilder(final PsiFile psiFile) {
        return new TreeBasedStructureViewBuilder() {
            public StructureViewModel createStructureViewModel() {
                return new TeaStructureViewModel((TeaElement)psiFile);
            }
        };
    }

    @NotNull
    public FindUsagesProvider getFindUsagesProvider() {
        return new TeaFindUsagesProvider();
    }

    public Commenter getCommenter() {
        return new TeaCommenter();
    }

    public FormattingModelBuilder getFormattingModelBuilder() {
        return new FormattingModelBuilder() {
            @NotNull
            public FormattingModel createModel(final PsiElement element, final CodeStyleSettings settings) {
                return new TeaFormattingModel(
                        element.getContainingFile(),
                        settings,
                        new TeaBlock(
                                element.getNode(),
                                null,
                                null,
                                null,
                                settings)
                );
            }
        };
    }


    @NotNull
    public SurroundDescriptor[] getSurroundDescriptors() {
        return SURROUND_DESCRIPTORS;
    }

    @NotNull
    public NamesValidator getNamesValidator() {
        return new TeaNamesValidator();
    }


    @Nullable
    public ImportOptimizer getImportOptimizer() {
        return StdLanguages.JAVA.getImportOptimizer();
    }


    @NotNull
    public TokenSet getReadableTextContainerElements() {
        return
                TokenSet.andSet(
                        super.getReadableTextContainerElements(),
                        TokenSet.create(
                                TeaTokenTypes.IDENTIFIER
                        )
                );
    }


    @NotNull
    public RefactoringSupportProvider getRefactoringSupportProvider() {
        return super.getRefactoringSupportProvider();    //To change body of overridden methods use File | Settings | File Templates.
    }


    public DocumentationProvider getDocumentationProvider() {
        return super.getDocumentationProvider();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
