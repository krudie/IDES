 package ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.util.Iterator;

import javax.swing.JPanel;

import model.DESObserver;
import model.fsa.Automaton;
import model.fsa.State;

import presentation.Glyph;
import presentation.GlyphLabel;
import presentation.GraphElement;
import presentation.fsa.Node;
import ui.listeners.DrawingBoardListener;

/**
 * The area in which users view, create and modify graphs.
 * 
 * @author helen bretzke
 *
 */
public class DrawingBoard extends JPanel implements DESObserver {

	/**
	 * ??? What is this variable? 
	 */
	private static final long serialVersionUID = 1L;
	
	public DrawingBoard() {
		graph = new GraphElement();
		addMouseListener(new DrawingBoardListener());		
	}
	
	
	public void paint(Graphics g){		
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
//		// Then would have to make all glyphs extend Component (heavy).
//		Glyph glyph = new GlyphLabel("(" + (int)p.getX() + "," + (int)p.getY() + ")", p);
//		glyph.draw(g2d);
		
	}

	/**
	 * Presentation model (the glyph structure that represents the DES model.)
	 */	
	private GraphElement graph;	
	
	/**
	 * Refresh my visual model from the data in the DESModel (Automaton)
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
		
		// for all free labels, refresh those as well
		
		
		// The following two should be handled recursively by nodes:
		// for all transitions in the model, refresh all of my edges		
		// for all events, see transitions
		
		repaint();
	}
}
