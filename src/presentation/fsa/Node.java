package presentation.fsa;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.LinkedList;

import presentation.Glyph;
import model.State;

public class Node implements Glyph {
	
	// determines the way this node is to be rendered
	// whether initial, final or standard node
	// also tells which transitions and hence edges are incoming and outgoing,
	// to and from this node for the purpose of highlighting and recursive drawing.
	private State s;
	
	// list of labels to be displayed within the bounds of this node
	private LinkedList<Glyph> labels;
	
	private Ellipse2D circle;

	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Draw myself and all incoming and outgoing edges 
	 * in a highlighted colour in the given graphics context.
	 * 
	 * @param g
	 */
	public void highlight(Graphics g) {
				
	}
	
	public Rectangle bounds() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean intersects(Point p) {
		// TODO Auto-generated method stub
		return false;
	}

	public void insert(Glyph child, int index) {
		// TODO Auto-generated method stub
		
	}

	public void remove(Glyph child) {
		// TODO Auto-generated method stub
		
	}

	public Glyph child(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public Glyph parent() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
