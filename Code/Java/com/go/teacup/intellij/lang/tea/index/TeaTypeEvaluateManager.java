package com.go.teacup.intellij.lang.tea.index;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveResult;
import com.intellij.util.Processor;
import com.go.teacup.intellij.lang.tea.psi.TeaNamedElement;
import com.go.teacup.intellij.lang.tea.psi.TeaReferenceExpression;
import gnu.trove.TObjectIntHashMap;
import gnu.trove.TIntHashSet;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TObjectHashingStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NonNls;

import java.util.List;
import java.util.ArrayList;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 5:11:15 PM
 */
public class TeaTypeEvaluateManager implements ProjectComponent {
    private TObjectIntHashMap<TeaNamedElement> myTypeMap = new TObjectIntHashMap<TeaNamedElement>(30);
    private TObjectIntHashMap<TeaNamespace> mySupersMap = new TObjectIntHashMap<TeaNamespace>(50);
    private TIntObjectHashMap<List<TeaNamespace>> myName2ElementMap = new TIntObjectHashMap<List<TeaNamespace>>(30);
    private final TeaIndex myIndex;

    public static TeaTypeEvaluateManager getInstance(Project project) {
      return project.getComponent(TeaTypeEvaluateManager.class);
    }

    public TeaTypeEvaluateManager(TeaIndex index) {
      myIndex = index;
    }

    public void projectOpened() {}
    public void projectClosed() {}

    @NonNls
    @NotNull
    public String getComponentName() {
      return "Tea.TypeEvaluateManager";
    }

    public void initComponent() {}
    public void disposeComponent() {}

    public void setElementType(TeaNamedElement element,String type) {
      myTypeMap.put(element, myIndex.getIndexOf(type));
    }

    public String getElementType(PsiNamedElement element) {
      if (element instanceof TeaNamedElement) {
        int i = myTypeMap.get((TeaNamedElement)element);
        if (i == 0) return null;
        return myIndex.getStringByIndex(i);
      }
      return null;
    }

    public void setBaseType(TeaNamespace namespace,String fqtype, TeaNamespace superNs, String superFQType) {
      final int fqSuperIndex = myIndex.getIndexOf(superFQType);
      mySupersMap.put(namespace, fqSuperIndex);
      doAddNsWithType(myIndex.getIndexOf(fqtype), namespace);
      doAddNsWithType(fqSuperIndex, superNs);
    }

    public void iterateTypeHierarchy(String fqTypeName, Processor<TeaNamespace> processor) {
      final int key = myIndex.getIndexOf(fqTypeName);

      List<TeaNamespace> namedElements = myName2ElementMap.get(key);
      if (namedElements != null) {
        for(TeaNamespace namespace:namedElements) {
          if (!doIterateType(namespace, processor)) return;
        }
      }
    }

    public boolean doIterateType(final TeaNamespace namespace, final Processor<TeaNamespace> processor) {
      return doIterateTypeImpl(namespace, processor, new TIntHashSet());
    }

    private boolean doIterateTypeImpl(final TeaNamespace namespace, final Processor<TeaNamespace> processor, TIntHashSet visited) {
      final int superNameId = mySupersMap.get(namespace);

      if (superNameId != 0 && !visited.contains(superNameId)) {
        visited.add(superNameId);
        final List<TeaNamespace> superNamedElements = myName2ElementMap.get(superNameId);

        if (superNamedElements != null) {
          for(TeaNamespace superNs:superNamedElements) {
            if (!processor.process(superNs)) return false;
            if (!doIterateTypeImpl(superNs, processor, visited)) return false;
          }
        }
      }
      return true;
    }

    public boolean isTypeWithDefaultIndexedProperty(String s) {
      if (s == null) return false;
      return isArrayType(s) || s.endsWith("List") || s.endsWith("Map");
    }

    public boolean isArrayType(String s) {
      if (s == null) return false;
      return s.endsWith("[]");
    }

    public String getComponentType(String s) {
      if (isArrayType(s)) return s.substring(0,s.length() - 2);
      return s;
    }

    public String evaluateType(TeaReferenceExpression expr) {
      for(ResolveResult r:expr.multiResolve(false)) {
        final String type = getElementType((TeaNamedElement)r.getElement());
        if (type != null) return type;
      }
      return null;
    }

    public String getInstanceNameByType(String className) {
      if ("Document".equals(className)) return "HTMLDocument";
      if ("Element".equals(className)) return "HTMLElement";
      return className;
    }

    public void clear() {
      myTypeMap.clear();
      mySupersMap.clear();
      myName2ElementMap.clear();
    }

    public void removeNSInfo(final TeaNamespace el) {
      final int i = mySupersMap.remove(el);

      if (i != 0) {
        final List<TeaNamespace> list = myName2ElementMap.get(i);

        if (list != null) {
          list.remove(el);
          if (list.size() == 0) myName2ElementMap.remove(i);
        }
      }
    }

    public void removeElementInfo(final TeaNamedElement el) {
      myTypeMap.remove(el);
    }

    public String getBaseType(final TeaNamespace namespace) {
      int i = mySupersMap.get(namespace);
      if (i == 0) return null;
      return myIndex.getStringByIndex(i);
    }

    private void doAddNsWithType(final int key, final TeaNamespace superNs) {
      List<TeaNamespace> namespaces = myName2ElementMap.get(key);

      if (namespaces == null) {
        namespaces = new ArrayList<TeaNamespace>(1);
        myName2ElementMap.put(key, namespaces);
        namespaces.add(superNs);
      } else {
        if (namespaces.indexOf(superNs) == -1) namespaces.add(superNs);
      }
    }

    private static class MyTObjectHashingStrategy implements TObjectHashingStrategy<TeaNamespace> {
      static MyTObjectHashingStrategy INSTANCE = new MyTObjectHashingStrategy();

      public int computeHashCode(final TeaNamespace object) {
        return object.getNameId();
      }

      public boolean equals(TeaNamespace o1, TeaNamespace o2) {
        while(o1 != null && o2 != null && o1.getNameId() == o2.getNameId()) {
          o1 = o1.getParent();
          o2 = o2.getParent();
        }

        return o1 == o2;
      }
    }
}
