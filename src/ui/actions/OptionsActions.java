package ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ides.api.core.Hub;
import ui.OptionsWindow;

/**
 * The class with the commands from the "Options" menu.
 * 
 * @author Lenko Grigorov
 */
public class OptionsActions {

    /**
     * The class for the "More options..." menu item.
     * 
     * @author Lenko Grigorov
     */
    public static class MoreOptionsAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 8948633481180579925L;

        /**
         * Default constructor; handy for exporting this command for group setup.
         */
        public MoreOptionsAction() {
            super(Hub.string("comMoreOptions"));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintMoreOptions"));
        }

        /**
         * Executes the command.
         */
        public void actionPerformed(ActionEvent e) {
            new OptionsWindow();
        }
    }

}
