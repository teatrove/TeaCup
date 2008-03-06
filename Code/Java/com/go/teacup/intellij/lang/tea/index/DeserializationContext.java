package com.go.teacup.intellij.lang.tea.index;

import com.intellij.psi.PsiManager;

import java.io.DataInputStream;

import gnu.trove.TIntObjectHashMap;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 5:13:53 PM
 */
public class DeserializationContext {
    final DataInputStream inputStream;
    final TIntObjectHashMap<String> myNames;
    final TIntObjectHashMap<TeaNamespace> myNameSpaces = new TIntObjectHashMap<TeaNamespace>();
    final TeaTypeEvaluateManager typeEvaluateManager;
    final PsiManager manager;

    DeserializationContext(DataInputStream _inputStream, PsiManager _manager, TIntObjectHashMap<String> names) {
      inputStream = _inputStream;
      manager = _manager;
      myNames =  names;
      typeEvaluateManager = TeaTypeEvaluateManager.getInstance(manager.getProject());
    }
}
