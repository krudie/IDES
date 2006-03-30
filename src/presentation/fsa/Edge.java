package presentation.fsa;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;

import model.Transition;
import presentation.Glyph;

public class Edge implements Glyph {

	// collection of child glyphs e.g. label(s?) and destination node
	private LinkedList<Glyph> children;
	
	// the abstract concept that this edge represents
	// ??? do i need to know about the transition or simply 
	// the edge and the destination node?
	private Transition t;
		
	public Edge(Transition t){
		this.t = t;
	}

	public void draw(Graphics g) {
		// TODO
		// draw my children
	
		// draw myself
		
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
