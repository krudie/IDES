package presentation.fsa;

import java.awt.geom.Point2D;
import java.awt.geom.CubicCurve2D;
import java.util.ArrayList;

import presentation.Geometry;
import presentation.GraphicalLayout;


public class EdgeLayout extends GraphicalLayout {

	private ArrayList eventNames;
	private Point2D.Float[] bezierControls;
	private Point2D.Float labelOffset;
		
	public static final int P1 = 0;	
	public static final int CTRL1 = 1;
	public static final int CTRL2 = 2;
	public static final int P2 = 3;
	
	public EdgeLayout(){
		bezierControls = new Point2D.Float[4];
		eventNames = new ArrayList();
		labelOffset = new Point2D.Float(5,5);
	}
	
	public EdgeLayout(Point2D.Float[] bezierControls){
		this.bezierControls = bezierControls;
		eventNames = new ArrayList();
		labelOffset = new Point2D.Float(5,5);
	}
	
	public EdgeLayout(Point2D.Float[] bezierControls, ArrayList eventNames){
		this.bezierControls = bezierControls;
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
		bezierControls = computeCurve(n1, n2);
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
	public static Point2D.Float[] computeCurve(NodeLayout s, NodeLayout t){
		Point2D.Float[] ctrls = new Point2D.Float[4];
		// if source and target nodes are the same, compute a self-loop
		if(s.equals(t)){
			// TODO
			throw new UnsupportedOperationException("Self-loops not net implemented.");
		}else{		
			// otherwise compute a straight edge		
			Point2D.Float c1 = s.getLocation();
			Point2D.Float c2 = t.getLocation();
			// compute intersection of straight line from c1 to c2 with arcs of nodes
			Point2D.Float dir = Geometry.subtract(c2, c1);
			float norm = (float)Geometry.norm(dir);
			Point2D.Float unit = Geometry.unit(dir);  // computing norm twice :(
			ctrls[P1] = Geometry.add(c1, Geometry.scale(unit, s.getRadius()));
			ctrls[CTRL1] = Geometry.add(c1, Geometry.scale(unit, norm/3));
			ctrls[CTRL2] = Geometry.add(c1, Geometry.scale(unit, 2*norm/3));
			ctrls[P2] = Geometry.add(c2, Geometry.scale(unit, -t.getRadius()-ArrowHead.SHORT_HEAD_LENGTH));
		}
		return ctrls;
	}
	
	/**
	 * Returns an array of 4 control points for a straight, directed edge from
	 * <code>s</code>, the layout for the source node to endpoint <code>c2</code>.
	 * 
	 * @param s layout for source node
	 * @param c2 endpoint for the edge
	 * @return array of 4 Bezier control points for a straight, directed edge from s to c2. 
	 */
	public static Point2D.Float[] computeCurve(NodeLayout s, Point2D.Float c2){
		Point2D.Float[] ctrls = new Point2D.Float[4];
		Point2D.Float c1 = s.getLocation();
		Point2D.Float dir = Geometry.subtract(c2, c1);
		float norm = (float)Geometry.norm(dir);
		Point2D.Float unit = Geometry.unit(dir);  // computing norm twice :(
		ctrls[P1] = Geometry.add(c1, Geometry.scale(unit, s.getRadius()));
		ctrls[CTRL1] = Geometry.add(c1, Geometry.scale(unit, norm/3));
		ctrls[CTRL2] = Geometry.add(c1, Geometry.scale(unit, 2*norm/3));
		ctrls[P2] = c2;		
		return ctrls;
	}
	
	
	/**
	 * Returns a cubic bezier curve representing a self-loop for the
	 * given node.
	 * 
	 * @param n the node
	 * @return a self-loop
	 */
	public static CubicCurve2D computeSelfLoop(Node n){
		// TODO find a clear bit of arc - this is why we need node
		// instead of just NodeLayout
		
		// For now, loop above node by default
		// direction vectors (-r, 2r) (r, 2r)
		
		
		return null;
	}
	
	public Point2D.Float[] getCurve() {
		return bezierControls;
	}
	
	public void setCurve(Point2D.Float[] bezierControls) {
		this.bezierControls = bezierControls;
	}
	
	public void setCurve(Point2D p1, Point2D c1, Point2D c2, Point2D p2) {		
		bezierControls[P1] = new Point2D.Float((float)p1.getX(), (float)p1.getY());
		bezierControls[CTRL1] = new Point2D.Float((float)c1.getX(), (float)c1.getY());
		bezierControls[CTRL2] = new Point2D.Float((float)c2.getX(), (float)c2.getY());;
		bezierControls[P2] = new Point2D.Float((float)p2.getX(), (float)p2.getY());
	}

	public void setCurve(Point2D.Float p1, Point2D.Float c1, Point2D.Float c2, Point2D.Float p2){
		bezierControls[0] = p1;
		bezierControls[1] = c1;
		bezierControls[2] = c2;
		bezierControls[3] = p2;
	}

	public ArrayList getEventNames() {
		return eventNames;
	}

	public void setEventNames(ArrayList eventNames) {
		this.eventNames = eventNames;
	}

	public void addEventName(String symbol) {
		eventNames.add(symbol);		
	}

	public Point2D.Float getLabelOffset() {
		return labelOffset;
	}

	public void setLabelOffset(Point2D.Float labelOffset) {
		this.labelOffset = labelOffset;
	}

}
