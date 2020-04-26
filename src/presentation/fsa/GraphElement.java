package presentation.fsa;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.HashMap;
import java.util.Iterator;

import presentation.GraphicalLayout;
import presentation.PresentationElement;

/**
 * The most generic implementation of elements displayed in a graphical view.
 * Recursive composition of a graph element with child elements of the same
 * type.
 * 
 * @author Helen Bretzke
 */
public class GraphElement implements PresentationElement {

    protected boolean visible = true;

    protected boolean highlighted = false;

    protected boolean selected = false;

    protected boolean needsRefresh = false;

    private GraphicalLayout layout;

    /** Collection of child elements */
    private HashMap<Long, GraphElement> children;

    private GraphElement parent;

    public GraphElement() {
        this(null);
    }

    public GraphElement(GraphElement parent) {
        this.parent = parent;
        children = new HashMap<Long, GraphElement>();
        layout = new GraphicalLayout();
    }

    /**
     * Draws all of my children in the given graphics context.
     * 
     * @param g the graphics context
     */
    public void draw(Graphics g) {

        if (needsRefresh()) {
            refresh();
        }

        for (PresentationElement child : children.values()) {
            child.draw(g);
        }
    }

    /**
     * Returns the smallest rectangle containing all of my children.
     * 
     * @return the smallest rectangle containing all of my children
     */
    public Rectangle bounds() {
        Rectangle bounds = null;
        for (PresentationElement child : children.values()) {
            if (bounds == null) {
                bounds = child.bounds();
            } else {
                bounds = (Rectangle) bounds.createUnion(child.bounds());
            }
        }
        if (bounds == null) {
            bounds = new Rectangle();
        }
        return bounds;
    }

    public boolean intersects(Point2D p) {
        return bounds().contains(p);
    }

    /**
     * Inserts the given child at key <code>Object.hashCode()</code>.
     * 
     * @param child the child to be inserted
     */
    public void insert(GraphElement child) {
        children.put((long) child.hashCode(), child);
        child.setParent(this);
    }

    /**
     * Returns true iff <code>child</code> is present in the set of child elements.
     * 
     * @param child
     * @return true iff <code>child</code> is present in the set of child elements
     */
    public boolean contains(PresentationElement child) {
        return children.containsValue(child);
    }

    /**
     * Remove <code>child</code> from the set of child elements.
     * 
     * @param child
     */
    public void remove(PresentationElement child) {
        if (child != null) {
            children.remove((long) child.hashCode());
        }
    }

    /**
     * Removes all elements from the set of child elements.
     */
    public void clear() {
        children.clear();
    }

    /**
     * Returns the child element at the given key or null if there is no child
     * corresponding to <code>key</code>.
     * 
     * @param key the key that maps to the returned child
     * @return the child at the given key
     */
    public PresentationElement child(long key) {
        return children.get(key);
    }

    /**
     * Returns an iterator of all child elements.
     * 
     * @return an iterator of all child elements
     */
    public Iterator<GraphElement> children() {
        return children.values().iterator();
    }

    /**
     * Sets the set of child elements to <code>children</code>.
     * 
     * @param children the map of child elements to set
     */
    public void setChildren(HashMap<Long, GraphElement> children) {
        this.children = children;
    }

    /**
     * Returns the parent element for this presentation element.
     * 
     * @return the parent element
     */
    public GraphElement getParent() {
        return parent;
    }

    /**
     * Gets the graph to which this element belongs.
     * 
     * @return the graph to which this element belongs; <code>null</code> if it
     *         doesn't belong to a graph.
     */
    public FSAGraph getGraph() {
        if (parent == null) {
            return null;
        }
        return parent.getGraph();
    }

    /**
     * Sets the parent element for this presentation element to <code>parent</code>.
     * 
     * @param parent the parent element to set
     */
    public void setParent(GraphElement parent) {
        this.parent = parent;
    }

    /**
     * Returns true iff this element is visible.
     * 
     * @return true iff this element is visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets this element to visible iff the given parameter is true.
     * 
     * @param visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
        for (PresentationElement g : children.values()) {
            g.setVisible(visible);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        for (PresentationElement g : children.values()) {
            g.setSelected(selected);
        }
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlight) {
        this.highlighted = highlight;
        for (PresentationElement g : children.values()) {
            g.setHighlighted(highlight);
        }
    }

    public void setLayout(GraphicalLayout layout) {
        this.layout = layout;
    }

    /**
     * Translates this element in the plane by the given displacement.
     * 
     * @param dx displacement in x dimension
     * @param dy displacement in y dimension
     */
    public void translate(float dx, float dy) {
        layout.translate(dx, dy);
        for (PresentationElement g : children.values()) {
            g.translate(dx, dy);
        }
        // setNeedsRefresh(true);
    }

    /**
     * Sets the location of this element to <code>p</code>.
     * 
     * @param p the new location to set
     */
    public void setLocation(Point2D.Float p) {
        layout.setLocation((float) p.getX(), (float) p.getY());
    }

    /**
     * Returns true iff the dirty flag has been set, indicating that this element
     * needs to be refreshed.
     */
    public boolean needsRefresh() {
        return needsRefresh;
    }

    /**
     * Sets the dirty flag to <code>d</code>
     * 
     * @param d the flag to set
     */
    public void setNeedsRefresh(boolean d) {
        if (d && parent != null && needsRefresh != d) {
            parent.setNeedsRefresh(d);
        }
        needsRefresh = d;
    }

    // TODO define a generic response
    public void showPopup(Component context) {
    }

    /**
     * Returns true iff the set of children is non-empty.
     * 
     * @return true iff the set of children is non-empty
     */
    public boolean hasChildren() {
        return !children.isEmpty();
    }

    /**
     * Refreshes all of children and clears the dirty flag.
     */
    public void refresh() {
        for (PresentationElement g : children.values()) {
            g.refresh();
        }
        setNeedsRefresh(false);
    }

    /**
     * Returns the number of children for this element.
     * 
     * @return the number of children
     */
    public int size() {
        return children.size();
    }

    /*
     * TODO remove this : prevent outside access to layout object (non-Javadoc)
     * 
     * @see presentation.PresentationElement#getLayout()
     */
    public GraphicalLayout getLayout() {
        return layout;
    }

    /*
     * (non-Javadoc)
     * 
     * @see presentation.PresentationElement#getLocation()
     */
    public Float getLocation() {
        return layout.getLocation();
    }

    /**
     * Returns the unique id (hash code as a Long) for this element instance.
     * 
     * @return the unique id for this element instance
     */
    public Long getId() {
        return (long) hashCode();
    }

    public Point2D.Float snapToGrid() {
        return layout.snapToGrid();
    }
}
