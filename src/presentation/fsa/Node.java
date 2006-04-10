package presentation.fsa;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.LinkedList;

import presentation.Glyph;
import presentation.GlyphLabel;
import presentation.GraphElement;
import model.DESState;
import model.fsa.State;
import model.fsa.SubElement;

public class Node extends GraphElement {
	
	// determines the way this node is to be rendered
	// whether initial, marked (terminal) or standard node
	// also tells which transitions and hence edges are incoming and outgoing,
	// for the purpose of highlighting and recursive drawing.
	private State s;
	
	// list of labels to be displayed within the bounds of this node
	private GlyphLabel label;
	
	private Ellipse2D circle;

	public Node(State s){
		this.s = s;
		label = new GlyphLabel("");
		update();
	}
	
	public Node(State s, Glyph parent){
		super(parent);
		this.s = s;
		label = new GlyphLabel("");
		update();
	}	
	
	public void update() {
		SubElement layout = s.getSubElement("graphic");
		int radius = Integer.parseInt(layout.getAttribute("r"));
		Point centre = new Point(Integer.parseInt(layout.getAttribute("x")),
								 Integer.parseInt(layout.getAttribute("y")));
		// upper left corner, width and height
		int d = 2*radius;
		circle = new Ellipse2D.Double(centre.x - radius, centre.y - radius, d, d);
		
		// TODO what about colour and line thickness?

		// TODO change to iterate over collection of labels on a state
		// (requires change to file reading and writing, states be composed of many states)
		label.setText(s.getSubElement("name").getName());
		label.setLocation((int)centre.x - label.getWidth()/2, 
				(int)centre.y - label.getHeight()/2);
		
		// TODO create and add all edges from transition lists
		
	}
	
	public void draw(Graphics g) {		
		// get radius and centre point
		label.draw(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.draw(circle);
	}

	/**
	 * Draw myself and all incoming and outgoing edges 
	 * in a highlighted colour in the given graphics context.
	 * 
	 * @param g
	 */
	public void highlight(Graphics g) {
				
	}
	
	public Rectangle bounds() {		
		return circle.getBounds();
	}

	public boolean intersects(Point p) {		
		return circle.contains(p);
	}	
	
}
