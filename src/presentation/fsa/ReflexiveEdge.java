/**
 * 
 */
package presentation.fsa;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import model.fsa.FSATransition;
import model.fsa.ver1.Transition;

import presentation.CubicParamCurve2D;
import presentation.Geometry;
import presentation.GraphicalLayout;
import util.BentoBox;

/**
 * A symmetric self-loop manipulated by a single control point. 
 * 
 * TODO 
 * set up handler
 * refactor constructors (if super constructors are a pain, don't call them).
 * 
 * @author helen bretzke
 */
public class ReflexiveEdge extends BezierEdge {	 
	
	/**
	 * Index of midpoint used as handle to modify the curve position.
	 */
	public static final int MIDPOINT = 4;
	
	/**
	 * Creates a reflexive edge on <code>node</code> with no underlying transition. 
	 * @param node
	 */
	public ReflexiveEdge(Node node)
	{
		super(node, node);
		super.setLayout(new ReflexiveLayout(node));		
		super.setHandler(new ReflexiveHandler(this));
	}
		
	/**
	 * Creates a reflexive edge on <code>node</code> with the given layout and transition.
	 * @param layout
	 * @param node
	 * @param t a transition this represented by this edge
	 */
	public ReflexiveEdge(BezierLayout layout, Node node, Transition t)
	{
		super(node, node);
		addTransition(t);
		//layout.setEdge(this);
		setLayout(new ReflexiveLayout(node, this, layout));
		setHandler(new ReflexiveHandler(this));
	}
	
	/**
	 * Creates a reflexive edge on <code>node</code> representing the given transition.
	 * @param node
	 * @param t a transition this represented by this edge
	 */
	public ReflexiveEdge(Node node, FSATransition t) {
		super(node, node);
		addTransition(t);				
		setLayout(new ReflexiveLayout(node, this));
		setHandler(new ReflexiveHandler(this));
		
		computeEdge();
		
		// place me among any other edges adjacent to node
		Iterator<Edge> neighbours = node.adjacentEdges();
		if(neighbours.hasNext()){
			Set<Edge> n = new HashSet<Edge>();
			while(neighbours.hasNext())
			{
				n.add(neighbours.next());
			}
			insertAmong(n);
		}		
	}

	/**
	 * Searchs for enough room along circumference of node to place this edge.  
	 * If not enough space, looks for a layout that doesn't clobber another reflexive edge.
	 */
	public void insertAmong(Set<Edge> neighbours)
	{	
		double delta = Math.toRadians(2.0);
		double alpha = 0.0; 
		
		if(!BezierEdgePlacer.tooClose(this, neighbours))
		{
			return;
		}
		
		/**
		 * Search for a free space using brute force and ignorance.
		 */
		while(BezierEdgePlacer.tooClose(this, neighbours) && alpha < 360)
		{
			((ReflexiveLayout)getLayout()).axis = Geometry.rotate(((ReflexiveLayout)getLayout()).axis, delta);			
			setMidpoint(Geometry.add(getSource().getLocation(), ((ReflexiveLayout)getLayout()).axis));
			computeEdge();
			alpha ++;
		}
		
		if(alpha == 360)
		{
			// TODO find a spot that doesn't mask another reflexive edge
			
		}
	}
	
	
	/**
	 * Set the midpoint of the curve to <code>point</code>. 
	 * 
	 * @param point the new midpoint.
	 */
	public void setMidpoint(Point2D.Float point)
	{
		((ReflexiveLayout)getLayout()).setPoint(point, MIDPOINT);
		setDirty(true);
	}
		
	public Point2D.Float getMidpoint() {
		return ((ReflexiveLayout)getLayout()).getMidpoint();		
	}

	/**
	 * TODO customize so that if curve is invisible, arrow points to centre of node.
	 */
	public void draw(Graphics g)
	{		
		super.draw(g);	
	}

	/**
	 * TODO customize so that intersection with boundary is computed properly;
	 * parameters (sourceT and targetT) are currently being reverse.
	 */
	public void refresh()
	{		
		super.refresh();
	}

	
	/**
	 * To be used when moving/reshaping self-loops.
	 * 
	 * @param point
	 * @param index
	 */
	public void snapToNode(Point2D.Float point, int index){
		
	}
	
	/* (non-Javadoc)
	 * @see presentation.fsa.Edge#createExportString(java.awt.Rectangle, int)
	 */
	@Override
	public String createExportString(Rectangle selectionBox, int exportType) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * This method returns the bounding box for the edge and its label.
	 * 
	 * @return Rectangle The bounds of the Bezier Curve and its label. 
	 */
	public Rectangle bounds()
	{
		return ((ReflexiveLayout)getLayout()).getCurve().getBounds().union(getLabel().bounds());		
	}
	
	public void translate(float x, float y){
		super.translate(x, y);
		((ReflexiveLayout)getLayout()).midpoint.x += x;
		((ReflexiveLayout)getLayout()).midpoint.y += y;
	}
	
	public void computeEdge() {
		((ReflexiveLayout)getLayout()).computeCurve();
		refresh();
	}
	
	public boolean isStraight()
	{
		return false;
	}
		
	
	/** 
	 * Sets the coordinates of <code>intersection</code> to the location where
	 * my bezier curve intersects the boundary of <code>node</code>. 
	 * 
	 * @return param t at which my bezier curve intersects <code>node</code>
	 *  
	 * @precondition node != null and intersection != null
	 */
	protected float intersectionWithBoundary(Shape nodeShape, Point2D.Float intersection, int type) {
		
		// setup curves for iterative subdivision
		CubicParamCurve2D curve = this.getBezierLayout().getCurve();
			
		CubicParamCurve2D left = new CubicParamCurve2D();
		CubicParamCurve2D right = new CubicParamCurve2D();
		
		CubicParamCurve2D temp = new CubicParamCurve2D();
		// if target, then this algorithm needs to be reversed since
		// it searches curve assuming t=0 is inside the node.		
				
		if( type == TARGET_NODE ) {
			// swap endpoints and control points		
			temp.setCurve(curve.getP2(), curve.getCtrlP2(), curve.getCtrlP1(), curve.getP1());			
		}else if( type == SOURCE_NODE ){
			temp.setCurve(curve);
		}else{
			return 0f;
		}
		
		float epsilon = 0.00001f;		
		float tPrevious = 0f;
		float t = 0.5f - 0.01f; //1f;		
		float step = t; //1f;
		
		temp.subdivide(left, right, t);		
		// the point on curve at param t
		Point2D c_t = left.getP2();
	
		while(Math.abs(t - tPrevious) > epsilon){			
			step =  Math.abs(t - tPrevious);
			tPrevious = t;
			if(nodeShape.contains(c_t)){  // inside boundary
				// search right segment
				t += step/2;
			}else{
				// search left segment
				t -= step/2;
			}
			temp.subdivide(left, right, t);					
			c_t = left.getP2();
		}		
		
		// TODO keep searching from c_t towards t=0 until we're sure we've found the first intersection.
		// Start again with step size at t.
		
		if( type == TARGET_NODE  ) 
		{
			t = 1-t;
			assert(0 <= t && t <=1);			
		}
	
		intersection.x = (float)c_t.getX();
		intersection.y = (float)c_t.getY();
			
		return t;		
	}

	
	/**
	 * Same data as BezierLayout (control points and label offset vector)
	 * but different algorithms and handlers specific to rendering a self-loop. 
	 * 
	 * @author helen bretzke
	 *
	 */
	public class ReflexiveLayout extends BezierLayout
	{
		// NOTE no need to store either of these variables here.
		// vector from centre of source/target node to this point 
		// is the axis around with a symmetrical arc is drawn.		
		private Point2D.Float axis;		
		private Point2D.Float midpoint;
		
		// default angle from centre axis vector to the tangents of the bezier curve
		public static final double DEFAULT_ANGLE = Math.PI /5; 
		public static final float DEFAULT_SCALAR = 2f;
		
		/**
		 * Layout for a reflexive edge with vertical axis vector from centre of
		 * node to midpoint of bezier curve given by <code>bLayout</code>.
		 */
		public ReflexiveLayout(Node source, ReflexiveEdge edge, BezierLayout bLayout)
		{	
			Point2D temp = Geometry.midpoint(bLayout.getCurve());
			setPoint(new Point2D.Float((float)temp.getX(), (float)temp.getY()), MIDPOINT);					
			setEdge(edge);
			setCurve(bLayout.getCurve());
//			 TODO updateAnglesAndScalars(); from shape of given curve
			
			setEventNames(bLayout.getEventNames());			
		}
		

		/**
		 * Layout for a reflexive edge with default vertical axis.  
		 */
		public ReflexiveLayout(Node source) 
		{							
			setPoint(Geometry.add(source.getLocation(), new Point2D.Float(0, - source.bounds().height)), MIDPOINT);		
			initAnglesAndScalars();
		}
				
		
		/**
		 * @param source
		 * @param edge
		 */
		public ReflexiveLayout(Node source, ReflexiveEdge edge) {
			this(source);
			setEdge(edge);			
		}
		
		/**
		 * @param controls
		 */
//		public ReflexiveLayout(Float[] controls) {
//			super(controls);
//		}


		/**
		 * @return the midpoint of the curve.
		 */
		public Point2D.Float getMidpoint() {
			return midpoint;
		}

		
		/**
		 * FIXME using fixed scalars and angles causes midpoint 
		 * of computed curve to drift away from midpoint set by user.
		 * 
		 *
		 */
		public void initAnglesAndScalars(){
			angle1 = -DEFAULT_ANGLE;
			angle2 = DEFAULT_ANGLE;
			s1 = DEFAULT_SCALAR;
			s2 = s1;					
		}
		
		/** 
		 * @return the portion of the curve that is external to the node,
		 * null if no such segment exists
		 */
		public CubicCurve2D getVisibleCurve()
		{
			// FIXME check for NaN here or in CubicCurve2Dex class
			return curve.getSegment(sourceT, targetT);		
		}
		
		/**		 
		 * Computes a symmetric reflexive bezier curve based on location of node,
		 * and angle of tangent vectors (to bezier curve) from centre axis vector.
		 * 
		 */
		public void computeCurve()
		{			
			if(getEdge() == null){
				return;
			}
			
			// TODO compute curve from centre and midpoint such that midpoint is fixed
			// and curve has same shape as default.
			
			setPoint(getEdge().getSource().getLocation(), P1);
			setPoint(getEdge().getSource().getLocation(), P2);

			axis = Geometry.subtract(midpoint, curve.getP1());
			
			Point2D.Float v1 = Geometry.rotate(axis, angle1);
			Point2D.Float v2 = Geometry.rotate(axis, angle2);
							
			setPoint(Geometry.add(getEdge().getP1(), Geometry.scale(v1, (float)s1)), CTRL1);
			setPoint(Geometry.add(getEdge().getP2(), Geometry.scale(v2, (float)s2)), CTRL2);
						
			setDirty(true);
		}	
		
		/**
		 * Set the midpoint for a symmetric, reflexive bezier edge.
		 * Constraint: if midpoint is inside node, set to minimum distance from node border. 
		 * 
		 * @param point
		 * @param index
		 */
		public void setPoint(Point2D.Float point, int index){
			switch(index)
			{			
				case MIDPOINT:	
// FIXME don't constrain the point, but make sure the midpoint, arrowhead and the handler behave properly.				
					if(getSource().intersects(point))
					{
						// snap to arc minimum distance from border of node
						midpoint = Geometry.add(curve.getP1(), 
								Geometry.scale(Geometry.unitDirectionVector(
												new Point2D.Float((float)curve.getP1().getX(), 
																	(float)curve.getP1().getY()),
												midpoint), 
								getSource().getShape().getBounds().width));
						  
					}else{					
						midpoint = point;
					}										
					setLocation(midpoint.x, midpoint.y);
					// TODO computeCurve();
					break;
				case P1:
					curve.x1 = point.x;
					curve.y1 = point.y;
					break;
				case P2:
					curve.x2 = point.x;
					curve.y2 = point.y;
					break;				
				case CTRL1:
					curve.ctrlx1 = point.x;
					curve.ctrly1 = point.y;
					break;
				case CTRL2:
					curve.ctrlx2 = point.x;
					curve.ctrly2 = point.y;
					break;
				default: throw new IllegalArgumentException("Invalid control point index: " + index);				
			}			
			setDirty(true);
		}
	} // end Layout
	
	public class ReflexiveHandler extends EdgeHandler {
		
		private Ellipse2D.Double anchor;	
		private static final int RADIUS = 5;
		
		/**
		 * @param edge
		 */
		public ReflexiveHandler(Edge edge) {
			super(edge);
			refresh();
		}

		public void refresh(){
			int d = 2*RADIUS;
			// upper left corner, width and height of circle's bounding box
			anchor = new Ellipse2D.Double(((ReflexiveEdge)getEdge()).getMidpoint().x - RADIUS, ((ReflexiveEdge)getEdge()).getMidpoint().y - d, d, d);
			setDirty(false);
		}
		
		/**		 
		 * @return true iff p intersects the control point circle. 
		 */
		public boolean intersects(Point2D p) {
			if(anchor.contains(p)){
				lastIntersected = MIDPOINT;
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
}
