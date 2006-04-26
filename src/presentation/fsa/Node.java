package presentation.fsa;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.BasicStroke;
import java.awt.geom.Ellipse2D;
import java.util.Iterator;
import presentation.Glyph;
import presentation.GlyphLabel;
import presentation.GraphElement;
import model.fsa.State;
import model.fsa.StateLayout;
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
	private State state;	
	// list of labels to be displayed within the bounds of this node
	private GlyphLabel label;
	
	private Ellipse2D circle;
	
	// TODO Move to subclasses of Node
	private Ellipse2D innerCircle = null;  // only drawn for final states	
	private ArrowHead arrow = null;  // only draw for initial states
	
	public Node(State s){
		this.state = s;
		label = new GlyphLabel("");
		update();
	}
	
	public Node(State s, Glyph parent){
		super(parent);
		this.state = s;
		label = new GlyphLabel("");
		update();
	}	
	
	/**
	 * 	// TODO what about colour and line thickness?

		// TODO change to iterate over collection of labels on a state
		// (requires change to file reading and writing, states be composed of many states)		

	 *
	 */
	public void update() {
				
		StateLayout layout = (StateLayout)state.getLayout();
		int radius = layout.getRadius();
		Point centre = layout.getLocation();
		
		// upper left corner, width and height
		int d = 2*radius;
		circle = new Ellipse2D.Double(centre.x - radius, centre.y - radius, d, d);

		if(state.isMarked()){			
			innerCircle = new Ellipse2D.Double(centre.x - 0.75*radius, centre.y - 0.75*radius, 1.5*radius, 1.5*radius);
		}
			
		if(state.isInitial()){
			// FIXME what is the point on the edge of the circle?
			// A: centre point - arrow vector
			// arrow = new ArrowHead(state.getLayout().getArrow(), ???);
		}
		
		label.setText(layout.getText());
		label.setLocation((int)centre.x - label.getWidth(), 
				(int)centre.y - label.getHeight());
		
		
		// Create and add all edges from transition lists
		// Start with only the outgoing edges
		// TODO figure out how to store the incoming edges as well. 
		// Since they are highlighted on MouseDown event.
		clear(); // remove all of my child glyphs
		Iterator t = state.getTargetTransitionListIterator();
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
		
		// FIXME If Node were subclassed, we wouldn't need this logic.
		if(state.isMarked()){
			g2d.draw(innerCircle);
		}
		
		if(state.isInitial()){ // TODO draw an arrow
			
		}
				
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
