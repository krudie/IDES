package ides.api.undo;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.undo.UndoableEdit;

import ides.api.plugin.model.DESModel;

/**
 * Interface to the IDES UndoManager. The manager is responsible for undoing and
 * redoing undoable actions. It automatically manages the undo stacks for the
 * many models that may be opened in a workspace. Registered UI elements are
 * automatically updated with the changing status of the undo system.
 * 
 * @author Lenko Grigorov
 */
public interface UndoManager {
    /**
     * Adds an UndoableEdit to the undo stack of the active model.
     */
    public void addEdit(UndoableEdit edit);

    /**
     * Adds an UndoableEdit to the undo stack of the specified model, if the model
     * is loaded in the workspace. If the model is not in the workspace, does
     * nothing.
     * 
     * @param model model in whose undo stack the edit should be added
     * @param edit  the edit to be added in the undo stack
     */
    public void addEdit(DESModel model, UndoableEdit edit);

    /**
     * Registers an {@link AbstractButton} to be updated when the undo state
     * changes. The text of the UI element will be updated.
     * 
     * @param element the UI element to be registered for automatic updates
     */
    public void bindUndo(AbstractButton element);

    /**
     * Registers an {@link AbstractButton} to be updated when the undo state
     * changes. The text of the UI element will not be updated.
     * 
     * @param element the UI element to be registered for automatic updates
     */
    public void bindNoTextUndo(AbstractButton element);

    /**
     * Registers an {@link AbstractButton} to be updated when the undo state
     * changes. The text of the UI element will be updated.
     * 
     * @param element the UI element to be registered for automatic updates
     */
    public void bindRedo(AbstractButton element);

    /**
     * Registers an {@link AbstractButton} to be updated when the undo state
     * changes. The text of the UI element will not be updated.
     * 
     * @param element the UI element to be registered for automatic updates
     */
    public void bindNoTextRedo(AbstractButton element);

    /**
     * Unregisters an {@link AbstractButton} so that it is no longer updated when
     * the undo state changes.
     * 
     * @param element the UI element to be unregistered from automatic updates
     */
    public void unbind(AbstractButton element);

    /**
     * Returns an action which undoes the last entry in the undo manager, if any.
     * 
     * @return an action which undoes the last entry in the undo manager
     */
    public AbstractAction getUndoAction();

    /**
     * Returns an action which redoes the last undone entry in the undo manager, if
     * any.
     * 
     * @return an action which redoes the last undone entry in the undo manager
     */
    public AbstractAction getRedoAction();

}
