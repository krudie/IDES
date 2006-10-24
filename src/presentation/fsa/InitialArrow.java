package presentation.fsa;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Set;

import presentation.Geometry;
import presentation.GraphicalLayout;

/**
 * A visual representation of the arrow on a node indicating the initial 
 *  state in a finite state machine.
 * 
 * @author Helen Bretzke
 * DES Lab, ECE Dept. Queen's University 
 * 2 August 2006
 *
 */
public class InitialArrow extends Edge {

	/** direction vector from tail of arrow shaft to centre of node */
	private Point2D.Float direction;  // redundant since stored in target node's layout
	
	/** point of intersection of direction vector with boundary of target node shape */
	private Point2D.Float targetPoint;
	
	/** the visible line representing the arrow shaft */
	private Line2D.Float line;
	
	/** the arrow head */
	private ArrowHead arrowHead;
 
	// For a fixed-length arrow shaft, max and min are equal; 
	// may wish to allow user to expand and shrink in future
	/** maximum distance between node centre and tail of arrow shaft */
	private double maxShaftLength = 2 * ( CircleNodeLayout.DEFAULT_RADIUS + 2 * CircleNodeLayout.RADIUS_MARGIN );
	
	/** minimum distance between node centre and tail of arrow shaft */
	private double minShaftLength = maxShaftLength;
		
	/**
	 * Creates an initial arrow on the given target node representing an initial state.
	 * Precondition: target != null
	 * 
	 * @param target the node representing an initial state
	 */
	public InitialArrow(Node target) {
		super(null, target);
				
		setLocation(target.getLocation());
		
		// TODO move this into computeEdge ///////////////////////////////////////////
		// will need to check the length and direction of the arrow
		// each time node is e.g. resized, loaded
		CircleNodeLayout layout = (CircleNodeLayout)target.getLayout();
		if(layout != null){
			direction = layout.getArrow();
		}
		
		// check the magnitude of the arrow shaft
		// make sure it is long enough
		maxShaftLength = target.getShape().getBounds2D().getHeight();
		minShaftLength = maxShaftLength;
		if(direction != null) {
			double norm = Geometry.norm(direction);
			if(norm < minShaftLength){
				direction = Geometry.unit(direction);			
				direction = Geometry.scale( direction, (float)minShaftLength);
			}
		}

		if(layout == null || direction == null){	
			// compute default direction and magnitude for this edge
			direction = new Point2D.Float( (float)(minShaftLength), 0f );
			// TODO do something about the missing node layout info?
		}
				
		layout.setArrow(direction);
		line = new Line2D.Float();			
		arrowHead = new ArrowHead();
		setHandler(new Handler(this));
		computeEdge();		
	}
	
	/**
	 * Renders this arrow in the given graphics context.
	 * 
	 * @param g the graphics context. 
	 */
	public void draw(Graphics g){
		
		if( ! isVisible() ) return;
		
		if(needsRefresh()){
			computeEdge();
		}

		Graphics2D g2d = (Graphics2D)g;
		
		if(highlighted || getTargetNode() != null && getTargetNode().isHighlighted()){
			setHighlighted(true);
			g2d.setColor(getLayout().getHighlightColor());
		}else{
			g2d.setColor(getLayout().getColor());
		}		
		
		if(isSelected() || getTargetNode() != null && getTargetNode().isSelected()){
			g2d.setColor(getLayout().getSelectionColor());
		}
				
		// only show handler if target node is not also selected
		if(isSelected() && getTargetNode() != null && !getTargetNode().isSelected()){
			getHandler().setVisible(true);
		}else{			
			getHandler().setVisible(false);
		}		

		g2d.setStroke(GraphicalLayout.WIDE_STROKE);			
		g2d.draw(line);		
		
		// Compute the direction and location of the arrow head
		AffineTransform at = new AffineTransform();
		Point2D.Float unitArrowDir = Geometry.unit(direction); //Geometry.unitDirectionVector(line.getP1(), line.getP2());
	    Point2D.Float tEndPt = getTargetEndPoint();
	    
	    // NOTE point movement will be constrained to be outside of node boundary so targetPt should never be null.
	    Point2D.Float basePt;	    
	    basePt = Geometry.add(tEndPt, Geometry.scale(unitArrowDir, -(ArrowHead.SHORT_HEAD_LENGTH)));
	   	    
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
		
	/**
	 * Using the shape, update the visible curve (in this case a straight line).
	 * 
	 * (non-Javadoc)
	 * @see presentation.fsa.Edge#computeEdge()
	 */
	@Override
	public void computeEdge() {
		setLocation(getTargetNode().getLocation());
		Point2D.Float sourcePt = Geometry.subtract(getLocation(), direction);
		line.x1 = sourcePt.x;
		line.y1 = sourcePt.y;
		targetPoint=intersectionWithBoundary(getTargetNode(), Edge.TARGET_NODE);
		line.x2 = targetPoint.x;
		line.y2 = targetPoint.y;		
		getHandler().refresh();
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
		return targetPoint;		
	}

	/** the only valid pointType for method setPoint : starting point of the line */
	public static final int P1 = 0;
	
	/**
	 * Sets the point of the given type to <code>point</code>.
	 * 
	 * @param point the new value for the point of the given type
	 * @param pointType must be of type P1 (? or P2 when Node is moved)
	 *
	 * @see presentation.fsa.Edge#setPoint(java.awt.geom.Point2D, int)
	 */	
	public void setPoint(Point2D point, int pointType) {
		if(pointType == P1){			
			// compute direction vector from P1 to centre of node
			direction = Geometry.subtract(getLocation(), point);
			double n = Geometry.norm(direction);			
			if( n < minShaftLength ){ // too short
				// constrain to min length
				direction = Geometry.unit(direction);			
				direction = Geometry.scale( direction, (float)minShaftLength );
			}
			if( n > maxShaftLength ){ // too long
				// constrain to max length
				direction = Geometry.unit(direction);			
				direction = Geometry.scale( direction, (float)maxShaftLength );
			}					
			/* 	TODO Since updating direction vector both in this class and in NodeLayout
			 	get rid of direction field and just use get and setArrow in NodeLayout 
			 */		
			((CircleNodeLayout)getTargetNode().getLayout()).setArrow(direction);			
			computeEdge();
		}				
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
		
			// NOTE point movement will be constrained to be outside of node boundary so targetPt should never be null.
			// no intersection with boundary
			if(nodeShape.contains(p)){
	//FIXME: this condition was hit a few times when node was dragged outside of drawing boundary
	//and when some operations called auto layout
	//			Exception in thread "AWT-EventQueue-0" java.lang.NullPointerException
	//			at presentation.fsa.InitialArrow.computeEdge(InitialArrow.java:191)
	//			at presentation.fsa.Node.recomputeEdges(Node.java:91)
	//			at presentation.fsa.CircleNode.refresh(CircleNode.java:72)
	//			at presentation.fsa.CircleNode.translate(CircleNode.java:204)
	//			at presentation.fsa.GraphElement.translate(GraphElement.java:160)
	//			at ui.tools.MovementTool.handleMouseDragged(MovementTool.java:56)
	//			at presentation.fsa.GraphDrawingView.mouseDragged(GraphDrawingView.java:282)
	//TEMP SOLUTION: reset arrow
				direction = new Point2D.Float( (float)(minShaftLength * -1), 0f);	
				return Geometry.add(getLocation(), Geometry.scale(direction, -1));
				//return null; //new Point2D.Float((float)p.getX(), (float)p.getY());
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

	/* (non-Javadoc)
	 * @see presentation.fsa.Edge#isMovable(int)
	 */
	@Override
	public boolean isMovable(int pointType) {		
		return pointType == P1;
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
		if(isSelected()){
			return line.contains(point) || getHandler().intersects(point);
		}
		boolean b = line.intersects(point.getX() - 4, point.getY() - 4, 8, 8);
		return b; // || arrowHead.contains(point);
		// FIXME arrow head is only rotated to visible orientation when drawn
		// it is not stored at the location in memory...
	}	
	
	/* (non-Javadoc)
	 * @see presentation.fsa.Edge#isStraight()
	 */
	@Override
	public boolean isStraight() {		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see presentation.fsa.Edge#straighten()
	 */
	@Override
	public void straighten() { 
		// does nothing since already a straight line
	}
	
	/**
	 * Does nothing since there are no transitions or events on this edge type. 
	 * 
	 * @see presentation.fsa.Edge#addEventName(java.lang.String)
	 */
	@Override
	public void addEventName(String symbol) {}

	/* TODO ask SJW what she needs to export to LaTeX and eps. 
	 * Sarah: OK, I've been in and I'm working on it.
	 * (non-Javadoc)
	 * @see presentation.fsa.Edge#createExportString(java.awt.Rectangle, int)
	 */
	@Override
	public String createExportString(Rectangle selectionBox, int exportType) 
	{
		Rectangle initialArrowBounds = bounds();
		
		return "    \\psline[arrowsize=4pt]{->}(" 
			+ (initialArrowBounds.getMinX() - selectionBox.x) + "," 
			+ (selectionBox.height + selectionBox.y - initialArrowBounds.getMinY()) + ")(" 
			+ (initialArrowBounds.getMaxX() - selectionBox.x) + "," 
			+ (selectionBox.height + selectionBox.y - initialArrowBounds.getMaxY()) + ")\n";

	}

	/**
	 * Edge popup disabled since there is nothing to do with this type of edge 
	 * except move with mouse and toggle on/off via target's NodePopup.
	 */
	public void showPopup(Component c){}
	
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
			setNeedsRefresh(false);
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
		
		/**
		 * Renders this handler in the given graphics context.
		 * 
		 * @param g the graphics context. 
		 */
		public void draw(Graphics g){
			if(needsRefresh()) refresh();
					
			if(!visible) return;
			
			Graphics2D g2d = (Graphics2D)g;
					
			g2d.setColor(Color.BLUE);
			g2d.setStroke(GraphicalLayout.FINE_STROKE);
			g2d.draw(anchor);
		}
	} // end Handler
	
} // end InitialArrow
