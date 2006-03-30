package presentation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;


public class Label implements Glyph {

	private Glyph parent;  // either the DrawingBoard, a node or an edge
	private String text;
	private Color colour = Color.blue;
	// the x,y coordinates of the top left corner of this label
	private Point location;  
	
	public Label(String text){
		this.text = text;
		parent = null;
	}
	
	public Label(String text, Glyph parent) {	
		this.text = text;
		this.parent = parent;
	}
	
	public void draw(Graphics g) {
		// get the bounding box of my parent
		// and then draw myself correctly oriented to my parent
		g.setColor(colour);
		g.drawString(text, location.x, location.y);
	}

	public boolean intersects(Point p) {
		// TODO Auto-generated method stub
		return false;
	}

	public void insert(Glyph child, int index) {}
	public void remove(Glyph child) {}

	public Glyph child(int index) {	return null; }

	public Glyph parent() {		
		return parent;
	}
	
	/**
	 * TODO need to know the font size to compute the bounding box.
	 */
	public Rectangle bounds() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
