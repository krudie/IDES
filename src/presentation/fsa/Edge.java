package presentation.fsa;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
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
import util.BentoBox;

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

	private GraphLabel label;		
	
	public Edge(EdgeLayout layout, Node source){
		this.layout = layout;
		this.source = source;
		target = null;
		transitions = new ArrayList<FSATransition>();		 
		path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);		
		curve = new CubicCurve2D.Float();
		arrow = new ArrowHead();
		label = new GraphLabel("");
		insert(label);
	    handler = new EdgeHandler(this);
	    insert(handler);

		update();
	}
	
	public Edge(FSATransition t, EdgeLayout layout){
		transitions = new ArrayList<FSATransition>();
		transitions.add(t);
		this.layout = layout; 
		path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);		
		curve = new CubicCurve2D.Float();
		arrow = new ArrowHead();
		label = new GraphLabel("");
		insert(label);
	    handler = new EdgeHandler(this);
	    insert(handler);

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
		label = new GraphLabel("");		
		insert(label);
	    handler = new EdgeHandler(this);
	    insert(handler);

		update();
	}

	public void draw(Graphics g) {	
		if(layout.isDirty()){
			update();
			handler.update();
			layout.setDirty(false);
		}
		
		Graphics2D g2d = (Graphics2D)g;		
		// if either my source or target node is highlighted
		// then I am also hightlighted.
		if(source.isHighlighted() || 
				target != null && target.isHighlighted()){
			setHighlighted(true);
			g2d.setColor(layout.getHighlightColor());
		}else{
			setHighlighted(false);
			g2d.setColor(layout.getColor());
		}
			
		
		if(isSelected()){
			g2d.setColor(layout.getSelectionColor());
		}else{
			handler.setVisible(false); // KLUGE to clean up after modify edge tool
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
		curve.setCurve((Point2D.Float[])((EdgeLayout)layout).getCurve(), 0);
		
		if(!isSelected()){
			handler.setVisible(false);
		}
		
		// prepare to draw myself as a cubic (bezier) curve
		path.reset();
		path.moveTo((float)curve.getX1(), (float)curve.getY1());	    
	    path.curveTo((float)curve.getCtrlX1(), (float)curve.getCtrlY1(),
	    			(float)curve.getCtrlX2(), (float)curve.getCtrlY2(),
	    			(float)curve.getX2(), (float)curve.getY2());
	    
		// Compute and store the arrow layout (the direction vector from base to tip of the arrow) 
	    Point2D.Float unitDir = Geometry.unit(new Point2D.Float((float)(curve.getX2() - curve.getCtrlX2()), (float)(curve.getY2() - curve.getCtrlY2())));
	    
	    // FIXME arrow direction vector is incorrect
	    arrow = new ArrowHead(unitDir, Geometry.subtract(new Point2D.Float((float)(curve.getP2().getX()), (float)(curve.getP2().getY())), Geometry.scale(unitDir, ArrowHead.SHORT_HEAD_LENGTH)));
	    
		// Concat label from associated event[s]
	    String s = "";	    
	    Iterator iter = ((EdgeLayout)layout).getEventNames().iterator();
	    while(iter.hasNext()){
	    	s += (String)iter.next();
	    	s += ", ";
	    }
	    s = s.trim();
	    if(s.length()>0) s = s.substring(0, s.length() - 1);
	    label.setText(s);
	    
	    // Compute location of label: midpoint of curve	     
	    CubicCurve2D.Float left = new CubicCurve2D.Float(); 
	    curve.subdivide(left, new CubicCurve2D.Float());	        
	    Point2D midpoint = left.getP2();	    
	    Point2D.Float location = Geometry.add(new Point2D.Float((float)midpoint.getX(), (float)midpoint.getY()), ((EdgeLayout)layout).getLabelOffset());
	    label.getLayout().setLocation((float)location.getX(), (float)location.getX());	    	
	}
	
	
	/**	 
	 * @return true iff p intersects with this edge. 
	 */
	public boolean intersects(Point2D p){
		if(isSelected() && handler.isVisible()){
			return curve.contains(p) || arrow.contains(p) || handler.intersects(p) || label.intersects(p);
		}else{
			return curve.contains(p) || arrow.contains(p) || label.intersects(p);
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
		return label;
	}
	
	public void showPopup(Component c){
		EdgePopup.showPopup((GraphDrawingView)c, this);
	}
	
	public boolean isDirty(){
		return super.isDirty() || layout.isDirty();
	}
	
	/**
	 * This method is responsible for creating a string that contains
	 * an appropriate (depending on the type) representation of this
	 * edge.
	 *  
	 * @param selectionBox The area being selected or considered
	 * @param exportType The export format
	 * @return String The string representation
	 * 
	 * @author Sarah-Jane Whittaker
	 */
	public String createExportString(Rectangle selectionBox, int exportType)
	{
		String exportString = "";
		
		Point2D.Float edgeP1 = getP1();
		Point2D.Float edgeP2 = getP2();
		Point2D.Float edgeCTRL1 = getCTRL1();
		Point2D.Float edgeCTRL2 = getCTRL2();
		EdgeLayout edgeLayout = (EdgeLayout) getLayout();

		// Make sure this node is contained within the selection box
		if (! (selectionBox.contains(edgeP1) && selectionBox.contains(edgeP2)
			&& selectionBox.contains(edgeCTRL1) && selectionBox.contains(edgeCTRL2)))
		{
			System.out.println("Edge " + edgeP1 + " "
				+ edgeP2 + " "
				+ edgeCTRL1 + " "
				+ edgeCTRL2 + " "
				+ " outside bounds " + selectionBox);
			return exportString;
		}
		
		if (exportType == GraphExporter.INT_EXPORT_TYPE_PSTRICKS)
		{
			// Check whether this should be a line or a curve
			if ((edgeLayout.getAngle1() < EdgeLayout.EPSILON) &&
				(edgeLayout.getAngle2() < EdgeLayout.EPSILON))
			{
				// Draw a straight line
				exportString += "  \\psline[arrowsize=5pt]{->}(" 
					+ (edgeP1.x - selectionBox.x) + "," 
					+ (selectionBox.y + selectionBox.height - edgeP1.y) + ")(" 
					+ (edgeP2.x - selectionBox.x) + "," 
					+ (selectionBox.y + selectionBox.height - edgeP2.y) + ")\n";
			}
			else
			{	
				// Draw a curve				
				exportString += "  \\psbezier[arrowsize=5pt]{->}" //+ dash 
					+ "(" + (edgeP1.x - selectionBox.x) + "," 
					+ (selectionBox.y + selectionBox.height - edgeP1.y) + ")(" 
					+ (edgeCTRL1.x - selectionBox.x) + "," 
					+ (selectionBox.y + selectionBox.height -edgeCTRL1.y) + ")(" 
					+ (edgeCTRL2.x - selectionBox.x) + "," 
					+ (selectionBox.y + selectionBox.height -edgeCTRL2.y) + ")(" 
					+ (edgeP2.x - selectionBox.x) + "," 
					+ (selectionBox.y + selectionBox.height - edgeP2.y) + ")\n";
			}
			
			// Now for the label
			if ((layout.getText() != null) && (layout.getText().length() > 0))
			{
				exportString += "  " 
					+ label.createExportString(selectionBox, exportType);
			}
		}
		else if (exportType == GraphExporter.INT_EXPORT_TYPE_EPS)
		{	
			// LENKO!!!
		}

		return exportString;
	}
	
	/**
	 * This method returns the bounding box for the edge based 
	 * on its four points.
	 * 
	 * @return Rectangle The bounds of the Bezier Curve
	 * 
	 * @author Sarah-Jane Whittaker
	 */
	public Rectangle getCurveBounds()
	{
		Point2D.Float edgeP1 = getP1();
		Point2D.Float edgeP2 = getP2();
		Point2D.Float edgeCTRL1 = getCTRL1();
		Point2D.Float edgeCTRL2 = getCTRL2();
		
		float minX = BentoBox.getMinValue(edgeP1.x, edgeP2.x, 
			edgeCTRL1.x, edgeCTRL2.x);
		float minY = BentoBox.getMinValue(edgeP1.y, edgeP2.y, 
			edgeCTRL1.y, edgeCTRL2.y);
		float maxX = BentoBox.getMaxValue(edgeP1.x, edgeP2.x, 
			edgeCTRL1.x, edgeCTRL2.x);		
		float maxY = BentoBox.getMaxValue(edgeP1.y, edgeP2.y, 
			edgeCTRL1.y, edgeCTRL2.y);
		
		return new Rectangle(BentoBox.convertFloatToInt(minX), 
			BentoBox.convertFloatToInt(minY), 
			BentoBox.convertFloatToInt(maxX - minX), 
			BentoBox.convertFloatToInt(maxY - minY));
	}

	public Long getId(){
		if(!transitions.isEmpty()){
			return new Long(transitions.get(0).getId());
		}
		return null;
	}
}
