package presentation.fsa;

import java.awt.geom.Point2D;
import java.awt.geom.CubicCurve2D;
import presentation.Geometry;


public class EdgeLayout extends GraphicalLayout {

	private String[] eventNames = {""};
	private Point2D.Float[] bezierControls;
	
	// TODO use this class instead of mucking about with an array
	private CubicCurve2D.Float controls;
	
	public EdgeLayout(){
		bezierControls = new Point2D.Float[4];		
	}
	
	public EdgeLayout(Point2D.Float[] bezierControls, String[] eventNames){
		this.bezierControls = bezierControls;
		this.eventNames = eventNames;
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

	public String[] getEventNames() {
		return eventNames;
	}

	public void setEventNames(String[] eventNames) {
		this.eventNames = eventNames;
	}

}
