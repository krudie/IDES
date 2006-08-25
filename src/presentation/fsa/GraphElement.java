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
	public Rectangle bounds() {			
		Rectangle bounds = null;		
		for(PresentationElement child : children.values()){			
			if(bounds == null){
				bounds = child.bounds();
			}else{
				bounds = (Rectangle)bounds.createUnion(child.bounds());
			}
		}		
		if(bounds == null){
			bounds = new Rectangle();			
		}
		return bounds;
	}

	public boolean intersects(Point2D p) {		
		return bounds().contains(p);
	}

	/**
	 * Inserts the given child at key <code>Object.hashCode()</code>.
	 */
	public void insert(PresentationElement child) {
		children.put((long)child.hashCode(), child);	
		child.setParent(this);
	}
	
	public boolean contains(PresentationElement child){
		return children.containsValue(child);		
	}
		
	public void remove(PresentationElement child) {		
		children.remove((long)child.hashCode());		
	}

	public void clear() {
		children.clear();
	}
	
	/**
	 * @return the child at the given key
	 */
	public PresentationElement child(long key) {
		return children.get(key);		
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
	
	// TODO define a generic response
	public void showPopup(Component context){}
	
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

	/**
	 * @return the hashCode of this GraphElement
	 */
	public Long getId() {		
		return (long)hashCode();
	}
}
