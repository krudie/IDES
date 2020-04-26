package presentation.supeventset.actions;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import ides.api.core.Hub;

public abstract class AbstractSupEventSetAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = -6480933439174015445L;

    protected CompoundEdit parentEdit = null;

    protected boolean usePluralDescription = false;

    public AbstractSupEventSetAction() {
        super();
    }

    public AbstractSupEventSetAction(String name) {
        super(name);
    }

    public AbstractSupEventSetAction(String name, Icon icon) {
        super(name, icon);
    }

    protected void postEdit(UndoableEdit edit) {
        if (usePluralDescription && edit instanceof AbstractSupEventSetUndoableEdit) {
            ((AbstractSupEventSetUndoableEdit) edit).setLastOfMultiple(true);
        }
        if (parentEdit != null) {
            parentEdit.addEdit(edit);
        } else {
            Hub.getUndoManager().addEdit(edit);
        }
    }

    public void setLastOfMultiple(boolean b) {
        usePluralDescription = b;
    }

    public void execute() {
        actionPerformed(null);
    }
}
