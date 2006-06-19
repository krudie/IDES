package presentation.fsa;

import java.awt.geom.Point2D;
import java.awt.geom.CubicCurve2D;
import java.util.ArrayList;

import presentation.Geometry;
import presentation.GraphicalLayout;


public class EdgeLayout extends GraphicalLayout {

	/**
	 * Indices of bezier curve control points. 
	 */
	public static final int P1 = 0;	
	public static final int CTRL1 = 1;
	public static final int CTRL2 = 2;
	public static final int P2 = 3;
		
	// Indicates whether an edge can be rigidly translated 
	// with both of its nodes or must be recomputed.
	// Default value is false;
	private boolean rigidTranslation = false; 
	private ArrayList eventNames;
	private Point2D.Float[] ctrls; // TODO Replace with CubicCurve2D
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
		eventNames = new ArrayList();
		labelOffset = new Point2D.Float(5,5);
	}
	
	public EdgeLayout(Point2D.Float[] bezierControls){
		this.ctrls = bezierControls;
		eventNames = new ArrayList();
		labelOffset = new Point2D.Float(5,5);
	}
	
	public EdgeLayout(Point2D.Float[] bezierControls, ArrayList eventNames){
		this.ctrls = bezierControls;
		this.eventNames = eventNames;
		labelOffset = new Point2D.Float(5,5);
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
		computeCurve(n1, n2);
		eventNames = new ArrayList();
		labelOffset = new Point2D.Float(5,5);
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
			throw new UnsupportedOperationException("Self-loops not net implemented.");
			// return;
		}
		
		Point2D.Float c1 = s.getLocation();
		Point2D.Float c2 = t.getLocation();
		// compute intersection of straight line from c1 to c2 with arcs of nodes
		Point2D.Float dir = Geometry.subtract(c2, c1);
		float norm = (float)Geometry.norm(dir);
		Point2D.Float unit = Geometry.unit(dir);  // computing norm twice :(
		ctrls[P1] = Geometry.add(c1, Geometry.scale(unit, s.getRadius()));
	
		if(angle1 == 0 && angle2 == 0){		
			// compute a straight edge		
			s1 = DEFAULT_CONTROL_HANDLE_SCALAR;
			s2 = DEFAULT_CONTROL_HANDLE_SCALAR;
			
			ctrls[CTRL1] = Geometry.add(c1, Geometry.scale(unit, (float)(norm * s1)));
			ctrls[CTRL2] = Geometry.add(c1, Geometry.scale(unit, (float)(2 * norm * s2)));			
			
			// This looks wrong: P2 should be on the circumference of the target node.
			ctrls[P2] = Geometry.add(c2, Geometry.scale(unit, -t.getRadius()-ArrowHead.SHORT_HEAD_LENGTH));			
		}else{ // TODO recompute the edge preserving the shape of the curve
			
		}
		setDirty(true);
	}
	
	/**
	 * Returns an array of 4 control points for a straight, directed edge from
	 * <code>s</code>, the layout for the source node to endpoint <code>c2</code>.
	 * 
	 * @param s layout for source node
	 * @param c2 endpoint for the edge
	 * @return array of 4 Bezier control points for a straight, directed edge from s to c2. 
	 */
	public void computeCurve(NodeLayout s, Point2D.Float c2){		
		Point2D.Float c1 = s.getLocation();
		Point2D.Float dir = Geometry.subtract(c2, c1);
		float norm = (float)Geometry.norm(dir);
		Point2D.Float unit = Geometry.unit(dir);  // computing norm twice :(
		ctrls[P1] = Geometry.add(c1, Geometry.scale(unit, s.getRadius()));
		ctrls[CTRL1] = Geometry.add(c1, Geometry.scale(unit, (float)(norm * s1)));
		ctrls[CTRL2] = Geometry.add(c2, Geometry.scale(unit, (float)(-1 * norm * s2)));
		ctrls[P2] = c2;
		setDirty(true);		
	}
	
	
	/**
	 * Returns a cubic bezier curve representing a self-loop for the
	 * given node.
	 * 
	 * @param n the node
	 * @return a self-loop
	 */
	public void computeSelfLoop(Node n){
		// TODO find a clear bit of arc - this is why we need node
		// instead of just NodeLayout.
		
		// For now, loop above node by default
		// direction vectors (-r, 2r) (r, 2r)
		
		
		setDirty(true);
	}
	
	public Point2D.Float[] getCurve() {
		return ctrls;
	}
	
	public void setCurve(Point2D.Float[] bezierControls) {
		this.ctrls = bezierControls;
		setDirty(true);
	}
	
	public void setCurve(Point2D p1, Point2D c1, Point2D c2, Point2D p2) {		
		ctrls[P1] = new Point2D.Float((float)p1.getX(), (float)p1.getY());
		ctrls[CTRL1] = new Point2D.Float((float)c1.getX(), (float)c1.getY());
		ctrls[CTRL2] = new Point2D.Float((float)c2.getX(), (float)c2.getY());;
		ctrls[P2] = new Point2D.Float((float)p2.getX(), (float)p2.getY());
		setDirty(true);
	}

	public void setCurve(Point2D.Float p1, Point2D.Float c1, Point2D.Float c2, Point2D.Float p2){
		ctrls[0] = p1;
		ctrls[1] = c1;
		ctrls[2] = c2;
		ctrls[3] = p2;
		setDirty(true);
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

	public Point2D.Float getLabelOffset() {
		return labelOffset;
	}

	public void setLabelOffset(Point2D.Float labelOffset) {
		this.labelOffset = labelOffset;
		setDirty(true);
	}
	
	/**
	 * Computes:
	 *  s1   scalar |(CTRL1 - P1)|/|(P2-P1)|
	 *  s2   scalar |(CTRL2 - P2)|/|(P1-P2)|
	 *  angle1  angle between  (CTRL1 - P1) and (P2-P1)
	 *  angle2  angle between  (CTRL2 - P2) and (P1-P2)
	 */
	public void update(){
		Point2D.Float p1p2 = Geometry.subtract(ctrls[P2], ctrls[P1]); 
		double n = Geometry.norm(p1p2);
		Point2D.Float p1c1 = Geometry.subtract(ctrls[CTRL1], ctrls[P1]);
		Point2D.Float p2c2 = Geometry.subtract(ctrls[CTRL2], ctrls[P2]);
		s1 = Geometry.norm(p1c1)/n;
		s2 = Geometry.norm(p2c2)/n;
		angle1 = Geometry.angleBetween(p1c1, p1p2);
		angle1 = Geometry.angleBetween(p2c2, Geometry.scale(p1p2, -1f));		
	}

	protected boolean isRigidTranslation() {
		return rigidTranslation;
	}

	protected void setRigidTranslation(boolean rigid) {
		this.rigidTranslation = rigid;
	}
}
