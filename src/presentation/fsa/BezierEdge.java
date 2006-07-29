package presentation.fsa;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Float;
import java.util.ArrayList;
import java.util.Iterator;

import model.fsa.FSATransition;
import model.fsa.ver1.Transition;
import presentation.Geometry;
import presentation.GraphicalLayout;
import util.BentoBox;

/**
 * The graphical representation of a transition in a finite state automaton.
 * 
 * @author Helen Bretzke
 *
 */
public class BezierEdge extends Edge {
	
	protected static final float INTERSECT_EPSILON=9;
	/**
	 * Indices of bezier curve control points. 
	 */
	public static final int P1 = 0;	
	public static final int CTRL1 = 1;
	public static final int CTRL2 = 2;
	public static final int P2 = 3;
			
	private ArrowHead arrowHead;	
	
	// TODO Refactor these constructors.
	
	/**
	 * Precondition: layout != null
	 * @param layout
	 * @param source
	 */
	public BezierEdge(BezierLayout layout, Node source){
		super(source);		
		this.layout = layout;
		layout.setEdge(this);		
		arrowHead = new ArrowHead();		   
		setDirty(true);
	}
		
	/**
	 * Precondition: layout != null
	 * @param layout
	 * @param source
	 * @param target
	 * @param t
	 */
	public BezierEdge(BezierLayout layout, Node source, Node target, FSATransition t){		
			
		super(source, target, t);
		
		this.layout = layout;
		layout.setEdge(this);
		
		arrowHead = new ArrowHead();		
		setDirty(true);
	}

	public void draw(Graphics g) {
		if( ! isVisible() ){
			return;
		}
		
		if(isDirty()){
			update();
			getHandler().update();			
			layout.setDirty(false);
		}
	
		Graphics2D g2d = (Graphics2D)g;		
		// if either my source or target node is highlighted
		// then I am also hightlighted.
		if(highlighted ||
				getSource().isHighlighted() || 
				getTarget() != null && getTarget().isHighlighted()){
			setHighlighted(true);
			g2d.setColor(layout.getHighlightColor());
		}else{
			g2d.setColor(layout.getColor());
		}		
		
		if(isSelected()){
			g2d.setColor(layout.getSelectionColor());
			getHandler().setVisible(true);
		}else{
			//handler.setVisible(false); // KLUGE to clean up after modify edge tool
			getHandler().setVisible(false);
		}

		if(hasUncontrollableEvent()){
			g2d.setStroke(GraphicalLayout.DASHED_STROKE);
		}else{

			g2d.setStroke(GraphicalLayout.WIDE_STROKE);
		}		   

		// FIXME stop drawing at base of arrowhead
		// subtract Geometry.scale(unitDir, ArrowHead.SHORT_HEAD_LENGTH) from P2		
		g2d.draw(getLayout().getCubicCurve());   
	    
		g2d.drawPolygon(arrowHead);
	    g2d.fillPolygon(arrowHead);
	    
	    // draw label and handler
	    super.draw(g);
		
	}
	
	/**
	 * Updates my visualization of curve, arrow and label.
	 */
	public void update() {		
		super.update();
		CubicCurve2D.Float curve = getLayout().getCubicCurve();
//		 DEBUG
		assertAllPointsNumbers(curve);		
		
		if(!isSelected()){
			getHandler().setVisible(false);
		}	
				
	    getLabel().setText(getLayout().getText());
	    
	    // Compute location of label: midpoint of curve	plus offset vector     
	    CubicCurve2D.Float left = new CubicCurve2D.Float(); 
	    curve.subdivide(left, new CubicCurve2D.Float());	        
	    Point2D midpoint = left.getP2();	    
	    this.setLocation(midpoint);
	    Point2D.Float location = Geometry.add(new Point2D.Float((float)midpoint.getX(), (float)midpoint.getY()), layout.getLabelOffset());	    
	    getLabel().setLocation(location);
	    
	    // Compute and store the arrow layout (the direction vector from base to tip of the arrow)
	    Point2D.Float dir = computeArrowDirection();
	    
	    Point2D.Float unitDir = Geometry.unit(Geometry.subtract(curve.getP2(), curve.getCtrlP2()));
	    arrowHead.update(unitDir, Geometry.add(curve.getP2(), Geometry.scale(unitDir, -(ArrowHead.SHORT_HEAD_LENGTH))));       
	    setDirty(false);
	}
	
	
	/**
	 * @return
	 */
	private Float computeArrowDirection() {
		// Starting at p2, find point on curve where base of arrow head
		// intersects with ...
		
		// float d = 2 * target.getRadius() + 2 * ArrowHead.SHORT_HEAD_LENGTH;
		// Ellipse2D circle = new Ellipse2D.Float(target.getLayout().getLocation().x, target.getLayout().getLocation().y, d, d);		
		return null;
	}

	/************************************************************
	 *  Debugging 
	 */
	protected void assertAllPointsNumbers(CubicCurve2D.Float curve){
		
		assert !Double.isNaN(curve.getCtrlX1()) : "cx1 is NaN"; 
		assert !Double.isNaN(curve.getCtrlX2()) : "cx2 is NaN";
		assert !Double.isNaN(curve.getCtrlY1()) : "cy1 is NaN";
		assert !Double.isNaN(curve.getCtrlY2()) : "cy2 is NaN";
		assert !Double.isNaN(curve.getX1()) : "x1 is NaN";
		assert !Double.isNaN(curve.getX2()) : "x2 is NaN";
		assert !Double.isNaN(curve.getY1()) : "y1 is NaN";
		assert !Double.isNaN(curve.getY2()) : "y2 is NaN";
		
	}
	/*************************************************************/
	
	/**	 
	 * @return true iff p intersects with this edge. 
	 */
	public boolean intersects(Point2D p){
		
		boolean hit=false;
		boolean limitReached=false;
		CubicCurve2D.Float curve= getLayout().getCubicCurve();
		
		// DEBUG
		assertAllPointsNumbers(curve);
		
		do
		{
			// DEBUG
			assertAllPointsNumbers(curve);
			
			CubicCurve2D.Float c1=new CubicCurve2D.Float(),c2=new CubicCurve2D.Float();
			curve.subdivide(c1,c2);
			if(c1.intersects(p.getX() - 4, p.getY() - 4, 8, 8))
			{ 
				curve=c1;
				hit=true;
			}
			else if(c2.intersects(p.getX() - 4, p.getY() - 4, 8, 8))
			{
				curve=c2;
				hit=true;
			}
			else
				hit=false;
			if(curve.getP1().distanceSq(curve.getP2())<INTERSECT_EPSILON)
				limitReached=true;
		}while(hit&&!limitReached);
		
		if(isSelected() && getHandler().isVisible()){
			// expand the intersection point to an 8 by 8 rectangle
			return hit || 
				arrowHead.intersects(p.getX() - 4, p.getY() - 4, 8, 8) || 
				getLabel().intersects(p) || 
				getHandler().intersects(p) ;
		}else{
			// expand the intersection point to an 8 by 8 rectangle
			//boolean r = getLayout().getCubicCurve().intersects(p.getX() - 4, p.getY() - 4, 8, 8);			
			boolean a = arrowHead.contains(p);
			boolean l = getLabel().intersects(p);			
			return hit || a || l ;
		}		
	}
	
	public Point2D.Float getP1() {
		return new Point2D.Float((float)getLayout().getCubicCurve().getX1(), (float)getLayout().getCubicCurve().getY1());
	}

	public Point2D.Float getP2() {
		return new Point2D.Float((float)getLayout().getCubicCurve().getX2(), (float)getLayout().getCubicCurve().getY2());
	}
	
	public Point2D.Float getCTRL1() {
		return new Point2D.Float((float)getLayout().getCubicCurve().getCtrlX1(), (float)getLayout().getCubicCurve().getCtrlY1());		
	}

	public Point2D.Float getCTRL2() {
		return new Point2D.Float((float)getLayout().getCubicCurve().getCtrlX2(), (float)getLayout().getCubicCurve().getCtrlY2());		
	}

	
	
	/**
	 * Returns the bounding rectangle with P1 and P2 as vertices.
	 * (Assumes for sake of simplicity that the edge is a straight line i.e. ignores control points).
	 */
	public Rectangle2D bounds(){				
		CubicCurve2D curve = getLayout().getCubicCurve();
		return new Rectangle2D.Float((float)Math.min(curve.getX1(), curve.getX2()),
					  				(float)Math.min(curve.getY1(), curve.getY2()),					  				
					  				(float)Math.abs(curve.getX2() - curve.getX1()), 
					  				(float)Math.abs(curve.getY2() - curve.getY1()));	
	}

	

	/**
	 * Precondition: layout != null
	 * @param layout
	 */
	private void setLayout(BezierLayout layout) {
		super.setLayout(layout);
		layout.setEdge(this);
		update();
	}
	
	// TODO get rid of this method
	public BezierLayout getLayout(){
		return (BezierLayout)layout;
	}
	
	public void translate(float x, float y){		
		BezierLayout l = (BezierLayout)layout;
		CubicCurve2D.Float curve = l.getCubicCurve();
		if(l.isRigidTranslation()){			
		// Translate the whole curve assuming that its
		// source and target nodes have been translated by the same displacement.			
			curve.setCurve(curve.getX1()+x, curve.getY1()+y,
					curve.getCtrlX1()+x, curve.getCtrlY1()+y,
					curve.getCtrlX2()+x, curve.getCtrlY2()+y,						
					curve.getX2(), curve.getY2()+y);	
			
//			 DEBUG
			assertAllPointsNumbers(curve);
			
			l.setRigidTranslation(false);
			super.translate(x, y);
			
		}else if(isSelfLoop()){
			// TODO make sure (x,y) is outside my source/target node
			
			// TODO rotate the orientation of loop in direction of vector from centre to (x,y)
			Point2D.Float centre = getSource().getLayout().getLocation();
			Point2D.Float dir = Geometry.subtract(new Point2D.Float(x, y), centre);
			
			l.computeCurve();

		}else{ 	// reset the control points in the layout object

			if(getTarget()!=null) //translation can occur in the middle of drawing a new edge
			{
				l.computeCurve(getSource().getLayout(), getTarget().getLayout());
			}			

		}		
	}

	/**
	 * @return true iff source node is same as target node
	 */

	public boolean isSelfLoop() {
		if(getSource() != null && getTarget()!= null){
			return getSource().equals(getTarget());
		}
		return false;
	}

	/**
	 * This method is responsible for creating a string that contains
	 * an appropriate (depending on the type) representation of this
	 * edge.
	 *  
	 * @param selectionBox The area being selected or considered
	 * @param exportType The export format
	 * @return String The string representation
	 * 
	 * @author Sarah-Jane Whittaker
	 */
	public String createExportString(Rectangle selectionBox, int exportType)
	{
		String exportString = "";
		
		Point2D.Float edgeP1 = getP1();
		Point2D.Float edgeP2 = getP2();
		Point2D.Float edgeCTRL1 = getCTRL1();
		Point2D.Float edgeCTRL2 = getCTRL2();
		BezierLayout edgeLayout = (BezierLayout) getLayout();

		// Make sure this node is contained within the selection box
		if (! (selectionBox.contains(edgeP1) && selectionBox.contains(edgeP2)
			&& selectionBox.contains(edgeCTRL1) && selectionBox.contains(edgeCTRL2)))
		{
			System.out.println("Edge " + edgeP1 + " "
				+ edgeP2 + " "
				+ edgeCTRL1 + " "
				+ edgeCTRL2 + " "
				+ " outside bounds " + selectionBox);
			return exportString;
		}
		
		if (exportType == GraphExporter.INT_EXPORT_TYPE_PSTRICKS)
		{
			// Check whether this should be a line or a curve
			if (edgeLayout.isStraight())
			{
				// Draw a straight line
				exportString += "  \\psline[arrowsize=5pt";
				exportString += (hasUncontrollableEvent() ?
					", linestyle=dashed" : "");
				exportString += "]{->}(" 
					+ (edgeP1.x - selectionBox.x) + "," 
					+ (selectionBox.y + selectionBox.height - edgeP1.y) + ")(" 
					+ (edgeP2.x - selectionBox.x) + "," 
					+ (selectionBox.y + selectionBox.height - edgeP2.y) + ")\n";
			}
			else
			{	
				// Draw a curve				
				exportString += "  \\psbezier[arrowsize=5pt";
				exportString += (hasUncontrollableEvent() ?
						", linestyle=dashed" : "");
				exportString += "]{->}"
					+ "(" + (edgeP1.x - selectionBox.x) + "," 
					+ (selectionBox.y + selectionBox.height - edgeP1.y) + ")(" 
					+ (edgeCTRL1.x - selectionBox.x) + "," 
					+ (selectionBox.y + selectionBox.height -edgeCTRL1.y) + ")(" 
					+ (edgeCTRL2.x - selectionBox.x) + "," 
					+ (selectionBox.y + selectionBox.height -edgeCTRL2.y) + ")(" 
					+ (edgeP2.x - selectionBox.x) + "," 
					+ (selectionBox.y + selectionBox.height - edgeP2.y) + ")\n";
			}
			
			// Now for the label
			if ((layout.getText() != null) && (getLabel().getText().length() > 0))
			{
				exportString += "  " 
					+ getLabel().createExportString(selectionBox, exportType);
			}
		}
		else if (exportType == GraphExporter.INT_EXPORT_TYPE_EPS)
		{	
			// LENKO!!!
		}

		return exportString;
	}
	
	/**
	 * This method returns the bounding box for the edge based 
	 * on its four points.
	 * 
	 * @return Rectangle The bounds of the Bezier Curve
	 * 
	 * @author Sarah-Jane Whittaker
	 */
	public Rectangle getCurveBounds()
	{
		Point2D.Float edgeP1 = getP1();
		Point2D.Float edgeP2 = getP2();
		Point2D.Float edgeCTRL1 = getCTRL1();
		Point2D.Float edgeCTRL2 = getCTRL2();
		
		float minX = BentoBox.getMinValue(edgeP1.x, edgeP2.x, 
			edgeCTRL1.x, edgeCTRL2.x);
		float minY = BentoBox.getMinValue(edgeP1.y, edgeP2.y, 
			edgeCTRL1.y, edgeCTRL2.y);
		float maxX = BentoBox.getMaxValue(edgeP1.x, edgeP2.x, 
			edgeCTRL1.x, edgeCTRL2.x);		
		float maxY = BentoBox.getMaxValue(edgeP1.y, edgeP2.y, 
			edgeCTRL1.y, edgeCTRL2.y);
		
		return new Rectangle(BentoBox.convertFloatToInt(minX), 
			BentoBox.convertFloatToInt(minY), 
			BentoBox.convertFloatToInt(maxX - minX), 
			BentoBox.convertFloatToInt(maxY - minY));
	}
	
	
	/**
	 * @param point
	 * @param pointType
	 */
	public void setPoint(Float point, int pointType) {
		getLayout().setPoint(point, pointType);		
	}

	/**
	 * @param symbol
	 */
	public void addEventName(String symbol) {
		getLayout().addEventName(symbol);		
	}

	public void addTransitions(Transition t)
	{
		super.addTransition(t);
		if(t.getEvent() != null){			
			addEventName(t.getEvent().getSymbol());
		}		
	}
	
	/**
	 * @param s
	 * @param p
	 */
	public void computeCurve(NodeLayout s, Float p) {
		getLayout().computeCurve(s,p);		
	}

	public void computeCurve(NodeLayout nL1, NodeLayout nL2) {
		getLayout().computeCurve(nL1, nL2);		
	}

	/**
	 * @param opposite
	 */
	public void arcAway(BezierEdge opposite) {
		getLayout().arcAway(opposite.getLayout());		
	}

	/**
	 * @return
	 */
	public boolean isStraight() {		
		return getLayout().isStraight();
	}	
}