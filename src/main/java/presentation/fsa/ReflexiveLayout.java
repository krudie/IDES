package presentation.fsa;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import presentation.CubicParamCurve2D;
import presentation.Geometry;

/**
 * Same data as BezierLayout (control points and label offset vector) but
 * different algorithms and handlers specific to rendering a self-loop.
 * 
 * @author Helen Bretzke
 */
public class ReflexiveLayout extends BezierLayout implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1877381622274118088L;

    /* the edge to be laid out */
    private ReflexiveEdge edge;

    /*
     * Index of midpoint used as handle to modify the curve position.
     */
    public static final int HANDLEPOINT = 4;

    // /////////////////////////////////////////////////////////////////////////
    /**
     * Explicitly saves its own fields
     * 
     * @serialData Store own serializable fields
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeDouble(axis.getX());
        out.writeDouble(axis.getY());

        // out.writeDouble(midpoint.getX());
        // out.writeDouble(midpoint.getY());

        out.writeFloat(minAxisLength);
    }

    /**
     * Restores its own fields by calling defaultReadObject and then explicitly
     * restores the fields of its supertype.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        axis = new Point2D.Double(in.readDouble(), in.readDouble());
        // midpoint = new Point2D.Double(in.readDouble(), in.readDouble());
        minAxisLength = in.readFloat();
    }

    @Override
    public ReflexiveLayout clone() {
        ReflexiveLayout layout = (ReflexiveLayout) super.clone();
        layout.setEdge(edge);
        return layout;
    }

    /**
     * The minimum length of the axis vector from the centre of the node to the
     * midpoint of this edge. FIXME the axis doesn't reach the *computed* midpoint.
     */
    private float minAxisLength;

    // NOTE no need to store either of these variables here.
    // vector from centre of source/target node to this point
    // is the axis around which a symmetrical arc is drawn.
    private Point2D axis;

    // private Point2D midpoint;

    // //////////////////////////////////////////////////////

    /**
     * Default angle from centre axis vector to the tangents of the bezier curve
     * NOTE this looks ugly but minimizes the problem of control (mid)point drift
     */
    public static final double DEFAULT_ANGLE = Math.PI / 4;

    /**
     * Default value to scale the centre axis vector to the length of the tangents
     * of the bezier curve
     */
    public static final float DEFAULT_SCALAR = 2f;

    /**
     * Layout for a reflexive edge with vertical axis vector from centre of node to
     * midpoint of bezier curve given by <code>bLayout</code>.
     */
    public ReflexiveLayout(Node source, ReflexiveEdge edge, BezierLayout bLayout) {
        minAxisLength = 2 * ((CircleNodeLayout) source.getLayout()).getRadius();
        setEdge(edge);
        if (bLayout != null) {
            setCurve(bLayout.getCurve());
            setEventNames(bLayout.getEventNames());
            setLabelOffset(bLayout.getLabelOffset());
            setGroup(bLayout.getGroup());
        } else {
            axis = computeBestDirection(source);
            axis = Geometry.scale(axis, minAxisLength / Geometry.norm(axis));
            setCurve(curveFromAxis());
        }
        axis = axisFromCurve();
        // Point2D temp = Geometry.midpoint(bLayout.getCurve());
        // setPoint(new Point2D.Float((float)temp.getX(), (float)temp.getY()),
        // MIDPOINT);
        // setCurve(bLayout.getCurve());
        // initializeShape();
    }

    /**
     * @param source
     * @param edge
     */
    public ReflexiveLayout(Node source, ReflexiveEdge edge) {
        this(source, edge, null);
        // minAxisLength = source.bounds().height;
        // setEdge(edge);
        // initializeShape();
    }

    /**
     * @return the midpoint of the curve.
     */
    public Point2D getHandleLocation() {
        Point2D.Float p = new Point2D.Float();
        if (getEdge() != null) {
            p = getEdge().getSourceNode().getLocation();
        }
        return Geometry.add(axis, p);
    }

    // /**
    // * FIXME Problem: using fixed scalars and angles causes midpoint of
    // computed
    // * curve to drift away from midpoint set by user. If the curve has already
    // * been loaded from a file, compute the correct axis, angles and scalars
    // to
    // * correctly reproduce the curve.
    // */
    // public void initializeShape()
    // {
    // angle1 = -DEFAULT_ANGLE;
    // angle2 = DEFAULT_ANGLE;
    // s1 = DEFAULT_SCALAR;
    // s2 = s1;
    // if (getEdge() == null)
    // {
    // return;
    // }
    // Float centrePoint = getEdge().getSourceNode().getLocation();
    // // setPoint(centrePoint, P1);
    // // setPoint(centrePoint, P2);
    // // Point2D.Float v1 = Geometry.rotate(axis, angle1);
    // // Point2D.Float v2 = Geometry.rotate(axis, angle2);
    // // setPoint(Geometry.add(centrePoint, Geometry.scale(v1, s1)), CTRL1);
    // // setPoint(Geometry.add(centrePoint, Geometry.scale(v2, s2)), CTRL2);
    // System.err.println(curve);
    // if (midpoint == null)
    // {
    // setPoint(Geometry.add(centrePoint, Geometry
    // .scale(new Point2D.Float(0, -1), minAxisLength)), MIDPOINT);
    // }
    // }

    protected Point2D axisFromCurve() {
        return Geometry.rotate(Geometry.scale(Geometry.subtract(curve.getCtrlP1(), curve.getP1()), 1 / DEFAULT_SCALAR),
                DEFAULT_ANGLE);
    }

    protected CubicParamCurve2D curveFromAxis() {
        Point2D.Float p = new Point2D.Float();
        if (getEdge() != null) {
            p = getEdge().getSourceNode().getLocation();
        }
        Point2D.Float ctrl1 = Geometry.add(Geometry.rotate(Geometry.scale(axis, DEFAULT_SCALAR), -DEFAULT_ANGLE), p);
        Point2D.Float ctrl2 = Geometry.add(Geometry.rotate(Geometry.scale(axis, DEFAULT_SCALAR), DEFAULT_ANGLE), p);
        return new CubicParamCurve2D(p.x, p.y, ctrl1.x, ctrl1.y, ctrl2.x, ctrl2.y, p.x, p.y);
    }

    /**
     * @return the portion of the curve that is external to the node, null if no
     *         such segment exists
     */
    @Override
    public CubicParamCurve2D getVisibleCurve() {
        // FIXME check for NaN here or in CubicCurve2Dex class
        return curve.getSegment(sourceT, targetT);
    }

    /**
     * Computes a symmetric reflexive bezier curve based on location of node, and
     * angle of tangent vectors (to bezier curve) from centre axis vector.
     */
    @Override
    public void computeCurve() {
        if (getEdge() == null) {
            return;
        }
        // Node sourceNode = edge.getSourceNode();
        // Point2D nodeLocation = sourceNode.getLocation();

        // In case the node change its size, recalculate the midpoint position
        // by increasing value of the minAxisLength.
        float currentNodeRadius = ((CircleNodeLayout) getEdge().getSourceNode().getLayout()).getRadius();
        // System.err.println(""+currentNodeRadius+" "+edge.lastNodeRadius);
        float factor = currentNodeRadius / edge.lastNodeRadius;
        if (factor != 1) {
            minAxisLength = getEdge().getSourceNode().bounds().height;
            axis = Geometry.scale(axis, factor);
        }
        edge.lastNodeRadius = currentNodeRadius;

        setCurve(curveFromAxis());
        // // Setting midpoint according to the axis:
        // midpoint.setLocation(nodeLocation.getX() + axis.getX(), nodeLocation
        // .getY()
        // + axis.getY());
        // setPoint(midpoint, MIDPOINT);
        // // Setting the Bezier control points based on the axis:
        // Point2D.Float v1 = Geometry.rotate(axis, angle1);
        // Point2D.Float v2 = Geometry.rotate(axis, angle2);
        // // Setting center of the edge
        // setPoint(nodeLocation, P1);
        // setPoint(nodeLocation, P2);
        // // Setting the control points
        // setPoint(Geometry.add(nodeLocation, Geometry.scale(v1, s1)), CTRL1);
        // setPoint(Geometry.add(nodeLocation, Geometry.scale(v2, s2)), CTRL2);
        setDirty(true);
    }

    /**
     * Set the midpoint for a symmetric, reflexive bezier edge. Constraint: if
     * midpoint is inside node, set to minimum distance from node border.
     * 
     * @param point
     * @param index
     */
    @Override
    public void setPoint(Point2D point, int index) {
        // float x = (float)point.getX();
        // float y = (float)point.getY();
        // switch (index)
        // {
        // case MIDPOINT:
        // Float centrePoint;
        // // System.out.println(getEdge());
        //
        // centrePoint = getEdge().getSourceNode().getLocation();
        // axis = Geometry.subtract(point, centrePoint);
        // double norm = Geometry.norm(axis);
        // if (norm < minAxisLength)
        // {
        // // snap to arc minimum distance from border of node
        // axis = Geometry.scale(axis, minAxisLength / norm);
        // midpoint = Geometry.add(centrePoint, axis);
        // }
        // else
        // {
        // midpoint = new Point2D.Float(x, y);
        // }
        // // computeCurve();
        // // TODO set midpoint after computing curve...
        // // midpoint = Geometry.midpoint(getCurve());
        // System.err.println("midpoint "+midpoint+" "+axis+" "+getCurve());
        // // try{throw new RuntimeException();}catch(Exception
        // e){e.printStackTrace();}
        // setLocation((float)midpoint.getX(), (float)midpoint.getY());
        // // setDirty(true);
        // break;
        // case P1:
        // curve.x1 = x;
        // curve.y1 = y;
        // break;
        // case P2:
        // curve.x2 = x;
        // curve.y2 = y;
        // break;
        // case CTRL1:
        // curve.ctrlx1 = x;
        // curve.ctrly1 = y;
        // break;
        // case CTRL2:
        // curve.ctrlx2 = x;
        // curve.ctrly2 = y;
        // break;
        // default:
        // throw new IllegalArgumentException("Invalid control point index: "
        // + index);
        // }
        //
        if (index == HANDLEPOINT) {
            Point2D.Float centrePoint = new Point2D.Float();
            if (getEdge() != null) {
                centrePoint = getEdge().getSourceNode().getLocation();
            }
            axis = Geometry.subtract(point, centrePoint);
            double norm = Geometry.norm(axis);
            if (norm < minAxisLength) {
                // snap to arc minimum distance from border of node
                axis = Geometry.scale(axis, minAxisLength / norm);
            }
            setCurve(curveFromAxis());
            setDirty(true);
        }
    }

    /**
     * author Christian
     */
    public Point2D.Float computeBestDirection(Node target) {

        Iterator<Edge> adjEdges = target.adjacentEdges();

        // Check the angles of the existent edges
        ArrayList<java.lang.Float> angles = new ArrayList<java.lang.Float>();
        Point2D.Float currentDirVector = new Point2D.Float();
        int number = 0;

        while (adjEdges.hasNext()) {
            Edge edge = adjEdges.next();
            if (edge.getTargetNode().equals(edge.getSourceNode())) {
                currentDirVector = Geometry.subtract(target.getLocation(), ((ReflexiveEdge) edge).getHandleLocation());
                float currentAngle = (float) Geometry.angleFrom(currentDirVector, new Point2D.Float(-1, 0));
                angles.add(currentAngle);
                angles.add(currentAngle + 0.5f * (float) angle1);
                angles.add(currentAngle + 0.5f * (float) angle2);
            } else {
                currentDirVector = Geometry.subtract(target.getLocation(), edge.getTargetEndPoint());
                float currentAngle = (float) Geometry.angleFrom(currentDirVector, new Point2D.Float(-1, 0));
                angles.add(currentAngle);
            }

            number++;
        }
        for (int i = 0; i < angles.size(); i++) {
            if (angles.get(i) < 0) {
                angles.set(i, (float) (2 * Math.PI) + angles.get(i));
            }
        }
        Collections.sort(angles);

        // Try to fit the selfloop at its "favorite" position: 90 degrees
        boolean canFit = true;
        // Scan from limMin->limMax and check whether all this spaces are
        // avaliables.
        float limMin = (float) Math.toRadians(50);
        float limMax = (float) Math.toRadians(130);
        for (int i = 0; i < angles.size(); i++) {
            float angle = angles.get(i);
            if (angle >= limMin & angle <= limMax) {
                canFit = false;
            }
        }
        if (canFit) {
            return Geometry.rotate(new Point2D.Float(1, 0), Math.toRadians(-90));
        }

        // If the prefered positions are not available, look for the most
        // confortable
        // position.
        angles.add((float) (angles.get(0) + 2 * Math.PI));
        float maxAngle = -1;
        int bestIndex = 0;
        for (int i = 0; i < angles.size() - 1; i++) {
            float currentAngle = angles.get(i + 1) - angles.get(i);
            if (currentAngle > maxAngle) {
                maxAngle = currentAngle;
                bestIndex = i;
            }
        }
        float rotAngle = maxAngle / 2 + angles.get(bestIndex);
        return Geometry.rotate(new Point2D.Float(1, 0), -rotAngle);
    }

    public void resetPosition(Node node) {
        axis = Geometry.scale(computeBestDirection(node), Geometry.norm(axis));
    }

    /**
     * Sets the edge to <code>edge</code>.
     * 
     * @param edge the edge to be set
     */
    public void setEdge(ReflexiveEdge edge) {
        this.edge = edge;
        setDirty(true);
    }

    @Override
    public ReflexiveEdge getEdge() {
        return edge;
    }

    public Point2D getAxis() {
        return axis;
    }

    public void setAxis(Point2D newAxis) {
        axis.setLocation(newAxis.getX(), newAxis.getY());
        setCurve(curveFromAxis());
        setDirty(true);
    }

    /**
     * Returns true iff <code>o</code> is an instance of ReflexiveLayout and this
     * layout has the same curve and label offset as <code>o</code>.
     * 
     * @param o the other layout to be compared
     * @return true iff <code>o</code> is an instance of ReflexiveLayout and this
     *         layout has the same curve and label offset as <code>o</code> .
     */
    /*
     * public boolean equals(Object o) { Won't work since need to use this to
     * compare BezierLayout instances read from file with ReflexiveLayouts in
     * memory. try{ ReflexiveLayout other = (ReflexiveLayout)o; return
     * other.curve.equals(this.curve) &&
     * other.getLabelOffset().equals(this.getLabelOffset());
     * }catch(ClassCastException cce){ return false; } }
     */

} // end Layout
