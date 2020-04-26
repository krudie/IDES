/*
 * This entire class is the work of Lenko except for getCoefficients, equals and
 * toString.
 */

/*
 * Created on Nov 24, 2004
 */
package presentation;

import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

/**
 * A cubic parametric curve segment defined by float coordinates and having
 * extended capabilities: parameter-specified subdivision and computation of
 * bounds on curve length.
 * 
 * @author Lenko Grigorov
 * @author Helen Bretzke
 */
public class CubicParamCurve2D extends CubicCurve2D.Float {

    private static final long serialVersionUID = 2275044234810964347L;

    /**
     * Constructs and initializes a CubicCurve with coordinates (0, 0, 0, 0, 0, 0).
     */
    public CubicParamCurve2D() {
        super();
    }

    /**
     * Constructs and initializes a CubicCurve2D from the specified coordinates for
     * the four control (inflection) points.
     */
    public CubicParamCurve2D(float x1, float y1, float ctrlx1, float ctrly1, float ctrlx2, float ctrly2, float x2,
            float y2) {
        super(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2);
    }

    /**
     * Constructs a CubicCurve2D and sets the location of the endpoints and
     * controlpoints of this curve to the same as those in the specified
     * CubicCurve2D.
     * 
     * @param curve the curve whose control points to copy
     */
    public CubicParamCurve2D(CubicParamCurve2D curve) {
        setCurve(curve);
    }

    /**
     * Subdivides this cubic curve and stores the resulting two subdivided curves
     * into the left and right curve parameters. Either or both of the left and
     * right objects may be the same as this object or null.
     * <p>
     * The splitting works as follows:<br>
     * Let M be the matrix:
     * 
     * <pre>
     *    1  0  0  0
     *   -3  3  0  0
     *    3 -6  3  0
     *   -1  3 -3  1
     * </pre>
     * 
     * and M^-1 would be
     * 
     * <pre>
     *   1   0   0   0
     *   1  1/3  0   0
     *   1  2/3 1/3  0
     *   1   1   1   1
     * </pre>
     * 
     * Then a Bezier curve can be written as:
     * 
     * <pre>
     *   (1  t  t&circ;2  t&circ;3) M ([x1,y1]
     *                       [ctrlx1,ctrly1]
     *                       [ctrlx2,ctrly2]
     *                       [x2,y2])
     * </pre>
     * 
     * for t from 0 to 1. Thus, if we want to split the curve at t=T, we get:
     * 
     * <pre>
     *   (1  Tt  (Tt)&circ;2  (Tt)&circ;3) M ([x1,y1]
     *                              [ctrlx1,ctrly1]
     *                              [ctrlx2,ctrly2]
     *                              [x2,y2])
     * </pre>
     * 
     * or
     * 
     * <pre>
     *   (1  t  t&circ;2  t&circ;3) (1   0   0   0    M ([x1,y1]
     *                     0   T   0   0       [ctrlx1,ctrly1]
     *                     0   0   T&circ;2 0       [ctrlx2,ctrly2]
     *                     0   0   0   T&circ;3)    [x2,y2])
     * </pre>
     * 
     * or
     * 
     * <pre>
     *   (1  t  t&circ;2  t&circ;3) M S1 ([x1,y1]
     *                          [ctrlx1,ctrly1]
     *                          [ctrlx2,ctrly2]
     *                          [x2,y2])
     * </pre>
     * 
     * where S1 equals:
     * 
     * <pre>
     *   M&circ;-1  (1   0   0   0    M
     *          0   T   0   0
     *          0   0   T&circ;2 0
     *          0   0   0   T&circ;3)
     * </pre>
     * 
     * or
     * 
     * <pre>
     *      1           0          0        0
     *     1-T          T          0        0
     *   (1-T)&circ;2     2T-2T&circ;2      T&circ;2       0
     *   (1-T)&circ;3  3T-6T&circ;2+3T&circ;3  3T&circ;2-3T&circ;3  T&circ;3
     * </pre>
     * 
     * Thus, if we multiply S1 by the vector:
     * 
     * <pre>
     *   ([x1,y1]
     *    [ctrlx1,ctrly1]
     *    [ctrlx2,ctrly2]
     *    [x2,y2])
     * </pre>
     * 
     * we will obtain the four points for the first part of the split curve.
     * <p>
     * Similarly, for the second part of the curve we have:
     * 
     * <pre>
     *   (1  T+(1-T)t  (T+(1-T)t)&circ;2  (T+(1-T)t)&circ;3) M ([x1,y1]
     *                                                [ctrlx1,ctrly1]
     *                                                [ctrlx2,ctrly2]
     *                                                [x2,y2])
     * </pre>
     * 
     * or
     * 
     * <pre>
     *   (1  t  t&circ;2  t&circ;3) (1   T     T&circ;2       T&circ;3     M ([x1,y1]
     *                     0  1-T  2T(1-T)  3T&circ;2(1-T)     [ctrlx1,ctrly1]
     *                     0   0   (1-T)&circ;2  3T(1-T)&circ;2     [ctrlx2,ctrly2]
     *                     0   0      0      (1-T)&circ;3 )    [x2,y2])
     * </pre>
     * 
     * or
     * 
     * <pre>
     *   (1  t  t&circ;2  t&circ;3) M S2 ([x1,y1]
     *                          [ctrlx1,ctrly1]
     *                          [ctrlx2,ctrly2]
     *                          [x2,y2])
     * </pre>
     * 
     * where S2 equals:
     * 
     * <pre>
     *   M&circ;-1  (1   T     T&circ;2       T&circ;3      M
     *          0  1-T  2T(1-T)  3T&circ;2(1-T)
     *          0   0   (1-T)&circ;2  3T(1-T)&circ;2
     *          0   0      0      (1-T)&circ;3 )
     * </pre>
     * 
     * or
     * 
     * <pre>
     *   (1-T)&circ;3  3T-6T&circ;2+3T&circ;3  3T&circ;2-3T&circ;3  T&circ;3
     *      0       (1-T)&circ;2      2T-2T&circ;2   T&circ;2
     *      0          0           1-T      T
     *      0          0            0       1
     * </pre>
     * 
     * Thus, if we multiply S2 by the vector:
     * 
     * <pre>
     *   ([x1,y1]
     *    [ctrlx1,ctrly1]
     *    [ctrlx2,ctrly2]
     *    [x2,y2])
     * </pre>
     * 
     * we will obtain the four points for the second part of the split curve.
     * 
     * @param part1 the cubic curve object for storing for the left or first half of
     *              the subdivided curve
     * @param part2 the cubic curve object for storing for the right or second half
     *              of the subdivided curve
     * @param t     the cubic curve parameter where the splitting has to occur (0 to
     *              1)
     */
    public void subdivide(CubicParamCurve2D part1, CubicParamCurve2D part2, float t) {
        float t2 = t * t;
        float t3 = t2 * t;
        float ti = 1 - t;
        float ti2 = ti * ti;
        float ti3 = ti2 * ti;
        float tt2 = 2 * t - 2 * t2;
        float tt3 = 3 * t2 - 3 * t3;
        float ttt = 3 * t - 6 * t2 + 3 * t3;

        if (part1 != null) {
            part1.setCurve(x1, y1, ti * x1 + t * ctrlx1, ti * y1 + t * ctrly1, ti2

                    * x1 + tt2 * ctrlx1 + t2 * ctrlx2, ti2 * y1 + tt2 * ctrly1 + t2 * ctrly2,
                    ti3 * x1 + ttt * ctrlx1 + tt3 * ctrlx2 + t3 * x2, ti3 * y1 + ttt * ctrly1 + tt3 * ctrly2 + t3 * y2);
        }
        if (part2 != null) {
            part2.setCurve(ti3 * x1 + ttt * ctrlx1 + tt3 * ctrlx2 + t3 * x2,
                    ti3 * y1 + ttt * ctrly1 + tt3 * ctrly2 + t3 * y2, ti2 * ctrlx1 + tt2 * ctrlx2 + t2 * x2,
                    ti2 * ctrly1 + tt2 * ctrly2 + t2 * y2, ti * ctrlx2 + t * x2, ti * ctrly2 + t * y2, x2, y2);
        }
    }

    /**
     * Returns the segment of this curve between the given pair of parameters.
     * Precondition 0 <= tStart < tEnd <= 1
     * 
     * @param tStart in [0, tEnd)
     * @param tEnd   in (tStart, 1]
     * @return the segment of this curve between the given pair of parameters
     */
    public CubicParamCurve2D getSegment(float tStart, float tEnd) {
        // DEBUG
        /*
         * if(! (0 <= tStart && tStart < tEnd && tEnd <=1) )
         * System.err.println("tStart= " + tStart + " tEnd = " + tEnd );
         */

        // assert(0 <= tStart && tStart < tEnd && tEnd <=1);
        // if(! (0 <= tStart && tStart < tEnd && tEnd <=1 ) ){
        // throw new RuntimeException("precondition violated: ! (0 <= tStart <
        // tEnd <= 1)");
        // }
        CubicParamCurve2D left = new CubicParamCurve2D();
        CubicParamCurve2D right = new CubicParamCurve2D();
        subdivide(left, right, tEnd);
        left.subdivide(null, right, tStart / tEnd);
        return right;
    }

    /**
     * Returns the point on the curve at the given parameter.
     * 
     * @param t in [0,1]
     * @return the point on the curve at the given parameter
     */
    public Point2D.Float getPointAt(float t) {
        CubicParamCurve2D left = new CubicParamCurve2D();
        subdivide(left, null, t);
        return new Point2D.Float((float) left.getP2().getX(), (float) left.getP2().getY());
    }

    /**
     * Returns an upper bound on the length of the curve.
     * 
     * @return upper bound on the length of the curve
     */
    public float maxLength() {
        return 2 * (float) (Math.sqrt((x1 - ctrlx1) * (x1 - ctrlx1) + (y1 - ctrly1) * (y1 - ctrly1))
                + Math.sqrt((ctrlx2 - ctrlx1) * (ctrlx2 - ctrlx1) + (ctrly2 - ctrly1) * (ctrly2 - ctrly1))
                + Math.sqrt((x2 - ctrlx2) * (x2 - ctrlx2) + (y2 - ctrly2) * (y2 - ctrly2)));
    }

    /**
     * Returns a lower bound on the length of the curve.
     * 
     * @return lower bound on the length of the curve
     */
    public float minLength() {
        return (float) (Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
    }

    /**
     * Returns the coefficients for the parametric equation of this 2D cubic curve.
     * 
     * @return a_x, b_x, c_x a_y, b_y, c_y}
     */
    public float[][] getCoefficients() {
        // make these into public variables to simplify use of this class
        float a_x, b_x, c_x, a_y, b_y, c_y;
        float[][] coeffs = new float[2][3];
        c_x = 3 * (ctrlx1 - x1);
        b_x = 3 * (ctrlx2 - ctrlx1) - c_x;
        a_x = x2 - x1 - c_x - b_x;
        coeffs[0][0] = a_x;
        coeffs[0][1] = b_x;
        coeffs[0][2] = c_x;

        c_y = 3 * (ctrly1 - y1);
        b_y = 3 * (ctrly2 - ctrly1) - c_y;
        a_y = y2 - y1 - c_y - b_y;
        coeffs[1][0] = a_y;
        coeffs[1][1] = b_y;
        coeffs[1][2] = c_y;

        return coeffs;
    }

    /**
     * Returns true iff this curve has the same control points as <code>o</code> .
     * Precondition: <code>o</code> is a <code>CubicCurve2D</code>
     * 
     * @return true iff this curve has the same control points as <code>o</code>
     */
    @Override
    public boolean equals(Object o) {
        CubicCurve2D other = (CubicCurve2D) o;
        return getP1().equals(other.getP1()) && getP2().equals(other.getP2()) && getCtrlP1().equals(other.getCtrlP1())
                && getCtrlP2().equals(other.getCtrlP2());
    }

    /**
     * Returns a string representing the control points of this curve. Formatted by
     * square brackets with each point comma-delimited.
     * 
     * @return a string representing the control points of this curve.
     */
    @Override
    public String toString() {
        return "[" + getP1() + ", " + getCtrlP1() + ", " + getCtrlP2() + ", " + getP2() + "]";
    }
}
