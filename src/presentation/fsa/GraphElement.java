package presentation.fsa;

import java.awt.Graphics;
import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import presentation.Glyph;


public class GraphElement implements Glyph {
	
	private boolean visible;
	private boolean highlighted;
	private boolean selected;
	
	// my states and free labels
	private LinkedList<Glyph> children;
	private Glyph parent;
	
	public GraphElement() {		
		this.parent = null;
		children = new LinkedList<Glyph>();
	}
		
	public GraphElement(Glyph parent) {		
		this.parent = parent;
		children = new LinkedList<Glyph>();
	}
	
	/**
	 * Draws all of my children.
	 */
	public void draw(Graphics g) {		
		Iterator c = children.iterator();
		while(c.hasNext()){
			Glyph child = (Glyph)c.next();
			child.draw(g);
		}
	}

	public Rectangle2D bounds() {
		// compute the max bounding rectangle to hold all 
		// of my children
		Iterator c = children.iterator();
		Rectangle2D.Float bounds = new Rectangle2D.Float();
		while(c.hasNext()){
			Glyph child = (Glyph)c.next();
			bounds.createUnion(child.bounds()); 
		}		
		return bounds;
	}

	public boolean intersects(Point2D p) {		
		return bounds().contains(p);
	}

	public void insert(Glyph child, long index) {
		// KLUGE - change to the index to a String or Long and hash the children
		children.add((int)index, child);
	}

	public void insert(Glyph g) {
		children.add(g);		
	}
	
	public void remove(Glyph child) {
		children.remove(child);
	}

	public void clear() {
		children.clear();
	}
	
	public Glyph child(int index) {
		return children.get(index);		
	}

	public Glyph parent() {
		return parent;
	}

	public Iterator children() { 
		return children.iterator();
	}

	public LinkedList<Glyph> getChildren() {
		return children;
	}

	public void setChildren(LinkedList<Glyph> children) {
		this.children = children;
	}

	public Glyph getParent() {
		return parent;
	}

	public void setParent(Glyph parent) {
		this.parent = parent;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		Iterator i = children.iterator();
		Glyph g;
		while(i.hasNext()){
			g = (Glyph)i.next();
			g.setVisible(visible);
		}
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		Iterator i = children.iterator();
		Glyph g;
		while(i.hasNext()){
			g = (Glyph)i.next();
			g.setSelected(selected);
		}
	}	

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlight) {
		this.highlighted = highlight;
		Iterator i = children.iterator();
		Glyph g;
		while(i.hasNext()){
			g = (Glyph)i.next();
			g.setSelected(highlight);
		}
	}
}
