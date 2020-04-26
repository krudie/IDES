/**
 * 
 */
package presentation.fsa;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Set;

import ides.api.model.fsa.FSATransition;
import io.fsa.ver2_1.GraphExporter;
import presentation.CubicParamCurve2D;
import presentation.Geometry;
import presentation.GraphicalLayout;

/**
 * A symmetric self-loop manipulated by a single control point. TODO - refactor
 * constructors (if super constructors are a pain, don't call them). - find a
 * way to compute control points from midpoint, centre point, scalars and angles
 * such that the midpoint set by the user remains fixed.
 * 
 * @author Helen Bretzke
 */
public class ReflexiveEdge extends BezierEdge {
    /**
     * Old size of the correspondent edge. When this variable changes the value, the
     * refresh method call routines to recompute the curve paramethers.
     */
    float lastNodeRadius;

    /**
     * Index of midpoint used as handle to modify the curve position.
     */
    public static final int MIDPOINT = 4;

    /**
     * Creates a reflexive edge on <code>node</code> with the given layout and
     * transition.
     * 
     * @param layout
     * @param node
     * @param t      a transition this represented by this edge
     */
    public ReflexiveEdge(BezierLayout layout, Node node, FSATransition t) {
        super(node, node);
        // CHRISTIAN
        lastNodeRadius = ((CircleNodeLayout) node.getLayout()).getRadius();
        // CHRISTIAN
        addTransition(t);
        setLayout(new ReflexiveLayout(node, this, layout));
        setHandler(new ReflexiveHandler(this));
        ((ReflexiveLayout) this.getLayout()).setGroup(layout.getGroup());
    }

    /**
     * Creates a reflexive edge on <code>node</code> representing the given
     * transition.
     * 
     * @param node
     * @param t    a transition this represented by this edge
     */
    public ReflexiveEdge(Node node, FSATransition t) {
        super(node, node);
        lastNodeRadius = ((CircleNodeLayout) node.getLayout()).getRadius();
        if (t != null) {
            addTransition(t);
        }
        setLayout(new ReflexiveLayout(node, this));
        setHandler(new ReflexiveHandler(this));

        // // place me among any other edges adjacent to node
        // Iterator<Edge> neighbours = node.adjacentEdges();
        // if(neighbours.hasNext()){
        // Set<Edge> n = new HashSet<Edge>();
        // while(neighbours.hasNext()) {
        // n.add(neighbours.next());
        // }
        // insertAmong(n);
        // }
        // ((ReflexiveLayout)getLayout()).setAxis(((ReflexiveLayout)getLayout())
        // .computeBestDirection(this.getSourceNode()));
        // computeEdge();
    }

    /**
     * Auto-format: Change the angle of the axis to make the initial arrow go to the
     * most confortable position
     */
    public void resetPosition() {
        ((ReflexiveLayout) getLayout()).resetPosition(this.getTargetNode());
    }

    /**
     * Searchs for enough space along circumference of node to place this edge. If
     * not enough space, places this edge in the default position. TODO If not
     * enough space, looks for a layout that doesn't clobber another reflexive edge.
     */
    @Override
    public void insertAmong(Set<Edge> neighbours) {
        double delta = Math.toRadians(2.0);
        double alpha = 0.0;

        if (!BezierEdgePlacer.tooClose(this, neighbours)) {
            return;
        }

        /**
         * Search for a free space using brute force and ignorance.
         */
        while (BezierEdgePlacer.tooClose(this, neighbours) && alpha < 360) {
            ((ReflexiveLayout) getLayout()).setAxis(Geometry.rotate(((ReflexiveLayout) getLayout()).getAxis(), delta));
            // setMidpoint(Geometry.add(getSourceNode().getLocation(),
            // ((ReflexiveLayout)getLayout()).getAxis()));
            // computeEdge();
            alpha++;
        }

        if (alpha == 360) {
            // TODO find a spot that doesn't mask another reflexive edge

        }
    }

    /**
     * Set the midpoint of the curve to <code>point</code>.
     * 
     * @param point the new midpoint.
     */
    public void setMidpoint(Point2D point) {
        ((ReflexiveLayout) getLayout()).setPoint(point, MIDPOINT);
        setNeedsRefresh(true);
    }

    /**
     * Returns the midpoint of the curve representing this edge.
     * 
     * @return the midpoint of this edge
     */
    public Point2D getHandleLocation() {
        return ((ReflexiveLayout) getLayout()).getHandleLocation();
    }

    /**
     * Returns true iff pointType is MIDPOINT since all other points are either
     * fixed or computed from this point.
     * 
     * @return whether the given point type is movable for this edge type
     */
    @Override
    public boolean isMovable(int pointType) {
        return pointType == MIDPOINT;
    }

    /**
     * FIXME customize so that intersection with boundary is computed properly;
     * parameters (sourceT and targetT) are currently being reverse.
     */
    @Override
    public void refresh() {
        super.refresh();
        ((ReflexiveLayout) getLayout()).computeCurve();
    }

    /**
     * This method is responsible for creating a string that contains an appropriate
     * (depending on the type) representation of this self-loop. TODO: Calculate a
     * better C1 and C2 or export - there are no PSTricks self-loop options
     * <p>
     * author Sarah-Jane Whittaker
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

        // Make sure this node is contained within the selection box
        if (!(selectionBox.contains(edgeP1) && selectionBox.contains(edgeP2) && selectionBox.contains(edgeCTRL1)
                && selectionBox.contains(edgeCTRL2))) {
            // System.out.println("Self-loop " + edgeP1 + " "
            // + edgeP2 + " "
            // + edgeCTRL1 + " "
            // + edgeCTRL2 + " "
            // + " outside bounds " + selectionBox);
            return exportString;
        }

        if (exportType == GraphExporter.INT_EXPORT_TYPE_PSTRICKS) {
            // Draw the curve
            exportString += "  \\psbezier[arrowsize=5pt";
            exportString += (hasUnobservableEvent() ? ", linestyle=dashed" : "");
            exportString += "]{->}" + "(" + (edgeP1.x - selectionBox.x) + ","
                    + (selectionBox.y + selectionBox.height - edgeP1.y) + ")(" + (edgeCTRL1.x - selectionBox.x) + ","
                    + (selectionBox.y + selectionBox.height - edgeCTRL1.y) + ")(" + (edgeCTRL2.x - selectionBox.x) + ","
                    + (selectionBox.y + selectionBox.height - edgeCTRL2.y) + ")(" + (edgeP2.x - selectionBox.x) + ","
                    + (selectionBox.y + selectionBox.height - edgeP2.y) + ")\n";

            // Now for uncontrollable event line
            if (!hasUncontrollableEvent()) {
                controlLine = getBezierLayout().getControllableMarker();
                if (controlLine != null) {
                    exportString += "\\psline(" + (controlLine.getX1() - selectionBox.x) + ","
                            + (selectionBox.y + selectionBox.height - controlLine.getY1()) + ")("
                            + (controlLine.getX2() - selectionBox.x) + ","
                            + (selectionBox.y + selectionBox.height - controlLine.getY2()) + ")\n";
                }
            }

            // Now for the label
            if ((getBezierLayout().getText() != null) && (getLabel().getText().length() > 0)) {
                exportString += "  " + getLabel().createExportString(selectionBox, exportType);
            }
        } else if (exportType == GraphExporter.INT_EXPORT_TYPE_EPS) {
            // LENKO!!!
        }

        return exportString;
    }

    /**
     * This method returns the bounding box for the edge and its label.
     * 
     * @return Rectangle The bounds of the Bezier Curve and its label.
     */
    @Override
    public Rectangle bounds() {
        return ((ReflexiveLayout) getLayout()).getCurve().getBounds().union(getLabel().bounds());
    }

    @Override
    public void translate(float x, float y) {
        super.translate(x, y);
        // Christian(May, 17, 2007)
        // Manual midpoint translation commented.
        // Point2D midpoint = ((ReflexiveLayout)getLayout()).midpoint;
        // midpoint.setLocation(midpoint.getX() + x, midpoint.getY() + y);
    }

    @Override
    public void computeEdge() {
        refresh();
        ((ReflexiveLayout) getLayout()).computeCurve();
    }

    /**
     * Returns false since a self-loop cannot be straight.
     */
    @Override
    public boolean isStraight() {
        return false;
    }

    /**
     * Returns false since cannot straighten a self-loop.
     */
    @Override
    public boolean canBeStraightened() {
        return false;
    }

    /**
     * Sets the coordinates of <code>intersection</code> to the location where my
     * bezier curve intersects the boundary of <code>node</code>.
     * <p>
     * Precondition: node != null and intersection != null
     * 
     * @return param t at which my bezier curve intersects <code>node</code>
     */
    @Override
    protected float intersectionWithBoundary(Shape nodeShape, Point2D.Float intersection, int type) {

        // setup curves for iterative subdivision
        CubicParamCurve2D curve = this.getBezierLayout().getCurve();

        CubicParamCurve2D left = new CubicParamCurve2D();
        CubicParamCurve2D right = new CubicParamCurve2D();

        CubicParamCurve2D temp = new CubicParamCurve2D();
        // if target, then this algorithm needs to be reversed since
        // it searches curve assuming t=0 is inside the node.

        if (type == TARGET_NODE) {
            // swap endpoints and control points
            temp.setCurve(curve.getP2(), curve.getCtrlP2(), curve.getCtrlP1(), curve.getP1());
        } else if (type == SOURCE_NODE) {
            temp.setCurve(curve);
        } else {
            return 0f;
        }

        float epsilon = 0.00001f;
        float tPrevious = 0f;
        float t = 0.5f - 0.01f; // 1f;
        float step = t; // 1f;

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

        if (type == TARGET_NODE) {
            t = 1 - t;
            assert (0 <= t && t <= 1);
        }

        intersection.x = (float) c_t.getX();
        intersection.y = (float) c_t.getY();

        return t;
    }

    /**
     * Visual representation of the single control point at the midpoint of a
     * reflexive edge. Used to modify the size and orientation a reflexive edge.
     * 
     * @author helen bretzke
     */
    public class ReflexiveHandler extends EdgeHandler {

        /* Circle representing the handle to drag with the mouse */
        private Ellipse2D.Double anchor;

        /* Radius of the anchor */
        private static final int RADIUS = 5;

        /**
         * Creates a handler for the given edge.
         * 
         * @param edge
         */
        public ReflexiveHandler(ReflexiveEdge edge) {
            super(edge);
            refresh();
        }

        /**
         * Refreshes the position of this handler based on location of midpoint of edge.
         */
        @Override
        public void refresh() {
            int d = 2 * RADIUS;
            Point2D midpoint = ((ReflexiveEdge) getEdge()).getHandleLocation();
            Point2D endpoint = ((ReflexiveEdge) getEdge()).getBezierLayout().getCurve().getP1();
            midpoint = Geometry.translate(midpoint, -endpoint.getX(), -endpoint.getY());
            double len = Geometry.norm(midpoint);
            midpoint = Geometry.scale(midpoint, (len + RADIUS) / len);
            midpoint = Geometry.translate(midpoint, endpoint.getX(), endpoint.getY());
            anchor = new Ellipse2D.Double(midpoint.getX() - RADIUS, midpoint.getY() - RADIUS, d, d);
            // anchor = new
            // Ellipse2D.Double(((ReflexiveEdge)getEdge()).getMidpoint().getX()
            // - RADIUS, ((ReflexiveEdge)getEdge()).getMidpoint().getY() - d, d,
            // d);
            setNeedsRefresh(false);
        }

        /**
         * Returns true iff p intersects the midpoint anchor.
         * 
         * @return true iff p intersects the midpoint anchor.
         */
        @Override
        public boolean intersects(Point2D p) {
            if (anchor.contains(p)) {
                lastIntersected = MIDPOINT;
                return true;
            }
            lastIntersected = NO_INTERSECTION;
            return false;
        }

        /**
         * Renders this handler in the given graphics context.
         * 
         * @param g the graphics context in which to render this handler
         */
        @Override
        public void draw(Graphics g) {
            if (needsRefresh()) {
                refresh();
            }

            if (!visible) {
                return;
            }

            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(Color.BLUE);
            g2d.setStroke(GraphicalLayout.FINE_STROKE);
            g2d.draw(anchor);
        }

    } // end Handler

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

}
