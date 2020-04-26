package services.latex;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ides.api.core.Hub;

/**
 * The class for the "Use LaTeX rendering" menu item.
 * 
 * @author Lenko Grigorov
 */
public class UseLatexAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = -8562721652171460273L;

    /**
     * Default constructor; handy for exporting this command for group setup.
     */
    public UseLatexAction() {
        super(Hub.string("comUseLaTeX"));
        putValue(SHORT_DESCRIPTION, Hub.string("comHintUseLaTeX"));
    }

    /**
     * Changes the property state.
     */
    public void actionPerformed(ActionEvent evt) {
        LatexBackend.instance().setLatexEnabled(!LatexBackend.instance().isLatexEnabled());
    }
}
