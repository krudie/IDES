 package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

import model.Subscriber;

import presentation.Glyph;
import presentation.fsa.GraphElement;
import ui.tools.CreationTool;
import ui.tools.DrawingTool;
import ui.tools.SelectionTool;

/**
 * The area in which users view, create and modify graphs.
 * 
 * 
 * * current interaction mode,
 * * currently selected object in the drawing area,
 * * copy and cut buffers.
 * 
 * TODO override setName to set name of parent component (i.e. scrollpane) so the name
 * will appear in the tabbed pane or title area of frame?
 * OR define a custom (decorated) scrollpane for this component that sets its name to the name of this component.
 * 
 * @author helen bretzke
 *
 */
@SuppressWarnings("serial")
public class GraphDrawingView extends JComponent implements Subscriber, MouseMotionListener, MouseListener, KeyListener {
			
	protected float scaleFactor = 1f;
	
	private int currentTool = DEFAULT;
	private DrawingTool[] drawingTools;
	
	/******************************************************************
	 * TODO Move these to a UISettings class that reads, saves and makes
	 * accessible everything the UI needs to know.
	 ******************************************************************/
	private Font font; 
	private FontMetrics fontMetrics;
	private BasicStroke wideStroke, fineStroke, dashedStroke;
		
	/**
	 * Copy buffer
	 */
	private Glyph copyBuffer;
	
	/**
	 * Cut buffer 
	 */
	private Glyph cutBuffer;
	
	/**
	 * Delete and restore buffer
	 */
	private Glyph deleteBuffer;
	
	
	/**
	 * Currently selected group or item.
	 */
	private Glyph currentSelection;
	
	/**
	 * Retangle to render as the area selected by mouse. 
	 */
	private Rectangle selectionArea;
		
	/**
	 * The selected print area.
	 */
	private Glyph printArea;
	
	/**
	 * An object to handle synchronizing FSA model with the displayed graph.
	 */
	private GraphModel graphModel;

	/**
	 * Presentation model (the composite structure that represents the DES model.)
	 */	
	private GraphElement graph;	

	public GraphDrawingView() {
		graphModel = null;
		graph = new GraphElement();
		// DEBUG
		//graph.insert(new GraphLabel("No Graph.", new Point(100, 100)));
		
		currentSelection = new GraphElement();
		selectionArea = new Rectangle();
		
		drawingTools = new DrawingTool[NUMBER_OF_TOOLS];
		drawingTools[DEFAULT] = new SelectionTool(this);
		drawingTools[SELECTION] = drawingTools[DEFAULT];
		drawingTools[CREATE] = new CreationTool(this);
		
		// TODO construct all other drawing tools
		currentTool = DEFAULT;
		
	    wideStroke = new BasicStroke(2);
	    fineStroke = new BasicStroke(1);
	    dashedStroke = new BasicStroke(
	            1, 
	            BasicStroke.CAP_BUTT,
	            BasicStroke.JOIN_MITER,
	            50,
	            new float[] {5, 2}, 
	            0
	          );
	    setVisible(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
	}	
	
	public void paint(Graphics g){
			
		Graphics2D g2D = (Graphics2D) g; // cast to 2D	

		
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                         RenderingHints.VALUE_ANTIALIAS_ON);		
	    g2D.setBackground(Color.white);  // FIXME THIS DOESN'T WORK
	    
	    // TODO what happens to stroke when scaled?
	    g2D.setStroke(wideStroke);
		
		// Warning: scales distance from origin as well as size of nodes
		// we want to scale everything from the centre of the user's view.
		// Solution: translate origin before scaling and beware of op precendence.
	    // FIXME: this is not working
//	    g2D.translate(-(getWidth()/scaleFactor), -(getHeight()/scaleFactor));
//	    g2D.scale(scaleFactor, scaleFactor);		
	    //	TODO other transformation?

	    graph.draw(g2D);
		
		g2D.setStroke(dashedStroke);
		g2D.setColor(Color.GRAY);

		g2D.draw(selectionArea);

//		g2D.translate((getWidth()/scaleFactor), (getHeight()/scaleFactor));
	}

	/**
	 * Refresh my visual model from GraphModel.
	 */
	public void update() {		
		graph = graphModel.getGraph();
		repaint();
	}

	/**
	 * Returns the set of currently selected elements in this view.
	 * 
	 * @return
	 */
	public Glyph getCurrentSelection() {
		return currentSelection;
	}

	/**
	 * Precondition: <code>currentSelection</code> != null
	 * 
	 * @param currentSelection
	 */
	public void setCurrentSelection(Glyph currentSelection) {
		this.currentSelection = currentSelection;
	}	

	// Mouse events
	public void mouseClicked(MouseEvent arg0) {
		drawingTools[currentTool].handleMouseClicked(arg0);		
	}


	public void mousePressed(MouseEvent arg0) {
		drawingTools[currentTool].handleMousePressed(arg0);		
	}


	public void mouseReleased(MouseEvent arg0) {
		drawingTools[currentTool].handleMouseReleased(arg0);		
	}

	public void mouseDragged(MouseEvent arg0) {
		drawingTools[currentTool].handleMouseDragged(arg0);
		
	}

	public void mouseMoved(MouseEvent arg0) {
		drawingTools[currentTool].handleMouseMoved(arg0);
	}	

	public void mouseEntered(MouseEvent arg0) {}		
	public void mouseExited(MouseEvent arg0) {}
	

	// Key listener events
	public void keyTyped(KeyEvent arg0) {
		drawingTools[currentTool].handleKeyTyped(arg0);		
	}


	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub		
	}


	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * Deselects and un-highlights the set of selected elements. 
	 */
	public void clearCurrentSelection(){
		if(currentSelection != null){
			currentSelection.setSelected(false);
			currentSelection.setHighlighted(false);
			currentSelection = null;
			selectionArea.setSize(0,0);			
		}
	}
	
	/**	
	 * FIXME if no graph yet loaded, user can't select anything.
	 * What should the default behavior of the drawing tool be?
	 * 
	 * @param point
	 */
	public void updateCurrentSelection(Point point) {
		 currentSelection = graphModel.getElementIntersectedBy(point);
		 if(currentSelection != null){ currentSelection.setSelected(true); }
	}
 
	/**
	 * Set the current selection to all elements contained by the given rectangle.
	 * 
	 * @param rectangle
	 */
	public void updateCurrentSelection(Rectangle rectangle){
		// IDEA make a GraphElement called Group (see EdgeGroup in Ver1 & 2)
		// that sets highlight(boolean) on all of its elements.		
		currentSelection = graphModel.getElementsContainedBy(rectangle);
		currentSelection.setSelected(true);
	}

	/**
	 * Highlights the graph elements currently selected iff <code>b</code>. 
	 * 
	 * @param b boolean flag to toggle highlighting
	 */
	public void highlightCurrentSelection(boolean b){
		if(currentSelection != null){
			currentSelection.setHighlighted(b);
			currentSelection.setSelected(!b);
		}
	}	
	
	public void setGraphModel(GraphModel graphModel) {
		this.graphModel = graphModel;		
	}

	public GraphModel getGraphModel(){
		return graphModel;
	}
	
	public Rectangle getSelectionArea() {
		return selectionArea;
	}	
	
	/**
	 * Tools types (corresponding to user interaction modes) to 
	 * determine mouse and keyboard responses.
	 */
	public final static int DEFAULT = 0;
	public final static int SELECTION = 1;
	public final static int ZOOM_IN = 2;
	public final static int ZOOM_OUT = 7;
	public final static int SCALE = 8;
	public final static int CREATE = 3;
	public final static int MODIFY = 4;
	public final static int MOVE = 5;
	public final static int TEXT = 6;
	public final static int NUMBER_OF_TOOLS = 9;
	
	/**
	 * Set the current drawing tool to the one with the given tool id.
	 * 
	 * @param toolId 
	 */
	public void setTool(int toolId){
		currentTool = toolId;
		this.setCursor(drawingTools[currentTool].getCursor());
	}	
}
