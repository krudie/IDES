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
 * A symmetric self loop manipulated by a single control point. 
 * 
 * @author helen bretzke
 */
public class SelfLoop extends Edge {

	//private CubicCurve2D.Float curve; Use the one in BezierLayout
	private double angle = 5*Math.PI /8;
	
	// vector from centre of source/target node to this point 
	// is the axis around with a symmetrical arc is drawn.
	private Point2D.Float control;
	
	public SelfLoop(Node source)
	{
		super(source, source);
		//curve = new CubicCurve2D.Float();
		control = new Point2D.Float(0,1);
	}
		
	/**
	 * FIXME
	 * Need same data as BezierLayout (control points and label offset vector)
	 * to draw this type of edge - but different algorithms and handlers. 
	 * 
	 * @param layout
	 * @param source
	 */
	public SelfLoop(BezierLayout layout, Node source, Transition t)
	{
		super(source, source, t);
		// TODO construct a ReflexiveLayout from the given BezierLayout
		this.setLayout(layout);
		control = Geometry.subtract(Geometry.midpoint(layout.getCubicCurve()), source.getLocation()); 				
	}
	
	public void computeCurve()
	{
		// TODO Implement
		
		// TODO set the location to the midpoint of the curve.		
		
	}
	
	public void setPoint(Point2D.Float point)
	{
		control = point;
		setDirty(true);
	}
	
	public void draw(Graphics g)
	{
		if(isDirty()){		
			computeCurve();
			super.update();
		}
		
		// TODO implement
		
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
	
	public class SelfLoopLayout extends BezierLayout
	{
		// TODO constructor that takes BezierLayout
		// Override the computeCurve methods to use a single control point
		
	}
}
