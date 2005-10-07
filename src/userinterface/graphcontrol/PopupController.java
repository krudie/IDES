/*
 * Created on Dec 14, 2004
 */
package userinterface.graphcontrol;

import ides2.SystemVariables;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import userinterface.GraphingPlatform;
import userinterface.ResourceManager;
import userinterface.geometric.Point;
import userinterface.graphcontrol.graphparts.Edge;
import userinterface.graphcontrol.graphparts.GraphObject;
import userinterface.graphcontrol.graphparts.Node;

/**
 * This class handles all popup menus for the GraphController. Note that while
 * many menus are in sync with the menu-controller, any menu can break free and
 * behave in entirely cusom ways.
 * 
 * @author MichaelWood
 */
public class PopupController {
    /**
     * The platform in which this GraphObject exists.
     */
    private GraphingPlatform gp = null;

    /**
     * Popup menus for graph parts on the j2d canvas
     */
    public Menu mnu_node = null, mnu_edge = null, mnu_internal = null,
            mnu_external = null;

    /**
     * Menu items for the popup menu
     */
    public MenuItem mitm_node_startstate = null, mitm_node_markedstate = null,
            mitm_node_label = null, mitm_sperator = null;

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PopupController construction
    // ///////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construct the PopupController.
     * 
     * @param gp
     *            The platform in which this PopupController will exist.
     */
    public PopupController(GraphingPlatform gp) {
        this.gp = gp;
        initializeEdgePopup();
        initializeNodePopup();
        initializeAreaPopup();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Node Popup
    // /////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Initialize the node popup menu.
     */
    private void initializeNodePopup() {
        // create the node popup menu.
        mnu_node = new Menu(gp.shell, SWT.POP_UP);

        mitm_node_startstate = new MenuItem(mnu_node, SWT.CHECK);
        mitm_node_startstate.setText(ResourceManager
                .getString("node_startstate.mtext"));
        mitm_node_markedstate = new MenuItem(mnu_node, SWT.CHECK);
        mitm_node_markedstate.setText(ResourceManager
                .getString("node_markedstate.mtext"));
        mitm_sperator = new MenuItem(mnu_node, SWT.SEPARATOR);
        mitm_node_label = new MenuItem(mnu_node, SWT.PUSH);
        mitm_node_label.setText(ResourceManager.getString("node_label.mtext"));
        mitm_sperator = new MenuItem(mnu_node, SWT.SEPARATOR);
        MenuItem mitm_node_reset = new MenuItem(mnu_node, SWT.PUSH);
        mitm_node_reset.setText(ResourceManager.getString("node_reset.mtext"));
        mitm_sperator = new MenuItem(mnu_node, SWT.SEPARATOR);
        MenuItem mitm_node_delete = gp.mc.edit_delete.addPopupMitm(mnu_node,
                true, true);

        mnu_node.addMenuListener(new MenuListener() {
            public void menuHidden(MenuEvent e) {
                if (gp.gc.menued_object != null) {
                    gp.gc.menued_object
                            .removeAttribute(GraphObject.HOT_SELECTED);
                    gp.gc.j2dcanvas.repaint();
                }
                gp.gc.j2dcanvas.setMenu(null);
            }

            public void menuShown(MenuEvent e) {
            }
        });

        mitm_node_startstate.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                if (gp.gc.menued_object != null) {
                    if (mitm_node_startstate.getSelection()) {
                        if (gp.gc.start_state_node != null) {
                            gp.gc.start_state_node
                                    .removeAttribute(GraphObject.START_STATE);
                        }
                        gp.gc.menued_object
                                .addAttribute(GraphObject.START_STATE);
                        gp.gc.start_state_node = ((Node) gp.gc.menued_object);
                    } else {
                        gp.gc.menued_object
                                .removeAttribute(GraphObject.START_STATE);
                        gp.gc.start_state_node = null;
                    }
                    gp.gc.io.markUnsavedChanges();
                }
                gp.gc.j2dcanvas.repaint();
            }
        });

        mitm_node_markedstate.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                if (gp.gc.menued_object != null) {
                    if (mitm_node_markedstate.getSelection()) {
                        gp.gc.menued_object
                                .addAttribute(GraphObject.MARKED_STATE);
                    } else {
                        gp.gc.menued_object
                                .removeAttribute(GraphObject.MARKED_STATE);
                    }
                    gp.gc.menued_object.accomodateLabel();
                    gp.gc.io.markUnsavedChanges();
                    gp.gc.j2dcanvas.repaint();
                }
            }
        });

        mitm_node_label.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                if (gp.gc.menued_object != null) {
                    gp.gc.floating_text.initialize(((Node) gp.gc.menued_object)
                            .origin(), ((Node) gp.gc.menued_object).origin(),
                            ((Node) gp.gc.menued_object).glyph_label);
                    gp.gc.floating_text.setVisible(true);
                }
            }
        });

        mitm_node_reset.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                if (gp.gc.menued_object != null) {
                    ((Node) gp.gc.menued_object).resetEdges();
                    gp.gc.io.markUnsavedChanges();
                    gp.gc.j2dcanvas.repaint();
                }
            }
        });

        mitm_node_delete.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                if (gp.gc.menued_object != null) {
                    gp.gc.menued_object.delete();
                    gp.gc.io.markUnsavedChanges();
                    gp.gc.j2dcanvas.repaint();
                }
            }
        });
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Edge Popup
    // /////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Initialize the edge popup menu.
     */
    private void initializeEdgePopup() {
        // start the edge popup menu.
        mnu_edge = new Menu(gp.shell, SWT.POP_UP);

        MenuItem mitm_edge_label = new MenuItem(mnu_edge, SWT.PUSH);
        mitm_edge_label.setText(ResourceManager.getString("edge_label.mtext"));
        mitm_sperator = new MenuItem(mnu_edge, SWT.SEPARATOR);
        MenuItem mitm_edge_reset = new MenuItem(mnu_edge, SWT.PUSH);
        mitm_edge_reset.setText(ResourceManager.getString("edge_reset.mtext"));
        MenuItem mitm_edge_straighten = new MenuItem(mnu_edge, SWT.PUSH);
        mitm_edge_straighten.setText(ResourceManager
                .getString("edge_straighten.mtext"));
        MenuItem mitm_edge_arcmore = new MenuItem(mnu_edge, SWT.PUSH);
        mitm_edge_arcmore.setText(ResourceManager
                .getString("edge_arcmore.mtext"));
        MenuItem mitm_edge_arcless = new MenuItem(mnu_edge, SWT.PUSH);
        mitm_edge_arcless.setText(ResourceManager
                .getString("edge_arcless.mtext"));
        MenuItem mitm_edge_reverse = new MenuItem(mnu_edge, SWT.PUSH);
        mitm_edge_reverse.setText(ResourceManager
                .getString("edge_reverse.mtext"));
        mitm_sperator = new MenuItem(mnu_edge, SWT.SEPARATOR);
        MenuItem mitm_edge_delete = gp.mc.edit_delete.addPopupMitm(mnu_edge,
                true, true);

        mnu_edge.addMenuListener(new MenuListener() {
            public void menuHidden(MenuEvent e) {
                if (gp.gc.menued_object != null) {
                    gp.gc.menued_object
                            .removeAttribute(GraphObject.HOT_SELECTED);
                    gp.gc.j2dcanvas.repaint();
                }
                gp.gc.j2dcanvas.setMenu(null);
            }

            public void menuShown(MenuEvent e) {
            }
        });

        mitm_edge_label.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                if (gp.gc.menued_object != null) {
                    if (gp.td.edges_table.getItems().length > 0) {
                        gp.gc.floating_toggles.initialize((Point) mnu_edge
                                .getData(), (Edge) gp.gc.menued_object);
                        gp.gc.floating_toggles.setVisible(true);
                    } else {
                        gp.tabFolder
                                .setSelection(GraphingPlatform.SPECIFICATIONS_TAB);
                    }
                }
            }
        });

        mitm_edge_reset.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                if (gp.gc.menued_object != null) {
                    gp.gc.menued_object.addAttribute(GraphObject.SIMPLE);
                    ((Edge) gp.gc.menued_object).autoConfigureCurve();
                    gp.gc.menued_object.accomodateLabel();
                    gp.gc.io.markUnsavedChanges();
                    gp.gc.j2dcanvas.repaint();
                }
            }
        });

        mitm_edge_reverse.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                if (gp.gc.menued_object != null) {
                    ((Edge) gp.gc.menued_object).reverseDirection();
                    gp.gc.io.markUnsavedChanges();
                    gp.gc.j2dcanvas.repaint();
                }
            }
        });

        mitm_edge_straighten.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                if (gp.gc.menued_object != null) {
                    ((Edge) gp.gc.menued_object).autoStraightenCurve();
                    gp.gc.io.markUnsavedChanges();
                    gp.gc.j2dcanvas.repaint();
                }
            }
        });

        mitm_edge_arcmore.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                if (gp.gc.menued_object != null) {
                    ((Edge) gp.gc.menued_object).autoArcMore();
                    gp.gc.io.markUnsavedChanges();
                    gp.gc.j2dcanvas.repaint();
                }
            }
        });

        mitm_edge_arcless.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                if (gp.gc.menued_object != null) {
                    ((Edge) gp.gc.menued_object).autoArcLess();
                    gp.gc.io.markUnsavedChanges();
                    gp.gc.j2dcanvas.repaint();
                }
            }
        });

        mitm_edge_delete.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                if (gp.gc.menued_object != null) {
                    gp.gc.menued_object.delete();
                    gp.gc.io.markUnsavedChanges();
                    gp.gc.j2dcanvas.repaint();
                }
            }
        });
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Area Popup
    // /////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Initialize the area popup menu.
     */
    private void initializeAreaPopup() {
        // create the internal popup menu.
        mnu_internal = new Menu(gp.shell, SWT.POP_UP);
        MenuItem mitm_internal_snap = new MenuItem(mnu_internal, SWT.PUSH);
        mitm_internal_snap.setText(ResourceManager
                .getString("internal_snap.mtext"));
        MenuItem mitm_internal_reset = new MenuItem(mnu_internal, SWT.PUSH);
        mitm_internal_reset.setText(ResourceManager
                .getString("internal_reset.mtext"));
        mitm_sperator = new MenuItem(mnu_internal, SWT.SEPARATOR);
        gp.mc.edit_copy.addPopupMitm(mnu_internal);
        mitm_sperator = new MenuItem(mnu_internal, SWT.SEPARATOR);
        gp.mc.edit_delete.addPopupMitm(mnu_internal);
        mnu_internal.addMenuListener(new MenuListener() {
            public void menuHidden(MenuEvent e) {
                gp.gc.j2dcanvas.setMenu(null);
            }

            public void menuShown(MenuEvent e) {
            }
        });

        mitm_internal_reset.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                gp.gc.gpc.resetConfiguration();
                gp.gc.io.markUnsavedChanges();
                gp.gc.j2dcanvas.repaint();
            }
        });

        mitm_internal_snap.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                gp.gc.gpc.snapToGrid();
                gp.gc.io.markUnsavedChanges();
                gp.gc.j2dcanvas.repaint();
            }
        });

        // create the external popup menu.
        mnu_external = new Menu(gp.shell, SWT.POP_UP);
        mitm_sperator = new MenuItem(mnu_external, SWT.SEPARATOR);
        MenuItem mitm_external_paste = gp.mc.edit_paste.addPopupMitm(
                mnu_external, true, false);
        mnu_external.addMenuListener(new MenuListener() {
            public void menuHidden(MenuEvent e) {
                gp.gc.j2dcanvas.setMenu(null);
            }

            public void menuShown(MenuEvent e) {
            }
        });

        // note that we provide a cusotm Adapter instead of using the default
        // provided by the UnifiedMenu
        mitm_external_paste
                .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                    public void widgetSelected(
                            org.eclipse.swt.events.SelectionEvent e) {
                        Point offset = (Point) mnu_external.getData();
                        e.x = offset.x;
                        e.y = offset.y;
                        gp.mc.editListener.paste(e);
                    }
                });
    }
}