package com.go.teacup.intellij.lang.tea.formatter.blocks;

import com.go.teacup.intellij.lang.tea.formatter.TeaSpacingProcessor;
import com.go.teacup.intellij.lang.tea.lexer.TeaTokenTypes;
import com.go.teacup.intellij.lang.tea.parser.TeaElementTypes;
import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * User: JACKSBRR
 * Created: Apr 3, 2007 11:09:55 AM
 */
public class TeaBlock implements Block {
    private ASTNode node;

    private final CodeStyleSettings settings;

    private Alignment alignment;
    private Indent indent;
    private Wrap wrap;
    private List<Block> subBlocks = null;

    public TeaBlock(final ASTNode node, final Alignment alignment, final Indent indent, final Wrap wrap, final CodeStyleSettings settings) {
        this.alignment = alignment;
        this.indent = indent;
        this.node = node;
        this.wrap = wrap;
        this.settings = settings;
    }


    public ASTNode getNode() {
        return node;
    }

    @NotNull
    public List<Block> getSubBlocks() {
        if(subBlocks == null) {
            SubBlockVisitor visitor = new SubBlockVisitor(getSettings());
            visitor.visit(node);
            subBlocks = visitor.getBlocks();
        }
        return subBlocks;
    }

    public Wrap getWrap() {
        return wrap;
    }

    public Indent getIndent() {
        return indent;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public CodeStyleSettings getSettings() {
        return settings;
    }


    @Nullable
    public Spacing getSpacing(Block child1, Block child2) {
        return new TeaSpacingProcessor(getNode(), ((TeaBlock)child1).getNode(), ((TeaBlock)child2).getNode(), settings).getResult();
    }


    @NotNull
    public ChildAttributes getChildAttributes(final int newChildIndex) {

        Indent indent = null;
        final IElementType blockElementType = node.getElementType();

        if (blockElementType == TeaElementTypes.BLOCK /*||
            blockElementType == JSElementTypes.OBJECT_LITERAL_EXPRESSION*/
           ) {
          indent = Indent.getNormalIndent();
        }
        else if (blockElementType == TeaElementTypes.FILE) {
          indent = Indent.getNoneIndent();
        }
        else if (TeaElementTypes.SOURCE_ELEMENTS.contains(blockElementType)) {
          indent = Indent.getNoneIndent();
        }

        Alignment alignment = null;
        final List<Block> subBlocks = getSubBlocks();
        for (int i = 0; i < newChildIndex; i++) {
          final Alignment childAlignment = subBlocks.get(i).getAlignment();
          if (childAlignment != null) {
            alignment = childAlignment;
            break;
          }
        }

        // in for loops, alignment is required only for items within parentheses
        if (blockElementType == TeaElementTypes.FOREACH_STATEMENT ) {
          for(int i=0; i < newChildIndex; i++) {
            if (((TeaBlock) subBlocks.get(i)).getNode().getElementType() == TeaTokenTypes.RPAR) {
              alignment = null;
              break;
            }
          }
        }
        
        return new ChildAttributes(indent, alignment);
    }


    @NotNull
    public TextRange getTextRange() {
        return new TextRange(node.getStartOffset(), node.getStartOffset()+node.getTextLength()); //TODO: is this right?
    }

    public boolean isIncomplete() {
        return isIncomplete(node);
    }

    private boolean isIncomplete(ASTNode node) {
        ASTNode lastChild = node.getLastChildNode();
        while (lastChild != null && lastChild.getPsi() instanceof PsiWhiteSpace) {
            lastChild = lastChild.getTreePrev();
        }
        if (lastChild == null) return false;
        if (lastChild.getPsi() instanceof PsiErrorElement) return true;
        return isIncomplete(lastChild); 
    }

    public boolean isLeaf() {
        return node.getFirstChildNode() == null;
    }
}
