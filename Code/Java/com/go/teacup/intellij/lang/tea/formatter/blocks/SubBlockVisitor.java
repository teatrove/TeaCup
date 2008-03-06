package com.go.teacup.intellij.lang.tea.formatter.blocks;

import com.go.teacup.intellij.lang.tea.TeaNodeVisitor;
import com.go.teacup.intellij.lang.tea.TeaTokenTypes;
import com.go.teacup.intellij.lang.tea.TeaElementTypes;
import com.intellij.formatting.Block;
import com.intellij.formatting.Alignment;
import com.intellij.formatting.Wrap;
import com.intellij.formatting.Indent;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.lang.ASTNode;

import java.util.List;
import java.util.ArrayList;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 5:30:02 PM
 */
public class SubBlockVisitor extends TeaNodeVisitor {
    private List<Block> blocks = new ArrayList<Block>();
    private final CodeStyleSettings settings;

    public SubBlockVisitor(CodeStyleSettings settings) {
        this.settings = settings;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void visitElement(final ASTNode node) {
        Alignment alignment = getDefaultAlignment(node);

        ASTNode child = node.getFirstChildNode();
        while(child != null) {
            if(child.getElementType() != TeaTokenTypes.WHITE_SPACE && child.getTextRange().getLength() > 0) {
                Wrap wrap = getWrap(node, child);
                Alignment childAlignment = alignmentProjection(alignment, node, child);
                Indent childIndent = getIndent(node, child);
                blocks.add(new TeaBlock(child, null, null, null, settings));
            }
        }
    }

    private Indent getIndent(ASTNode parent, ASTNode child) {
        return null; //TODO
    }

    private Alignment alignmentProjection(Alignment alignment, ASTNode parent, ASTNode child) {
        return null;  //TODO
    }

    private Wrap getWrap(ASTNode parent, ASTNode child) {
        return null;  // TODO
    }

    private Alignment getDefaultAlignment(ASTNode node) {
        if (node.getElementType() == TeaElementTypes.FOREACH_STATEMENT ||
                node.getElementType() == TeaElementTypes.PARAMETER_LIST ||
                node.getElementType() == TeaElementTypes.BINARY_EXPRESSION ||
                node.getElementType() == TeaElementTypes.ASSIGNMENT_STATEMENT) {
            return Alignment.createAlignment();
        }
        return null;
    }
}
