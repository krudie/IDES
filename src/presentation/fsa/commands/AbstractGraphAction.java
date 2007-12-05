package presentation.fsa.commands;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import services.undo.UndoManager;

public abstract class AbstractGraphAction extends AbstractAction {

	protected CompoundEdit parentEdit=null; 
	protected boolean usePluralDescription=false;
	
	protected void postEdit(UndoableEdit edit)
	{
		if(usePluralDescription&&edit instanceof AbstractGraphUndoableEdit)
		{
			((AbstractGraphUndoableEdit)edit).setLastOfMultiple(true);
		}
		if(parentEdit!=null)
		{
			parentEdit.addEdit(edit);
		}
		else
		{
			UndoManager.addEdit(edit);
		}
	}
	
	public void setLastOfMultiple(boolean b)
	{
		usePluralDescription=b;
	}
	
	public void execute() {
		actionPerformed(null);
	}

}
