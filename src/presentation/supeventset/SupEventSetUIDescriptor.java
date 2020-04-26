package presentation.supeventset;

import javax.swing.JMenu;
import javax.swing.JToolBar;

import ides.api.model.supeventset.SupervisoryEventSet;
import ides.api.plugin.presentation.Presentation;
import ides.api.plugin.presentation.UIDescriptor;

/**
 * @author Valerie Sugarman
 */
public class SupEventSetUIDescriptor implements UIDescriptor {
    protected Presentation[] view;

    protected Presentation statusBar;

    public SupEventSetUIDescriptor(SupervisoryEventSet model) {
        view = new Presentation[1];
        view[0] = new SupEventSetView(model);
        statusBar = new SupEventSetStatusBar(model);
    }

    public Presentation[] getLeftPanePresentations() {
        // doesn't do anything in IDES 3 anyways
        return new Presentation[0];
    }

    public Presentation[] getMainPanePresentations() {
        return view;
    }

    public JMenu[] getMenus() {
        return new JMenu[0];
    }

    public Presentation[] getRightPanePresentations() {
        return new Presentation[0];
    }

    public Presentation getStatusBar() {
        return statusBar;
    }

    public JToolBar getToolbar() {
        return new JToolBar();
    }
}
