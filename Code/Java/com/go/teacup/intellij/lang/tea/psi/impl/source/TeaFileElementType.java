/*
 *  Revision Information:
 *  $Id$
 *  $Author$
 *  $DateTime$
 *
 * Copyright ©2010 ESPN.com and Disney Interactive Media Group.  All rights reserved.
 */
package com.go.teacup.intellij.lang.tea.psi.impl.source;

import com.go.teacup.intellij.lang.tea.TeaFileType;
import com.go.teacup.intellij.lang.tea.TeaLanguage;
import com.go.teacup.intellij.lang.tea.TeaLexer;
import com.go.teacup.intellij.lang.tea.TeaSupportLoader;
import com.go.teacup.intellij.lang.tea.psi.TeaFile;
import com.intellij.lang.ASTNode;
import com.intellij.lang.StdLanguages;
import com.intellij.lexer.JavaLexer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.PsiManager;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.impl.java.stubs.PsiJavaFileStub;
import com.intellij.psi.impl.java.stubs.impl.PsiJavaFileStubImpl;
import com.intellij.psi.impl.source.JavaFileStubBuilder;
import com.intellij.psi.impl.source.parsing.FileTextParsing;
import com.intellij.psi.impl.source.tree.FileElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.io.StringRef;

import java.io.IOException;

/**
 * TODO: Add documentation for TeaFileElementType
 *
 * @author <a href="mailto:Brian.R.Jackson@espn3.com">Brian R. Jackson</a>
 * @version $Change$
 */
public class TeaFileElementType extends IStubFileElementType<PsiJavaFileStub> {
  public static final int STUB_VERSION = 3;

  public TeaFileElementType() {
    super("java.FILE", TeaSupportLoader.TEA.getLanguage());
  }

  public StubBuilder getBuilder() {
    return new TeaFileStubBuilder();
  }

  public int getStubVersion() {
    return STUB_VERSION;
  }

  @Override
  public boolean shouldBuildStubFor(final VirtualFile file) {
    final VirtualFile dir = file.getParent();
    return dir == null || dir.getUserData(LanguageLevel.KEY) != null;
  }

  public ASTNode parseContents(ASTNode chameleon) {
    FileElement node = (FileElement)chameleon;
    final CharSequence seq = node.getChars();

    final PsiManager manager = node.getManager();
    final TeaLexer lexer = new TeaLexer();
    return FileTextParsing.parseFileText(manager, lexer, seq, 0, seq.length(), node.getCharTable());
  }

  public String getExternalId() {
    return "tea.FILE";
  }

  public void serialize(final PsiTeaFileStub stub, final StubOutputStream dataStream)
      throws IOException {
    dataStream.writeBoolean(stub.isCompiled());
    dataStream.writeName(stub.getPackageName());
  }

  public PsiJavaFileStub deserialize(final StubInputStream dataStream, final StubElement parentStub) throws IOException {
    boolean compiled = dataStream.readBoolean();
    StringRef packName = dataStream.readName();
    return new PsiJavaFileStubImpl(packName, compiled);
  }

  public void indexStub(final PsiJavaFileStub stub, final IndexSink sink) {
  }
}
