/*
 * Created on Jun 21, 2004
 */
package userinterface.graphcontrol;

import ides2.SystemVariables;

import java.util.Vector;

import org.eclipse.swt.graphics.Rectangle;

import userinterface.GraphingPlatform;
import userinterface.geometric.Box;
import userinterface.geometric.Line;
import userinterface.geometric.Point;
import userinterface.graphcontrol.graphparts.Edge;
import userinterface.graphcontrol.graphparts.GraphObject;
import userinterface.graphcontrol.graphparts.Node;

/**
 * @author Michael Wood
 */
public class GraphModel {
    /**
     * The different styles that parts of this GraphModel may have.
     */
    public static final int NORMAL = 0, SELECTED = 1, HOT_SELECTED = 1,
            CUSTOM = 2, ANCHORS = 3, TETHERS = 3, TRACE = 2, GROUPED = 4,
            GREY = 5;

    /**
     * The platform in which this GraphModel will exist.
     */
    private GraphingPlatform gp = null;

    /**
     * The print area used in export to latex.
     */
    public SelectionArea print_area = null;

    /**
     * An unordered list of all Nodes in this GraphModel.
     */
    private Vector node_list = null;

    /**
     * An unordered list of all Edges in this GraphModel.
     */
    private Vector edge_list = null;

    /**
     * Rememberst the last edge search index so overlapping edges can be
     * distinguished with multiple clicks
     */
    private int last_found_edge_index = 0;

    /**
     * Records the displacement of the grid origion point, so that the
     * (translate_all) function don't cause a massive resnap.
     */
    public Point grid_displacement = null;

    /**
     * Bounds for the scale variable used in Zoom in/out control.
     */
    public static final float MAXIMUM_SCALE = 32,
            MINIMUM_SCALE = (float) 1 / 32;

    /**
     * Zoom in/out control. Normal is 1
     */
    public float scale = 1;

    /**
     * Used to offer a standard radius size amongst the nodes.
     */
    public int max_node_size = 0;

    /**
     * The last recorded system time. This is used to gather performance
     * statistics.
     */
    private long last_system_time = 0;

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GraphModel construction
    // ////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construct the GraphModel.
     * 
     * @param gp
     *            The GraphingPlatform in which this GraphModel will exist.
     */
    public GraphModel(GraphingPlatform gp) {
        this.gp = gp;
        node_list = new Vector();
        edge_list = new Vector();
        print_area = new SelectionArea(gp, SelectionArea.MARKING_OUT_AN_AREA);
        grid_displacement = new Point(0, 0);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GraphModel modification
    // ////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Add a Node to this GraphModel.
     * 
     * @param new_node
     *            The Node to be added to this GraphModel.
     */
    public void addNode(Node new_node) {
        node_list.addElement(new_node);
        new_node.confirm(this);
    }

    /**
     * Add an Edge to this GraphModel.
     * 
     * @param new_edge
     *            The Edge to be added to this GraphModel.
     */
    public void addEdge(Edge new_edge) {
        edge_list.addElement(new_edge);
        new_edge.confirm(this);
    }

    /**
     * Remove a Node from this GraphModel.
     * 
     * @param node
     *            The Node to be removed from this GraphModel.
     */
    public void removeNode(Node node) {
        node_list.removeElement(node);
    }

    /**
     * Remove an Edge from this GraphModel.
     * 
     * @param edge
     *            The Edge to be removed from this GraphModel.
     */
    public void removeEdge(Edge edge) {
        edge_list.removeElement(edge);
    }

    /**
     * Notify the GraphController of the deletion of a GraphObject
     * 
     * @param dead_object
     *            The GraphObject that has been deleted.
     */
    public void safeNull(GraphObject dead_object) {
        gp.gc.safeNull(dead_object);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GraphModel actions
    // /////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Draw all parts of this GraphModel.
     * 
     * @param drawer
     *            The Drawer that will handle the drawing.
     */
    public void draw(Drawer drawer) {
        // System.out.println(getSize() + "\t" + (System.currentTimeMillis() -
        // last_system_time));
        // last_system_time = System.currentTimeMillis();

        if (gp.gc.draw_grid && SystemVariables.grid > 0) {
            drawer.setColor(GraphModel.GREY);
            Point offset = new Point(
                    grid_displacement.x % SystemVariables.grid,
                    grid_displacement.y % SystemVariables.grid);
            int width = gp.gc.j2dcanvas.getSize().x;
            int height = gp.gc.j2dcanvas.getSize().y;
            int i = 0;
            while (i + offset.x < width || i + offset.y < height) {
                drawer.drawLine(i + offset.x, 0, i + offset.x, height,
                        Drawer.TINY_DASHED);
                drawer.drawLine(0, i + offset.y, width, i + offset.y,
                        Drawer.TINY_DASHED);
                i = i + SystemVariables.grid;
            }
            drawer.setColor(GraphModel.NORMAL);
        }

        Node n = null;
        Edge e = null;
        for (int i = 0; i < node_list.size(); i++) {
            n = (Node) node_list.elementAt(i);
            n.draw(drawer);
        }
        for (int i = 0; i < edge_list.size(); i++) {
            e = (Edge) edge_list.elementAt(i);
            boolean ok_tool = (gp.gc.selected_tool == gp.gc.MODIFY_TOOL || gp.gc.selected_tool == gp.gc.ZOOM_TOOL);
            e.draw(drawer, (SystemVariables.show_all_edges && ok_tool),
                    (SystemVariables.show_all_labels && ok_tool));
        }

        drawer.setColor(GraphModel.GREY);
        print_area.draw(drawer);
        drawer.setColor(GraphModel.NORMAL);
    }

    /**
     * Cause all GraphObjects to accomodate any recent changes in their Labels.
     */
    public void accomodateLabels() {
        for (int i = 0; i < node_list.size(); i++) {
            ((Node) node_list.elementAt(i)).accomodateLabel();
        }
        for (int i = 0; i < edge_list.size(); i++) {
            ((Edge) edge_list.elementAt(i)).accomodateLabel();
        }
        if (gp.gc != null) {
            gp.gc.floating_toggles.populate();
        }
    }

    /**
     * Cause all GraphObjects to copy any existing information from the
     * abandoned label type into the new label type, whenever the new values are
     * empty.
     */
    public void fillBlankLabels() {
        for (int i = 0; i < node_list.size(); i++) {
            ((Node) node_list.elementAt(i)).fillBlankLabels();
        }
        gp.td.fillBlankLabels();
    }

    /**
     * Translate all variables.
     * 
     * @param x
     *            Translation in the x direction.
     * @param y
     *            Translation in the y direction.
     */
    public void translateAll(int x, int y) {
        for (int i = 0; i < node_list.size(); i++) {
            ((Node) node_list.elementAt(i)).translateAll(x, y);
        }
        for (int i = 0; i < edge_list.size(); i++) {
            ((Edge) edge_list.elementAt(i)).translateAll(x, y);
        }
        print_area.translateAll(new Point(x, y));
        gp.gc.group_area.translateAll(new Point(x, y));
        grid_displacement.x = grid_displacement.x + x;
        grid_displacement.y = grid_displacement.y + y;
    }

    /**
     * Add all graph objects bounded by the given rectangle into the grouping.
     * 
     * @param area
     *            The bounding area for inclusion into the grouping.
     * @return The minimized bounding area of the grouping.
     */
    public Box addToGrouping(Box area) {
        Box new_area = null;
        Vector list1 = new Vector();
        Vector list2 = new Vector();
        Vector valid_edges = new Vector();
        Node n = null;
        for (int i = 0; i < node_list.size(); i++) {
            n = (Node) node_list.elementAt(i);
            if (n.isBoundBy(area)) {
                gp.gc.gpc.addToGrouping(n);
                list1.add(n);
                list2.add(n);
                if (new_area == null) {
                    new_area = new Box(n.x(), n.y(), n.x(), n.y());
                }
                if (n.x() - n.r() < new_area.x1()) {
                    new_area.x1(n.x() - n.r());
                }
                if (n.y() - n.r() < new_area.y1()) {
                    new_area.y1(n.y() - n.r());
                }
                if (n.x() + n.r() > new_area.x2()) {
                    new_area.x2(n.x() + n.r());
                }
                if (n.y() + n.r() > new_area.y2()) {
                    new_area.y2(n.y() + n.r());
                }
            }
        }
        for (int i = 0; i < list1.size(); i++) {
            // for each node, test it's edge groups and add them to the grouping
            // if their destination node is in list2
            n = (Node) list1.elementAt(i);
            valid_edges = n.fetchEdges(list2);
            list2.remove(n);
            for (int j = 0; j < valid_edges.size(); j++) {
                gp.gc.gpc.addToGrouping((Edge) valid_edges.elementAt(j));
            }
        }
        if (new_area != null) {
            new_area.grow(SelectionArea.PADDING);
        }
        return new_area;
    }

    /**
     * Attempt to retrieve a Node that matches the given id. The id should have
     * come from a file, and it should match an index in the list of Nodes
     * inside this GraphModel.
     * 
     * @param id
     *            The id of the requested Node.
     * @return The Node if it was found, else null.
     */
    public Node getNodeById(int id) {
        if (id >= 0 && id < node_list.size()) {
            return (Node) node_list.elementAt(id);
        } else {
            return null;
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GraphModel queries
    // /////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Find the number of objects in the model.
     * 
     * @return The number of nodes and edges in the model.
     */
    public int getSize() {
        return node_list.size() + edge_list.size();
    }

    /**
     * Get a bounding box for this graph model.
     */
    public Box getBounds() {
        Rectangle canvas = gp.gc.j2dcanvas.getBounds();
        Box bounds = new Box(50, 50, 51, 51);

        Node n = null;
        for (int i = 0; i < node_list.size(); i++) {
            n = (Node) node_list.elementAt(i);
            if (i == 0) {
                // first node
                bounds.x1(n.x() - n.r());
                bounds.y1(n.y() - n.r());
                bounds.x2(n.x() + n.r());
                bounds.y2(n.y() + n.r());
            } else {
                // grow
                if (n.x() - n.r() < bounds.x1()) {
                    bounds.x1(n.x() - n.r());
                }
                if (n.y() - n.r() < bounds.y1()) {
                    bounds.y1(n.y() - n.r());
                }
                if (n.x() + n.r() > bounds.x2()) {
                    bounds.x2(n.x() + n.r());
                }
                if (n.y() + n.r() > bounds.y2()) {
                    bounds.y2(n.y() + n.r());
                }
            }
        }

        Edge e = null;
        for (int i = 0; i < edge_list.size(); i++) {
            e = (Edge) edge_list.elementAt(i);
            // grow
            if (e.midpoint().x < bounds.x1()) {
                bounds.x1(e.midpoint().x);
            }
            if (e.midpoint().y < bounds.y1()) {
                bounds.y1(e.midpoint().y);
            }
            if (e.midpoint().x > bounds.x2()) {
                bounds.x2(e.midpoint().x);
            }
            if (e.midpoint().y > bounds.y2()) {
                bounds.y2(e.midpoint().y);
            }
        }

        bounds.grow(SelectionArea.PADDING * 4);
        return bounds;
    }

    /**
     * Find the first a Node in this GraphModel that lies near the connecting
     * line of the two distinct input nodes.
     * 
     * @param n1
     *            A Node in this graph model.
     * @param n2
     *            A different Node in this graph model.
     * @return true if any third Node lise near the connectin line of the two
     *         input nodes.
     */
    public boolean findNode(Node n1, Node n2) {
        Node n = null;
        Line line = new Line(n1.origin(), n2.origin());
        for (int i = 0; i < node_list.size(); i++) {
            n = (Node) node_list.elementAt(i);
            if (n != n1 && n != n2) {
                if (line.isNear(n.origin(), n.r())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Find the first a Node in this GraphModel that contains the given query
     * point within its bounding circle.
     * 
     * @param mouse
     *            The co-ordinates of the query point.
     * @param padded
     *            Whether or not the target is within the nodes radius or within
     *            the nodes radius + arrowhead
     * @return The first Node that contains the given query point within its
     *         bounding circle. Null otherwise.
     */
    public Node findNode(Point mouse, boolean padded) {
        Node n = null;
        for (int i = 0; i < node_list.size(); i++) {
            n = (Node) node_list.elementAt(i);
            if (n.isLocated(mouse, padded)) {
                break;
            } else {
                n = null;
            }
        }
        return n;
    }

    /**
     * Find where the input co-ordinates would lie if they were snapped to the
     * grid.
     * 
     * @param p
     *            The input co-ordinates.
     * @return Where the input co-ordinates would lie if they were snapped to
     *         the grid.
     */
    private Point snappedToGrid(Point p) {
        Point r = p.getCopy();
        if (SystemVariables.grid > 0) {
            int difference = 0;
            Point offset = new Point(
                    grid_displacement.x % SystemVariables.grid,
                    grid_displacement.y % SystemVariables.grid);

            difference = (p.x - offset.x) % SystemVariables.grid;
            if (difference != 0) {
                if (difference > SystemVariables.grid / 2) {
                    r.x = p.x + SystemVariables.grid - difference;
                } else {
                    r.x = p.x - difference;
                }
            }

            difference = (p.y - offset.y) % SystemVariables.grid;
            if (difference != 0) {
                if (difference > SystemVariables.grid / 2) {
                    r.y = p.y + SystemVariables.grid - difference;
                } else {
                    r.y = p.y - difference;
                }
            }
        }
        return r;
    }

    /**
     * Find the first a Edge in this GraphModel that contains the given query
     * point within its bounding area. This searches the list from the position
     * after it's last search. Hence multiple clicks on the same location where
     * there are overlapping edges will eventually find all the edges.
     * 
     * @param mouse
     *            The co-ordinates of the query point.
     * @param options
     *            selection options regarding anchors and tethers. Use the
     *            locations constants from the Edge class (start with L_). It is
     *            allowed to OR multiple constants.
     * @return The first Edge that contains the given query point within its
     *         bounding area. Null otherwise.
     */
    public Edge findEdge(Point mouse, int options) {
        Edge e = null;
        if (edge_list.size() - 1 < last_found_edge_index + 1) {
            last_found_edge_index = 0;
        }

        for (int i = last_found_edge_index + 1; i < edge_list.size(); i++) {
            e = (Edge) edge_list.elementAt(i);
            if (e.isLocated(mouse.x, mouse.y, options)) {
                last_found_edge_index = i;
                break;
            } else {
                e = null;
            }
        }
        if (e == null) {
            // not found yet, search other portion
            for (int i = 0; (i <= last_found_edge_index && i < edge_list.size()); i++) {
                e = (Edge) edge_list.elementAt(i);
                if (e.isLocated(mouse.x, mouse.y, options)) {
                    last_found_edge_index = i;
                    break;
                } else {
                    e = null;
                }
            }
        }
        return e;
    }

    /**
     * Return the id of the given node. This is really jsut it's position in the
     * node_list structure.
     * 
     * @param n
     *            The node whose id is requested.
     * @return The id of the given node.
     */
    public int getId(Node n) {
        return node_list.indexOf(n);
    }

    /**
     * Calculates where this point would lie if it were snapped to the grid.
     * 
     * @param p
     *            The query Point.
     * @return Where the query Point would lie if it were snapped to the grid.
     */
    public Point snapToGrid(Point p) {
        Point snaped = p.getCopy();
        if (SystemVariables.grid > 0) {
            int difference = 0;
            Point offset = new Point(
                    grid_displacement.x % SystemVariables.grid,
                    grid_displacement.y % SystemVariables.grid);

            difference = (p.x - offset.x) % SystemVariables.grid;
            if (difference != 0) {
                if (difference > SystemVariables.grid / 2) {
                    snaped.x = p.x + SystemVariables.grid - difference;
                } else {
                    snaped.x = p.x - difference;
                }
            }

            difference = (p.y - offset.y) % SystemVariables.grid;
            if (difference != 0) {
                if (difference > SystemVariables.grid / 2) {
                    snaped.y = p.y + SystemVariables.grid - difference;
                } else {
                    snaped.y = p.y - difference;
                }
            }
        }
        return snaped;
    }
}
