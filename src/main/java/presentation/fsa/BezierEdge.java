package presentation.fsa;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.Iterator;
import java.util.Set;

import ides.api.model.fsa.FSATransition;
import ides.api.model.supeventset.SupervisoryEvent;
import io.fsa.ver2_1.GraphExporter;
import presentation.CubicParamCurve2D;
import presentation.Geometry;
import presentation.GraphicalLayout;
import util.BentoBox;

/**
 * The graphical representation of a transition in a finite state automaton.
 * This edge is represented by a cubic bezier curve which can be reshaped by its
 * control points.
 * 
 * @author Helen Bretzke
 * @author Sarah-Jane Whittaker
 * @author Lenko Grigorov
 */
public class BezierEdge extends Edge {

    protected static final float INTERSECT_EPSILON = 9;

    /** Index of first end point */
    public static final int P1 = 0;

    /** Index of first control point */
    public static final int CTRL1 = 1;

    /** Index of second control point */
    public static final int CTRL2 = 2;

    /** Index of second end point */
    public static final int P2 = 3;

    /** the arrow head to be rendered at the second end point */
    private ArrowHead arrowHead;

    // TODO Refactor these constructors.

    /**
     * Creates a new directed Bezier edge emanating from <code>source</code>, with
     * null target node, no transition, and having the given layout data.
     * Precondition: layout is not null
     * 
     * @param layout the layout data
     * @param source the source node
     */
    public BezierEdge(BezierLayout layout, CircleNode source) {
        super(source);
        setLayout(layout);
        setHandler(new BezierHandler(this));
        arrowHead = new ArrowHead();
        setNeedsRefresh(true);
    }

    /**
     * Creates a new directed Bezier edge from <code>source</code> to
     * <code>target</code> with the given layout data and representing the given
     * transition. Precondition: layout is not null
     * 
     * @param layout the layout data
     * @param source the source node
     * @param target the target node
     * @param t      the transition to represent
     */
    public BezierEdge(BezierLayout layout, Node source, Node target, FSATransition t) {
        super(source, target, t);
        setLayout(layout);
        setHandler(new BezierHandler(this));
        arrowHead = new ArrowHead();
        setNeedsRefresh(true);
    }

    /**
     * Creates a new directed Bezier edge from <code>source</code> to
     * <code>target</code> with default layout and no transition.
     * 
     * @param source the source node
     * @param target the target node
     */
    public BezierEdge(Node source, Node target) {
        super(source, target);
        arrowHead = new ArrowHead();
        setNeedsRefresh(true);
    }

    /**
     * Renders this edge in the given graphics context.
     * 
     * @param g the graphics context.
     */
    @Override
    public void draw(Graphics g) {
        if (!isVisible()) {
            return;
        }

        // make sure the appearance is in sync with underlying data
        if (needsRefresh() || getBezierLayout().isDirty()) {
            refresh();
            getBezierLayout().setDirty(false);
        }

        Graphics2D g2d = (Graphics2D) g;

        // if either my source or target node is highlighted
        // then I am also hightlighted.
        if (highlighted || getSourceNode().isHighlighted()
                || getTargetNode() != null && getTargetNode().isHighlighted()) {
            setHighlighted(true);
            g2d.setColor(getLayout().getHighlightColor());
        } else {
            g2d.setColor(getLayout().getColor());
        }

        if (isSelected()) {
            g2d.setColor(getLayout().getSelectionColor());
            getHandler().setVisible(true);
        } else {
            getHandler().setVisible(false);
        }

        if (hasUnobservableEvent()) {
            g2d.setStroke(GraphicalLayout.DASHED_STROKE);
        } else {
            g2d.setStroke(GraphicalLayout.WIDE_STROKE);
        }

        // TODO should stop drawing at base of arrowhead and at outside of node
        // boundaries.
        if (getBezierLayout().getEdge() == null) {
            getBezierLayout().setEdge(this);
        }
        CubicCurve2D curve = getBezierLayout().getVisibleCurve();
        if (curve != null) {
            g2d.draw(curve);
        }
        if (!hasUncontrollableEvent() && getBezierLayout().getControllableMarker() != null) {
            g2d.setStroke(GraphicalLayout.FINE_STROKE);
            g2d.draw(getBezierLayout().getControllableMarker());
        }

        // Compute the direction and location of the arrow head
        AffineTransform at = new AffineTransform();

        // FIXME Compute and *STORE?* the arrow layout (the direction vector
        // from base to tip of the arrow)
        // i.e. in BezierLayout class.
        // Make certain that it points the right direction when nodes are
        // touching or overlapping.
        Point2D.Float unitArrowDir = computeArrowDirection();

        arrowHead.reset();

        // If available, use point of intersection with target node boundary
        Point2D basePt;
        Point2D.Float tEndPt = getTargetEndPoint();
        if (tEndPt != null) {
            basePt = Geometry.add(tEndPt, Geometry.scale(unitArrowDir, -(ArrowHead.SHORT_HEAD_LENGTH + 2)));
        } else {
            basePt = Geometry.add(getBezierLayout().getCurve().getP2(),
                    Geometry.scale(unitArrowDir, -(ArrowHead.SHORT_HEAD_LENGTH + 2)));
        }
        at.setToTranslation(basePt.getX(), basePt.getY());
        g2d.transform(at);

        // rotate to align with end of curve
        double rho = Geometry.angleFrom(ArrowHead.axis, unitArrowDir);
        if (!Double.isNaN(rho)) {
            at.setToRotation(rho);
            g2d.transform(at);
            g2d.setStroke(GraphicalLayout.FINE_STROKE);
            g2d.draw(arrowHead);
            g2d.fill(arrowHead);
            at.setToRotation(-rho);
            g2d.transform(at);
        }
        at.setToTranslation(-basePt.getX(), -basePt.getY());
        g2d.transform(at);

        // draw label and handler
        super.draw(g);
    }

    /**
     * Updates the visualization of this curve, arrow and label from underlying
     * data.
     */
    @Override
    public void refresh() {
        super.refresh(); // refresh all children
        CubicCurve2D.Float curve = getBezierLayout().getCurve();
        // DEBUG //////////////////////////////
        assertAllPointsNumbers(curve);
        // /////////////////////////////////////

        // Compute points where curve intersects boundaries of source and target
        // nodes
        Point2D.Float sourceEndPt = getSourceEndPoint();
        if (sourceEndPt == null) {
            sourceEndPt = new Point2D.Float();
        }

        float sourceT = intersectionWithBoundary(getSourceNode().getShape(), sourceEndPt, SOURCE_NODE);
        ((BezierLayout) getLayout()).setSourceT(sourceT);

        if (getTargetNode() != null) {
            Point2D.Float targetEndPt = getTargetEndPoint();
            if (targetEndPt == null) {
                targetEndPt = new Point2D.Float();
            }

            float tTarget = intersectionWithBoundary(getTargetNode().getShape(), targetEndPt, TARGET_NODE);
            ((BezierLayout) getLayout()).setTargetT(tTarget);
        }
        ////////////////////////////////////////////////////////////////////////
        // /////////
        if (!isSelected()) {
            getHandler().setVisible(false);
            getHandler().refresh();
        }
        refreshLabelText();
        // Compute location of label: midpoint of curve plus offset vector
        CubicCurve2D.Float left = new CubicCurve2D.Float();
        curve.subdivide(left, new CubicCurve2D.Float());
        Point2D.Float midpoint = (Point2D.Float) left.getP2();
        this.setLocation(midpoint);
        Point2D.Float location = Geometry.add(new Point2D.Float((float) midpoint.getX(), (float) midpoint.getY()),
                getBezierLayout().getLabelOffset());
        getLabel().setLocation(location);
        getBezierLayout().setDirty(false);
        setNeedsRefresh(false);
    }

    /**
     * Sets the text of the label to a comma-delimited string of event symbols.
     */
    private void refreshLabelText() {
        // Concat label from associated event[s]
        String s = "";
        Iterator<FSATransition> iter = this.getTransitions();
        SupervisoryEvent event;
        while (iter.hasNext()) {
            event = iter.next().getEvent();
            if (event != null) {
                s += event.getSymbol();
                s += ", ";
            }
        }
        s = s.trim();
        if (s.length() > 0) {
            s = s.substring(0, s.length() - 1);
        }
        getLabel().setText(s);
    }

    /**
     * FIXME points wrong direction when tangent angle is close to +- PI. Computes
     * and returns a unit vector indicating the direction in which the arrowhead
     * should point.
     * 
     * @return unit direction vector for arrow head to point
     */
    private Point2D.Float computeArrowDirection() {
        Node target = getTargetNode();
        if (target == null) {
            BezierLayout bL = getBezierLayout();
            if (bL != null) {
                CubicCurve2D curve = bL.getCurve();
                if (curve != null) {
                    return Geometry.unit(Geometry.subtract(curve.getP2(), curve.getCtrlP2()));
                }
            }
            return null;
        } else {
            Shape s = getTargetNode().getShape();
            Rectangle box = s.getBounds();
            double delta = ArrowHead.SHORT_HEAD_LENGTH;
            Rectangle fat = new Rectangle((int) (box.x - delta / 2), (int) (box.y - delta / 2),
                    (int) (box.width + delta), (int) (box.height + delta));
            Point2D.Float p = new Point2D.Float();

            // TODO use return value; store as new endpoint in layout
            // Now result is stored in 'p'
            this.intersectionWithBoundary(fat, p, TARGET_NODE);

            return Geometry.unitDirectionVector(p, getTargetEndPoint());
        }
    }

    /***************************************************************************
     * Debugging
     */
    protected void assertAllPointsNumbers(CubicCurve2D.Float curve) {

        assert !Double.isNaN(curve.getCtrlX1()) : "cx1 is NaN";
        assert !Double.isNaN(curve.getCtrlX2()) : "cx2 is NaN";
        assert !Double.isNaN(curve.getCtrlY1()) : "cy1 is NaN";
        assert !Double.isNaN(curve.getCtrlY2()) : "cy2 is NaN";
        assert !Double.isNaN(curve.getX1()) : "x1 is NaN";
        assert !Double.isNaN(curve.getX2()) : "x2 is NaN";
        assert !Double.isNaN(curve.getY1()) : "y1 is NaN";
        assert !Double.isNaN(curve.getY2()) : "y2 is NaN";

    }

    /** ********************************************************** */

    /**
     * Returns whether p intersects with this edge. FIXME change to return true iff
     * intersects VISIBLE part of curve.
     * 
     * @return true iff p intersects with this edge.
     */
    @Override
    public boolean intersects(Point2D p) {

        boolean hit = false;
        boolean limitReached = false;
        CubicCurve2D curve = getBezierLayout().getVisibleCurve();

        if (curve == null) {
            return false;
        }

        do {
            // DEBUG
            // assertAllPointsNumbers(curve);

            CubicCurve2D.Float c1 = new CubicCurve2D.Float(), c2 = new CubicCurve2D.Float();
            curve.subdivide(c1, c2);
            if (c1.intersects(p.getX() - 4, p.getY() - 4, 8, 8)) {
                curve = c1;
                hit = true;
            } else if (c2.intersects(p.getX() - 4, p.getY() - 4, 8, 8)) {
                curve = c2;
                hit = true;
            } else {
                hit = false;
            }
            if (curve.getP1().distanceSq(curve.getP2()) < INTERSECT_EPSILON) {
                limitReached = true;
            }
        } while (hit && !limitReached);

        if (isSelected() && getHandler().isVisible()) {
            // expand the intersection point to an 8 by 8 rectangle
            return hit || arrowHead.intersects(p.getX() - 4, p.getY() - 4, 8, 8) || getLabel().intersects(p)
                    || getHandler().intersects(p);
        } else {
            // expand the intersection point to an 8 by 8 rectangle
            // boolean r = getLayout().getCubicCurve().intersects(p.getX() - 4,
            // p.getY() - 4, 8, 8);
            boolean a = arrowHead.contains(p);
            boolean l = getLabel().intersects(p);
            return hit || a || l;
        }
    }

    /**
     * Get point of visible curve.
     * 
     * @return end point 1
     */
    public Point2D.Float getP1() {
        return new Point2D.Float((float) getBezierLayout().getCurve().getX1(),
                (float) getBezierLayout().getCurve().getY1());
    }

    /**
     * Get point of visible curve.
     * 
     * @return end point 2
     */
    public Point2D.Float getP2() {
        return new Point2D.Float((float) getBezierLayout().getCurve().getX2(),
                (float) getBezierLayout().getCurve().getY2());
    }

    /**
     * Get point of visible curve.
     * 
     * @return control point 1 for the visible curve
     */
    public Point2D.Float getVisibleCTRL1() {
        return new Point2D.Float((float) getBezierLayout().getVisibleCurve().getCtrlX1(),
                (float) getBezierLayout().getVisibleCurve().getCtrlY1());
    }

    /**
     * Get point of complete curve.
     * 
     * @return control point 1
     */
    public Point2D.Float getCTRL1() {
        return new Point2D.Float((float) getBezierLayout().getCurve().getCtrlX1(),
                (float) getBezierLayout().getCurve().getCtrlY1());
    }

    /**
     * Get point of visible curve.
     * 
     * @return control point 2 for the visible curve
     */
    public Point2D.Float getVisibleCTRL2() {
        return new Point2D.Float((float) getBezierLayout().getVisibleCurve().getCtrlX2(),
                (float) getBezierLayout().getVisibleCurve().getCtrlY2());
    }

    /**
     * Get point of complete curve.
     * 
     * @return control point 2
     */
    public Point2D.Float getCTRL2() {
        return new Point2D.Float((float) getBezierLayout().getCurve().getCtrlX2(),
                (float) getBezierLayout().getCurve().getCtrlY2());
    }

    /**
     * Sets the graphical layout to <code>layout</code>. Precondition: layout !=
     * null
     * 
     * @param layout graphical layout data to be set
     */
    @Override
    public void setLayout(GraphicalLayout layout) {
        ((BezierLayout) layout).setEdge(this);
        super.setLayout(layout);
        // ??? //
        // computeEdge();
        setNeedsRefresh(true);
    }

    public BezierLayout getBezierLayout() {
        try {
            return (BezierLayout) super.getLayout();
        } catch (ClassCastException cce) {
            return null;
        }
    }

    @Override
    public void addTransition(FSATransition t) {
        super.addTransition(t);
        // TODO uncomment after MetaData class is disconnected
        // Event event = (Event) t.getEvent();
        // if(event != null){
        // addEventName(event.getSymbol());
        // }
    }

    public void removeTransition(FSATransition t) {
        super.removeTransition(t);
        // TODO uncomment after MetaData class is disconnected
        // Event event = (Event) t.getEvent();
        // if(event != null){
        // getBezierLayout().removeEventName(event.getSymbol());
        // }
    }

    /*
     * (non-Javadoc)
     * 
     * @see presentation.fsa.GraphElement#translate(float, float)
     */
    @Override
    public void translate(float x, float y) {
        BezierLayout l = getBezierLayout();
        CubicCurve2D.Float curve = l.getCurve();
        if (l.isRigidTranslation()) {
            // Translate the whole curve assuming that its
            // source and target nodes have been translated by the same
            // displacement.
            curve.setCurve(curve.getX1() + x, curve.getY1() + y, curve.getCtrlX1() + x, curve.getCtrlY1() + y,
                    curve.getCtrlX2() + x, curve.getCtrlY2() + y, curve.getX2(), curve.getY2() + y);

            // DEBUG ////////////////////
            assertAllPointsNumbers(curve);
            // ////////////////////////////

            l.setRigidTranslation(false);
            super.translate(x, y);

        } else { // reset the control points in the layout object

            if (getTargetNode() != null) // translation can occur in the
            // middle of drawing a new edge
            {
                l.computeCurve((CircleNodeLayout) getSourceNode().getLayout(),
                        (CircleNodeLayout) getTargetNode().getLayout());
            }

        }
    }

    /**
     * Returns a string that contains an encoding of this edge formatted for the
     * given export type.
     * 
     * @param selectionBox The area being selected or considered
     * @param exportType   The export format
     * @return String The string representation
     */
    @Override
    public String createExportString(Rectangle selectionBox, int exportType) {
        String exportString = "";
        Line2D controlLine = null;

        Point2D.Float edgeP1 = getSourceEndPoint();
        Point2D.Float edgeP2 = getTargetEndPoint();
        Point2D.Float edgeCTRL1 = getVisibleCTRL1();
        Point2D.Float edgeCTRL2 = getVisibleCTRL2();
        BezierLayout edgeLayout = getBezierLayout();

        // Make sure this node is contained within the selection box
        if (!(selectionBox.contains(edgeP1) && selectionBox.contains(edgeP2) && selectionBox.contains(edgeCTRL1)
                && selectionBox.contains(edgeCTRL2))) {
            // System.out.println("Edge " + edgeP1 + " "
            // + edgeP2 + " "
            // + edgeCTRL1 + " "
            // + edgeCTRL2 + " "
            // + " outside bounds " + selectionBox);
            return exportString;
        }

        if (exportType == GraphExporter.INT_EXPORT_TYPE_PSTRICKS) {
            // Check whether this should be a line or a curve
            if (edgeLayout.isStraight()) {
                // Draw a straight line
                exportString += "  \\psline[arrowsize=5pt";
                exportString += (hasUnobservableEvent() ? ", linestyle=dashed" : "");
                exportString += "]{->}(" + (edgeP1.x - selectionBox.x) + ","
                        + (selectionBox.y + selectionBox.height - edgeP1.y) + ")(" + (edgeP2.x - selectionBox.x) + ","
                        + (selectionBox.y + selectionBox.height - edgeP2.y) + ")\n";
            } else {
                // Draw a curve
                exportString += "  \\psbezier[arrowsize=5pt";
                exportString += (hasUnobservableEvent() ? ", linestyle=dashed" : "");
                exportString += "]{->}" + "(" + (edgeP1.getX() - selectionBox.x) + ","
                        + (selectionBox.y + selectionBox.height - edgeP1.getY()) + ")("
                        + (edgeCTRL1.getX() - selectionBox.x) + ","
                        + (selectionBox.y + selectionBox.height - edgeCTRL1.getY()) + ")("
                        + (edgeCTRL2.getX() - selectionBox.x) + ","
                        + (selectionBox.y + selectionBox.height - edgeCTRL2.getY()) + ")("
                        + (edgeP2.getX() - selectionBox.x) + ","
                        + (selectionBox.y + selectionBox.height - edgeP2.getY()) + ")\n";
            }

            // Now for controllable event line
            if (!hasUncontrollableEvent()) {
                controlLine = edgeLayout.getControllableMarker();
                if (controlLine != null) {
                    exportString += "\\psline(" + (controlLine.getX1() - selectionBox.x) + ","
                            + (selectionBox.y + selectionBox.height - controlLine.getY1()) + ")("
                            + (controlLine.getX2() - selectionBox.x) + ","
                            + (selectionBox.y + selectionBox.height - controlLine.getY2()) + ")\n";
                }
            }

            // Now for the label
            if ((edgeLayout.getText() != null) && (getLabel().getText().length() > 0)) {
                exportString += "  " + getLabel().createExportString(selectionBox, exportType);
            }
        } else if (exportType == GraphExporter.INT_EXPORT_TYPE_EPS) {
            // LENKO!!!
        }

        return exportString;
    }

    /**
     * Returns the bounding box for the edge based on its four control points.
     * 
     * @return Rectangle The bounds of the Bezier Curve
     */
    @Override
    public Rectangle bounds() {
        Point2D.Float edgeP1 = getP1();
        Point2D.Float edgeP2 = getP2();
        Point2D.Float edgeCTRL1 = getCTRL1();
        Point2D.Float edgeCTRL2 = getCTRL2();

        float minX = BentoBox.getMinValue(edgeP1.x, edgeP2.x, edgeCTRL1.x, edgeCTRL2.x);
        float minY = BentoBox.getMinValue(edgeP1.y, edgeP2.y, edgeCTRL1.y, edgeCTRL2.y);
        float maxX = BentoBox.getMaxValue(edgeP1.x, edgeP2.x, edgeCTRL1.x, edgeCTRL2.x);
        float maxY = BentoBox.getMaxValue(edgeP1.y, edgeP2.y, edgeCTRL1.y, edgeCTRL2.y);

        return new Rectangle(BentoBox.convertFloatToInt(minX), BentoBox.convertFloatToInt(minY),
                BentoBox.convertFloatToInt(maxX - minX), BentoBox.convertFloatToInt(maxY - minY));
    }

    /*
     * (non-Javadoc)
     * 
     * @see presentation.fsa.Edge#isMovable(int)
     */
    @Override
    public boolean isMovable(int pointType) {
        return (pointType == BezierEdge.CTRL1 || pointType == BezierEdge.CTRL2);
    }

    /**
     * Sets the point of the given type to <code>point</code>.
     * 
     * @param point     the point to set
     * @param pointType see <code>P1, CTRL1, CTRL2, P2</code>
     */
    @Override
    public void setPoint(Point2D point, int pointType) {
        getBezierLayout().setPoint(point, pointType);
    }

    /**
     * Adds the given symbol to be displayed in the label on this edge.
     * 
     * @param symbol the event symbol to be displayed
     */
    @Override
    public void addEventName(String symbol) {
        getBezierLayout().addEventName(symbol);
    }

    /**
     * Computes this curve as a straight, directed edge from <code>s</code>, the
     * layout for the source node to <code>endPoint</code>.
     * 
     * @param s        layout for source node
     * @param endPoint endpoint for the edge
     */
    public void computeCurve(CircleNodeLayout s, Float endPoint) {
        getBezierLayout().computeCurve(s, endPoint);
    }

    /**
     * Computes this curve as a straight, directed edge from the source node with
     * layout data <code>nL1</code>, to the target node having layout data
     * <code>nL2</code>.
     * 
     * @param nL1 the layout data for the source node
     * @param nL2 the layout data for the target node
     */
    public void computeCurve(CircleNodeLayout nL1, CircleNodeLayout nL2) {
        getBezierLayout().computeCurve(nL1, nL2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see presentation.fsa.Edge#computeEdge(presentation.fsa.Node,
     * presentation.fsa.Node)
     */
    @Override
    public void computeEdge() {
        if (getSourceNode() != null && getTargetNode() != null) {
            getBezierLayout().computeCurve((CircleNodeLayout) getSourceNode().getLayout(),
                    (CircleNodeLayout) getTargetNode().getLayout());
            refresh();
        }
    }

    /**
     * Returns whether this edge is straight.
     * 
     * @return true iff this edge is straight
     */
    @Override
    public boolean isStraight() {
        return getBezierLayout().isStraight();
    }

    /*
     * (non-Javadoc)
     * 
     * @see presentation.fsa.Edge#straighten()
     */
    @Override
    public void straighten() {
        ((BezierLayout) getLayout()).straighten();
    }

    /**
     * Returns whether the shape of this curve can be modified; always true.
     * 
     * @return true
     */
    @Override
    public boolean canBeStraightened() {
        return true;
    }

    /**
     * Increases the arc of this curve.
     */
    public void arcMore() {
        getBezierLayout().arcMore();
        // this.computeCurve((CircleNodeLayout)this.getSourceNode().getLayout(),
        // (CircleNodeLayout)this.getTargetNode().getLayout());
    }

    /**
     * Decreases the arc of this curve.
     */
    public void arcLess() {
        getBezierLayout().arcLess();
    }

    public void symmetrize() {
        getBezierLayout().symmetrize();
    }

    /**
     * @see presentation.fsa.Edge#intersectionWithBoundary(presentation.fsa.Node,
     *      int type)
     */
    @Override
    public Point2D.Float intersectionWithBoundary(Node node, int type) {
        Point2D.Float intersection = new Point2D.Float();
        intersectionWithBoundary(node.getShape(), intersection, type);
        return intersection;
    }

    /**
     * A simple binary search starting within the boundary of the given node shape.
     * Sets the coordinates of <code>intersection</code> to the point where this
     * curve intersects the boundary of <code>nodeShape</code>. If endpoints are
     * both inside node (self-loop or overlapping target and source), then returns
     * 0.5. Precondition: node is not null and intersection is not null
     * 
     * @param nodeShape    the shape representing this node
     * @param type         ignored
     * @param intersection acts as a second return value; to be set to the point of
     *                     intersection with the boundary of the given node shape
     * @return the parameter t in [0,1] at which the bezier curve intersects
     *         <code>node</code>
     */
    protected float intersectionWithBoundary(Shape nodeShape, Point2D.Float intersection, int type) {

        // setup curves for iterative subdivision
        CubicParamCurve2D curve = this.getBezierLayout().getCurve();

        // if endpoints are both inside node (self-loop or overlapping target
        // and source)
        // FIXME
        if (nodeShape.contains(curve.getP1()) && nodeShape.contains(curve.getP2())) {
            return 0.5f;
        }

        CubicParamCurve2D left = new CubicParamCurve2D();
        CubicParamCurve2D right = new CubicParamCurve2D();

        CubicParamCurve2D temp = new CubicParamCurve2D();

        // if target, then this algorithm needs to be reversed since
        // it searches curve assuming t=0 is inside the node.
        boolean intersectWithTarget = (type == Edge.TARGET_NODE);

        if (intersectWithTarget) {
            // swap endpoints and control points
            temp.setCurve(curve.getP2(), curve.getCtrlP2(), curve.getCtrlP1(), curve.getP1());
        } else {
            temp.setCurve(curve);
        }

        float epsilon = 0.00001f;
        float tPrevious = 0f;
        float t = 0.5f;
        float step = 0.5f;

        temp.subdivide(left, right, t);
        // the point on curve at param t
        Point2D c_t = left.getP2();

        while (Math.abs(t - tPrevious) > epsilon) {
            step = Math.abs(t - tPrevious);
            tPrevious = t;
            if (nodeShape.contains(c_t)) { // inside boundary
                                           // search right segment
                t += step / 2;
            } else {
                // search left segment
                t -= step / 2;
            }
            temp.subdivide(left, right, t);
            c_t = left.getP2();
        }

        // TODO keep searching from c_t towards t=0 until we're sure we've found
        // the first intersection.
        // Start again with step size at t.

        if (intersectWithTarget) {
            t = 1 - t;
            assert (0 <= t && t <= 1);
        }

        intersection.x = (float) c_t.getX();
        intersection.y = (float) c_t.getY();

        return t;
    }

    /**
     * Sets this edge's layout to fit among the set of existing edges between its
     * source and target nodes. Other edges' layouts are adjusted as necessary.
     * 
     * @param neighbours the set of edges between the source and target nodes
     */
    public void insertAmong(Set<Edge> neighbours) {
        BezierEdgePlacer.insertEdgeAmong(this, neighbours);
    }

    /*
     * (non-Javadoc)
     * 
     * @see presentation.fsa.Edge#getSourceEndPoint()
     */
    @Override
    public Float getSourceEndPoint() {
        return ((BezierLayout) getLayout()).getSourceEndPoint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see presentation.fsa.Edge#getTargetEndPoint()
     */
    @Override
    public Float getTargetEndPoint() {
        return ((BezierLayout) getLayout()).getTargetEndPoint();
    }

    // /**
    // * If this edge is not straight, make it have a symmetrical appearance.
    // * Make the two vectors - from P1 to CTRL1 and from P2 to CTRL2, be of
    // * the same length and have the same angle. So the edge will look it has
    // * a symmetrical curve.
    // * There are two cases:
    // * The 2 control points are on the same side of the curve (a curve with
    // the
    // * form of a bow); and the 2 control points are on different sides of the
    // * edge (a curve like a wave). In one of the cases, theangles of the
    // vectors
    // * should be A=B, in the other A=-B.
    // */
    // public void symmetrize()
    // {
    // this.getBezierLayout().symmetrize();
    // }
}