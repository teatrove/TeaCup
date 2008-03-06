package com.go.teacup.intellij.lang.tea.index;

import com.intellij.openapi.project.Project;
import gnu.trove.TObjectIntHashMap;

import java.io.DataOutputStream;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 5:10:07 PM
 */
public class SerializationContext {
    final DataOutputStream outputStream;
    final TObjectIntHashMap<String> myNames = new TObjectIntHashMap<String>();
    final TObjectIntHashMap<TeaNamespace> myNameSpaces = new TObjectIntHashMap<TeaNamespace>();
    final TeaTypeEvaluateManager typeEvaluateManager;
    final TeaIndex myIndex;

    SerializationContext(DataOutputStream _outputStream, Project project) {
      outputStream = _outputStream;
      typeEvaluateManager = TeaTypeEvaluateManager.getInstance(project);
      myIndex = TeaIndex.getInstance(project);
    }

    public void addName(final String name) {
      if (!myNames.contains(name)) myNames.put(name, myNames.size() + 1);
    }
}
