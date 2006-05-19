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
	
	// TODO use this class instead of mucking about with an array
	private CubicCurve2D.Float controls;
	
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
		bezierControls = getCurve(n1, n2);
		eventNames = new ArrayList();
		labelOffset = new Point2D.Float(5,5);
	}
	
	/**
	 * Returns an array of 4 control points for a straight, directed edge from
	 * <code>n1</code> to <code>n2</code>.
	 * 
	 * @param n1 layout for source node
	 * @param n2 layout for target node
	 * @return array of 4 control points for a straight, directed edge from n1 to n2
	 */
	public Point2D.Float[] getCurve(NodeLayout n1, NodeLayout n2){
		Point2D.Float[] ctrls = new Point2D.Float[4];
		Point2D.Float c1 = n1.getLocation();
		Point2D.Float c2 = n2.getLocation();
		// compute intersection of straight line from c1 to c2 with arcs of nodes
		Point2D.Float dir = Geometry.subtract(c2, c1);
		float norm = (float)Geometry.norm(dir);
		Point2D.Float unit = Geometry.unit(dir);  // computing norm twice :(
		ctrls[Edge.P1] = Geometry.add(c1, Geometry.scale(unit, n1.getRadius()));
		ctrls[Edge.CTRL1] = Geometry.add(c1, Geometry.scale(dir, norm/3));
		ctrls[Edge.CTRL2] = Geometry.add(c1, Geometry.scale(dir, 2*norm/3));
		ctrls[Edge.P2] = Geometry.add(c2, Geometry.scale(dir, -n2.getRadius()));      
		return ctrls;
	}
	
	public Point2D.Float[] getCurve() {
		return bezierControls;
	}
	
	public void setCurve(Point2D.Float[] bezierControls) {
		this.bezierControls = bezierControls;
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
