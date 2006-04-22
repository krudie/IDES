package presentation.fsa;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.BasicStroke;
import java.awt.geom.Ellipse2D;
import java.util.Iterator;
import java.util.LinkedList;

import presentation.Glyph;
import presentation.GlyphLabel;
import presentation.GraphElement;
import model.DESState;
import model.fsa.State;
import model.fsa.SubElement;
import model.fsa.Transition;

/**
 * The graphical representation of a state in a finite state automaton.
 * 
 * @author helen bretzke
 *
 */
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
		
		SubElement layout = s.getSubElement("graphic").getSubElement("circle");
		int radius = Integer.parseInt(layout.getAttribute("r"));
		Point centre = new Point(Integer.parseInt(layout.getAttribute("x")),
								 Integer.parseInt(layout.getAttribute("y")));
		
		// upper left corner, width and height
		int d = 2*radius;
		circle = new Ellipse2D.Double(centre.x - radius, centre.y - radius, d, d);
		
		// TODO what about colour and line thickness?

		// TODO change to iterate over collection of labels on a state
		// (requires change to file reading and writing, states be composed of many states)
		// FIXME where does the subelementcontainer structure store the name of the state ????!!
		SubElement name = s.getSubElement("name");
        String l = (name.getChars() != null) ? name.getChars() : "";
		label.setText(l);
		label.setLocation((int)centre.x - label.getWidth()/2, 
				(int)centre.y - label.getHeight()/2);
		
		// Create and add all edges from transition lists
		// Start with only the outgoing edges
		// TODO figure out how to store the incoming edges as well (are highlighted on MouseDown event)
		clear(); // remove all of my child glyphs
		Iterator t = s.getTargetTransitionListIterator();
		int i = 0;
		while(t.hasNext()){						
			insert(new Edge((Transition)t.next()), i++);			
		}
	}
	
	public void draw(Graphics g) {				
		super.draw(g);	// calls draw on all of the outgoing edges
		Graphics2D g2d = (Graphics2D)g;
		
		// should be in GraphElement ///////		
		g2d.setStroke(new BasicStroke(2));
		////////////////////////////////////
		
		g2d.draw(circle);
		label.draw(g);
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
