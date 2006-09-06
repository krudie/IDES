package presentation;

import java.awt.Point;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Point2D.Float;

public class Geometry {

	/**
		 * @param v1 source vector
		 * @param v2 target vector (within a scalar)
		 * @return the angle to rotate v1 to make it a scalar of v2
		 */
		public static double angleFrom(Point2D.Float v1, Point2D.Float v2){
			
			 /* a · b  =  cos ? 
	
			 For non-unit vectors: 
			 (1) normalize each vector, 
			 (2) compute the dot product, 
			 (3) take the arc cos to get the angle.
	
			 */
			
			// convert everything to double precision
			Point2D.Double v1d, v2d;
			v1d = new Point2D.Double(v1.x, v1.y);
			v2d = new Point2D.Double(v2.x, v2.y);
						
			double n1 = norm(v1d);
			double n2 = norm(v2d);
			
			if(n1 == 0 || n2 == 0)
			{
				return 0;
			}
			
			double dot = dot(v1d, v2d);	
			double cosA = dot/(n1*n2);
			
			// DEBUG
			//if(cosA < -1 || cosA > 1)	System.err.println(cosA);
//			assert(cosA >= -1 && cosA <= 1);
			
			double e = 0.000000000001;
			if(Math.abs(cosA) > 1 && Math.abs(1 - cosA) < e )
			{				
				if(cosA < 0)
				{
					cosA = -1.0;
				}else{
					cosA = 1.0;
				}
			}
			double a = Math.acos(cosA);
	
			e = 0.01;
			// DEBUG
			if(java.lang.Double.isNaN(a))
			{
				System.err.println("angle is NaN: cosA is " + cosA);
			}
			//assert(!java.lang.Double.isNaN(a));
					
			// Try sending unit v1 to unit v2
			Point2D.Double test = rotate(unit(v1d), a);
			//double distance = test.distance(v2d); 
			//if(distance < e){
//			double dx = Math.abs(test.x - v2.x);
//			double dy = Math.abs(test.y - v2.y);
//			if(dx < e && dy < e)
			if(unit(v2).distance(test) < e)
			{
				return a;
			}else{
				return -a;
			}		
		}

	/**
	 * @param curve
	 * @return the midpoint of curve
	 */
	public static Point2D midpoint(CubicCurve2D curve) {
		CubicCurve2D.Float left = new CubicCurve2D.Float(); 
	    curve.subdivide(left, new CubicCurve2D.Float());	        
	    return left.getP2();
	}

	/**
	 * Returns the norm of the given vector.
	 * 
	 * @param axis
	 * @return norm (length) of vector
	 */
	public static double norm(Point2D v) {
		return Math.sqrt(Math.pow(v.getX(), 2) + Math.pow(v.getY(), 2));		
	}
	
	/**
	 * @param v
	 * @return
	 */
	private static double norm(Point2D.Double v) {
		return Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2));		
	}

	/**  
	 * TODO specify and enforce precision to unit length
	 * 
	 * @param v
	 * @return the unit direction vector for vector from (0,0) to v.
	 */
	public static Point2D.Float unit(Point2D.Float v){
		double n = norm(v);
		if(n != 0){
			Point2D.Float v1 = new Point2D.Float((float)(v.x/n), (float)(v.y/n));			
			return v1;
		}
		return v;		
	}

	/**
	 * TODO specify and enforce precision to unit length
	 * 
	 * @param v
	 * @return the unit direction vector for vector from (0,0) to v.
	 */
	private static Double unit(Point2D.Double v) {
		double n = norm(v);
		if(n != 0){
			Point2D.Double v1 = new Point2D.Double((float)(v.x/n), (float)(v.y/n));			
			return v1;
		}
		return v;		
	}

	/**
	 * @param p1
	 * @param p2
	 * @return the unit direction vector for vector from p1 to p2 i.e. norm(p2 - p1)
	 */
	public static Point2D.Float unitDirectionVector(Point2D.Float p1, Point2D.Float p2){
		return unit(subtract(p2, p1));
	}
	

	/**
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static Float unitDirectionVector(Point2D p1, Point2D p2) {		
		return unit(subtract(p2, p1));
	}

	/** 
	 * @param v vector with origin at (0,0) and given direction
	 * @return the vector perpendicular to v (rotated 90 degrees clockwise)
	 */
	public static Point2D.Float perp(Point2D.Float v){
		return new Point2D.Float(v.y, -v.x);		
	}
	
	/**
	 * @param point2D vector
	 * @param r radians
	 * @return the vector resulting from rotating v by r radians
	 */
	public static Point2D.Float rotate(Point2D v, double r) {
		float c = (float)Math.cos(r);
		float s = (float)Math.sin(r);
		return new Point2D.Float((float)(v.getX()*c - v.getY()*s), (float)(v.getY()*c + v.getX()*s));	
	}
	
	/**
	 * @param v vector
	 * @param r radians
	 * @return the vector resulting from rotating v by r radians
	 */
	private static Point2D.Double rotate(Point2D.Double v, double r) {
		double c = Math.cos(r);
		double s = Math.sin(r);
		return new Point2D.Double(v.x*c - v.y*s, v.y*c + v.x*s);	
	}

	/** 
	 * @param v vector with origin at (0,0) and given direction
	 * @param d the scalar 
	 * @return the result of scaling v by s
	 */
	public static Point2D.Float scale(Point2D v, double s) {		
		return new Point2D.Float((float)(v.getX() * s), (float)(v.getY() * s));		
	}	
		
	
	public static Point2D.Float subtract(Point2D.Float a, Point2D.Float v){
		return new Point2D.Float(a.x - v.x, a.y - v.y);		
	}

	public static Point2D.Float subtract(Point a, Point b) {		
		return new Point2D.Float(a.x - b.x, a.y - b.y);	
	}

	/**
	 * @param a
	 * @param b
	 * @return
	 */
	public static Float subtract(Point2D a, Point2D b) {
		return new Point2D.Float((float)(a.getX() - b.getX()), (float)(a.getY() - b.getY()));	
	}

	/** 
	 * @param a point
	 * @param v direction vector
	 * @return a new point a + v
	 */
	public static Point2D.Float add(Point2D.Float a, Point2D.Float v){
		return new Point2D.Float(a.x + v.x, a.y + v.y);		
	}
		
	public static Point2D add(Point2D p1, Point2D p2) {		
		return new Point2D.Float((float)(p1.getX() + p2.getX()), (float)(p1.getY() + p2.getY()));
	}

	public static double dot(Point2D.Float v1, Point2D.Float v2){		
		return v1.x * v2.x + v1.y * v2.y;
	}
	
	private static double dot(Point2D.Double v1, Point2D.Double v2) {		
		return v1.x * v2.x + v1.y * v2.y;
	}

	/**
	 * Given two points, find the slope (Y2-Y1)/(X2-X1) of the line between them
	 * @param p1 point 1
	 * @param p2 point 2
	 * @return the slope of the line
	 */
	public static float slope(Point2D.Float p1,Point2D.Float p2)
	{
		return (p2.y-p1.y)/(p2.x-p1.x);
	}
	
	/**
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static double slope(Point2D p1, Point2D p2) {
		return (p2.getY()-p1.getY())/(p2.getX()-p1.getX());
	}
	
	public static Point2D.Float translate(Point2D.Float p, float x, float y)
	{
		return new Point2D.Float(p.x+x,p.y+y);
	}

	/**
	 * @param p
	 * @param x
	 * @param y
	 * @return new point resulting from translating p by x and y
	 */
	public static Float translate(Point2D p, double x, double y) {		
		return new Point2D.Float( (float)(p.getX() + x), (float)(p.getY() + y) );
	}	
}
