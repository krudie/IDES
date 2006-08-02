/**
 * 
 */
package presentation.fsa;

import java.awt.Point;
import java.awt.geom.Ellipse2D;

/**
 * @author helen
 *
 */
public class SelfLoopHandler extends EdgeHandler {
	
	private Ellipse2D.Double anchor;	
	private static final int RADIUS = 5;	
	
	/**
	 * @param edge
	 */
	public SelfLoopHandler(SelfLoop edge) {
		super(edge);
		refresh();		
	}
	
	public void refresh()
	{
		double d = 2 * RADIUS;
		anchor = new Ellipse2D.Double(getEdge().getLocation().x - RADIUS, getEdge().getLocation().y - RADIUS, d, d);
	}
	
	public boolean intersects(Point p)
	{
		return anchor.contains(p);
	}
	
}
