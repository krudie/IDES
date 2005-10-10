/*
 * Created on Jan 29, 2005
 */
package userinterface.graphcontrol;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import userinterface.GraphingPlatform;
import userinterface.geometric.Point;
import userinterface.graphcontrol.graphparts.Edge;

/**
 * @author Michael Wood
 */
public class FloatingToggles {

    /**
     * The platform in which this GraphObject exists.
     */
    protected GraphingPlatform gp = null;

    /**
     * The Shell that displays this FloatingText.
     */
    private Shell shell = null;

    /**
     * Helps hide the window and prevents running the deactivate routine twice
     */
    private boolean is_live = false;

    /**
     * The Edge being worked on.
     */
    private Edge edge = null;

    /**
     * The array of all toggle buttons in the shell.
     */
    private Button[] btns = null;

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // FloatingToggles construction ///////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construct the FloatingToggles.
     * 
     * @param gp
     *            The platform in which this FloatingToggles will exist.
     */
    public FloatingToggles(GraphingPlatform gp) {
        this.gp = gp;
        populate();
    }

    public void populate() {
        if (shell != null) {
            if (!shell.isDisposed()) {
                shell.dispose();
            }
            shell = null;
        }

        //TODO: indkommenter
        //TableItem[] table_items = gp.td.edges_table.getItems();

        shell = new Shell(gp.shell, SWT.ON_TOP | SWT.RESIZE);
        GridLayout grid_layout = new GridLayout();
        //grid_layout.numColumns = Math.max((int) Math.round(Math.sqrt(table_items.length)), 1);
        grid_layout.marginWidth = 2;
        grid_layout.marginHeight = 2;
        grid_layout.verticalSpacing = 2;
        grid_layout.horizontalSpacing = 2;
        shell.setLayout(grid_layout);

        //Button btn = null;
        //GridData gd = null;
        //btns = new Button[table_items.length];
        //for (int i = 0; i < table_items.length; i++) {
        //    btn = new Button(shell, SWT.TOGGLE);
        //    btn.setText(table_items[i].getText(TransitionDaEventSpecification));
        //    btn.setData(table_items[i]);
        //    btn.addListener(SWT.Selection, new Listener() {
        //        public void handleEvent(Event e) {
        //            buttonToggleAction(e);
        //        }
        //    });
        //    gd = new GridData(GridData.FILL_BOTH);
        //    btn.setLayoutData(gd);
        //   btns[i] = btn;
        //}

        shell.layout();
        shell.pack();

        shell.addShellListener(new ShellListener() {
            public void shellDeactivated(ShellEvent e) {
                shellDeactivatedAction();
            }

            public void shellActivated(ShellEvent e) {
            }

            public void shellClosed(ShellEvent e) {
            }

            public void shellDeiconified(ShellEvent e) {
            }

            public void shellIconified(ShellEvent e) {
            }
        });
    }

    private void shellDeactivatedAction() {
        // if they click on MAIN or EXTERNAL then we need to hide FLOAT
        if (shell.getVisible()) {
            shell.setVisible(false);
        }

        if (is_live) {
            is_live = false;
        }
    }

    private void buttonToggleAction(Event e) {
        if (edge != null) {
            if (((Button) e.widget).getSelection()) {
                edge.addLabel((TableItem) (((Button) e.widget).getData()));
            } else {
                edge.removeLabel((TableItem) (((Button) e.widget).getData()));
            }
            edge.accomodateLabel();
            gp.gc.io.markUnsavedChanges();
            gp.gc.j2dcanvas.repaint();
            gp.gc.j2dcanvas.update();
        }

    }

    public void initialize(Point scaled_origin, Edge edge) {
        this.edge = edge;

        for (int i = 0; i < btns.length; i++) {
            if (edge.checkLabel(btns[i].getData())) {
                btns[i].setSelection(true);
            } else {
                btns[i].setSelection(false);
            }
        }

        Rectangle display_bounds = shell.getDisplay().getBounds();
        Rectangle shell_bounds = shell.getBounds();
        shell_bounds.x = Math.max(Math.min(gp.gc.j2dcanvas.toDisplay(
                scaled_origin.getX(), scaled_origin.getX()).x, display_bounds.width
                - shell_bounds.width), 0);
        shell_bounds.y = Math.max(Math.min(gp.gc.j2dcanvas.toDisplay(
                scaled_origin.getX(), scaled_origin.getY()).y, display_bounds.height
                - shell_bounds.height), 0);
        shell.setBounds(shell_bounds);
        shell.forceActive();

        is_live = true;
    }

    public void setVisible(boolean visibility) {
        shell.setVisible(visibility);
    }

    public void dispose() {
        shell.dispose();
    }
}