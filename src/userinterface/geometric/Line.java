/*
 * Created on Nov 11, 2004
 */
package userinterface.geometric;

/**
 * This class represents a line between two points in cartesian space. It
 * provides many methods for queries on the relationship between other points in
 * space and itself. For example, it can be used to find the perpendicular
 * distance from itself to an arbitrary point.
 * 
 * @author Michael Wood
 */
public class Line {
    /**
     * The first point on the line.
     */
    public Point p1 = null;

    /**
     * The second point on the line.
     */
    public Point p2 = null;

    /**
     * A UnitVector in the direction from p1 to p2.
     */
    public UnitVector d = null;

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Line construction //////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construct the Line.
     * 
     * @param p1 A first point on the line.
     * @param p2 A second point on the line.
     */
    public Line(Point p1, Point p2) {
        this.p1 = p1.getCopy();
        this.p2 = p2.getCopy();
        constructLine();
    }

    /**
     * Construct the Line.
     * 
     * @param p A first point on the line.
     * @param d The direction of the line.
     */
    public Line(Point p, UnitVector d) {
        this.p1 = p.getCopy();
        float x = p.getX() + 100 * d.x;
        float y = p.getY() + 100 * d.y;
        this.p2 = new Point(x, y);
        constructLine();
    }

    /**
     * Construct the Line.
     * 
     * @param x1 The x co-ordinate of a fisrt point on the line.
     * @param y1 The y co-ordinate of a first point on the line.
     * @param x2 The x co-ordinate of a second point on the line.
     * @param y2 The y co-ordinate of a second point on the line.
     */
    public Line(int x1, int y1, int x2, int y2) {
        p1 = new Point(x1, y1);
        p2 = new Point(x2, y2);
        constructLine();
    }

    /**
     * Construct the parameters of the Line.
     */
    private void constructLine() {
        d = new UnitVector(p1, p2);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Manipulation ///////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Change this line to a new line perpendicular to the origional, and
     * passing through the specified point. After the operation, the specified
     * point becomes p1 and p2 is created 10 units along the new unit vector.
     * 
     * @param point The point (1 or 2) through which the perpendicular will pass.
     */
    public void makePerpendicular(int point) {
        d.perpendicularize();
        int size = 100;

        if (point == 1) {
            p2 = new Point(p1.getX() + d.x * size, p1.getY() + d.y * size);
        } else {
            p1 = p2;
            p2 = new Point(p1.getX() + d.x * size, p1.getY() + d.y * size);
        }
        p1 = new Point(p1.getX() - d.x * size, p1.getY() - d.y * size);

        constructLine();
    }

    /**
     * Rotate this line about p1 by the specified number of degrees, keeping p2
     * at a constant magnitude from p1.
     * 
     * @param degrees The number of degrees to rotate the line.
     */
    public void rotate(float degrees) {
        float mag = Geometric.distance(p1, p2);
        d.rotateByDegrees(degrees);
        p2.setX(p1.getX() + (int) Math.round(mag * d.x));
        p2.setY(p1.getY() + (int) Math.round(mag * d.y));
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Queries ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Test if the given point is within the given perpendicular distance of
     * this line.
     * 
     * @param q The query point.
     * @param distance Quantifies the meaning of near.
     * @return true if q is near this line.
     */
    public boolean isNear(Point q, int distance) {
        UnitVector d_perp = d.newPerpendicular();
             
        // check if q is in between the endpoints
        
        // recall d is in the direction from p1 to p2.
        // if the constant from (p1 + k*d) is negative or (p2 + k*d) is positive than the query point
        // is not between p1 and p2
        
        if ((solveDirectionEquation(p2, q, d, d_perp) > 0) || (solveDirectionEquation(p1, q, d, d_perp) < 0)){
            return false;
        }
        
        // test the perpendicular distance
        return (perpendicularDistance(q) < distance);

    }

    /**
     * Find the directions of the query point from p2 of this line with this
     * line's UnitVector as the positive directgion
     * 
     * @param q The query point.
     * @return A point (k1,k2) where the only values are +1 and -1, and these
     *         represent the directions.
     */
    public Point findDirections(Point q) {
        return findDirections(p2, q, d);
    }

    /**
     * Find the directions of the query point from the origin point. o + k1 * d =
     * p + k2 * dPerp. The sign of k1 gives the direction of p from o along the
     * d vector. The sign of k2 gives the direction of p from o along the dPerp
     * vector.
     * 
     * @param o The origin point.
     * @param q The query point.
     * @param d The direction from the origin that is positive.
     * @return A point (k1,k2) where the only values are +1 and -1, and these
     *         represent the directions discussed above.
     */
    private Point findDirections(Point o, Point q, UnitVector d) {
        Point result = new Point(0, 0);
        UnitVector d_perp = d.newPerpendicular();

        result.setX(solveDirectionEquation(o, q, d, d_perp));
        result.setY(solveDirectionEquation(q, o, d_perp, d));

        return result;
    }

    /**
     * Find the directions of the query point from the origin point. p1 + k1 *
     * d1 = p2 + k2 * d2. The sign of k1 gives the direction of p2 from p1 along
     * the d1. This method solvs for k1, to solve for k2, simply swap p1,p2 and
     * d1,d2.
     * 
     * Note: this is really two equations with two unknowns: p1.x + k1 * d1.x =
     * p2.x + k2 * d2.x p1.y + k1 * d1.y = p2.y + k2 * d2.y
     * 
     * 
     * @param p1 A point.
     * @param p2 A point.
     * @param d1 A direction.
     * @param d2 A direction perpendicular to d1.
     * @return +1 if k1 is positive, -1 if k1 is negative or zero.
     */
    private int solveDirectionEquation(Point p1, Point p2, UnitVector d1, UnitVector d2) {
        if (solveDirectionEquationComplete(p1, p2, d1, d2) > 0) {
            return 1;
        }
        return -1;   
    }

    /**
     * Find the directions of the query point from the origin point. p1 + k1 *
     * d1 = p2 + k2 * d2. The sign of k1 gives the direction of p2 from p1 along
     * the d1. This method solvs for k1, to solve for k2, simply swap p1,p2 and
     * d1,d2.
     * 
     * Note: this is really two equations with two unknowns: p1.x + k1 * d1.x =
     * p2.x + k2 * d2.x p1.y + k1 * d1.y = p2.y + k2 * d2.y
     * 
     * @param p1 A point.
     * @param p2 A point.
     * @param d1 A direction.
     * @param d2 A direction perpendicular to d1.
     * @return k1.
     */
    private float solveDirectionEquationComplete(Point p1, Point p2, UnitVector d1, UnitVector d2) {
        float term1 = (d2.x / d2.y);
        if (term1 == Float.POSITIVE_INFINITY) {
            term1 = Float.MAX_VALUE;
        }
        if (term1 == Float.NEGATIVE_INFINITY) {
            term1 = -Float.MAX_VALUE;
        }
        float term2 = (d1.x - (d1.y * d2.x) / d2.y);
        if (term2 == Float.POSITIVE_INFINITY) {
            term2 = Float.MAX_VALUE;
        }
        if (term2 == Float.NEGATIVE_INFINITY) {
            term2 = -Float.MAX_VALUE;
        }
        float k = (p2.getX() - p1.getX() + term1 * (p1.getY() - p2.getY())) / term2;
        return k;
    }

    /**
     * Calculate the perpendicualr distance from the given query point to this
     * line.
     * 
     * @param q The query point.
     * @return The perpendicualr distance from the given query point to this
     *         line.
     */
    public float perpendicularDistance(Point q) {
        UnitVector d_perp = d.newPerpendicular();
        return (float) Math.abs(solveDirectionEquationComplete(q, p1, d_perp, d));
    }
}