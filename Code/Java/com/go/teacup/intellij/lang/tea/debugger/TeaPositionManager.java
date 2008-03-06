package com.go.teacup.intellij.lang.tea.debugger;

import com.go.teacup.intellij.lang.tea.TeaSupportLoader;
import com.intellij.debugger.NoDataException;
import com.intellij.debugger.PositionManager;
import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.requests.ClassPrepareRequestor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.ClassPrepareRequest;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:Brian.R.Jackson@espn3.com">Brian Jackson</a>
 * @since Feb 13, 2008 10:33:35 PM
 */
public class TeaPositionManager implements PositionManager {
    private static final Logger LOG = Logger.getLogger(TeaPositionManager.class);

    private static final String TEA_STRATUM = "Java";

    // Need to make this configurable
    private static final String TEA_ROOT_PACKAGE = "beano";

    private Project project;
    private DebugProcess debugProcess;


    public TeaPositionManager(Project project, DebugProcess debugProcess) {
        this.project = project;
        this.debugProcess = debugProcess;
    }

    public SourcePosition getSourcePosition(final com.sun.jdi.Location location) throws NoDataException {
        final String relativePath;
        try {
            relativePath = location.sourceName();
        } catch (AbsentInformationException e) {
            throw (NoDataException) new NoDataException().initCause(e);
        }

        final PsiFile psiFile = getFileByRelativePath(relativePath);

        SourcePosition sourcePosition = null;
        if(psiFile != null) {
            sourcePosition = SourcePosition.createFromLine(psiFile, location.lineNumber(TEA_STRATUM) - 1 );
        }

        if(sourcePosition == null) {
            throw new NoDataException();
        }
        return sourcePosition;
    }

    private PsiFile getFileByRelativePath(final String relativePath) {
        return ApplicationManager.getApplication().runReadAction(
                new Computable<PsiFile>() {
                    public PsiFile compute() {
                        VirtualFile virtualFile = null;
                        for (VirtualFile root : ProjectRootManager.getInstance(project).getContentSourceRoots()) {
                            virtualFile = root.findFileByRelativePath(relativePath);
                            if(virtualFile != null) {
                                break;
                            }
                        }
                        final PsiManager psiManager = PsiManager.getInstance(project);
                        final PsiFile psiFile;
                        if(virtualFile != null) {
                            psiFile = psiManager.findFile(virtualFile);
                        } else {
                            psiFile = null;
                        }
                        return psiFile;
                    }
                }
        );
    }

    public List<com.sun.jdi.ReferenceType> getAllClasses(SourcePosition sourcePosition) throws NoDataException {
        com.intellij.openapi.fileTypes.FileType fileType = sourcePosition.getFile().getFileType();
        if(fileType != TeaSupportLoader.TEA) {
                throw new NoDataException();
        }
        final List<ReferenceType> result = new ArrayList<ReferenceType>();
        final List<ReferenceType> allClasses = debugProcess.getVirtualMachineProxy().allClasses();

        for (ReferenceType referenceType : allClasses) {
            if(referenceType.name().startsWith(TEA_ROOT_PACKAGE) && locationsOfLine(referenceType, sourcePosition).size() > 0) {
                result.add(referenceType);
            }
        }

        return result;
    }

    public List<com.sun.jdi.Location> locationsOfLine(com.sun.jdi.ReferenceType referenceType, SourcePosition sourcePosition) throws NoDataException {
        com.intellij.openapi.fileTypes.FileType fileType = sourcePosition.getFile().getFileType();
        if(fileType != TeaSupportLoader.TEA) {
                throw new NoDataException();
        }
        String relativePath = getRelativePathFromFQDN(referenceType.name());
        PsiFile psiFile = getFileByRelativePath(relativePath);
        if(!sourcePosition.getFile().equals(psiFile)) {
            return Collections.emptyList();
        }

        try {
            final List<Location> list = referenceType.locationsOfLine(sourcePosition.getLine() + 1);
            return list;
        } catch (AbsentInformationException e) {
            throw (NoDataException) new NoDataException().initCause(e);
        }
    }

    private String getRelativePathFromFQDN(String fqdn) {
        StringBuilder stringBuilder = new StringBuilder(fqdn);
        stringBuilder.delete(0, TEA_ROOT_PACKAGE.length() + 1);
        int index = 0;
        while((index = stringBuilder.indexOf(".", index)) >= 0) {
            stringBuilder.replace(index, index + 1, "/");
        }
        stringBuilder.append(".tea");
        return stringBuilder.toString();
    }

    public com.sun.jdi.request.ClassPrepareRequest createPrepareRequest(final ClassPrepareRequestor classPrepareRequestor, final SourcePosition sourcePosition) throws NoDataException {
        com.intellij.openapi.fileTypes.FileType fileType = sourcePosition.getFile().getFileType();
        if(fileType != TeaSupportLoader.TEA) {
                throw new NoDataException();
        }
        final ClassPrepareRequest request = debugProcess.getRequestsManager().createClassPrepareRequest(
                new ClassPrepareRequestor() {
                    final SourcePosition position = sourcePosition;
                    final ClassPrepareRequestor requestor = classPrepareRequestor;

                    public void processClassPrepare(DebugProcess debugProcess, ReferenceType referenceType) {
                        try {
                            if(locationsOfLine(referenceType, position).size() > 0) {
                                requestor.processClassPrepare(debugProcess, referenceType);
                            }
                        } catch (NoDataException e) {
                            if(LOG.isEnabledFor(Priority.ERROR)) { LOG.error(e,e); }
                        }
                    }
                },
                TEA_ROOT_PACKAGE + ".*");
        return request;
    }

}
