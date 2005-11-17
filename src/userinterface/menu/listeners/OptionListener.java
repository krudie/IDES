/*
 * Created on Jan 18, 2005
 */
package userinterface.menu.listeners;

import ides2.SystemVariables;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;

import userinterface.MainWindow;
import userinterface.ResourceManager;

/**
 * @author Micahel Wood
 */
public class OptionListener extends AbstractListener {
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // OptionListener construction
    // /////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    Shell shell = null;

    /**
     * Construct the OptionListener.
     * 
     * @param shell The shell in which this OptionListener will exist.
     */
    public OptionListener(Shell shell) {
        this.shell = shell;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // adapters///////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Find the appropriate Listener for this resource.
     * 
     * @param resource_handle
     *            The constant identification for a concept in the
     *            ResourceManager.
     * @return The appropriate Listener for this resource.
     */
    public SelectionListener getListener(String resource_handle) {
        if (resource_handle.equals(ResourceManager.OPTION_ERRORREPORT)) {
            return new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    errorReport(e);
                }
            };
        }
        if (resource_handle.equals(ResourceManager.OPTION_NODE)) {
            return new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    node(e);
                }
            };
        }
        if (resource_handle.equals(ResourceManager.OPTION_LATEX)) {
            return new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    latex(e);
                }
            };
        }
        System.out.println("Error: no match for resource_handle = "
                + resource_handle);
        return new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            }
        };
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // listeners //////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Toggle the use_error_reporting system varaible.
     * 
     * @param e
     *            The SelectionEvent that initiated this action.
     */
    public void errorReport(org.eclipse.swt.events.SelectionEvent e) {
        SystemVariables.use_error_reporting = MainWindow.getGraphingPlatform().mc.option_errorreport
                .getSelection();
    }

    /**
     * Toggle the use_standard_node_size system varaible.
     * 
     * @param e
     *            The SelectionEvent that initiated this action.
     */
    public void node(org.eclipse.swt.events.SelectionEvent e) {
        SystemVariables.use_standard_node_size = MainWindow
                .getGraphingPlatform().mc.option_node.getSelection();
        MainWindow.getGraphingPlatform().gc.gm.accomodateLabels();
        MainWindow.getGraphingPlatform().gc.repaint();
    }
    public void latex(org.eclipse.swt.events.SelectionEvent e) {
        SystemVariables.use_latex_labels = MainWindow
                .getGraphingPlatform().mc.option_uselatex.getSelection();
        MainWindow.getGraphingPlatform().gc.gm.accomodateLabels();
        MainWindow.getGraphingPlatform().gc.repaint();
    }

}