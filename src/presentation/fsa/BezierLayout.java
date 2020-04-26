package presentation.fsa;

import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import presentation.CubicParamCurve2D;
import presentation.Geometry;
import presentation.GraphicalLayout;

/**
 * Graphical data and operations for visual display of a BezierEdge.
 * 
 * @author Helen Bretzke
 */
public class BezierLayout extends GraphicalLayout implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4631267756770700873L;

    /* the edge to be laid out */
    private BezierEdge edge;

    public static final long UNGROUPPED = -1;

    /* Indices of bezier curve control points. */
    public static final int P1 = 0;

    public static final int CTRL1 = 1;

    public static final int CTRL2 = 2;

    public static final int P2 = 3;

    protected long group = UNGROUPPED;

    public void setGroup(long i) {
        group = i;
    }

    public long getGroup() {
        return group;
    }

    /* default displacement vector for the label from the midpoint of the edge */
    public final Point2D.Float DEFAULT_LABEL_OFFSET = new Point2D.Float(5, 5);

    /* lower bound for abs(angle), below which is angle set to zero. */
    public static final double EPSILON = 0.0001;

    /*
     * List of event symbols to be displayed on the edge's label TODO redundant
     * storage since this information should only be stored in the label's layout
     */
    private ArrayList<String> eventNames;

    /** the curve representing the edge being laid out */
    protected CubicParamCurve2D curve;

    /*
     * Compact representation of data required to maintain shape of edge while
     * moving one or both of its nodes.
     */
    protected static final Float UNIT_VERTICAL = new Point2D.Float(0, -1);

    private static final double DEFAULT_CONTROL_HANDLE_SCALAR = 1.0 / 3.0f;

    private static final double DEFAULT_CONTROL_HANDLE_ANGLE = Math.PI / 6;

    protected double s1 = DEFAULT_CONTROL_HANDLE_SCALAR; // scalar |(CTRL1 -

    // P1)|/|(P2-P1)|

    protected double s2 = DEFAULT_CONTROL_HANDLE_SCALAR; // scalar |(CTRL2 -

    // P2)|/|(P1-P2)|

    protected double angle1 = 0.0; // angle between (CTRL1 - P1) and (P2-P1)

    protected double angle2 = 0.0; // angle between (CTRL2 - P2) and (P1-P2)

    /*
     * the start and end parameters for the visible portion of the curve initialized
     * such that the entire curve is visible
     */
    protected float sourceT = 0;

    protected float targetT = 1;

    /**
     * Creates a default layout for a bezier curve.
     */
    public BezierLayout() {
        curve = new CubicParamCurve2D();
        eventNames = new ArrayList<String>();
        setLabelOffset(DEFAULT_LABEL_OFFSET);
    }

    /**
     * Creates a layout for a bezier edge with the given control points.
     * 
     * @param bezierControls the control points for the edge
     */
    public BezierLayout(Point2D.Float[] bezierControls) {
        curve = new CubicParamCurve2D();
        curve.setCurve(bezierControls, 0);
        eventNames = new ArrayList<String>();
        setLabelOffset(DEFAULT_LABEL_OFFSET);
        updateAnglesAndScalars();
        setDirty(true);
    }

    /**
     * Creates a layout for a bezier edge with the given control points and event
     * names.
     * 
     * @param bezierControls the control points for the edge
     * @param eventNames     list of event symbols
     */
    public BezierLayout(Point2D.Float[] bezierControls, ArrayList<String> eventNames) {
        curve = new CubicParamCurve2D();
        curve.setCurve(bezierControls, 0);
        this.eventNames = eventNames;
        setLabelOffset(DEFAULT_LABEL_OFFSET);
        setDirty(true);
        updateAnglesAndScalars();
    }

    /**
     * Constructs an edge layout object for a straight, directed edge connecting the
     * nodes with source and target layouts <code>n1</code> and <code>n2</code>
     * respectively.
     * 
     * @param sourceLayout layout for source node
     * @param targetLayout layout for target node
     */
    public BezierLayout(CircleNodeLayout sourceLayout, CircleNodeLayout targetLayout) {
        curve = new CubicParamCurve2D();
        computeCurve(sourceLayout, targetLayout);
        eventNames = new ArrayList<String>();
        setLabelOffset(DEFAULT_LABEL_OFFSET);
        // updateAnglesAndScalars();
    }

    /**
     * Creates a layout with same edge and curve as <code>other</code> and default
     * label offset.
     * 
     * @param other the other layout
     */
    BezierLayout(BezierLayout other) {
        edge = other.edge;
        curve = new CubicParamCurve2D(other.curve);
        updateAnglesAndScalars();
        eventNames = new ArrayList<String>();
        setLabelOffset(DEFAULT_LABEL_OFFSET);
    }

    /**
     * Sets the edge to <code>edge</code>.
     * 
     * @param edge the edge to be set
     */
    public void setEdge(BezierEdge edge) {
        this.edge = edge;
        setDirty(true);
    }

    /**
     * Returns the edge associated with this layout.
     * 
     * @return the edge
     */
    public BezierEdge getEdge() {
        return edge;
    }

    /**
     * Returns true iff <code>o</code> is an instance of BezierLayout and this
     * layout has the same curve and label offset as <code>o</code>.
     * 
     * @param o the other layout to be compared
     * @return true iff <code>o</code> is an instance of BezierLayout and this
     *         layout has the same curve and label offset as <code>o</code>.
     */
    @Override
    public boolean equals(Object o) {

        try {
            BezierLayout other = (BezierLayout) o;
            // DEBUG
            /*
             * if(other instanceof ReflexiveLayout || this instanceof ReflexiveLayout){
             * System.out.println(other.getText() + " " + other.getLabelOffset() + " " +
             * other.curve); System.out.println(this.getText() + " " + this.getLabelOffset()
             * + " " + this.curve); System.out.println(); }
             */

            return other.curve.equals(this.curve) && other.getLabelOffset().equals(this.getLabelOffset());
        } catch (ClassCastException cce) {
            return false;
        }
    }

    /**
     * Calls computeCurve(NodeLayout s, NodeLayout t) with source and target layouts
     * for this layout's edge.
     */
    public void computeCurve() {
        if (edge != null) {
            computeCurve((CircleNodeLayout) edge.getSourceNode().getLayout(),
                    (CircleNodeLayout) edge.getTargetNode().getLayout());
        }
    }

    /**
     * Returns an array of 4 control points for a straight, directed edge from
     * <code>s</code>, the layout for the source node to <code>t</code>, the layout
     * for the target node. Precondition: must call updateAnglesAndScalars() before
     * calling this method to ensure that the curve shape is in sync.
     * 
     * @param s layout for source node, s != null
     * @param t layout for target node, t != null
     */
    public void computeCurve(CircleNodeLayout s, CircleNodeLayout t) {
        Point2D.Float centre1 = s.getLocation();
        Point2D.Float centre2 = t.getLocation();
        Point2D.Float[] ctrls = new Point2D.Float[4];

        // TODO remove self-loop case once ReflexiveEdge class is fully debugged
        if (s.equals(t)) {
            // endpoints are at intersections of circle with rotations from
            // vertical vector
            float targetRadius = s.getRadius();
            ctrls[P1] = Geometry.add(centre1, Geometry.rotate(Geometry.scale(UNIT_VERTICAL, targetRadius), angle1));
            ctrls[P2] = Geometry.add(centre1, Geometry.rotate(Geometry.scale(UNIT_VERTICAL, targetRadius), angle2));
            ctrls[CTRL1] = Geometry.add(ctrls[P1],
                    Geometry.rotate(Geometry.scale(UNIT_VERTICAL, targetRadius), angle1));
            ctrls[CTRL2] = Geometry.add(ctrls[P2],
                    Geometry.rotate(Geometry.scale(UNIT_VERTICAL, targetRadius), angle2));
        } else {

            Point2D.Float base = Geometry.subtract(centre2, centre1);
            float norm = (float) Geometry.norm(base);
            Point2D.Float unitBase = Geometry.unit(base); // computing norm
            // twice :(

            // endpoints are at node centres
            ctrls[P1] = s.getLocation();//
            ctrls[P2] = t.getLocation();

            base = Geometry.subtract(ctrls[P2], ctrls[P1]);
            norm = (float) Geometry.norm(base);
            unitBase = Geometry.unit(base);

            if (isStraight()) { // compute a default straight edge
                angle1 = 0;
                angle2 = 0;
                s1 = DEFAULT_CONTROL_HANDLE_SCALAR;
                s2 = DEFAULT_CONTROL_HANDLE_SCALAR;

                ctrls[CTRL1] = Geometry.add(ctrls[P1], Geometry.scale(unitBase, (float) (norm * s1)));
                ctrls[CTRL2] = Geometry.add(ctrls[P2], Geometry.scale(unitBase, -(float) (norm * s2)));

            } else { // recompute the edge preserving the shape of the curve

                ctrls[CTRL1] = Geometry.add(ctrls[P1], Geometry.rotate(Geometry.scale(base, (float) s1), angle1));
                ctrls[CTRL2] = Geometry.add(ctrls[P2], Geometry.rotate(Geometry.scale(base, -(float) s2), angle2));

            }
        }

        curve.setCurve(ctrls, 0);
        Point2D midpoint = Geometry.midpoint(curve);
        setLocation((float) midpoint.getX(), (float) midpoint.getY());

        setDirty(true);
    }

    /**
     * Returns true iff the edge is close enough to straight; i.e. has tangents
     * within angle EPSILON of being parallel to to a straight edge.
     * 
     * @return true iff the edge is straight enough
     */
    protected boolean isStraight() {
        return Math.abs(angle1) < EPSILON && Math.abs(angle2) < EPSILON;
    }

    /**
     * Computes and stores the control points for a straight, directed edge from
     * <code>s</code>, the layout for the source node to endpoint <code>c2</code>.
     * 
     * @param s        layout for source node
     * @param endPoint endpoint for the edge
     */
    public void computeCurve(CircleNodeLayout s, Point2D.Float endPoint) {

        Point2D.Float[] ctrls = new Point2D.Float[4];
        Point2D.Float centre1 = s.getLocation();
        ctrls[P1] = centre1;
        ctrls[P2] = endPoint;
        if (s.getLocation().distance(endPoint) < 0.00001) {
            // set control points to node's centre
            ctrls[CTRL1] = centre1;
            ctrls[CTRL2] = endPoint;
        } else {
            Point2D.Float dir = Geometry.subtract(endPoint, centre1);
            float norm = (float) Geometry.norm(dir);
            Point2D.Float unit = Geometry.unit(dir); // computing norm twice
            // :(
            dir = Geometry.subtract(endPoint, ctrls[P1]);
            norm = (float) Geometry.norm(dir);
            unit = Geometry.unit(dir);
            ctrls[CTRL1] = Geometry.add(ctrls[P1], Geometry.scale(unit, (float) (norm * s1)));
            ctrls[CTRL2] = Geometry.add(ctrls[P2], Geometry.scale(unit, (float) (-norm * s2)));
        }
        curve.setCurve(ctrls, 0);
        Point2D midpoint = Geometry.midpoint(curve);
        setLocation((float) midpoint.getX(), (float) midpoint.getY());
        setDirty(true);
    }

    /**
     * Computes and store the shape of the curve so that it can be preserved under
     * compression or expansion due to movement of its end nodes. Computes and
     * stores: s1 scalar |(CTRL1 - P1)|/|(P2-P1)| s2 scalar |(CTRL2 - P2)|/|(P1-P2)|
     * angle1 angle between (CTRL1 - P1) and (P2-P1) angle2 angle between (CTRL2 -
     * P2) and (P1-P2) In case of self-loop stores angles from UNIT_VERTICAL to
     * (CTRL2 - P2) and (CTRL1 - P1) and scalars are simply the lengths of (CTRL2 -
     * P2) and (CTRL1 - P1).
     */
    protected void updateAnglesAndScalars() {

        // IDEA should there be constraints on the angle to control point
        // e.g. abs(angle between base line and tangent) <= PI/2?
        Point2D.Float p1p2 = Geometry.subtract(curve.getP2(), curve.getP1());
        Point2D.Float p2p1 = Geometry.subtract(curve.getP1(), curve.getP2());
        Point2D.Float p1c1 = Geometry.subtract(curve.getCtrlP1(), curve.getP1());
        Point2D.Float p2c2 = Geometry.subtract(curve.getCtrlP2(), curve.getP2());

        // if(selfLoop){
        // s1 = Geometry.norm(p1c1);
        // s2 = Geometry.norm(p2c2);
        // angle1 = Geometry.angleFrom(UNIT_VERTICAL, p1c1);
        // angle2 = Geometry.angleFrom(UNIT_VERTICAL, p2c2);
        // }else{
        double n = Geometry.norm(p1p2);

        if (n != 0) {
            s1 = Geometry.norm(p1c1) / n;
            s2 = Geometry.norm(p2c2) / n;
            angle1 = Geometry.angleFrom(p1p2, p1c1);
            angle2 = Geometry.angleFrom(p2p1, p2c2);
        } else
        // reflexive edge
        {
            s1 = Geometry.norm(p1c1); // arbitrary value
            s2 = s1;
            angle1 = -Geometry.angleFrom(p1c1, p2c2) / 2; // angle from edge
            // axis
            angle2 = -angle1;
        }
        // }

        // DEBUG
        assert (!Double.isNaN(angle1));
        assert (!Double.isNaN(angle2));
    }

    /**
     * Sets the control point with the given index to <code>point</code>.
     * Precondition: <code>index</code> is a valid index i.e. P1, P2, CTRL1, CTRL2.
     * Precondition: point != null
     * 
     * @param point the value of the control point
     * @param index the index of the control point to be set
     */
    public void setPoint(Point2D point, int index) {

        float x = (float) point.getX();
        float y = (float) point.getY();

        switch (index) {
        case P1:
            curve.x1 = x;
            curve.y1 = y;
            break;
        case P2:
            curve.x2 = x;
            curve.y2 = y;
            break;
        case CTRL1:
            curve.ctrlx1 = x;
            curve.ctrly1 = y;
            break;
        case CTRL2:
            curve.ctrlx2 = x;
            curve.ctrly2 = y;
            break;
        default:
            throw new IllegalArgumentException("Invalid control point index: " + index);
        }
        updateAnglesAndScalars();
        setDirty(true);
    }

    /**
     * Returns the curve representing the edge.
     * 
     * @return the curve representing the edge
     */
    public CubicParamCurve2D getCurve() {
        return curve;
    }

    /**
     * Sets the curve representing the edge.
     * 
     * @param cubicCurve the curve to be set
     */
    protected void setCurve(CubicParamCurve2D cubicCurve) {
        this.curve = cubicCurve;
        updateAnglesAndScalars();
        setDirty(true);
    }

    /**
     * Sets the curve representing the edge.
     * 
     * @param cubicCurve the curve to be set
     */
    public void setCurve(CubicCurve2D.Float cubicCurve) {

        this.curve = new CubicParamCurve2D(cubicCurve.x1, cubicCurve.y1, cubicCurve.ctrlx1, cubicCurve.ctrly1,
                cubicCurve.ctrlx2, cubicCurve.ctrly2, cubicCurve.x2, cubicCurve.y2);
        updateAnglesAndScalars();
        setDirty(true);
    }

    /**
     * Returns the visible portion of the curve i.e. the part that is outside the
     * boundaries of both source and target nodes; null if no such segment exists.
     * 
     * @return the portion of the curve that is external to both source and target
     *         nodes, null if no such segment exists
     */
    public CubicParamCurve2D getVisibleCurve() {
        // TODO check sourceT and targetT

        Node s = edge.getSourceNode();
        Node t = edge.getTargetNode();
        if (s.intersects(curve.getPointAt(targetT)) || (t != null && t.intersects(curve.getPointAt(sourceT)))) {
            return null;
        }

        return curve.getSegment(sourceT, targetT);
    }

    public Line2D getControllableMarker() {
        CubicParamCurve2D curve = getVisibleCurve();
        if (curve == null) {
            return null;
        }
        CubicParamCurve2D part = new CubicParamCurve2D();
        curve.subdivide(part, null, 0.25f);
        Point2D.Float vector = Geometry.unit(new Point2D.Float(part.ctrlx2 - part.x2, part.ctrly2 - part.y2));
        vector = Geometry.rotate(vector, Math.PI / 2);
        vector = Geometry.scale(vector, 5);
        return new Line2D.Float(part.x2 + vector.x, part.y2 + vector.y, part.x2 - vector.x, part.y2 - vector.y);
    }

    /**
     * Increase the arc on this edge layout. Increases the angle of the tangents to
     * the curve, clockwise around circumference of source node.
     */
    protected void arcMore() {
        arcMore(true);
    }

    /**
     * Increase the arc on this edge layout. Increases the angle of the tangents to
     * the curve, clockwise around circumference of source node if
     * <code>clockwise</code>, otherwise counter-clockwise.
     * 
     * @param clockwise
     */
    protected void arcMore(boolean clockwise) {

        if (clockwise) { // swap angles
            double temp = angle1;
            angle1 = angle2;
            angle2 = temp;
        }

        if (Math.abs(angle1) < EPSILON) {
            angle1 = DEFAULT_CONTROL_HANDLE_ANGLE;
            s1 = DEFAULT_CONTROL_HANDLE_SCALAR;
        } else {
            if (angle1 > 0) {
                angle1 += DEFAULT_CONTROL_HANDLE_ANGLE / 2;
            } else {
                angle1 -= DEFAULT_CONTROL_HANDLE_ANGLE / 2;
            }
            s1 *= 1.2;
        }

        if (Math.abs(angle2) < EPSILON) {
            angle2 = -DEFAULT_CONTROL_HANDLE_ANGLE;
            s2 = DEFAULT_CONTROL_HANDLE_SCALAR;
        } else {
            if (angle2 < 0) {
                angle2 -= DEFAULT_CONTROL_HANDLE_ANGLE / 2;
            } else {
                angle2 += DEFAULT_CONTROL_HANDLE_ANGLE / 2;
            }
            s2 *= 1.2;
        }

        if (clockwise) { // swap angles back
            double temp = angle1;
            angle1 = angle2;
            angle2 = temp;
        }
        computeCurve();
    }

    /**
     * Decreases angles of tangents to curve by DEFAULT_CONTROL_HANDLE_ANGLE / 2 and
     * tangent length by 20%. If angle < DEFAULT_CONTROL_HANDLE_ANGLE / 2, set it to
     * 0 (i.e. flatten the curve).
     */
    protected void arcLess() {
        if (Math.abs(angle1) < DEFAULT_CONTROL_HANDLE_ANGLE / 2) { // EPSILON){
            angle1 = 0;
            s1 = DEFAULT_CONTROL_HANDLE_SCALAR;
        } else {
            if (angle1 > 0) {
                angle1 -= DEFAULT_CONTROL_HANDLE_ANGLE / 2;
            } else {
                angle1 += DEFAULT_CONTROL_HANDLE_ANGLE / 2;
            }
            s1 *= 0.8;
        }

        if (Math.abs(angle2) < DEFAULT_CONTROL_HANDLE_ANGLE / 2) { // EPSILON){
            angle2 = 0;
            s2 = DEFAULT_CONTROL_HANDLE_SCALAR;
        } else {
            if (angle2 < 0) {
                angle2 += DEFAULT_CONTROL_HANDLE_ANGLE / 2;
            } else {
                angle2 -= DEFAULT_CONTROL_HANDLE_ANGLE / 2;
            }
            s2 *= 0.8;
        }
        computeCurve();
    }

    /**
     * Straightens the curve representing the edge to a straight line.
     */
    protected void straighten() {
        angle1 = 0;
        s1 = DEFAULT_CONTROL_HANDLE_SCALAR;
        angle2 = 0;
        s2 = DEFAULT_CONTROL_HANDLE_SCALAR;
        computeCurve();
    }

    /**
     * If this edge is not straight, make it have a symmetrical appearance. Make the
     * two vectors - from P1 to CTRL1 and from P2 to CTRL2, be of the same length
     * and have the same angle. So the edge will look it has a symmetrical curve.
     * There are two cases: The 2 control points are on the same side of the curve
     * (a curve with the form of a bow); and the 2 control points are on different
     * sides of the edge (a curve like a wave). In one of the cases, theangles of
     * the vectors should be A=B, in the other A=-B.
     */
    protected void symmetrize() {

        // translate all points to 0,0
        Point2D.Float[] points = new Point2D.Float[4];
        points[0] = Geometry.translate(curve.getP1(), -curve.getX1(), -curve.getY1());
        points[1] = Geometry.translate(curve.getCtrlP1(), -curve.getX1(), -curve.getY1());
        points[2] = Geometry.translate(curve.getCtrlP2(), -curve.getX1(), -curve.getY1());
        points[3] = Geometry.translate(curve.getP2(), -curve.getX1(), -curve.getY1());

        // rotate all points so edge has angle 0
        float edgeAngle = (float) Math.atan(Geometry.slope(curve.getP1(), curve.getP2()));
        points[0] = Geometry.rotate(points[0], -edgeAngle);
        points[1] = Geometry.rotate(points[1], -edgeAngle);
        points[2] = Geometry.rotate(points[2], -edgeAngle);
        points[3] = Geometry.rotate(points[3], -edgeAngle);

        // compute quadrants for the control points
        double quadrantFix1 = (points[0].x - points[1].x > 0) ? Math.PI : 0;
        double quadrantFix2 = (points[2].x - points[3].x > 0) ? Math.PI : 0;

        float a1 = (float) Math.atan(Geometry.slope(points[0], points[1]));
        float a2 = (float) Math.atan(Geometry.slope(points[3], points[2]));
        float angle = (Math.abs(a1) + Math.abs(a2)) / 2F;
        float distance = (float) (points[0].distance(points[1]) + points[2].distance(points[3])) / 2F;

        points[1] = Geometry.rotate(new Point2D.Float(distance, 0),
                (angle * (a1 == 0 ? 1 : Math.signum(a1)) + quadrantFix1));
        points[2] = Geometry.rotate(new Point2D.Float(distance, 0),
                (angle * (a2 == 0 ? 1 : Math.signum(a2)) + quadrantFix2 + Math.PI));
        points[2].x += points[3].x;
        points[2].y += points[3].y;

        // rotate and translate edge back to original location/angle
        points[1] = Geometry.rotate(points[1], edgeAngle);
        points[2] = Geometry.rotate(points[2], edgeAngle);
        points[0] = new Point2D.Float((float) curve.getX1(), (float) curve.getY1());
        points[1] = Geometry.translate(points[1], curve.getX1(), curve.getY1());
        points[2] = Geometry.translate(points[2], curve.getX1(), curve.getY1());
        points[3] = new Point2D.Float((float) curve.getX2(), (float) curve.getY2());

        curve.setCurve(points, 0);
        updateAnglesAndScalars();
        setDirty(true);
    }

    /**
     * Indicates whether an edge can be rigidly translated with both of its nodes or
     * must be recomputed. Default value is false. NOT YET USED
     */
    private boolean rigidTranslation = false;

    protected boolean isRigidTranslation() {
        return rigidTranslation;
    }

    protected void setRigidTranslation(boolean rigid) {
        this.rigidTranslation = rigid;
    }

    /**
     * Set this layout to be the reflection of <code>other</code> where reflection
     * is about the line between source and target nodes.
     * 
     * @param other the other layout whose reflection this layout will take
     */
    public void setToReflectionOf(BezierLayout other) {
        if (other.isStraight()) {
            return;
        }

        if (this.edge.getSourceNode().equals(other.edge.getSourceNode())) {
            this.angle1 = other.angle1 * -1;
            this.angle2 = other.angle2 * -1;
        } else { // heads to toes
            this.angle1 = other.angle1;
            this.angle2 = other.angle2;
        }
        this.s1 = other.s1;
        this.s2 = other.s2;
        this.computeCurve();
    }

    /**
     * Check to see if this layout is the reflection of <code>other</code>, about
     * the line between source and target nodes.
     * 
     * @param other the other layout to test against
     * @return true if <code>other</code> is a reflection of <code>this</code>;
     *         false otherwise.
     */
    public boolean isReflectionOf(BezierLayout other) {
        if (this.edge.getSourceNode().equals(other.edge.getSourceNode())) {
            return ((this.angle1 == other.angle1 * -1) && (this.angle2 == other.angle2 * -1));
        } else {
            return ((this.angle1 == other.angle1) && (this.angle2 == other.angle2));
        }
    }

    /**
     * Returns the parameter in [0,1] of the point on a parametric cubic curve where
     * my edge intersects with with the boundary of its source node.
     * 
     * @return sourceT the parameter of point on parametric cubic curve where my
     *         edge intersects with its source node
     */
    protected float getSourceT() {
        return sourceT;
    }

    /**
     * Sets the parameter in [0,1] of the point on a parametric cubic curve where my
     * edge intersects with with the boundary of its source node. Precondition: 0 <=
     * sourceT <=1
     * 
     * @param sourceT the parameter of point on parametric cubic curve where my edge
     *                intersects with its source node
     */
    protected void setSourceT(float sourceT) {
        // assert(0 <= sourceT && sourceT < targetT);
        // if(! (0 <= sourceT && sourceT < targetT ) ) System.err.println("s=" +
        // sourceT + ", t=" + targetT);
        this.sourceT = sourceT;
    }

    /**
     * Returns the parameter in [0,1] of the point on a parametric cubic curve where
     * my edge intersects with with the boundary of its target node.
     * 
     * @return the parameter of point on parametric cubic curve where my edge
     *         intersects with its target node
     */
    protected float getTargetT() {
        return targetT;
    }

    /**
     * Sets the parameter in [0,1] of the point on a parametric cubic curve where my
     * edge intersects with the boundary of its target node. Precondition: 0 <=
     * targetT <=1
     * 
     * @param targetT the parameter of point on parametric cubic curve where my edge
     *                intersects with its target node
     */
    protected void setTargetT(float targetT) {
        // assert(sourceT < targetT && targetT <=1);
        // if(! (sourceT < targetT && targetT <=1) ) System.err.println("s=" +
        // sourceT + ", t=" + targetT);
        this.targetT = targetT;
    }

    /**
     * Returns the point where my edge intersects with the boundary of its source
     * node
     * 
     * @return point where my edge intersects with its source node
     */
    public Point2D.Float getSourceEndPoint() {
        return curve.getPointAt(sourceT);// sourceEndPoint;
    }

    /**
     * Returns the point where my edge intersects with the boundary of its target
     * node
     * 
     * @return point where my edge intersects with its target node
     */
    public Point2D.Float getTargetEndPoint() {
        return curve.getPointAt(targetT); // targetEndPoint;
    }

    // ///////////////////////////////////////////////////////////////////////
    /*
     * TODO REMOVE The following methods manage the set of event names to appear on
     * the edge label. This should be between the edge and its label; this class
     * should stay out of it.
     */
    public ArrayList<String> getEventNames() {
        return eventNames;
    }

    public void setEventNames(ArrayList<String> eventNames) {
        this.eventNames = eventNames;
        updateTextFromEventNames();
        setDirty(true);
    }

    public void addEventName(String symbol) {
        eventNames.add(symbol);
        updateTextFromEventNames();
        setDirty(true);
    }

    public void removeEventName(String symbol) {
        eventNames.remove(symbol);
        updateTextFromEventNames();
        setDirty(true);
    }

    /**
     * KLUGE until all changes to text go directly from Edge to Label.
     */
    @Override
    public String getText() {
        updateTextFromEventNames();
        return super.getText();
    }

    /**
     * Sets text to comma-delimited string of event symbols.
     */
    private void updateTextFromEventNames() {
        // Concat label from associated event[s]
        String s = "";

        if (eventNames != null) {
            Iterator<String> iter = eventNames.iterator();
            while (iter.hasNext()) {
                s += (String) iter.next();
                s += ", ";
            }
            s = s.trim();
            if (s.length() > 0) {
                s = s.substring(0, s.length() - 1);
            }
        }
        setText(s);
    }

    // /////////////////////////////////////////////////////////////////////////
    /**
     * Explicitly saves its own fields
     * 
     * @serialData Store own serializable fields
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeBoolean(rigidTranslation);
        out.writeLong(group);
        out.writeFloat(curve.x1);
        out.writeFloat(curve.y1);
        out.writeFloat(curve.ctrlx1);
        out.writeFloat(curve.ctrly1);
        out.writeFloat(curve.ctrlx2);
        out.writeFloat(curve.ctrly2);
        out.writeFloat(curve.x2);
        out.writeFloat(curve.y2);
        out.writeFloat(sourceT);
        out.writeFloat(targetT);
        out.writeDouble(angle1);
        out.writeDouble(angle2);
        out.writeDouble(s1);
        out.writeDouble(s2);
        out.writeInt(eventNames.size());
        for (int i = 0; i < eventNames.size(); ++i) {
            out.writeObject(eventNames.get(i));
        }
        // out.writeObject(eventNames);
    }

    /**
     * Restores its own fields by calling defaultReadObject and then explicitly
     * restores the fields of its supertype.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        rigidTranslation = in.readBoolean();
        group = in.readLong();
        curve = new CubicParamCurve2D(in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat(),
                in.readFloat(), in.readFloat(), in.readFloat());
        sourceT = in.readFloat();
        targetT = in.readFloat();
        angle1 = in.readDouble();
        angle2 = in.readDouble();
        s1 = in.readDouble();
        s2 = in.readDouble();
        int eventCount = in.readInt();
        eventNames = new ArrayList<String>(eventCount);
        for (int i = 0; i < eventCount; ++i) {
            eventNames.add((String) in.readObject());
        }
        // eventNames = (ArrayList<String>)in.readObject();
    }

    @Override
    public BezierLayout clone() {
        BezierLayout layout = null;
        try {
            ByteArrayOutputStream fo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(fo);
            so.writeObject(this);
            so.flush();
            so.close();
            ByteArrayInputStream is = new ByteArrayInputStream(fo.toByteArray());
            ObjectInputStream objectIS = new ObjectInputStream(is);
            layout = (BezierLayout) objectIS.readObject();
            is.close();
            layout.setEdge(edge);
        } catch (Exception e) {
            throw new RuntimeException("Cloning of BezierEdge failed.");
        }
        return layout;
    }
}
