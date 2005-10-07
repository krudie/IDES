/*
 * Created on Dec 2, 2004
 */
package userinterface.menu.listeners;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;

import userinterface.GraphingPlatform;
import userinterface.MainWindow;
import userinterface.ResourceManager;

/**
 * This class handles all events the fall under the "Edit" menu concept.
 * 
 * @author Michael Wood
 */
public class EditListener extends AbstractListener {

    Shell shell;

    /**
     * Construct the ListenersEdit.
     * 
     * @param graphing_platform
     *            The platform in which this ListenersEdit will exist.
     */
    public EditListener(Shell shell) {
        this.shell = shell;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // adapters
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////
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
        if (resource_handle.equals(ResourceManager.EDIT_COPY)) {
            return new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    copy(e);
                }
            };
        }
        if (resource_handle.equals(ResourceManager.EDIT_PASTE)) {
            return new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    paste(e);
                }
            };
        }
        if (resource_handle.equals(ResourceManager.EDIT_DELETE)) {
            return new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    delete(e);
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
    // listeners
    // //////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Copy the grouped objects into the EditBuffer
     * 
     * @param e
     *            The SelectionEvent that initiated this action.
     */
    public void copy(org.eclipse.swt.events.SelectionEvent e) {
        // these actions should only occur when the canvas tab is showing.
        if (MainWindow.getGraphingPlatform().tabFolder.getSelectionIndex() == GraphingPlatform.GRAPH_CANVAS_TAB) {
            MainWindow.getGraphingPlatform().gc.eb.copyCollection();
            MainWindow.getGraphingPlatform().mc.edit_paste.setEnabled(true);
        }
    }

    /**
     * Insert the graph parts from the EditBuffer into the graph, and the
     * co-ordinates given by e
     * 
     * @param e
     *            The SelectionEvent that initiated this action.
     */
    public void paste(org.eclipse.swt.events.SelectionEvent e) {
        // these actions should only occur when the canvas tab is showing.
        if (MainWindow.getGraphingPlatform().tabFolder.getSelectionIndex() == GraphingPlatform.GRAPH_CANVAS_TAB) {
            MainWindow.getGraphingPlatform().gc.eb.pasteCollection(e.x, e.y);
        }
    }

    /**
     * Delete the selected group of graph objects.
     * 
     * @param e
     *            The SelectionEvent that initiated this action.
     */
    public void delete(org.eclipse.swt.events.SelectionEvent e) {
        // these actions should only occur when the canvas tab is showing.
        if (MainWindow.getGraphingPlatform().tabFolder.getSelectionIndex() == GraphingPlatform.GRAPH_CANVAS_TAB) {
            MainWindow.getGraphingPlatform().gc.gpc.deleteGroup();
            MainWindow.getGraphingPlatform().gc.group_area.setVisible(false);
        }
    }
}