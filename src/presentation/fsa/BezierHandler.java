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
 * @author helen
 *
 */
public class BezierHandler extends EdgeHandler {

	private Ellipse2D.Double[] anchors;	
	private static final int RADIUS = 5;
	public static final int NO_INTERSECTION = -1;
	
	/**
	 * Index of last intersected control point anchor
	 * @see EdgeLayout.P1, EdgeLayout.CTRL1 etc.
	 */
	private int lastIntersected = NO_INTERSECTION;  
	
	/**
	 * @param edge
	 */
	public BezierHandler(BezierEdge edge) {
		super(edge);		
		anchors = new Ellipse2D.Double[4];                      
		setDirty(true);
	}
	
	public BezierEdge getEdge()
	{
		return (BezierEdge)super.getEdge();
	}
	
	/**
	 * Update my layout information from my getEdge().
	 */
	public void update() {
		// upper left corner, width and height
		int d = 2*RADIUS;
		anchors[BezierLayout.P1] = new Ellipse2D.Double(getEdge().getP1().x - RADIUS, getEdge().getP1().y - RADIUS, d, d); 
		anchors[BezierLayout.CTRL1] = new Ellipse2D.Double(getEdge().getCTRL1().x - RADIUS, getEdge().getCTRL1().y - RADIUS, d, d);				
		anchors[BezierLayout.CTRL2] = new Ellipse2D.Double(getEdge().getCTRL2().x - RADIUS, getEdge().getCTRL2().y - RADIUS, d, d);
		anchors[BezierLayout.P2] = new Ellipse2D.Double(getEdge().getP2().x - RADIUS, getEdge().getP2().y - RADIUS, d, d);
		setDirty(false);
	}
	
	public void draw(Graphics g) {
		if(isDirty()) update();
		
		if(visible){
			Graphics2D g2d = (Graphics2D)g;
					
			g2d.setColor(Color.BLUE);
			g2d.setStroke(GraphicalLayout.FINE_STROKE);
						
			for(int i=1; i<3; i++){  // don't display end point circles since not moveable.
				g2d.draw(anchors[i]);
			}
			
			g2d.drawLine((int)(getEdge().getP1().x), 
					(int)(getEdge().getP1().y), 
					(int)(getEdge().getCTRL1().x), 
					(int)(getEdge().getCTRL1().y));
			
			g2d.drawLine((int)(getEdge().getP2().x), 
					(int)(getEdge().getP2().y), 
					(int)(getEdge().getCTRL2().x), 
					(int)(getEdge().getCTRL2().y));
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
	
	/** 
	 * @return index of the last intersected control point handle, if no intersection
	 * returns <code>NO_INTERSECTION</code>. 
	 */
	public int getLastIntersected() {
		return lastIntersected;
	}
}
