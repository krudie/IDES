/*
 * Created on Nov 11, 2004
 */
package userinterface.geometric;

/**
 * This class represents a point in cartesian space.
 * It provides many methods for creating new points based on itself.
 * For example, you can create a new Point a given distance along a given direction from this Point.
 * 
 * @author Michael Wood
 */
public class Point 
{
	/**
	 * The x co-ordinate of this Point.
	 */
	public int x=0;

	/**
	 * The y co-ordinate of this Point.
	 */
	public int y=0;
		
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Point construction /////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
	
	/**
	 * Construct the Point.
	 * 
	 * @param	x	The x co-ordinate of this Point.
	 * @param	y	The y co-ordinate of this Point.
	 */
	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Construct the Point.
	 * 
	 * @param	x	The x co-ordinate of this Point.
	 * @param	y	The y co-ordinate of this Point.
	 */
	public Point(float x, float y)
	{
		this.x = (int)Math.round(x);
		this.y = (int)Math.round(y);
	}

	/**
	 * Construct the Point.
	 * 
	 * @param	p	A Point in swt format.
	 */
	public Point(org.eclipse.swt.graphics.Point p)
	{
		this.x = p.x;
		this.y = p.y;
	}
	
	/**
	 * Construct the Point.
	 * 
	 * @param	coords	A String representation of integer co-ordinates. i.e. (5,4)
	 */
	public Point(String coords)
	{
		try
		{
			int comma = coords.indexOf(',');
			this.x = Integer.parseInt(coords.substring(1,comma));
			this.y = Integer.parseInt(coords.substring(comma+1,coords.length()-1));
		}
		catch (Exception e)
		{
			this.x = 0;
			this.y = 0;
		}
	}	
	
	/**
	 * Construct a Point the given distance along the given direction from the given Point.
	 * 
	 * @param	starting_point	A starting point.
	 * @param	direction		A direction.
	 * @param	distance		A distance.
	 */
	public Point(Point starting_point, UnitVector direction, float distance)
	{
		this.x = (int)Math.round(starting_point.x + distance*direction.x);
		this.y = (int)Math.round(starting_point.y + distance*direction.y);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// modification ///////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////		

	/**
	 * Change the co-ordinates of this Point to equal those of the given Point.
	 * 
	 * @param	p	The Point to copy.
	 */
	public void copy(Point p)
	{
		x = p.x;
		y = p.y;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// creation ///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////		

	/**
	 * Create a copy of this Point.
	 * 
	 * @return	A copy of this Point.
	 */
	public Point getCopy()
	{
		return new Point(x,y);
	}
	
	/**
	 * Create a new Point the given distance along the given direction from this Point.
	 * 
	 * @param	direction		A direction.
	 * @param	distance		A distance.
	 * @return	A new Point the given distance along the given direction from this Point.
	 */
	public Point newPoint(UnitVector direction, float distance)
	{
		return new Point((int)Math.round(this.x + distance*direction.x),(int)Math.round(this.y + distance*direction.y));
	}
	
	/**
	 * Create a new point modified by the difference between the two given points.
	 * 
	 * @param	p1	A first point.
	 * @param	p2	A second point.
	 * @return	A clone of this point modified by the difference between the two given points.
	 */
	public Point newModified(Point p1, Point p2)
	{
		return new Point(x + (p1.x-p2.x),y + (p1.x-p2.x));
	}
	
	/**
	 * Create a new point that is the addition of the two.
	 * 
	 * @param	p	Another point.
	 * @return	A new point.
	 */
	public Point plus(Point p)
	{
		return new Point(p.x+x,p.y+y);
	}
	
	/**
	 * Create a new point that is the subtraction of the two.
	 * 
	 * @param	p	Another point.
	 * @return	A new point.
	 */
	public Point minus(Point p)
	{
		return new Point(x-p.x,y-p.y);
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Queries ////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////		

	/**
	 * Tests if the given point is inside the circle defined by this point and the given radius.
	 * 
	 * @param	radius	The radius of the circle centered at this point.
	 * @param	p		The point to be tested for inside/outside.
	 * @return	true if p is inside the circle centered at this point with the given radius.
	 */
	public boolean isInsideCircle(int radius, Point p)
	{
		if (Math.pow(p.x-this.x,2) + Math.pow(p.y-this.y,2) < Math.pow(radius,2)) 
		{ return true; }
		else { return false; }
	}
	
	/**
     * Test if this Point is at the same location as the given Point.
     * 
     * @param	test_point	The point to test.
     * @return	true If this Point is at the same location as the given Point.
     */
	public boolean isSameAs(Point test_point) 
	{ return (test_point.x == x && test_point.y == y); }
	
	/**
	 * Provide a String representation of this Point in the form "(x,y)".
	 * 
	 * @return	A String representation of this Point in the form "(x,y)".
	 */
	public String toString()
	{
		return "(" + x + "," + y + ")";
	}
}