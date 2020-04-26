package services.undo;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.undo.UndoableEdit;

import ides.api.core.Hub;
import ides.api.core.WorkspaceMessage;
import ides.api.core.WorkspaceSubscriber;
import ides.api.plugin.model.DESModel;
import ides.api.undo.UndoManager;

public class UndoBackend implements UndoManager {
    protected static Vector<AbstractButton> uiElementsUndo = new Vector<AbstractButton>();

    protected static Vector<AbstractButton> textUIElementsUndo = new Vector<AbstractButton>();

    protected static Vector<AbstractButton> uiElementsRedo = new Vector<AbstractButton>();

    protected static Vector<AbstractButton> textUIElementsRedo = new Vector<AbstractButton>();

    protected final static String UNDO_MANAGER = "undoManager";

    // Make the class non-instantiable.
    private UndoBackend() {
    }

    @Override
    public Object clone() {
        throw new RuntimeException("Cloning of " + this.getClass().toString() + " not supported.");
    }

    /**
     * Instance for the non-static methods.
     */
    private static UndoBackend me = null;

    public static UndoBackend instance() {
        if (me == null) {
            me = new UndoBackend();
        }
        return me;
    }

    /**
     * Initializes the UndoManager. The UndoManager subscribes itself to the
     * workspace, so it can proccess events for model switching and similar tasks.
     */
    public static void init() {
        // Subscribing as a WorkspaceSubscriber
        Hub.getWorkspace().addSubscriber(new WorkspaceSubscriber() {
            /**
             * Notifies this subscriber that a model collection change (a DES model is
             * created or opened (added), closed (removed) or renamed) has occurred in a
             * <code>WorkspacePublisher</code> to which I have subscribed.
             * 
             * @param message details of the change notification
             */
            public void modelCollectionChanged(WorkspaceMessage message) {/* NOTE not used here */
            }

            /**
             * Notifies this subscriber that a the model type has been switched (the type of
             * active model has changed e.g. from FSA to petri net) in a
             * <code>WorkspacePublisher</code> to which I have subscribed.
             * 
             * @param message details of the change notification
             */
            public void modelSwitched(WorkspaceMessage message) {
                refreshUndoRedo();
            }

            /**
             * Notifies this subscriber that a change requiring a repaint has occurred in a
             * <code>WorkspacePublisher</code> to which I have subscribed.
             */
            public void repaintRequired() { /* NOTE not used here */
            }

            public void aboutToRearrangeWorkspace() {
            }
        });
    }

    /**
     * Gets the UndoManager for a model. If no manager was initialized before, this
     * method will instantiate a new one and then retrieve it.
     * 
     * @return a reference to the UndoManager.
     */
    protected static javax.swing.undo.UndoManager getUndoManager(DESModel model) {
        if (model == null) {
            return null;
        }
        boolean modelLoaded = false;
        for (Iterator<DESModel> i = Hub.getWorkspace().getModels(); i.hasNext();) {
            if (i.next() == model) {
                modelLoaded = true;
                break;
            }
        }
        if (!modelLoaded) {
            return null;
        }
        javax.swing.undo.UndoManager manager = (javax.swing.undo.UndoManager) model.getAnnotation(UNDO_MANAGER);
        // If no UndoManager was initialized, create a new instance.
        if (manager == null) {
            manager = new RobustUndoManager();
            model.setAnnotation(UNDO_MANAGER, manager);
        }
        return manager;
    }

    /**
     * Updates the names under the Undo/Redo items on the user menu, for the active
     * UndoManager, reflecting the action to be done/undone.
     */
    protected static void refreshUndoRedo() {
        if (getUndoManager(Hub.getWorkspace().getActiveModel()) == null) {
            // refreshes the "undo" UI elements
            for (AbstractButton button : uiElementsUndo) {
                button.setEnabled(false);
            }
            for (AbstractButton button : textUIElementsUndo) {
                button.setText(Hub.string("undo"));
            }
            // refreshes the "redo" UI elements
            for (AbstractButton button : uiElementsRedo) {
                button.setEnabled(false);
            }
            for (AbstractButton button : textUIElementsRedo) {
                button.setText(Hub.string("redo"));
            }
        } else {
            // refreshes the "undo" UI elements
            for (AbstractButton button : uiElementsUndo) {
                button.setEnabled(getUndoManager(Hub.getWorkspace().getActiveModel()).canUndo());
            }
            for (AbstractButton button : textUIElementsUndo) {
                button.setText(getUndoManager(Hub.getWorkspace().getActiveModel()).getUndoPresentationName());
            }
            // refreshes the "redo" UI elements
            for (AbstractButton button : uiElementsRedo) {
                button.setEnabled(getUndoManager(Hub.getWorkspace().getActiveModel()).canRedo());
            }
            for (AbstractButton button : textUIElementsRedo) {
                button.setText(getUndoManager(Hub.getWorkspace().getActiveModel()).getRedoPresentationName());
            }
        }
    }

    /**
     * Registers an {@link AbstractButton} to be updated when the undo state
     * changes. The text of the UI element will be updated.
     * 
     * @param element the UI element to be registered for automatic updates
     */
    public void bindUndo(AbstractButton element) {
        if (!uiElementsUndo.contains(element)) {
            uiElementsUndo.add(element);
            if (!textUIElementsUndo.contains(element)) {
                textUIElementsUndo.add(element);
            }
            refreshUndoRedo();
        }
    }

    /**
     * Registers an {@link AbstractButton} to be updated when the undo state
     * changes. The text of the UI element will not be updated.
     * 
     * @param element the UI element to be registered for automatic updates
     */
    public void bindNoTextUndo(AbstractButton element) {
        if (!uiElementsUndo.contains(element)) {
            uiElementsUndo.add(element);
            refreshUndoRedo();
        }
    }

    /**
     * Registers an {@link AbstractButton} to be updated when the undo state
     * changes. The text of the UI element will be updated.
     * 
     * @param element the UI element to be registered for automatic updates
     */
    public void bindRedo(AbstractButton element) {
        if (!uiElementsRedo.contains(element)) {
            uiElementsRedo.add(element);
            if (!textUIElementsRedo.contains(element)) {
                textUIElementsRedo.add(element);
            }
            refreshUndoRedo();
        }
    }

    /**
     * Registers an {@link AbstractButton} to be updated when the undo state
     * changes. The text of the UI element will not be updated.
     * 
     * @param element the UI element to be registered for automatic updates
     */
    public void bindNoTextRedo(AbstractButton element) {
        if (!uiElementsRedo.contains(element)) {
            uiElementsRedo.add(element);
            refreshUndoRedo();
        }
    }

    /**
     * Unregisters an {@link AbstractButton} so that it is no longer updated when
     * the undo state changes.
     * 
     * @param element the UI element to be unregistered from automatic updates
     */
    public void unbind(AbstractButton element) {
        uiElementsUndo.remove(element);
        textUIElementsUndo.remove(element);
        uiElementsRedo.remove(element);
        textUIElementsRedo.remove(element);
    }

    /**
     * Action listener for the Undoable actions, perform the undo action when the
     * user press the "Undo" item in the edit menu
     */
    public static class UndoAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = 9189857780769813207L;

        public void actionPerformed(ActionEvent evt) {
            undo();
        }
    }

    /**
     * Action listener for the Undoable actions, perform the undo action when the
     * user press the "Redo" item in the edit menu
     */
    public static class RedoAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = -7676826348987922701L;

        public void actionPerformed(ActionEvent evt) {
            redo();
        }
    }

    /**
     * This method is called everytime the user clicks at the Undo action under the
     * menu bar. It calls the undo() methos under the active UndoManager (dependent
     * on the active model).
     */
    protected static void undo() {
        // Makes the active UndoManager (dependent on the model) undo an
        // operation.
        getUndoManager(Hub.getWorkspace().getActiveModel()).undo();
        // Refreshes the Undo/Redo queue.
        refreshUndoRedo();
    }

    /**
     * This method is called everytime the user clicks at the Undo action under the
     * menu bar. It calls the redo() methos under the active UndoManager (dependent
     * on the active model).
     */
    protected static void redo() {
        // Makes the active UndoManager (dependent on the model) redo an
        // operation.
        getUndoManager(Hub.getWorkspace().getActiveModel()).redo();
        // Refreshes the Undo/Redo queue.
        refreshUndoRedo();
    }

    /**
     * Adds an UndoableAction to the active manager, the action has to be always
     * added to the active model.
     */
    public void addEdit(UndoableEdit edit) {
        addEdit(Hub.getWorkspace().getActiveModel(), edit);
    }

    /**
     * Adds an UndoableEdit to the undo stack of the specified model, if the model
     * is loaded in the workspace. If the model is not in the workspace, does
     * nothing.
     * 
     * @param model model in whose undo stack the edit should be added
     * @param edit  the edit to be added in the undo stack
     */
    public void addEdit(DESModel model, UndoableEdit edit) {
        javax.swing.undo.UndoManager manager = getUndoManager(model);
        if (manager != null) {
            // Adds the edit to the active UndoableManager
            manager.addEdit(edit);
            // Refreshes the Undo/Redo queue
            refreshUndoRedo();
        }
    }

    public AbstractAction getUndoAction() {
        return new UndoAction();
    }

    public AbstractAction getRedoAction() {
        return new RedoAction();
    }

}
