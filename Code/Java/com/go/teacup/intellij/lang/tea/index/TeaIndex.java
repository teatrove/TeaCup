package com.go.teacup.intellij.lang.tea.index;

import com.go.teacup.intellij.lang.tea.TeaBundle;
import com.go.teacup.intellij.lang.tea.TeaFileTypeLoader;
import com.go.teacup.intellij.lang.tea.psi.TeaFile;
import com.go.teacup.intellij.lang.tea.psi.impl.TeaChangeUtil;
import com.intellij.ProjectTopics;
import com.intellij.lang.PsiBuilder;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.intellij.util.LocalTimeCounter;
import gnu.trove.THashMap;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TObjectIntHashMap;
import gnu.trove.TObjectIntIterator;
import org.jdom.Document;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 4:55:06 PM
 */
public class TeaIndex implements ProjectComponent {
    private Project myProject;
    private final FileTypeManager myFileTypeManager;
    private THashMap<String, TeaIndexEntry> myOldTeaFiles;
    private THashMap<String, TeaIndexEntry> myTeaFiles = new THashMap<String, TeaIndexEntry>();
    private THashMap<String, TeaIndexEntry> myPredefinedTeaFiles = new THashMap<String, TeaIndexEntry>();
    private static Key<TeaIndexEntry> ourEntryKey = Key.create("js.indexentry");
    private TeaNamespace rootNamespace = new TeaNamespace();

    private TeaTreeChangeListener myTreeChangeListener;
    private VirtualFileListener myFileListener;
    private ModuleRootListener myRootListener;
    private Runnable myUpdateRunnable;
    private boolean myLoadingProject;

    private TObjectIntHashMap<String> myNames2Index = new TObjectIntHashMap<String>(50);
    private TIntObjectHashMap<String> myIndex2Names = new TIntObjectHashMap<String>(50);

    @NonNls private static final String NAME_ATTR_NAME = "name";
    @NonNls private static final String METHOD_TAG_NAME = "method";
    @NonNls private static final String PARAM_TAG_NAME = "param";
    @NonNls private static final String PROPERTY_TAG_NAME = "property";
    @NonNls private static final String EVENT_TAG_NAME = "event";
    @NonNls private static final String BROWSER_ATTR_NAME = "browser";

    @NonNls private static final String TEA_CACHES_DIR_NAME = "tea_caches";
    private static final byte CURRENT_VERSION = 1;
    private static final Logger LOG = Logger.getInstance("#"+TeaIndex.class.getName());

//    @NonNls private static final String DHTML_XML_FILE_NAME = "DHTML.xml";
    private static @NonNls Set<String> ourPredefinedFileNames = new HashSet<String>(/*Arrays.asList( "ECMAScript.xml", "DOMCore.xml",
                                                                                                   DHTML_XML_FILE_NAME, "AJAX.xml",
        "DOMEvents.xml", "DOMTraversalAndRange.xml", "DOMXPath.xml")*/);
    @NonNls private static final String PREDEFINES_PREFIX = "predefines.";
    @NonNls private static final String GLOBAL_CLASS_NAME = "Global";
    @NonNls private static final String OBJECT_CLASS_NAME = "Object";

    public TeaIndex(final Project project, final FileTypeManager fileTypeManager) {
      myProject = project;
      myFileTypeManager = fileTypeManager;
      myTreeChangeListener = new TeaTreeChangeListener();
      PsiManager.getInstance(myProject).addPsiTreeChangeListener(myTreeChangeListener);
    }

    public void projectOpened() {
      myLoadingProject = true;
      myUpdateRunnable = new Runnable() {
        public void run() {
          try {
            final ProgressIndicator progress = ProgressManager.getInstance().getProgressIndicator();

            if (myLoadingProject) loadCaches(progress);
            if (progress != null) {
              progress.pushState();
              progress.setIndeterminate(true);
              progress.setText(TeaBundle.message("building.index.message"));
            }

            final ProjectFileIndex fileIndex = ProjectRootManager.getInstance(myProject).getFileIndex();
            final List<VirtualFile> filesToProcess = new ArrayList<VirtualFile>(5);

            fileIndex.iterateContent(new ContentIterator() {
              public boolean processFile(VirtualFile fileOrDir) {
                if (myFileTypeManager.getFileTypeByFile(fileOrDir) == TeaFileTypeLoader.TEA &&
                    myTeaFiles.get(fileOrDir.getPath()) == null
                   ) {
                  filesToProcess.add(fileOrDir);
                }
                return true;
              }
            });

            if (progress != null) {
              progress.setIndeterminate(false);
            }

            int processed = 0;

            for(VirtualFile f:filesToProcess) {
              fileAdded(f);
              ++processed;
              if(progress != null) {
                progress.setFraction((double)processed/filesToProcess.size());
              }
            }

            if (progress != null) {
              progress.setFraction(1.0);
              progress.setText("");
              progress.setText2("");
              progress.popState();
            }
          }
          finally {
            myLoadingProject = false;
          }
        }
      };

      if (ApplicationManager.getApplication().isUnitTestMode()) {
        myUpdateRunnable.run();
      } else {
        StartupManager.getInstance(myProject).registerStartupActivity(myUpdateRunnable);
      }

      myFileListener = new VirtualFileAdapter() {
        public void fileCreated(VirtualFileEvent event) {
          fileAdded(event.getFile());
        }

        public void beforeFileDeleted(VirtualFileEvent event) {
          final VirtualFile fileOrDir = event.getFile();
          if (myFileTypeManager.getFileTypeByFile(fileOrDir) == TeaFileTypeLoader.TEA) {
            processFileRemoved( (TeaFile)PsiManager.getInstance(myProject).findFile(fileOrDir) );
          }
        }
      };

      VirtualFileManager.getInstance().addVirtualFileListener( myFileListener );
        
        myProject.getMessageBus().connect().subscribe(ProjectTopics.PROJECT_ROOTS,
                myRootListener = new ModuleRootListener() {
                    public void beforeRootsChange(ModuleRootEvent event) {
                    }

                    public void rootsChanged(ModuleRootEvent event) {
                        Runnable runnable = new Runnable() {
                            public void run() {
                                if (myProject.isDisposed()) return; // we are already removed
                                myOldTeaFiles = myTeaFiles;
                                myTeaFiles = new THashMap<String, TeaIndexEntry>(myOldTeaFiles.size());

                                if (ApplicationManager.getApplication().isUnitTestMode()) {
                                    myUpdateRunnable.run();
                                } else {
                                    ProgressManager.getInstance().runProcessWithProgressSynchronously(
                                            myUpdateRunnable,
                                            TeaBundle.message("building.index.message"),
                                            false,
                                            myProject
                                    );
                                }

                                myOldTeaFiles = null;
                            }
                        };
                        ApplicationManager.getApplication().invokeLater(runnable);
                    }
                }
        );
    }

    private void initPredefines(ProgressIndicator progress) {
      for(String name:ourPredefinedFileNames) {
        if (myPredefinedTeaFiles.get(name) == null) {
          if (progress != null) progress.setText2(name);
          createPredefinesFromModel(name);
        }
      }
    }

    public void loadCaches(final ProgressIndicator progress) {
      DataInputStream input = null;
      try {
        final File cacheFile = getCacheLocation(TEA_CACHES_DIR_NAME);
        if (!cacheFile.exists()) return;

        input = new DataInputStream(new BufferedInputStream(new FileInputStream(cacheFile)));
        int version = input.readByte();
        if (version != CURRENT_VERSION) {
          return;
        }

        if (progress != null) {
          progress.pushState();
          progress.setText(TeaBundle.message("loading.index.message"));
        }

        final int fileCount = input.readInt();
        final PsiManager manager = PsiManager.getInstance(myProject);
        final int namesCount = input.readInt();
        DeserializationContext context = new DeserializationContext(input, manager, myIndex2Names, rootNamespace);

        myIndex2Names.ensureCapacity( namesCount );
        myNames2Index.ensureCapacity( namesCount );

        for(int i = 0; i < namesCount; ++i) {
          final String name = input.readUTF();
          final int index = input.readInt();
          myIndex2Names.put(index, name);
          myNames2Index.put(name, index);
        }

        for (int i = 0; i < fileCount; i++) {
          final String url = input.readUTF();
          final long stamp = input.readLong();

          if (progress != null) {
            progress.setText2(url);
            progress.setFraction(((double)i) / fileCount);
          }

          boolean predefined = ourPredefinedFileNames.contains(url);
          boolean outdated = false;
          final PsiFile psiFile;

          if (!predefined) {
            final VirtualFile relativeFile = VfsUtil.findRelativeFile(url, null);

            if (relativeFile == null || stamp != relativeFile.getTimeStamp()) {
              outdated = true;
            }

            psiFile = relativeFile != null ? manager.findFile(relativeFile):null;
            if (!(psiFile instanceof TeaFile) || predefined) {
              outdated = true;
            }
          } else {
            psiFile = createPredefinesFile(url);
          }

          final TeaIndexEntry value = new TeaIndexEntry(context, outdated ? null:(TeaFile)psiFile);

          if (!outdated) {
            ((predefined)?myPredefinedTeaFiles:myTeaFiles).put(url, value);
          }
        }

        if (progress != null) {
          progress.popState();
        }

        input.close();
      } catch (NoSuchElementException e) {
        // this might happen if index version is not updated on minor update
      }
      catch (IOException e) {
        LOG.debug(e);
      } finally {
        if (input != null) {
          try {
            input.close();
          }
          catch (IOException e1) {}
        }

        initPredefines(progress);
      }
    }

    private File getCacheLocation(final String dirName) {
      final String cacheFileName = myProject.getName() + "." + Integer.toHexString(FileUtil.toSystemIndependentName(myProject.getPresentableUrl()).hashCode());
      return new File(PathManager.getSystemPath() + File.separator + dirName + File.separator + cacheFileName);
    }

    public void saveCaches() {
      final File cacheFile = getCacheLocation(TEA_CACHES_DIR_NAME);
      FileUtil.createParentDirs(cacheFile);
      DataOutputStream output = null;

      try {
        output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(cacheFile)));
        output.writeByte(CURRENT_VERSION);
        output.writeInt(myTeaFiles.size() + myPredefinedTeaFiles.size());
        SerializationContext context = new SerializationContext(output, myProject);

        enumerateNames(myPredefinedTeaFiles, context);
        enumerateNames(myTeaFiles, context);

        int namesCount = context.myNames.size();
        output.writeInt(namesCount);

        TObjectIntIterator<String> iterator = context.myNames.iterator();
        while(iterator.hasNext()) {
          iterator.advance();
          output.writeUTF(iterator.key());
          output.writeInt(iterator.value());
          --namesCount;
        }

        writeEntries(myPredefinedTeaFiles, output, context);
        writeEntries(myTeaFiles, output, context);
        output.close();
      }
      catch (IOException e) {
        LOG.debug(e);
        if (output != null) {
          try {
            output.close();
            output = null;
          }
          catch (IOException e1) {}
        }
        cacheFile.delete();
      } finally {
        if (output != null) {
          try {
            output.close();
          }
          catch (IOException e1) {}
        }
      }
    }

    private static void writeEntries(final Map<String, TeaIndexEntry> entries, final DataOutputStream output, final SerializationContext context)
      throws IOException {
      for (final String key : entries.keySet()) {
        final TeaIndexEntry value = entries.get(key);
        final VirtualFile virtualFile = value.getFile().getVirtualFile();

        if (virtualFile == null) {
          output.writeUTF(key);
          output.writeLong(-1);
        } else {
          output.writeUTF(virtualFile.getPath());
          output.writeLong(virtualFile.getTimeStamp());
        }

        value.write(context);
      }
    }

    private static void enumerateNames(final Map<String, TeaIndexEntry> entries, final SerializationContext context) {
      for (final TeaIndexEntry value : entries.values()) {
        value.enumerateNames(context);
      }
    }

    private TeaFile createPredefinesFile(String fileName) {
      final String type = fileName.substring(0,fileName.indexOf('.'));
      final String s = translateFile(fileName);

      if (s != null) {
        final PsiFileFactory psiFileFactory = TeaChangeUtil.getPsiFileFactory(myProject);

        try {
          // This method does not expand tree
          final Method method = psiFileFactory.getClass().getMethod(
            "createFileFromText",
            String.class,
            FileType.class,
            CharSequence.class,
            Long.TYPE,
            Boolean.TYPE,
            Boolean.TYPE
          );
          return (TeaFile)method.invoke(
            psiFileFactory,
            PREDEFINES_PREFIX + type + ".tea",
            TeaFileTypeLoader.TEA,
            s,
            LocalTimeCounter.currentTime(),
            false,
            false
          );
        }
        catch (Exception e) {
          return (TeaFile)psiFileFactory.createFileFromText(
            PREDEFINES_PREFIX + type + ".tea",
            TeaFileTypeLoader.TEA,
            s,
            LocalTimeCounter.currentTime(),
            false
          );
        }
      }
      return null;
    }

    private void createPredefinesFromModel(final String fileName) {
      final TeaFile fileFromText = createPredefinesFile(fileName);

      if (fileFromText != null) {
        final TeaIndexEntry value = new TeaIndexEntry(fileFromText, getTeaNamespace(fileFromText));
        myPredefinedTeaFiles.put(fileName, value);
        value.initTypesAndBrowserSpecifics();
      }
    }

    private void fileAdded(final VirtualFile fileOrDir) {
      if (myFileTypeManager.getFileTypeByFile(fileOrDir) == TeaFileTypeLoader.TEA) {
        final PsiFile psiFile = PsiManager.getInstance(myProject).findFile(fileOrDir);
        if (psiFile instanceof TeaFile) processFileAdded((TeaFile)psiFile);
      }
    }

    public void projectClosed() {
      VirtualFileManager.getInstance().removeVirtualFileListener( myFileListener );
      myFileListener = null;
      PsiManager.getInstance(myProject).removePsiTreeChangeListener(myTreeChangeListener);
//      ProjectRootManager.getInstance(myProject).removeModuleRootListener(myRootListener);

      if (!ApplicationManager.getApplication().isUnitTestMode()) {
        saveCaches();
      }
      clear();
    }

    public void processFileAdded(final TeaFile psiFile) {
      final String url = psiFile.getVirtualFile().getPath();

      if (myOldTeaFiles != null) {
        final TeaIndexEntry teaIndexEntry = myOldTeaFiles.get(url);

        if (teaIndexEntry != null) {
          myTeaFiles.put(url,teaIndexEntry);
          return;
        }
      }

      final ProgressIndicator progress = ProgressManager.getInstance().getProgressIndicator();
      if (progress != null) {
        progress.setText2(psiFile.getVirtualFile().getPresentableUrl());
      }
      myTeaFiles.put(url, new TeaIndexEntry(psiFile, getTeaNamespace(psiFile)));
    }

    private void processFileChanged(final TeaFile teaFile) {
      if (!teaFile.isPhysical()) return;
      final TeaIndexEntry indexEntry = myTeaFiles.get(teaFile.getVirtualFile().getPath());

      if (indexEntry == null) {
        processFileAdded(teaFile);
      }
    }

    private void processFileRemoved(final TeaFile jsFile) {
      final TeaIndexEntry jsIndexEntry = myTeaFiles.remove(jsFile.getVirtualFile().getPath());
      if (jsIndexEntry != null) jsIndexEntry.invalidate();
    }

    @NonNls
    public String getComponentName() {
      return "TeaIndex";
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    public static TeaIndex getInstance(final Project project) {
      return project.getComponent(TeaIndex.class);
    }

    public String[] getSymbolNames() {
      final Set<String> symbolNames = new HashSet<String>();
      for(TeaIndexEntry entry: myPredefinedTeaFiles.values()) {
        entry.fillSymbolNames(symbolNames);
      }
      for(TeaIndexEntry entry: myTeaFiles.values()) {
        entry.fillSymbolNames(symbolNames);
      }
      return symbolNames.toArray(new String[symbolNames.size()]);
    }

    public NavigationItem[] getSymbolsByName(final String name) {
      final Set<NavigationItem> symbolNavItems = new HashSet<NavigationItem>();
      for(TeaIndexEntry entry: myTeaFiles.values()) {
        entry.fillSymbolsByName(name, symbolNavItems);
      }
      return symbolNavItems.toArray(new NavigationItem[symbolNavItems.size()]);
    }

    public TeaNamespace getRootNamespace() {
        return rootNamespace;
    }

    public TeaNamespace getTeaNamespace(PsiFile containingFile) {
        return getTeaNamespace(containingFile.getContainingDirectory());
    }

    public TeaNamespace getTeaNamespace(PsiDirectory containingDirectory) {
        // TODO: Assumes src/main/tea
        if(containingDirectory.getName().equals("tea")
                && containingDirectory.getParent().getName().equals("main")
                && containingDirectory.getParent().getParent().getName().equals("src")) {
            return this.getRootNamespace();
        }

        return getTeaNamespace(containingDirectory.getParentDirectory()).getChildNamespace(this.getIndexOf(containingDirectory.getName()));
    }

    public void clear() {
      myTeaFiles.clear();
      myPredefinedTeaFiles.clear();
      TeaTypeEvaluateManager.getInstance(myProject).clear();
//      BrowserSupportManager.getInstance(myProject).clear();
      myIndex2Names.clear();
      myNames2Index.clear();
    }

    public void processAllSymbols(TeaSymbolProcessor processor) {
      for(TeaIndexEntry entry: myPredefinedTeaFiles.values()) {
        entry.processSymbols(processor);
      }

      final PsiFile psiFile = processor.getBaseFile();
      VirtualFile virtualFile = psiFile.getVirtualFile();
      if (virtualFile == null && psiFile.getOriginalFile() != null) {
        virtualFile = psiFile.getOriginalFile().getVirtualFile();
      }

      if (virtualFile == null) return;
      final ProjectFileIndex fileIndex = ProjectRootManager.getInstance(myProject).getFileIndex();

      final Module moduleForFile = fileIndex.getModuleForFile(virtualFile);

      if (moduleForFile != null) {
        final Module[] dependencies = ModuleRootManager.getInstance(moduleForFile).getDependencies();
        final Set<Module> modules = new java.util.HashSet<Module>(dependencies.length + 1);
        modules.addAll(Arrays.asList(dependencies));
        modules.add(moduleForFile);

        for(TeaIndexEntry entry: myTeaFiles.values()) {
          if (modules.contains(fileIndex.getModuleForFile(entry.getFile().getVirtualFile()))) {
            entry.processSymbols(processor);
          }
        }
      }

      if (psiFile.getFileType() != TeaFileTypeLoader.TEA) {
        TeaIndexEntry ourEntry = psiFile.getUserData(ourEntryKey);

        if (ourEntry == null) {
          ourEntry = new TeaIndexEntry(processor.getBaseFile(), getTeaNamespace(processor.getBaseFile()));
          psiFile.putUserData(ourEntryKey, ourEntry);
        }
        ourEntry.processSymbols(processor);
      }
    }

    public static boolean isFromPredefinedFile(final PsiFile containingFile) {
      return !containingFile.isPhysical();
    }

    private class TeaTreeChangeListener extends PsiTreeChangeAdapter {
      public void childAdded(PsiTreeChangeEvent event) {
        final PsiElement child = event.getChild();
        if (child instanceof TeaFile && child.isPhysical()) {
          processFileAdded((TeaFile)child);
        }
        else {
          process(event);
        }
      }

      public void childrenChanged(PsiTreeChangeEvent event) {
        process(event);
      }

      public void childRemoved(PsiTreeChangeEvent event) {
        if (event.getChild() instanceof TeaFile) {
          processFileRemoved((TeaFile)event.getChild());
        }
        else {
          process(event);
        }
      }

      public void childReplaced(PsiTreeChangeEvent event) {
        process(event);
      }

      private void process(final PsiTreeChangeEvent event) {
        final PsiElement psiElement = event.getParent();

        if (psiElement != null && psiElement.isValid()) {
          final PsiFile psiFile = psiElement.getContainingFile();

          if (psiFile instanceof TeaFile) processFileChanged((TeaFile) psiFile);
        }
      }
    }

    private static String translateFile(final String fileName) {
      try {
        final Document document = JDOMUtil.loadDocument(PsiBuilder.Marker.class.getResourceAsStream(fileName));
        final List elements = document.getRootElement().getChildren("class");
        StringBuilder builder = new StringBuilder(8192);

        for(Object e:elements) {
          if (e instanceof Element) {
            if (builder.length() > 0) builder.append('\n');
            final Element element = (Element)e;
            String className = element.getAttributeValue(NAME_ATTR_NAME);
            String extendsFromName = element.getAttributeValue("extends");
            if (extendsFromName == null &&
                !GLOBAL_CLASS_NAME.equals(className) &&
                !OBJECT_CLASS_NAME.equals(className)
               ) {
              extendsFromName = OBJECT_CLASS_NAME;
            }

            translateOneClass(element, className, extendsFromName, builder);
          }
        }

//        if (fileName.equals(DHTML_XML_FILE_NAME)) {
//          try {
//            Class cssPropertyTableClass = Class.forName("com.intellij.psi.css.impl.util.table.CssPropertyTable");
//            final Method method = cssPropertyTableClass.getMethod("initPropertyNames", new Class[]{Set.class});
//            Set<String> propertyNames = new java.util.HashSet<String>();
//            method.invoke(null,new Object[] {propertyNames});
//
//            StringBuffer result = new StringBuffer();
//            for(String propertyName:propertyNames) {
//              result.setLength(0);
//              result.ensureCapacity(propertyName.length());
//              StringTokenizer tokenizer = new StringTokenizer(propertyName, "-");
//
//              while(tokenizer.hasMoreTokens()) {
//                String token = tokenizer.nextToken();
//                if (result.length() != 0) token = StringUtil.capitalize(token);
//                result.append(token);
//              }
//
//              builder.append("style.").append(result.toString()).append(" = 0;\n");
//            }
//          }
//          catch (Exception e) {}
//        }
        return builder.toString();
      } catch(Exception e) {
        LOG.error(e);
      }
      return null;
    }

    private static void translateOneClass(final Element element, final String className, final String extendsClassName, final StringBuilder builder) {
      String targetBrowser = element.getAttributeValue(BROWSER_ATTR_NAME);
      String selectionExpr = !className.equals(GLOBAL_CLASS_NAME) ? className + '.' : "";

      List children = element.getChildren(PROPERTY_TAG_NAME);
      processNodes(children, builder, selectionExpr, targetBrowser);

      children = element.getChildren(METHOD_TAG_NAME);
      processNodes(children, builder, selectionExpr, targetBrowser);

      children = element.getChildren(EVENT_TAG_NAME);
      processNodes(children, builder, selectionExpr, targetBrowser);

      if (extendsClassName != null) {
        builder.append(className).append(".prototype = new ").append(extendsClassName).append("();\n");
      }
    }

    private static void processNodes(final List children, final StringBuilder builder, final String selectionExpr, String browserSpecific) {
      boolean headerStarted = false;
      boolean processingProperties = false;
      boolean seenConstructor = false;

      for(Object e2:children) {
        if (e2 instanceof Element) {
          final Element subelement = ((Element)e2);
          final String name = subelement.getAttributeValue(NAME_ATTR_NAME);
          final boolean method = METHOD_TAG_NAME.equals(subelement.getName());
          final boolean property = PROPERTY_TAG_NAME.equals(subelement.getName());
          final String type;
          final String typeValue;

          String elementBrowserSpecific = subelement.getAttributeValue(BROWSER_ATTR_NAME);
          if (elementBrowserSpecific == null) elementBrowserSpecific = browserSpecific;

          if (method) {
            final List grandchildren = subelement.getChildren(PARAM_TAG_NAME);
            String paramString = "";

            for(Object p:grandchildren) {
              final Element param = ((Element)p);

              //if (param.getAttributeValue("mandatory") == null) continue;
              final String paramName = param.getAttributeValue(NAME_ATTR_NAME);
              if (paramString.length() > 0) paramString += ",";
              paramString  += paramName;
            }

            type = "function("+paramString+") {}";
            typeValue = subelement.getAttributeValue("returnType");
          }
          else if (property) {
            processingProperties = true;

            if ("constructor".equals(name)) {
              seenConstructor = true;
              builder.append(selectionExpr.substring(0, selectionExpr.length() - 1)).append(" = ").append("function() {};\n");
            }

            type = "0";
            typeValue = subelement.getAttributeValue("type");
          } else { // event
            if (!headerStarted) {
              headerStarted = true;
              builder.append("var ").append(selectionExpr.substring(0,selectionExpr.length() - 1)).append(" = {\n");
            }
            builder.append(name).append(": function () {},\n");
            continue;
          }

          builder.append(selectionExpr).append(name) .append(" = ").append(type).append(";");
          TeaIndexEntry.encodeBrowserSpecificsAndType(builder, elementBrowserSpecific, typeValue);
          builder.append('\n');
        }
      }

      if (headerStarted) {
        builder.append("};\n");
      }

      if (processingProperties && !seenConstructor && selectionExpr.length() > 0) {
        // Create static var for noncostructable classes e.g. Math
        builder.append(selectionExpr.substring(0,selectionExpr.length() - 1)).append( " = {};\n");
      }
    }

    public int getIndexOf(String s) {
      if (s == null) {
        return -1;
      }
      final int i = myNames2Index.get(s);
      if (i > 0) return i;
      final int value = myNames2Index.size() + 1;
      myNames2Index.put(s, value);
      myIndex2Names.put(value,s);
      return value;
    }

    public String getStringByIndex(int i) {
      final String s = myIndex2Names.get(i);
      if (s != null) return s;
      if (i == -1) return null;
      throw new NoSuchElementException();
    }
}
