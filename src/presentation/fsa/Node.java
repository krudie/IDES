package presentation.fsa;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.BasicStroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Iterator;
import presentation.Glyph;
import presentation.MathUtils;
import model.fsa.FSAState;
import model.fsa.ver1.State;
import model.fsa.ver1.Transition;

/**
 * The graphical representation of a state in a finite state automaton.
 * Child glyphs are its out edges.
 * 
 * @author helen bretzke
 *
 */
public class Node extends GraphElement {

	// the state to be represented
	private FSAState state;
	// graphical layout metadata for the state
	private StateLayout layout;
	
	// TODO Change to list of labels to be displayed within the bounds of this node
	private GraphLabel label;

	// visualization objects
	private Ellipse2D circle = null;
	private Ellipse2D innerCircle = null;  // only drawn for final states	
	private ArrowHead arrow = null;  // only draw for initial states
	private Point2D.Float arrow1, arrow2;  // the arrow shaft
		
	public Node(FSAState s, StateLayout layout){
		this.state = s;
		this.layout = layout;
		label = new GraphLabel("");
		circle = new Ellipse2D.Double();
		arrow1 = new Point2D.Float();
		arrow2 = new Point2D.Float();		
		update();
	}

	public void setLayout(StateLayout layout){
		this.layout = layout;
	}
	
	// TODO change to iterate over collection of labels on a state
	// (requires change to file reading and writing, states be composed of many states)		
	public void update() {
			
		int radius = layout.getRadius();
		Point centre = layout.getLocation();
		
		// upper left corner, width and height
		int d = 2*radius;
		circle = new Ellipse2D.Double(centre.x - radius, centre.y - radius, d, d);

		if(state.isMarked()){			
			innerCircle = new Ellipse2D.Double(centre.x - 0.75*radius, centre.y - 0.75*radius, 1.5*radius, 1.5*radius);
		}
			
		if(state.isInitial()){
			// The point on the edge of the circle:
			// centre point - arrow vector
			Point2D.Float c = new Point2D.Float(centre.x, centre.y);
			Point2D.Float dir = new Point2D.Float(layout.getArrow().x, layout.getArrow().y);			
			float offset = layout.getRadius() + ArrowHead.SHORT_HEAD_LENGTH;
			arrow2 = MathUtils.subtract(c, MathUtils.scale(dir, offset));
			arrow = new ArrowHead(dir, arrow2);					
			// ??? How long should the shaft be?
			arrow1 = MathUtils.subtract(arrow2, MathUtils.scale(dir, ArrowHead.SHORT_HEAD_LENGTH * 2));
		}
		
		label.setText(layout.getText());
		
		// FIXME centre the label in the node; 
		// note that width and height of label are both 0.
					
		label.setLocation((int)centre.x, (int)centre.y);			
	}
	
	/**
	 * Draws this node and all of its out edges in the given graphics context.
	 */
	public void draw(Graphics g) {		
		super.draw(g);	// calls draw on all of the outgoing edges
		
		if (isSelected()){
			g.setColor(layout.getSelectionColor());
		}else if(isHighlighted()){
			g.setColor(layout.getHighlightColor());			
		}else{
			g.setColor(layout.getColor());	
		}
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(GraphicalLayout.WIDE_STROKE);		
		g2d.draw(circle);
				
		if(state.isMarked()){
			g2d.draw(innerCircle);
		}
		
		
		if(state.isInitial()){
			g2d.drawLine((int)arrow1.x, (int)arrow1.y, (int)arrow2.x, (int)arrow2.y);
			g2d.setStroke(GraphicalLayout.FINE_STROKE);
			g2d.draw(arrow);
			g2d.fill(arrow);
		}				
		label.draw(g);
	}
	
	public Rectangle bounds() {		
		return circle.getBounds();
	}

	public boolean intersects(Point p) {		
		return circle.contains(p);
	}	
	
}
