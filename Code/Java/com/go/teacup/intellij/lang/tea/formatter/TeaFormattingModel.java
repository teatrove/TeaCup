package com.go.teacup.intellij.lang.tea.formatter;

import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelProvider;
import com.intellij.formatting.Block;
import com.intellij.formatting.FormattingDocumentModel;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.openapi.util.TextRange;
import com.go.teacup.intellij.lang.tea.formatter.blocks.TeaBlock;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 11:09:24 AM
 */
public class TeaFormattingModel implements FormattingModel {
    private FormattingModel model;

    public TeaFormattingModel(PsiFile psiFile,
                              CodeStyleSettings settings,
                              TeaBlock block) {
        model = FormattingModelProvider.createFormattingModelForPsiFile(psiFile, block, settings);
    }


    public Block getRootBlock() {
        return model.getRootBlock();
    }

    public FormattingDocumentModel getDocumentModel() {
        return model.getDocumentModel();
    }

    public TextRange replaceWhiteSpace(TextRange textRange, String whiteSpace) {
        return model.replaceWhiteSpace(textRange, whiteSpace);
    }

    public TextRange shiftIndentInsideRange(TextRange range, int indent) {
        return model.shiftIndentInsideRange(range, indent);
    }

    public void commitChanges() {
        model.commitChanges();
    }
}
