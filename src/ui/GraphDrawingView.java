 package ui;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JPanel;

import model.fsa.FSAModel;
import model.fsa.ver1.State;
import presentation.Glyph;
import presentation.GraphLabel;
import presentation.fsa.GraphElement;
import presentation.fsa.Node;
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
 * 
 * @author helen bretzke
 *
 */
public class GraphDrawingView extends JComponent implements Subscriber, MouseMotionListener, MouseListener, KeyListener {
			
	private DrawingTool currentTool;
	private DrawingTool[] drawingTools;
	private Font font; 
	private FontMetrics fontMetrics;
	private BasicStroke wideStroke, fineStroke, dashedStroke;
		
//	 Tools types (corresponding to user interaction modes) to 
	// determine mouse and keyboard responses.
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
		graph.insert(new GraphLabel("No Graph.", new Point(100, 100)));
		
		currentSelection = new GraphElement();
		selectionArea = new Rectangle();
		
		drawingTools = new DrawingTool[NUMBER_OF_TOOLS];
		drawingTools[DEFAULT] = new SelectionTool(this);
		
		// TODO construct all other drawing tools
		currentTool = drawingTools[DEFAULT];
		
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
	}	
	
	public void paint(Graphics g){
			
		Graphics2D g2D = (Graphics2D) g; // cast to 2D	
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                         RenderingHints.VALUE_ANTIALIAS_ON);		
	    g2D.setBackground(Color.white);  // FIXME THIS DOESN'T WORK
	    g2D.setStroke(wideStroke);
		graph.draw(g2D);
		
		g2D.setStroke(dashedStroke);
		g2D.setColor(Color.DARK_GRAY);
		g2D.draw(selectionArea);
		
//		 TODO scale or other transformation?
		
		// Warning: scales distance from origin as well as size of nodes
		// we want to scale everything from the centre of the user's view.
		// Solution: translate origin before scaling and beware of op precendence.
//		g2d.translate(-(this.getWidth()/2), -(this.getHeight()/2));
//		g2d.scale(2.0f, 2.0f);

		// NOTE This type extends Component and could be inserted in the panel
//		// if we used an absolute layout manager.
//		// Then we could use geComponentAt(Point) to get the selected glyph in
//		// one call (use the container as the presentation model's DS).
//		// Then would have to make all glyphs extend Component (heavy?).
//		Glyph glyph = new GlyphLabel("(" + (int)p.getX() + "," + (int)p.getY() + ")", p);
//		glyph.draw(g2d);
	}

	/**
	 * Refresh my visual model from GraphModel.
	 */
	public void update() {		
		graph = graphModel.getGraph();
		repaint();
	}

	public Glyph getCurrentSelection() {
		return currentSelection;
	}

	public void setCurrentSelection(Glyph currentSelection) {
		this.currentSelection = currentSelection;
	}	

	// Mouse events
	public void mouseClicked(MouseEvent arg0) {
		currentTool.handleMouseClicked(arg0);		
	}


	public void mousePressed(MouseEvent arg0) {
		currentTool.handleMousePressed(arg0);		
	}


	public void mouseReleased(MouseEvent arg0) {
		currentTool.handleMouseReleased(arg0);		
	}

	public void mouseDragged(MouseEvent arg0) {
		currentTool.handleMouseDragged(arg0);
		
	}

	public void mouseMoved(MouseEvent arg0) {
		currentTool.handleMouseMoved(arg0);
	}	

	public void mouseEntered(MouseEvent arg0) {}		
	public void mouseExited(MouseEvent arg0) {}
	

	// Key listener events
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
// TODO Move these methods into the SelectionTool class; accessor to buffers and graph elements.
	
	/**
	 * TODO Clears and un-highlight the set of selected elements 
	 */
	public void clearCurrentSelection(){
		
	}
	
	/**
	 * IDEA To avoid duplicate code, call overloaded method with a Rectangle with
	 * the same point for opposite corners?
	 * 
	 * @param point
	 */
	public void updateCurrentSelection(Point point) {
		
		// TODO store and highlight the set of intersected elements
		// Delegate to GraphModel.
	}
 
	/**
	 * Set the current selection to all elements contained by the given rectangle.
	 * 
	 * @param rectangle
	 */
	public void updateCurrentSelection(Rectangle rectangle){
		// IDEA make a GraphElement called Group (see EdgeGroup in Ver1 & 2)
		// that sets highlight(boolean) on all of its elements.
		// TODO ask GraphModel to compute set of elements hit.
	}


	public void setGraphModel(GraphModel graphModel) {
		this.graphModel = graphModel;		
	}

	public Rectangle getSelectionArea() {
		return selectionArea;
	}	
	
}
