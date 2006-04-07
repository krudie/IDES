package presentation.fsa;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.util.LinkedList;

import model.DESTransition;
import presentation.Glyph;

public class Edge implements Glyph {

	// collection of child glyphs e.g. label(s?) and destination node
	private LinkedList<Glyph> children;
	
	// the abstract concept that this edge represents
	// ??? do i need to know about the transition or simply 
	// the edge and the destination node?
	private DESTransition t;
	
	// The bezier curve.
	// Review Lenko and Mike's curve code.
	private GeneralPath path;
	private Point[] controls; // four controls points
	
	public Edge(DESTransition t){
		this.t = t;
		path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		controls = new Point[4];
	}

	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		// TODO
		// draw my children
	
		// draw myself			    
	    path.moveTo(20.0f,50.0f);  // first point
	    path.curveTo(260.0f,100.0f,130.0f,50.0f,225.0f,0.0f);  // cubic (bezier) curve (ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2) starts at current point
	    path.closePath();
	    g2d.draw(path);
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
