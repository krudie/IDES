package presentation.fsa;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import model.fsa.FSATransition;
import model.fsa.ver1.Transition;
import presentation.MathUtils;

/**
 * The graphical representation of a transition in a finite state automaton.
 * 
 * @author Helen Bretzke
 *
 */
public class Edge extends GraphElement {

	// the transition that this edge represents
	// NOTE All that we need is the id to sync with the model
	private FSATransition t;
	//////////////////////////////////////////////////////////
	
	private Node source, target;
	private TransitionLayout layout;
	
	// The bezier curve.
	// Review Lenko and Mike's curve code.
	// replace the following with Class java.awt.geom.CubicCurve2D
	private GeneralPath path;
	private Point2D.Float[] controlPoints; // four controls points	
	private ArrowHead arrow;
	private EdgeHandler handler;
	
	public static final int P1 = 0;	
	public static final int CTRL1 = 1;
	public static final int CTRL2 = 2;
	public static final int P2 = 3;
	
	public Edge(FSATransition t, TransitionLayout layout){		
		this.t = t;
		this.layout = layout; 
		path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		controlPoints = new Point2D.Float[4];		
		arrow = new ArrowHead();
		update();
	}
	
	public Edge(Node source, Node target, TransitionLayout layout){		
		this(null, layout);
		this.source = source;
		this.target = target;		
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		Graphics2D g2d = (Graphics2D)g;
	
		// if either my source or target node is highlighted
		// then I am also hightlighted.
		if(source.isHighlighted() || target.isHighlighted()){
			setHighlighted(true);
		}else{
			setHighlighted(false);
		}
				
		// Silly duplicate code.
		if(isHighlighted()){
			g2d.setColor(layout.getHighlightColor());
		}else if (isSelected()){			
			handler.draw(g2d);
			g2d.setColor(layout.getSelectionColor());
		}else{
			g2d.setColor(layout.getColor());
		}
		 
		g2d.setStroke(GraphicalLayout.WIDE_STROKE);
	    g2d.draw(path);
	    
	    // draw an arrowhead
	    g2d.drawPolygon(arrow);
	    g2d.fillPolygon(arrow);
	}
	
	/**
	 * Updates my curve, arrow and label.
	 */
	public void update() {
			
		controlPoints = (Point2D.Float[])layout.getCurve();
		// prepare to draw myself as a cubic (bezier) curve 	
		path.moveTo(controlPoints[P1].x, controlPoints[P1].y);	    
	    path.curveTo(controlPoints[CTRL1].x, controlPoints[CTRL1].y,
	    			controlPoints[CTRL2].x, controlPoints[CTRL2].y,
	    			controlPoints[P2].x, controlPoints[P2].y);		   	
		// Compute and store the arrow layout (the direction vector from base to tip of the arrow) 
	    Point2D.Float dir = new Point2D.Float(controlPoints[P2].x - controlPoints[CTRL2].x, controlPoints[P2].y - controlPoints[CTRL2].y);    	    
	    arrow = new ArrowHead(MathUtils.unit(dir), controlPoints[P2]);
	    
	    handler = new EdgeHandler(this);
		
	    // TODO label[s] from associated event[s]		
		
	}
	
	
	/** 
	 * NOTE intersects with control point handles will be a different operation.
	 * 
	 * @return true iff p intersects with this edge. 
	 */
	public boolean intersects(Point p){		
		return path.contains(p) || arrow.contains(p);
	}
	
	public Point2D.Float getP1() {
		return controlPoints[P1];
	}

	public Point2D.Float getP2() {
		return controlPoints[P2];
	}
	
	public Point2D.Float getCTRL1() {
		return controlPoints[CTRL1];		
	}

	public Point2D.Float getCTRL2() {
		return controlPoints[CTRL2];		
	}

	public Node getSource() {
		return source;
	}

	public void setSource(Node source) {
		this.source = source;
	}

	public Node getTarget() {
		return target;
	}

	public void setTarget(Node target) {
		this.target = target;
	}
	
	public Rectangle2D bounds(){
		// compute the bounding rectangle of P1 and P2 (just assume that the edge is a straight line)		
		return new Rectangle2D.Float(Math.min(controlPoints[P1].x, controlPoints[P2].x),
					  				Math.min(controlPoints[P1].y, controlPoints[P2].y),
					  				Math.abs(controlPoints[P2].x - controlPoints[P1].x), 
					  				Math.abs(controlPoints[P2].y - controlPoints[P1].y));	
	}
	
}
