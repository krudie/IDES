package presentation.fsa;

import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.ArrayList;
import java.util.Iterator;

import presentation.CubicCurve2Dex;
import presentation.Geometry;
import presentation.GraphicalLayout;


public class BezierLayout extends GraphicalLayout {

	private BezierEdge edge;
	private boolean selfLoop = false;	
	
	/**
	 * Indices of bezier curve control points. 
	 */
	public static final int P1 = 0;	
	public static final int CTRL1 = 1;
	public static final int CTRL2 = 2;
	public static final int P2 = 3;
	public static final double EPSILON = 0.0001; // lower bound for abs(angles), below which is angles set to zero.  
	
	private ArrayList eventNames;	
	protected CubicCurve2Dex curve;

	// Compact representation of data required to maintain shape of edge while moving
	// one or both of its nodes.
	private static final double DEFAULT_CONTROL_HANDLE_SCALAR = 1.0/3.0f;
	protected double s1 = DEFAULT_CONTROL_HANDLE_SCALAR;  // scalar |(CTRL1 - P1)|/|(P2-P1)|
	protected double s2 = DEFAULT_CONTROL_HANDLE_SCALAR;  // scalar |(CTRL2 - P2)|/|(P1-P2)|
	protected double angle1 = 0.0; // angle between  (CTRL1 - P1) and (P2-P1)
	protected double angle2 = 0.0; // angle between  (CTRL2 - P2) and (P1-P2) 
	
	private static final Float UNIT_VERTICAL = new Point2D.Float(0, -1);
		
	public BezierLayout(){	
		curve = new CubicCurve2Dex();
		eventNames = new ArrayList();
		setLabelOffset(new Point2D.Float(5,5));
	}
	
	public BezierLayout(Point2D.Float[] bezierControls, boolean selfLoop){		
		curve = new CubicCurve2Dex();
		curve.setCurve(bezierControls, 0);
		this.selfLoop = selfLoop;
		eventNames = new ArrayList();
		setLabelOffset(new Point2D.Float(5,5));
		updateAnglesAndScalars();
		setDirty(true);
	}
	
	public BezierLayout(Point2D.Float[] bezierControls, ArrayList eventNames){		
		curve = new CubicCurve2Dex();
		curve.setCurve(bezierControls, 0);		
		this.eventNames = eventNames;
		setLabelOffset(new Point2D.Float(5,5));
		setDirty(true);
		updateAnglesAndScalars();
	}

	public void setEdge(BezierEdge edge){
		this.edge = edge;
		setDirty(true);
	}
	
	protected BezierEdge getEdge()
	{
		return edge;
	}
	
	/**
	 * Constructs an edge layout object for a straight, directed edge from
	 * <code>n1</code> to <code>n2</code>.
	 * 
	 * @param n1 layout for source node
	 * @param n2 layout for target node
	 */
	public BezierLayout(NodeLayout sourceLayout, NodeLayout targetLayout){		
		curve = new CubicCurve2Dex();		
		computeCurve(sourceLayout, targetLayout);		
		eventNames = new ArrayList();
		setLabelOffset(new Point2D.Float(5,5));
		//updateAnglesAndScalars();
	}
	
	public boolean equals(Object o)
	{
		BezierLayout other = (BezierLayout)o;
		return other.curve.equals(this.curve) &&
				other.getLabelOffset().equals(this.getLabelOffset());
	}
	
	/**
	 * Calls computeCurve(NodeLayout s, NodeLayout t) with source and target
	 * layouts for this layout's edge. 
	 */
	public void computeCurve(){
		if(edge != null){
			computeCurve((NodeLayout)edge.getSource().getLayout(), (NodeLayout)edge.getTarget().getLayout());			
		}
	}
	
	/**
	 * Returns an array of 4 control points for a straight, directed edge from
	 * <code>s</code>, the layout for the source node to <code>t</code>, the 
	 * layout for the target node.
	 * 
	 * Precondition: must call updateAnglesAndScalars() before calling this method.
	 * 
	 * @param s layout for source node, s != null
	 * @param t layout for target node, t != null
	 * @return array of 4 Bezier control points for a straight, directed edge
	 */
	public void computeCurve(NodeLayout s, NodeLayout t){

		////////////////////////////////////////////////////////////////
		// if source and target nodes are the same, compute a self-loop
		if(s.equals(t) && angle1 == 0 && angle2 == 0){
				computeDefaultSelfLoop(s);
				selfLoop = true;
				return;
		}
		////////////////////////////////////////////////////////////////
		
		
		Point2D.Float centre1 = s.getLocation();
		Point2D.Float centre2 = t.getLocation();		
		
		// FIXME endpoints must be spaced around node arc; need to know about fellow edges.
		// IDEA have the NodeLayout wiggle (rotate edges about centre) the desired/ideal adjacent edge 
		// endpoints as calculated by EdgeLayout.
		
		Point2D.Float[] ctrls = new Point2D.Float[4]; 
		
		// FIXME No curves happen on/after save.
		if(s.equals(t)){  
			// endpoints are at intersections of circle with rotations from vertical vector			
			ctrls[P1] = Geometry.add(centre1, Geometry.rotate(Geometry.scale(UNIT_VERTICAL, s.getRadius()), angle1));
			ctrls[P2] = Geometry.add(centre1, Geometry.rotate(Geometry.scale(UNIT_VERTICAL, s.getRadius()), angle2));
			ctrls[CTRL1] = Geometry.add(ctrls[P1], Geometry.rotate(Geometry.scale(UNIT_VERTICAL, (float)s1), angle1));
			ctrls[CTRL2] = Geometry.add(ctrls[P2], Geometry.rotate(Geometry.scale(UNIT_VERTICAL, (float)s2), angle2));			
		}else{
			
			Point2D.Float base = Geometry.subtract(centre2, centre1);
			float norm = (float)Geometry.norm(base);
			Point2D.Float unitBase = Geometry.unit(base);  // computing norm twice :(
		
			// endpoints are at node centres
			ctrls[P1] = s.getLocation();//		
			ctrls[P2] = t.getLocation();
			
			
			// endpoints are at intersection of straight line from centre1 to centre2 with arcs of nodes
//			ctrls[P1] = Geometry.add(centre1, Geometry.scale(unitBase, s.getRadius()));//		
//			ctrls[P2] = Geometry.add(centre2, Geometry.scale(unitBase, -t.getRadius())); // -ArrowHead.SHORT_HEAD_LENGTH));
			base = Geometry.subtract(ctrls[P2], ctrls[P1]);
			norm = (float)Geometry.norm(base);
			unitBase = Geometry.unit(base);		
		
			if(isStraight()){  // compute a default straight edge	
				angle1 = 0;
				angle2 = 0;					
				s1 = DEFAULT_CONTROL_HANDLE_SCALAR;
				s2 = DEFAULT_CONTROL_HANDLE_SCALAR;

				ctrls[CTRL1] = Geometry.add(ctrls[P1], Geometry.scale(unitBase, (float)(norm * s1)));			
				ctrls[CTRL2] = Geometry.add(ctrls[P2], Geometry.scale(unitBase, -(float)(norm * s2)));			
				
			}else{ // recompute the edge preserving the shape of the curve

				ctrls[CTRL1] = Geometry.add(ctrls[P1], Geometry.rotate(Geometry.scale(base, (float)s1), angle1)); 
				ctrls[CTRL2] = Geometry.add(ctrls[P2], Geometry.rotate(Geometry.scale(base, -(float)s2), angle2));

			}
		}		
		curve.setCurve(ctrls, 0);		
		Point2D midpoint = Geometry.midpoint(curve);
	    setLocation((float)midpoint.getX(), (float)midpoint.getY());
		setDirty(true);
	}	

	/**
	 * @return true iff the edge is has tangents within angle EPSILON of being parallel to straight edge.
	 */
	protected boolean isStraight() {		
		return Math.abs(angle1) < EPSILON && Math.abs(angle2) < EPSILON;
	}

	/**
	 * Returns an array of 4 control points for a straight, directed edge from
	 * <code>s</code>, the layout for the source node to endpoint <code>c2</code>.
	 * 
	 * @param s layout for source node
	 * @param endPoint endpoint for the edge	  
	 */
	public void computeCurve(NodeLayout s, Point2D.Float endPoint){		

		Point2D.Float[] ctrls = new Point2D.Float[4];
		Point2D.Float centre1 = s.getLocation();
		ctrls[P1] = centre1;
		
		if(s.getLocation().distance(endPoint) < 0.00001 ){ // TODO within epsilon of 0
			// set control points to node's centre
			ctrls[CTRL1] = centre1;
			ctrls[CTRL2] = centre1;
		}else{				
			Point2D.Float dir = Geometry.subtract(endPoint, centre1);		
			float norm = (float)Geometry.norm(dir);			
			Point2D.Float unit = Geometry.unit(dir);  // computing norm twice :(
			
	//		ctrls[P1] = Geometry.add(centre1, Geometry.scale(unit, s.getRadius()));
			
			dir = Geometry.subtract(endPoint, ctrls[P1]);
			norm = (float)Geometry.norm(dir);
			unit = Geometry.unit(dir);
			ctrls[CTRL1] = Geometry.add(ctrls[P1], Geometry.scale(unit, (float)(norm * s1)));
			ctrls[CTRL2] = Geometry.add(ctrls[P1], Geometry.scale(unit, (float)(2 * norm * s2)));
		}
		
		ctrls[P2] = endPoint;		
		curve.setCurve(ctrls, 0);
		Point2D midpoint = Geometry.midpoint(curve);
	    setLocation((float)midpoint.getX(), (float)midpoint.getY());
		setDirty(true);		
	}
	
	
	/**
	 * Returns a cubic bezier curve representing a self-loop for the
	 * given node layout oriented in the given unit direction.
	 * 
	 * @param s the node layout	 
	 */
	public void computeDefaultSelfLoop(NodeLayout s){		
		// rotate direction vector by alpha and -alpha
		double angleScalar = 5 * s.getRadius() / NodeLayout.DEFAULT_RADIUS;  
		angle1 = -Math.PI/angleScalar;
		angle2 = -angle1;
		s1 = 3f*NodeLayout.DEFAULT_RADIUS;
		s2 = 3f*NodeLayout.DEFAULT_RADIUS;						
		Point2D.Float axis = Geometry.scale(UNIT_VERTICAL, s.getRadius());
		
		Point2D.Float v1 = Geometry.rotate(axis, angle1);
		Point2D.Float v2 = Geometry.rotate(axis, angle2);
		
		Point2D.Float[] ctrls = new Point2D.Float[4];
		
		ctrls[P1] = Geometry.add(s.getLocation(), v1);
		ctrls[P2] = Geometry.add(s.getLocation(), v2);
		ctrls[CTRL1] = Geometry.add(ctrls[P1], Geometry.scale(Geometry.unit(v1), (float)s1));
		ctrls[CTRL2] = Geometry.add(ctrls[P2], Geometry.scale(Geometry.unit(v2), (float)s2));
		
		curve.setCurve(ctrls, 0);
		Point2D midpoint = Geometry.midpoint(curve);
	    setLocation((float)midpoint.getX(), (float)midpoint.getY());
	    selfLoop = true;
		setDirty(true);
	}
		
	
	/**
	 * Computes and stores:
	 *  s1   scalar |(CTRL1 - P1)|/|(P2-P1)|
	 *  s2   scalar |(CTRL2 - P2)|/|(P1-P2)|
	 *  angle1  angle between  (CTRL1 - P1) and (P2-P1)
	 *  angle2  angle between  (CTRL2 - P2) and (P1-P2)
	 *  
	 *  In case of self-loop stores angles from UNIT_VERTICAL to (CTRL2 - P2) and (CTRL1 - P1)
	 *  and scalars are simply the lengths of (CTRL2 - P2) and (CTRL1 - P1).
	 */
	private void updateAnglesAndScalars(){
		
		// IDEA should there be constraints on the angle to control point 
		// e.g. abs(angle between base line and tangent) <= PI/2?
		
		Point2D.Float p1p2 = Geometry.subtract(curve.getP2(), curve.getP1());	
		Point2D.Float p2p1 = Geometry.subtract(curve.getP1(), curve.getP2());
		Point2D.Float p1c1 = Geometry.subtract(curve.getCtrlP1(), curve.getP1());
		Point2D.Float p2c2 = Geometry.subtract(curve.getCtrlP2(), curve.getP2());		
				
		if(selfLoop){			
			s1 = Geometry.norm(p1c1);
			s2 = Geometry.norm(p2c2);		
			angle1 = Geometry.angleFrom(UNIT_VERTICAL, p1c1);
			angle2 = Geometry.angleFrom(UNIT_VERTICAL, p2c2);
		}else{
			double n = Geometry.norm(p1p2);
			
			if(n >= 1){			
				s1 = Geometry.norm(p1c1)/n;
				s2 = Geometry.norm(p2c2)/n;		
				angle1 = Geometry.angleFrom(p1p2, p1c1);
				angle2 = Geometry.angleFrom(p2p1, p2c2);
			}else{
				// FIXME do what? set to defaults?
			}
		}
		
		// DEBUG
		assert(!Double.isNaN(angle1));
		assert(!Double.isNaN(angle2));
	}

	/** 
	 * Sets the control point with the given index to <code>point</code>.  
	 * Precondition: <code>index</code> is a valid index i.e. P1, P2, CTRL1, CTRL2.
	 *   
	 * @param point the value of the control point
	 * @param index the index of the control point to be set
	 */
	public void setPoint(Point2D.Float point, int index){		
		
		switch (index){
			case P1:
				curve.x1 = point.x;
				curve.y1 = point.y;
				break;
			case P2:
				curve.x2 = point.x;
				curve.y2 = point.y;
				break;				
			case CTRL1:
				curve.ctrlx1 = point.x;
				curve.ctrly1 = point.y;
				break;
			case CTRL2:
				curve.ctrlx2 = point.x;
				curve.ctrly2 = point.y;
				break;
			default: throw new IllegalArgumentException("Invalid control point index: " + index);
		}
		
// DEBUG  OKAY before call updateAnglesEtc...
		edge.assertAllPointsNumbers(curve);
		
		updateAnglesAndScalars();				
		setDirty(true);
	}
		
	public CubicCurve2Dex getCubicCurve() {
		return curve;
	}	
	
	/**
	 * @param cubicCurve
	 */
	protected void setCurve(CubicCurve2Dex cubicCurve) {
		this.curve = cubicCurve;		
	}	
	
	/**
	 * Manage the set of event names to appear on the edge label. 
	 */
	public ArrayList getEventNames() {
		return eventNames;
	}

	public void setEventNames(ArrayList eventNames) {
		this.eventNames = eventNames;
		updateTextFromEventNames();
		setDirty(true);
	}

	public void addEventName(String symbol) {
		eventNames.add(symbol);
		updateTextFromEventNames();
		setDirty(true);
	}
	
	public void removeEventName(String symbol) {
		eventNames.remove(symbol);
		updateTextFromEventNames();
		setDirty(true);		
	}
	
	private void updateTextFromEventNames()
	{
		// Concat label from associated event[s]
	    String s = "";	    

		if(eventNames != null){
		    Iterator iter = eventNames.iterator();
		    while(iter.hasNext()){
		    	s += (String)iter.next();
		    	s += ", ";
		    }
		    s = s.trim();
		    if(s.length()>0) s = s.substring(0, s.length() - 1);
		}			
	    setText(s);
	}
	
	////////////////////////////////////////////////////////////
	/**
	 * Computes the angles and scalars such that this and other curve away from each other.
	 * Precondition: this.source = other.target and other.source = this.target and both are straight edges.
	 * 
	 * @param other another edge layout
	 */
	protected void arcAway(BezierLayout other){
		this.angle1 = Math.PI/6;
		this.angle2 = this.angle1/2;
		other.angle1 = this.angle1;
		other.angle2 = this.angle2;
		this.s1 = DEFAULT_CONTROL_HANDLE_SCALAR;
		this.s2 = 2 * DEFAULT_CONTROL_HANDLE_SCALAR;
		other.s1 = DEFAULT_CONTROL_HANDLE_SCALAR;
		other.s2 = 2 * DEFAULT_CONTROL_HANDLE_SCALAR;		
	}
	
	protected void arcMore()
	{
		
	}
	
	protected void arcLess()
	{
				
	}
		
	/**
	 * TODO DEBUG
	 */
	protected void symmetrize(){
		Point2D.Float[] points=new Point2D.Float[4];
		points[0]=Geometry.translate(curve.getP1(),-curve.getX1(),-curve.getY1());
		points[1]=Geometry.translate(curve.getCtrlP1(),-curve.getX1(),-curve.getY1());
		points[2]=Geometry.translate(curve.getCtrlP2(),-curve.getX1(),-curve.getY1());
		points[3]=Geometry.translate(curve.getP2(),-curve.getX1(),-curve.getY1());
		float edgeAngle=(float)Math.atan(Geometry.slope(curve.getP1(),curve.getP2()));
		points[0]=Geometry.rotate(points[0],-edgeAngle);
		points[1]=Geometry.rotate(points[1],-edgeAngle);
		points[2]=Geometry.rotate(points[2],-edgeAngle);
		points[3]=Geometry.rotate(points[3],-edgeAngle);
		
		double quadrantFix1=(points[0].x-points[1].x>0)?Math.PI:0;
		double quadrantFix2=(points[2].x-points[3].x>0)?Math.PI:0;
		
		float a1=(float)Math.atan(Geometry.slope(points[0],points[1]));
		float a2=(float)Math.atan(Geometry.slope(points[3],points[2]));
		float angle=(float)(Math.abs(a1)+Math.abs(a2))/2F;
		float distance=(float)(points[0].distance(points[1])+points[2].distance(points[3]))/2F;
		
		points[1]=Geometry.rotate(new Point2D.Float(distance,0),(angle*Math.signum(a1)+quadrantFix1));
		points[2]=Geometry.rotate(new Point2D.Float(distance,0),(angle*Math.signum(a2)+quadrantFix2+Math.PI));
		points[2].x+=points[3].x;
		points[2].y+=points[3].y;
		
		a1=(float)Math.atan(Geometry.slope(points[0],points[1]));
		a2=(float)Math.atan(Geometry.slope(points[3],points[2]));

		points[1]=Geometry.rotate(points[1],edgeAngle);
		points[2]=Geometry.rotate(points[2],edgeAngle);
		points[0]=new Point2D.Float((float)curve.getX1(), (float)curve.getY1());
		points[1]=Geometry.translate(points[1],curve.getX1(),curve.getY1());
		points[2]=Geometry.translate(points[2],curve.getX1(),curve.getY1());
		points[3]=new Point2D.Float((float)curve.getX2(), (float)curve.getY2());

		curve.setCurve(points, 0);
		setDirty(true);
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

	// Indicates whether an edge can be rigidly translated 
	// with both of its nodes or must be recomputed.
	// Default value is false;
	private boolean rigidTranslation = false; 
	
	protected boolean isRigidTranslation() {
		return rigidTranslation;
	}

	protected void setRigidTranslation(boolean rigid) {
		this.rigidTranslation = rigid;
	}

	/**
	 * @param b
	 */
	public void setSelfLoop(boolean b) {
		selfLoop = b;		
	}
	
}
