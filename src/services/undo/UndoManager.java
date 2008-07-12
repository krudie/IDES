package services.undo;

import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.undo.UndoableEdit;

import main.Hub;
import main.WorkspaceMessage;
import main.WorkspaceSubscriber;

/**
 * The UndoManager is the class responsible for undoing and redoing undoable
 * actions. This class manages many models that may be opened in a workspace.
 * Its operation renames the items under the Edit-Undo/Redo menus, acording to
 * the actions to be undone/redone. Example: Undo (Create node) / Redo (Delete
 * edge)
 */
public class UndoManager
{

	protected static Vector<AbstractButton> uiElementsUndo = new Vector<AbstractButton>();

	protected static Vector<AbstractButton> textUIElementsUndo = new Vector<AbstractButton>();

	protected static Vector<AbstractButton> uiElementsRedo = new Vector<AbstractButton>();

	protected static Vector<AbstractButton> textUIElementsRedo = new Vector<AbstractButton>();

	protected final static String UNDO_MANAGER = "undoManager";

	// Make the class non-instantiable.
	private UndoManager()
	{
	}

	@Override
	public Object clone()
	{
		throw new RuntimeException("Cloning of " + this.getClass().toString()
				+ " not supported.");
	}

	/**
	 * Initializes the UndoManager. The UndoManager subscribes itself to the
	 * workspace, so it can proccess events for model switching and similar
	 * tasks.
	 * 
	 * @param undoItem
	 *            the menu item which will be updated to display the current
	 *            undoable event. This should be the "Undo" menu item in the
	 *            "Edit" menu.
	 * @param redoItem
	 *            the menu item which will be updated to display the current
	 *            redoable event. This should be the "Redo" menu item in the
	 *            "Edit" menu.
	 */
	public static void init()
	{
		// Subscribing as a WorkspaceSubscriber
		Hub.getWorkspace().addSubscriber(new WorkspaceSubscriber()
		{
			/**
			 * Notifies this subscriber that a model collection change (a DES
			 * model is created or opened (added), closed (removed) or renamed)
			 * has occurred in a <code>WorkspacePublisher</code> to which I
			 * have subscribed.
			 * 
			 * @param message
			 *            details of the change notification
			 */
			public void modelCollectionChanged(WorkspaceMessage message)
			{/* NOTE not used here */
			}

			/**
			 * Notifies this subscriber that a the model type has been switched
			 * (the type of active model has changed e.g. from FSA to petri net)
			 * in a <code>WorkspacePublisher</code> to which I have
			 * subscribed.
			 * 
			 * @param message
			 *            details of the change notification
			 */
			public void modelSwitched(WorkspaceMessage message)
			{
				refreshUndoRedo();
			}

			/**
			 * Notifies this subscriber that a change requiring a repaint has
			 * occurred in a <code>WorkspacePublisher</code> to which I have
			 * subscribed.
			 */
			public void repaintRequired()
			{ /* NOTE not used here */
			}

			public void aboutToRearrangeWorkspace()
			{
			}
		});
	}

	/**
	 * Gets the active UndoManager. Since the active manager is dependent on the
	 * active model, this method queries the workspace about the name of the
	 * active model and then access the HasMap for the UndoManager for that
	 * model. If no manager was initialized before, this method will instantiate
	 * a new one and then retrieve it.
	 * 
	 * @return a reference to the active UndoManager.
	 */
	protected static javax.swing.undo.UndoManager getActiveUndoManager()
	{
		if (Hub.getWorkspace().getActiveModel() == null)
		{
			return null;
		}
		javax.swing.undo.UndoManager currentManager = (javax.swing.undo.UndoManager)Hub
				.getWorkspace().getActiveModel().getAnnotation(UNDO_MANAGER);
		// If no UndoManager was initialized, create a new instance.
		if (currentManager == null)
		{
			currentManager = new RobustUndoManager();
			Hub.getWorkspace().getActiveModel().setAnnotation(UNDO_MANAGER,
					currentManager);
		}
		return currentManager;
	}

	/**
	 * This method is called everytime the user clicks at the Undo action under
	 * the menu bar. It calls the undo() methos under the active UndoManager
	 * (dependent on the active model).
	 */
	protected static void undo()
	{
		// Makes the active UndoManager (dependent on the model) undo an
		// operation.
		getActiveUndoManager().undo();
		// Refreshes the Undo/Redo queue.
		refreshUndoRedo();
	}

	/**
	 * This method is called everytime the user clicks at the Undo action under
	 * the menu bar. It calls the redo() methos under the active UndoManager
	 * (dependent on the active model).
	 */
	protected static void redo()
	{
		// Makes the active UndoManager (dependent on the model) redo an
		// operation.
		getActiveUndoManager().redo();
		// Refreshes the Undo/Redo queue.
		refreshUndoRedo();
	}

	/**
	 * Adds an UndoableAction to the active manager, the action has to be always
	 * added to the active model.
	 */
	public static void addEdit(UndoableEdit edit)
	{
		// Adds the edit to the active UndoableManager
		getActiveUndoManager().addEdit(edit);
		// Refreshes the Undo/Redo queue
		refreshUndoRedo();
	}

	/**
	 * Updates the names under the Undo/Redo items on the user menu, for the
	 * active UndoManager, reflecting the action to be done/undone.
	 */
	protected static void refreshUndoRedo()
	{
		if (getActiveUndoManager() == null)
		{
			// refreshes the "undo" UI elements
			for (AbstractButton button : uiElementsUndo)
			{
				button.setEnabled(false);
			}
			for (AbstractButton button : textUIElementsUndo)
			{
				button.setText(Hub.string("undo"));
			}
			// refreshes the "redo" UI elements
			for (AbstractButton button : uiElementsRedo)
			{
				button.setEnabled(false);
			}
			for (AbstractButton button : textUIElementsRedo)
			{
				button.setText(Hub.string("redo"));
			}
		}
		else
		{
			// refreshes the "undo" UI elements
			for (AbstractButton button : uiElementsUndo)
			{
				button.setEnabled(getActiveUndoManager().canUndo());
			}
			for (AbstractButton button : textUIElementsUndo)
			{
				button
						.setText(getActiveUndoManager()
								.getUndoPresentationName());
			}
			// refreshes the "redo" UI elements
			for (AbstractButton button : uiElementsRedo)
			{
				button.setEnabled(getActiveUndoManager().canRedo());
			}
			for (AbstractButton button : textUIElementsRedo)
			{
				button
						.setText(getActiveUndoManager()
								.getRedoPresentationName());
			}
		}
	}

	/**
	 * Registers an {@link AbstractButton} to be updated when the undo state
	 * changes. The text of the UI element will be updated.
	 * 
	 * @param element
	 *            the UI element to be registered for automatic updates
	 */
	public static void bindUndo(AbstractButton element)
	{
		if (!uiElementsUndo.contains(element))
		{
			uiElementsUndo.add(element);
			if (!textUIElementsUndo.contains(element))
			{
				textUIElementsUndo.add(element);
			}
			refreshUndoRedo();
		}
	}

	/**
	 * Registers an {@link AbstractButton} to be updated when the undo state
	 * changes. The text of the UI element will not be updated.
	 * 
	 * @param element
	 *            the UI element to be registered for automatic updates
	 */
	public static void bindNoTextUndo(AbstractButton element)
	{
		if (!uiElementsUndo.contains(element))
		{
			uiElementsUndo.add(element);
			refreshUndoRedo();
		}
	}

	/**
	 * Registers an {@link AbstractButton} to be updated when the undo state
	 * changes. The text of the UI element will be updated.
	 * 
	 * @param element
	 *            the UI element to be registered for automatic updates
	 */
	public static void bindRedo(AbstractButton element)
	{
		if (!uiElementsRedo.contains(element))
		{
			uiElementsRedo.add(element);
			if (!textUIElementsRedo.contains(element))
			{
				textUIElementsRedo.add(element);
			}
			refreshUndoRedo();
		}
	}

	/**
	 * Registers an {@link AbstractButton} to be updated when the undo state
	 * changes. The text of the UI element will not be updated.
	 * 
	 * @param element
	 *            the UI element to be registered for automatic updates
	 */
	public static void bindNoTextRedo(AbstractButton element)
	{
		if (!uiElementsRedo.contains(element))
		{
			uiElementsRedo.add(element);
			refreshUndoRedo();
		}
	}

	/**
	 * Unregisters an {@link AbstractButton} so that it is no longer updated
	 * when the undo state changes.
	 * 
	 * @param element
	 *            the UI element to be unregistered from automatic updates
	 */
	public static void unbind(AbstractButton element)
	{
		uiElementsUndo.remove(element);
		textUIElementsUndo.remove(element);
		uiElementsRedo.remove(element);
		textUIElementsRedo.remove(element);
	}

	/**
	 * Action listener for the Undoable actions, perform the undo action when
	 * the user press the "Undo" item in the edit menu
	 */
	public static class UndoAction extends AbstractAction
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 9189857780769813207L;

		public void actionPerformed(ActionEvent evt)
		{
			undo();
		}
	}

	/**
	 * Action listener for the Undoable actions, perform the undo action when
	 * the user press the "Redo" item in the edit menu
	 */
	public static class RedoAction extends AbstractAction
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -7676826348987922701L;

		public void actionPerformed(ActionEvent evt)
		{
			redo();
		}
	}
}
