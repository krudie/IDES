package ui.actions;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import ides.api.core.Hub;
import ides.api.core.IncompleteWorkspaceDescriptorException;
import ides.api.plugin.io.IOSubsytem;
import ides.api.plugin.model.DESModel;
import ides.api.plugin.model.DESModelType;
import io.CommonFileActions;
import io.IOUtilities;
import main.Main;
import main.WorkspaceBackend;
import main.WorkspaceDescriptor;
import ui.NewModelDialog;
import util.AnnotationKeys;

/**
 * @author Lenko Grigorov
 */
public class FileActions {

    public static class NewAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = 5954957151134946394L;

        /**
         * used to create unique names in a session
         */
        private static int Count = 0;

        private static ImageIcon icon = new ImageIcon();

        public NewAction() {
            super(Hub.string("comNewModel"), icon);
            icon.setImage(Toolkit.getDefaultToolkit()
                    .createImage(Hub.getIDESResource("images/icons/file_new_automaton.gif")));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintNewModel"));
        }

        public void actionPerformed(ActionEvent e) {
            DESModelType type = new NewModelDialog().selectModel();
            if (type == null) {
                return;
            }
            DESModel des = type.createModel(Hub.string("newModelName") + "-" + Count++);
            Hub.getWorkspace().addModel(des);
            Hub.getWorkspace().setActiveModel(des.getName());
        }
    }

    public static class OpenAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 569604149693358517L;

        private static ImageIcon icon = new ImageIcon();

        public OpenAction() {
            super(Hub.string("comOpenModel"), icon);
            icon.setImage(Toolkit.getDefaultToolkit()
                    .createImage(Hub.getIDESResource("images/icons/file_open_automaton.gif")));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintOpenModel"));
        }

        public void actionPerformed(ActionEvent e) {
            // Open a window for the user to choose the file to open:
            io.CommonFileActions.open();
        }
    }

    public static class SaveAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 3281014341878051666L;

        private static ImageIcon icon = new ImageIcon();

        public SaveAction() {
            super(Hub.string("comSaveModel"), icon);
            icon.setImage(Toolkit.getDefaultToolkit()
                    .createImage(Hub.getIDESResource("images/icons/file_save_automaton.gif")));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintSaveModel"));
        }

        public void actionPerformed(ActionEvent e) {
            io.CommonFileActions.save(Hub.getWorkspace().getActiveModel(),
                    (File) Hub.getWorkspace().getActiveModel().getAnnotation(AnnotationKeys.FILE));
        }
    }

    public static class SaveAsAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = -168876017605256545L;

        private static ImageIcon icon = new ImageIcon();

        public SaveAsAction() {
            super(Hub.string("comSaveAsModel"), icon);
            icon.setImage(Toolkit.getDefaultToolkit()
                    .createImage(Hub.getIDESResource("images/icons/file_saveas_automaton.gif")));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintSaveAsModel"));
        }

        public void actionPerformed(ActionEvent e) {
            io.CommonFileActions.saveAs(Hub.getWorkspace().getActiveModel());
        }
    }

    public static class SaveAllAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 718036124004503876L;

        private static ImageIcon icon = new ImageIcon();

        public SaveAllAction() {
            super(Hub.string("comSaveAllModels"), icon);
            icon.setImage(Toolkit.getDefaultToolkit()
                    .createImage(Hub.getIDESResource("images/icons/file_saveall_automata.gif")));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintSaveAllModels"));
        }

        public void actionPerformed(ActionEvent e) {
            Cursor cursor = Hub.getMainWindow().getCursor();
            Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            Iterator<DESModel> iterator = Hub.getWorkspace().getModels();
            while (iterator.hasNext()) {
                DESModel model = iterator.next();
                if (model.getParentModel() == null) {
                    io.CommonFileActions.save(model, (File) model.getAnnotation(AnnotationKeys.FILE));
                }
            }
            Hub.getMainWindow().setCursor(cursor);
        }
    }

    public static class SaveWorkspaceAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = -5116230109767832605L;

        private static ImageIcon icon = new ImageIcon();

        public SaveWorkspaceAction() {
            super(Hub.string("comSaveWorkspace"), icon);
            icon.setImage(Toolkit.getDefaultToolkit()
                    .createImage(Hub.getIDESResource("images/icons/file_save_workspace.gif")));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintSaveWorkspace"));
        }

        public void actionPerformed(ActionEvent event) {
            Cursor cursor = Hub.getMainWindow().getCursor();
            Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                WorkspaceDescriptor wd = CommonFileActions.getWorkspaceDescriptor();
                if (wd != null) {
                    if (io.CommonFileActions.saveWorkspace(wd, wd.getFile())) {
                        Hub.getWorkspace().setDirty(false);
                    }
                }
            } catch (IncompleteWorkspaceDescriptorException e) {
                Hub.displayAlert(Hub.string("notAllUnsavedSaved"));
            } catch (NullPointerException e) {
                Hub.getMainWindow().setCursor(cursor);
                return;
            }
            Hub.getMainWindow().setCursor(cursor);
        }
    }

    public static class SaveWorkspaceAsAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = -3249878952484357204L;

        private static ImageIcon icon = new ImageIcon();

        public SaveWorkspaceAsAction() {
            super(Hub.string("comSaveAsWorkspace"), icon);
            icon.setImage(Toolkit.getDefaultToolkit()
                    .createImage(Hub.getIDESResource("images/icons/file_saveas_workspace.gif")));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintSaveAsWorkspace"));
        }

        public void actionPerformed(ActionEvent event) {
            Cursor cursor = Hub.getMainWindow().getCursor();
            Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                WorkspaceDescriptor wd = CommonFileActions.getWorkspaceDescriptor();
                if (wd != null) {
                    if (io.CommonFileActions.saveWorkspaceAs(wd)) {
                        Hub.getWorkspace().setDirty(false);
                    }
                }
            } catch (IncompleteWorkspaceDescriptorException e) {
                Hub.displayAlert(Hub.string("notAllUnsavedSaved"));
            }
            Hub.getMainWindow().setCursor(cursor);
        }
    }

    public static class ImportAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = -3784368999206844161L;

        public ImportAction() {
            super(Hub.string("comImport"));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintImport"));
        }

        public void actionPerformed(ActionEvent e) {
            io.CommonFileActions.importFile();
        }
    }

    public static class ExportAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = -1194933274159735798L;

        public ExportAction() {
            super(Hub.string("comExport"));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintExport"));
        }

        public void actionPerformed(ActionEvent e) {
            io.CommonFileActions.export(Hub.getWorkspace().getActiveModel());
        }
    }

    public static class CloseAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 931226234699246422L;

        private static ImageIcon icon = new ImageIcon();

        public CloseAction() {
            super(Hub.string("comCloseModel"), icon);
            icon.setImage(Toolkit.getDefaultToolkit()
                    .createImage(Hub.getIDESResource("images/icons/file_close_automaton.gif")));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintCloseModel"));
        }

        public void actionPerformed(ActionEvent e) {
            Hub.getWorkspace().removeModel(Hub.getWorkspace().getActiveModelName());
        }
    }

    public static class OpenWorkspaceAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = -7511178539387008033L;

        private static ImageIcon icon = new ImageIcon();

        public OpenWorkspaceAction() {
            super(Hub.string("comOpenWorkspace"), icon);
            icon.setImage(Toolkit.getDefaultToolkit()
                    .createImage(Hub.getIDESResource("images/icons/file_open_workspace.gif")));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintOpenWorkspace"));
        }

        public void actionPerformed(ActionEvent e) {
            if (Hub.getWorkspace().isDirty()) {
                if (!io.CommonFileActions.handleUnsavedWorkspace()) {
                    return;
                }
            }
            JFileChooser fc = new JFileChooser(
                    Hub.getPersistentData().getProperty(CommonFileActions.LAST_PATH_SETTING_NAME));
            fc.setDialogTitle(Hub.string("openWorkspaceTitle"));
            fc.setFileFilter(new IOUtilities.ExtensionFilter(new String[] { IOSubsytem.WORKSPACE_FILE_EXT },
                    Hub.string("workspaceFileDescription")));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int retVal = fc.showOpenDialog(Hub.getMainWindow());
            if (retVal == JFileChooser.APPROVE_OPTION) {
                Cursor cursor = Hub.getMainWindow().getCursor();
                Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                WorkspaceDescriptor wd = io.CommonFileActions.openWorkspace(fc.getSelectedFile());
                if (wd != null) {
                    WorkspaceBackend.instance().replaceWorkspace(wd);
                }
                Hub.getMainWindow().setCursor(cursor);
            }
        }
    }

    public static class ExitAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 5524796790387937549L;

        public ExitAction() {
            super(Hub.string("comExit"));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintExit"));
        }

        public void actionPerformed(ActionEvent e) {
            Main.onExit();
        }
    }
}