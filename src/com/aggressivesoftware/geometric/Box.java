/*
 * Created on Dec 17, 2004
 */
package com.aggressivesoftware.geometric;

import org.eclipse.swt.graphics.Rectangle;

/**
 * This class represents a rectangular region.
 * It allows easy access and modification of the parameters of the region is represents.
 * 
 * @author Michael Wood
 */
public class Box 
{
	/**
	 * The top left and bottom right corners of the box.
	 */
	private int x1=0,
			    y1=0,
			    x2=0,
			    y2=0;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Box Construction ///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	/**
	 * Construct the Box.
	 * 
	 * @param	x1	The x co-ordinate of the top left corner of the box.
	 * @param	y1	The y co-ordinate of the top left corner of the box.
	 * @param	x2	The x co-ordinate of the bottom right corner of the box.
	 * @param	y2	The y co-ordinate of the bottom right corner of the box.
	 */
	public Box(int x1, int y1, int x2, int y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	/**
	 * Construct the Box.
	 * 
	 * @param	x		The x co-ordinate of the top left corner of the box.
	 * @param	y		The y co-ordinate of the top left corner of the box.
	 * @param	width	The width of the box.
	 * @param	height	The height of the box.
	 * @param	nothing	Unused. Prevents identical signatures.
	 */
	public Box(int x, int y, int width, int height, int nothing)
	{
		this.x1 = x;
		this.y1 = y;
		this.x2 = x+width;
		this.y2 = y+height;
	}

	/**
	 * Construct the Box.
	 * 
	 * @param	rectangle	A Rectangle that defines the Box.
	 */
	public Box(Rectangle rectangle)
	{
		this.x1 = rectangle.x;
		this.y1 = rectangle.y;
		this.x2 = rectangle.width;
		this.y2 = rectangle.height;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// modification ///////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	/**
	 * Set the x1 value.
	 * 
	 * @param	n	The new x1 value.
	 */
	public void x1(int n) { x1 = n; }

	/**
	 * Set the y1 value.
	 * 
	 * @param	n	The new y1 value.
	 */
	public void y1(int n) { y1 = n; }

	/**
	 * Set the x2 value.
	 * 
	 * @param	n	The new x2 value.
	 */
	public void x2(int n) { x2 = n; }

	/**
	 * Set the y2 value.
	 * 
	 * @param	n	The new y2 value.
	 */
	public void y2(int n) { y2 = n; }

	/**
	 * Set the width by adjusting the x2 value.
	 * 
	 * @param	n	The new width value.
	 */
	public void w(int n) { x2 = x1 + n; }

	/**
	 * Set the height by adjusting the y2 value.
	 * 
	 * @param	n	The new height value.
	 */
	public void h(int n) { y2 = y1 + n; }

	/**
	 * Set (x1,y1) to the given (x,y) while maintaining the origional (w,h)
	 * 
	 * @param	x	The new x1 value.
	 * @param	y	The new y1 value.
	 */
	public void moveTo(int x, int y) 
	{ 
		int w = w();
		int h = h();
		x1 = x;
		y1 = y;
		x2 = x1 + w;
		y2 = y1 + h;
	}
		
	/**
	 * Grow by n in each direction, outward from the center.
	 * 
	 * @param 	n	The ammount to grow.
	 */
	public void grow(int n)
	{
		x1 = x1 - n;
		y1 = y1 - n;
		x2 = x2 + n;
		y2 = y2 + n;
	}

	/**
	 * Scale the Box parameters by the given factor.
	 * 
	 * @param 	scale	The factor by which to scale the Box's parameters.
	 */
	public void scale(float scale)
	{
		x1 = (int)Math.round(scale * x1);
		y1 = (int)Math.round(scale * y1);
		x2 = (int)Math.round(scale * x2);
		y2 = (int)Math.round(scale * y2);
	}	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// access /////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * Get the x1 value.
	 * 
	 * @return	The x1 value.
	 */
	public int x1() { return x1; }

	/**
	 * Get the y1 value.
	 * 
	 * @return	The y1 value.
	 */
	public int y1() { return y1; }

	/**
	 * Get the x2 value.
	 * 
	 * @return	The x2 value.
	 */
	public int x2() { return x2; }

	/**
	 * Get the y2 value.
	 * 
	 * @return	The y2 value.
	 */
	public int y2() { return y2; }

	/**
	 * Get the width by adjusting the x2 value.
	 * 
	 * @return	The width value.
	 */
	public int w() { return x2-x1; }

	/**
	 * Get the height by adjusting the y2 value.
	 * 
	 * @return	The height value.
	 */
	public int h() { return y2-y1; }

	/**
	 * Get the x co-ordinate of the centre of this Box.
	 * 
	 * @return	The x co-ordinate of the centre of this Box.
	 */
	public int cx() { return x1 + (x2-x1)/2; }

	/**
	 * Get the y co-ordinate of the centre of this Box.
	 * 
	 * @return	The y co-ordinate of the centre of this Box.
	 */
	public int cy() { return y1 + (y2-y1)/2; }
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Misc ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////		

	/**
	 * Return a String representation of this Object.
	 * 
	 * @return	The top-left coordiantes and width and height in the form (x,y,w,h)
	 */
	public String toString()
	{
		return "(" + x1 + "," + y1 + "," + w() + "," + h() + ")";
	}
}