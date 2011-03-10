package com.go.teacup.intellij.lang.tea.index;

import com.go.teacup.intellij.lang.tea.psi.*;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.*;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import gnu.trove.THashMap;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * User: jacksbrr
 * Created: Apr 16, 2007 12:08:23 PM
 */
public final class TeaIndexEntry {
    private PsiFile myPsiFile;

    public void initTypesAndBrowserSpecifics() {
//      final BrowserSupportManager browserSupportManager = BrowserSupportManager.getInstance(myPsiFile.getProject());
      final TeaTypeEvaluateManager typeEvaluateManager = TeaTypeEvaluateManager.getInstance(myPsiFile.getProject());

      for(TeaNamedElement el:myIndexValue.getValue().mySymbolNameComponents.keySet()) {
        TeaElement realElement = PsiTreeUtil.getParentOfType(((TeaNamedElementProxy)el).getElement(), TeaStatement.class);
        final PsiElement lastChild = realElement.getNextSibling();

        if (lastChild instanceof PsiComment) {
          final String s = lastChild.getText().substring(2);
          final int typeIndex = s.indexOf(',');
          final String browserType = typeIndex != -1 ? s.substring(0, typeIndex).toLowerCase():"";

//          if (browserType.equals("ie")) {
//            browserSupportManager.addIESpecificSymbol(el);
//          } else if (browserType.equals("gecko")) {
//            browserSupportManager.addGeckoSpecificSymbol(el);
//          }

          typeEvaluateManager.setElementType(el, s.substring(typeIndex + 1));
        }
      }
    }

    public static void encodeBrowserSpecificsAndType(StringBuilder builder, String browserSpecific, String type) {
      if (type == null) type = "Object";
      builder.append( "//" );
      final int length = builder.length();

      if (browserSpecific != null) {
        builder.append(browserSpecific);
      }
      if (type != null) {
        if (length != builder.length()) builder.append(',');
        builder.append(type);
      }
    }

    final PsiFile getFile() {
      return myPsiFile;
    }

    public void write(final SerializationContext context) throws IOException {
      final IndexEntryContent indexEntryContent = myIndexValue.getValue();
      indexEntryContent.myNamespace.write(context);

      final Map<TeaNamedElement, TeaNamespace> components = indexEntryContent.mySymbolNameComponents;
      context.outputStream.writeInt(components.size());

      for(TeaNamedElement key: components.keySet()) {
        ((TeaNamedElementProxy)key).write(context);
        context.outputStream.writeInt(context.myNameSpaces.get( components.get(key) ));
      }
    }

    public void enumerateNames(final SerializationContext context) {
      final IndexEntryContent indexEntryContent = myIndexValue.getValue();
      for(TeaNamedElement item: indexEntryContent.mySymbolNameComponents.keySet()) {
        ((TeaNamedElementProxy)item).enumerateNames(context);
      }
      indexEntryContent.myNamespace.enumerateNames(context);
    }

    TeaNamespace getNamespace(final TeaNamedElement myTeaNamedItem) {
      return myIndexValue.getValue().mySymbolNameComponents.get(myTeaNamedItem);
    }

    void invalidate() {
      final TeaTypeEvaluateManager typeEvaluateManager = TeaTypeEvaluateManager.getInstance(myPsiFile.getProject());
      for(Map.Entry<TeaNamedElement,TeaNamespace> entry:myContent.mySymbolNameComponents.entrySet()) {
        typeEvaluateManager.removeElementInfo(entry.getKey());
      }
      myContent.myNamespace.invalidate(typeEvaluateManager);
    }

    static class IndexEntryContent {
      private final TIntObjectHashMap<Object> mySymbols = new TIntObjectHashMap<Object>();
      private Map<TeaNamedElement,TeaNamespace> mySymbolNameComponents = new THashMap<TeaNamedElement,TeaNamespace>();
      private final TeaNamespace myNamespace = new TeaNamespace();
    }

    private CachedValue<IndexEntryContent> myIndexValue;
    private IndexEntryContent myContent;

    public TeaIndexEntry(final DeserializationContext context, @Nullable TeaFile file) throws IOException {
      final IndexEntryContent indexEntryContent = new IndexEntryContent();
      indexEntryContent.myNamespace.read(context, null);

      int mySymbolNameComponentsSize = context.inputStream.readInt();

      for(int i = 0; i < mySymbolNameComponentsSize; ++i) {
        TeaNamedElementProxy proxy = new MyTeaNamedItem(this, context);

        final TeaNamespace namespace = context.myNameSpaces.get(context.inputStream.readInt());
        doAddNamedItemProxy(proxy.getNameId(), proxy, false, namespace, indexEntryContent);
      }

      doInitFor(file, context.manager, indexEntryContent);
    }

    public TeaIndexEntry(final PsiFile psiFile) {
      doInitFor(psiFile, psiFile.getManager(), null);
      myIndexValue.getValue();
    }

    private static void doAddNamedItemProxy(final int nameId, final TeaNamedElement myElement,
                                                final boolean toCheckUniqueness,final TeaNamespace namespace,
                                                IndexEntryContent myContent) {
      final Object o = myContent.mySymbols.get(nameId);
      if (o == null) {
        myContent.mySymbols.put(nameId,myElement);
      }
      else if (o instanceof TeaNamedElement) {
        if (toCheckUniqueness && myContent.mySymbolNameComponents.get((TeaNamedElement)o) == namespace) {
          if (((MyTeaNamedItem)o).getType() == TeaNamedElementProxy.NamedItemType.Definition) return;
        }
        myContent.mySymbols.put(nameId,new Object[] { o, myElement });
      } else {
        final Object[] oArray = (Object[])o;

        if (toCheckUniqueness) {
          for(Object oE:oArray) {
            if (myContent.mySymbolNameComponents.get((TeaNamedElement)oE) == namespace) {
              if (((MyTeaNamedItem)oE).getType() == TeaNamedElementProxy.NamedItemType.Definition) return;
            }
          }
        }

        Object[] newArray = new Object[oArray.length + 1];
        System.arraycopy(oArray,0,newArray,0,oArray.length);
        newArray[oArray.length] = myElement;
        myContent.mySymbols.put(nameId,newArray);
      }

      myContent.mySymbolNameComponents.put(myElement, namespace);
    }

    private void doInitFor(final PsiFile psiFile, PsiManager manager, final IndexEntryContent content) {
      myPsiFile = psiFile;
      myIndexValue = manager.getCachedValuesManager().createCachedValue(new CachedValueProvider<IndexEntryContent>() {
        boolean computeFirstTime = true;

        public CachedValueProvider.Result<IndexEntryContent> compute() {
          myContent = content != null ? content : new IndexEntryContent();

          if (computeFirstTime) {
            computeFirstTime = false;

            if (content != null) { // loaded from disk
              return new Result<IndexEntryContent>(myContent,psiFile);
            }
          }

          invalidate();
          myContent.mySymbolNameComponents.clear();
          myContent.mySymbols.clear();

          updateFromTree();

          return new Result<IndexEntryContent>(myContent,psiFile);
        }

        private void updateFromTree() {
          TeaSymbolUtil.visitSymbols(myPsiFile, myContent.myNamespace, new TeaSymbolProcessor() {
            public void processTemplate(TeaNamespace namespace, final int nameId, TeaNamedElement function) {
              addSymbol(
                nameId,
                function,
                TeaNamedElementProxy.NamedItemType.Template,
                namespace
              );
            }

            private final void addSymbol(final int nameId, final PsiElement element,
                                   TeaNamedElementProxy.NamedItemType type, TeaNamespace namespace) {
              if (nameId == -1) return;

              final TeaNamedElement myElement = new MyTeaNamedItem(TeaIndexEntry.this, element.getTextOffset(), nameId, type );
              boolean toCheckUniqueness = element instanceof TeaDefinitionExpression;

              if (toCheckUniqueness) {
                TeaExpression expression = ((TeaDefinitionExpression)element).getExpression();
                while(expression instanceof TeaReferenceExpression) {
                  expression = ((TeaReferenceExpression)expression).getQualifier();
                }

                toCheckUniqueness = false;//expression instanceof TeaThisExpression;
              }

              doAddNamedItemProxy(nameId, myElement, toCheckUniqueness, namespace,myContent);
            }

            public void processVariable(TeaNamespace namespace, final int nameId, TeaNamedElement variable) {
              addSymbol(nameId, variable, TeaNamedElementProxy.NamedItemType.Variable, namespace);
            }

            public boolean acceptsFile(PsiFile file) {
              return true;
            }

            public PsiFile getBaseFile() {
              return null;
            }

            public void processProperty(final TeaNamespace namespace, final int nameId, final TeaNamedElement property) {
              addSymbol(nameId, property, TeaNamedElementProxy.NamedItemType.Property, namespace);
            }

            public void processDefinition(final TeaNamespace namespace, final int nameId, final TeaNamedElement expression) {
              //if (namespace.length > 0) {
                final TeaDefinitionExpression element = (TeaDefinitionExpression)expression;
                addSymbol(
                  nameId,
                  element,
                  TeaNamedElementProxy.NamedItemType.Definition,
                  namespace
                );
              //}
            }

            @Nullable
            public int getRequiredNameId() {
              return -1;
            }

            public void processTag(TeaNamespace namespace, final int nameId, PsiNamedElement namedElement, final String attrName) {
              if (myPsiFile instanceof XmlFile) {
                final XmlAttribute attribute = ((XmlTag)namedElement).getAttribute(attrName, null);
                final XmlAttributeValue xmlAttributeValue = attribute != null ? attribute.getValueElement():null;
                final PsiElement[] chidren = xmlAttributeValue != null ? xmlAttributeValue.getChildren(): PsiElement.EMPTY_ARRAY;

                if (chidren.length == 3) {
                  addSymbol(
                    nameId,
                    chidren[1],
                    TeaNamedElementProxy.NamedItemType.AttributeValue,
                    namespace
                  );
                }
              }
            }
          });
        }
      }, false);
    }

    public void fillSymbolNames(final Set<String> symbolNames) {
      synchronized(this) {
        final TIntObjectIterator<Object> symbolsIterator = myIndexValue.getValue().mySymbols.iterator();
        final TeaIndex index = TeaIndex.getInstance(myPsiFile.getProject());

        while(symbolsIterator.hasNext()) {
          symbolsIterator.advance();
          symbolNames.add(index.getStringByIndex(symbolsIterator.key()));
        }
      }
    }

    public void fillSymbolsByName(final String name, final Set<NavigationItem> symbolNavItems) {
      synchronized(this) {
        if (TeaIndex.isFromPredefinedFile(myPsiFile) && !ApplicationManager.getApplication().isUnitTestMode()) {
          return; // predefined
        }

        final IndexEntryContent index = myIndexValue.getValue();

        final Object value = index.mySymbols.get( TeaIndex.getInstance( myPsiFile.getProject() ).getIndexOf( name ) );
        if (value instanceof Object[]) {
          for(Object o:(Object[])value) {
            symbolNavItems.add( (NavigationItem)o );
          }
        } else if (value != null) {
          symbolNavItems.add( (NavigationItem)value );
        }
      }
    }

    public void processSymbols(TeaSymbolProcessor processor) {
      ProgressManager.getInstance().checkCanceled();

      synchronized(this) {
        if (!myPsiFile.isValid()) return;
        final TeaIndexEntry.IndexEntryContent index = myIndexValue.getValue();

        if (!processor.acceptsFile(myPsiFile)) return;

        final int requiredNameId = processor.getRequiredNameId();
        if (requiredNameId != -1) {
          final Object value = index.mySymbols.get(requiredNameId);

          if (value instanceof Object[]) {
            for(Object item:(Object[])value) {
              dispatchProcessorCall(index, (TeaNamedElementProxy)item, processor);
            }
          } else if (value != null) {
            dispatchProcessorCall(index, (TeaNamedElementProxy)value, processor);
          }
        } else { // full scan
          final TIntObjectIterator<Object> iterator = index.mySymbols.iterator();
          while(iterator.hasNext()) {
            iterator.advance();
            final Object value = iterator.value();

            if (value instanceof Object[]) {
              for(Object item:(Object[])value) {
                dispatchProcessorCall(index, (TeaNamedElementProxy)item, processor);
              }
            } else {
              dispatchProcessorCall(index, (TeaNamedElementProxy)value, processor);
            }
          }
        }
      }
    }

    private static void dispatchProcessorCall(final IndexEntryContent index,
                                              final TeaNamedElementProxy item,
                                              final TeaSymbolProcessor processor) {
      final TeaNamespace namespace = index.mySymbolNameComponents.get(item);

      final TeaNamedElementProxy.NamedItemType itemType = item.getType();
      final int nameId = item.getNameId();

      if (itemType == TeaNamedElementProxy.NamedItemType.Variable) {
        processor.processVariable(namespace, nameId, item);
      }
      else if (itemType == TeaNamedElementProxy.NamedItemType.Template /*||
               itemType == TeaNamedElementProxy.NamedItemType.FunctionExpression ||
               itemType == TeaNamedElementProxy.NamedItemType.FunctionProperty*/
               ) {
        processor.processTemplate(namespace, nameId, item);
      }
      else if (itemType == TeaNamedElementProxy.NamedItemType.Property) {
        processor.processProperty(namespace, nameId, item);
      }
      else if (itemType == TeaNamedElementProxy.NamedItemType.Definition) {
        processor.processDefinition(namespace, nameId, item);
      } else if (itemType == TeaNamedElementProxy.NamedItemType.AttributeValue) {
        processor.processTag(namespace, nameId, item, null);
      }
    }
}
