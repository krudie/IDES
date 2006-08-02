package presentation.fsa;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import presentation.GraphicalLayout;
import presentation.PresentationElement;

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
		//update();
	}
		
	/**
	 * FIXME this doesn't make much sense.
	 */
	public Rectangle2D bounds() {	
		return edge.bounds();
	}
	
	public Edge getEdge()
	{
		return (Edge)getParent();
	}
}
