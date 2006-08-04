/**
 * 
 */
package presentation.fsa;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import model.fsa.ver1.Transition;

import presentation.Geometry;
import presentation.GraphicalLayout;

/**
 * A symmetric self-loop manipulated by a single control point. 
 * 
 * @author helen bretzke
 */
public class ReflexiveEdge extends BezierEdge {	 
	
	/**
	 * Creates a reflexive edge on <code>source</code> with no underlying transition. 
	 * @param source
	 */
	public ReflexiveEdge(Node source)
	{
		super(source, source);
		setLayout(new ReflexiveLayout());		
	}
		
	/**
	 * @param layout
	 * @param source
	 */
	public ReflexiveEdge(BezierLayout layout, Node source, Transition t)
	{
		super(source, source);
		addTransition(t);
		setLayout(new ReflexiveLayout(layout, source));
	}
	
	/**
	 * Set the midpoint of the curve to <code>point</code>. 
	 * 
	 * @param point the new midpoint.
	 */
	public void setMidpoint(Point2D.Float point)
	{
		// TODO
		getLayout().setPoint(point, 0);
		setDirty(true);
	}
	
	public void draw(Graphics g)
	{
		if(isDirty()){		
			getLayout().computeCurve();
			super.refresh();
		}
		
		// TODO implement
		
	}

	public ReflexiveLayout getLayout()
	{
		return (ReflexiveLayout)super.getLayout();
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
		public static final double DEFAULT_ANGLE = 5*Math.PI /8; 
		private double angle = DEFAULT_ANGLE;
		
		// ???
		private double scalar = 1;
		
		/**
		 * Layout for a reflexive edge with vertical axis vector from centre of
		 * node to midpoint of bezier curve given by <code>bLayout</code>.
		 */
		public ReflexiveLayout(BezierLayout bLayout, Node source)
		{			
			axis = Geometry.subtract(Geometry.midpoint(bLayout.getCubicCurve()), source.getLocation());			
		}

		/**
		 * Layout for a reflexive edge with default vertical axis.  
		 */
		public ReflexiveLayout() 
		{			
			// TODO set location to default midpoint
			axis = new Point2D.Float(0,1);
			computeCurve();
		}
		
		/**
		 * Override
		 * Computes a reflexive bezier curve based on location of node,
		 * and angle of tangent vectors (to bezier curve) from centre axis vector.
		 * 
		 * Scalars?
		 */
		public void computeCurve()
		{
			// TODO Implement
			// vector from centre of source/target node to this point 
			// is the axis around with a symmetrical arc is drawn.

			axis = Geometry.subtract(midpoint, getEdge().getSource().getLocation());
			setPoint(getEdge().getSource().getLocation(), P1);
			setPoint(getEdge().getSource().getLocation(), P2);
			
			double s = 3f*NodeLayout.DEFAULT_RADIUS;		
			
			Point2D.Float v1 = Geometry.rotate(axis, angle);
			Point2D.Float v2 = Geometry.rotate(axis, -angle);
							
			setPoint(Geometry.add(getEdge().getP1(), Geometry.scale(Geometry.unit(v1), (float)s)), CTRL1);
			setPoint(Geometry.add(getEdge().getP2(), Geometry.scale(Geometry.unit(v2), (float)s)), CTRL2);
			
			setDirty(true);
		}
		
		/**
		 * Index of midpoint used as handle to modify the curve position.
		 */
		public static final int MIDPOINT = 4;
		
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
				default:
					super.setPoint(point, index);				
			}			
		}
	}
}
