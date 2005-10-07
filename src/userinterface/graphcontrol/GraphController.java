/*
 * Created on Jun 22, 2004
 */
package userinterface.graphcontrol;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Date;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.TableItem;
import org.holongate.j2d.J2DCanvas;

import userinterface.geometric.Box;
import userinterface.geometric.Point;
import userinterface.GraphingPlatform;
import userinterface.ResourceManager;
import ides2.SystemVariables;
import userinterface.graphcontrol.graphparts.Edge;
import userinterface.graphcontrol.graphparts.GraphObject;
import userinterface.graphcontrol.graphparts.Node;

/**
 * This class handles the creation and management of everything inside the
 * Graphing tab of the main application. Much of the controll functionality is
 * handled by seperate helper classes that exist with a 1:1 relationship with
 * this class. These include, the Drawer, EditBuffer, GraphControllerIO,
 * GraphPartCollection, LatexPrinter, PaintingController, PopupController.
 * 
 * The GraphModel is currently on a 1:1 realtionship with the GraphController,
 * but it is hoped that in the future we will have multiple GraphModels withing
 * the GraphController.
 * 
 * The SelectionArea is used twice to represent both the PrintArea and the
 * GroupingArea.
 * 
 * @author Michael Wood
 */
public class GraphController {

    /**
     * The platform in which this GraphController will exist.
     */
    private GraphingPlatform gp = null;

    /**
     * The GraphModel
     */
    public GraphModel gm = null;

    /**
     * The object that will handle the filesystem io for this GraphController.
     */
    public GraphControllerIO io = null;

    /**
     * The area used to create group selections
     */
    public SelectionArea group_area = null;

    /**
     * Records which graph parts are in the most recent grouping.
     */
    public GraphPartCollection gpc = null;

    /**
     * Clones and holds graph part collections for the purpose of copy and paste
     */
    public EditBuffer eb = null;

    /**
     * Manages all popups for the canvas area
     */
    public PopupController pc = null;

    /**
     * Constants for reference to the various ToolItems.
     */
    public final int CREATE_TOOL = 1,
                     MODIFY_TOOL = 3,
                     GRAB_TOOL = 7,
                     GRID_TOOL = 8,
                     PRINT_AREA_TOOL = 9,
                     ZOOM_TOOL = 10;

    /**
     * This objects facilitates the painting on the J2DCanvas
     */
    private PaintingController painting_controller = null;

    /**
     * The canvas on which the GraphModel is displayed. We use the J2DCanvas for
     * antialiasing and other advanced features.
     */
    public J2DCanvas j2dcanvas = null;

    /**
     * Used when creating an Edge. Counts the time between mouse down and mouse
     * up. If the time is short, interprets as a single click and asssumes edge
     * creation is a singleclick -- move -- singleclick process If the time is
     * long, assumes edge creation is a mousedown -- mousemove -- mouseup
     * process
     */
    private long click_time = 0;

    /**
     * Because of the click_time logic, in order to create a self loop when a
     * double click occurs, we must use the double click event. we don't want to
     * include the double click events where the first click was compleation of
     * an edge at this node and the second click is the creation of a new edge
     * at this node hence we need this flag.
     */
    private boolean last_up_finished_an_edge = false;

    /**
     * This lets us destinguish between a double click on a node (which should
     * create a self loop) and a double click on blank space (which should
     * create a node and start and edge from it)
     */
    private boolean last_down_created_an_edge = false;

    /**
     * Records the origion of displacement for the grab tool. values:
     * {x,y,state} state = 0 -> not in mid grab state = 1 -> in mid grab
     */
    private int[] grab_origin = null;

    /**
     * Colours for objects to be drawn in the graph.
     */
    public final Color black = new Color(0, 0, 0),
                       red = new Color(255, 0, 0),
                       blue = new Color(0, 0, 255),
                       green = new Color(0, 64, 0),
                       dark_red = new Color(128, 0, 0),
                       grey = new Color(180, 180, 180);

    /**
     * Colours for objects to be drawn in the graph. Indices correspond to the
     * constants in the GraphModel and the states in the Edges and Nodes.
     */
    public final Color[] color_set = { black, red, blue, green, dark_red, grey };

    /**
     * The drawing line width for all major objects in the graph.
     */
    public int line_width = 2;

    /**
     * The currently selected ToolItem.
     */
    public int selected_tool = 0;

    /**
     * Records if a mouse button is currently depressed. This is only used in
     * association with left clicks.
     */
    private boolean left_mouse_is_down = false;

    /**
     * Records if the current mouse-up is the second half of a double-click
     */
    private boolean is_double_click = false;

    /**
     * Whether or not to draw the grid.
     */
    public boolean draw_grid = false;

    /**
     * Records if a MODIFY_TOOL click is working in the selection area mode.
     * This is true for the down-move-up of defining a new area And it is true
     * for the down-move-up of dragging an existing area
     */
    private boolean is_selection_area_click = false;

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Special Nodes and Edges ////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Used to uniquely identify the currently selected node in a trace of the
     * graph.
     */
    private Node trace_node = null;

    /**
     * Used to uniquely identify the start state node in the graph.
     */
    public Node start_state_node = null;

    /**
     * Used to uniquely identify the edge or node that the current right click
     * menu is based upon.
     */
    public GraphObject menued_object = null;

    /**
     * Used to uniquely identify the edge or node that is currently hot selected
     * (being moved, etc)
     */
    private GraphObject hot_selected_object = null;

    /**
     * Records which edge is in moveable state when using the point tool
     */
    private Edge last_exclusive_edge = null;

    /**
     * Used to uniquely identify the node that is the origin node in the edge
     * creation process
     */
    private Node edge_creation_origin = null;

    /**
     * This is a popup text box for the latex code describing the label of a
     * node.
     */
    public FloatingText floating_text = null;

    /**
     * This is a popup box for specifying which labels should be associated with
     * a given Edge.
     */
    public FloatingToggles floating_toggles = null;

    /**
     * Used when disconnecting an existing edge from a node and reconnecting it
     * elsewhere.
     */
    private Vector broken_edge_labels = null;

    /**
     * Used when disconnecting an existing edge from a node and reconnecting it
     * elsewhere.
     */
    private Point broken_edge_label_displacement = null;

    /**
     * Scrollbars for the canvas.
     */
    public Slider hslider = null, vslider = null;

    /**
     * Records the last scroolbars values so that we can calculate displacement
     * in the selection events.
     */
    private int last_hslider_selection = 0, last_vslider_selection = 0;

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GraphController construction
    // ///////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construct the GraphController.
     * 
     * @param graphing_platform
     *            The platform in which this GraphController will exist.
     * @param parent
     *            The composite in which the J2DCanvas will be embedded.
     */
    public GraphController(GraphingPlatform graphing_platform, Composite parent) {
        gp = graphing_platform;

        // embed the J2DCanvas
        painting_controller = new PaintingController(this);
        j2dcanvas = new J2DCanvas(parent, SWT.NULL, painting_controller);
        GridData gd_canvas = new GridData(GridData.HORIZONTAL_ALIGN_FILL
                | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
                | GridData.GRAB_VERTICAL);
        j2dcanvas.setLayoutData(gd_canvas);
        j2dcanvas.setBackground(gp.display.getSystemColor(SWT.COLOR_WHITE));
        j2dcanvas.addListener(SWT.Resize, new Listener() {
            public void handleEvent(Event e) {
                refreshScrollbars();
            }
        });

        // attach the scrollbars, initiall disabled.
        vslider = new Slider(parent, SWT.VERTICAL);
        GridData gd_vslider = new GridData(GridData.FILL_VERTICAL);
        vslider.setLayoutData(gd_vslider);
        vslider.setEnabled(false);
        vslider.setMinimum(0);
        hslider = new Slider(parent, SWT.HORIZONTAL);
        GridData gd_hslider = new GridData(GridData.FILL_HORIZONTAL);
        hslider.setLayoutData(gd_hslider);
        hslider.setEnabled(false);
        hslider.setMinimum(0);

        vslider.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                gm.translateAll(0,
                        (int) Math.round((last_vslider_selection - vslider
                                .getSelection())
                                / gm.scale));
                last_vslider_selection = vslider.getSelection();
                j2dcanvas.repaint();
            }
        });

        hslider.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                gm.translateAll(
                        (int) Math.round((last_hslider_selection - hslider
                                .getSelection())
                                / gm.scale), 0);
                last_hslider_selection = hslider.getSelection();
                j2dcanvas.repaint();
            }
        });

        // initialize
        grab_origin = new int[] { 0, 0, 0 };
        selected_tool = CREATE_TOOL;
        j2dcanvas.setCursor(ResourceManager
                .getCursor(ResourceManager.CREATE_CURSOR));

        gm = new GraphModel(gp);

        pc = new PopupController(gp);

        j2dcanvas.setMenu(null);
        initializeCanvas();

        io = new GraphControllerIO(gp);
        group_area = new SelectionArea(gp,
                SelectionArea.SELECTING_OBJECTS_INSIDE_AN_AREA);
        gpc = new GraphPartCollection(gp);
        eb = new EditBuffer(gp);

        floating_text = new FloatingText(gp);
        floating_toggles = new FloatingToggles(gp);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Canvas
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Initialize the canvas.
     */
    private void initializeCanvas() {
        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Mouse Clicks  ///////////////////////////////////////////////////////////////////////////////////////////////////
        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        j2dcanvas.addMouseListener(new MouseListener() {
            // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Double Click ///////////////////////////////////////////////////////////////////////////////////////////////////
            // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /**
             * Process a doubleclick of the mouse button. If using the edge
             * tool, create a self loop at the selected node. The creation was
             * started by the "down" part of the "doubleclick", but the "up"
             * part didn't complete it because of the "click_time" logic The
             * "click_time" logic allows both a down-move-up and a
             * click-move-click creation process.
             * 
             * @param e
             *            The initiating MouseEvent.
             */
            public void mouseDoubleClick(MouseEvent e){
                Point edge_mouse = new Point(e.x / gm.scale, (e.y - 10)/ gm.scale);
                // allows two hotspots on the create tool

                if (e.button == 1) {
                    is_double_click = true;

                    // create a self loop
                    // because we allow creation by two methods:
                    // 1. down-up-move-down-up
                    // 2. down-move-up
                    // we had to put a time constraint on the up and ignore ups
                    // that occur shortly after downs.
                    // hence for a double click to create a self loop we really
                    // do have to use the double click event.
                    if (selected_tool == CREATE_TOOL && last_up_finished_an_edge == false) {
                        // further note that double click on a node should
                        // create a self loop
                        // but a double click on blank space should create a
                        // node and start and edge from it
                        // so if the first up/down created a node (instead of
                        // starting an edge)
                        // and the second up started this edge, then we
                        // shouldn't complete the edge, but leave it open ended.
                        if (edge_creation_origin != null && last_down_created_an_edge == false) {
                            Node n = gm.findNode(edge_mouse, false); // search
                            if (n == null) {
                                n = gm.findNode(edge_mouse, true);
                            } // padded search
                            if (n != null) {
                                finishEdgeCreation(n);
                            }
                        }
                    }
                    if (gpc.new_last_grabbed_object) {
                        if (gpc.lastGrabbedObject() instanceof Node) {
                            floating_text.initialize(new Point(e.x, e.y),
                                    ((Node) gpc.lastGrabbedObject()).origin(),
                                    gpc.lastGrabbedObject().glyph_label);
                            floating_text.setVisible(true);
                        } else if (gpc.lastGrabbedObject() instanceof Edge
                                && ((Edge) gpc.lastGrabbedObject())
                                        .getLastHitRegion() == Edge.R_LABEL
                                || ((Edge) gpc.lastGrabbedObject())
                                        .getLastHitRegion() == Edge.R_ARROWHEAD
                                || ((Edge) gpc.lastGrabbedObject())
                                        .getLastHitRegion() == Edge.R_HEAD_ANCHOR) {
                            floating_toggles.initialize(
                                    new Point(e.x, e.y + 8), (Edge) gpc
                                            .lastGrabbedObject());
                            floating_toggles.setVisible(true);
                        }
                    }
                    j2dcanvas.repaint();
                }
            }

            // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Mouse Up
            // ///////////////////////////////////////////////////////////////////////////////////////////////////////
            // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            /**
             * Process release of the mouse button. If using the node tool,
             * create a new node. If using the edge tool, finish creation of a
             * new edge. If using the point tool, finish node and/or edge
             * movement.
             * 
             * @param e
             *            The initiating MouseEvent.
             */
            public void mouseUp(MouseEvent e) {
                Point mouse = new Point(e.x / gm.scale, e.y / gm.scale);
                Point edge_mouse = new Point(e.x / gm.scale, (e.y - 10)/ gm.scale);
                // allows two hotspots on the create tool



                gpc.new_last_grabbed_object = false;
                last_up_finished_an_edge = false;

                if (e.button == 1 && left_mouse_is_down) {
                    // note: double click on file in file open dialogue causes a
                    // mouse_up event on canvas without a mouse_down
                    // note: we need to know when the left mouse is down so we
                    // can ignore right mouses at that time
                    left_mouse_is_down = false;

                    if (selected_tool == ZOOM_TOOL) {
                        if (gm.scale < GraphModel.MAXIMUM_SCALE) {
                            float old_scale = gm.scale;
                            gm.scale = gm.scale * 2;
                            gm.translateAll((int) Math.round(e.x / gm.scale
                                    - e.x / old_scale), (int) Math.round(e.y
                                    / gm.scale - e.y / old_scale));
                        } else {
                            gp.display.beep();
                        }
                    }

                    else if (selected_tool == CREATE_TOOL) {

                        if (edge_creation_origin == null && !is_double_click) {
                            // there was no node hit on the mouse down, so we
                            // assume they were trying to create a node
                            createNode(mouse);
                        } else if (edge_creation_origin != null
                                && new Date().getTime() - click_time > 300) {
                            // we are finishing the edge creation process, so we
                            // will try to connect it to an end node
                            Node n = gm.findNode(edge_mouse, false); // search
                            if (n == null) {
                                n = gm.findNode(edge_mouse, true);
                            } // padded search
                            if (n == null) {
                                n = createNode(edge_mouse);
                            } // if they clicked on blank space then create a
                                // new node
                            finishEdgeCreation(n);
                        }
                    }

                    else if (selected_tool == MODIFY_TOOL) {
                        if (hot_selected_object != null) {
                            finishObjectMovement();
                        }
                        if (start_state_node != null) {
                            if (start_state_node.isStartArrowSelected()) {
                                finishStartStateArrowMovement();
                            }
                        }
                        if (is_selection_area_click) {
                            is_selection_area_click = false;
                            group_area.mouseUp();
                        }
                    }

                    else if (selected_tool == PRINT_AREA_TOOL) {
                        gm.print_area.mouseUp();
                    }

                    else if (selected_tool == GRAB_TOOL) {
                        grab_origin[2] = 0; // mark as "not in mid grab"
                        io.markUnsavedChanges();
                    }
                } else if (e.button == 3) {
                    if (selected_tool == ZOOM_TOOL) {
                        if (gm.scale > GraphModel.MINIMUM_SCALE) {
                            float old_scale = gm.scale;
                            gm.scale = gm.scale / 2;
                            gm.translateAll((int) Math.round(e.x / gm.scale
                                    - e.x / old_scale), (int) Math.round(e.y
                                    / gm.scale - e.y / old_scale));
                        } else {
                            gp.display.beep();
                        }
                    }

                    else if (selected_tool == CREATE_TOOL) {
                        if (edge_creation_origin != null
                                && new Date().getTime() - click_time > 300) {
                            // we are finishing the edge creation process
                            // the user right-clicked, so we abandon the edge
                            gp.display.beep();
                            cleanupEdgeCreation();
                        }
                    }

                }

                is_double_click = false;
                j2dcanvas.repaint();

                refreshScrollbars();
            }

            // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Mouse Down
            // /////////////////////////////////////////////////////////////////////////////////////////////////////
            // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            /**
             * Process press of the mouse button. If using the node tool, do
             * nothing. If using the edge tool, start creation of a new edge. If
             * using the point tool, start node or edge movement, with priority
             * given to nodes.
             * 
             * @param e
             *            The initiating MouseEvent.
             */
            public void mouseDown(MouseEvent e) {
                Point mouse = new Point(e.x / gm.scale, e.y / gm.scale);
                Point edge_mouse = new Point(e.x / gm.scale, (e.y - 10)
                        / gm.scale); // allows two hotspots on the create
                                        // tool

                last_down_created_an_edge = false;

                if (e.button == 1) {
                    left_mouse_is_down = true;

                    if (selected_tool == CREATE_TOOL) {
                        if (edge_creation_origin == null) {
                            // assume we are starting the edge creation process;
                            // therefore look for a nearby node as the origin
                            Node n = gm.findNode(edge_mouse, false);
                            if (n != null) {
                                startEdgeCreation(n, edge_mouse);
                            } else {
                                // perhaps they are trying to disconnect and
                                // move an existing edge, test for arrowhead
                                // hits.
                                Edge broken_edge = gm.findEdge(edge_mouse,
                                        Edge.L_NO_TETHERS);
                                if (broken_edge != null) {
                                    Node start_node = broken_edge.getSource();
                                    broken_edge_labels = broken_edge
                                            .getLabelDataVector();
                                    broken_edge_label_displacement = broken_edge
                                            .getLabelDisplacement();
                                    broken_edge.delete();
                                    startEdgeCreation(start_node, edge_mouse);
                                } else {
                                    // still no hits. look again for nearby
                                    // nodes with a padded radius,
                                    // in case they thought they were supposed
                                    // to click near the node
                                    n = gm.findNode(edge_mouse, true);
                                    if (n != null) {
                                        startEdgeCreation(n, edge_mouse);
                                    }
                                }
                            }
                        }
                    }

                    else if (selected_tool == MODIFY_TOOL) {
                        // first we check if they are clicking inside a visible
                        // selection area.
                        if (group_area.cursorIsOverArea()) {
                            // this mousedown should not modify any individual
                            // objects
                            // it should cause a move of the selected group.
                            group_area.mouseDown(mouse);
                            is_selection_area_click = true;
                        } else {
                            // this mousedown shoud modify an individual graph
                            // part,
                            // or start a new selection area.

                            // this will be set to true if the mouse down is in
                            // blank space
                            // and the mousedown is not inside the visible
                            // selectiona area interior.
                            // note: it is the blank click action subroutine
                            // that will initiate a new seleciton area.
                            is_selection_area_click = false;

                            // show all edges

                            if (SystemVariables.show_all_edges
                                    && hot_selected_object == null) {
                                // look for nearby edges (by anchor hits)
                                hot_selected_object = gm.findEdge(mouse,
                                        Edge.L_ALL_ANCHORS | Edge.L_NO_TETHERS);
                                if (hot_selected_object != null) {
                                    hot_selected_object.initiateMovement(mouse,
                                            GraphObject.HOT_SELECTED,
                                            e.stateMask);
                                    gpc.updateGroup(e.stateMask,
                                            hot_selected_object);
                                }
                            }

                            // show all labels

                            if (SystemVariables.show_all_labels
                                    && hot_selected_object == null) {
                                // look for nearby edges (by label hits)
                                hot_selected_object = gm.findEdge(mouse,
                                        Edge.L_ALL_TETHERS | Edge.L_NO_ANCHORS);
                                if (hot_selected_object != null) {
                                    hot_selected_object.initiateMovement(mouse,
                                            GraphObject.HOT_SELECTED,
                                            e.stateMask);
                                    gpc.updateGroup(e.stateMask,
                                            hot_selected_object);
                                }
                            }

                            // regular tool (if the special options weren't
                            // selected or found nothing)

                            if (hot_selected_object == null) {
                                // look for nearby edges
                                hot_selected_object = gm.findEdge(mouse,
                                        Edge.L_NULL);
                                if (hot_selected_object != null) {
                                    // an edge was found
                                    if (((Edge) hot_selected_object)
                                            .getSelectionState() != Edge.EXCLUSIVE) {
                                        // this edge was not painting its
                                        // anchors
                                        // this was an arrow hit, mark it as the
                                        // exclusive edge
                                        if (last_exclusive_edge != null) {
                                            last_exclusive_edge
                                                    .setSelectionState(Edge.NO_ANCHORS);
                                        }
                                        ((Edge) hot_selected_object)
                                                .setSelectionState(Edge.EXCLUSIVE);
                                        last_exclusive_edge = ((Edge) hot_selected_object);
                                    }
                                    hot_selected_object.initiateMovement(mouse,
                                            GraphObject.HOT_SELECTED,
                                            e.stateMask);
                                    gpc.updateGroup(e.stateMask,
                                            hot_selected_object);
                                } else {
                                    // look for nearby nodes (since no edges
                                    // were found)
                                    hot_selected_object = gm.findNode(mouse,
                                            false);
                                    if (hot_selected_object != null) {
                                        // the user clicked inside a node, so
                                        // begin to move that node.
                                        hot_selected_object.initiateMovement(
                                                mouse,
                                                GraphObject.HOT_SELECTED,
                                                e.stateMask);
                                        gpc.updateGroup(e.stateMask,
                                                hot_selected_object);
                                    } else {
                                        // test if the user clicked on the start
                                        // arrow of the start state
                                        if (start_state_node != null) {
                                            if (start_state_node
                                                    .isLocatedStartArrow(mouse)) {
                                                startStartStateArrowMovement();
                                                gpc.updateGroup(e.stateMask,
                                                        start_state_node);
                                            } else {
                                                // the user clicked blank space.
                                                blankClickAction(mouse);
                                            }
                                        } else {
                                            // the user clicked blank space.
                                            blankClickAction(mouse);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    else if (selected_tool == PRINT_AREA_TOOL) {
                        gm.print_area.mouseDown(mouse);
                    }

                    else if (selected_tool == GRAB_TOOL) {
                        grab_origin[0] = mouse.x;
                        grab_origin[1] = mouse.y;
                        grab_origin[2] = 1; // mark as "in mid grab"
                    }
                }

                else if (e.button == 3 && !left_mouse_is_down
                        && selected_tool != ZOOM_TOOL) {
                    // select the correct hotspot
                    Point hot_mouse = mouse;
                    if (selected_tool == CREATE_TOOL) {
                        hot_mouse = edge_mouse;
                    }

                    // ignore right click that occur during edge creation
                    if (edge_creation_origin == null) {
                        // we can't cleanup after a popup action because we have
                        // no end event if the click off the canvas;
                        // therfore, we cleanup before each popup action.
                        menued_object = null;

                        // look for nearby edges
                        menued_object = gm.findEdge(hot_mouse, Edge.L_NULL);
                        if (menued_object != null) {
                            // an edge was found
                            menued_object
                                    .addAttribute(GraphObject.HOT_SELECTED);
                            pc.mnu_edge.setData(new Point(e.x, e.y));
                            pc.mnu_edge.setVisible(true);
                        } else {
                            // look for nearby nodes (since no edges were found)
                            menued_object = gm.findNode(hot_mouse, false);
                            if (menued_object != null) {
                                // a node was found
                                menued_object
                                        .addAttribute(GraphObject.HOT_SELECTED);
                                if (((Node) menued_object).isStartState()) {
                                    pc.mitm_node_startstate.setSelection(true);
                                } else {
                                    pc.mitm_node_startstate.setSelection(false);
                                }
                                if (((Node) menued_object).isMarkedState()) {
                                    pc.mitm_node_markedstate.setSelection(true);
                                } else {
                                    pc.mitm_node_markedstate
                                            .setSelection(false);
                                }
                                pc.mnu_node.setVisible(true);
                            } else if (selected_tool == MODIFY_TOOL
                                    && group_area.isVisible()
                                    && group_area.isInBounds(hot_mouse.x,
                                            hot_mouse.y)) {
                                // it is the modify area tool,
                                // and no particular graphpart was selected
                                // and they clicked inside a visible bounding
                                // box
                                pc.mnu_internal.setVisible(true);
                            } else {
                                // rightclick on blank space.
                                pc.mnu_external.setData(gm
                                        .snapToGrid(hot_mouse));
                                pc.mnu_external.setVisible(true);
                            }
                        }
                    }
                }
                j2dcanvas.repaint();
            }
        });

        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Mouse Move
        // /////////////////////////////////////////////////////////////////////////////////////////////////////
        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        j2dcanvas.addMouseMoveListener(new MouseMoveListener() {
            /**
             * Process movement of the mouse. If in the midst of Edge creation,
             * update the cursor_location. If in the midst of Node movement,
             * update the Node's position. If in the midst of Edge movement,
             * update the displacement vector.
             * 
             * @param e
             *            The initiating MouseEvent.
             */
            public void mouseMove(MouseEvent e) {
                Point mouse = new Point(e.x / gm.scale, e.y / gm.scale);
                Point edge_mouse = new Point(e.x / gm.scale, (e.y - 10)
                        / gm.scale); // allows two hotspots on the create
                                        // tool

                if (selected_tool == PRINT_AREA_TOOL) {
                    gm.print_area.mouseMove(mouse);
                }

                else if (selected_tool == MODIFY_TOOL
                        && (is_selection_area_click || (group_area.isVisible() && !left_mouse_is_down))) {
                    // only do the group move if the modify tool is selected.
                    // if the mouse is up and the area is visible we want to run
                    // if for the sake of updating the cursor
                    // if the mouse is down and the click was inside the area,
                    // we are moving it so we need to run it.
                    group_area.mouseMove(mouse);
                }

                else if (grab_origin[2] == 1) {
                    // marked as "in mid grab"
                    gm.translateAll(mouse.x - grab_origin[0], mouse.y
                            - grab_origin[1]);

                    grab_origin[0] = mouse.x;
                    grab_origin[1] = mouse.y;

                    // repaint
                    j2dcanvas.repaint();
                } else if (edge_creation_origin != null) {
                    edge_creation_origin.getPartialEdgeEndpoint().x = edge_mouse.x;
                    edge_creation_origin.getPartialEdgeEndpoint().y = edge_mouse.y;
                    j2dcanvas.repaint();
                } else if (hot_selected_object != null) {
                    hot_selected_object.updateMovement(mouse);
                    j2dcanvas.repaint();
                }
                // make sure this is last in the if-else, because it can be true
                // while the others are true.
                else if (start_state_node != null) {
                    if (start_state_node.isStartArrowSelected()) {
                        start_state_node.updateConfiguration(mouse.x, mouse.y);
                        j2dcanvas.repaint();
                    }
                }
            }
        });
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Object creation and modificaiton
    // ///////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Finish the movement of an object (node or edge). Set the object's
     * apperance back to normal, and kill the temp objects.
     */
    private void finishObjectMovement() {
        hot_selected_object.terminateMovement(GraphObject.HOT_SELECTED);
        hot_selected_object = null;
        io.markUnsavedChanges();
    }

    /**
     * Actions performed when the user clicks on nothing.
     * 
     * @param mouse
     *            The location of the mouse-click.
     */
    private void blankClickAction(Point mouse) {
        if (gm.findEdge(mouse, Edge.L_PADDED) == null) {
            // there weren't any edges even near by the click.
            if (last_exclusive_edge != null) {
                last_exclusive_edge.setSelectionState(Edge.NO_ANCHORS);
                gpc.abandonGroupHistory();
            }
        }
        is_selection_area_click = true;
        group_area.mouseDown(mouse);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Edge creation and modificaiton
    // /////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Start the Edge creation process. Draw a highlited edge from the
     * edge_creation_origin to the cursor_location.
     * 
     * @param n
     *            The Node from which the Edge origionates.
     * @param mouse
     *            The mouse co-ordinates.
     */
    private void startEdgeCreation(Node n, Point mouse) {
        edge_creation_origin = n;
        edge_creation_origin.addAttribute(GraphObject.HOT_SELECTED);
        edge_creation_origin.setPartialEdgeEndpoint(mouse.getCopy());
        click_time = new Date().getTime();
        last_down_created_an_edge = true;
    }

    /**
     * Finish the Edge creation process. Add the Edge to the GraphModel, and
     * cleanup.
     * 
     * @param n
     *            The Node in which the Edge terminates.
     */
    private void finishEdgeCreation(Node n) {
        Edge e = new Edge(gp, gm, edge_creation_origin, n);
        if (broken_edge_labels != null) {
            for (int i = 0; i < broken_edge_labels.size(); i++) {
                e.addLabel((TableItem) broken_edge_labels.elementAt(i));
            }
            broken_edge_labels = null;
        }
        if (broken_edge_label_displacement != null) {
            e.setLabelDisplacement(broken_edge_label_displacement);
            broken_edge_label_displacement = null;
        }
        gm.addEdge(e);
        e.accomodateLabel();
        last_exclusive_edge = e;
        cleanupEdgeCreation();
        io.markUnsavedChanges();
    }

    /**
     * Clean-up after the Edge creation process. The current parital_edge has
     * already been added to the GraphModel, or the process has been aborted.
     * This kills the temp objects, and sets the colours back to normal.
     */
    private void cleanupEdgeCreation() {
        edge_creation_origin.removeAttribute(GraphObject.HOT_SELECTED);
        edge_creation_origin.setPartialEdgeEndpoint(null);
        edge_creation_origin = null;
        click_time = 0;
        last_up_finished_an_edge = true;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Node creation and modificaiton
    // /////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Attempt creation of a Node from the MouseEvent If this would draw the new
     * Node exactly over top of an existing node then cause a flashNodeError
     * 
     * @param mouse
     *            The mouse co-ordinates
     */
    private Node createNode(Point mouse) {
        Node n = new Node(gp, gm, mouse.x, mouse.y);
        gm.addNode(n);
        io.markUnsavedChanges();
        return n;
        // gp.display.beep();
        // try { Thread.sleep(400); } catch (Exception e) {}
    }

    /**
     * Start the movement of the start state arrow. Set the Edge's apperance to
     * selected, and initialize the displacement vector.
     */
    private void startStartStateArrowMovement() {
        start_state_node.addAttribute(GraphObject.START_ARROW_SELECTED);
    }

    /**
     * Finish the movement of the start state arrow. Set the Edge's apperance
     * back to normal, and kill the temp objects.
     */
    private void finishStartStateArrowMovement() {
        start_state_node.removeAttribute(GraphObject.START_ARROW_SELECTED);
        io.markUnsavedChanges();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // toolbar ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Re-paint the canvas.
     */
    public void repaint() {
        j2dcanvas.repaint();
    }

    /**
     * draw or hide a grid behind the graph.
     */
    public void toggleGrid() {
        draw_grid = !draw_grid;
        j2dcanvas.repaint();
    }

    /**
     * Abandon any processes currently underway with any GefTool
     */
    public void abandonGefTool() {
        if (edge_creation_origin != null) {
            gp.display.beep();
            cleanupEdgeCreation();
        }

        if (last_exclusive_edge != null) {
            last_exclusive_edge.setSelectionState(Edge.NO_ANCHORS);
        }

        if (selected_tool != MODIFY_TOOL) {
            gpc.abandonGroupHistory();
            group_area.setVisible(false);
        } else {
            group_area.highlite(gpc.getBoundingArea());
        }

        j2dcanvas.repaint();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // miscellaneous //////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void refreshScrollbars() {
        Box graph = gm.getBounds();
        graph.scale(gm.scale);
        Box canvas = new Box(j2dcanvas.getBounds());

        if (graph.x1() < canvas.x1() || graph.x2() > canvas.x2()) {
            int left_white_space = 0;
            if (graph.x1() > canvas.x1()) {
                left_white_space = graph.x1() - canvas.x1();
            }
            int right_white_space = 0;
            if (graph.x2() < canvas.x2()) {
                right_white_space = canvas.x2() - graph.x2();
            }

            hslider
                    .setMaximum(graph.w() + left_white_space
                            + right_white_space);
            hslider.setThumb(canvas.w());

            if (graph.x1() < canvas.x1()) {
                hslider.setSelection(canvas.x1() - graph.x1());
            } else {
                hslider.setSelection(0);
            }
            last_hslider_selection = hslider.getSelection();

            hslider.setEnabled(true);
        } else {
            hslider.setEnabled(false);
        }

        if (graph.y1() < canvas.y1() || graph.y2() > canvas.y2()) {
            int top_white_space = 0;
            if (graph.y1() > canvas.y1()) {
                top_white_space = graph.y1() - canvas.y1();
            }
            int bottom_white_space = 0;
            if (graph.y2() < canvas.y2()) {
                bottom_white_space = canvas.y2() - graph.y2();
            }

            vslider
                    .setMaximum(graph.h() + top_white_space
                            + bottom_white_space);
            vslider.setThumb(canvas.h());

            if (graph.y1() < canvas.y1()) {
                vslider.setSelection(canvas.y1() - graph.y1());
            } else {
                vslider.setSelection(0);
            }
            last_vslider_selection = vslider.getSelection();

            vslider.setEnabled(true);
        } else {
            vslider.setEnabled(false);
        }
    }

    /**
     * Sets all pointers to this object = null (used in deletion)
     * 
     * @param dead_object
     *            The GraphObject that has been deleted.
     */
    public void safeNull(GraphObject dead_object) {
        if (dead_object == trace_node) {
            trace_node = null;
        }
        if (dead_object == start_state_node) {
            start_state_node = null;
        }
        if (dead_object == last_exclusive_edge) {
            last_exclusive_edge = null;
        }
        gpc.removeFromCollection(dead_object);
    }

    /**
     * Reset all variables to the initial state.
     */
    public void resetState() {
        resetInternalState();
        io.resetState();
    }

    /**
     * Reset all variables to the initial state, excluding the IO object.
     */
    public void resetInternalState() {
        menued_object = null;
        hot_selected_object = null;
        last_exclusive_edge = null;
        edge_creation_origin = null;
        gm = null;
        gpc.abandonGroupHistory();
        grab_origin[2] = 0;
        group_area.resetState();
        gp.td.resetInputState();
        gm = new GraphModel(gp);
        j2dcanvas.repaint();
        gp.tabFolder.setSelection(GraphingPlatform.GRAPH_CANVAS_TAB);
    }

    /**
     * The user has decided to shut down.
     */
    public void dispose() {
        floating_text.dispose();
        floating_toggles.dispose();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // drawing
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Draw the GraphModel and special markers.
     * 
     * @param g2d
     *            The Graphics2D object where it will be drawn.
     */
    public void draw(Graphics2D g2d) {
        Drawer drawer = new Drawer(gp, g2d, gm.scale);

        gm.draw(drawer);

        drawer.setColor(GraphModel.GROUPED);
        group_area.draw(drawer);
        drawer.setColor(GraphModel.NORMAL);
    }

}
