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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

import model.fsa.FSAObserver;
import model.fsa.ver1.Automaton;
import model.fsa.ver1.State;
import presentation.Glyph;
import presentation.GraphElement;
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
public class DrawingBoard extends Canvas implements FSAObserver, MouseListener, KeyListener {
		
	private DrawingTool currentTool;
	private DrawingTool[] drawingTools;
	private Font font; 
	private FontMetrics fontMetrics;
	private BasicStroke wideStroke, fineStroke;	
	
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
	 * The selected print area.
	 */
	private Glyph printArea;
	

	public DrawingBoard() {	
		graph = new GraphElement();
		currentSelection = new GraphElement();
		
		drawingTools = new DrawingTool[NUMBER_OF_TOOLS];
		drawingTools[DEFAULT] = new SelectionTool(this);
		// TODO construct all other drawing tools
		currentTool = drawingTools[DEFAULT];
		
	    wideStroke = new BasicStroke(2);
	    fineStroke = new BasicStroke(1);
	    this.setVisible(true);
		addMouseListener(this);		
	}	
	
	public void paint(Graphics g){
		
		// TODO scale or other transformation?
		Graphics2D g2D = (Graphics2D) g; // cast to 2D
	    g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                         RenderingHints.VALUE_ANTIALIAS_ON);
	    //g2D.setBackground(Color.white);
	    g2D.setStroke(fineStroke);
		graph.draw(g);
		
		//Graphics2D g2d = (Graphics2D)g;
		
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
	 * Presentation model (the glyph structure that represents the DES model.)
	 */	
	private GraphElement graph;	
	
	/**
	 * Refresh my visual model from the data in the DESModel (Automaton)
	 * 
	 * FIXME spurious updates of graph elements that have not been modified.
	 * 
	 * TODO define equals() on all glyph implementations so that don't need
	 * to update everything. BUT checking every element is almost as expensive as 
	 * rebuilding the graph every time...
	 * 
	 * IDEA Try maintaining a set of dirty bits.
	 * 
	 */
	public void update() {
		
		Automaton a = (Automaton)UIStateModel.instance().getDESModel();
		Iterator states = a.getStateIterator();
		State s;
		
		// TODO for all states in the model, refresh all of my nodes		
		// For now, just create everthing new.		
		graph.clear();
		while(states.hasNext()){
			s = (State)states.next();
			graph.insert(new Node(s), s.getId());
		}
		
		// TODO refresh all free labels
		
		
		// The following two are handled recursively by nodes:
		// for all transitions in the model, refresh all edges		
		// for all events, see transitions
		
		repaint();
	}

	public Glyph getCurrentSelection() {
		return currentSelection;
	}

	public void setCurrentSelection(Glyph currentSelection) {
		this.currentSelection = currentSelection;
	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void mouseClicked(MouseEvent arg0) {
		currentTool.handleMouseClicked(arg0);		
	}


	public void mousePressed(MouseEvent arg0) {
		currentTool.handleMousePressed(arg0);		
	}


	public void mouseReleased(MouseEvent arg0) {
		currentTool.handleMouseReleased(arg0);		
	}


	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	// TODO Move these methods into the SelectionTool class; accessor to buffers and graph elements.
	
//	 TODO clear and un-highlight the set of selected elements
	
	public void clearCurrentSelection(){
		Iterator els = graph.children();
		Node g;
		while(els.hasNext()){
			g = (Node)els.next();
			g.setHighlight(false);
		}		
	}
	
	public void updateCurrentSelection(Point point) {
		
		// TODO store and highlight the set of intersected elements 
		
		Iterator els = graph.children();
		Node g;
		while(els.hasNext()){
			g = (Node)els.next();
			if(g.intersects(point)){
				g.setHighlight(true);
			}else{
				g.setHighlight(false);
			}
		}		
	}
 
	/**
	 * Set the current selection to all elements contained by the given rectangle.
	 * 
	 * @param rectangle
	 */
	public void updateCurrentSelection(Rectangle rectangle){
			
	}
}
