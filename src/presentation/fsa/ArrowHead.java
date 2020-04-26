/*
 * Created April 2006
 */
package presentation.fsa;

import java.awt.Polygon;
import java.awt.geom.Point2D;

import presentation.Geometry;

/**
 * A filled polygon in the shape of an arrowhead.
 * 
 * @author Helen Bretzke
 */
@SuppressWarnings("serial")
public class ArrowHead extends Polygon {

    // location of the nock (where the shaft meets the head)
    private Point2D.Float basePt = new Point2D.Float(0, 0);

    // distance from nock to tip
    public static final int HEAD_LENGTH = 9;

    // distance from nock to tip
    public static final int SHORT_HEAD_LENGTH = 7;

    /**
     * Centre axis vector of default direction.
     */
    public static final Point2D.Float axis = new Point2D.Float(0, 1);

    /**
     * Construct a default arrowhead at location (0,0) and orientation in direction
     * (0,1) ie. pointing down.
     */
    public ArrowHead() {
        reset();
    }

    /**
     * Construct an ArrowHead shape oriented in direction <code>dir</code> with nock
     * located at <code>base</code>.
     * 
     * @param dir  A unit direction from nock to tip.
     * @param base the coordinates of the nock
     */
    public ArrowHead(Point2D.Float dir, Point2D.Float base) {
        setLocationAndDirection(base, dir);
    }

    /**
     * Construct an ArrowHead shape oriented in direction <code>dir</code> with nock
     * located at <code>base</code>.
     * 
     * @param dir  A unit direction from nock to tip.
     * @param base the coordinates of the nock
     */
    public ArrowHead(Point2D dir, Point2D base) {
        setLocationAndDirection(new Point2D.Float((float) base.getX(), (float) base.getY()),
                new Point2D.Float((float) dir.getX(), (float) dir.getY()));
    }

    /**
     * Set my location to <code>base</code> and rotate all of my points to orient
     * with direction vector <code>dir</code>. NOTE this method is not used when
     * drawing the arrowhead on edges since problems occurred with flicker. When
     * drawing arrowhead on canvas use AffineTransform on the Graphics context.
     * FIXME Since the default orientation is stored in memory but the arrowhead is
     * only rotated when rendered, checking for intersections will not work
     * properly.
     * 
     * @param base base point of the arrow head
     * @param dir  unit direction vector
     */
    public void setLocationAndDirection(Point2D.Float base, Point2D.Float dir) {
        reset();
        this.basePt = base;
        double alpha = Geometry.angleFrom(axis, dir);
        for (int i = 0; i < npoints; i++) {
            Point2D.Float temp = new Point2D.Float(xpoints[i], ypoints[i]);
            temp = Geometry.rotate(temp, alpha);
            xpoints[i] = (int) (temp.x + base.x);
            ypoints[i] = (int) (temp.y + base.y);
        }
    }

    /**
     * Sets arrowhead to default location (0,0) and orientation in direction (0,1)
     * ie. pointing down.
     */
    @Override
    public void reset() {
        super.reset();
        basePt = new Point2D.Float(0, 0);
        // compute default arrowhead pointing down
        addPoint(0, HEAD_LENGTH);
        addPoint(-4, -3);
        addPoint((int) basePt.x, (int) basePt.y);
        addPoint(4, -3);
    }

    /**
     * Returns the base point or location of nock of this arrowhead.
     * 
     * @return the base point
     */
    protected Point2D.Float getBasePt() {
        return basePt;
    }
}
