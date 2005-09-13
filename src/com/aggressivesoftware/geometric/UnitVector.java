/*
 * Created on Nov 11, 2004
 */
package com.aggressivesoftware.geometric;

/**
 * This class represents a unit vector in cartesian space.
 * It provides many methods to manipulate itself, or to produce manipulated versions of itself.
 * For example, it can be made perpendicular to itself.
 * 
 * @author Michael Wood
 */
public class UnitVector 
{
	/**
	 * The run. (this is guarenteed to never be exactly zero)
	 */
	public float x = Float.MIN_VALUE;

	/**
	 * The rise. (this is guarenteed to never be exactly zero)
	 */
	public float y = Float.MIN_VALUE;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// UnitVector construction ////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////		

	/**
	 * Construct a random UnitVector.
	 */
	public UnitVector()
	{
		unitize(1,1);
		rotateByDegrees((float)(Math.random()*360));
	}	
	
	/**
	 * Construct the UnitVector.
	 * 
	 * @param	x	The run.
	 * @param	y	The rise.
	 */
	public UnitVector(int x, int y)
	{
		unitize(x,y);
	}

	/**
	 * Construct the UnitVector.
	 * 
	 * @param	x	The run.
	 * @param	y	The rise.
	 */
	public UnitVector(float x, float y)
	{
		unitize(x,y);
	}

	/**
	 * Construct the UnitVector.
	 * 
	 * @param	p1	The starting point.
	 * @param	p2	The ending point.
	 */
	public UnitVector(Point p1, Point p2)
	{
		unitize(p2.x-p1.x,p2.y-p1.y);
	}
	
	/**
	 * Construct the UnitVector.
	 * 
	 * @param	x	The run.
	 * @param	y	The rise.
	 */
	private void unitize(float x, float y)
	{
		float mag = Geometric.magnitude(x,y);
		this.x = (float)(x/mag);
		this.y = (float)(y/mag);		
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Manipulation ///////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////		

	/**
	 * Change this to a new UnitVector, perpendicular to the origional.
	 */
	public void perpendicularize()
	{
		float temp = x;
		x = -y;
		y = temp;
	}

	/**
	 * Change this to a new UnitVector, in the reverse direction to the origional.
	 */
	public void reverse()
	{
		x = -x;
		y = -y;
	}

	/**
	 * Change this to a new UnitVector, rotated angle degrees to the origional.
	 * 
	 * @param	angle	The number of degrees to rotate this UnitVector.
	 */
	public void rotateByDegrees(float angle)
	{
		rotateByRadians((float)Math.toRadians(angle));
	}	

	/**
	 * Change this to a new UnitVector, rotated angle radians to the origional.
	 * 
	 * @param	angle	The number of radians to rotate this UnitVector.
	 */
	public void rotateByRadians(float angle)
	{
		float x = (float)(Math.cos(angle)*this.x + Math.sin(angle)*this.y);
		float y = (float)(Math.cos(angle)*this.y - Math.sin(angle)*this.x);
		unitize(x,y);
	}		
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Creation ///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////		

	/**
	 * Create a new UnitVector, perpendicular to the origional.
	 * 
	 * @return	A new UnitVector, perpendicular to the origional.
	 */
	public UnitVector newPerpendicular()
	{
		return new UnitVector(-y,x);
	}
	
	/**
	 * Create a new UnitVector, in the reverse direction to the origional.
	 * 
	 * @return	A new UnitVector, in the reverse direction to the origional.
	 */
	public UnitVector newReversed()
	{
		return new UnitVector(-x,-y);
	}
		
	/**
	 * Create a new UnitVector, rotated angle degrees to the origional.
	 * 
	 * @param	angle		The angle (in degrees) by which to rotate the new UnitVector from the origional.
	 * @return	A new UnitVector, rotated angle degrees to the origional.
	 */
	public UnitVector newRotatedByDegrees(float angle)
	{
		UnitVector d = new UnitVector(x,y);
		d.rotateByDegrees(angle);
		return d;
	}

	/**
	 * Create a new UnitVector, rotated angle radians to the origional.
	 * 
	 * @param	angle		The angle (in radians) by which to rotate the new UnitVector from the origional.
	 * @return	A new UnitVector, rotated angle radians to the origional.
	 */
	public UnitVector newRotatedByRadians(float angle)
	{
		UnitVector d = new UnitVector(x,y);
		d.rotateByRadians(angle);
		return d;
	}	
	
	/**
	 * Create a new Point the given distance from the given origin along this direction.
	 * 
	 * @param	distance	The distance to move along this direction.
	 * @param	origin		The origion to move from.
	 * @return	A point the given distance from the given origin along this direction.
	 */
	public Point newPoint(float distance, Point origin)
	{
		return new Point(origin.x + distance*this.x, origin.y + distance*this.y);
	}

	/**
	 * Create a new Point the given distance from the given origin along this direction rotated by angle degrees.
	 * 
	 * @param	distance	The distance to move along this direction.
	 * @param	origin		The origin to move from.
	 * @param	angle		The angle (in degrees) by which to rotate this direction before movement
	 * @return	A point the given distance from the given origin along this direction rotated by angle degrees.
	 */
	public Point newPoint(float distance, Point origin, float angle)
	{
		UnitVector d = this.newRotatedByDegrees(angle);
		return new Point(origin.x + distance*d.x, origin.y + distance*d.y);
	}
	
	/**
	 * Get a copy of this UnitVector.
	 * 
	 * @return	A copy of this UnitVector.
	 */
	public UnitVector getCopy()
	{ return new UnitVector(x,y); }
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Misc ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////		

	/**
	 * Return a String representation of this Object.
	 * 
	 * @return	the rise and run of this vector in the form: (x,y)
	 */
	public String toString()
	{
		return "(" + x + "," + y + ")";
	}
}