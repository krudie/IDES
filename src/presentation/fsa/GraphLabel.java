package presentation.fsa;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Label;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.util.Iterator;

import presentation.GraphicalLayout;
import presentation.PresentationElement;

/**
 * TODO Change so that doesn't extend label; waste of space and rounds the location to int coords.
 * 
 * @author helen
 *
 */
@SuppressWarnings("serial")
public class GraphLabel extends GraphElement {
	protected Rectangle bounds;
	protected boolean visible = true;
	protected PresentationElement parent = null;  // either the DrawingBoard, a node or an edge	
	protected Font font;	
	
	public GraphLabel(String text){
		layout = new GraphicalLayout(text);
		// TODO change to a dynamic value read from a config file and stored in 
		// SystemVariables? ResourceManager?
		font = new Font("times", Font.ITALIC, 12);
		bounds = new Rectangle();
	}
	
	public GraphLabel(GraphicalLayout layout){		
		this.layout = layout;	
		bounds = new Rectangle();
	}
	
	/**
	 * @param text string to display in this label
	 * @param location the x,y coordinates of the top left corner of this label 
	 */
	public GraphLabel(String text, Point2D location){
		this(text);		
		layout.setLocation((float)location.getX(), (float)location.getY());		
	}
	
	/**
	 * TODO decide whether the DrawingBoard is a special kind of Glyph.
	 * 
	 * @param text string to display in this label
	 * @param parent glyph in which this label is displayed
	 * @param location the x,y coordinates of the top left corner of this label
	 */
	public GraphLabel(String text, PresentationElement parent, Point2D location) {	
		this(text, location);		
		this.parent = parent;
	}
	
	public void draw(Graphics g) {
//		if(layout.isDirty()){
//			update();
//		}
		
		// FIXME this computes the position for a Node label but won't work for an edge; see bounds()
		// If this is a NodeLabel, set the location at the centre of the node.
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics();
		int width = metrics.stringWidth( layout.getText() );
		int height = metrics.getHeight();
		bounds.setLocation(new Point((int)(layout.getLocation().x - bounds.width/2), 
				(int)(layout.getLocation().y/2)));
		
		int x = (int)layout.getLocation().x - width/2;
		int y = (int)layout.getLocation().y + metrics.getAscent()/2;
		bounds.setSize(width, height);
		if(visible){
			g.setColor(layout.getColor());		
			g.drawString(layout.getText(), x, y);
		}
	}

	public Rectangle bounds() {
		// TODO annoying that we can't compute the bounds until we have a graphics context in the draw method ...
		// NOTE have to draw the label before drawing the node in case node needs to be resized.
		if(bounds.getWidth() == 0 || bounds.getHeight() == 0){
			Toolkit tk = Toolkit.getDefaultToolkit();
			FontMetrics metrics = tk.getFontMetrics(font);
			bounds.setSize(metrics.stringWidth(layout.getText()), metrics.getHeight() );
			bounds.setLocation(new Point((int)(layout.getLocation().x - bounds.width/2), 
										(int)(layout.getLocation().y - bounds.height/2)));			
		}
		return bounds;				
	}
	
	public boolean intersects(Point2D p) {		
		return bounds().intersects(p.getX(), p.getY(), 1, 1);
	}

	public void insert(PresentationElement child, long index) {}
	public void insert(PresentationElement g) {}
	public void remove(PresentationElement child) {}
	public PresentationElement child(int index) {	return null; }
	public Iterator children() { return null; }
	
	public PresentationElement parent() {		
		return parent;
	}

	public void setText(String s){
		layout.setText(s);		
	}

	public boolean isVisible() {		
		return visible;
	}

	public void setVisible(boolean b) {
		visible = b;
	}

}