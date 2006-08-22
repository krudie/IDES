/**
 * 
 */
package presentation.fsa;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.Set;

import presentation.Geometry;
import presentation.GraphicalLayout;

/**
 * A visual representation of the arrow indicating the initial 
 * node in a finite state machine.
 * 
 * @author Helen Bretzke
 * DES Lab, ECE Dept., Queen's University 
 * 2 August 2006
 *
 */
public class InitialArrow extends Edge {

	private Line2D.Float line;
	private ArrowHead arrowHead;
		
	public InitialArrow(Node target) {
		super(null, target);
		Point2D.Float offset = new Point2D.Float( (float)(target.getShape().getBounds2D().getWidth() * 2), 0f);
		line = new Line2D.Float(Geometry.add(target.getLocation(), offset), target.getLocation());
		arrowHead = new ArrowHead();
		setHandler(new Handler(this));
	}
	
	public void draw(Graphics g){
		// TODO implement
		if(isDirty())
		{
			getHandler().refresh();
			refresh();
		}

		Graphics2D g2d = (Graphics2D)g;
		
		if(highlighted || getTarget() != null && getTarget().isHighlighted()){
			setHighlighted(true);
			g2d.setColor(getLayout().getHighlightColor());
		}else{
			g2d.setColor(getLayout().getColor());
		}		
		
		if(isSelected()){
			g2d.setColor(getLayout().getSelectionColor());
			getHandler().setVisible(true);
		}else{
			//handler.setVisible(false); // KLUGE to clean up after modify edge tool
			getHandler().setVisible(false);
		}

		g2d.setStroke(GraphicalLayout.WIDE_STROKE);
		g2d.draw(line);
		
		// Compute the direction and location of the arrow head
		AffineTransform at = new AffineTransform();
		Point2D.Float unitArrowDir = Geometry.unitDirectionVector(line.getP1(), line.getP2());
	    Point2D.Float tEndPt = getTargetEndPoint();
	    Point2D.Float basePt = Geometry.add(tEndPt, Geometry.scale(unitArrowDir, -(ArrowHead.SHORT_HEAD_LENGTH)));
	    
		at.setToTranslation(basePt.x, basePt.y);
		g2d.transform(at);
		
	    // rotate to align with end of curve
	    double rho = Geometry.angleFrom(ArrowHead.axis, unitArrowDir);
		at.setToRotation(rho);		
		g2d.transform(at);
		g2d.fill(arrowHead);
		// invert transformation
		at.setToRotation(-rho);
		g2d.transform(at);
		at.setToTranslation(-basePt.x, -basePt.y);		
		g2d.transform(at);
		
		// draw handler
	    super.draw(g);		
	}
	
	public void refresh(){
		// TODO compute the endpoints of the visible line
				
		setDirty(false);
	}
	
	/* (non-Javadoc)
	 * @see presentation.fsa.Edge#createExportString(java.awt.Rectangle, int)
	 */
	@Override
	public String createExportString(Rectangle selectionBox, int exportType) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see presentation.fsa.Edge#intersectionWithBoundary(presentation.fsa.Node, int)
	 */
	@Override
	public Point2D.Float intersectionWithBoundary(Node node, int type) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see presentation.fsa.Edge#addEventName(java.lang.String)
	 */
	@Override
	public void addEventName(String symbol) {
		// No events on this edge type		
	}

	/* (non-Javadoc)
	 * @see presentation.fsa.Edge#getSourceEndPoint()
	 */
	@Override
	public Point2D.Float getSourceEndPoint() {	
		// try casting the return from line.getP1(); it should be of type Float
		return new Point2D.Float(line.x1, line.y1);
	}

	/* (non-Javadoc)
	 * @see presentation.fsa.Edge#getTargetEndPoint()
	 */
	@Override
	public Point2D.Float getTargetEndPoint() {
		// TODO compute intersection with node boundary
		return new Point2D.Float(line.x2, line.y2);
		// return arrowHead.getBasePt();
	}

	/** the only valid pointType for method setPoint : starting point of the line */
	public static final int P1 = 0;

	/**
	 * Sets the point of the given type to <code>point</code>. 
	 * @param point
	 * @param pointType must be of type P1 (? or P2 when Node is moved)
	 */
	public void setPoint(Float point, int pointType) {
		if(pointType == P1){
			line.x1 = point.x;
			line.y1 = point.y;
			setDirty(true);
		}
	}
	
	/* (non-Javadoc)
	 * @see presentation.fsa.Edge#computeEdge()
	 */
	@Override
	public void computeEdge() {
		
		// FIXME this is silly; decide when to store direction of line
		// What happens when move target node?
		// What happens when move handler (P1)?
		
		// preserve the direction and magnitude of the line		
		Point2D.Float direction = Geometry.subtract(line.getP2(), line.getP1());		
		line.x2 = getTarget().getLocation().x;
		line.y2 = getTarget().getLocation().y;
		Point2D.Float sourcePt = Geometry.subtract(line.getP1(), direction);
		line.x1 = sourcePt.x;
		line.y1 = sourcePt.y;
		refresh();
	}
	
	/**
	 * Sets my layout to fit among the set of existing edges adjacent to my target node.
	 * 
	 * @param neighbours the set of edges between my source and target nodes
	 */
	public void insertAmong(Set<Edge> neighbours) {
		// TODO implement
	}
	
	public boolean intersects(Point2D point) {
		// FIXME arrow head is only rotated to visible orientation when drawn
		// it is not stored at the location in memory...
		if(isSelected()){
			return line.contains(point) || getHandler().intersects(point);
		}
		return line.contains(point) || arrowHead.contains(point);
	}
	
	public void translate(float x, float y) {
		line.x1 += x;
		line.x2 += x;
		line.y1 += y;
		line.y2 += y;
		setDirty(true);
	}
	
	/* (non-Javadoc)
	 * @see presentation.fsa.Edge#isStraight()
	 */
	@Override
	public boolean isStraight() {		
		return true;
	}

	/**
	 * Edge handler for modifying the position and length of
	 * the initial arrow.
	 * 
	 * @author Helen Bretzke
	 */
	private class Handler extends EdgeHandler {

		private Ellipse2D.Double anchor;	
		private static final int RADIUS = 5;
		
		/**
		 * @param edge the edge to be handled
		 */
		public Handler(Edge edge) {
			super(edge);			
		}
		
		public void refresh(){
			int d = 2*RADIUS;
			// upper left corner, width and height of circle's bounding box
			anchor = new Ellipse2D.Double(getEdge().getSourceEndPoint().x - RADIUS, getEdge().getSourceEndPoint().y - d, d, d);
			setDirty(false);
		}
		
		/**		 
		 * @return true iff p intersects the control point circle. 
		 */
		public boolean intersects(Point2D p) {
			if(anchor.contains(p)){
				lastIntersected = P1;
				return true;
			}			
			lastIntersected = NO_INTERSECTION;
			return false;	
		}
		
		public void draw(Graphics g){
			if(isDirty()) refresh();
					
			if(!visible) return;
			
			Graphics2D g2d = (Graphics2D)g;
					
			g2d.setColor(Color.BLUE);
			g2d.setStroke(GraphicalLayout.FINE_STROKE);
			g2d.draw(anchor);
		}
	} // end Handler
	
} // end InitialArrow
