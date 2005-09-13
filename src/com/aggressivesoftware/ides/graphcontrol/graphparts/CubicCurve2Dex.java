/*
 * This entrie class is the work of Lenko
 */

/*
 * Created on Nov 24, 2004
 */
package com.aggressivesoftware.ides.graphcontrol.graphparts;

/**
 * A cubic parametric curve segment specified with float coordinates and extended
 * capabilities: parameter-specified subdivision and computation of bounds on curve length.
 * 
 * @author Lenko Grigorov
 */
public class CubicCurve2Dex extends java.awt.geom.CubicCurve2D.Float
{

	/**
	 * Constructs and initializes a CubicCurve with coordinates (0, 0, 0, 0, 0, 0).
	 */
	public CubicCurve2Dex() {
		super();
	}

	/**
	 * Constructs and initializes a CubicCurve2D from the specified coordinates.
	 */
	public CubicCurve2Dex(float x1, float y1, float ctrlx1, float ctrly1,
			float ctrlx2, float ctrly2, float x2, float y2) {
		super(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2);
	}

	/**
	 * Subdivides this cubic curve and stores the resulting two subdivided curves
	 * into the left and right curve parameters.
	 * Either or both of the left and right objects may be the same as this object
	 * or null.
	 * <p>The splitting works as follows:<br>
	 * Let M be the matrix:
	 * <pre>
	 *  1  0  0  0
	 * -3  3  0  0
	 *  3 -6  3  0
	 * -1  3 -3  1
	 * </pre>
	 * and M^-1 would be
	 * <pre>
	 * 1   0   0   0
	 * 1  1/3  0   0
	 * 1  2/3 1/3  0
	 * 1   1   1   1
	 * </pre>
	 * Then a Bezier curve can be written as:
	 * <pre>
	 * (1  t  t^2  t^3) M ([x1,y1]
	 *                     [ctrlx1,ctrly1]
	 *                     [ctrlx2,ctrly2]
	 *                     [x2,y2])
	 * </pre>
	 * for t from 0 to 1. Thus, if we want to split the curve at t=T, we get:
	 * <pre>
	 * (1  Tt  (Tt)^2  (Tt)^3) M ([x1,y1]
	 *                            [ctrlx1,ctrly1]
	 *                            [ctrlx2,ctrly2]
	 *                            [x2,y2])
	 * </pre>
	 * or
	 * <pre>
	 * (1  t  t^2  t^3) (1   0   0   0    M ([x1,y1]
	 *                   0   T   0   0       [ctrlx1,ctrly1]
	 *                   0   0   T^2 0       [ctrlx2,ctrly2]
	 *                   0   0   0   T^3)    [x2,y2])
	 * </pre>
	 * or
	 * <pre>
	 * (1  t  t^2  t^3) M S1 ([x1,y1]
	 *                        [ctrlx1,ctrly1]
	 *                        [ctrlx2,ctrly2]
	 *                        [x2,y2])
	 * </pre>
	 * where S1 equals:
	 * <pre>
	 * M^-1  (1   0   0   0    M
	 *        0   T   0   0
	 *        0   0   T^2 0
	 *        0   0   0   T^3)
	 * </pre>
	 * or
	 * <pre>
	 *    1           0          0        0
	 *   1-T          T          0        0
	 * (1-T)^2     2T-2T^2      T^2       0
	 * (1-T)^3  3T-6T^2+3T^3  3T^2-3T^3  T^3
	 * </pre>
	 * Thus, if we multiply S1 by the vector:
	 * <pre>
	 * ([x1,y1]
	 *  [ctrlx1,ctrly1]
	 *  [ctrlx2,ctrly2]
	 *  [x2,y2])
	 * </pre>
	 * we will obtain the four points for the first part of the split curve.
	 * <p>Similarly, for the second part of the curve we have: 
	 * <pre>
	 * (1  T+(1-T)t  (T+(1-T)t)^2  (T+(1-T)t)^3) M ([x1,y1]
	 *                                              [ctrlx1,ctrly1]
	 *                                              [ctrlx2,ctrly2]
	 *                                              [x2,y2])
	 * </pre>
	 * or
	 * <pre>
	 * (1  t  t^2  t^3) (1   T     T^2       T^3     M ([x1,y1]
	 *                   0  1-T  2T(1-T)  3T^2(1-T)     [ctrlx1,ctrly1]
	 *                   0   0   (1-T)^2  3T(1-T)^2     [ctrlx2,ctrly2]
	 *                   0   0      0      (1-T)^3 )    [x2,y2])
	 * </pre>
	 * or
	 * <pre>
	 * (1  t  t^2  t^3) M S2 ([x1,y1]
	 *                        [ctrlx1,ctrly1]
	 *                        [ctrlx2,ctrly2]
	 *                        [x2,y2])
	 * </pre>
	 * where S2 equals:
	 * <pre>
	 * M^-1  (1   T     T^2       T^3      M
	 *        0  1-T  2T(1-T)  3T^2(1-T)
	 *        0   0   (1-T)^2  3T(1-T)^2
	 *        0   0      0      (1-T)^3 )
	 * </pre>
	 * or
	 * <pre>
	 * (1-T)^3  3T-6T^2+3T^3  3T^2-3T^3  T^3
	 *    0       (1-T)^2      2T-2T^2   T^2
	 *    0          0           1-T      T
	 *    0          0            0       1
	 * </pre>
	 * Thus, if we multiply S2 by the vector:
	 * <pre>
	 * ([x1,y1]
	 *  [ctrlx1,ctrly1]
	 *  [ctrlx2,ctrly2]
	 *  [x2,y2])
	 * </pre>
	 * we will obtain the four points for the second part of the split curve.
	 * @param part1	the cubic curve object for storing for the left or first half of the subdivided curve
	 * @param part2	the cubic curve object for storing for the right or second half of the subdivided curve
	 * @param t	the cubic curve parameter where the splitting has to occur (0 to 1) 
	 */
	public void subdivide(CubicCurve2Dex part1, CubicCurve2Dex part2, float t)
	{
		float t2=t*t;
		float t3=t2*t;
		float ti=1-t;
		float ti2=ti*ti;
		float ti3=ti2*ti;
		float tt2=2*t-2*t2;
		float tt3=3*t2-3*t3;
		float ttt=3*t-6*t2+3*t3;
		
		if(part1!=null)
			part1.setCurve(
				x1,
				y1,
				ti*x1+t*ctrlx1,
				ti*y1+t*ctrly1,
				ti2*x1+tt2*ctrlx1+t2*ctrlx2,
				ti2*y1+tt2*ctrly1+t2*ctrly2,
				ti3*x1+ttt*ctrlx1+tt3*ctrlx2+t3*x2,
				ti3*y1+ttt*ctrly1+tt3*ctrly2+t3*y2
				);
		if(part2!=null)
			part2.setCurve(
				ti3*x1+ttt*ctrlx1+tt3*ctrlx2+t3*x2,
				ti3*y1+ttt*ctrly1+tt3*ctrly2+t3*y2,
				ti2*ctrlx1+tt2*ctrlx2+t2*x2,
				ti2*ctrly1+tt2*ctrly2+t2*y2,
				ti*ctrlx2+t*x2,
				ti*ctrly2+t*y2,
				x2,
				y2
				);
	}
	
	/**
	 * Returns an upper bound on the length of the curve.
	 * @return	upper bound on the length of the curve
	 */
	public float maxLength()
	{
		return 2*(float)(
				Math.sqrt((x1-ctrlx1)*(x1-ctrlx1)+(y1-ctrly1)*(y1-ctrly1))+
				Math.sqrt((ctrlx2-ctrlx1)*(ctrlx2-ctrlx1)+(ctrly2-ctrly1)*(ctrly2-ctrly1))+
				Math.sqrt((x2-ctrlx2)*(x2-ctrlx2)+(y2-ctrly2)*(y2-ctrly2))
				);
	}

	/**
	 * Returns a lower bound on the length of the curve.
	 * @return	lower bound on the length of the curve
	 */
	public float minLength()
	{
		return (float)(
				Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2))
				);
	}
}
