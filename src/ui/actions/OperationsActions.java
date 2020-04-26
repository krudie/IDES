/**
 * 
 */
package ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ides.api.core.Hub;
import ui.OperationDialog;

/**
 * @author Lenko Grigorov
 */
public class OperationsActions {

    public static class ShowDialogAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 4642290900651487727L;

        public ShowDialogAction() {
            super(Hub.string("comOperationsDialog"));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintOperationsDialog"));
        }

        public void actionPerformed(ActionEvent evt) {
            new OperationDialog();
        }

    }
}
