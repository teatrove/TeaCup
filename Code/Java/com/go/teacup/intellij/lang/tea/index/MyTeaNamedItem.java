package com.go.teacup.intellij.lang.tea.index;

import com.go.teacup.intellij.lang.tea.TeaSupportLoader;
import com.intellij.extapi.psi.PsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.Icons;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 12:10:49 PM
 */
public class MyTeaNamedItem extends PsiElementBase implements TeaNamedElementProxy {
    private final int myOffset;
    private final int myNameId;
    private final NamedItemType myValueType;
    private WeakReference<PsiElement> myCachedElement;
    private TeaIndexEntry myJsIndexEntry;

    private static final int DEFINITION_TAG = 1;
    private static final int FUNCTION_TAG = 2;
    private static final int FUNCTION_EXPRESSION_TAG = 3;
    private static final int FUNCTION_PROPERTY_TAG = 4;
    private static final int PROPERTY_TAG = 5;
    private static final int VARIABLE_TAG = 6;
    private static final int UNDEFINED_TAG = 7;

    private static final int VALUE_TAG_MASK = 0xF;
//    private static final int BROWSER_TAG_MASK = 0x30;
//    private static final int BROWSER_TAG_SHIFT = 4;
//    private static final int IE_SPECIFIC_ITEM_TAG = 1;
//    private static final int GECKO_SPECIFIC_ITEM_TAG = 2;

    MyTeaNamedItem(final TeaIndexEntry jsIndexEntry, int _offset, int _nameId, NamedItemType _type) {
      myJsIndexEntry = jsIndexEntry;
      myOffset = _offset;
      myNameId = _nameId;
      myValueType = _type;
    }

    MyTeaNamedItem(final TeaIndexEntry jsIndexEntry, DeserializationContext context) throws IOException {
      myJsIndexEntry = jsIndexEntry;
      myOffset = context.inputStream.readInt();
      myNameId = context.inputStream.readInt();
      final byte attributes = context.inputStream.readByte();
      final byte valueTag = (byte)(attributes & VALUE_TAG_MASK);

      myValueType =
        valueTag == DEFINITION_TAG ? NamedItemType.Definition:
          valueTag == FUNCTION_TAG ? NamedItemType.Template :
            valueTag == FUNCTION_EXPRESSION_TAG ? NamedItemType.FunctionExpression:
              valueTag == FUNCTION_PROPERTY_TAG ? NamedItemType.FunctionProperty:
                valueTag == PROPERTY_TAG ? NamedItemType.Property:
                  valueTag == VARIABLE_TAG ? NamedItemType.Variable:
                    null;
      if (myValueType == null) throw new NullPointerException();

//      final byte browserSpecific = (byte)((attributes & BROWSER_TAG_MASK) >> BROWSER_TAG_SHIFT);
//      if (browserSpecific == IE_SPECIFIC_ITEM_TAG) {
//        context.browserSupportManager.addIESpecificSymbol(this);
//      } else if (browserSpecific == GECKO_SPECIFIC_ITEM_TAG) {
//        context.browserSupportManager.addGeckoSpecificSymbol(this);
//      }

      String type = context.myNames.get( context.inputStream.readInt() );
      if (type.length() > 0) {
        context.typeEvaluateManager.setElementType(this, type);
      }
    }

    @NotNull
    public Language getLanguage() {
      return TeaSupportLoader.TEA.getLanguage();
    }

    @NotNull
    public PsiElement[] getChildren() {
      return PsiElement.EMPTY_ARRAY;
    }

    public PsiElement getParent() {
      return null;
    }

    @Nullable
    public PsiElement getFirstChild() {
      return null;
    }

    @Nullable
    public PsiElement getLastChild() {
      return null;
    }

    @Nullable
    public PsiElement getNextSibling() {
      return null;
    }

    public boolean isPhysical() {
      return getContainingFile().isPhysical();
    }

    @Nullable
    public PsiElement getPrevSibling() {
      return null;
    }

    public TextRange getTextRange() {
      return new TextRange(myOffset, myOffset + 1);
    }

    public int getStartOffsetInParent() {
      return 0;
    }

    public int getTextLength() {
      return 1;
    }

    public PsiElement findElementAt(int offset) {
      return null;
    }

    public int getTextOffset() {
      return myOffset;
    }

    public String getText() {
      return null;
    }

    @NotNull
    public char[] textToCharArray() {
      return new char[0];
    }

    public boolean textContains(char c) {
      return false;
    }

    public ASTNode getNode() {
      return null;
    }

    public PsiElement setName(String name) throws IncorrectOperationException {
      final PsiElement element = getElement();
      if (element instanceof PsiNamedElement) ((PsiNamedElement)element).setName(name);
      return null;
    }

    public void write(SerializationContext context) throws IOException {
      context.outputStream.writeInt(myOffset);
      context.outputStream.writeInt( context.myNames.get( context.myIndex.getStringByIndex( myNameId ) ) );

      final int valueTag = myValueType == NamedItemType.Definition
                           ? DEFINITION_TAG
                           : myValueType == NamedItemType.Template
              ? FUNCTION_TAG
                             : myValueType == NamedItemType.FunctionExpression
                               ? FUNCTION_EXPRESSION_TAG
                               : myValueType == NamedItemType.FunctionProperty
                                 ? FUNCTION_PROPERTY_TAG
                                 : myValueType == NamedItemType.Property
                                   ? PROPERTY_TAG
                                   : myValueType == NamedItemType.Variable ? VARIABLE_TAG : UNDEFINED_TAG;

//      final int browserSpecific =
//        (context.browserSupportManager.isGeckoSpecificSymbol(this) ? GECKO_SPECIFIC_ITEM_TAG:
//          context.browserSupportManager.isIESpecificSymbol(this) ? IE_SPECIFIC_ITEM_TAG:0) << BROWSER_TAG_SHIFT;
//      context.outputStream.writeByte(valueTag | browserSpecific);

      String elementType = context.typeEvaluateManager.getElementType(this);
      if (elementType == null) elementType = "";
      context.outputStream.writeInt( context.myNames.get(elementType) );
    }

    public void enumerateNames(final SerializationContext context) {
      context.addName( context.myIndex.getStringByIndex( myNameId ));
      final String elementType = context.typeEvaluateManager.getElementType(this);
      context.addName(elementType != null ? elementType:"");
    }

    public String getName() {
      return TeaIndex.getInstance( getProject() ).getStringByIndex(myNameId);
    }

    public PsiFile getContainingFile() {
      return myJsIndexEntry.getFile();
    }

    public boolean isValid() {
      return getContainingFile().isValid();
    }

    @NotNull
    public Project getProject() {
      return getContainingFile().getProject();
    }

    public PsiManager getManager() {
      return getContainingFile().getManager();
    }

    public PsiElement getNavigationElement() {
      return getElement();
    }

    public PsiElement getElement() {
      PsiElement element = myCachedElement != null ? myCachedElement.get():null;

      if (element == null || !element.isValid()) {
        if (myValueType != NamedItemType.AttributeValue) {
          element = PsiTreeUtil.getParentOfType(getContainingFile().findElementAt(myOffset), PsiNamedElement.class);

//          if (myValueType == NamedItemType.FunctionExpression && element instanceof TeaDefinitionExpression) {
//            element = ((TeaAssignmentStatement)element.getParent()).getROperand();
////          } else if (myValueType == NamedItemType.FunctionProperty && element instanceof TeaFunctionExpression) {
////            element = element.getParent();
//          }
        } else {
          element = getContainingFile().findElementAt(myOffset);
        }

        myCachedElement = new WeakReference<PsiElement>(element);
      }
      return element;
    }


    public Icon getIcon(int flags) {
      if (myValueType == NamedItemType.Template ||
          myValueType == NamedItemType.FunctionExpression ||
          myValueType == NamedItemType.FunctionProperty) {
        return Icons.METHOD_ICON;
      }

      if (myValueType == NamedItemType.Variable) return Icons.VARIABLE_ICON;
      if (myValueType == NamedItemType.Property) return Icons.PROPERTY_ICON;
      if (myValueType == NamedItemType.AttributeValue) return Icons.XML_TAG_ICON;
      return Icons.FIELD_ICON;
    }

    public NamedItemType getType() {
      return myValueType;
    }

    @Nullable
    public ItemPresentation getPresentation() {
      return new TeaItemPresentation(this, myJsIndexEntry.getNamespace(this));
    }

    public FileStatus getFileStatus() {
      return FileStatusManager.getInstance(getProject()).getStatus(getContainingFile().getVirtualFile());
    }

    public void navigate(boolean requestFocus) {
      Navigatable navItem = (Navigatable)getElement();
      if (navItem != null) {
        navItem.navigate(requestFocus);
      }
    }

    public boolean canNavigate() {
      Navigatable navItem = (Navigatable)getElement();
      return navItem != null && (navItem.canNavigate() || navItem instanceof XmlToken);
    }

    public boolean canNavigateToSource() {
      return canNavigate();
    }

    public int getNameId() {
      return myNameId;
    }
}
