package presentation;

import java.awt.Color;
import java.awt.Label;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.Iterator;


public class GlyphLabel extends Label implements Glyph {

	private Glyph parent = null;  // either the DrawingBoard, a node or an edge	
	private Color colour = Color.DARK_GRAY;
	private Font font;
	
	public GlyphLabel(String text){
		super(text);
		font = new Font("times", Font.ITALIC, 12);
	}
	
	/**
	 * @param text string to display in this label
	 * @param location the x,y coordinates of the top left corner of this label 
	 */
	public GlyphLabel(String text, Point location){
		this(text);
		setLocation(location);		
	}
	
	/**
	 * TODO decide whether the DrawingBoard is a special kind of Glyph.
	 * 
	 * @param text string to display in this label
	 * @param parent glyph in which this label is displayed
	 * @param location the x,y coordinates of the top left corner of this label
	 */
	public GlyphLabel(String text, Glyph parent, Point location) {	
		super(text);
		setLocation(location);
		this.parent = parent;
	}
	
	public void draw(Graphics g) {
		// get the bounding box of my parent
		// and then draw myself correctly oriented to my parent
		g.setColor(colour);
		// DEBUG
		g.setFont(font);
		g.drawString(getText(), getLocation().x, getLocation().y);
	}

	public Rectangle bounds() {
		return getBounds();
	}
	
	public boolean intersects(Point p) {
		return getBounds().contains(p);
	}

	public void insert(Glyph child, int index) {}
	public void insert(Glyph g) {}
	public void remove(Glyph child) {}
	public Glyph child(int index) {	return null; }
	public Iterator children() { return null; }
	
	public Glyph parent() {		
		return parent;
	}

}