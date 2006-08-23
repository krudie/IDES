/**
 * 
 */
package presentation.fsa;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
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
 * DES Lab, ECE Dept. Queen's University 
 * 2 August 2006
 *
 */
public class InitialArrow extends Edge {

	private Point2D.Float direction;  // redundant since stored in target node's layout
	private Point2D.Float targetPoint;
	private Line2D.Float line;
	private ArrowHead arrowHead;
		
	public InitialArrow(Node target) {
		super(null, target);
		
		// TODO move this into computeEdge ///////////////////////////////////////////
		// will need to check the length and direction of the arrow
		// each time node is e.g. resized, loaded
		NodeLayout layout = (NodeLayout)target.getLayout();
		if(layout != null){
			direction = layout.getArrow();
		}
		
		// check the magnitude of the arrow shaft
		// make sure it is long enough
		double width = target.getShape().getBounds2D().getWidth();
		if(direction != null && Geometry.norm(direction) <= width){
			direction = Geometry.unit(direction);
			double n = target.getShape().getBounds2D().getWidth();
			direction = Geometry.scale( direction, (float)n );
		}

		if(layout == null || direction == null){	
			// compute default direction vector for this edge
			direction = new Point2D.Float( (float)(target.getShape().getBounds2D().getWidth() * -1), 0f);			
		}
		
		line = new Line2D.Float(Geometry.add(target.getLocation(), Geometry.scale(direction, -1)), target.getLocation());
		targetPoint = new Point2D.Float((float)line.getP2().getX(), (float)line.getP2().getY());
		arrowHead = new ArrowHead();
		setHandler(new Handler(this));
		refresh();
	}
	
	public void draw(Graphics g){
		
		if( ! isVisible() ) return;
		
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
		
		// visible segment of line
		Line2D visible = new Line2D.Float(line.getP1(), targetPoint);		
		g2d.draw(visible);
		
		// Compute the direction and location of the arrow head
		AffineTransform at = new AffineTransform();
		Point2D.Float unitArrowDir = Geometry.unit(direction); //Geometry.unitDirectionVector(line.getP1(), line.getP2());
	    Point2D.Float tEndPt = getTargetEndPoint();
	    Point2D.Float basePt = Geometry.add(tEndPt, Geometry.scale(unitArrowDir, -(ArrowHead.SHORT_HEAD_LENGTH)));
	    
		at.setToTranslation(basePt.x, basePt.y);
		g2d.transform(at);
		
	    // rotate to align with end of curve
	    double rho = Geometry.angleFrom(ArrowHead.axis, unitArrowDir);
		at.setToRotation(rho);
		g2d.transform(at);
		g2d.setStroke(GraphicalLayout.FINE_STROKE);
		g2d.draw(arrowHead);		
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
		// TODO update the shape (direction and magnitude) and
		// compute the endpoints of the visible line
		targetPoint = intersectionWithBoundary(getTarget(), Edge.TARGET_NODE);		
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
		
		Shape nodeShape = node.getShape();
		
		float epsilon = 0.00001f;		
		float tPrevious = 0f;
		float t = 0.5f;	
		float step = 0.5f;
		
		// use equation of line
		Point2D p = line.getP1();
		Point2D.Float d = direction;
		
		// FIXME What should happen if arrow shaft is contained in node shape?
		if(nodeShape.contains(p)){
			return null;
		}
		
		// the point on curve at param t
		Point2D L_t = Geometry.add(p, Geometry.scale(d, t)); 
	
		while(Math.abs(t - tPrevious) > epsilon){			
			step =  Math.abs(t - tPrevious);
			tPrevious = t;
			if(nodeShape.contains(L_t)){  // inside boundary				
				t -= step/2;
			}else{				
				t += step/2;				
			}								
			L_t = Geometry.add(p, Geometry.scale(d, t)); 
		}		
		
		return new Point2D.Float((float)L_t.getX(), (float)L_t.getY());
	}

	/**
	 * No transitions/events on this edge type. 
	 * 
	 * @see presentation.fsa.Edge#addEventName(java.lang.String)
	 */
	@Override
	public void addEventName(String symbol) {}

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
		return targetPoint;		
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
			// update direction vector here and in NodeLayout
			// TODO get rid of direction field
			direction = Geometry.subtract(line.getP2(), line.getP1());
			((NodeLayout)getTarget().getLayout()).setArrow(direction);
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
		//direction = Geometry.subtract(line.getP2(), line.getP1());	
		line.x2 = getTarget().getLocation().x;
		line.y2 = getTarget().getLocation().y;
		Point2D.Float sourcePt = Geometry.subtract(line.getP2(), direction);
		line.x1 = sourcePt.x;
		line.y1 = sourcePt.y;		
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
		boolean b = line.intersects(point.getX() - 4, point.getY() - 4, 8, 8);
		return b; // || arrowHead.contains(point);
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
	
	// Nothing to do with this type of edge except move and delete.
	public void showPopup(){}
	
	/**
	 * FIXME can't use arrow to compute bounds since it is never moved to where it is drawn
	 * (see java.awt.geom.AffineTransform)
	 */
	public Rectangle bounds() {
		return line.getBounds();
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
			refresh();
		}
		
		public void refresh(){
			int d = 2*RADIUS;
			// upper left corner, width and height of circle's bounding box
			anchor = new Ellipse2D.Double(getEdge().getSourceEndPoint().x - RADIUS, getEdge().getSourceEndPoint().y - RADIUS, d, d);
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
