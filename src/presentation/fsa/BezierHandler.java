/**
 * 
 */
package presentation.fsa;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import presentation.GraphicalLayout;

/**
 * Visual representation of the control points for the bezier curve
 * of an edge.  Used to modify the shape of a bezier edge.
 * 
 * @author helen bretzke
 *
 */
public class BezierHandler extends EdgeHandler {

	private Ellipse2D.Double[] anchors;	
	protected static final int RADIUS = 5;
	/**
	 * @param edge
	 */
	public BezierHandler(BezierEdge edge) {
		super(edge);		
		anchors = new Ellipse2D.Double[4];
		refresh();		
	}
	
	public BezierEdge getEdge()
	{
		return (BezierEdge)super.getEdge();
	}
	
	/**
	 * Update my layout information from my getEdge().
	 */
	public void refresh() {		
		int d = 2*RADIUS;
		// upper left corner, width and height of circle's bounding box
		anchors[BezierLayout.P1] = new Ellipse2D.Double(getEdge().getP1().x - RADIUS/2, getEdge().getP1().y - RADIUS/2, RADIUS, RADIUS); 
		anchors[BezierLayout.CTRL1] = new Ellipse2D.Double(getEdge().getCTRL1().x - RADIUS, getEdge().getCTRL1().y - RADIUS, d, d);				
		anchors[BezierLayout.CTRL2] = new Ellipse2D.Double(getEdge().getCTRL2().x - RADIUS, getEdge().getCTRL2().y - RADIUS, d, d);
		anchors[BezierLayout.P2] = new Ellipse2D.Double(getEdge().getP2().x - RADIUS/2, getEdge().getP2().y - RADIUS/2, RADIUS, RADIUS);
		setNeedsRefresh(false);
	}
	
	public void draw(Graphics g) {
		if(needsRefresh()) refresh();
		
		if(visible){
			Graphics2D g2d = (Graphics2D)g;
					
			g2d.setColor(Color.BLUE);
			g2d.setStroke(GraphicalLayout.FINE_STROKE);
			
			for(int i=1; i<3; i++){  // don't display end point circles since not moveable.
				g2d.setColor(Color.WHITE);
				g2d.fill(anchors[i]);
				g2d.setColor(Color.BLUE);
			}
			
			// solid endpoints
			/*g2d.fill(anchors[0]);
			g2d.fill(anchors[3]);*/
			
			g2d.drawLine((int)(getEdge().getP1().x), 
					(int)(getEdge().getP1().y), 
					(int)(getEdge().getCTRL1().x), 
					(int)(getEdge().getCTRL1().y));
			
			g2d.drawLine((int)(getEdge().getP2().x), 
					(int)(getEdge().getP2().y), 
					(int)(getEdge().getCTRL2().x), 
					(int)(getEdge().getCTRL2().y));
			
			/*g2d.drawLine((int)getEdge().getCTRL1().x,
					(int)getEdge().getCTRL1().y,
					(int)getEdge().getCTRL2().x,
					(int)getEdge().getCTRL2().y);
			*/
			
			for(int i=1; i<3; i++){  // don't display end point circles since not moveable.
				g2d.draw(anchors[i]);			
			}
			
			//g2d.fill(anchors[2]);
		}
	}
	
	/**
	 * @return true iff p intersects one of the control point circles. 
	 */
	public boolean intersects(Point2D p) {
		for(int i=0; i<4; i++){
			if(anchors[i]!=null && anchors[i].contains(p)){
				lastIntersected = i;
				return true;
			}			
		}
		lastIntersected = NO_INTERSECTION;
		return false;
	}
}
