package presentation.fsa;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Float;

import presentation.GraphicalLayout;
import presentation.PresentationElement;


public class GraphElement implements PresentationElement {
		
	protected boolean visible = true;
	protected boolean highlighted = false;
	protected boolean selected = false;
	protected boolean dirty = false;
		
	private GraphicalLayout layout;
	
	// my states and free labels
	private HashMap<Long, PresentationElement> children;
	private PresentationElement parent;
	
	public GraphElement() {		
		this(null);		
	}
		
	public GraphElement(PresentationElement parent) {		
		this.parent = parent;
		children = new HashMap<Long, PresentationElement>();
		layout = new GraphicalLayout();
	}
	
	/**
	 * Draws all of my children.
	 */
	public void draw(Graphics g) {
		
		if(isDirty()) refresh();
		
		for(PresentationElement child : children.values()){			 
			child.draw(g);
		}
	}

	/**
	 * @return the smallest rectangle containing all of my children 
	 */
	public Rectangle2D bounds() {		
		
		Rectangle2D bounds = null;		
		for(PresentationElement child : children.values()){			
			if(bounds == null){
				bounds = child.bounds();
			}else{
				bounds = bounds.createUnion(child.bounds());
			}
		}		
		if(bounds == null){
			bounds = new Rectangle2D.Float();			
		}
		return bounds;
	}

	public boolean intersects(Point2D p) {		
		return bounds().contains(p);
	}

	/**
	 * TEST Make certain that elements that have an id don't hash to
	 * the same location as those that use a hashcode.
	 */
//	public void insert(PresentationElement child, long key) {				
//		children.put(new Long(key), child);
//		child.setParent(this);
//	}

	/**
	 * Inserts the given child at key <code>Object.hashCode()</code>.
	 */
	public void insert(PresentationElement child) {
		children.put(child.getId(), child);	
		child.setParent(this);
	}
	
	public boolean contains(PresentationElement child){
		return children.containsValue(child);		
	}
	
	/**
	 * FIXME does this remove the given child or the child at the given key?
	 */
	public void remove(PresentationElement child) {
		children.remove(child.getId());		
	}

	public void clear() {
		children.clear();
	}
	
	/**
	 * @returns the child at the given key
	 */
	public PresentationElement child(long key) {
		return children.get(new Long(key));		
	}

	public Iterator children() { 
		return children.values().iterator();
	}

//	public ArrayList<PresentationElement> getChildren() {
//		return children;
//	}

	public void setChildren(HashMap<Long, PresentationElement> children) {
		this.children = children;
	}

	public PresentationElement getParent() {
		return parent;
	}

	public void setParent(PresentationElement parent) {
		this.parent = parent;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;		
		for(PresentationElement g : children.values()){			
			g.setVisible(visible);
		}
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		for(PresentationElement g : children.values()){			
			g.setSelected(selected);
		}
	}	

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlight) {
		this.highlighted = highlight;
		for(PresentationElement g : children.values()){			
			g.setHighlighted(highlight);
		}
	}
	
	public void setLayout(GraphicalLayout layout) {
		this.layout = layout;		
	}


	public void translate(float x, float y){
		layout.translate(x, y);
		for(PresentationElement g : children.values()){
			g.translate(x,y);
		}
		setDirty(true);		
	}

	public void setLocation(Point2D p) {
		layout.setLocation((float)p.getX(), (float)p.getY());		
	}

	public boolean isDirty() { 
		return dirty;
	}

	public void setDirty(boolean d){
		dirty = d;
		if(parent != null && d){
			parent.setDirty(d);
		}
	}
	
	// TODO what is a generic response to this call?
	public void showPopup(Component context){
		//DEBUG
		//main.Hub.displayAlert("TODO: define showPopup(Component) for this subclass");		
	}
	
	public boolean hasChildren() {		
		return !children.isEmpty();
	}

	public void refresh(){
		for(PresentationElement g : children.values()){
			g.refresh();
		}
		setDirty(false);
	}
	
	public int size()
	{
		return children.size();
	}

	/* TODO remove this : prevent outside access to layout object
	 *  (non-Javadoc)
	 * @see presentation.PresentationElement#getLayout()
	 */
	public GraphicalLayout getLayout() {		
		return layout;
	}

	/* (non-Javadoc)
	 * @see presentation.PresentationElement#getLocation()
	 */
	public Float getLocation() {		
		return layout.getLocation();
	}

	/* (non-Javadoc)
	 * @see presentation.PresentationElement#getId()
	 */
	public Long getId() {		
		return (long)hashCode();
	}
}
