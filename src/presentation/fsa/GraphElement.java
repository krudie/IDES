package presentation.fsa;

import java.awt.Graphics;
import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import presentation.GraphicalLayout;
import presentation.PresentationElement;


public class GraphElement implements PresentationElement {
	
	protected boolean visible;
	protected boolean highlighted;
	protected boolean selected;
	protected GraphicalLayout layout;
	
	// my states and free labels
	private LinkedList<PresentationElement> children;
	private PresentationElement parent;
	
	public GraphElement() {		
		this(null);		
	}
		
	public GraphElement(PresentationElement parent) {		
		this.parent = parent;
		children = new LinkedList<PresentationElement>();
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
		// KLUGE - change to the index to a String or Long and hash the children
		children.add((int)index, child);
	}

	public void insert(PresentationElement g) {
		children.add(g);		
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

	public LinkedList<PresentationElement> getChildren() {
		return children;
	}

	public void setChildren(LinkedList<PresentationElement> children) {
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
	public void update(){}

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
}
