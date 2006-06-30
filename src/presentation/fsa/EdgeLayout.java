package presentation.fsa;

import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import presentation.Geometry;
import presentation.GraphicalLayout;


public class EdgeLayout extends GraphicalLayout {

	private Edge edge;
	
	/**
	 * Indices of bezier curve control points. 
	 */
	public static final int P1 = 0;	
	public static final int CTRL1 = 1;
	public static final int CTRL2 = 2;
	public static final int P2 = 3;
	public static final double EPSILON = 0.0001; // lower bound for abs(angles), below which is treated as zero 
		
	// Indicates whether an edge can be rigidly translated 
	// with both of its nodes or must be recomputed.
	// Default value is false;
	private boolean rigidTranslation = false; 
	private ArrayList eventNames;
	private Point2D.Float[] ctrls; // TODO Replace with CubicCurve2D
	private CubicCurve2D curve;
	private Point2D.Float labelOffset;
		
	// Compact representation of data required to maintain shape of edge while moving
	// one or both of its nodes.
	private static final double DEFAULT_CONTROL_HANDLE_SCALAR = 1.0/3.0f;
	private double s1 = DEFAULT_CONTROL_HANDLE_SCALAR;  // scalar |(CTRL1 - P1)|/|(P2-P1)|
	private double s2 = DEFAULT_CONTROL_HANDLE_SCALAR;  // scalar |(CTRL2 - P2)|/|(P1-P2)|
	private double angle1 = 0.0; // angle between  (CTRL1 - P1) and (P2-P1)
	private double angle2 = 0.0; // angle between  (CTRL2 - P2) and (P1-P2)
	
	public EdgeLayout(){		
		ctrls = new Point2D.Float[4];
		for(int i = 0; i<4; i++){
			ctrls[i] = new Point2D.Float();			
		}
		curve = new CubicCurve2D.Float();
		eventNames = new ArrayList();
		labelOffset = new Point2D.Float(5,5);
	}
	
	public EdgeLayout(Point2D.Float[] bezierControls){
		this.ctrls = bezierControls;
		curve = new CubicCurve2D.Float();
		curve.setCurve(bezierControls, 0);
		eventNames = new ArrayList();
		labelOffset = new Point2D.Float(5,5);
		updateAnglesAndScalars();
	}
	
	public EdgeLayout(Point2D.Float[] bezierControls, ArrayList eventNames){
		this.ctrls = bezierControls;
		curve = new CubicCurve2D.Float();
		curve.setCurve(bezierControls, 0);
		this.eventNames = eventNames;
		labelOffset = new Point2D.Float(5,5);
		updateAnglesAndScalars();
	}

	public void setEdge(Edge edge){
		this.edge = edge;
	}
	
	/**
	 * Constructs an edge layout object for a straight, directed edge from
	 * <code>n1</code> to <code>n2</code>.
	 * 
	 * @param n1 layout for source node
	 * @param n2 layout for target node
	 */
	public EdgeLayout(NodeLayout n1, NodeLayout n2){
		ctrls = new Point2D.Float[4];
		curve = new CubicCurve2D.Float();
		computeCurve(n1, n2);		
		eventNames = new ArrayList();
		labelOffset = new Point2D.Float(5,5);
		updateAnglesAndScalars();
	}
	
	/**
	 * Returns an array of 4 control points for a straight, directed edge from
	 * <code>s</code>, the layout for the source node to <code>t</code>, the 
	 * layout for the target node.
	 * 
	 * @param s layout for source node
	 * @param t layout for target node
	 * @return array of 4 Bezier control points for a straight, directed edge
	 */
	public void computeCurve(NodeLayout s, NodeLayout t){
		
		// if source and target nodes are the same, compute a self-loop
		if(s.equals(t)){
			// TODO
			// throw new UnsupportedOperationException("Self-loops not net implemented.");
			computeSelfLoop(s);
			return;
		}
		
		Point2D.Float centre1 = s.getLocation();
		Point2D.Float centre2 = t.getLocation();
		// TODO compute intersection of straight line from centre1 to centre2 with arcs of nodes
		Point2D.Float base = Geometry.subtract(centre2, centre1);
		float norm = (float)Geometry.norm(base);
		Point2D.Float unitBase = Geometry.unit(base);  // computing norm twice :(
		
		// FIXME endpoints must be spaced around node arc; need to know about fellow edges.
		// IDEA have the NodeLayout wiggle (rotate edges about centre) the desired/ideal adjacent edge 
		// endpoints as calculated by EdgeLayout.
		ctrls[P1] = Geometry.add(centre1, Geometry.scale(unitBase, s.getRadius()));//		
		ctrls[P2] = Geometry.add(centre2, Geometry.scale(unitBase, -t.getRadius())); // -ArrowHead.SHORT_HEAD_LENGTH));		
		
		base = Geometry.subtract(ctrls[P2], ctrls[P1]);		
		unitBase = Geometry.unit(base);		
		
		if(Math.abs(angle1) < EPSILON && Math.abs(angle2) < EPSILON){		
			// compute a straight edge		
			s1 = DEFAULT_CONTROL_HANDLE_SCALAR;
			s2 = DEFAULT_CONTROL_HANDLE_SCALAR;
		
			norm = (float)Geometry.norm(base);
			ctrls[CTRL1] = Geometry.add(ctrls[P1], Geometry.scale(unitBase, (float)(norm * s1)));
			ctrls[CTRL2] = Geometry.add(ctrls[P1], Geometry.scale(unitBase, (float)(2 * norm * s2)));				
			
		}else{ // recompute the edge preserving the shape of the curve
			// compute CTRL1
			Point2D.Float v = Geometry.rotate(Geometry.scale(base, (float)s1), angle1);
			ctrls[CTRL1] = Geometry.add(ctrls[P1], v);	
			
			// compute CTRL2			
			v = Geometry.rotate(Geometry.scale(base, (float)s2), angle2);
			ctrls[CTRL2] = Geometry.add(ctrls[P1], v);			
		}	
		
		curve.setCurve(ctrls, 0);
		Point2D midpoint = midpoint(curve);
	    setLocation((float)midpoint.getX(), (float)midpoint.getY());
		setDirty(true);
	}
	
	/**
	 * Returns an array of 4 control points for a straight, directed edge from
	 * <code>s</code>, the layout for the source node to endpoint <code>c2</code>.
	 * 
	 * @param s layout for source node
	 * @param centre2 endpoint for the edge	  
	 */
	public void computeCurve(NodeLayout s, Point2D.Float centre2){		
		Point2D.Float centre1 = s.getLocation();
		Point2D.Float dir = Geometry.subtract(centre2, centre1);
		
		// FIXME recompute all points and scalars from P1 based on vector from p1 to p2
		// NOT from centres of nodes.
		
		float norm = (float)Geometry.norm(dir);
		Point2D.Float unit = Geometry.unit(dir);  // computing norm twice :(
		ctrls[P1] = Geometry.add(centre1, Geometry.scale(unit, s.getRadius()));
		
		dir = Geometry.subtract(centre2, ctrls[P1]);
		norm = (float)Geometry.norm(dir);
		unit = Geometry.unit(dir);
		ctrls[CTRL1] = Geometry.add(ctrls[P1], Geometry.scale(unit, (float)(norm * s1)));
		ctrls[CTRL2] = Geometry.add(ctrls[P1], Geometry.scale(unit, (float)(2 * norm * s2)));
		ctrls[P2] = centre2;
		curve.setCurve(ctrls, 0);
		Point2D midpoint = midpoint(curve);
	    setLocation((float)midpoint.getX(), (float)midpoint.getY());
		setDirty(true);		
	}
	
	
	/**
	 * Returns a cubic bezier curve representing a self-loop for the
	 * given node layout.
	 * 
	 * @param s the node layout	 
	 */
	public void computeSelfLoop(NodeLayout s){
		// Draw default loop above node and let node rotate it if necessary.
		// direction vectors rotate (0, r) by pi/4 and -pi/4
		double angleScalar = 5 * s.getRadius() / NodeLayout.DEFAULT_RADIUS;  
		angle1 = Math.PI/angleScalar;
		angle2 = Math.PI/-angleScalar;
		s1 = s2 = 0.0;
		
		// FIXME make this work for arbitrary rotation (not just vertical).
		Point2D.Float vert = new Point2D.Float(0, -s.getRadius());  // Effing screen coordinates with inverted y axis (grrrrr).
		
		Point2D.Float v1 = Geometry.rotate(vert, angle1);
		Point2D.Float v2 = Geometry.rotate(vert, angle2);
		ctrls[P1] = Geometry.add(s.getLocation(), v1);
		ctrls[P2] = Geometry.add(s.getLocation(), v2);
		ctrls[CTRL1] = Geometry.add(ctrls[P1], Geometry.scale(Geometry.unit(v1), 3f*NodeLayout.DEFAULT_RADIUS));
		ctrls[CTRL2] = Geometry.add(ctrls[P2], Geometry.scale(Geometry.unit(v2), 3f*NodeLayout.DEFAULT_RADIUS));
		curve.setCurve(ctrls, 0);
		Point2D midpoint = midpoint(curve);
	    setLocation((float)midpoint.getX(), (float)midpoint.getY());		
		setDirty(true);
	}
	
	

	/**
	 * @param curve2
	 * @return the midpoint of curve2
	 */
	private Point2D midpoint(CubicCurve2D curve2) {
		CubicCurve2D.Float left = new CubicCurve2D.Float(); 
	    curve.subdivide(left, new CubicCurve2D.Float());	        
	    return left.getP2();
	}
	
	/**
	 * Computes:
	 *  s1   scalar |(CTRL1 - P1)|/|(P2-P1)|
	 *  s2   scalar |(CTRL2 - P2)|/|(P1-P2)|
	 *  angle1  angle between  (CTRL1 - P1) and (P2-P1)
	 *  angle2  angle between  (CTRL2 - P2) and (P1-P2)
	 */
	private void updateAnglesAndScalars(){
		// compute tangent scalars
		Point2D.Float p1p2 = Geometry.subtract(ctrls[P2], ctrls[P1]); 
		double n = Geometry.norm(p1p2);
		Point2D.Float p1c1 = Geometry.subtract(ctrls[CTRL1], ctrls[P1]);
		Point2D.Float p1c2 = Geometry.subtract(ctrls[CTRL2], ctrls[P1]);
		s1 = Geometry.norm(p1c1)/n;
		s2 = Geometry.norm(p1c2)/n;		
		angle1 = Geometry.angleFrom(p1p2, p1c1);
		angle2 = Geometry.angleFrom(p1p2, p1c2);		
	}

	/**
	 * FIXME if move control points, endpoints must adjust too.
	 * Let Node or NodeLayout wiggle the endpoints?
	 * 
	 * @param point
	 * @param index
	 */
	public void setPoint(Point2D.Float point, int index){
		ctrls[index] = point;
		updateAnglesAndScalars();
		setDirty(true);
	}
		
	public void setCurve(Point2D.Float[] bezierControls) {
		this.ctrls = bezierControls;		
		updateAnglesAndScalars();
		setDirty(true);
	}
	
	public void setCurve(Point2D p1, Point2D c1, Point2D c2, Point2D p2) {		
		ctrls[P1] = new Point2D.Float((float)p1.getX(), (float)p1.getY());
		ctrls[CTRL1] = new Point2D.Float((float)c1.getX(), (float)c1.getY());
		ctrls[CTRL2] = new Point2D.Float((float)c2.getX(), (float)c2.getY());;
		ctrls[P2] = new Point2D.Float((float)p2.getX(), (float)p2.getY());
		updateAnglesAndScalars();
		setDirty(true);
	}

	public void setCurve(Point2D.Float p1, Point2D.Float c1, Point2D.Float c2, Point2D.Float p2){
		ctrls[0] = p1;
		ctrls[1] = c1;
		ctrls[2] = c2;
		ctrls[3] = p2;		
		updateAnglesAndScalars();
		setDirty(true);
	}

	public Point2D.Float[] getCurve() {
		return ctrls;
	}
	
	public CubicCurve2D getCubicCurve() {
		return curve;
	}	
	
	public ArrayList getEventNames() {
		return eventNames;
	}

	public void setEventNames(ArrayList eventNames) {
		this.eventNames = eventNames;
		setDirty(true);
	}

	public void addEventName(String symbol) {
		eventNames.add(symbol);
		setDirty(true);
	}

	/**
	 * @param symbol
	 */
	public void removeEventName(String symbol) {
		eventNames.remove(symbol);
		setDirty(true);		
	}

	public Point2D.Float getLabelOffset() {
		return labelOffset;
	}

	public void setLabelOffset(Point2D.Float labelOffset) {
		this.labelOffset = labelOffset;
		setDirty(true);
	}
	
	protected boolean isRigidTranslation() {
		return rigidTranslation;
	}

	protected void setRigidTranslation(boolean rigid) {
		this.rigidTranslation = rigid;
	}
	
	/**
	 * These helper methods are needed for PSTricks export to
	 * determine whether the edge is a straight line or a 
	 * Bezier curve.
	 * 
	 * @return double The desired angle
	 * 
	 * @author Sarah-Jane Whittaker
	 */
	protected double getAngle1()
	{
		return angle1;
	}
	protected double getAngle2()
	{
		return angle2;
	}
	
	protected void arcMore(){
		if(angle1 == 0 && angle2 == 0){
			angle1 = Math.PI/8;
			angle2 = Math.PI/16;
			s1 = this.DEFAULT_CONTROL_HANDLE_SCALAR;
			s2 = this.DEFAULT_CONTROL_HANDLE_SCALAR * 2;
		}else{
			angle1 *= 1.2;
			angle2 *= 1.2;
		}		
	}
	
	protected void arcLess(){
		if(angle1 == 0 && angle2 == 0){
			return;
		}else{
			angle1 *= 0.8;
			angle2 *= 0.8;
		}		
	}
	
	/**
	 * KLUGE: all accesss to EdgeLayout should go through the Edge interface.
	 */
	public void setDirty(boolean b){
		super.setDirty(b);
		if(edge != null){
			edge.setDirty(b);
		}
	}	
}
