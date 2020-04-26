package services.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import ides.api.core.Hub;

public class RobustUndoManager extends UndoManager {

    /**
     * 
     */
    private static final long serialVersionUID = -57313015546231279L;

    @Override
    public void undo() {
        try {
            super.undo();
        } catch (CannotUndoException e) {
            Hub.displayAlert(Hub.string("undoFailed"));
            int idx = edits.indexOf(editToBeRedone());
            trimEdits(idx, edits.size() - 1);
        }

    }

    @Override
    public void redo() {
        try {
            super.redo();
        } catch (CannotRedoException e) {
            Hub.displayAlert(Hub.string("redoFailed"));
            int idx = edits.indexOf(editToBeUndone());
            trimEdits(idx, edits.size() - 1);
        }
    }

}
