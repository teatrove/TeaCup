package com.go.teacup.intellij.lang.tea.structureView;

import com.go.teacup.intellij.lang.tea.psi.TeaElement;
import com.go.teacup.intellij.lang.tea.psi.TeaTemplate;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.Grouper;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 11:08:10 AM
 */
public class TeaStructureViewModel extends TextEditorBasedStructureViewModel {
    private TeaElement root;

    public TeaStructureViewModel(final TeaElement root) {
        super(root.getContainingFile());
        this.root = root;
    }

    public StructureViewTreeElement getRoot() {
        return new TeaStructureViewElement(root);
    }


    @NotNull
    public Grouper[] getGroupers() {
        return Grouper.EMPTY_ARRAY;
    }

    @NotNull
    public Sorter[] getSorters() {
        return new Sorter[] {Sorter.ALPHA_SORTER};
    }

    @NotNull
    public Filter[] getFilters() {
        return Filter.EMPTY_ARRAY;
    }


    protected PsiFile getPsiFile() {
        return root.getContainingFile();
    }

    @NotNull
    protected Class[] getSuitableClasses() {
        return new Class[] {TeaTemplate.class};
    }
}
