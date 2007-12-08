package services.latex;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import presentation.fsa.GraphLabel;
import presentation.fsa.Node;
import presentation.fsa.actions.AbstractGraphUndoableEdit;
import services.undo.UndoManager;

import main.Hub;

/**
 * The class for the "Use LaTeX rendering" menu item.
 * 
 * @author Lenko Grigorov
 */
public class UseLatexAction extends AbstractAction {

	protected class UndoableLatexSwitch extends AbstractUndoableEdit
	{
		protected boolean state;
		protected String desc;
		
		public UndoableLatexSwitch(boolean state)
		{
			this.state=state;
			if(state)
			{
				desc=Hub.string("undoLatexSwitchOn");
			}
			else
			{
				desc=Hub.string("undoLatexSwitchOff");
			}
		}

		/**
		 * Undoes a movement by applying a vector opposite to <code>displacement</code>
		 * over <code>collection</code>
		 */
		public void undo() throws CannotUndoException {
			LatexManager.setLatexEnabled(state);
			state=!state;
			Hub.getWorkspace().fireRepaintRequired();
		}

		/**
		 * Redoes a movement by applying a <code>displacement</code>
		 * over <code>collection</code>
		 */
		public void redo() throws CannotRedoException {
			LatexManager.setLatexEnabled(state);
			state=!state;
			Hub.getWorkspace().fireRepaintRequired();
		}

		public boolean canUndo() {
			return true;
		}

		public boolean canRedo() {
			return true;
		}

		/**
		 * Returns the name that should be displayed besides the Undo/Redo menu items, so the user knows
		 * which action will be undone/redone.
		 */
		public String getPresentationName() {
			return desc;
		}
	}
	
	private boolean state;
	/**
	 * Default constructor; handy for exporting this command for group setup.
	 */
	public UseLatexAction(){
		super(Hub.string("comUseLaTeX"));
		putValue(SHORT_DESCRIPTION, Hub.string("comHintUseLaTeX"));
		setSelected(LatexManager.isLatexEnabled());
	}
	
	/**
	 * Changes the property state.
	 */
	public void actionPerformed(ActionEvent evt) {
		state = !state;
//		LatexManager.setLatexEnabledFromMenu(state);
		UndoableEdit edit=new UndoableLatexSwitch(state);
		edit.redo();
		UndoManager.addEdit(edit);
	}

	public void setSelected(boolean b)
	{
		state = b;
	}
}
