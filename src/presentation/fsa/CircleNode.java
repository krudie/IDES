package presentation.fsa;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Float;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Iterator;

import presentation.GraphicalLayout;
import presentation.PresentationElement;
import presentation.Geometry;
import main.Hub;
import model.fsa.FSAState;
import model.fsa.ver1.State;
import model.fsa.ver1.Transition;
import util.BentoBox;

/**
 * The graphical representation of a state in a finite state automaton.
 * Child glyphs are its out edges.
 * 
 * @author helen bretzke
 *
 */
public class CircleNode extends Node {

	// visualization objects
	private Ellipse2D circle = null;
	private Ellipse2D innerCircle = null;  // only drawn for final states
	
	// TODO Change this to type InitialArrow
	private ArrowHead arrow = null;  // only draw for initial states
	
	private Point2D.Float arrow1, arrow2;  // the arrow shaft
		
	public CircleNode(FSAState s, NodeLayout layout){
		this.state = s;
		setLayout(layout);		
		label = new GraphLabel("");
		this.insert(label);
		circle = new Ellipse2D.Double();
		arrow1 = new Point2D.Float();
		arrow2 = new Point2D.Float();
		arrow = new ArrowHead();
		refresh();
	}

	// TODO change to iterate over collection of labels on a state
	// (requires change to file reading and writing, states be composed of many states)		
	public void refresh() 
	{	
		Point2D.Float centre = getLayout().getLocation();
			
		label.updateLayout(getLayout().getText(), centre);
		
		// compute new radius
		Rectangle2D labelBounds = label.bounds();
		float radius = (float)Math.max(labelBounds.getWidth()/2 + 2* NodeLayout.RDIF, NodeLayout.DEFAULT_RADIUS + 2 * NodeLayout.RDIF);			
		((NodeLayout)getLayout()).setRadius(radius);
		radius=((NodeLayout)getLayout()).getRadius();
		recomputeEdges();
		
		// upper left corner, width and height
		float d = 2*radius;
		circle = new Ellipse2D.Double(centre.x - radius, centre.y - radius, d, d);

		if(state.isMarked()){			
			float r = radius - NodeLayout.RDIF;
			d = 2*r;
			innerCircle = new Ellipse2D.Double(centre.x - r, centre.y - r, d, d);
		}
			
		if(state.isInitial()){
			// The point on the edge of the circle:
			// centre point - arrow vector
			Point2D.Float c = new Point2D.Float(centre.x, centre.y);
			Point2D.Float dir = new Point2D.Float(((NodeLayout)getLayout()).getArrow().x, ((NodeLayout)getLayout()).getArrow().y);			
			float offset = ((NodeLayout)getLayout()).getRadius() + ArrowHead.SHORT_HEAD_LENGTH;
			arrow2 = Geometry.subtract(c, Geometry.scale(dir, offset));
			arrow = new ArrowHead(dir, arrow2);					
			// ??? How long should the shaft be?
			arrow1 = Geometry.subtract(arrow2, Geometry.scale(dir, ArrowHead.SHORT_HEAD_LENGTH * 2));
		}
		super.refresh();					
	}
	
	/**
	 * Draws this node and all of its out edges in the given graphics context.
	 */
	public void draw(Graphics g) {
		if(isDirty()){
			refresh();
			getLayout().setDirty(false);
		}
		
		// only calls draw on all of the outgoing edges
		Iterator c = children();
		while(c.hasNext()){
			try{
				BezierEdge child = (BezierEdge)c.next();
				if(child.getSource().equals(this)){
					child.draw(g);
				}
			}catch(ClassCastException cce){ 
				// skip the label and keep going
				// Why am I skipping the label?				
			}
		}

		Graphics2D g2d = (Graphics2D)g;
		
		if (isSelected()){
			g.setColor(getLayout().getSelectionColor());
// DEBUG
//			g2d.setStroke(GraphicalLayout.DASHED_STROKE);
//			g2d.draw(bounds());
//			g2d.setStroke(GraphicalLayout.WIDE_STROKE);
// END DEBUG		
		}else if(isHighlighted()){
			g.setColor(getLayout().getHighlightColor());			
		}else{
			g.setColor(getLayout().getColor());	
		}
	
//		Color temp = g2d.getColor();
//		g2d.setColor(getLayout().getBackgroundColor());
//		g2d.fill(circle);
//		g2d.setColor(temp);	
		
		g2d.setStroke(GraphicalLayout.WIDE_STROKE);		
		g2d.draw(circle);		
		
		if(state.isMarked()){
			g2d.draw(innerCircle);
		}		
		
		if(state.isInitial()){
			g2d.drawLine((int)arrow1.x, (int)arrow1.y, (int)arrow2.x, (int)arrow2.y);
			g2d.setStroke(GraphicalLayout.FINE_STROKE);
			g2d.draw(arrow);
			g2d.fill(arrow);
		}				
		label.draw(g);
	}
	
	public Rectangle bounds() {		
		if(getState().isInitial()){
			return (Rectangle)circle.getBounds().union(arrow.getBounds());
		}
		return circle.getBounds();
	}
	
	/**	 
	 * @return bounding rectangle for union of this with all of its children.
	 */
	public Rectangle adjacentBounds(){		
		Rectangle bounds = bounds();		
		return (Rectangle)bounds.createUnion(super.bounds());
	}
	
	/**
	 * Try with a rectangle of dimensions 1,1
	 * public boolean contains(double x,
                        double y,
                        double w,
                        double h)

       Tests if the interior of this Ellipse2D entirely contains the specified rectangular area.
       
   	// FIXME this is calling RectangularShape.contains(p) which computes intersection with bounding box
	// instead of with circle.

	 */
	public boolean intersects(Point2D p) {
		
		if(state.isInitial()){
			//return circle.contains(p) || arrow.contains(p);
			return circle.intersects(p.getX() - 5, p.getY() - 5, 10, 10) ||
					arrow.intersects(p.getX() - 4, p.getY() - 4, 8, 8);
		}
		return circle.intersects(p.getX() - 5, p.getY() - 5, 10, 10); 
	}	

	public void setSelected(boolean b){
		this.selected = b;		
	}
	
	/**
	 * NOTE since we are sharing references to unique node objects, 
	 * this shouldn't be necessary.
	 *  
	 * @param n
	 * @return
	 */
	public boolean equals(Object n){
		try{
			return this.getId().equals( ((CircleNode)n).getId() );
		}catch(Exception e){
			return false;
		}
	}
	
	public void translate(float x, float y){
		super.translate(x,y);		
		refresh();
	}
	
	public void showPopup(Component context){
		NodePopup.showPopup((GraphDrawingView)context, this); // cast is a KLUGE
	}

	/**
	 * This method is needed by the GraphExporter to draw the initial
	 * arrow.  
	 *  
	 * @return Rectangle The bounding box for the initial arrow
	 * 
	 * @author Sarah-Jane Whittaker
	 */
	protected Rectangle getInitialArrowBounds()
	{
		return (state.isInitial() ?
			new Rectangle(
				BentoBox.convertFloatToInt(arrow1.x + ArrowHead.SHORT_HEAD_LENGTH), 
				BentoBox.convertFloatToInt(arrow1.y), 
				BentoBox.convertFloatToInt(arrow2.x - arrow1.x),
				BentoBox.convertFloatToInt(arrow2.y - arrow1.y)) 
			:
			new Rectangle(0, 0, 0, 0));
	}
	
	/**
	 * This method is responsible for creating a string that contains
	 * an appropriate (depending on the type) representation of this
	 * node.
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
		
		NodeLayout nodeLayout = ((NodeLayout)getLayout());
		Rectangle squareBounds = getSquareBounds();
		Point2D.Float nodeLocation = nodeLayout.getLocation();
		int radius = BentoBox.convertFloatToInt(nodeLayout.getRadius());
		Rectangle initialArrowBounds = null;

		// Make sure this node is contained within the selection box
		if (! selectionBox.contains(squareBounds))
		{
			System.out.println("Node " + squareBounds 
				+ " (Radius " + radius * 2
				+ ") outside bounds " + selectionBox);
			return exportString;
		}
		
		if (exportType == GraphExporter.INT_EXPORT_TYPE_PSTRICKS)
		{
			// A QUOTE FROM MIKE WOOD - thanks, Mike!
			// "java coords are origin @ top left, x increasing right, 
			// y increasing down
			// latex coords are origin @ bottom left, x increasing right, 
			// y increasing up"
			exportString += "  \\pscircle(" 
				+ (nodeLocation.x - selectionBox.x) + ","
				+ (selectionBox.height + selectionBox.y - nodeLocation.y) + "){" 
				+ nodeLayout.getRadius() + "}\n";
			
			// If this is a marked state, make a smaller circle within
			// this one to simulate double lines
			if (state.isMarked())
			{ 
				exportString += "    \\pscircle(" + 
					+ (nodeLocation.x - selectionBox.x) + ","
					+ (selectionBox.height + selectionBox.y - nodeLocation.y) + "){" 
					+ (nodeLayout.getRadius() 
							- GraphExporter.INT_PSTRICKS_MARKED_STATE_RADIUS_DIFF) 
					+ "}\n";
			}				
			
			// If this is the initial state, draw an initial arrow
			if (state.isInitial())
			{
				initialArrowBounds = getInitialArrowBounds();
				exportString += "    \\psline[arrowsize=5pt]{->}(" 
					+ (initialArrowBounds.getMinX() - selectionBox.x) + "," 
					+ (selectionBox.height + selectionBox.y - initialArrowBounds.getMinY()) + ")(" 
					+ (initialArrowBounds.getMaxX() - selectionBox.x) + "," 
					+ (selectionBox.height + selectionBox.y - initialArrowBounds.getMaxY()) + ")\n";
			}
			
			// Now for the label
			if (getLayout().getText() != null)
			{
				exportString += "  " 
					+ label.createExportString(selectionBox, exportType);
			}
		}
		else if (exportType == GraphExporter.INT_EXPORT_TYPE_EPS)
		{	
			// LENKO ?
		}

		return exportString;
	}
	
	/**
	 * NOTE super isDirty no longer checks children, assumes children set this.
	 */
	public boolean isDirty(){
		return super.isDirty() || getLayout().isDirty();
	}
	
	/** 
	 * @deprecated
	 */
	public boolean hasSelfLoop() {
		Iterator<Edge> edges = adjacentEdges();
		while(edges.hasNext()){
			Edge e = edges.next();
			if(e.getSource().equals(this) && e.getTarget().equals(this)){
					return true;
			}			
		}		
		return false;
	}

	/**
	 * @return an iterator of all adjacent edges
	 */
	public Iterator<Edge> adjacentEdges() {
		Iterator children = children();
		ArrayList<Edge> edges = new ArrayList<Edge>();
		while(children.hasNext()){
			try{
				Edge e = (Edge)children.next();				
				edges.add(e);
			}catch(ClassCastException cce){
				// Child is not an edge
			}
		}		
		return edges.iterator();
	}
	
	public float getRadius() {	
		return ((NodeLayout)getLayout()).getRadius();
	}
	
	/**
	 * TODO: Comment!
	 * 
	 * @author Sarah-Jane Whittaker
	 */
	public Rectangle getSquareBounds()
	{
		Point2D.Float nodeLocation = getLayout().getLocation();
		float radius = getRadius();
		
		// Node location is at the centre of the circle
		return new Rectangle(
			BentoBox.convertFloatToInt(nodeLocation.x - radius),
			BentoBox.convertFloatToInt(nodeLocation.y - radius), 
			BentoBox.convertFloatToInt(radius * 2), 
			BentoBox.convertFloatToInt(radius * 2));
	}

	public void setLayout(NodeLayout layout) {
	//		if(getLayout()!=null)
	//			getLayout().dispose();
	//		
			super.setLayout(layout);
			((NodeLayout)getLayout()).setNode(this);
			setDirty(true);
		}

	/* (non-Javadoc)
	 * @see presentation.fsa.Node#getShape()
	 */
	@Override
	public Shape getShape() {		
		return circle;
	}

//	public NodeLayout getLayout() {
//		return (NodeLayout)super.getLayout();
//	}

}
