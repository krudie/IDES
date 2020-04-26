package ui.actions;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import ides.api.core.Hub;
import ides.api.plugin.io.IOSubsytem;
import ides.api.plugin.model.DESModel;
import ides.api.utilities.GeneralUtils;
import util.AnnotationKeys;

public class EditActions {
    public static class RenameAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = -9199994494744280645L;

        protected class UndoableRename extends AbstractUndoableEdit {
            /**
             * 
             */
            private static final long serialVersionUID = -2016080536150859228L;

            protected String name = null;

            protected String originalName = null;

            protected DESModel model = null;

            public UndoableRename(DESModel model, String name) {
                this.model = model;
                this.name = name;
            }

            @Override
            public void undo() throws CannotUndoException {
                if (originalName == null) {
                    throw new CannotUndoException();
                }
                name = model.getName();
                if (!exerciseRename(originalName)) {
                    throw new CannotUndoException();
                }
                originalName = null;
            }

            @Override
            public void redo() throws CannotRedoException {
                if (name == null) {
                    throw new CannotRedoException();
                }
                originalName = model.getName();
                if (!exerciseRename(name)) {
                    throw new CannotRedoException();
                }
                name = null;
            }

            protected boolean exerciseRename(String newName) {
                if (model.hasAnnotation(AnnotationKeys.FILE)) {
                    File oldFile = (File) model.getAnnotation(AnnotationKeys.FILE);
                    File newFile = new File(oldFile.getParentFile().getAbsolutePath() + File.separator + newName + '.'
                            + IOSubsytem.MODEL_FILE_EXT);
                    if (newFile.exists()) {
                        int choice = JOptionPane.showConfirmDialog(Hub.getMainWindow(),
                                GeneralUtils.JOptionPaneKeyBinder.messageLabel(
                                        Hub.string("fileExistAsk1") + newFile.getPath() + Hub.string("fileExistAsk2")),
                                Hub.string("renameModelTitle"), JOptionPane.YES_NO_CANCEL_OPTION);
                        if (choice != JOptionPane.YES_OPTION) {
                            return false;
                        }
                        if (!newFile.delete()) {
                            return false;
                        }
                    }
                    if (!oldFile.renameTo(newFile)) {
                        return false;
                    }
                    model.setAnnotation(AnnotationKeys.FILE, newFile);
                }
                model.setName(newName);
                return true;
            }

            @Override
            public boolean canUndo() {
                return originalName != null;
            }

            @Override
            public boolean canRedo() {
                return name != null;
            }

            /**
             * Returns the name that should be displayed besides the Undo/Redo menu items,
             * so the user knows which action will be undone/redone.
             */
            @Override
            public String getPresentationName() {
                return Hub.string("undoRenameModel");
            }
        }

        public RenameAction() {
            super(Hub.string("comRename"));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintRename"));
        }

        public void actionPerformed(ActionEvent e) {
            DESModel model = Hub.getWorkspace().getActiveModel();
            Box labels = Box.createVerticalBox();
            labels.add(new JLabel(Hub.string("enterNewModelNameL1")));
            labels.add(Box.createRigidArea(new Dimension(0, 5)));
            labels.add(new JLabel(Hub.string("enterNewModelNameL2")));
            labels.add(Box.createRigidArea(new Dimension(0, 5)));
            String newName = "";
            while ("".equals(newName)) {
                newName = (String) JOptionPane.showInputDialog(Hub.getMainWindow(), labels,
                        Hub.string("renameModelTitle"), JOptionPane.PLAIN_MESSAGE, null, null, model.getName());
                if (newName == null || model.getName().equals(newName)) {
                    return;
                }
                if ("".equals(newName)) {
                    Hub.displayAlert(Hub.string("enterNonEmptyLabel"));
                }
            }
            UndoableEdit edit = new UndoableRename(model, newName);
            try {
                edit.redo();
                Hub.getUndoManager().addEdit(edit);
            } catch (CannotRedoException ex) {
                Hub.displayAlert(Hub.string("renameFailed"));
            }
        }
    }

    public static class GoToParentAction extends AbstractAction {
        private static final long serialVersionUID = 8704537876437702968L;

        private static ImageIcon icon = new ImageIcon();

        public GoToParentAction() {
            super(Hub.string("comGoToParent"), icon);
            icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/go_parent.gif")));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintGoToParent"));
        }

        public void actionPerformed(ActionEvent e) {
            DESModel model = Hub.getWorkspace().getActiveModel();
            if (model.getParentModel() != null) {
                Hub.getWorkspace().setActiveModel(model.getParentModel().getName());
            }
        }
    }
}
