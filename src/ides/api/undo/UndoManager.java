package ides.api.undo;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.undo.UndoableEdit;

/**
 * Interface to the IDES UndoManager. The manager is responsible for undoing and
 * redoing undoable actions. It automatically manages the undo stacks for the
 * many models that may be opened in a workspace. Registered UI elements are
 * automatically updated with the changing status of the undo system.
 * 
 * @author Lenko Grigorov
 */
public interface UndoManager
{
	/**
	 * Adds an UndoableAction to the active manager, the action has to be always
	 * added to the active model.
	 */
	public void addEdit(UndoableEdit edit);

	/**
	 * Registers an {@link AbstractButton} to be updated when the undo state
	 * changes. The text of the UI element will be updated.
	 * 
	 * @param element
	 *            the UI element to be registered for automatic updates
	 */
	public void bindUndo(AbstractButton element);

	/**
	 * Registers an {@link AbstractButton} to be updated when the undo state
	 * changes. The text of the UI element will not be updated.
	 * 
	 * @param element
	 *            the UI element to be registered for automatic updates
	 */
	public void bindNoTextUndo(AbstractButton element);

	/**
	 * Registers an {@link AbstractButton} to be updated when the undo state
	 * changes. The text of the UI element will be updated.
	 * 
	 * @param element
	 *            the UI element to be registered for automatic updates
	 */
	public void bindRedo(AbstractButton element);

	/**
	 * Registers an {@link AbstractButton} to be updated when the undo state
	 * changes. The text of the UI element will not be updated.
	 * 
	 * @param element
	 *            the UI element to be registered for automatic updates
	 */
	public void bindNoTextRedo(AbstractButton element);

	/**
	 * Unregisters an {@link AbstractButton} so that it is no longer updated
	 * when the undo state changes.
	 * 
	 * @param element
	 *            the UI element to be unregistered from automatic updates
	 */
	public void unbind(AbstractButton element);

	/**
	 * Returns an action which undoes the last entry in the undo manager, if
	 * any.
	 * 
	 * @return an action which undoes the last entry in the undo manager
	 */
	public AbstractAction getUndoAction();

	/**
	 * Returns an action which redoes the last undone entry in the undo manager,
	 * if any.
	 * 
	 * @return an action which redoes the last undone entry in the undo manager
	 */
	public AbstractAction getRedoAction();

}
