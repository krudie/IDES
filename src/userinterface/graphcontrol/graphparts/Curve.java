/*
 * Created on Nov 12, 2004
 */
package userinterface.graphcontrol.graphparts;

import org.eclipse.swt.SWT;

import projectModel.SubElement;

import userinterface.general.Ascii;
import userinterface.geometric.Geometric;
import userinterface.geometric.Line;
import userinterface.geometric.Point;
import userinterface.geometric.UnitVector;
import userinterface.graphcontrol.Drawer;

/**
 * A Curve is a Bezier or Cubic curve, with the concept of a starting end (tail)
 * and a finishing edn (head). The curve is aware of the node at its tail, the
 * node at it's head, and the ArrowHead at its head. It is only indirectly aware
 * of the GraphModel, and the Edge it represents. This class primarly handles
 * the geometric concepts of the Edge.
 * 
 * @author Michael Wood
 */
public class Curve{
    /**
     * Which drawing and manipulation method to use.
     */
    private static final int SELECTED_METHOD = 0;

    /**
     * The radius of the anchor circles.
     */
    private static final int ANCHOR_RADIUS = 5;

    /**
     * The position of the self loop anchor on the self loop curve
     */
    private static final float SELF_LOOP_ANCHOR_POSITION = (float) 0.53;

    /**
     * The tail Node.
     */
    private Node tail_node = null;

    /**
     * The head Node.
     */
    private Node head_node = null;

    /**
     * The curve object used for rendering to the screen.
     */
    private CubicCurve2Dex curve = null;

    /**
     * The ArrowHead for this curve.
     */
    private ArrowHead arrowhead = null;

    /**
     * The direction bisecting a self loop.
     */
    private UnitVector self_loop_direction = null;

    /**
     * Remembers the angles between the node origins and the 4 curve points, and
     * remembers the radius at which the ctrl points exist from their nodes.
     * This lets us form reasonable configurations when the nodes are moved.
     * These are only recalculated when the curve points are custom modified.
     * The ctrl_mag_factors are the ctrl_flat_distances divided by the distance
     * between anchors. The ctrl_ratios are the ctrl_flat_distances divided by
     * the ctrl_radial_distances.
     */
    private float tail_anchor_angle = 0, tail_ctrl_angle = 0, tail_ctrl_radius = 0,
            tail_ctrl_mag_factor = 0, tail_ctrl_ratio = 0, head_ctrl_ratio = 0,
            head_ctrl_mag_factor = 0, head_ctrl_radius = 0, head_ctrl_angle = 0,
            head_anchor_angle = 0;

    /*
     * Lenko
     */
    /**
     * Remember the coefficients for the distances between the end points of the
     * curve and the control points relative to the distance between the two end
     * points, i.e., D(P1,CTRL1)/D(P1,P2) and D(P2,CTRL2)/D(P1,P2).
     */
    protected float distanceCoefCtrl1 = 1, distanceCoefCtrl2 = 1;

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Curve construction /////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construct the Curve.
     * 
     * @param tail_node The tail Node.
     * @param head_node The head Node.
     */
    public Curve(Node tail_node, Node head_node){
        this.tail_node = tail_node;
        this.head_node = head_node;
        curve = new CubicCurve2Dex();
        arrowhead = new ArrowHead();
        calculateCurve(0, 0);
    }

    /**
     * Construct a self-loop Curve.
     * 
     * @param tail_node The tail Node.
     * @param head_node The head Node.
     * @param self_loop_direction The direction of the self loop bisector.
     */
    public Curve(Node tail_node, Node head_node, UnitVector self_loop_direction){
        this.self_loop_direction = self_loop_direction;
        this.tail_node = tail_node;
        this.head_node = head_node;
        curve = new CubicCurve2Dex();
        arrowhead = new ArrowHead();
        recalculateSelfLoop();
    }

    /**
     * Construct the Curve.
     * 
     * @param tail_node The tail Node.
     * @param head_node The head Node.
     * @param x1 x1 parameter for the curve object.
     * @param y1 y1 parameter for the curve object.
     * @param ctrlx1 ctrlx1 parameter for the curve object.
     * @param ctrly1 ctrly1 parameter for the curve object.
     * @param ctrlx2 ctrlx2 parameter for the curve object.
     * @param ctrly2 ctrly2 parameter for the curve object.
     * @param x2 x2 parameter for the curve object.
     * @param y2 y2 parameter for the curve object.
     * @param self_loop_direction The direction for the self loop.
     */
    public Curve(Node tail_node, Node head_node, float x1, float y1, float ctrlx1, float ctrly1,
            float ctrlx2, float ctrly2, float x2, float y2, UnitVector self_loop_direction){
        this.self_loop_direction = self_loop_direction;
        this.tail_node = tail_node;
        this.head_node = head_node;
        curve = new CubicCurve2Dex(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2);
        arrowhead = new ArrowHead();
        updateArrowhead();
        if(tail_node == head_node) recalculateSelfLoop();
    }

    
    
    /**
     * Destruct the curve.
     */
    public void dispose(){
        tail_node = null;
        head_node = null;
        curve = null;
        arrowhead = null;
        self_loop_direction = null;
    }

    /**
     * Create a clone of this Curve.
     * 
     * @param tail_node
     *            The tail Node.
     * @param head_node
     *            The head Node.
     * @return A clone of this Curve.
     */
    public Curve newClone(Node tail_node, Node head_node){
        UnitVector cloned_direction = null;
        if(self_loop_direction != null){
            cloned_direction = self_loop_direction.getCopy();
        }
        return new Curve(tail_node, head_node, curve.x1, curve.y1, curve.ctrlx1, curve.ctrly1,
                curve.ctrlx2, curve.ctrly2, curve.x2, curve.y2, cloned_direction);
    }
    
    public Curve(Node tail, Node head, SubElement se){
        this.self_loop_direction = new UnitVector(
                Ascii.safeFloat(se.getAttribute("dx")),
                Ascii.safeFloat(se.getAttribute("dy")));
        this.tail_node = tail;
        this.head_node = head;
        curve = new CubicCurve2Dex(
                Ascii.safeFloat(se.getAttribute("x1")),
                Ascii.safeFloat(se.getAttribute("y1")),
                Ascii.safeFloat(se.getAttribute("ctrlx1")),
                Ascii.safeFloat(se.getAttribute("ctrly1")),
                Ascii.safeFloat(se.getAttribute("ctrlx2")),
                Ascii.safeFloat(se.getAttribute("ctrly2")),
                Ascii.safeFloat(se.getAttribute("x2")),
                Ascii.safeFloat(se.getAttribute("y2")));
        arrowhead = new ArrowHead();
        updateArrowhead();
        if(tail_node == head_node) recalculateSelfLoop();
    }

    public SubElement toSubElement(String name){
        SubElement c = new SubElement(name);
        c.setAttribute("x1", Float.toString(curve.x1));
        c.setAttribute("y1", Float.toString(curve.y1));
        c.setAttribute("x2", Float.toString(curve.x2));
        c.setAttribute("y2", Float.toString(curve.y2));
        c.setAttribute("ctrlx1", Float.toString(curve.ctrlx1));
        c.setAttribute("ctrly1", Float.toString(curve.ctrly1));
        c.setAttribute("ctrlx2", Float.toString(curve.ctrlx2));
        c.setAttribute("ctrly2", Float.toString(curve.ctrly2));
        if(self_loop_direction != null){
            c.setAttribute("dx", Float.toString(self_loop_direction.x));
            c.setAttribute("dy", Float.toString(self_loop_direction.y));
        }
        return c;
    }
    
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Drawing
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Draw the curve and arrowhead.
     * 
     * @param drawer
     *            The Drawer that will handle the drawing.
     * @param style
     *            The style for the Drawer (i.e. solid/dashed).
     */
    public void drawCurve(Drawer drawer, int style){
        drawer.drawCurve(curve, style);
        arrowhead.draw(drawer);
    }

    /**
     * Draw the self loop anchor.
     * 
     * @param drawer
     *            The Drawer that will handle the drawing.
     */
    public void drawSelfLoopAnchor(Drawer drawer){
        drawer.drawCircle(selfLoopAnchor(), ANCHOR_RADIUS, Drawer.SMALL_SOLID);
    }

    /**
     * Draw the tail anchor, ctrl and tether.
     * 
     * @param drawer
     *            The Drawer that will handle the drawing.
     */
    public void drawTailAnchors(Drawer drawer){
        drawer.drawCircle(tailAnchor(), ANCHOR_RADIUS, Drawer.SMALL_SOLID);
        drawer.drawCircle(tailCtrl(), ANCHOR_RADIUS, Drawer.SMALL_SOLID);
        drawer.drawLine(tailAnchor(), tailCtrl(), Drawer.SMALL_DASHED);
    }

    /**
     * Draw the head anchor, ctrl and tether.
     * 
     * @param drawer
     *            The Drawer that will handle the drawing.
     */
    public void drawHeadAnchors(Drawer drawer){
        drawer.drawCircle(headAnchor(), ANCHOR_RADIUS, Drawer.SMALL_SOLID);
        drawer.drawCircle(headCtrl(), ANCHOR_RADIUS, Drawer.SMALL_SOLID);
        drawer.drawLine(headAnchor(), headCtrl(), Drawer.SMALL_DASHED);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Intersections
    // //////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
     * Lenko
     */
    /**
     * Finds the cubic curve parameter t for the intersection of the curve with
     * a circle that has its centerpoint at one of the endpoints of the curve
     * (used to find the intersections of the edge with its nodes).
     * 
     * @param radius
     *            The radius of the circle.
     * @param startPoint
     *            true if the circle is at the start point of the curve or false
     *            if the circle is at the end point of the curve
     * @return the prameter t of the cubic curve which indicates the
     *         intersection with the circle
     */
    protected float intersectWithCircle(int radius, boolean startPoint){
        float t;
        int x, y;
        if(startPoint){
            t = 1 - (curve.minLength() - radius) / curve.maxLength();
            if(t > 1) return 1;
            x = (int) curve.x1;
            y = (int) curve.y1;
        }
        else{
            t = (curve.minLength() - radius) / curve.maxLength();
            if(t < 0) return 0;
            x = (int) curve.x2;
            y = (int) curve.y2;
        }
        Point b = calculateBezierPoint(t);
        float distance = (float) Math.sqrt((x - b.getX()) * (x - b.getX()) + (y - b.getY())
                * (y - b.getY()))
                - radius;
        while(Math.round(distance) > 0){
            if(startPoint) t -= distance / curve.maxLength();
            else t += distance / curve.maxLength();
            b = calculateBezierPoint(t);
            distance = (float) (Math.sqrt((x - b.getX()) * (x - b.getX()) + (y - b.getY())
                    * (y - b.getY())) - radius);
        }
        if(t < 0) t = 0;
        else if(t > 1) t = 1;
        return t;
    }

    /*
     * Lenko
     */
    /**
     * Finds the cubic curve parameter t such that the Euclid distance between
     * the points on the curve defined by the parameters start and t is distance
     * (used to leave room for the arrow of the edge).
     * 
     * @param start
     *            the cubic curve parameter defining the start point
     * @param distance
     *            the distance between the points defined by start and the
     *            return value
     * @param startToEnd
     *            true if distance is computed in the direction of the curve or
     *            false if it is computed in the opposite direction
     * @return the cubic curve parameter t such that the distance between the
     *         points defined by start and t is distance
     */
    protected float travelEdge(float start, float distance, boolean startToEnd){
        float t;
        Point s = calculateBezierPoint(start);
        if(startToEnd){
            t = start + distance / curve.maxLength();
            if(t > 1) return 1;
        }
        else{
            t = start - distance / curve.maxLength();
            if(t < 0) return 0;
        }
        Point b = calculateBezierPoint(t);
        float d = distance
                - (float) Math.sqrt((s.getX() - b.getX()) * (s.getX() - b.getX())
                        + (s.getY() - b.getY()) * (s.getY() - b.getY()));
        while(Math.round(d) > 0){
            if(startToEnd) t += d / curve.maxLength();
            else t -= d / curve.maxLength();
            b = calculateBezierPoint(t);
            d = (float) (distance - Math.sqrt((s.getX() - b.getX()) * (s.getX() - b.getX())
                    + (s.getY() - b.getY()) * (s.getY() - b.getY())));
        }
        if(t < 0) t = 0;
        else if(t > 1) t = 1;
        return t;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Manipulation (custom points)
    // ///////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Move the tail anchor, and if necessary the tail ctrl point. The tail
     * anchor is attached to a circle, and the anchor-ctrl line shouldn't breach
     * that circle.
     * 
     * @param origional_configuration
     *            The configuration of the curve at the initiation of movement.
     * @param mouse
     *            The current mouse position.
     */
    public void moveTailAnchor(Configuration origional_configuration, Point mouse){
        // update the anchor
        Line line = new Line(tail_node.origin(), mouse);
        line.rotate(origional_configuration.selection_error_fix);
        curve.x1 = tail_node.origin().getX() + tail_node.getR() * line.d.x;
        curve.y1 = tail_node.origin().getY() + tail_node.getR() * line.d.y;

        if((origional_configuration.state_mask & SWT.CTRL) != SWT.CTRL){
            // update the ctrl point
            line = new Line(tail_node.origin(), tailAnchor());
            Point directions = line.findDirections(tailCtrl());
            if(directions.getX() < 0){
                // only move the ctrl point if it has fallen past the
                // perpendicular of the radial arm.
                float mag = Geometric.distance(origional_configuration.tail_anchor,
                        origional_configuration.tail_ctrl);
                UnitVector d = line.d.newPerpendicular();
                if(directions.getY() > 0){
                    d.reverse();
                } // make sure we position it in the proper direction.
                curve.ctrlx1 = curve.x1 + mag * d.x;
                curve.ctrly1 = curve.y1 + mag * d.y;
            }
        }
    }

    /**
     * Move the tail ctrl, and if necessary the tail anchor point. The tail
     * anchor is attached to a circle, and the anchor-ctrl line shouldn't breach
     * that circle.
     * 
     * @param origional_configuration
     *            The configuration of the curve at the initiation of movement.
     * @param mouse
     *            The current mouse position.
     */
    public void moveTailCtrl(Configuration origional_configuration, Point mouse){
        // update the ctrl point
        curve.ctrlx1 = origional_configuration.tail_ctrl.getX()
                + (mouse.getX() - origional_configuration.movement_origin.getX());
        curve.ctrly1 = origional_configuration.tail_ctrl.getY()
                + (mouse.getY() - origional_configuration.movement_origin.getY());

        if((origional_configuration.state_mask & SWT.CTRL) != SWT.CTRL){
            // update the anchor
            Line line = new Line(tail_node.origin(), tailAnchor());
            Point directions = line.findDirections(tailCtrl());
            if(directions.getX() < 0){
                // only move the anchor point if the ctrl has been moved past
                // the perpendicular of the radial arm.
                float mag = (float) (2 * Math.PI * tail_node.getR() / 20);
                UnitVector d = new UnitVector(tailAnchor(), tailCtrl());
                Point new_anchor = new Point(curve.x1 + mag * d.x, curve.y1 + mag * d.y);

                // update the anchor (this forces the location back onto the
                // circle)
                line = new Line(tail_node.origin(), new_anchor);
                curve.x1 = tail_node.origin().getX() + tail_node.getR() * line.d.x;
                curve.y1 = tail_node.origin().getY() + tail_node.getR() * line.d.y;
            }
        }
    }

    /**
     * Move the head ctrl, and if necessary the head anchor point. The head
     * anchor is attached to a circle, and the anchor-ctrl line shouldn't breach
     * that circle.
     * 
     * @param origional_configuration
     *            The configuration of the curve at the initiation of movement.
     * @param mouse
     *            The current mouse position.
     */
    public void moveHeadCtrl(Configuration origional_configuration, Point mouse){
        // pad the circle
        int padded_radius = head_node.getR() + ArrowHead.SHORT_HEAD_LENGTH;

        // update the ctrl point
        curve.ctrlx2 = origional_configuration.head_ctrl.getX()
                + (mouse.getX() - origional_configuration.movement_origin.getX());
        curve.ctrly2 = origional_configuration.head_ctrl.getY()
                + (mouse.getY() - origional_configuration.movement_origin.getY());

        if((origional_configuration.state_mask & SWT.CTRL) != SWT.CTRL){
            // update the anchor
            Line line = new Line(head_node.origin(), headAnchor());
            Point directions = line.findDirections(headCtrl());
            if(directions.getX() < 0){
                // only move the anchor point if the ctrl has been moved past
                // the perpendicular of the radial arm.
                float mag = (float) (2 * Math.PI * padded_radius / 20);
                UnitVector d = new UnitVector(headAnchor(), headCtrl());
                Point new_anchor = new Point(curve.x2 + mag * d.x, curve.y2 + mag * d.y);

                // update the anchor (this forces the location back onto the
                // circle)
                line = new Line(head_node.origin(), new_anchor);
                curve.x2 = head_node.origin().getX() + padded_radius * line.d.x;
                curve.y2 = head_node.origin().getY() + padded_radius * line.d.y;
            }

            updateArrowhead();
        }
        else updateArrowheadSafely();
    }

    /**
     * Move the head anchor, and if necessary the head ctrl point. The head
     * anchor is attached to a circle, and the anchor-ctrl line shouldn't breach
     * that circle. Also, because of the arrowhead, the anchor should end
     * outside of the circle radius
     * 
     * @param origional_configuration
     *            The configuration of the curve at the initiation of movement.
     * @param mouse
     *            The current mouse position.
     */
    public void moveHeadAnchor(Configuration origional_configuration, Point mouse){
        // pad the circle
        int padded_radius = head_node.getR() + ArrowHead.SHORT_HEAD_LENGTH;

        // update the anchor
        Line line = new Line(head_node.origin(), mouse);
        line.rotate(origional_configuration.selection_error_fix);
        curve.x2 = head_node.origin().getX() + padded_radius * line.d.x;
        curve.y2 = head_node.origin().getY() + padded_radius * line.d.y;

        if((origional_configuration.state_mask & SWT.CTRL) != SWT.CTRL){
            // update the ctrl point
            line = new Line(head_node.origin(), headAnchor());
            Point directions = line.findDirections(headCtrl());
            if(directions.getX() < 0){
                // only move the ctrl point if it has fallen past the
                // perpendicular of the radial arm.
                float mag = Geometric.distance(origional_configuration.head_anchor,
                        origional_configuration.head_ctrl);
                UnitVector d = line.d.newPerpendicular();
                if(directions.getY() > 0){
                    d.reverse();
                } // make sure we position it in the proper direction.
                curve.ctrlx2 = curve.x2 + mag * d.x;
                curve.ctrly2 = curve.y2 + mag * d.y;
            }
        }

        updateArrowhead();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Manipulation (self loop)
    // ///////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Adjust the curve co-ordinates for a change in the UnitVector or Node
     * position.
     */
    public void recalculateSelfLoop(){
        Point p1 = self_loop_direction.newPoint(tail_node.getR(), tail_node.origin(), 25);
        Point p2 = self_loop_direction.newPoint(tail_node.getR() + 40, tail_node.origin(), 32);
        Point p3 = self_loop_direction.newPoint(tail_node.getR() + 35, tail_node.origin(), -28);
        Point p4 = self_loop_direction.newPoint(tail_node.getR() + ArrowHead.SHORT_HEAD_LENGTH,
                tail_node.origin(), -25);
        Point p5 = self_loop_direction.newPoint(tail_node.getR(), tail_node.origin(), -25);
        setTailAnchor(p1);
        setTailCtrl(p2);
        setHeadCtrl(p3);
        setHeadAnchor(p4);
        UnitVector d = self_loop_direction.newRotatedByDegrees(-25 + 180);
        arrowhead.update(d, p5);
    }

    /**
     * Adjust the curve co-ordinates for a change in the UnitVector.
     * 
     * @param origional_configuration
     *            The configuration of the curve at the initiation of movement.
     * @param mouse
     *            The current mouse position.
     */
    public void moveSelfLoop(Configuration origional_configuration, Point mouse){
        self_loop_direction = new UnitVector(tail_node.origin(), mouse);
        self_loop_direction.rotateByDegrees(origional_configuration.selection_error_fix);
        recalculateSelfLoop();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Manipulation (automatic)
    // ///////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Move the arrowhead to compensate for curve repositioning. (tip-anchor,
     * anchor-ctrl, tip-ctrl) forms a triangle. Use vector addition to find a
     * UnitVector in the tip-ctrl direction. Move the anchor to a point on that
     * direction HeadLength from the tip.
     */
    private void updateArrowhead(){
        // reposition the arrowhead radially
        UnitVector d = new UnitVector(head_node.origin(), headAnchor());
        Point tip = new Point(head_node.origin().getX() + head_node.getR() * d.x, head_node
                .origin().getY()
                + head_node.getR() * d.y);
        d.reverse();
        arrowhead.update(d, tip);
        // compensate for kink
        d = new UnitVector(headCtrl(), arrowhead.tip());
        arrowhead.update(d, arrowhead.tip());

        UnitVector tip_to_nock = arrowhead.direction();
        tip_to_nock.reverse();
        curve.x2 = arrowhead.xcoords[ArrowHead.TIP] + ArrowHead.SHORT_HEAD_LENGTH * tip_to_nock.x;
        curve.y2 = arrowhead.ycoords[ArrowHead.TIP] + ArrowHead.SHORT_HEAD_LENGTH * tip_to_nock.y;
    }

    /**
     * Move the arrowhead to compensate for curve repositioning. (tip-anchor,
     * anchor-ctrl, tip-ctrl) forms a triangle. Use vector addition to find a
     * UnitVector in the tip-ctrl direction. Move the anchor to a point on that
     * direction HeadLength from the tip.
     */
    private void updateArrowheadSafely(){
        UnitVector d = new UnitVector(headCtrl(), arrowhead.tip());
        arrowhead.update(d, arrowhead.tip());

        UnitVector tip_to_nock = arrowhead.direction();
        tip_to_nock.reverse();
        curve.x2 = arrowhead.xcoords[ArrowHead.TIP] + ArrowHead.SHORT_HEAD_LENGTH * tip_to_nock.x;
        curve.y2 = arrowhead.ycoords[ArrowHead.TIP] + ArrowHead.SHORT_HEAD_LENGTH * tip_to_nock.y;
    }

    /**
     * Record the origional relativistic layout.
     */
    public void initiateNodeMovement(){
        // origional distance between anchors
        float anchors_mag = Geometric.distance(tailAnchor(), headAnchor());

        // useful directions
        UnitVector tail_to_head = new UnitVector(tail_node.origin(), head_node.origin());
        UnitVector head_to_tail = tail_to_head.newReversed();

        // tail config
        Point on_tail_bisector = new Point(tail_node.origin(), tail_to_head, 100);
        tail_anchor_angle = -Geometric.calculateRadians(on_tail_bisector, tail_node.origin(),
                tailAnchor());
        tail_ctrl_angle = -Geometric.calculateRadians(on_tail_bisector, tail_node.origin(),
                tailCtrl());
        tail_ctrl_radius = Geometric.distance(tail_node.origin(), tailCtrl());

        // find the fraction of the distance along the bisector direction that
        // this ctrl traverses
        Point tail_ctrl_vector = tailCtrl().minus(tailAnchor());
        float tail_ctrl_flat_mag = Geometric.dotProduct(tail_ctrl_vector, tail_to_head)
                / (Geometric.magnitude(tail_ctrl_vector) * (float) Math.cos(tail_ctrl_angle));
        tail_ctrl_mag_factor = Geometric.floatBounds(tail_ctrl_flat_mag / anchors_mag);
        // find the ratio of flat_mag to radial_mag for this ctrl
        tail_ctrl_ratio = Geometric.floatBounds(tail_ctrl_flat_mag / tail_ctrl_radius);

        // head config
        Point on_head_bisector = new Point(head_node.origin(), head_to_tail, 100);
        head_anchor_angle = -Geometric.calculateRadians(on_head_bisector, head_node.origin(),
                headAnchor());
        head_ctrl_angle = -Geometric.calculateRadians(on_head_bisector, head_node.origin(),
                headCtrl());
        head_ctrl_radius = Geometric.distance(head_node.origin(), headCtrl());

        // find the fraction of the distance along the bisector direction that
        // this ctrl traverses
        Point head_ctrl_vector = headCtrl().minus(headAnchor());
        float head_ctrl_flat_mag = Geometric.dotProduct(head_ctrl_vector, head_to_tail)
                / (Geometric.magnitude(head_ctrl_vector) * (float) Math.cos(head_ctrl_angle));
        head_ctrl_mag_factor = Geometric.floatBounds(head_ctrl_flat_mag / anchors_mag);
        // find the ratio of flat_mag to radial_mag for this ctrl
        head_ctrl_ratio = Geometric.floatBounds(head_ctrl_flat_mag / head_ctrl_radius);
    }

    /**
     * Recalculate the parameters of this Edge based on a node movement,
     * attempting to approximate the origional layout.
     * 
     * @param origional_configuration
     *            The configuration of the Node at the initiation of movement.
     * @param mouse
     *            The current mouse position.
     * @param fixed_circle
     *            The node of the fixed circle.
     * @param moving_circle
     *            The node of the moving circle.
     */
    public void updateNodeMovement(Configuration origional_configuration, Point mouse,
            Node fixed_circle, Node moving_circle){
        // useful directions
        UnitVector tail_to_head = new UnitVector(tail_node.origin(), head_node.origin());
        UnitVector head_to_tail = tail_to_head.newReversed();

        // directions of anchor/ctrl points from their respective origins
        UnitVector tail_anchor_direction = tail_to_head.newRotatedByRadians(tail_anchor_angle);
        UnitVector tail_ctrl_direction = tail_to_head.newRotatedByRadians(tail_ctrl_angle);
        UnitVector head_ctrl_direction = head_to_tail.newRotatedByRadians(head_ctrl_angle);
        UnitVector head_anchor_direction = head_to_tail.newRotatedByRadians(head_anchor_angle);

        // calculate new anchor positions
        setTailAnchor(tail_node.origin().newPoint(tail_anchor_direction, tail_node.getR()));
        setHeadAnchor(head_node.origin().newPoint(head_anchor_direction,
                tail_node.getR() + ArrowHead.SHORT_HEAD_LENGTH));

        // new distance between anchors
        float new_anchors_mag = Geometric.distance(tailAnchor(), headAnchor());

        // estimate new ctrl radius'
        float new_tail_ctrl_radius = Geometric.floatBounds((new_anchors_mag * tail_ctrl_mag_factor)
                / tail_ctrl_ratio);
        float new_head_ctrl_radius = Geometric.floatBounds((new_anchors_mag * head_ctrl_mag_factor)
                / head_ctrl_ratio);

        // this gives bad behaviour if a ctrl radius is greater than the
        // distance between the two nodes.
        float node_distance = Geometric.distance(tail_node.origin(), head_node.origin())
                - tail_node.getR() - head_node.getR();
        if(node_distance < 0) node_distance = 0;
        if(new_tail_ctrl_radius > node_distance) new_tail_ctrl_radius = node_distance;
        if(new_head_ctrl_radius > node_distance) new_head_ctrl_radius = node_distance;

        // calculate new ctrl positions
        int tail_radius_adjust = 0;
        int head_radius_adjust = 0;
        if(moving_circle == tail_node) tail_radius_adjust = tail_node.getR()
                - origional_configuration.radius;
        else if(moving_circle == head_node) head_radius_adjust = head_node.getR()
                - origional_configuration.radius;

        setTailCtrl(tail_node.origin().newPoint(tail_ctrl_direction,
                new_tail_ctrl_radius + tail_radius_adjust));
        setHeadCtrl(head_node.origin().newPoint(head_ctrl_direction,
                new_head_ctrl_radius + head_radius_adjust));

        // this algorithm can result in the ctrl points being pushed back inside
        // the node. we must prevent that.
        Line ctrl_adjust = null;
        if(Geometric.distance(tail_node.origin(), tailCtrl()) < tail_node.getR() + 2
                * ANCHOR_RADIUS){
            ctrl_adjust = new Line(tail_node.origin(), tailCtrl());
            curve.ctrlx1 = tail_node.origin().getX() + (tail_node.getR() + 2 * ANCHOR_RADIUS)
                    * ctrl_adjust.d.x;
            curve.ctrly1 = tail_node.origin().getY() + (tail_node.getR() + 2 * ANCHOR_RADIUS)
                    * ctrl_adjust.d.y;
        }
        if(Geometric.distance(head_node.origin(), headCtrl()) < head_node.getR() + 2
                * ANCHOR_RADIUS + ArrowHead.SHORT_HEAD_LENGTH){
            ctrl_adjust = new Line(head_node.origin(), headCtrl());
            curve.ctrlx2 = head_node.origin().getX()
                    + (head_node.getR() + 4 * ANCHOR_RADIUS + ArrowHead.SHORT_HEAD_LENGTH)
                    * ctrl_adjust.d.x;
            curve.ctrly2 = head_node.origin().getY()
                    + (head_node.getR() + 4 * ANCHOR_RADIUS + ArrowHead.SHORT_HEAD_LENGTH)
                    * ctrl_adjust.d.y;
        }

        updateArrowhead();
    }

    /**
     * Attempt to automatically layout this curve to a default configuration.
     * 
     * @param rise
     *            The rise factor of the control points from the bisector
     *            between node origins
     * @param angle
     *            The angle start-origin-nodeline and end-origin-nodeline.
     */
    public void calculateCurve(float rise, float angle){
        if(SELECTED_METHOD == 1){
            calculateCurve1(rise, angle);
            return;
        }
        // create a unit vector from center of circle1 to center of circle2
        UnitVector d = new UnitVector(tail_node.origin(), head_node.origin());
        UnitVector dperp = d.newPerpendicular();

        // create rotated copies of the vector.
        UnitVector d1 = d.newRotatedByDegrees(angle);
        UnitVector d2 = d.newRotatedByDegrees(-angle);

        // create the start and end points, by moving along d1 and d2 away
        // from the centers of the nodes
        int padded_radius = head_node.getR() + ArrowHead.SHORT_HEAD_LENGTH;
        setTailAnchor(new Point((int) Math.round(tail_node.getX() + tail_node.getR() * d1.x),
                (int) Math.round(tail_node.getY() + tail_node.getR() * d1.y)));
        setHeadAnchor(new Point((int) Math.round(head_node.getX() - padded_radius * d2.x),
                (int) Math.round(head_node.getY() - padded_radius * d2.y)));

        // re-create a unit vector from tailAnchor to headAnchor
        d = new UnitVector(tailAnchor(), headAnchor());
        dperp = d.newPerpendicular();

        // create the control points, by moving run in the d direction and
        // rise in the dperp direction from the origin anchors
        float run = Geometric.distance(tailAnchor(), headAnchor()) / 3;
        setTailCtrl(new Point((int) Math.round(tailAnchor().getX() + run * d.x - rise * dperp.x),
                (int) Math.round(tailAnchor().getY() + run * d.y - rise * dperp.y)));
        setHeadCtrl(new Point((int) Math.round(headAnchor().getX() - run * d.x - rise * dperp.x),
                (int) Math.round(headAnchor().getY() - run * d.y - rise * dperp.y)));

        updateArrowhead();
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
        curve.x1 = curve.x1 + x;
        curve.ctrlx1 = curve.ctrlx1 + x;
        curve.ctrlx2 = curve.ctrlx2 + x;
        curve.x2 = curve.x2 + x;
        curve.y1 = curve.y1 + y;
        curve.ctrly1 = curve.ctrly1 + y;
        curve.ctrly2 = curve.ctrly2 + y;
        curve.y2 = curve.y2 + y;
        arrowhead.translateAll(x, y);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Manipulation (miscelaneous)
    // ////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Put the arrowhead on the other end. This should only be used for non self
     * loops that are in a custom configuration. This manipulation is not
     * accurate hence re-doing is will not undo it.
     * 
     * @param reconfigure
     *            Whether or not to adjust the curve and arrowhead points
     */
    public void reverseDirection(boolean reconfigure){
        // swap the nodes to preserve direction relation to naming convention
        Node n = tail_node;
        tail_node = head_node;
        head_node = n;

        if(reconfigure){
            // swap the curve points to preserve direction relation to naming
            // convention
            float t = 0;
            t = curve.x1;
            curve.x1 = curve.x2;
            curve.x2 = t;
            t = curve.y1;
            curve.y1 = curve.y2;
            curve.y2 = t;
            t = curve.ctrlx1;
            curve.ctrlx1 = curve.ctrlx2;
            curve.ctrlx2 = t;
            t = curve.ctrly1;
            curve.ctrly1 = curve.ctrly2;
            curve.ctrly2 = t;

            Line line = null;

            // snap the tail to the node
            line = new Line(tail_node.origin(), tailAnchor());
            curve.x1 = tail_node.origin().getX() + tail_node.getR() * line.d.x;
            curve.y1 = tail_node.origin().getY() + tail_node.getR() * line.d.y;

            // pad the head for the arrowhead
            int padded_radius = head_node.getR() + ArrowHead.SHORT_HEAD_LENGTH;
            line = new Line(head_node.origin(), headAnchor());
            curve.x2 = head_node.origin().getX() + padded_radius * line.d.x;
            curve.y2 = head_node.origin().getY() + padded_radius * line.d.y;
            updateArrowhead();
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Access
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Access a copy of the tail anchor point.
     * 
     * @return A copy of the tail anchor point.
     */
    public Point tailAnchor(){
        return new Point((int) Math.round(curve.x1), (int) Math.round(curve.y1));
    }

    /**
     * Access a copy of the tail ctrl point.
     * 
     * @return A copy of the tail ctrl point.
     */
    public Point tailCtrl(){
        return new Point((int) Math.round(curve.ctrlx1), (int) Math.round(curve.ctrly1));
    }

    /**
     * Access a copy of the head ctrl point.
     * 
     * @return A copy of the head ctrl point.
     */
    public Point headCtrl(){
        return new Point((int) Math.round(curve.ctrlx2), (int) Math.round(curve.ctrly2));
    }

    /**
     * Access a copy of the head anchor point.
     * 
     * @return A copy of the head anchor point.
     */
    public Point headAnchor(){
        return new Point((int) Math.round(curve.x2), (int) Math.round(curve.y2));
    }

    /**
     * Access a copy of the angle from head to origin to bisector.
     * 
     * @return A copy of the angle from head to origin to bisector.
     */
    public float headAnchorAngle(){
        UnitVector head_to_tail = new UnitVector(head_node.origin(), tail_node.origin());
        Point on_head_bisector = new Point(head_node.origin(), head_to_tail, 100);
        return -Geometric.calculateRadians(on_head_bisector, head_node.origin(), headAnchor());
    }

    /**
     * Access a copy of the midpoint of the curve.
     * 
     * @return A copy of the midpoint of the curve.
     */
    public Point midpoint(){
        return calculateBezierPoint((float) 0.5);
    }

    /**
     * Access a copy of the self loop anchor point.
     * 
     * @return A copy of the self loop anchor point.
     */
    public Point selfLoopAnchor(){
        return calculateBezierPoint(SELF_LOOP_ANCHOR_POSITION);
    }

    /**
     * Access a copy of the self loop direction as a point.
     * 
     * @return A copy of the self loop direction as a point.
     */
    public Point selfLoopDirection(){
        return self_loop_direction != null ? new Point(self_loop_direction.x, self_loop_direction.y)
                : new Point(0, -1);
    }

    /**
     * Set the tail anchor point.
     * 
     * @param p
     *            A new value for the tail anchor point.
     */
    public void setTailAnchor(Point p){
        curve.x1 = p.getX();
        curve.y1 = p.getY();
    }

    /**
     * Set the tail ctrl point.
     * 
     * @param p
     *            A new value for the tail ctrl point.
     */
    public void setTailCtrl(Point p){
        curve.ctrlx1 = p.getX();
        curve.ctrly1 = p.getY();
    }

    /**
     * Set the head ctrl point.
     * 
     * @param p
     *            A new value for the head ctrl point.
     */
    public void setHeadCtrl(Point p){
        curve.ctrlx2 = p.getX();
        curve.ctrly2 = p.getY();
    }

    /**
     * Set the head anchor point.
     * 
     * @param p
     *            A new value for the head anchor point.
     */
    public void setHeadAnchor(Point p){
        curve.x2 = p.getX();
        curve.y2 = p.getY();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Queries
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

     /**
     * Calculate a point on a bezier curve.
     * 
     * @param t
     *            The parameter of the curve's equation, 0..1 gives placement on
     *            curve.
     * @return A point on this curve.
     */
    public Point calculateBezierPoint(float t){
        float x = (1 - t) * (1 - t) * (1 - t) * curve.x1 + 3 * t * (1 - t) * (1 - t) * curve.ctrlx1
        + 3 * t * t * (1 - t) * curve.ctrlx2 + t * t * t * curve.x2;
        float y = (1 - t) * (1 - t) * (1 - t) * curve.y1 + 3 * t * (1 - t) * (1 - t) * curve.ctrly1
                + 3 * t * t * (1 - t) * curve.ctrly2 + t * t * t * curve.y2;
        return new Point(x, y);
    }

    /**
     * Checks if a mouse-click landed in any of the regions of this object. If
     * the mouse-click isn't in any of the regions of this object then
     * Edge.R_NONE is returned.
     * 
     * @param mouse
     *            The current mouse position.
     * @param padded
     *            Whether or not we are testing for an exact hit or a nearby
     *            hit.
     * @return An Edge region value describing where the mouse landed.
     */
    public int isLocatedSelfLoop(Point mouse, boolean padded){
        // check the midpoint fictional anchor.
        int test_radius = padded ? 2 * ANCHOR_RADIUS : ANCHOR_RADIUS;
        return selfLoopAnchor().isInsideCircle(test_radius, mouse) ? Edge.R_LOOP : Edge.R_NONE;
    }

    /**
     * Checks if a mouse-click landed in any of the regions of this object. If
     * the mouse-click isn't in any of the regions of this object then
     * Edge.R_NONE is returned.
     * 
     * @param mouse
     *            The current mouse position.
     * @param padded
     *            Whether or not we are testing for an exact hit or a nearby
     *            hit.
     * @return An Edge region value describing where the mouse landed.
     */
    public int isLocatedAnchors(Point mouse, boolean padded){
        int test_radius = padded ? 4 * ANCHOR_RADIUS : ANCHOR_RADIUS;
        if(tailAnchor().isInsideCircle(test_radius, mouse)) return Edge.R_TAIL_ANCHOR;
        if(tailCtrl().isInsideCircle(test_radius, mouse)) return Edge.R_TAIL_CTRL;
        if(headCtrl().isInsideCircle(test_radius, mouse)) return Edge.R_HEAD_CTRL;
        if(headAnchor().isInsideCircle(test_radius, mouse)) return Edge.R_HEAD_ANCHOR;
        return Edge.R_NONE;
    }

    /**
     * Checks if a mouse-click landed in any of the regions of this object. If
     * the mouse-click isn't in any of the regions of this object then
     * Edge.R_NONE is returned.
     * 
     * @param mouse
     *            The current mouse position.
     * @param padded
     *            Whether or not we are testing for an exact hit or a nearby
     *            hit.
     * @return An Edge region value describing where the mouse landed.
     */
    public int isLocatedArrowhead(Point mouse, boolean padded){
        // check the arrow head
        if(arrowhead.isLocated(mouse)) return Edge.R_ARROWHEAD;
        return Edge.R_NONE;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Alternate Methodology
    // //////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
     * Lenko
     */
    /**
     * Attempt to automatically layout this curve to a default configuration.
     * 
     * @param rise_factor
     *            The rise factor of the control points from the bisector
     *            between node origins
     * @param angle
     *            The angle start-origin-nodeline and end-origin-nodeline.
     */
    private void calculateCurve1(float rise_factor, float angle){
        // create a unit vector from center of circle1 to center of circle2
        UnitVector d = new UnitVector(tail_node.origin(), head_node.origin());
        UnitVector dperp = d.newPerpendicular();

        // create the start and end points, by moving along d1 and d2 away from
        // the centers of the nodes
        Point p1 = tail_node.origin();
        Point p4 = head_node.origin();

        // create the control points, by moving along d1 and d2 away from the
        // centers of the nodes and perpendicular to these directions to achieve
        // rise
        float mag = (float) Math.sqrt(Math.pow(p1.getX() - p4.getX(), 2)
                + Math.pow(p1.getY() - p4.getY(), 2)) / 3; 
        // one third of the distance
        // between the end points
        // (used to move away from
        // the centers of the nodes)

        // used to rise from the bisector of the nodes
        float rise = rise_factor != 0 ? mag / rise_factor : 0;

        Point p2 = new Point((int) Math.round(tail_node.getX() + mag * d.x - rise * dperp.x),
                (int) Math.round(tail_node.getY() + mag * d.y - rise * dperp.y));
        Point p3 = new Point((int) Math.round(head_node.getX() - mag * d.x - rise * dperp.x),
                (int) Math.round(head_node.getY() - mag * d.y - rise * dperp.y));

        setTailAnchor(p1);
        setTailCtrl(p2);
        setHeadCtrl(p3);
        setHeadAnchor(p4);
        updateArrowhead();
    }
}
