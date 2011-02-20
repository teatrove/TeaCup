package com.go.teacup.intellij.lang.tea;

import com.go.teacup.intellij.lang.tea.debugger.TeaPositionManager;
import com.intellij.debugger.DebuggerManager;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.engine.DebugProcessAdapter;
import com.intellij.debugger.engine.DebugProcessListener;
import com.intellij.debugger.impl.DebuggerManagerImpl;
import com.intellij.debugger.impl.DebuggerManagerListener;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * User: JACKSBRR
 * Created: Apr 13, 2007 10:37:55 AM
 */
public class TeaProjectSupportLoader implements ProjectComponent {
    private static final Logger LOG = Logger.getInstance("#"+TeaProjectSupportLoader.class.getName());

    private final Project project;
    private final DebugProcessListener debugProcessListener;
//    private TeaPlainTextInjector injector;

    public TeaProjectSupportLoader(Project project) {
        this.project = project;
//        this.injector = new TeaPlainTextInjector(project);
        debugProcessListener = new DebugProcessAdapter() {
            public void processAttached(DebugProcess debugProcess) {
                debugProcess.appendPositionManager(new TeaPositionManager(TeaProjectSupportLoader.this.project, debugProcess));
            }
        };
    }

    public void projectOpened() {

        final DebuggerManager debuggerManager = DebuggerManager.getInstance(project);
        ((DebuggerManagerImpl)debuggerManager).addDebuggerManagerListener(
                new DebuggerManagerListener() {
                    public void sessionCreated(DebuggerSession debuggerSession) {
                        debuggerSession.getProcess().addDebugProcessListener(debugProcessListener);
                    }

                    public void sessionRemoved(DebuggerSession debuggerSession) {
                        debuggerSession.getProcess().removeDebugProcessListener(debugProcessListener);
                    }
                }
        );

//        ExecutionManager.getInstance(project).getContentManager().addRunContentListener(
//                new RunContentListener() {
//                    public void contentSelected(RunContentDescriptor runContentDescriptor) {
//                        final DebuggerManager debuggerManager = DebuggerManager.getInstance(project);
//                        debuggerManager.removeDebugProcessListener(
//                                runContentDescriptor.getProcessHandler(),
//                                debugProcessListener
//                        );
//                        debuggerManager.addDebugProcessListener(
//                                runContentDescriptor.getProcessHandler(),
//                                debugProcessListener
//                        );
//                    }
//
//                    public void contentRemoved(RunContentDescriptor runContentDescriptor) {
//                        DebuggerManager.getInstance(project).removeDebugProcessListener(
//                                runContentDescriptor.getProcessHandler(),
//                                debugProcessListener
//                        );
//                    }
//                }
//        );

//        LOG.warn("Injecting language");
//        PsiManager.getInstance(project).registerLanguageInjector(injector);
    }

    public void projectClosed() {
//        PsiManager.getInstance(project).unregisterLanguageInjector(injector);
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return "Tea project support loader";
    }

    public void initComponent() {

    }

    public void disposeComponent() {
//        injector = null;
    }
}
