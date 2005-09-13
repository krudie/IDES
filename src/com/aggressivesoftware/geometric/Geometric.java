/*
 * Created on Nov 16, 2004
 */
package com.aggressivesoftware.geometric;

/**
 * This class provides various methods for geometric computation,
 * such as the distance between two points.
 * 
 * @author Michael Wood
 */
public final class Geometric 
{	
	/**
     * Calculate the angle between arm1 and arm2 through vertex.
     * Clockwise from arm1 to arm2 is positive.
     * 
     * @param	arm1	The Point that defines the first arm of the angle.
     * @param	vertex	The Point that defines the vertex of the angle.
     * @param	arm2	The Point that defines the second arm of the angle.
     * 
     * @return	The angle (in radians) between arm1 and arm2 through vertex.
     */
	public static float calculateRadians(Point arm1, Point vertex, Point arm2)
	{
		float a1 = absoluteRadians(vertex,arm1);
		float a2 = absoluteRadians(vertex,arm2);
		return a2-a1;
	}

	/**
     * Calculate the angle between arm1 and arm2 through vertex.
     * Clockwise from arm1 to arm2 is positive.
     * 
     * @param	arm1	The Point that defines the first arm of the angle.
     * @param	vertex	The Point that defines the vertex of the angle.
     * @param	arm2	The Point that defines the second arm of the angle.
     * 
     * @return	The angle (in degrees) between arm1 and arm2 through vertex.
     */
	public static float calculateDegrees(Point arm1, Point vertex, Point arm2)
	{
		return (float)Math.toDegrees(calculateRadians(arm1,vertex,arm2));
	}		
	
	/**
     * Calculate the angle where the first arm is from the vertex down (which is the positive y direction)
     * and the second arm is from the vertex to the given arm point
     * and positive is in the clockwise direction.
     * 
     * @param	vertex	The Point that defines the vertex of the angle.
     * @param	arm		The Point that defines the arm of the angle that is not the vertical arm.
     * 
     * @return	The absolute angle in radians.
     */
	public static float absoluteRadians(Point vertex, Point arm)
	{
		float a = 0;
		if      (arm.x == vertex.x) { if (arm.y > vertex.y) { a = 0;                   } else { a = (float)Math.PI;       } }
		else if (arm.y == vertex.y) { if (arm.x < vertex.x) { a = (float)(Math.PI/2) ; } else { a = (float)(3*Math.PI/2); } }
		else
		{
			a = (float)Math.atan((float)Math.abs(arm.y - vertex.y)/(float)Math.abs(arm.x - vertex.x)); 
			     if (arm.y > vertex.y && arm.x > vertex.x) { a = a + (float)(3*Math.PI/2); }
			else if (arm.y > vertex.y && arm.x < vertex.x) { a = (float)(Math.PI/2) - a; }
			else if (arm.y < vertex.y && arm.x > vertex.x) { a = (float)(3*Math.PI/2) - a; }
			else if (arm.y < vertex.y && arm.x < vertex.x) { a = a + (float)(Math.PI/2); }
		}
		return a;
	}

	/**
	 * Calculateds the distance between a point and the origin.
	 * This is also the length of a vector.
	 * 
	 * @param	x	The x co-ordinate of the point.
	 * @param	y	The y co-ordinate of the point.
	 * @return	The distance between the point and the origin (aka vector length).
	 */
	public static float magnitude(float x, float y)
	{
		float mag = (float)Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
		if (mag==0) { mag = Float.MIN_VALUE; }
		return floatBounds(mag);
	}	
	
	/**
	 * Calculateds the distance between a point and the origin.
	 * This is also the length of a vector.
	 * 
	 * @param	x	The x co-ordinate of the point.
	 * @param	y	The y co-ordinate of the point.
	 * @return	The distance between the point and the origin (aka vector length).
	 */
	public static int magnitude(int x, int y)
	{
		return (int)Math.round(Math.sqrt(Math.pow(x,2) + Math.pow(y,2)));
	}
	
	/**
	 * Calculateds the distance between a point and the origin.
	 * This is also the length of a vector.
	 * 
	 * @param	p	The point.
	 * @return	The distance between the point and the origin (aka vector length).
	 */
	public static float magnitude(Point p)
	{
		return magnitude(p.x,p.y);
	}	
	
	/**
	 * Calculateds the distance between two points.
	 * 
	 * @param	x1	The x co-ordinate of the first point.
	 * @param	y1	The y co-ordinate of the first point.
	 * @param	x2	The x co-ordinate of the second point.
	 * @param	y2	The y co-ordinate of the second point.
	 * @return	The distance between the two points.
	 */
	public static float distance(int x1, int y1, int x2, int y2)
	{
		float mag = (float)Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2,2));
		if (mag==0) { mag = Float.MIN_VALUE; }
		return floatBounds(mag);
	}

	/**
	 * Calculates the distance between two points.
	 * 
	 * @param	p1	The first point.
	 * @param	p2	The second point.
	 * @return	The distance between the two points.
	 */
	public static float distance(Point p1, Point p2)
	{
		return distance(p1.x,p1.y,p2.x,p2.y);
	}
	
	/**
	 * Calculates the dot product of the two vectors.
	 * 
	 * @param	p	A point representing a vector from the origin.
	 * @param	u	A unit vector.
	 * @return	The dot product of the two vectors.
	 */
	public static float dotProduct(Point p, UnitVector u)
	{
		return p.x*u.x + p.y*u.y;
	}
	
	/**
	 * Prevent NaN problems.
	 * 
	 * @param 	f 	An unknown float
	 * @return	A float that is within the Float bounds
	 */
	public static float floatBounds(float f)
	{
		if (f==Float.POSITIVE_INFINITY) { f =  Float.MAX_VALUE; }
		if (f==Float.NEGATIVE_INFINITY) { f = -Float.MAX_VALUE; }		
		return f;
	}
}
