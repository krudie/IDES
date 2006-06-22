package presentation.fsa;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import main.Main;
import model.fsa.FSATransition;
import model.fsa.ver1.Transition;
import presentation.Geometry;
import presentation.GraphicalLayout;

/**
 * The graphical representation of a transition in a finite state automaton.
 * 
 * @author Helen Bretzke
 *
 */
public class Edge extends GraphElement {

	// the transitions that this edge represents
	private ArrayList<FSATransition> transitions;	
	
	private Node source, target;
	
	// Handles for modifying control points/tangents to the curve.
	private EdgeHandler handler;
	
	// The bezier curve.
	// TODO this should be stored in EdgeLayout class.
	private CubicCurve2D curve;
	
	// Visualization of the curve
	private GeneralPath path;	
	private ArrowHead arrow;		
	
	public Edge(EdgeLayout layout, Node source){
		this.layout = layout;
		this.source = source;
		target = null;
		transitions = new ArrayList<FSATransition>();		 
		path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);		
		curve = new CubicCurve2D.Float();
		arrow = new ArrowHead();
		update();
	}
	
	public Edge(FSATransition t, EdgeLayout layout){
		transitions = new ArrayList<FSATransition>();
		transitions.add(t);
		this.layout = layout; 
		path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);		
		curve = new CubicCurve2D.Float();
		arrow = new ArrowHead();
		update();
	}
	
	public Edge(FSATransition t, Node source, Node target, EdgeLayout layout){		
		this.source = source;
		this.target = target;
		transitions = new ArrayList<FSATransition>();
		transitions.add(t);
		this.layout = layout; 
		path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);		
		curve = new CubicCurve2D.Float();
		arrow = new ArrowHead();
		update();
	}

	public void draw(Graphics g) {	
		if(layout.isDirty()){
			update();
			layout.setDirty(false);
		}
		
		Graphics2D g2d = (Graphics2D)g;		
		// if either my source or target node is highlighted
		// then I am also hightlighted.
		if(source.isHighlighted() || 
				target != null && target.isHighlighted()){
			setHighlighted(true);
		}else{
			setHighlighted(false);
		}
				
		// Silly duplicate code.
		if(isHighlighted()){
			g2d.setColor(layout.getHighlightColor());
		}else if (isSelected()){
			handler.setVisible(true);
			g2d.setColor(layout.getSelectionColor());
		}else{
			handler.setVisible(false);
			g2d.setColor(layout.getColor());
		}		
		
		g2d.setStroke(GraphicalLayout.WIDE_STROKE);
	    g2d.draw(path);
	    
	    // draw an arrowhead
	    g2d.drawPolygon(arrow);
	    g2d.fillPolygon(arrow);
	    
	    // draw label and handler
	    super.draw(g);
	}
	
	/**
	 * Updates my visualization of curve, arrow and label.
	 */
	public void update() {
		// FIXME optimize: inefficient to remove child glyphs and construct new ones.
		// At least make sure there are no other references to the children 
		// or you will have an evil memory leak.
	    clear();		
		curve.setCurve((Point2D.Float[])((EdgeLayout)layout).getCurve(), 0);
		// prepare to draw myself as a cubic (bezier) curve
		path.reset();
		path.moveTo((float)curve.getX1(), (float)curve.getY1());	    
	    path.curveTo((float)curve.getCtrlX1(), (float)curve.getCtrlY1(),
	    			(float)curve.getCtrlX2(), (float)curve.getCtrlY2(),
	    			(float)curve.getX2(), (float)curve.getY2());
	    
		// Compute and store the arrow layout (the direction vector from base to tip of the arrow) 
	    Point2D.Float unitDir = Geometry.unit(new Point2D.Float((float)(curve.getX2() - curve.getCtrlX2()), (float)(curve.getY2() - curve.getCtrlY2())));	    
	    arrow = new ArrowHead(unitDir, Geometry.subtract(new Point2D.Float((float)(curve.getP2().getX()), (float)(curve.getP2().getY())), Geometry.scale(unitDir, ArrowHead.SHORT_HEAD_LENGTH)));
	    	    
	    handler = new EdgeHandler(this);
	    handler.setVisible(false);
	    insert(handler);
		
	    // assign label from associated event[s]
	    String s = "";
	    
	    Iterator iter = ((EdgeLayout)layout).getEventNames().iterator();
	    while(iter.hasNext()){
	    	s += (String)iter.next();
	    	s += ", ";
	    }
	    s = s.trim();
	    if(s.length()>0) s = s.substring(0, s.length() - 1);

	    // Compute location of label: midpoint of curve plus label offset vector 
	    CubicCurve2D.Float left = new CubicCurve2D.Float(); 
	    curve.subdivide(left, new CubicCurve2D.Float());	        
	    Point2D midpoint = left.getP2();
		insert(new GraphLabel(s, this, 
					Geometry.add(new Point2D.Float((float)midpoint.getX(), (float)midpoint.getY()),	((EdgeLayout)layout).getLabelOffset())));
	}
	
	
	/**	 
	 * @return true iff p intersects with this edge. 
	 */
	public boolean intersects(Point p){
		if(isSelected()){
			return curve.contains(p) || arrow.contains(p) || handler.intersects(p);
		}else{
			return curve.contains(p) || arrow.contains(p);
		}		
	}
	
	public Point2D.Float getP1() {
		return new Point2D.Float((float)curve.getX1(), (float)curve.getY1());
	}

	public Point2D.Float getP2() {
		return new Point2D.Float((float)curve.getX2(), (float)curve.getY2());
	}
	
	public Point2D.Float getCTRL1() {
		return new Point2D.Float((float)curve.getCtrlX1(), (float)curve.getCtrlY1());		
	}

	public Point2D.Float getCTRL2() {
		return new Point2D.Float((float)curve.getCtrlX2(), (float)curve.getCtrlY2());		
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
		return new Rectangle2D.Float((float)Math.min(curve.getX1(), curve.getX2()),
					  				(float)Math.min(curve.getY1(), curve.getY2()),					  				
					  				(float)Math.abs(curve.getX2() - curve.getX1()), 
					  				(float)Math.abs(curve.getY2() - curve.getY1()));	
	}

	public void addTransition(Transition t) {
		transitions.add(t);		
	}
	
	public void removeTransition(Transition t){
		transitions.remove(t);
	}

	public void setLayout(EdgeLayout layout) {
		super.setLayout(layout);
		update();
	}
	
	public void translate(float x, float y){
		EdgeLayout l = (EdgeLayout)layout;
		if(l.isRigidTranslation()){
		// Translate the whole curve assuming that its
		// source and target nodes have been translated by the same displacement.
		curve.setCurve(curve.getX1()+x, curve.getY1()+y,
						curve.getCtrlX1()+x, curve.getCtrlY1()+y,
						curve.getCtrlX2()+x, curve.getCtrlY2()+y,						
						curve.getX2(), curve.getY2()+y);
		l.setCurve(curve.getP1(), curve.getCtrlP1(), curve.getCtrlP2(), curve.getP2());
		l.setRigidTranslation(false);
		
		// reset the control points in the layout object
		}else{
			l.computeCurve(source.getLayout(), target.getLayout());
		}		
	}

	public Iterator<FSATransition> getTransitions() {		
		return transitions.iterator();
	}

	public EdgeHandler getHandler() {
		return handler;
	}

	/**
	 * @return true iff source node is same as target node
	 */
	public boolean isSelfLoop() {		
		return source.equals(target);
	}

	/**
	 * Gets the label of the node.
	 * @return the label of the node
	 */
	public GraphLabel getLabel()
	{
		//FIXME: this is a big hack
		GraphLabel label=null;
		Iterator i=children();
		while(i.hasNext())
		{
			Object o=i.next();
			if(o instanceof GraphLabel)
			{
				label=(GraphLabel)o;
				break;
			}
		}
		return label;
	}
	
	public void showPopup(Component c){
		EdgePopup.showPopup((GraphDrawingView)c, this);
	}
}
