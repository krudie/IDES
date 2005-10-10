/*
 * Created on Jun 21, 2004
 */
package userinterface.graphcontrol.graphparts;

import java.util.Vector;

import org.eclipse.swt.widgets.TableItem;

import userinterface.GraphingPlatform;
import userinterface.geometric.Line;
import userinterface.geometric.Point;
import userinterface.geometric.UnitVector;
import userinterface.graphcontrol.Drawer;
import userinterface.graphcontrol.GraphModel;
import userinterface.graphcontrol.EventSpecification;

/**
 * An Edge is a Curve connecting two Nodes with and ArrowHead at one end. The
 * main job of this class is to handle more GraphModel based concepts, while the
 * Curve class itself exist to handle more Geometric based concepts.
 * 
 * All GraphObjects are directly aware of the GraphModel. An Edge may also be
 * directly aware of its Label, its ArrowHead, and it's start and end Nodes, and
 * the EdgeGroup of which it is a part.
 * 
 * @author Michael Wood
 */
public class Edge extends GraphObject{
    /**
     * possible values for selection_state
     */
    public static final int NO_ANCHORS = 0, EXCLUSIVE = 1;

    /**
     * region constants: possible values for last_hit_region
     */
    public static final int R_ARROWHEAD = 0, R_TAIL_ANCHOR = 1, R_TAIL_CTRL = 2, R_HEAD_ANCHOR = 3,
            R_HEAD_CTRL = 4, R_LABEL = 5, R_NONE = 6, R_LOOP = 7;

    /**
     * location constants: possible values for options when performing hit tests
     * note: tethers refer to labels, and anchors refer to the curve.
     */
    public static final int L_NULL = 0, L_ALL_ANCHORS = 1, L_ALL_TETHERS = 2, L_NO_ANCHORS = 4,
            L_NO_TETHERS = 8, L_PADDED = 16;

    /**
     * the default values for the label_displacement variable
     */
    public static final int DEFAULT_LABEL_DISPLACEMENT = 5;

    /**
     * The Node where this Edge originates.
     */
    private Node source = null;

    public Node getSource(){
        return source;
    }

    /**
     * The Node where this Edge terminates.
     */
    private Node target = null;

    public Node getTarget(){
        return target;
    }

    /**
     * The EdgeGroup that contains this Edge.
     */
    private EdgeGroup edge_group = null;

    /**
     * Determines edge behaviour, such as visibility, colour, etc of anchors and
     * the tether.
     */
    private int selectionState = NO_ANCHORS;

    public int getSelectionState(){
        return selectionState;
    }

    public void setSelectionState(int selectionState){
        this.selectionState = selectionState;
    }

    /**
     * Visual representation of the edge.
     */
    private Curve curve = null;

    /**
     * records which region was last hit by the mouse. uses region constants
     * from Edge as valid values.
     */
    private int lastHitRegion = Edge.R_NONE;

    public int getLastHitRegion(){
        return lastHitRegion;
    }

    public void setLastHitRegion(int lhr){
        lastHitRegion = lhr;
    }

    /**
     * The label data for this Edge
     */
    private Vector<TableItem> label_data = null;

    /**
     * Represents the displacement from the t=0.5 point of the bezier curve to
     * the top left corner of the label
     */
    private Point label_displacement = null;

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Edge construction
    // //////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construct the Edge. (used at user creation)
     * 
     * @param gp
     *            The GraphingPlatform in which this Edge will exist.
     * @param gm
     *            The GraphModel in which this Edge will exist.
     * @param start_node
     *            The Node where the Edge originates.
     * @param end_node
     *            The Node where the Edge terminates.
     */
    public Edge(GraphingPlatform gp, GraphModel gm, Node start_node, Node end_node){
        super(gp, gm, GraphObject.SIMPLE);
        constructEdge(start_node, end_node);
        initializeLabels(DEFAULT_LABEL_DISPLACEMENT, DEFAULT_LABEL_DISPLACEMENT);

        curve = isSelfLoop() ? new Curve(source, target, edge_group.newUnitVector(this))
                : new Curve(source, target);

        edge_group.recalculate();
    }

    /**
     * Construct the Edge. (used at cloning)
     * 
     * @param gp
     *            The GraphingPlatform in which this Edge will exist.
     * @param gm
     *            The GraphModel in which this Edge will exist.
     * @param start_node
     *            The node where the edge originates.
     * @param end_node
     *            The node where the edge terminates.
     * @param curve
     *            A cloned curve to be used.
     * @param label_displacement
     *            A cloned Point representing the displacement of the label from
     *            the midpoint of the curve.
     * @param label_data
     *            A cloned Vector containing the required label data objects.
     * @param a
     *            The attributes for this Edge.
     * @param glyph_label
     *            A Label to be cloned for the glyph label of this Edge.
     * @param latex_label
     *            A Label to be cloned for the latex label of this Edge.
     */
    private Edge(GraphingPlatform gp, GraphModel gm, Node start_node, Node end_node, Curve curve,
            Point label_displacement, int a, Vector<TableItem> label_data, GlyphLabel glyphLabel){
        super(gp, gm, a);
        constructEdge(start_node, end_node);

        this.curve = curve;
        this.label_displacement = label_displacement;
        this.label_data = label_data;
        this.setGlyphLabel(new GlyphLabel(gp, this, glyphLabel));
    }

    /**
     * Construct the Edge. (used at load from file)
     * 
     * @param gp
     *            The GraphingPlatform in which this Edge will exist.
     * @param gm
     *            The GraphModel in which this Edge will exist.
     * @param start_node
     *            The node where the edge originates.
     * @param end_node
     *            The node where the edge terminates.
     * @param x1
     *            x1 parameter for the curve object
     * @param y1
     *            y1 parameter for the curve object
     * @param ctrlx1
     *            ctrlx1 parameter for the curve object
     * @param ctrly1
     *            ctrly1 parameter for the curve object
     * @param ctrlx2
     *            ctrlx2 parameter for the curve object
     * @param ctrly2
     *            ctrly2 parameter for the curve object
     * @param x2
     *            x2 parameter for the curve object
     * @param y2
     *            y2 parameter for the curve object
     * @param dx
     *            x parameter for the direction object
     * @param dy
     *            y parameter for the direction object
     * @param gtx
     *            x parameter for the label_displacement object
     * @param gty
     *            y parameter for the label_displacement object
     * @param a
     *            The attributes for this Edge.
     */
    public Edge(GraphingPlatform gp, GraphModel gm, Node start_node, Node end_node, float x1,
            float y1, float ctrlx1, float ctrly1, float ctrlx2, float ctrly2, float x2, float y2,
            float dx, float dy, int gtx, int gty, int a){
        super(gp, gm, a);
        constructEdge(start_node, end_node);
        initializeLabels(gtx, gty);

        curve = new Curve(source, target, x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2, new UnitVector(
                dx, dy));
    }

    /**
     * Set the class variables of this Edge.
     * 
     * @param start_node
     *            The node where the edge originates.
     * @param end_node
     *            The node where the edge terminates.
     */
    private void constructEdge(Node start_node, Node end_node){
        source = start_node;
        target = end_node;
        edge_group = source.join(this, target);
    }

    /**
     * Set the class variables of this Edge.
     * 
     * @param gtx
     *            x parameter for the label_displacement object
     * @param gty
     *            y parameter for the label_displacement object
     */
    private void initializeLabels(int gtx, int gty){
        label_displacement = new Point(gtx, gty);
        label_data = new Vector<TableItem>();
        setGlyphLabel(new GlyphLabel(gp, this));
    }

    /**
     * Create a clone of this Edge, with null as its GraphModel. If it's nodes
     * were not previously cloned, this will return null. Note: after cloning
     * nodes, then edges, you should null all the lastClones of the nodes.
     * 
     * @return A clone of this Edge.
     */
    public Edge newClone(){
        if(source.getLastClone() == null || target.getLastClone() == null) return null;
        int clone_attribute = GraphObject.NULL;
        if(isSimple()) clone_attribute = GraphObject.SIMPLE;
        return new Edge(gp, null, source.getLastClone(), target.getLastClone(), curve.newClone(source
                .getLastClone(), target.getLastClone()), label_displacement.getCopy(), clone_attribute,
                getLabelDataVector(), getGlyphLabel());
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Edge calculation
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Initiate for movement by node position.
     */
    public void initiateNodeMovement(int attribute){
        curve.initiateNodeMovement();
        addAttribute(attribute);
    }

    /**
     * Recalculate the parameters of this Edge based on a node movement, keeping
     * the origional edge configuration.
     * 
     * @param n
     *            The initiating Node.
     * @param origional_configuration
     *            The configuration of the Node at the initiation of movement.
     * @param mouse
     *            The current mouse position.
     */
    public void updateNodeMovement(Node n, Configuration origional_configuration, Point mouse){
        if(isSimple()) autoConfigureCurve();
        else if(isSelfLoop()) curve.recalculateSelfLoop();
        else if(n == source) curve.updateNodeMovement(origional_configuration, mouse, target, source);
        else curve.updateNodeMovement(origional_configuration, mouse, source, target);

        getGlyphLabel().setAnchor(label_displacement.plus(curve.calculateBezierPoint((float) 0.5)),
                Label.CORNER);
    }

    /**
     * Terminate movement by node position.
     */
    public void terminateNodeMovement(int attribute){
        removeAttribute(attribute);
    }

    /**
     * Recalculate the parameters of this Edge using an automatic algorithm.
     */
    public void autoConfigureCurve(){
        if(isSelfLoop()){
            curve.recalculateSelfLoop();
            return;
        }
        int edge_position = edge_group.indexOf(this);
        int edge_group_levels = edge_group.levels();
        boolean odd_number_in_group = edge_group.hasOddEdges();
        boolean intersects_node = gm.findNode(source, target);
        boolean against_group_direction = edge_group.isStartNode(target);
        int level = 0;
        float angle = 120 / (edge_group_levels + 1);

        if(odd_number_in_group && !intersects_node){
            if(edge_position == 0){
                curve.calculateCurve(0, 0); // strait edge
                return;
            }
            level = (int) Math.ceil(edge_position / 2.0);
            if(edge_position % 2 == 0) level = -level;
            // swap side if our assumption of direction was false
            if(against_group_direction) level = -level;

            curve.calculateCurve(level * 8, level * angle);
            return;
        }

        level = (int) Math.ceil((edge_position + 1) / 2.0);
        if(intersects_node){
            level = level + 2;
            angle = 120 / (edge_group_levels + 3);
        }
        if(edge_position % 2 == 1) level = -level;
        // swap side if our assumption of direction was false
        if(against_group_direction) level = -level;
        float adjust = angle / 2;
        // because there is no center edge,
        // we want all angles to be less by
        // one half increment
        if(level < 0) adjust = -adjust;
        // because we want a decrease in magnitude
        curve.calculateCurve(level * 8, level * angle - adjust);
    }

    public void autoStraightenCurve(){
        curve.calculateCurve(0, 0); // strait edge
        removeAttribute(GraphObject.SIMPLE);
    }

    public void autoArcMore(){
        float angle = (float) Math.toDegrees(curve.headAnchorAngle());
        // force angle to -180 ... 180 (it represents the angle from the
        // bisector)

        if(angle > 180) angle = 360 - angle;
        if(angle < -180) angle = 360 + angle;
        // increase the size to a maximum of +-90
        if(angle >= 0 && angle < 90) angle = angle + 10;
        else if(angle < 0 && angle > -90) angle = angle - 10;
        // fix the angle for convention of calculateCurve
        angle = -angle;

        // compute and increase the rise
        Line bisector = new Line(source.origin(), target.origin());
        float rise = bisector.perpendicularDistance(curve.headCtrl());
        rise = (float) (rise * 1.1);
        // fix the rise for convention of calculateCurve
        if(angle < 0) rise = -rise;

        curve.calculateCurve(rise, angle);
        removeAttribute(GraphObject.SIMPLE);
    }

    public void autoArcLess(){
        float angle = (float) Math.toDegrees(curve.headAnchorAngle());
        // force angle to -180 ... 180 (it represents the angle from the
        // bisector)
        if(angle > 180) angle = 360 - angle;
        if(angle < -180) angle = 360 + angle;
        // decrease the size
        angle += angle > 0 ? -10 : 10;
        // fix the angle for convention of calculateCurve
        angle *= -1;

        // compute and decrease the rise
        Line bisector = new Line(source.origin(), target.origin());
        float rise = bisector.perpendicularDistance(curve.headCtrl());
        float factor = (float) ((100 - Math.abs(angle)) / 100);
        rise -= rise * factor;
        // fix the rise for convention of calculateCurve
        if(angle < 0) rise *= -1;
        curve.calculateCurve(rise, angle);
        removeAttribute(GraphObject.SIMPLE);
    }
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Edge drawing
    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Draw this edge. It's style is determined here by testing it's properties,
     * and it's edgegroup.
     * 
     * @param drawer
     *            The Drawer that will handle the drawing.
     * @param all_anchors
     *            True if all edges in the graph should draw their anchors
     *            (which define the curves).
     * @param all_tethers
     *            True if all edges in the graph should draw their tethers
     *            (which define the label positions).
     */
    public void draw(Drawer drawer, boolean all_anchors, boolean all_tethers){
        if(isGrouped()) drawer.setColor(GraphModel.GROUPED);
        if(isTraceObject()) drawer.setColor(GraphModel.TRACE);
        if(isHotSelected()) drawer.setColor(GraphModel.HOT_SELECTED);

        if(hasUncontrollableLabel()) curve.drawCurve(drawer, Drawer.DASHED);
        else curve.drawCurve(drawer, Drawer.SOLID);

        if(!labelDataIsNull()){
            getGlyphLabel().drawData(drawer, label_data);

            if(all_tethers || selectionState == Edge.EXCLUSIVE){
                Point destination = curve.calculateBezierPoint((float) 0.5);
                if(isHotSelected() && lastHitRegion == Edge.R_LABEL) drawer
                        .setColor(GraphModel.CUSTOM);
                else drawer.setColor(GraphModel.TETHERS);

                getGlyphLabel().drawBox(drawer);
                getGlyphLabel().drawTether(drawer, destination);
            }
        }

        if(all_anchors || selectionState == Edge.EXCLUSIVE){
            drawer
                    .setColor((isHotSelected() && lastHitRegion != R_NONE && lastHitRegion != R_LABEL) ? GraphModel.CUSTOM
                            : GraphModel.ANCHORS);

            if(isSelfLoop()) curve.drawSelfLoopAnchor(drawer);
            else{
                curve.drawHeadAnchors(drawer);
                curve.drawTailAnchors(drawer);
            }
        }
        drawer.setColor(GraphModel.NORMAL);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Edge selection
    // /////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Used to determine if a mouse-click should select this edge or its label.
     * This first checks if the click falls into any of the edges anchors, or on
     * the arrowhead. Then it checks if it falls in the bounding box of the
     * label, if one exists. For the options variable use the locations
     * constants from the Edge class (start with L_). It is allowed to OR
     * multiple constants.
     * 
     * @param x
     *            x co-ordinate of the mouse.
     * @param y
     *            y co-ordinate of the mouse.
     * @param options
     *            selection options regarding anchors and tethers. Use the
     *            locations constants from the Edge class (start with L_). It is
     *            allowed to OR multiple constants.
     * @return true if this edge should be selected by this mouse click.
     */
    public boolean isLocated(int x, int y, int options){
        boolean all_anchors = ((options & Edge.L_ALL_ANCHORS) > 0);
        boolean padded = ((options & Edge.L_PADDED) > 0);
        Point mouse = new Point(x, y);

        // anchors
        if((options & Edge.L_NO_ANCHORS) == 0 && (all_anchors || selectionState == Edge.EXCLUSIVE)){
            if(isSelfLoop()) lastHitRegion = curve.isLocatedSelfLoop(mouse, padded);
            else lastHitRegion = curve.isLocatedAnchors(mouse, padded);
            if(lastHitRegion != Edge.R_NONE) return true;
        }

        // arrowhead
        if((options & Edge.L_ALL_TETHERS) == 0){
            lastHitRegion = curve.isLocatedArrowhead(mouse, padded);
            if(lastHitRegion != Edge.R_NONE) return true;
        }

        // tethers
        if(!labelDataIsNull() && ((options & Edge.L_NO_TETHERS) == 0)){
            // vary the selection area based on whether or not the bounding box
            // is being displayed.
            int adjustment = Label.BOUNDING_BOX_FACTOR;
            if(padded) adjustment = 2 * adjustment;

            if(((options & Edge.L_ALL_TETHERS) > 0) || selectionState == Edge.EXCLUSIVE) adjustment = 0;

            if(getGlyphLabel().isLocated(new Point(x, y))){
                lastHitRegion = Edge.R_LABEL;
                return true;
            }
        }

        lastHitRegion = Edge.R_NONE;
        return false;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Edge movement
    // //////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void initiateMovement(Point mouse, int attribute, int state_mask){
        removeAttribute(GraphObject.SIMPLE);

        Point origin = null;
        if(lastHitRegion == Edge.R_TAIL_ANCHOR || lastHitRegion == Edge.R_TAIL_CTRL) origin = source
                .origin();
        else origin = target.origin();

        Point selection_target = null;
        if(isSelfLoop()) selection_target = curve.selfLoopAnchor();
        else{
            switch(lastHitRegion){
            case Edge.R_TAIL_ANCHOR:
                selection_target = curve.tailAnchor();
                break;
            case Edge.R_TAIL_CTRL:
                selection_target = curve.tailCtrl();
                break;
            case Edge.R_HEAD_CTRL:
                selection_target = curve.headCtrl();
                break;
            default:
                selection_target = curve.headAnchor();
            }
        }

        origional_configuration = new Configuration(origin, curve.tailAnchor(), curve.tailCtrl(),
                curve.headCtrl(), curve.headAnchor(), label_displacement, mouse, selection_target,
                state_mask);
        addAttribute(attribute);
    }

    public void updateMovement(Point mouse){
        if(isSelfLoop() && (lastHitRegion == Edge.R_LOOP || lastHitRegion == Edge.R_ARROWHEAD)){
            curve.moveSelfLoop(origional_configuration, mouse);
        }
        else{
            switch(lastHitRegion){
            case Edge.R_TAIL_ANCHOR:
                curve.moveTailAnchor(origional_configuration, mouse);
                break;
            case Edge.R_TAIL_CTRL:
                curve.moveTailCtrl(origional_configuration, mouse);
                break;
            case Edge.R_HEAD_CTRL:
                curve.moveHeadCtrl(origional_configuration, mouse);
                break;
            case Edge.R_HEAD_ANCHOR:
            case Edge.R_ARROWHEAD:
                curve.moveHeadAnchor(origional_configuration, mouse);
                break;
            case Edge.R_LABEL:
                label_displacement.setX(origional_configuration.label_displacement.getX()
                        + (mouse.getX() - origional_configuration.movement_origin.getX()));
                label_displacement.setY(origional_configuration.label_displacement.getY()
                        + (mouse.getY() - origional_configuration.movement_origin.getY()));
                break;
            }
        }
        getGlyphLabel().setAnchor(label_displacement.plus(curve.calculateBezierPoint((float) 0.5)),
                Label.CORNER);
    }

    public void terminateMovement(int attribute){
        origional_configuration = null;
        removeAttribute(attribute);
        lastHitRegion = Edge.R_NONE;
    }

    /**
     * Adjust settings of this GraphObject to take into consideration recent
     * changes to it's label.
     */
    public void accomodateLabel(){
        getGlyphLabel().setAnchor(label_displacement.plus(curve.calculateBezierPoint((float) 0.5)),
                Label.CORNER);

        String representation = getLabelDataString();
        if(!getGlyphLabel().string_representation.equals(representation)){
            // the label has changed we must render it.
            getGlyphLabel().string_representation = representation;
            getGlyphLabel().render();
        }

        getGlyphLabel().renderIfNeeded();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Labels
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Add a label to this Edge
     * 
     * @param new_label_data
     *            The label to be added to this Edge
     */
    public void addLabel(TableItem new_label_data){
        label_data.add(new_label_data);
    }

    /**
     * Remove a label from this Edge
     * 
     * @param new_label_data
     *            The label to be removed from this Edge
     */
    public void removeLabel(TableItem new_label_data){
        label_data.remove(new_label_data);
    }

    /**
     * Tests the label of this Edge
     * 
     * @param label
     *            A test value.
     * @return true if the test value matches the text in the label_data.
     */
    public boolean checkLabel(Object label){
        return !labelDataIsNull() && label_data.contains(label);
    }

    /**
     * Tests if the label_data object should be null
     * 
     * @return true if the label_data object should be null
     */
    public boolean labelDataIsNull(){
        int i = 0;
        while(i < label_data.size()){
            if(label_data.elementAt(i).isDisposed()) label_data.removeElementAt(i);
            else i++;
        }
        return label_data.size() == 0;
    }

    /**
     * Calculates the string to be displayed from the array of associated
     * TableItems
     * 
     * @return The string to be displayed.
     */
    private String getLabelDataString(){
        if(labelDataIsNull()) return "";

        int column = TransitionData.SPEC_SYMBOL;


        String representation = ((TableItem) label_data.elementAt(0)).getText(column);
        for(int i = 1; i < label_data.size(); i++){
            representation += ", " + ((TableItem) label_data.elementAt(i)).getText(column);
        }
        return representation;
    }

    /**
     * Delivers a copy of the label_data Vector of this Edge.
     * 
     * @return A copy of the label_data Vector of this Edge.
     */
    public Vector<TableItem> getLabelDataVector(){
        labelDataIsNull();
        return new Vector<TableItem>(label_data);
    }


    /**
     * Test if this edge has any labels that are uncontrollable transitions.
     * 
     * @return true if this edge bears any uncontrollable transitions.
     */
    private boolean hasUncontrollableLabel(){
        if(labelDataIsNull()) return false;
        
        for(int i = 0; i < label_data.size(); i++){
            if(label_data.elementAt(i).getText(TransitionData.SPEEventSpecification)
                    .equals(TransitionData.BOOLEEventSpecification)) return true;
        }
        return false;
    }

    public Point getLabelDisplacement(){
        return label_displacement;
    }

    public void setLabelDisplacement(Point label_displacement){
        this.label_displacement = label_displacement;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // miscellaneous
    // //////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Access a copy of the midpoint of the curve.
     * 
     * @return A copy of the midpoint of the curve.
     */
    public Point midpoint(){
        return curve.midpoint();
    }

    /**
     * Put the arrowhead on the other end.
     */
    public void reverseDirection(){
        if(!isSelfLoop()){
            Node n = source;
            source = target;
            target = n;
            if(isSimple()){
                curve.reverseDirection(false);
                autoConfigureCurve();
            }
            else curve.reverseDirection(true);
        }
    }

    /**
     * Delete this Edge from it's EdgeGroup
     */
    public void delete(){
        source = null;
        target = null;
        curve.dispose();
        curve = null;
        label_data = null;
        setGlyphLabel(null);

        edge_group.removeEdge(this);
        edge_group = null;

        gm.safeNull(this);
        gm.removeEdge(this);
        gm = null;
    }

    /**
     * If source == target then this is a self loop edge.
     * 
     * @return true if it is a self loop edge.
     */
    public boolean isSelfLoop(){
        return source == target;
    }

    /**
     * Translate all variables.
     * 
     * @param x
     *            Translation in the x direction.
     * @param y
     *            Translation in the y direction.
     */
    public void translateAll(int x, int y){
        curve.translateAll(x, y);
        getGlyphLabel().setAnchor(label_displacement.plus(curve.calculateBezierPoint((float) 0.5)),
                Label.CORNER);
    }
}
