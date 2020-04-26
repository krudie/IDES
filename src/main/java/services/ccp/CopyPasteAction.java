package services.ccp;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import ides.api.core.Hub;

/**
 * An all purpose cut/copy/paste listener. Gets the active CopyPastePresentation
 * from the CopyPasteManager and performs the action associated with the action
 * command of the button, or in the ACTION_COMMAND_KEY field of an instantiation
 * of this action.
 * 
 * @author Valerie Sugarman
 */
public class CopyPasteAction extends AbstractAction {
    /**
    	 * 
    	 */
    private static final long serialVersionUID = 34609113182784288L;

    public void actionPerformed(ActionEvent arg0) {
        String actionName = arg0.getActionCommand();
        Action action = null;
        if (actionName == Hub.string("cut")) {
            action = Hub.getCopyPasteManager().getActiveCopyPastePresentation().getCutAction();
        } else if (actionName == Hub.string("copy")) {
            action = Hub.getCopyPasteManager().getActiveCopyPastePresentation().getCopyAction();
        } else if (actionName == Hub.string("paste")) {
            action = Hub.getCopyPasteManager().getActiveCopyPastePresentation().getPasteAction();

        }
        if (action != null) {
            action.actionPerformed(
                    new ActionEvent(Hub.getWorkspace().getActiveModel(), ActionEvent.ACTION_PERFORMED, ""));
        }

    }

}