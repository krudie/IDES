package presentation.fsa.commands;

import javax.swing.undo.AbstractUndoableEdit;

public abstract class AbstractGraphUndoableEdit extends AbstractUndoableEdit {
	
	protected boolean usePluralDescription=false;
	
	public void setLastOfMultiple(boolean b)
	{
		usePluralDescription=b;
	}
}
