/*
 * Created on Sep 21, 2004
 */
package userinterface.graphcontrol.graphparts;

import userinterface.geometric.Point;
import userinterface.geometric.UnitVector;
import userinterface.graphcontrol.Drawer;

/**
 * This is a filled polygon in the shape of an arrowhead.
 * 
 * @author Michael Wood
 */
public class ArrowHead {
    /**
     * The dimensions of the arrow head.
     * 
     * tang \\ \\\\ nock ]>>>>> tip //// // tang
     * 
     * HEAD_LENGTH = nock to tip. TANG_X = distance along shaft from tip to
     * projection of tang on shaft. TANG_Y = distance perpendicluar to shaft
     * from projection of tang on shaft to tang.
     */
    public static final int HEAD_LENGTH = 9, TANG_X = 13, TANG_Y = 5,
            SHORT_HEAD_LENGTH = 7;

    /**
     * Constants for accessing the xcoords and ycoords arrays.
     */
    public static final int TIP = 0, NOCK = 2;

    /**
     * The co-ordinates of the ArrowHead. tip,tang,nock,tip (clockwise).
     */
    public int[] xcoords = new int[4], ycoords = new int[4];

    /**
     * Whether or not the ArrowHead should be drawn.
     */
    public boolean visible = false;

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ArrowHead Construction
    // /////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construct the ArrowHead.
     */
    public ArrowHead() {
        visible = false;
    }

    /**
     * Construct the ArrowHead.
     * 
     * @param d
     *            A unit direction from nock to tip.
     * @param tip_x
     *            The x co-ordinate of the tip.
     * @param tip_y
     *            The y co-ordinate of the tip.
     */
    public ArrowHead(UnitVector d, int tip_x, int tip_y) {
        update(d, tip_x, tip_y);
    }

    /**
     * Construct the ArrowHead.
     * 
     * @param d
     *            A unit direction from nock to tip.
     * @param tip
     *            The tip point.
     */
    public ArrowHead(UnitVector d, Point tip) {
        update(d, tip);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Access
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Access the tip of the ArrowHead.
     * 
     * @return A point representing the tip of the arrowhead.
     */
    public Point tip() {
        return new Point(xcoords[ArrowHead.TIP], ycoords[ArrowHead.TIP]);
    }

    /**
     * Access the nock of the ArrowHead.
     * 
     * @return A point representing the nock of the arrowhead.
     */
    public Point nock() {
        return new Point(xcoords[ArrowHead.NOCK], ycoords[ArrowHead.NOCK]);
    }

    /**
     * Calculates the direction of this arrowhead.
     * 
     * @return The direction of this arrowhead.
     */
    public UnitVector direction() {
        return new UnitVector(nock(), tip());
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Drawing
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Draw the ArrowHead.
     * 
     * @param drawer
     *            The Drawer that will handle the drawing.
     */
    public void draw(Drawer drawer) {
        if (visible) {
            drawer.drawPolygon(xcoords, ycoords, 4);
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Manipulation
    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Update the ArrowHead.
     * 
     * @param d
     *            A unit direction from nock to tip.
     * @param tip_x
     *            The x co-ordinate of the tip.
     * @param tip_y
     *            The y co-ordinate of the tip.
     */
    public void update(UnitVector d, int tip_x, int tip_y) {
        update(d, new Point(tip_x, tip_y));
    }

    /**
     * Update the ArrowHead.
     * 
     * @param d
     *            A UnitVector from nock to tip.
     * @param tip
     *            A Point representing the tip.
     */
    public void update(UnitVector d, Point tip) {
        UnitVector d_perp = d.newPerpendicular();
        xcoords[0] = tip.x;
        ycoords[0] = tip.y;
        xcoords[2] = tip.x - (int) Math.round(ArrowHead.HEAD_LENGTH * d.x);
        ycoords[2] = tip.y - (int) Math.round(ArrowHead.HEAD_LENGTH * d.y);
        xcoords[1] = (int) Math.round(tip.x - ArrowHead.TANG_X * d.x
                + ArrowHead.TANG_Y * d_perp.x);
        ycoords[1] = (int) Math.round(tip.y - ArrowHead.TANG_X * d.y
                + ArrowHead.TANG_Y * d_perp.y);
        xcoords[3] = (int) Math.round(tip.x - ArrowHead.TANG_X * d.x
                - ArrowHead.TANG_Y * d_perp.x);
        ycoords[3] = (int) Math.round(tip.y - ArrowHead.TANG_X * d.y
                - ArrowHead.TANG_Y * d_perp.y);
        visible = true;
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
        xcoords[0] = xcoords[0] + x;
        xcoords[1] = xcoords[1] + x;
        xcoords[2] = xcoords[2] + x;
        xcoords[3] = xcoords[3] + x;
        ycoords[0] = ycoords[0] + y;
        ycoords[1] = ycoords[1] + y;
        ycoords[2] = ycoords[2] + y;
        ycoords[3] = ycoords[3] + y;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Queries
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Used to determine if a mouse-click should select this ArrowHead.
     * 
     * @param x
     *            x co-ordinate of the mouse.
     * @param y
     *            y co-ordinate of the mouse.
     * @return true if this ArrowHead should be selected by this mouse click.
     */
    public boolean isLocated(int x, int y) {
        return isLocated(new Point(x, y), false);
    }

    /**
     * Used to determine if a mouse-click should select this ArrowHead.
     * 
     * @param mouse
     *            The current mouse position.
     * @return true if this ArrowHead should be selected by this mouse click.
     */
    public boolean isLocated(Point mouse) {
        return isLocated(mouse, false);
    }

    /**
     * Used to determine if a mouse-click should select this ArrowHead.
     * 
     * @param mouse
     *            The current mouse position.
     * @param padded
     *            Whether or not we are testing for an exact hit or a nearby
     *            hit.
     * @return true if this ArrowHead should be selected by this mouse click.
     */
    public boolean isLocated(Point mouse, boolean padded) {
        int test_radius = ArrowHead.HEAD_LENGTH;
        if (padded) {
            test_radius = 2 * ArrowHead.HEAD_LENGTH;
        }

        if (visible) {
            // this is a simple circle test
            if (Math.pow(xcoords[ArrowHead.NOCK] - mouse.x, 2)
                    + Math.pow(ycoords[ArrowHead.NOCK] - mouse.y, 2) < Math
                    .pow(test_radius, 2)) {
                return true;
            }
        }
        return false;
    }
}
