/**
 * 
 */
package presentation.fsa;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import model.fsa.ver1.Transition;

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
	 * Creates a reflexive edge on <code>source</code> with no underlying transition. 
	 * @param node
	 */
	public ReflexiveEdge(Node node)
	{
		super(node, node);
		super.setLayout(new ReflexiveLayout(node));		
		super.setHandler(new ReflexiveHandler(this));
	}
		
	/**
	 * @param layout
	 * @param node
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
	 * @param node
	 * @param t
	 */
	public ReflexiveEdge(Node node, Transition t) {
		super(node, node);
		addTransition(t);
		setLayout(new ReflexiveLayout(node, this));	
		setHandler(new ReflexiveHandler(this));
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
	
	/**
	 * @return
	 */
	public Point2D.Float getMidpoint() {
		return ((ReflexiveLayout)getLayout()).getMidpoint();		
	}

	public void draw(Graphics g)
	{
		if(isDirty()){		
			refresh();
		}
		super.draw(g);	
	}

	public void refresh()
	{
		((ReflexiveLayout)getLayout()).computeCurve();
		super.refresh();
	}
	
//	public ReflexiveLayout getLayout()
//	{
//		return (ReflexiveLayout)super.getLayout();
//	}
	
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
	 * 
	 * @author Helen Bretzke
	 */
	public Rectangle bounds()
	{
		return ((ReflexiveLayout)getLayout()).getCubicCurve().getBounds().union(getLabel().bounds());		
	}
	
	public void translate(float x, float y){
		super.translate(x, y);
		((ReflexiveLayout)getLayout()).midpoint.x += x;
		((ReflexiveLayout)getLayout()).midpoint.y += y;
	}
	
	public void computeEdge() {
		((ReflexiveLayout)getLayout()).computeCurve();
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
		public static final double DEFAULT_ANGLE = Math.PI /4; 
				
		/**
		 * Layout for a reflexive edge with vertical axis vector from centre of
		 * node to midpoint of bezier curve given by <code>bLayout</code>.
		 */
		public ReflexiveLayout(Node source, ReflexiveEdge edge, BezierLayout bLayout)
		{	
			Point2D temp = Geometry.midpoint(bLayout.getCubicCurve());
			midpoint = new Point2D.Float((float)temp.getX(), (float)temp.getY());			
			setLocation(midpoint.x, midpoint.y);
			
			axis = Geometry.subtract(Geometry.midpoint(bLayout.getCubicCurve()), source.getLocation());
			
			setEdge(edge);
			setCurve(bLayout.getCubicCurve());			
			setSelfLoop(true);
			setEventNames(bLayout.getEventNames());	
		
			updateAnglesAndScalars();
		}
		

		/**
		 * Layout for a reflexive edge with default vertical axis.  
		 */
		public ReflexiveLayout(Node source) 
		{			
			// TODO set location to default midpoint			
			midpoint = Geometry.add(source.getLocation(), new Point2D.Float(0,-source.bounds().height));
			setLocation(midpoint.x, midpoint.y);
			updateAnglesAndScalars();			
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
		 * @return the midpoint of the curve.
		 */
		public Point2D.Float getMidpoint() {
			return midpoint;
		}

		/**
		 * TODO
		 * chech and use the midpoint
		 * if it is unknown, we set to defaults
		 * otherwise...
		 */
		public void updateAnglesAndScalars(){
			angle1 = -DEFAULT_ANGLE;
			angle2 = DEFAULT_ANGLE;
			// Proportion of the length of axis from node centre to midpoint of curve
			// to the length of line segment from  node centre to each symmetric control point.
			s1 = 2.0f;
			s2 = 2.0f;
		}
		
		/**
		 * Override
		 * Computes a symmetric reflexive bezier curve based on location of node,
		 * and angle of tangent vectors (to bezier curve) from centre axis vector.
		 * 
		 */
		public void computeCurve()
		{
			// TODO Implement
			// vector from centre of source/target node to this point 
			// is the axis around with a symmetrical arc is drawn.
			
			// *** if(isDirty()) updateAnglesAndScalars();
			
			axis = Geometry.subtract(midpoint, getEdge().getSource().getLocation());
			setPoint(getEdge().getSource().getLocation(), P1);
			setPoint(getEdge().getSource().getLocation(), P2);
			
			Point2D.Float v1 = Geometry.rotate(axis, angle1);
			Point2D.Float v2 = Geometry.rotate(axis, angle2);
							
			setPoint(Geometry.add(getEdge().getP1(), Geometry.scale(v1, (float)s1)), CTRL1);
			setPoint(Geometry.add(getEdge().getP2(), Geometry.scale(v2, (float)s2)), CTRL2);
	
			// *** setDirty(false);
			setDirty(true);
		}		
		
		/**
		 * Set the midpoint for a symmetric, reflexive bezier edge.
		 * 
		 * @param midpoint
		 * @param index
		 */
		public void setPoint(Point2D.Float point, int index){
			switch(index)
			{			
				case MIDPOINT:
					midpoint = point;
					setLocation(midpoint.x, midpoint.y);					
					computeCurve();
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
		 * TODO implement
		 * 
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
