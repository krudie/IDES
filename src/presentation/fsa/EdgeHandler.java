package presentation.fsa;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * Visual representation of the control points for the bezier curve
 * of an edge.
 * 
 * @author Helen Bretzke
 *
 */
public class EdgeHandler extends GraphElement {

	private Edge edge;
		
	public EdgeHandler(Edge edge) {		
		this.edge = edge;
		setParent(edge);
		setDirty(true);		
	}
		
	/**
	 * FIXME this doesn't make much sense.
	 */
	public Rectangle bounds() {	
		return edge.bounds();
	}
	
	public Edge getEdge()
	{
		return (Edge)getParent();
	}	
	
}
