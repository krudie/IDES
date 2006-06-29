package presentation.fsa;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.BasicStroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
public class Node extends GraphElement {

	// the state to be represented
	private FSAState state;

	private GraphLabel label;

	// visualization objects
	private Ellipse2D circle = null;
	private Ellipse2D innerCircle = null;  // only drawn for final states	
	private ArrowHead arrow = null;  // only draw for initial states
	private Point2D.Float arrow1, arrow2;  // the arrow shaft
		
	public Node(FSAState s, NodeLayout layout){
		this.state = s;
		this.layout = layout;
		layout.setNode(this);
		label = new GraphLabel("");
		this.insert(label);
		circle = new Ellipse2D.Double();
		arrow1 = new Point2D.Float();
		arrow2 = new Point2D.Float();
		update();
	}

	public long getId(){
		return state.getId();
	}
	
	public void setLayout(NodeLayout layout){
		this.layout = layout;
		update();
	}
	
	public NodeLayout getLayout(){
		return (NodeLayout)layout;
	}
	
	// TODO change to iterate over collection of labels on a state
	// (requires change to file reading and writing, states be composed of many states)		
	public void update() {
		
		super.update();
		
		Point2D.Float centre = ((NodeLayout)layout).getLocation();
		
		// Resize based on bounds of label and expand the node size if necessary.
		// make sure that node is big enough to fit text inside inner circle if isMarked.
		// FIXME make sure small enough to snap around tiny label
		label = new GraphLabel(layout.getText(), centre);
		Rectangle labelBounds = label.bounds();
		if( ! bounds().contains(labelBounds ) ){			
			// compute new radius
			double max = labelBounds.getWidth() > labelBounds.getHeight() ? labelBounds.getWidth() : labelBounds.getHeight();
			((NodeLayout)layout).setRadius((float)max/2 + 2*NodeLayout.RDIF);
			
			// TODO recompute all edges (endpoints will have changed)
			recomputeEdges();
		}
		
		float radius = ((NodeLayout)layout).getRadius();
		
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
			Point2D.Float dir = new Point2D.Float(((NodeLayout)layout).getArrow().x, ((NodeLayout)layout).getArrow().y);			
			float offset = ((NodeLayout)layout).getRadius() + ArrowHead.SHORT_HEAD_LENGTH;
			arrow2 = Geometry.subtract(c, Geometry.scale(dir, offset));
			arrow = new ArrowHead(dir, arrow2);					
			// ??? How long should the shaft be?
			arrow1 = Geometry.subtract(arrow2, Geometry.scale(dir, ArrowHead.SHORT_HEAD_LENGTH * 2));
		}
					
	}
	
	/**
	 * Compute edge layout for each edge adjacent to this Node.
	 */
	private void recomputeEdges() {
		Iterator children = children();
		while(children.hasNext()){
			try{
				Edge e = (Edge)children.next();				
				((EdgeLayout)e.getLayout()).computeCurve(e.getSource().getLayout(), e.getTarget().getLayout());
			}catch(ClassCastException cce){
				// Child is not an edge
			}
		}		
	}

	/**
	 * Draws this node and all of its out edges in the given graphics context.
	 */
	public void draw(Graphics g) {
		
		if(layout.isDirty()){
			update();
			layout.setDirty(false);
		}
		
		// only calls draw on all of the outgoing edges
		Iterator c = children();
		while(c.hasNext()){
			try{
				Edge child = (Edge)c.next();
				if(child.getSource().equals(this)){
					child.draw(g);
				}
			}catch(ClassCastException cce){ 
				// skip the label and keep going
				// Why am I skipping the label?				
			}
		}
		
		
		if (isSelected()){
			g.setColor(layout.getSelectionColor());
		}else if(isHighlighted()){
			g.setColor(layout.getHighlightColor());			
		}else{
			g.setColor(layout.getColor());	
		}
		
		Graphics2D g2d = (Graphics2D)g;
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
		return circle.getBounds();
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
	public boolean intersects(Point p) {
		if(state.isInitial()){
			return circle.contains(p) || arrow.contains(p); 
		}
		return circle.contains(p); 
	}	

	public void setSelected(boolean b){
		this.selected = b;		
	}
	
	/**
	 * DEBUG: since we are sharing references to unique node objects, 
	 * this shouldn't be necessary.
	 *  
	 * @param n
	 * @return
	 */
	public boolean equals(Node n){
		return this.getId() == n.getId();
	}
	
	public void translate(float x, float y){
		super.translate(x,y);		
		update();
	}
	
	public void showPopup(Component context){
		NodePopup.showPopup((GraphDrawingView)context, this); // cast is a KLUGE
	}

	protected FSAState getState(){
		return state;
	}
	
	/**
	 * Gets the label of the node.
	 * @return the label of the node
	 */
	public GraphLabel getLabel()
	{
		return label;
	}

	
	/**
	 * This method is needed by the GraphExporter to draw the initial
	 * arrow.  
	 *  
	 * @return Rectangle The bounding box for the initial arrow
	 * 
	 * @author Sarah-Jane Whittaker
	 */
	protected Rectangle getInitialArrowExportBounds()
	{
		Rectangle2D arrowBounds = arrow.getBounds2D();
		NodeLayout nodeLayout = getLayout();
		float radius = nodeLayout.getRadius();
		
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
		
		NodeLayout nodeLayout = getLayout();
		Point2D.Float nodeLocation = nodeLayout.getLocation();
		float radius = nodeLayout.getRadius();
		Rectangle initialArrowBounds = null;

		// Make sure this node is contained within the selection box
		if (! selectionBox.contains(nodeLocation.x - radius, 
			nodeLocation.y - radius, radius * 2, radius * 2))
		{
			System.out.println("Node " + nodeLocation 
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
				initialArrowBounds = getInitialArrowExportBounds();
				exportString += "    \\psline[arrowsize=5pt]{->}(" 
					+ (initialArrowBounds.getMinX() - selectionBox.x) + "," 
					+ (selectionBox.height + selectionBox.y - initialArrowBounds.getMinY()) + ")(" 
					+ (initialArrowBounds.getMaxX() - selectionBox.x) + "," 
					+ (selectionBox.height + selectionBox.y - initialArrowBounds.getMaxY()) + ")\n";
			}
			
			// Now for the label
			if (layout.getText() != null)
			{
				exportString += "  " 
					+ label.createExportString(selectionBox, exportType);
			}
		}
		else if (exportType == GraphExporter.INT_EXPORT_TYPE_EPS)
		{	
			// LENKO!!!
		}

		return exportString;
	}
	
	/**
	 * NOTE super isDirty no longer checks children, assumes children set this.
	 */
	public boolean isDirty(){
		return super.isDirty() || layout.isDirty();
	}

	/**
	 * @return
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

	/**
	 * @return
	 */
	public float getRadius() {	
		return ((NodeLayout)layout).getRadius();
	}
}
