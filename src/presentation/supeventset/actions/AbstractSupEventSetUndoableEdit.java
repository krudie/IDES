package presentation.supeventset.actions;

import javax.swing.undo.AbstractUndoableEdit;

public abstract class AbstractSupEventSetUndoableEdit extends AbstractUndoableEdit {

    /**
     * 
     */
    private static final long serialVersionUID = 8532773092260964731L;

    protected boolean usePluralDescription = false;

    public void setLastOfMultiple(boolean b) {
        usePluralDescription = b;
    }

}
