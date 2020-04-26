package presentation.fsa.actions;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import ides.api.core.Hub;
import presentation.fsa.FSAGraph;

public abstract class AbstractGraphAction extends AbstractAction {

    private static final long serialVersionUID = 8575309066575932944L;

    protected CompoundEdit parentEdit = null;

    protected boolean usePluralDescription = false;

    public AbstractGraphAction() {
        super();
    }

    public AbstractGraphAction(String name) {
        super(name);
    }

    public AbstractGraphAction(String name, Icon icon) {
        super(name, icon);
    }

    protected void postEdit(UndoableEdit edit) {
        if (usePluralDescription && edit instanceof AbstractGraphUndoableEdit) {
            ((AbstractGraphUndoableEdit) edit).setLastOfMultiple(true);
        }
        if (parentEdit != null) {
            parentEdit.addEdit(edit);
        } else {
            Hub.getUndoManager().addEdit(edit);
        }
    }

    protected void postEditAdjustCanvas(FSAGraph graph, UndoableEdit edit) {
        postEdit(addBoundsAdjust(graph, edit));
    }

    protected UndoableEdit addBoundsAdjust(FSAGraph graph, UndoableEdit edit) {
        CompoundEdit adjEdit = new CompoundEdit();
        adjEdit.addEdit(edit);
        new GraphActions.ShiftGraphInViewAction(adjEdit, graph).execute();
        adjEdit.addEdit(new GraphUndoableEdits.UndoableDummyLabel(edit.getPresentationName()));
        adjEdit.end();
        return adjEdit;
    }

    public void setLastOfMultiple(boolean b) {
        usePluralDescription = b;
    }

    public void execute() {
        actionPerformed(null);
    }

}
