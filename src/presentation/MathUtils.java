package presentation;

import java.awt.geom.Point2D;

public class MathUtils {

	/**
	 * Returns the norm of the given vector.
	 * 
	 * @param vector
	 * @return norm (length) of vector
	 */
	public static double norm(Point2D.Float vector) {
		return Math.sqrt(Math.pow(vector.x, 2) + Math.pow(vector.y, 2));		
	}
	
	/**
	 * 
	 * @param p
	 * @return the unit direction vector for p.
	 */
	public static Point2D.Float unit(Point2D.Float p){
		float n = (float)norm(p);
		Point2D.Float p1 = new Point2D.Float(p.x/n, p.y/n);
		return p1;
	}

	/** 
	 * @param v vector with origin at (0,0) and given direction
	 * @return the vector perpendicular to v (rotated 90 degrees clockwise)
	 */
	public static Point2D.Float perp(Point2D.Float v){
		return new Point2D.Float(v.y, -v.x);		
	}
	
	/**
	 * @param v vector with origin at (0,0) and given direction
	 * @param r radians
	 * @return the vector resulting from rotating v by r radians
	 */
	public static Point2D.Float rotate(Point2D.Float v, double r) {
		float c = (float)Math.cos(r);
		float s = (float)Math.sin(r);
		return new Point2D.Float(v.x*c + v.y*s, v.y*c - v.x*s);	
	}
	
	/** 
	 * @param v vector with origin at (0,0) and given direction
	 * @param s the scalar 
	 * @return the result of scaling v by s
	 */
	public static Point2D.Float scale(Point2D.Float v, float s) {		
		return new Point2D.Float(Math.round(v.x * s), Math.round(v.y * s));		
	}

}
