package presentation.fsa;

import java.awt.Color;
import java.awt.Label;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Iterator;

import presentation.Glyph;

/**
 * TODO Change so that doesn't extend label; waste of space and rounds the location to int coords.
 * 
 * @author helen
 *
 */
@SuppressWarnings("serial")
public class GraphLabel extends Label implements Glyph {

	private Glyph parent = null;  // either the DrawingBoard, a node or an edge	
	private Color colour = GraphicalLayout.DEFAULT_COLOR;
	private Font font;
	
	public GraphLabel(String text){
		super(text);
		// TODO change to a dynamic value read from a config file and stored in 
		// SystemVariables? ResourceManager?
		font = new Font("times", Font.ITALIC, 12);
	}
	
	/**
	 * @param text string to display in this label
	 * @param location the x,y coordinates of the top left corner of this label 
	 */
	public GraphLabel(String text, Point2D location){
		this(text);		
		setLocation(new Point((int)location.getX(), (int)location.getY()));		
	}
	
	/**
	 * TODO decide whether the DrawingBoard is a special kind of Glyph.
	 * 
	 * @param text string to display in this label
	 * @param parent glyph in which this label is displayed
	 * @param location the x,y coordinates of the top left corner of this label
	 */
	public GraphLabel(String text, Glyph parent, Point2D location) {	
		this(text, location);		
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
	
	public boolean intersects(Point2D p) {
		return getBounds().contains(p);
	}

	public void insert(Glyph child, long index) {}
	public void insert(Glyph g) {}
	public void remove(Glyph child) {}
	public Glyph child(int index) {	return null; }
	public Iterator children() { return null; }
	
	public Glyph parent() {		
		return parent;
	}

	public boolean isHighlighted() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setHighlighted(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setSelected(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public void setText(String s){
		super.setText(s);
		this.validate();
		this.setVisible(true);
		this.setSize(this.getPreferredSize());		
	}	
}