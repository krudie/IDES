package presentation;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

/**
 * Interface for any element that can be displayed in the drawing area. Adheres
 * to the Composite (recursive composition) design pattern.
 * 
 * @author Helen Bretzke March 2006
 */
public interface PresentationElement {

    /**
     * Draws this element and its children in the given graphics context.
     * 
     * @param g the graphics context
     */
    public void draw(Graphics g);

    /**
     * Returns the smallest area rectangular that this element occupies.
     * 
     * @return the smallest rectangle containing this element
     */
    public Rectangle bounds();

    /**
     * Returns true iff this element intersects with the given point. Used to
     * determine which element is under the mouse.
     * 
     * @param p the point
     * @return true iff this element intersects with the given point.
     */
    public boolean intersects(Point2D p);

    // /**
    // * Inserts the given child element.
    // *
    // * @param child the element to be inserted
    // */
    // public void insert(PresentationElement child);
    //
    // /**
    // * Removes the first occurrence of the given child from my list of
    // children.
    // *
    // * @param child the child to be removed.
    // */
    // public void remove(PresentationElement child);
    //
    // /**
    // * Returns the child at the given key, null if does not exist.
    // *
    // * @param key the key mapping to the desired child element
    // * @return the child at the given key, null if no such element
    // */
    // public PresentationElement child(long key);
    //
    // /**
    // * Returns the parent of this element, null if this element
    // * has no parent.
    // *
    // * @return the parent of this element, null if does not exist
    // */
    // public PresentationElement getParent();
    //
    // /**
    // * Sets the parent element of this element to <code>parent</code>.
    // *
    // * @param parent the element to be set
    // */
    // public void setParent(PresentationElement parent);
    //
    // /**
    // * Returns an iterator of all child elements.
    // *
    // * @return an iterator of child elements
    // */
    // public Iterator children();

    /**
     * Returns whether this element is to appear highlighted when rendered.
     * 
     * @return true iff this element is to appear highlighted
     */
    public boolean isHighlighted();

    /**
     * Sets whether this element is to apprear highlighted when rendered.
     * 
     * @param b the flag to be set
     */
    public void setHighlighted(boolean b);

    /**
     * Returns whether this element is to appear selected when rendered.
     * 
     * @return true iff this element is to appear selected
     */
    public boolean isSelected();

    /**
     * Sets whether this element is to apprear selected when rendered.
     * 
     * @param b the flag to be set
     */
    public void setSelected(boolean b);

    /**
     * Returns whether this element is visible.
     * 
     * @return true iff this element is visible
     */
    public boolean isVisible();

    /**
     * Sets whether this element is visible.
     * 
     * @param b flag to be set
     */
    public void setVisible(boolean b);

    /**
     * Sets the graphical layout data for this element to the given layout.
     * 
     * @param layout the layout data to be set
     */
    public void setLayout(GraphicalLayout layout);

    /*
     * FIXME Don't want layout to be tampered with directly, should be guarded by
     * its GraphElement to control nature and timing of updates.
     */
    /**
     * Returns the graphical layout data for this element.
     * 
     * @return the graphical layout data for this element
     */
    public GraphicalLayout getLayout();

    /**
     * Translates the location of this element by the given x and y offsets.
     * 
     * @param x the x offset
     * @param y the y offset
     */
    public void translate(float x, float y);

    /**
     * Sets the location of this element to the given x and y coordinates.
     * 
     * @param p the coordinate
     */
    public void setLocation(Point2D.Float p);

    /**
     * Returns the x and y coordinates of the associated element on the graph
     * canvas.
     * 
     * @return x and y coordinates of the associated element on the graph canvas
     */
    public Point2D.Float getLocation();

    /**
     * Sets whether this element needs to to refresh its display characteristics
     * from its underlying data.
     * 
     * @param b the flag to set
     */
    public void setNeedsRefresh(boolean b);

    /**
     * Returns whether this element needs to refresh its display characteristics
     * from its underlying data.
     * 
     * @return whether this element needs to be refreshed
     */
    public boolean needsRefresh();

    /**
     * Refreshes this element's display characteristics from its underlying data.
     */
    public void refresh();

    /**
     * Returns the ID of this element.
     * 
     * @return the ID of this element
     */
    public Long getId();
}
