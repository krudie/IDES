package presentation;

import java.awt.Graphics;
import java.awt.Rectangle;
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
	public Rectangle bounds();
	
	/**
	 * Used to determine which element is under the mouse.
	 * 
	 * @param p the point
	 * @return true iff this element intersects with the given point.
	 */
	public boolean intersects(Point2D p);
	
	/**
	 * Inserts the given child into my list at the given key, overwrites
	 * any other element in that position.
	 * 
	 * @param child the child to be added
	 * @param key the key at which to insert the child
	 */
	//public void insert(PresentationElement child, long key);
	
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
	 * @param key
	 * @return the child at the given key, null if does not exist
	 */
	public PresentationElement child(long key);
	
	/** 
	 * @return the parent of this element, null if does not exist
	 */
	public PresentationElement getParent();
	
	public void setParent(PresentationElement parent);
	
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
	
	// FIXME Don't want layout to be tampered with directly, 
	// should be guarded by its GraphElement to control nature and timing of updates.
	public GraphicalLayout getLayout(); 
	
	public void translate(float x, float y);
	
	public void setLocation(Point2D p);	
	
	public Point2D.Float getLocation();
	
	public void setDirty(boolean b);
	
	public boolean isDirty();

	public void refresh();	
	
	public Long getId();	
}
