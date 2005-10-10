/*
 * Created on Jun 21, 2004
 */
package userinterface.graphcontrol.graphparts;

import ides2.SystemVariables;

import java.util.Vector;

import userinterface.GraphingPlatform;
import userinterface.geometric.Box;
import userinterface.geometric.Point;
import userinterface.geometric.UnitVector;
import userinterface.graphcontrol.Drawer;
import userinterface.graphcontrol.GraphModel;

/**
 * This represcents a Node or circle in the graph. All GraphObjects are directly
 * aware of the GraphModel. A Node may also be directly aware of its Label, its
 * start arrow, and any of its EdgeGroups. Nodes are indirectly aware of their
 * neighbours and incidents edges via the EdgeGroups.
 * 
 * @author Michael Wood
 */
public class Node extends GraphObject{

    /**
     * The default radius of the circle which represents this Node, and the
     * fixed distance between outer and inner circles for marked Nodes.
     */
    public static final int DEFAULT_RADIUS = 15, RDIF = 4;

    /**
     * The x and y co-ordinates of the center of this Node.
     */
    private int x = 20, y = 20;

    /**
     * The radius of this Node.
     */
    private int r = DEFAULT_RADIUS;

    /**
     * The direction of the arrow if this node is a start state.
     */
    private UnitVector start_arrow_direction = null;

    /**
     * The ArrowHead for the start arrow if this Node has the "start" attribute
     */
    private ArrowHead arrowhead = null;

    /**
     * The list of EdgeGroups in which this Node participates.
     */
    private Vector<EdgeGroup> edge_group_list = null;

    /**
     * The endpoint for a partial edge. This is used when creating an edge.
     */
    private Point partialEdgeEndpoint = null;

    public Point getPartialEdgeEndpoint(){
        return partialEdgeEndpoint;
    }

    public void setPartialEdgeEndpoint(Point partialEdgeEndpoint){
        this.partialEdgeEndpoint = partialEdgeEndpoint;
    }

    /**
     * Temporarily rembers the last created clone, for use in graph cloning.
     */
    private Node lastClone = null;

    public Node getLastClone(){
        return lastClone;
    }

    public void setLastClone(Node n){
        lastClone = n;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Node construction
    // //////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construct the Node. (used at user creation)
     * 
     * @param gp
     *            The GraphingPlatform in which this Node will exist.
     * @param gm
     *            The GraphModel in which this Node will exist.
     * @param x
     *            The x co-ordinate for the origin of this Node.
     * @param y
     *            The y co-ordinate for the origin of this Node.
     */
    public Node(GraphingPlatform gp, GraphModel gm, int x, int y){
        super(gp, gm, GraphObject.NULL);
        this.x = x;
        this.y = y;
        snapToGrid();
        constructNode(DEFAULT_RADIUS, 1, 0);
        initializeLabels("");
    }

    /**
     * Construct the Node. (used at cloning)
     * 
     * @param gp
     *            The GraphingPlatform in which this Node will exist.
     * @param gm
     *            The GraphModel in which this Node will exist.
     * @param x
     *            The x co-ordinate for the origin of this Node.
     * @param y
     *            The y co-ordinate for the origin of this Node.
     * @param r
     *            The radius for this Node.
     * @param attributes
     *            The attributes for this node.
     * @param arrow_x
     *            The x component of the start arrow direction.
     * @param arrow_y
     *            The y component of the start arrow direction.
     * @param glyphLabel
     *            A Label to be cloned for the glyph label of this Node.
     * @param latex_label
     *            A Label to be cloned for the latex label of this Node.
     */
    public Node(GraphingPlatform gp, GraphModel gm, int x, int y, int r, int attributes,
            float arrow_x, float arrow_y, GlyphLabel glyphLabel){
        super(gp, gm, attributes);
        this.x = x;
        this.y = y;
        constructNode(r, arrow_x, arrow_y);
        this.setGlyphLabel(new GlyphLabel(gp, this, glyphLabel));
    }

    /**
     * Construct the Node. (used at load from file)
     * 
     * @param gp
     *            The GraphingPlatform in which this Node will exist.
     * @param gm
     *            The GraphModel in which this Node will exist.
     * @param x
     *            The x co-ordinate for the origin of this Node.
     * @param y
     *            The y co-ordinate for the origin of this Node.
     * @param r
     *            The radius for this Node.
     * @param attributes
     *            The attributes for this node.
     * @param arrow_x
     *            The x component of the start arrow direction.
     * @param arrow_y
     *            The y component of the start arrow direction.
     * @param glyph_string
     *            The string representation of the glyph label.
     * @param latex_string
     *            The string representation of the latex label.
     */
    public Node(GraphingPlatform gp, GraphModel gm, int x, int y, int r, int attributes,
            float arrow_x, float arrow_y, String glyph_string){
        super(gp, gm, attributes);
        this.x = x;
        this.y = y;
        constructNode(r, arrow_x, arrow_y);
        initializeLabels(glyph_string);
    }

    /**
     * Set the class variables of this Node.
     * 
     * @param r
     *            The radius for this Node.
     * @param arrow_x
     *            The x component of the start arrow direction.
     * @param arrow_y
     *            The y component of the start arrow direction.
     */
    private void constructNode(int r, float arrow_x, float arrow_y){
        if(r > 0){
            this.r = r;
        }
        edge_group_list = new Vector<EdgeGroup>();
        start_arrow_direction = new UnitVector(arrow_x, arrow_y);
        arrowhead = new ArrowHead(start_arrow_direction, x
                - (int) Math.round(this.r * start_arrow_direction.x), y
                - (int) Math.round(this.r * start_arrow_direction.y));
    }

    /**
     * Set the class variables of this Node.
     * 
     * @param glyph_string
     *            The string representation of the glyph label.
     * @param latex_string
     *            The string representation of the latex label.
     */
    private void initializeLabels(String glyph_string){
        setGlyphLabel(new GlyphLabel(gp, this, glyph_string, origin(), Label.CENTER));
    }

    /**
     * Create a clone of this Node, with null as its GraphModel.
     * 
     * @return A clone of this Node.
     */
    public Node newClone(){
        int attributes = GraphObject.NULL;
        if(isMarkedState()){
            attributes = GraphObject.MARKED_STATE;
        }
        lastClone = new Node(gp, null, x, y, r, attributes, start_arrow_direction.x,
                start_arrow_direction.y, getGlyphLabel());
        return lastClone;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Node drawing
    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Draw this Node.
     * 
     * @param drawer
     *            The Drawer that will handle the drawing.
     */
    public void draw(Drawer drawer){
        if(isGrouped()) drawer.setColor(GraphModel.GROUPED);
        if(isTraceObject()) drawer.setColor(GraphModel.TRACE);
        if(isHotSelected()) drawer.setColor(GraphModel.HOT_SELECTED);

        drawer.drawCircle(x, y, r, Drawer.SOLID);

        if(isMarkedState()) drawer.drawCircle(x, y, r - RDIF, Drawer.SOLID);

        getGlyphLabel().drawLabel(drawer);

        if(isStartState()){
            drawer.setColor(GraphModel.NORMAL);
            if(isGrouped()) drawer.setColor(GraphModel.GROUPED);
            if(isStartArrowSelected() || isHotSelected()) drawer.setColor(GraphModel.SELECTED);
            arrowhead.draw(drawer);
            drawer.drawLine(arrowhead.xcoords[ArrowHead.NOCK], arrowhead.ycoords[ArrowHead.NOCK],
                    arrowhead.xcoords[ArrowHead.NOCK]
                            - (int) Math.round(ArrowHead.HEAD_LENGTH * start_arrow_direction.x),
                    arrowhead.ycoords[ArrowHead.NOCK]
                            - (int) Math.round(ArrowHead.HEAD_LENGTH * start_arrow_direction.y),
                    Drawer.SOLID);
        }

        if(partialEdgeEndpoint != null){
            UnitVector d = new UnitVector(partialEdgeEndpoint, origin());
            drawer.drawLine(this.x, this.y, (int) Math.round(partialEdgeEndpoint.getX()
                    + ArrowHead.HEAD_LENGTH * d.x), (int) Math.round(partialEdgeEndpoint.getY()
                    + ArrowHead.HEAD_LENGTH * d.y), Drawer.SOLID);
            d.reverse();
            ArrowHead partial_edge_arrow = new ArrowHead(d, partialEdgeEndpoint);
            partial_edge_arrow.draw(drawer);
        }

        drawer.setColor(GraphModel.NORMAL);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Node movement
    // //////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Calls initateMovement with exclusive=false
     */
    public void initiateMovement(Point origin, int attribute, int state_mask){
        initiateMovement(origin, attribute, false);
    }

    /**
     * Initiate movement of this Node and all associated edges. The exclusion
     * flag is used for group movement. Note that group movement modifies the
     * Node using the translateAll method.
     * 
     * @param mouse
     *            The origin of movement.
     * @param attribute
     *            The temporary attribute to be applied during movement.
     * @param exclusive
     *            If this is true than the Node is not modified, and only non
     *            exclusive edges are modified.
     */
    public void initiateMovement(Point mouse, int attribute, boolean exclusive){
        EdgeGroup edge_group = null;
        origional_configuration = new Configuration(this.origin(), arrowhead.tip(), mouse, this.r);
        addAttribute(attribute);
        for(int i = 0; i < edge_group_list.size(); i++){
            edge_group = (EdgeGroup) edge_group_list.elementAt(i);
            if(!exclusive || !edge_group.exclusive){
                edge_group.initiateNodeMovement(attribute);
            }
        }
    }

    /**
     * Calls updateMovement with exclusive=false
     */
    public void updateMovement(Point mouse){
        updateMovement(mouse, false);
    }

    /**
     * Update movement of this Node and all associated edges. The exclusion flag
     * is used for group movement. Note that group movement modifies the Node
     * using the translateAll method.
     * 
     * @param mouse
     *            The current mouse position.
     * @param exclusive
     *            If this is true than the Node is not modified, and only non
     *            exclusive edges are modified.
     */
    public void updateMovement(Point mouse, boolean exclusive){
        if(origional_configuration != null){
            if(!exclusive){
                Point displacement = new Point(mouse.getX()
                        - origional_configuration.movement_origin.getX(), mouse.getY()
                        - origional_configuration.movement_origin.getY());
                this.x = origional_configuration.origin.getX() + displacement.getX();
                this.y = origional_configuration.origin.getY() + displacement.getY();

                snapToGrid();

                arrowhead.update(start_arrow_direction, origional_configuration.arrow_tip.getX()
                        + this.x - origional_configuration.origin.getX(),
                        origional_configuration.arrow_tip.getY() + this.y
                                - origional_configuration.origin.getY());
            }
            // edges
            EdgeGroup edge_group = null;
            for(int i = 0; i < edge_group_list.size(); i++){
                edge_group = (EdgeGroup) edge_group_list.elementAt(i);
                if(!edge_group.exclusive){
                    edge_group.updateNodeMovement(this, origional_configuration, mouse);
                }
            }
        }

        getGlyphLabel().setAnchor(origin(), Label.CENTER);
    }

    /**
     * Calls terminateMovement with exclusive=false
     */
    public void terminateMovement(int attribute){
        terminateMovement(attribute, false);
    }

    /**
     * Terminate movement of this Node and all associated edges. The exclusion
     * flag is used for group movement. Note that group movement modifies the
     * Node using the translateAll method.
     * 
     * @param attribute
     *            The temporary attribute to be applied during movement.
     * @param exclusive
     *            If this is true than the Node is not modified, and only non
     *            exclusive edges are modified.
     */
    public void terminateMovement(int attribute, boolean exclusive){
        EdgeGroup edge_group = null;
        origional_configuration = null;
        removeAttribute(attribute);
        for(int i = 0; i < edge_group_list.size(); i++){
            edge_group = (EdgeGroup) edge_group_list.elementAt(i);
            if(!exclusive || !edge_group.exclusive){
                edge_group.terminateNodeMovement(attribute);
            }
        }
    }

    /**
     * Adjust settings of this GraphObject to take into consideration recent
     * changes to itself or its label. For example, if the Label changes size,
     * the Node may need to change size. Or if the node toggles its marked
     * value, it may need to be resized. Or if we have switched from one label
     * type to another label type, it may need it's origin updated and the node
     * may need to be resized.
     */
    public void accomodateLabel(){
        getGlyphLabel().setAnchor(origin(), Label.CENTER);

        getGlyphLabel().renderIfNeeded();

        int new_radius = getGlyphLabel().rendered_radius;
        if(getGlyphLabel().isntEmpty() && isMarkedState()){
            new_radius = new_radius + RDIF;
        }
        if(gm.max_node_size < new_radius){
            gm.max_node_size = new_radius;
            // because we changed the node size, we need to tell all the other
            // nodes to update.
            if(SystemVariables.use_standard_node_size) gm.accomodateLabels();
        }
        if(SystemVariables.use_standard_node_size) new_radius = gm.max_node_size;

        if(new_radius != r){
            initiateMovement(origin(), GraphObject.NULL, 0);
            r = new_radius;
            updateMovement(origin());
            terminateMovement(GraphObject.NULL);

            if(arrowhead != null){
                updateConfiguration(arrowhead.xcoords[ArrowHead.NOCK],
                        arrowhead.ycoords[ArrowHead.NOCK]);
            }
        }
    }

    /**
     * Copy any existing information from the abandoned label type into the new
     * label type providing that the new type has an empty value.
     */
    public void fillBlankLabels(){
        if(getGlyphLabel().string_representation.length() < 1){
            getGlyphLabel().string_representation = " ";
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Node methods
    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Displace this Node so that it is aligned with the grid of the GraphModel.
     */
    private void snapToGrid(){
        // snap to grid
        Point snaped = gm.snapToGrid(origin());
        this.x = snaped.getX();
        this.y = snaped.getY();
    }

    /**
     * Used only for the cursor location Node.
     * 
     * @param x
     *            The new x co-ordinate.
     * @param y
     *            The new y co-ordinate.
     */
    public void setOrigin(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     * Used when rendering latex label.
     * 
     * @param r
     *            The new radius.
     */
    public void setRadius(int r){
        this.r = r >= DEFAULT_RADIUS ? r : DEFAULT_RADIUS;
    }

    /**
     * @return The x co-ordinate for this node.
     */
    public int getX(){
        return x;
    }

    /**
     * @return The y co-ordinate for this node.
     */
    public int getY(){
        return y;
    }

    /**
     * @return The radius for this node.
     */
    public int getR(){
        return r;
    }

    /**
     * @return A Point representing the origin of this Node.
     */
    public Point origin(){
        return new Point(x, y);
    }

    /**
     * Delete this node and all its edges.
     */
    public void delete(){
        while(edge_group_list.size() > 0){
            edge_group_list.elementAt(0).delete();
        }
        edge_group_list = null;
        gm.safeNull(this);
        gm.removeNode(this);
        gm = null;
    }

    /**
     * Test if this node is contained in the given area.
     * 
     * @param area
     *            The bounding area to be considered.
     * @return true if this Node is contained in the given area.
     */
    public boolean isBoundBy(Box area){
        return (x - r > area.x1() && x + r < area.x2()) && (y - r > area.y1() && y + r < area.y2());
    }

    /**
     * Test if this Node should be selected by a mouse-click.
     * 
     * @param mouse
     *            The co-ordinates of the mouse.
     * @param padded
     *            If true then the nodes radius is padded by
     *            ArrowHead.HEAD_LENGTH, thereby accepting nearby points as
     *            well.
     * @return true if this Node should be selected by this mouse-click.
     */
    public boolean isLocated(Point mouse, boolean padded){
        int padding = padded ? ArrowHead.HEAD_LENGTH : 0;
        return origin().isInsideCircle(r + padding, mouse);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Arrow methods
    // //////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Used to determine if a mouse-click should select the start arrow of this
     * node (if it exists)
     * 
     * @param mouse
     *            The co-ordinates of the mouse.
     * @return true if the start arrow should be selected by this mouse click.
     */
    public boolean isLocatedStartArrow(Point mouse){
        return isStartState() ? arrowhead.isLocated(mouse.getX(), mouse.getY()) : false;
    }

    /**
     * Used in movement of the start state arrow. Displacement is origin-out,
     * hence reverse direction of start arrow.
     * 
     * @param x
     *            x co-ordinate of the mouse.
     * @param y
     *            y co-ordinate of the mouse.
     */
    public void updateConfiguration(int x, int y){
        start_arrow_direction = new UnitVector(this.x - x, this.y - y);
        arrowhead.update(start_arrow_direction, this.x
                - (int) Math.round(r * start_arrow_direction.x), this.y
                - (int) Math.round(r * start_arrow_direction.y));
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Edge methods
    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Reset all attached edges to their default configurations.
     */
    public void resetEdges(){
        for(int i = 0; i < edge_group_list.size(); i++){
            edge_group_list.elementAt(i).recalculate();
            edge_group_list.elementAt(i).accomodateLabels();
        }
    }

    /**
     * Connect a new Edge and Node to this Node. If there allready exists an
     * EdgeGroup between this Node and the new Node, simply add the new Edge to
     * it. Else create a new EdgeGroup between the two Nodes with the new Edge
     * as the initial Edge.
     * 
     * @param new_edge
     *            The new Edge to be added to this Node.
     * @param new_neighbour
     *            The Node that will be made adjacent to this Node by the new
     *            Edge.
     * @return The EdgeGroup of the new Edge.
     */
    public EdgeGroup join(Edge new_edge, Node new_neighbour){
        for(int i = 0; i < edge_group_list.size(); i++){
            EdgeGroup e = edge_group_list.elementAt(i);
            if(e.hasNodes(this, new_neighbour)){
                e.addEdge(new_edge);
                return e;
            }
        }
        EdgeGroup e = new EdgeGroup(this, new_neighbour, new_edge);
        edge_group_list.addElement(e);
        if(this != new_neighbour) new_neighbour.addEdgeGroup(e);
        return e;
    }

    /**
     * Add an EdgeGroup to this Node's edge_group_list
     * 
     * @param edge_group
     *            The EdgeGroup to be added to this Node's edge_group_list.
     */
    public void addEdgeGroup(EdgeGroup edge_group){
        edge_group_list.addElement(edge_group);
    }

    /**
     * Delete the specified EdgeGroup from this Node.
     * 
     * @param edge_group
     *            The EdgeGroup to be deleted.
     */
    public void removeEdgeGroup(EdgeGroup edge_group){
        edge_group_list.remove(edge_group);
    }

    /**
     * Look for edges that join this Node to a node in the given nodeList
     * 
     * @param nodeList
     *            The list of valid nodes
     * @return A Vector of edges that join this Node to Nodes in the given
     *         nodeList
     */
    public Vector<Edge> fetchEdges(Vector<Node> nodeList){
        Vector<Edge> validEdges = new Vector<Edge>();
        for(int i = 0; i < edge_group_list.size(); i++){
            EdgeGroup edgeGroup = edge_group_list.elementAt(i);
            edgeGroup.exclusive = false;
            for(int j = 0; j < nodeList.size(); j++){
                if(edgeGroup.hasNodes(this, nodeList.elementAt(j))){
                    edgeGroup.addToList(validEdges);
                    edgeGroup.exclusive = true;
                    break;
                }
            }
        }
        return validEdges;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // miscellaneous
    // //////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Translate all variables.
     * 
     * @param x
     *            Translation in the x direction.
     * @param y
     *            Translation in the y direction.
     */
    public void translateAll(int x, int y){
        this.x = this.x + x;
        this.y = this.y + y;
        arrowhead.translateAll(x, y);
        getGlyphLabel().setAnchor(origin(), Label.CENTER);
    }
}