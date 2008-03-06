package com.go.teacup.intellij.lang.tea.index;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;

import java.io.IOException;

import com.intellij.util.Processor;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 5:12:38 PM
 */
public class TeaNamespace {
    private TeaNamespace myParent;
    private int myNameId;
    private TIntObjectHashMap<TeaNamespace> myChildNamespaces;

    public TeaNamespace() {
      myParent = null;
      myNameId = -1;
    }

    public TeaNamespace(final TeaNamespace parent, final int nameId) {
      myParent = parent;
      myNameId = nameId;
    }

    public void clear() {
      if (myChildNamespaces != null) myChildNamespaces.clear();
    }

    public TeaNamespace getChildNamespace(final int nameId) {
      if (myChildNamespaces == null) {
        myChildNamespaces = new TIntObjectHashMap<TeaNamespace>();
      }
      TeaNamespace ns = myChildNamespaces.get(nameId);
      if (ns == null) {
        ns = new TeaNamespace(this, nameId);
        myChildNamespaces.put(nameId, ns);
      }
      return ns;
    }

    public int getNameId() {
      return myNameId;
    }

    public TeaNamespace getParent() {
      return myParent;
    }

    public void enumerateNames(final SerializationContext context) {
      if (myNameId != -1) context.addName( context.myIndex.getStringByIndex( myNameId ) );
      final String superType = context.typeEvaluateManager.getBaseType(this);
      context.addName(superType != null ? superType:"");

      if (myChildNamespaces != null) {
        final TIntObjectIterator<TeaNamespace> iterator = myChildNamespaces.iterator();

        while(iterator.hasNext()) {
          iterator.advance();
          iterator.value().enumerateNames(context);
        }
      }
    }

    public void write(SerializationContext context) throws IOException {
      context.outputStream.writeInt( doEnumerateNS(this,context) );
      context.outputStream.writeInt( myNameId != -1 ? context.myNames.get( context.myIndex.getStringByIndex(myNameId)): myNameId );

      String superType = context.typeEvaluateManager.getBaseType(this);
      if (superType != null) {
        final TeaNamespace[] parent = new TeaNamespace[1];
        TeaNamespace myRootNs = this;
        while(myRootNs.getParent() != null) myRootNs = myRootNs.getParent();

        final TeaNamespace myRootNs1 = myRootNs;
        context.typeEvaluateManager.doIterateType(
          this,
          new Processor<TeaNamespace>() {
            public boolean process(final TeaNamespace t) {
              TeaNamespace tRootNs = t;
              while(tRootNs.getParent() != null) tRootNs = tRootNs.getParent();

              if (tRootNs == myRootNs1) {
                parent [0] = t;
                return false;
              }
              return true;
            }
          });

        context.outputStream.writeInt( doEnumerateNS(parent[0], context) );
        context.outputStream.writeInt( context.myNames.get(superType) );
      } else {
        context.outputStream.writeInt( -1 );
      }

      if (myChildNamespaces != null) {
        context.outputStream.writeInt(myChildNamespaces.size());
        final TIntObjectIterator<TeaNamespace> iterator = myChildNamespaces.iterator();

        while(iterator.hasNext()) {
          iterator.advance();
          iterator.value().write(context);
        }
      } else {
        context.outputStream.writeInt(0);
      }
    }

    private static int doEnumerateNS(TeaNamespace ns, final SerializationContext context) {
      int i = context.myNameSpaces.get(ns);
      if (i == 0) context.myNameSpaces.put(ns, i = context.myNameSpaces.size() + 1 );
      return i;
    }

    public TeaNamespace read(final DeserializationContext context, TeaNamespace parent) throws IOException {
      final int nsIndex = context.inputStream.readInt();
      final TeaNamespace ns = context.myNameSpaces.get(nsIndex);
      final TeaNamespace result = ns != null ? ns:this;

      context.myNameSpaces.put(nsIndex, result);
      result.doRead(context, parent);
      return result;
    }

    private void doRead(final DeserializationContext context, TeaNamespace parent) throws IOException {
      myParent = parent;
      myNameId = context.inputStream.readInt();

      final int superNsId = context.inputStream.readInt();

      if (superNsId > 0) {
        TeaNamespace superNs = context.myNameSpaces.get(superNsId);
        if (superNs == null) {
          superNs = new TeaNamespace();
          context.myNameSpaces.put(superNsId, superNs);
        }

        final int superNsType = context.inputStream.readInt();

        context.typeEvaluateManager.setBaseType(
          this,
          getQualifiedName(TeaIndex.getInstance(context.manager.getProject())),
          superNs,
          context.myNames.get(superNsType)
        );
      }

      int childCount = context.inputStream.readInt();

      while(childCount > 0) {
        TeaNamespace item = new TeaNamespace();
        item = item.read(context, this);
        if (myChildNamespaces == null) myChildNamespaces = new TIntObjectHashMap<TeaNamespace>(childCount);
        myChildNamespaces.put(item.getNameId(), item);
        --childCount;
      }
    }

    public int[] getIndices() {
      int count = 0;

      for(TeaNamespace ns = this; ns.getParent() != null; ns = ns.getParent()) {
        ++count;
      }

      final int[] result = new int[count];
      for(TeaNamespace ns = this; ns.getParent() != null; ns = ns.getParent()) {
        result[--count] = ns.getNameId();
      }

      return result;
    }

    public String getQualifiedName(TeaIndex index) {
      StringBuffer buf = new StringBuffer();

      for(TeaNamespace ns = this; ns.getParent() != null; ns = ns.getParent()) {
        if (buf.length() > 0) buf.insert(0, '.');
        buf.insert(0, index.getStringByIndex( ns.getNameId()) );
      }

      return buf.toString();
    }

    public void invalidate(final TeaTypeEvaluateManager typeEvaluateManager) {
      typeEvaluateManager.removeNSInfo(this);

      if (myChildNamespaces != null) {
        final TIntObjectIterator<TeaNamespace> iterator = myChildNamespaces.iterator();

        while(iterator.hasNext()) {
          iterator.advance();
          iterator.value().invalidate(typeEvaluateManager);
        }
      }
    }
}
