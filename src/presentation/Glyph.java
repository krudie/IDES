package presentation;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

/**
 * Interface for any object that can appear in the drawing area.
 * Adheres to Composite design pattern.
 * 
 * TODO extend to add highlight() and select() methods that render the glyph 
 * in distinct ways from draw().
 * 
 * @author Helen Bretzke
 *
 */
public interface Glyph {

	/**
	 * Draws this glyph and its children (recursively, if any) 
	 * in the given graphics context.
	 * 
	 * @param g the graphics context
	 */
	public void draw(Graphics g);

	/**
	 * Returns the opposite corners of the smallest area that this Glyph occupies.
	 * 
	 * @return the smallest rectangular area that the glyph occupies.
	 */
	public Rectangle2D bounds();
	
	/**
	 * Used to determine which glyph is under the mouse.
	 * 
	 * @param p the point
	 * @return true iff this Glyph intersects with the given point.
	 */
	public boolean intersects(Point p);
	
	/**
	 * Inserts the given child into my list at the given index, overwrites
	 * any other glyph in that position.
	 * 
	 * FIXME It is not great to index the children in this way.
	 * Should I hash them instead?
	 * 
	 * @param child the child to be added
	 * @param index the index at which to insert the child
	 */
	public void insert(Glyph child, long index);
	
	/**
	 * Inserts the given child Glyph.
	 * 
	 * @param g
	 */
	public void insert(Glyph child);
	
	/**
	 * Removes the first occurrence of the given child from my list of children.
	 * 
	 * @param child the child to be removed.
	 */
	public void remove(Glyph child);
	
	/** 
	 * @param index
	 * @return the child at the given index, null if does not exist
	 */
	public Glyph child(int index);
	
	/** 
	 * @return the parent of this glyph, null if does not exist
	 */
	public Glyph parent();
	
	/**
	 * @return an iterator of my children
	 */
	public Iterator children();
	
	public boolean isHighlighted();
	
	public void setHighlighted(boolean b);
		
	public boolean isSelected();
	
	public void setSelected(boolean b);
	
	public boolean isVisible();
	
	public void setVisible(boolean b);
	
}
