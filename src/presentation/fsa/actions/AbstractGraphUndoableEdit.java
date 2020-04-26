package presentation.fsa.actions;

import javax.swing.undo.AbstractUndoableEdit;

public abstract class AbstractGraphUndoableEdit extends AbstractUndoableEdit {
    private static final long serialVersionUID = 5971155434703142571L;

    protected boolean usePluralDescription = false;

    public void setLastOfMultiple(boolean b) {
        usePluralDescription = b;
    }
}
