package presentation;

import java.awt.Graphics;
import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.awt.geom.Rectangle2D;


public class GraphElement implements Glyph {
	
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

	public boolean intersects(Point p) {		
		return bounds().contains(p);
	}

	public void insert(Glyph child, int index) {		
		children.add(index, child);
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

}
