/*
 * Created on Nov 11, 2004
 */
package userinterface.graphcontrol.graphparts;

import userinterface.geometric.Geometric;
import userinterface.geometric.Point;

/**
 * This class remembers the origional configuration of a Node or an Edge. It is
 * used in movement, etc. for the purpose of preventing loss of resolution due
 * to multiple calculations over small intervals.
 * 
 * @author Michael Wood
 */
public class Configuration{
    /**
     * The origin of the object.
     */
    public Point origin = null;

    /**
     * The radius of the object.
     */
    public int radius = 0;

    /**
     * The tip of the arrow head.
     */
    public Point arrow_tip = null;

    /**
     * The curve points.
     */
    public Point tail_anchor = null, tail_ctrl = null, head_ctrl = null, head_anchor = null,
            label_displacement = null, selection_target = null;

    /**
     * The mouse-click origin of movement.
     */
    public Point movement_origin = null;

    /**
     * The number of degrees by which the movement origin was off the movement
     * target.
     */
    public float selection_error_fix = 0;

    /**
     * The state mask of the mouse event that created the configuration
     */
    public int state_mask = 0;

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Configuration construction/////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construct the Configuration.
     * 
     * @param origin The origin of the Node.
     * @param arrow_tip The tip of the ArrowHead.
     * @param movement_origin The mouse-click origin of movement.
     * @param radius The radius of the Node.
     */
    public Configuration(Point origin, Point arrow_tip, Point movement_origin, int radius){
        this.origin = origin.getCopy();
        this.arrow_tip = arrow_tip.getCopy();
        this.movement_origin = movement_origin.getCopy();
        this.radius = radius;
    }

    /**
     * Construct the Configuration.
     * 
     * @param origin The origin of the circle of interest (the node connected to
     *            the currently manipulated anchor/ctrl points).
     * @param tail_anchor The tail anchor point.
     * @param tail_ctrl The tail ctrl point.
     * @param head_ctrl The head ctrl point.
     * @param head_anchor The head anchor point.
     * @param label_displacement The displacement of the top-left of the label
     *            from the mid-point of the curve
     * @param movement_origin The mouse-click origin of movement.
     * @param selection_target The target that the mouse-click was supposed to
     *            hit.
     * @param state_mask The state mask of the mouse event that created the
     *            configuration.
     */
    public Configuration(Point origin, Point tail_anchor, Point tail_ctrl, Point head_ctrl,
            Point head_anchor, Point label_displacement, Point movement_origin,
            Point selection_target, int state_mask){
        this.origin = origin.getCopy();
        this.tail_anchor = tail_anchor.getCopy();
        this.tail_ctrl = tail_ctrl.getCopy();
        this.head_ctrl = head_ctrl.getCopy();
        this.head_anchor = head_anchor.getCopy();
        this.label_displacement = label_displacement.getCopy();
        this.movement_origin = movement_origin.getCopy();
        this.selection_target = selection_target.getCopy();
        this.state_mask = state_mask;

        selection_error_fix = -Geometric
                .calculateDegrees(movement_origin, origin, selection_target);
    }
}