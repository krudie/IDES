/**
 * 
 */
package io;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import ides.api.core.Hub;
import ides.api.core.IncompleteWorkspaceDescriptorException;
import ides.api.plugin.io.FileLoadException;
import ides.api.plugin.io.IOPluginManager;
import ides.api.plugin.io.IOSubsytem;
import ides.api.plugin.io.ImportExportPlugin;
import ides.api.plugin.model.DESModel;
import ides.api.utilities.GeneralUtils;
import main.WorkspaceBackend;
import main.WorkspaceDescriptor;
import ui.SaveDialog;
import util.AnnotationKeys;

/**
 * @author christiansilvano
 */
public class CommonFileActions {

    public static final String LAST_PATH_SETTING_NAME = "lastUsedPath";

    public static final String LAST_IMPEX_SETTING_NAME = "lastUsedImpExFilter";

    public static void open() {
        JFileChooser fc = new JFileChooser(Hub.getPersistentData().getProperty(LAST_PATH_SETTING_NAME));
        fc.setDialogTitle(Hub.string("openModelTitle"));
        fc.setFileFilter(new IOUtilities.ExtensionFilter(new String[] { IOSubsytem.MODEL_FILE_EXT },
                Hub.string("modelFileDescription")));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int retVal = fc.showOpenDialog(Hub.getMainWindow());
        if (retVal == JFileChooser.APPROVE_OPTION) {
            Cursor cursor = Hub.getMainWindow().getCursor();

            Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            if (Hub.getWorkspace().getModel(ParsingToolbox.removeFileType(fc.getSelectedFile().getName())) != null) {
                Hub.displayAlert(Hub.string("modelAlreadyOpen"));
            }

            // calling IOCoordinator to handle the selected file
            // It will make the correct plugins load the file):
            DESModel model = null;
            File file = fc.getSelectedFile();
            try {
                model = IOCoordinator.getInstance().load(file);
            } catch (Exception e) {
                if (e instanceof FileLoadException && ((FileLoadException) e).getPartialModel() != null) {
                    model = ((FileLoadException) e).getPartialModel();
                    Hub.displayAlert(Hub.string("errorsParsingXMLFileL1") + file.getName() + "\n"
                            + GeneralUtils.truncateMessage(e.getMessage()) + "\n"
                            + Hub.string("errorsParsingXMLFileL2"));
                } else {
                    Hub.displayAlert(Hub.string("errorsParsingXMLFileL1") + file.getName() + "\n"
                            + GeneralUtils.truncateMessage(e.getMessage()) + "\n" + Hub.string("errorsParsingXMLfail"));
                }
            }
            if (model != null) {
                Hub.getWorkspace().addModel(model);
                Hub.getWorkspace().setActiveModel(model.getName());
            }
            Hub.getPersistentData().setProperty(LAST_PATH_SETTING_NAME, file.getParent());
            Hub.getMainWindow().setCursor(cursor);
        }
    }

    public static boolean save(DESModel model, File file) {
        // Make the model be saved by the IOCoordinator.
        // IOCoordinator will select the plugins which saves data and metadata
        // information for model.
        if (model != null) {
            if (file == null) {
                file = (File) model.getAnnotation(AnnotationKeys.FILE);
                if (file == null) {
                    return saveAs(model);
                }
            }
            try {
                IOCoordinator.getInstance().save(model, file);
            } catch (IOException e) {
                Hub.displayAlert(Hub.string("cantSaveModel") + file.getName() + "\n"
                        + GeneralUtils.truncateMessage(e.getMessage()));
                return false;
            }
            if (model.getParentModel() == null) {
                String name = ParsingToolbox.removeFileType(file.getName());
                model.setAnnotation(AnnotationKeys.FILE, file);
                model.setName(name);
            }
            model.modelSaved();
            return true;
        }
        return false;
    }

    public static boolean saveAs(DESModel model) {
        if (model != null) {
            JFileChooser fc;
            String path = Hub.getPersistentData().getProperty(LAST_PATH_SETTING_NAME);
            if (path == null) {
                fc = new JFileChooser();
            } else {
                fc = new JFileChooser(path);
            }
            fc.setDialogTitle(Hub.string("saveModelTitle"));
            fc.setFileFilter(new IOUtilities.ExtensionFilter(new String[] { IOSubsytem.MODEL_FILE_EXT },
                    Hub.string("modelFileDescription")));

            if ((File) model.getAnnotation(AnnotationKeys.FILE) != null) {
                fc.setSelectedFile((File) model.getAnnotation(AnnotationKeys.FILE));
            } else {
                fc.setSelectedFile(new File(model.getName()));
            }

            int retVal;
            boolean fcDone = true;
            File file = null;
            do {
                fcDone = true;
                retVal = fc.showSaveDialog(Hub.getMainWindow());
                if (retVal != JFileChooser.APPROVE_OPTION) {
                    break;
                }
                file = fc.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith("." + IOSubsytem.MODEL_FILE_EXT)) {
                    file = new File(file.getPath() + "." + IOSubsytem.MODEL_FILE_EXT);
                }
                if (file.exists()) {
                    int choice = JOptionPane.showConfirmDialog(Hub.getMainWindow(),
                            GeneralUtils.JOptionPaneKeyBinder.messageLabel(
                                    Hub.string("fileExistAsk1") + file.getPath() + Hub.string("fileExistAsk2")),
                            Hub.string("saveModelTitle"), JOptionPane.YES_NO_CANCEL_OPTION);
                    fcDone = choice != JOptionPane.NO_OPTION;
                    if (choice != JOptionPane.YES_OPTION) {
                        retVal = JFileChooser.CANCEL_OPTION;
                    }
                }
            } while (!fcDone);

            if (retVal != JFileChooser.CANCEL_OPTION) {
                if (save(model, file)) {
                    Hub.getPersistentData().setProperty(LAST_PATH_SETTING_NAME, file.getParentFile().getAbsolutePath());
                    // Inform the workspace that one of its models was modified
                    // (renamed)
                    Hub.getWorkspace().setDirty(true);
                    return true;
                }
            }
        }
        return false;
    }

    public static void importFile() {
        JFileChooser fc = new JFileChooser(Hub.getPersistentData().getProperty(LAST_PATH_SETTING_NAME));
        fc.setDialogTitle(Hub.string("importTitle"));
        Vector<String> ext = new Vector<String>();
        Vector<String> desc = new Vector<String>();
        Iterator<ImportExportPlugin> it = IOPluginManager.instance().getImporters().iterator();
        while (it.hasNext()) {
            ImportExportPlugin plugin = it.next();
            if (plugin.getFileExtension() == null) {
                continue;
            }
            ext.add(plugin.getFileExtension());
            desc.add(plugin.getFileDescription());
        }
        if (ext.size() <= 0) {
            // NO PLUGINS REGISTERED TO IMPORT!
            // actually this should never happen since IDES already register
            // some "basic" plugins
            // in the Main.main() method.
            Hub.displayAlert(Hub.string("pluginNotFoundImport"));
            return;
        }
        Iterator<String> extIt = ext.iterator();
        Iterator<String> descIt = desc.iterator();
        while (extIt.hasNext()) {
            fc.addChoosableFileFilter(new IOUtilities.ExtensionFilter(new String[] { extIt.next() }, descIt.next()));
        }
        String lastFilter = Hub.getPersistentData().getProperty(LAST_IMPEX_SETTING_NAME);
        FileFilter[] f = fc.getChoosableFileFilters();
        for (int i = 0; i < f.length; i++) {
            if (f[i].getDescription().equals(lastFilter)) {
                fc.setFileFilter(f[i]);
                break;
            }
        }
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int retVal = fc.showOpenDialog(Hub.getMainWindow());
        if (retVal == JFileChooser.APPROVE_OPTION) {

            File file = fc.getSelectedFile();
            Hub.getPersistentData().setProperty(LAST_PATH_SETTING_NAME, file.getParentFile().getAbsolutePath());
            Hub.getPersistentData().setProperty(LAST_IMPEX_SETTING_NAME, fc.getFileFilter().getDescription());

            if (Hub.getWorkspace().getModel(ParsingToolbox.removeFileType(file.getName())) != null) {
                Hub.displayAlert(Hub.string("modelAlreadyOpen"));
                return;
            }

            Cursor cursor = Hub.getMainWindow().getCursor();
            Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            DESModel model = null;
            try {
                // calling IOCoordinator to handle the selected file
                // It will make the correct plugins load the file):
                model = IOCoordinator.getInstance().importFile(file, fc.getFileFilter().getDescription());
            } catch (Exception e) {
                if (e instanceof FileLoadException && ((FileLoadException) e).getPartialModel() != null) {
                    model = ((FileLoadException) e).getPartialModel();
                    Hub.displayAlert(Hub.string("problemImport") + file.getName() + "\n"
                            + GeneralUtils.truncateMessage(e.getMessage()) + "\n"
                            + Hub.string("errorsParsingXMLFileL2"));
                } else {
                    Hub.displayAlert(Hub.string("cantParseImport") + file.getName() + "\n"
                            + GeneralUtils.truncateMessage(e.getMessage()));
                }
            }
            if (model != null) {
                Hub.getWorkspace().addModel(model);
                Hub.getWorkspace().setActiveModel(model.getName());
            }
            Hub.getMainWindow().setCursor(cursor);
        }
    }

    public static void export(DESModel model) {
        JFileChooser fc;
        String path = Hub.getPersistentData().getProperty(LAST_PATH_SETTING_NAME);
        if (path == null) {
            fc = new JFileChooser();
        } else {
            fc = new JFileChooser(path);
        }
        fc.setDialogTitle(Hub.string("exportTitle"));
        fc.setSelectedFile(new File(model.getName()));
        Set<ImportExportPlugin> ieplugins = IOPluginManager.instance()
                .getExporters(model.getModelType().getMainPerspective());
        if (ieplugins == null) {
            ieplugins = new HashSet<ImportExportPlugin>();
        }
        Iterator<ImportExportPlugin> pluginIt = ieplugins.iterator();
        while (pluginIt.hasNext()) {
            ImportExportPlugin p = pluginIt.next();
            fc.addChoosableFileFilter(
                    new IOUtilities.ExtensionFilter(new String[] { p.getFileExtension() }, p.getFileDescription()));
        }
        String lastFilter = Hub.getPersistentData().getProperty(LAST_IMPEX_SETTING_NAME);
        FileFilter[] f = fc.getChoosableFileFilters();
        for (int i = 0; i < f.length; i++) {
            if (f[i].getDescription().equals(lastFilter)) {
                fc.setFileFilter(f[i]);
                break;
            }
        }
        int retVal;
        boolean fcDone = true;
        File file = null;
        do {
            fcDone = true;
            retVal = fc.showSaveDialog(Hub.getMainWindow());
            if (retVal != JFileChooser.APPROVE_OPTION) {
                break;
            }
            file = fc.getSelectedFile();
            String extension = ParsingToolbox.getFileType(file.getName());
            String extPlugin = "";
            Set<ImportExportPlugin> plugins = IOPluginManager.instance()
                    .getExporters(model.getModelType().getMainPerspective());
            if (plugins == null) {
                plugins = new HashSet<ImportExportPlugin>();
            }
            for (ImportExportPlugin p : plugins) {
                if (p.getFileDescription().equals(fc.getFileFilter().getDescription())) {
                    extPlugin = p.getFileExtension();
                    break;
                }
            }
            // If the user doesn't select a file with an extension, IDES will
            // automatically put an
            // extension for the file, based on a plugin which exports the
            // model.
            if (extension.equals("")) {
                file = new File(file.getParentFile().getAbsolutePath() + File.separator
                        + ParsingToolbox.removeFileType(file.getName()) + (extPlugin != null ? ("." + extPlugin) : ""));
            }
            // Confirms with the user whether an existent file should be
            // replaced.
            if (file.exists()) {
                int choice = JOptionPane.showConfirmDialog(Hub.getMainWindow(),
                        GeneralUtils.JOptionPaneKeyBinder.messageLabel(
                                Hub.string("fileExistAsk1") + file.getPath() + Hub.string("fileExistAsk2")),
                        Hub.string("saveModelTitle"), JOptionPane.YES_NO_CANCEL_OPTION);
                fcDone = choice != JOptionPane.NO_OPTION;
                if (choice != JOptionPane.YES_OPTION) {
                    retVal = JFileChooser.CANCEL_OPTION;
                }
            }
        } while (!fcDone);

        if (retVal != JFileChooser.CANCEL_OPTION) {
            Hub.getPersistentData().setProperty(LAST_PATH_SETTING_NAME, file.getParentFile().getAbsolutePath());
            Hub.getPersistentData().setProperty(LAST_IMPEX_SETTING_NAME, fc.getFileFilter().getDescription());
            try {
                IOCoordinator.getInstance().export(model, file, fc.getFileFilter().getDescription());
            } catch (IOException e) {
                Hub.displayAlert(Hub.string("problemWhileExporting") + file.getName() + "\n"
                        + GeneralUtils.truncateMessage(e.getMessage()));
            }
        }
    }

    /**
     * Saves the workspace described by <code>wd</code>. If the file name is
     * invalid, calls {@link #saveWorkspaceAs(WorkspaceDescriptor)} to get a new
     * file name.
     * 
     * @param wd   the description of the workspace
     * @param file the file where the workspace will be written
     * @return true if file was saved
     */
    public static boolean saveWorkspace(WorkspaceDescriptor wd, File file) {
        PrintStream ps = IOUtilities.getPrintStream(file);
        if (ps == null) {
            return saveWorkspaceAs(wd);
        } else {
            workspaceToXML(wd, ps);
            WorkspaceBackend.instance().setFile(file);
            return true;
        }
    }

    /**
     * Asks the user for a file name and then calls
     * {@link #saveWorkspace(WorkspaceDescriptor, File)}.
     * 
     * @param wd the description of the workspace
     * @return true if file was saved
     */
    public static boolean saveWorkspaceAs(WorkspaceDescriptor wd) {
        JFileChooser fc;

        if (wd.getFile() != null) {
            fc = new JFileChooser(wd.getFile().getParent());
        } else {
            fc = new JFileChooser(Hub.getPersistentData().getProperty(LAST_PATH_SETTING_NAME));
        }

        fc.setDialogTitle(Hub.string("saveWorkspaceTitle"));
        fc.setFileFilter(new IOUtilities.ExtensionFilter(new String[] { IOSubsytem.WORKSPACE_FILE_EXT },
                Hub.string("workspaceFileDescription")));

        if (wd.getFile() != null) {
            fc.setSelectedFile(wd.getFile());
        } else {
            fc.setSelectedFile(new File(Hub.string("newModelName")));
        }

        int retVal;
        boolean fcDone = true;
        File file = null;
        do {
            fcDone = true;
            retVal = fc.showSaveDialog(Hub.getMainWindow());
            if (retVal != JFileChooser.APPROVE_OPTION) {
                break;
            }
            file = fc.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith("." + IOSubsytem.WORKSPACE_FILE_EXT)) {
                file = new File(file.getPath() + "." + IOSubsytem.WORKSPACE_FILE_EXT);
            }

            if (file.exists()) {
                int choice = JOptionPane.showConfirmDialog(Hub.getMainWindow(),
                        GeneralUtils.JOptionPaneKeyBinder.messageLabel(
                                Hub.string("fileExistAsk1") + file.getPath() + Hub.string("fileExistAsk2")),
                        Hub.string("saveWorkspaceTitle"), JOptionPane.YES_NO_CANCEL_OPTION);
                fcDone = choice != JOptionPane.NO_OPTION;
                if (choice != JOptionPane.YES_OPTION) {
                    retVal = JFileChooser.CANCEL_OPTION;
                }
            }
        } while (!fcDone);

        if (retVal == JFileChooser.APPROVE_OPTION) {
            boolean saved = saveWorkspace(wd, file);
            if (saved) {
                Hub.getPersistentData().setProperty(LAST_PATH_SETTING_NAME, file.getParent());
            }
            return saved;
        }
        return false;
    }

    /**
     * Opens the workspace described in the given configuration file.
     * 
     * @param file the file containing the workspace description
     * @return a workspace descriptor object if file is valid, null otherwise
     */
    public static WorkspaceDescriptor openWorkspace(File file) {
        WorkspaceDescriptor wd = null;
        if (!file.canRead()) {
            Hub.displayAlert(Hub.string("fileCantRead") + file.getPath());
            return wd;
        }
        WorkspaceParser wdp = new WorkspaceParser();
        wd = wdp.parse(file);
        if (!"".equals(wdp.getParsingErrors())) {
            Hub.displayAlert(Hub.string("errorsParsingXMLFileL1") + file.getPath() + "\n"
                    + GeneralUtils.truncateMessage(wdp.getParsingErrors()) + "\n"
                    + Hub.string("errorsParsingXMLFileL2"));
        }
        Hub.getPersistentData().setProperty(LAST_PATH_SETTING_NAME, file.getParent());
        return wd;
    }

    /**
     * prints a object to XML.
     * 
     * @param wd the workspace descriptor to convert to XML
     * @param ps the printstream this object should be printed to.
     */
    private static void workspaceToXML(WorkspaceDescriptor wd, PrintStream ps) {
        ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        ps.println("<workspace version=\"3\">");
        Vector<String[]> models = wd.getModels();
        for (int i = 0; i < models.size(); ++i) {
            ps.print("\t<model ");
            String[] info = models.elementAt(i);
            if (WorkspaceDescriptor.FILE_ID.equals(info[0])) {
                ps.print("file=\"" + info[1] + "\"");
            } else if (WorkspaceDescriptor.CHILD_ID.equals(info[0])) {
                ps.print("parent=\"" + info[1] + "\" " + "childid=\"" + info[2] + "\"");
            }
            ps.print(" position=\"" + i + "\"");
            if (i == wd.getSelectedModel()) {
                ps.print(" selected=\"true\"");
            }
            ps.println("/>");
        }
        ps.println("</workspace>");
    }

    /**
     * Asks the user if they want to save the workspace
     * 
     * @return false if the process was cancelled
     */
    public static boolean handleUnsavedWorkspace() {
        int choice = JOptionPane.showConfirmDialog(Hub.getMainWindow(),
                GeneralUtils.JOptionPaneKeyBinder.messageLabel(
                        Hub.string("saveChangesAskWorkspace") + "\"" + Hub.getWorkspace().getName() + "\"?"),
                Hub.string("saveChangesWorkspaceTitle"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                new ImageIcon(Toolkit.getDefaultToolkit()
                        .createImage(Hub.getIDESResource("images/icons/save_workspace.gif"))));
        if (choice != JOptionPane.YES_OPTION && choice != JOptionPane.NO_OPTION) {
            return false;
        }
        if (choice == JOptionPane.YES_OPTION) {
            try {
                WorkspaceDescriptor wd = getWorkspaceDescriptor();
                if (wd == null) {
                    return false;
                }
                if (io.CommonFileActions.saveWorkspace(wd, wd.getFile())) {
                    Hub.getWorkspace().setDirty(false);
                } else {
                    return false;
                }
            } catch (IncompleteWorkspaceDescriptorException e) {
                Hub.displayAlert(Hub.string("notAllUnsavedSaved"));
                return false;
            }
        }
        return true;
    }

    /**
     * Attempts to get a workspace descriptor. If this fails, asks the user to save
     * all unsaved models and attempts to get the descriptor again.
     * 
     * @return a descriptor of the workspace, or <code>null</code> if the operation
     *         was cancelled by the user
     * @throws IncompleteWorkspaceDescriptorException if the user chooses not to
     *                                                save all new models in the
     *                                                workspace
     */
    public static WorkspaceDescriptor getWorkspaceDescriptor() throws IncompleteWorkspaceDescriptorException {
        WorkspaceDescriptor wd = null;
        try {
            wd = WorkspaceBackend.instance().getDescriptor();
        } catch (IncompleteWorkspaceDescriptorException e) {
            Hub.displayAlert(Hub.string("firstSaveUnsaved"));
            Vector<DESModel> unsavedModels = new Vector<DESModel>(e.getNeverSavedModels());
            Iterator<DESModel> it = Hub.getWorkspace().getModels();
            while (it.hasNext()) {
                DESModel model = it.next();
                if (model.needsSave() && model.getParentModel() == null && !unsavedModels.contains(model)) {
                    unsavedModels.add(model);
                }
            }
            if (!io.CommonFileActions.handleUnsavedModels(unsavedModels)) {
                return null;
            }
            wd = WorkspaceBackend.instance().getDescriptor();
        }
        return wd;
    }

    /**
     * Asks the user if they want to save the model
     * 
     * @param m the DESModel that needs to be saved
     * @return false if the process was cancelled
     */
    public static boolean handleUnsavedModel(DESModel m) {
        int saveChoice = JOptionPane.showConfirmDialog(Hub.getMainWindow(),
                GeneralUtils.JOptionPaneKeyBinder
                        .messageLabel(Hub.string("saveChangesAskModel") + "\"" + m.getName() + "\"?"),
                Hub.string("saveChangesModelTitle"), JOptionPane.YES_NO_CANCEL_OPTION);
        if (saveChoice != JOptionPane.YES_OPTION && saveChoice != JOptionPane.NO_OPTION) {
            return false;
        }
        if (saveChoice == JOptionPane.YES_OPTION) {
            if ((File) m.getAnnotation(AnnotationKeys.FILE) != null) {
                try {
                    IOCoordinator.getInstance().save(m, (File) m.getAnnotation(AnnotationKeys.FILE));
                } catch (IOException e) {
                    Hub.displayAlert(Hub.string("cantSaveModel") + " " + m.getAnnotation(AnnotationKeys.FILE) + "\n"
                            + "Message: " + GeneralUtils.truncateMessage(e.getMessage()));
                    return false;
                }
            } else {
                JFileChooser fc;
                String path = Hub.getPersistentData().getProperty(LAST_PATH_SETTING_NAME);
                if (path == null) {
                    fc = new JFileChooser();
                } else {
                    fc = new JFileChooser(path);
                }
                fc.setDialogTitle(Hub.string("saveModelTitle"));
                fc.setFileFilter(new IOUtilities.ExtensionFilter(new String[] { IOSubsytem.MODEL_FILE_EXT },
                        Hub.string("modelFileDescription")));

                if ((File) m.getAnnotation(AnnotationKeys.FILE) != null) {
                    fc.setSelectedFile((File) m.getAnnotation(AnnotationKeys.FILE));
                } else {
                    fc.setSelectedFile(new File(m.getName()));
                }

                int retVal;
                boolean fcDone = true;
                File file = null;
                do {
                    fcDone = true;
                    retVal = fc.showSaveDialog(Hub.getMainWindow());
                    if (retVal != JFileChooser.APPROVE_OPTION) {
                        break;
                    }
                    file = fc.getSelectedFile();
                    if (!file.getName().toLowerCase().endsWith("." + IOSubsytem.MODEL_FILE_EXT)) {
                        file = new File(file.getPath() + "." + IOSubsytem.MODEL_FILE_EXT);
                    }
                    if (file.exists()) {
                        int choice = JOptionPane.showConfirmDialog(Hub.getMainWindow(),
                                Hub.string("fileExistAsk1") + file.getPath() + Hub.string("fileExistAsk2"),
                                Hub.string("saveModelTitle"), JOptionPane.YES_NO_CANCEL_OPTION);
                        fcDone = choice != JOptionPane.NO_OPTION;
                        if (choice != JOptionPane.YES_OPTION) {
                            retVal = JFileChooser.CANCEL_OPTION;
                        }
                    }
                } while (!fcDone);

                if (retVal != JFileChooser.CANCEL_OPTION) {
                    m.setAnnotation(AnnotationKeys.FILE, file);
                    try {
                        IOCoordinator.getInstance().save(m, (File) m.getAnnotation(AnnotationKeys.FILE));
                    } catch (Exception e) {
                        Hub.displayAlert(Hub.string("cantSaveModel") + " " + file.getName() + "\n" + "Message: "
                                + GeneralUtils.truncateMessage(e.getMessage()));
                        return false;
                    }
                    Hub.getWorkspace().fireRepaintRequired();
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean handleUnsavedModels(Vector<DESModel> models) {
        if (models.size() > 0) {
            Vector<DESModel> selectedModels = new SaveDialog(models).selectModels();
            if (selectedModels == null) {
                return false;
            }
            Iterator<DESModel> it = selectedModels.iterator();
            while (it.hasNext()) {
                DESModel model = it.next();
                File file = (File) model.getAnnotation(AnnotationKeys.FILE);
                if (file != null) {
                    try {
                        IOCoordinator.getInstance().save(model, (File) model.getAnnotation(AnnotationKeys.FILE));
                        model.modelSaved();
                    } catch (IOException e) {
                        Hub.displayAlert(Hub.string("cantSaveModel") + " " + file.getName() + "\n" + "Message: "
                                + GeneralUtils.truncateMessage(e.getMessage()));
                        return false;
                    }
                } else {
                    if (!saveAs(model)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
