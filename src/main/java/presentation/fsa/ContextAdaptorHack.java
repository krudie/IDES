package presentation.fsa;

import ides.api.core.WorkspaceMessage;
import ides.api.core.WorkspaceSubscriber;

/**
 * FIXME this class should not exist in the first place. Fix all code to remove
 * this class.
 * 
 * @author Lenko Grigorov
 */
public class ContextAdaptorHack implements WorkspaceSubscriber {

    public static GraphDrawingView context = null;

    public void modelCollectionChanged(WorkspaceMessage message) {
    }

    public void modelSwitched(WorkspaceMessage message) {
        context = FSAToolset.getCurrentBoard();
        // if(context==null)
        // context=empty;
    }

    public void repaintRequired() {
        if (context != null) {
            context.repaint();
            context.revalidate();
        }
    }

    public void aboutToRearrangeWorkspace() {
    }

}
