package com.go.teacup.intellij.lang.tea.psi.impl;

import com.go.teacup.intellij.lang.tea.parser.TeaElementTypes;
import com.go.teacup.intellij.lang.tea.psi.TeaStatement;
import com.intellij.lang.ASTNode;
import com.intellij.util.IncorrectOperationException;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 3:29:05 PM
 */
public class TeaStatementImpl extends TeaElementImpl implements TeaStatement {
    public TeaStatementImpl(final ASTNode node) {
      super(node);
    }

    public TeaStatement addStatementBefore(TeaStatement toAdd) throws IncorrectOperationException {
      return addStatementImpl(toAdd, true);
    }

    public TeaStatement addStatementAfter(TeaStatement toAdd) throws IncorrectOperationException {
      return addStatementImpl(toAdd, false);
    }

    //TODO: [lesya] the formatter stuff definitely needs more intelligence
    private TeaStatement addStatementImpl(final TeaStatement toAdd, final boolean before) throws IncorrectOperationException {
      final ASTNode treeParent = getNode().getTreeParent();

      if (treeParent.getElementType() != TeaElementTypes.BLOCK &&
          treeParent.getElementType() != TeaElementTypes.FILE /*&&
          treeParent.getElementType() != TeaElementTypes.EMBEDDED_CONTENT*/
        ) {
        if (before) {
          return (TeaStatement)treeParent.getPsi().addBefore(toAdd, this);
        }
        else {
          return (TeaStatement)treeParent.getPsi().addAfter(toAdd, this);
        }

      } else {
        final ASTNode copy = toAdd.getNode().copyElement();
        addChildAndReformat(treeParent, copy, before ? getNode() : getNode().getTreeNext());
        return (TeaStatement)copy.getPsi();
      }
    }

    private void addChildAndReformat(final ASTNode block, final ASTNode addedElement, final ASTNode anchorBefore) throws
                                                                                                                  IncorrectOperationException {
      block.addChild(addedElement, anchorBefore);
      getManager().getCodeStyleManager().reformatNewlyAddedElement(block, addedElement);
    }

    public TeaStatement replace(TeaStatement newStatement) {
      return TeaChangeUtil.replaceStatement(this, newStatement);
    }

    public void delete() throws IncorrectOperationException {
      getNode().getTreeParent().removeChild(getNode());
    }
}
