package presentation.fsa;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.Set;

import model.fsa.FSATransition;
import model.fsa.ver1.Transition;
import presentation.CubicParamCurve2D;
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
	public BezierEdge(BezierLayout layout, CircleNode source){
		super(source);		
		setLayout(layout);		
		setHandler(new BezierHandler(this));		
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
		setLayout(layout);	
		setHandler(new BezierHandler(this));		
		arrowHead = new ArrowHead();		
		setDirty(true);
	}

	/**
	 * Creates a new directed Bezier edge from <code>source</code> to <code>target</code> 
	 * with default layout and no transition.
	 * 
	 * @param source
	 * @param target
	 */
	public BezierEdge(Node source, Node target) {
		super(source, target);			
		arrowHead = new ArrowHead();		
		setDirty(true);
	}
	

	public void draw(Graphics g) {
		if( ! isVisible() ){
			return;
		}
		
		if(isDirty()){
			refresh();
			//getHandler().refresh();			
			getBezierLayout().setDirty(false);
		}
	
		Graphics2D g2d = (Graphics2D)g;	
		// if either my source or target node is highlighted
		// then I am also hightlighted.
		if(highlighted ||
				getSource().isHighlighted() || 
				getTarget() != null && getTarget().isHighlighted()){
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

		if(hasUncontrollableEvent()){
			g2d.setStroke(GraphicalLayout.DASHED_STROKE);
		}else{
			g2d.setStroke(GraphicalLayout.WIDE_STROKE);
		}		   

		// UGLY should stop drawing at base of arrowhead and at outside of node boundaries.
		CubicCurve2D curve = getBezierLayout().getVisibleCurve();
		if(curve != null)
		{
			g2d.draw(curve);
		}	    
		
		// Compute the direction and location of the arrow head
		AffineTransform at = new AffineTransform();
		
		// FIXME Compute and *STORE?* the arrow layout (the direction vector from base to tip of the arrow)
		// i.e. in BezierLayout class.
	    // Make certain that it points the right direction when nodes are touching or overlapping.
	    Point2D.Float unitArrowDir = computeArrowDirection(); 
	    	   
	    arrowHead.reset();
		
	    // If available, use point of intersection with target node boundary		
	    Point2D.Float basePt;
	    Point2D.Float tEndPt = getTargetEndPoint();
	    if(tEndPt != null){
	    	basePt = Geometry.add(tEndPt, Geometry.scale(unitArrowDir, -(ArrowHead.SHORT_HEAD_LENGTH)));
	    }else{
	    	basePt = Geometry.add(getBezierLayout().getCurve().getP2(), Geometry.scale(unitArrowDir, -(ArrowHead.SHORT_HEAD_LENGTH)));	
	    }
		at.setToTranslation(basePt.x, basePt.y);
		g2d.transform(at);
		
	    // rotate to align with end of curve
	    double rho = Geometry.angleFrom(ArrowHead.axis, unitArrowDir);
		at.setToRotation(rho);		
		
		g2d.transform(at);

		// DEBUG
//		Color temp = g2d.getColor();
//		g2d.setColor(getBezierLayout().getHighlightColor());
		
				
		g2d.fill(arrowHead);
		
//		 DEBUG
//		g2d.setColor(temp);
//		g2d.draw(arrowHead);
		
		at.setToRotation(-rho);
		g2d.transform(at);
		at.setToTranslation(-basePt.x, -basePt.y);		
		g2d.transform(at);

		// DEBUG
//		Ellipse2D.Double anchorS = new Ellipse2D.Double(getSourceEndPoint().x - 1, getSourceEndPoint().y - 1, 2, 2);
//		Color temp = g2d.getColor();
//		g2d.setColor(getBezierLayout().getHighlightColor());
//		g2d.draw(anchorS);
				
//		if(getTarget() != null){
//			Ellipse2D.Double anchorT = new Ellipse2D.Double(getTargetEndPoint().x - 3, getTargetEndPoint().y - 1, 2, 2);
//			g2d.draw(anchorT);
//		}
	
// 		g2d.setColor(temp);		
		// end DEBUG
		
	    // draw label and handler
	    super.draw(g);		
	}
	
	/**
	 * Updates my visualization of curve, arrow and label.
	 */
	public void refresh() {		
		super.refresh(); // refresh all children
		
		CubicCurve2D.Float curve = getBezierLayout().getCurve();
//		 DEBUG
		assertAllPointsNumbers(curve);		
		
		// Should be computed and at least stored in layout class ////////////////////////////////
		// TODO don't bother storing these points, just store the params (which are initialized to 0 and 1
		// and ask for the visible segment of the curve as needed
		
		Point2D.Float sourceEndPt = getSourceEndPoint();
		if(sourceEndPt == null) 
		{
			sourceEndPt = new Point2D.Float();
		}
		
		float sourceT = intersectionWithBoundary(getSource().getShape(), sourceEndPt, SOURCE_NODE);		
		((BezierLayout)getLayout()).setSourceT(sourceT);
		
		if(getTarget() != null)	
		{
			Point2D.Float targetEndPt = getTargetEndPoint();
			if(targetEndPt == null) 
			{
				targetEndPt = new Point2D.Float();
			}
			
			float tTarget = intersectionWithBoundary(getTarget().getShape(), targetEndPt, TARGET_NODE);
			((BezierLayout)getLayout()).setTargetT(tTarget);
		}	
		
		/////////////////////////////////////////////////////////////////////////////////
		
		if(!isSelected()){
			getHandler().setVisible(false);
		}	
				
	    getLabel().setText(getBezierLayout().getText());
	    
	    // Compute location of label: midpoint of curve	plus offset vector     
	    CubicCurve2D.Float left = new CubicCurve2D.Float(); 
	    curve.subdivide(left, new CubicCurve2D.Float());	        
	    Point2D midpoint = left.getP2();	    
	    this.setLocation(midpoint);
	    Point2D.Float location = Geometry.add(new Point2D.Float((float)midpoint.getX(), (float)midpoint.getY()), getBezierLayout().getLabelOffset());	    
	    getLabel().setLocation(location);
	    
	    // Compute and store the arrow layout (the direction vector from base to tip of the arrow)
	    // Make certain that it points the right direction when nodes are touching or overlapping.
//	    Point2D.Float dir = computeArrowDirection();
//	    
//	    // FIXME arrow position must be moved to outside boundary of curve (backup by distance radius).
//	    Point2D.Float unitDir = Geometry.unit(Geometry.subtract(curve.getP2(), curve.getCtrlP2()));
//	    arrowHead.update(unitDir, Geometry.add(curve.getP2(), Geometry.scale(unitDir, -(ArrowHead.SHORT_HEAD_LENGTH))));
	    getBezierLayout().setDirty(false);
	    setDirty(false);
	}
		

	/**
	 * FIXME points wrong direction when tangent angle is close to +- PI. 
	 * 
	 * @return unit direction vector for arrow head to point
	 */
	private Point2D.Float computeArrowDirection() {			
		Node target = getTarget();
		if(target == null)
		{
			return Geometry.unit(Geometry.subtract(getBezierLayout().getCurve().getP2(), getBezierLayout().getCurve().getCtrlP2())); 
		}else{
			Shape s = getTarget().getShape();
			Rectangle box = s.getBounds();
			double delta = ArrowHead.SHORT_HEAD_LENGTH;
			Rectangle fat = new Rectangle((int)(box.x - delta/2), 
					(int)(box.y - delta/2), 
					(int)(box.width + delta), 
					(int)(box.height + delta) );
			Point2D.Float p = new Point2D.Float();			
			double t = this.intersectionWithBoundary(fat, p, TARGET_NODE);
			// TODO use t; store as new endpoint in layout
			return Geometry.unitDirectionVector(p, getTargetEndPoint());
		}
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
		CubicCurve2D.Float curve= getBezierLayout().getCurve();
		
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
		return new Point2D.Float((float)getBezierLayout().getCurve().getX1(), (float)getBezierLayout().getCurve().getY1());
	}

	public Point2D.Float getP2() {
		return new Point2D.Float((float)getBezierLayout().getCurve().getX2(), (float)getBezierLayout().getCurve().getY2());
	}
	
	public Point2D.Float getCTRL1() {
		return new Point2D.Float((float)getBezierLayout().getCurve().getCtrlX1(), (float)getBezierLayout().getCurve().getCtrlY1());		
	}

	public Point2D.Float getCTRL2() {
		return new Point2D.Float((float)getBezierLayout().getCurve().getCtrlX2(), (float)getBezierLayout().getCurve().getCtrlY2());		
	}

	
	
//	/**
//	 * Returns the bounding rectangle with P1 and P2 as vertices.
//	 * (Assumes for sake of simplicity that the edge is a straight line i.e. ignores control points).
//	 */
//	public Rectangle2D bounds(){				
//		CubicCurve2D curve = getLayout().getCubicCurve();
//		return new Rectangle2D.Float((float)Math.min(curve.getX1(), curve.getX2()),
//					  				(float)Math.min(curve.getY1(), curve.getY2()),					  				
//					  				(float)Math.abs(curve.getX2() - curve.getX1()), 
//					  				(float)Math.abs(curve.getY2() - curve.getY1()));	
//	}

	

	/**
	 * Precondition: layout != null
	 * @param layout
	 */
	private void setLayout(BezierLayout layout) {		
		layout.setEdge(this);
		super.setLayout(layout);
		// ??? //
		//computeEdge();
		setDirty(true);
	}
	
	public BezierLayout getBezierLayout(){
		try{
			return (BezierLayout)super.getLayout();
		}catch(ClassCastException cce){
			return null;
		}
	}
	
	
	public void addTransition(FSATransition t)
	{
		super.addTransition(t);
		// TODO uncomment after MetaData class is disconnected
//		Event event = (Event) t.getEvent();
//		if(event != null){			
//			addEventName(event.getSymbol());
//		}	
	}
	
	public void removeTransition(Transition t)
	{
		super.removeTransition(t);
//		 TODO uncomment after MetaData class is disconnected
//		Event event = (Event) t.getEvent();
//		if(event != null){
//			getBezierLayout().removeEventName(event.getSymbol());
//		}
	}

	public void translate(float x, float y){		
		BezierLayout l = (BezierLayout)getBezierLayout();
		CubicCurve2D.Float curve = l.getCurve();
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
			
		/*}else if(isSelfLoop()){
			// TODO make sure (x,y) is outside my source/target node
			
			// TODO rotate the orientation of loop in direction of vector from centre to (x,y)
			Point2D.Float centre = getSource().getLayout().getLocation();
			Point2D.Float dir = Geometry.subtract(new Point2D.Float(x, y), centre);
			
			l.computeCurve();*/

		}else{ 	// reset the control points in the layout object

			if(getTarget()!=null) //translation can occur in the middle of drawing a new edge
			{
				l.computeCurve((NodeLayout)getSource().getLayout(), (NodeLayout)getTarget().getLayout());
			}			

		}		
	}

	/**
	 * @deprecated Delete after ReflexiveEdge class has been debugged. 
	 * 
	 * @return true iff source node is same as target node
	 *//*

	public boolean isSelfLoop() {
		if(getSource() != null && getTarget()!= null){
			return getSource().equals(getTarget());
		}
		return false;
	}*/

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
		BezierLayout edgeLayout = (BezierLayout) getBezierLayout();

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
			if ((getBezierLayout().getText() != null) && (getLabel().getText().length() > 0))
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
	public Rectangle bounds()
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
	 * Sets the point of the given type to <code>point</code>.
	 * @param point
	 * @param pointType
	 */
	public void setPoint(Float point, int pointType) {
		getBezierLayout().setPoint(point, pointType);		
	}

	/**
	 * @param symbol
	 */
	public void addEventName(String symbol) {
		getBezierLayout().addEventName(symbol);		
	}

	/**
	 * @param s
	 * @param p
	 */
	public void computeCurve(NodeLayout s, Float p) {
		getBezierLayout().computeCurve(s,p);		
	}

	public void computeCurve(NodeLayout nL1, NodeLayout nL2) {
		getBezierLayout().computeCurve(nL1, nL2);		
	}

//	/**
//	 * @param opposite
//	 */
//	public void arcAway(BezierEdge opposite) {
//		getBezierLayout().arcAway(opposite.getBezierLayout());		
//	}
	
	public boolean isStraight() {		
		return getBezierLayout().isStraight();
	}

	/**
	 * @see presentation.fsa.Edge#intersectionWithBoundary(presentation.fsa.Node, int type)
	 */
	public Point2D.Float intersectionWithBoundary(Node node, int type)
	{
		Point2D.Float intersection = new Point2D.Float();
		intersectionWithBoundary(node.getShape(), intersection, type);
		return intersection;
	}
	
	/** 
	 * Sets the coordinates of <code>intersection</code> to the location where
	 * my bezier curve intersects the boundary of <code>node</code>. 
	 * @param type 
	 * 
	 * @return param t at which my bezier curve intersects <code>node</code>
	 *  
	 * @precondition node != null and intersection != null
	 */
	protected float intersectionWithBoundary(Shape nodeShape, Point2D.Float intersection, int type) {
		
		// setup curves for iterative subdivision
		CubicParamCurve2D curve = this.getBezierLayout().getCurve();
		
		// if endpoints are both inside node (self-loop or overlapping target and source)
		// FIXME		
		if(nodeShape.contains(curve.getP1()) && nodeShape.contains(curve.getP2()) ) {
			return 0.5f;
			//return node.getLocation();
		}
	
		CubicParamCurve2D left = new CubicParamCurve2D();
		CubicParamCurve2D right = new CubicParamCurve2D();
		
		CubicParamCurve2D temp = new CubicParamCurve2D();
		// if target, then this algorithm needs to be reversed since
		// it searches curve assuming t=0 is inside the node.		
		boolean intersectWithTarget = (type == this.TARGET_NODE); //nodeShape.contains(curve.getP2()); //nodeShape.equals(getTarget().getShape());
		
		if( intersectWithTarget ) {
			// swap endpoints and control points		
			temp.setCurve(curve.getP2(), curve.getCtrlP2(), curve.getCtrlP1(), curve.getP1());			
		}else{
			temp.setCurve(curve);
		}
		
		float epsilon = 0.00001f;		
		float tPrevious = 0f;
		float t = 0.5f; //1f;		
		float step = 0.5f; //1f;
		
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
		
		if( intersectWithTarget ) 
		{
			t = 1-t;
			assert(0 <= t && t <=1);			
		}
	
		intersection.x = (float)c_t.getX();
		intersection.y = (float)c_t.getY();
			
		return t;		
	}

	/* (non-Javadoc)
	 * @see presentation.fsa.Edge#computeEdge(presentation.fsa.Node, presentation.fsa.Node)
	 */
	@Override
	public void computeEdge() {
		if(getSource() != null && getTarget() != null){
			getBezierLayout().computeCurve((NodeLayout)getSource().getLayout(), 
											(NodeLayout)getTarget().getLayout());
			refresh();
		}
	}

	public void arcMore() {
		((BezierLayout)getLayout()).arcMore();		
	}
	
	public void arcLess() {
		((BezierLayout)getLayout()).arcLess();		
	}

	/**
	 * Sets my layout to fit among the set of existing edges between 
	 * my source and target nodes.  Other edges' layouts are adjusted 
	 * as necessary. 
	 * 
	 * @param neighbours the set of edges between my source and target nodes
	 */
	public void insertAmong(Set<Edge> neighbours) {
		BezierEdgePlacer.insertEdgeAmong(this, neighbours);
	}

	/* (non-Javadoc)
	 * @see presentation.fsa.Edge#getSourceEndPoint()
	 */
	@Override
	public Float getSourceEndPoint() {		
		return ((BezierLayout)getLayout()).getSourceEndPoint();
	}

	/* (non-Javadoc)
	 * @see presentation.fsa.Edge#getTargetEndPoint()
	 */
	@Override
	public Float getTargetEndPoint() {
		return ((BezierLayout)getLayout()).getTargetEndPoint();	
	}		
	
}