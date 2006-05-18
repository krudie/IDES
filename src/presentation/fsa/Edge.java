package presentation.fsa;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import model.fsa.FSATransition;
import model.fsa.ver1.Event;
import model.fsa.ver1.Transition;
import presentation.Geometry;

/**
 * The graphical representation of a transition in a finite state automaton.
 * 
 * @author Helen Bretzke
 *
 */
public class Edge extends GraphElement {

	// the transition that this edge represents
	// NOTE All that we need is the id to sync with the model
	// FIXME replace with a collection of transitions (or just the ids)
	private ArrayList<FSATransition> transitions;
	//////////////////////////////////////////////////////////
	
	private Node source, target;
	private EdgeLayout layout;
	
	// The bezier curve.
	private GeneralPath path;	
	private ArrowHead arrow;
	private EdgeHandler handler;
	
	// replace the following with Class java.awt.geom.CubicCurve2D	//////////
	private Point2D.Float[] controlPoints; // four controls points	
	public static final int P1 = 0;	
	public static final int CTRL1 = 1;
	public static final int CTRL2 = 2;
	public static final int P2 = 3;
	//////////////////////////////////////////////////////////////////////////
	
	private CubicCurve2D curve;
	
	
	public Edge(FSATransition t, EdgeLayout layout){
		transitions = new ArrayList<FSATransition>();
		transitions.add(t);
		this.layout = layout; 
		path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		controlPoints = new Point2D.Float[4];
		curve = new CubicCurve2D.Float();
		arrow = new ArrowHead();
		update();
	}
	
	public Edge(FSATransition t, Node source, Node target, EdgeLayout layout){		
		this(t, layout);
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
		curve.setCurve(controlPoints, 0);
		
		// prepare to draw myself as a cubic (bezier) curve 	
		path.moveTo((float)curve.getX1(), (float)curve.getY1());	    
	    path.curveTo((float)curve.getCtrlX1(), (float)curve.getCtrlY1(),
	    			controlPoints[CTRL2].x, controlPoints[CTRL2].y,
	    			controlPoints[P2].x, controlPoints[P2].y);		   	
		// Compute and store the arrow layout (the direction vector from base to tip of the arrow) 
	    Point2D.Float dir = new Point2D.Float(controlPoints[P2].x - controlPoints[CTRL2].x, controlPoints[P2].y - controlPoints[CTRL2].y);    	    
	    arrow = new ArrowHead(Geometry.unit(dir), controlPoints[P2]);
	    
	    handler = new EdgeHandler(this);
		
	    // assign label from associated event[s]
	    String s = "";
	    
	    Iterator iter = layout.getEventNames().iterator();
	    while(iter.hasNext()){
	    	s += (String)iter.next();
	    	s += ", ";
	    }
	    s = s.trim();
	    if(s.length()>0) s = s.substring(0, s.length() - 1);

	    CubicCurve2D.Float left = new CubicCurve2D.Float(); 
	    curve.subdivide(left, new CubicCurve2D.Float());
	    
	    // TODO add offset vector to location of label
	    Point2D midpoint = left.getP2();
	    
	    // TODO optimize: inefficient to remove child glyph and construct new one.
	    this.clear();
		insert(new GraphLabel(s, this, midpoint));
	}
	
	
	/**	 
	 * @return true iff p intersects with this edge. 
	 */
	public boolean intersects(Point p){		
		return curve.contains(p) || arrow.contains(p);
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
	
	/**
	 * Returns the bounding rectangle with P1 and P2 as vertices.
	 * (Assumes for sake of simplicity that the edge is a straight line i.e. ignores control points).
	 */
	public Rectangle2D bounds(){				
		return new Rectangle2D.Float(Math.min(controlPoints[P1].x, controlPoints[P2].x),
					  				Math.min(controlPoints[P1].y, controlPoints[P2].y),
					  				Math.abs(controlPoints[P2].x - controlPoints[P1].x), 
					  				Math.abs(controlPoints[P2].y - controlPoints[P1].y));	
	}

	public void addTransition(Transition t) {
		transitions.add(t);		
	}
	
	public void removeTransition(Transition t){
		transitions.remove(t);
	}

	public EdgeLayout getLayout() {
		return layout;
	}

	public void setLayout(EdgeLayout layout) {
		this.layout = layout;
	}
}
