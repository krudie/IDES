package presentation;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

/**
 * Interface for any object that can appear in the drawing area.
 * Follows Composite (recursive composition) design pattern.
 * 
 * @author Helen Bretzke
 *
 */
public interface PresentationElement {

	/**
	 * Draws this element and its children (recursively, if any) 
	 * in the given graphics context.
	 * 
	 * @param g the graphics context
	 */
	public void draw(Graphics g);

	/**
	 * Returns the opposite corners of the smallest area that this element occupies.
	 * 
	 * @return the smallest rectangular area that the element occupies.
	 */
	public Rectangle2D bounds();
	
	/**
	 * Used to determine which element is under the mouse.
	 * 
	 * @param p the point
	 * @return true iff this element intersects with the given point.
	 */
	public boolean intersects(Point2D p);
	
	/**
	 * Inserts the given child into my list at the given index, overwrites
	 * any other element in that position.
	 * 
	 * FIXME It is not great to index the children in this way.
	 * Should I hash them instead?
	 * 
	 * @param child the child to be added
	 * @param index the index at which to insert the child
	 */
	public void insert(PresentationElement child, long index);
	
	/**
	 * Inserts the given child element.
	 * 
	 * @param g
	 */
	public void insert(PresentationElement child);
	
	/**
	 * Removes the first occurrence of the given child from my list of children.
	 * 
	 * @param child the child to be removed.
	 */
	public void remove(PresentationElement child);
	
	/** 
	 * @param index
	 * @return the child at the given index, null if does not exist
	 */
	public PresentationElement child(int index);
	
	/** 
	 * @return the parent of this element, null if does not exist
	 */
	public PresentationElement parent();
	
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
	
	public void setLayout(GraphicalLayout layout);
	
	public GraphicalLayout getLayout();
	
}
