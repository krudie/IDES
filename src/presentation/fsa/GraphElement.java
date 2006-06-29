package presentation.fsa;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import presentation.GraphicalLayout;
import presentation.PresentationElement;


public class GraphElement implements PresentationElement {
	
	protected boolean visible = true;
	protected boolean highlighted = false;
	protected boolean selected = false;
	protected boolean dirty = false;
	protected GraphicalLayout layout;
	
	// my states and free labels
	private ArrayList<PresentationElement> children;
	private PresentationElement parent;
	
	public GraphElement() {		
		this(null);		
	}
		
	public GraphElement(PresentationElement parent) {		
		this.parent = parent;
		children = new ArrayList<PresentationElement>();
		layout = new GraphicalLayout();
	}
	
	/**
	 * Draws all of my children.
	 */
	public void draw(Graphics g) {		
		Iterator c = children.iterator();
		while(c.hasNext()){
			PresentationElement child = (PresentationElement)c.next();
			child.draw(g);
		}
	}

	public Rectangle2D bounds() {
		// compute the max bounding rectangle to hold all 
		// of my children
		Iterator c = children.iterator();
		Rectangle2D.Float bounds = new Rectangle2D.Float();
		while(c.hasNext()){
			PresentationElement child = (PresentationElement)c.next();
			bounds.createUnion(child.bounds()); 
		}		
		return bounds;
	}

	public boolean intersects(Point2D p) {		
		return bounds().contains(p);
	}

	public void insert(PresentationElement child, long index) {
		// TODO - change the index to a String or Long and hash the children
		children.add((int)index, child);
	}

	public void insert(PresentationElement e) {
		children.add(e);	
		e.setParent(this);
	}
	
	public boolean contains(PresentationElement e){
		return children.contains(e);		
	}
	
	public void remove(PresentationElement child) {
		children.remove(child);
	}

	public void clear() {
		children.clear();
	}
	
	public PresentationElement child(int index) {
		return children.get(index);		
	}

	public PresentationElement parent() {
		return parent;
	}

	public Iterator children() { 
		return children.iterator();
	}

//	public ArrayList<PresentationElement> getChildren() {
//		return children;
//	}

	public void setChildren(ArrayList<PresentationElement> children) {
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
		Iterator i = children.iterator();
		PresentationElement g;
		while(i.hasNext()){
			g = (PresentationElement)i.next();
			g.setVisible(visible);
		}
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		Iterator i = children.iterator();
		PresentationElement g;
		while(i.hasNext()){
			g = (PresentationElement)i.next();
			g.setSelected(selected);
		}
	}	

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlight) {
		this.highlighted = highlight;
		Iterator i = children.iterator();
		PresentationElement g;
		while(i.hasNext()){
			g = (PresentationElement)i.next();
			g.setHighlighted(highlight);
		}
	}
	
	/**
	 * TODO should this be in class PresentationElement?
	 * No.  If make this an abstract method, then can't instantiate a generic GraphElement :(
	 *
	 */
	//public void update(){}

	public void setLayout(GraphicalLayout layout) {
		this.layout = layout;		
	}

	public GraphicalLayout getLayout() {		
		return layout;
	};
	
	public void translate(float x, float y){
		layout.translate(x, y);
		Iterator c = children.iterator();
		while(c.hasNext()){
			PresentationElement child = (PresentationElement)c.next();
			child.translate(x,y);
		}
	}

	public void setLocation(Point2D p) {
		layout.setLocation((float)p.getX(), (float)p.getY());		
	}

	public boolean isDirty() { 
		return dirty;
	}

	public void setDirty(boolean d){
		dirty = d;
		if(parent != null){
			parent.setDirty(d);
		}
	}
	
	// TODO what is a generic response to this call?
	public void showPopup(Component context){
		//DEBUG
		main.Hub.displayAlert("TODO: define showPopup(Component) for this subclass");
	}
	
	public boolean hasChildren() {		
		return !children.isEmpty();
	}

	public void update(){
		Iterator c = children.iterator();
		while(c.hasNext()){
			PresentationElement child = (PresentationElement)c.next();
			child.update();
		}
	}
}
