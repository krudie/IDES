package presentation.fsa;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * Visual representation of the modification handles of an edge.
 * 
 * @author Helen Bretzke
 *
 */
public class EdgeHandler extends GraphElement {

	public static final int NO_INTERSECTION = -1;
	private Edge edge;
	/**
	 * Index of last intersected control point anchor
	 * @see EdgeLayout.P1, EdgeLayout.CTRL1 etc.
	 */
	protected int lastIntersected = NO_INTERSECTION;
		
	public EdgeHandler(Edge edge) 
	{		
		this.edge = edge;
		setParent(edge);
//		setDirty(true);		
	}	
	
	public Edge getEdge()
	{
		return (Edge)getParent();
	}

	/** 
	 * @return index of the last intersected control point handle, if no intersection
	 * returns <code>NO_INTERSECTION</code>. 
	 */
	public int getLastIntersected() {
		return lastIntersected;
	}	
}
