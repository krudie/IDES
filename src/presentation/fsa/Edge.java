package presentation.fsa;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.util.LinkedList;

import model.DESTransition;
import model.fsa.Transition;
import presentation.Glyph;
import presentation.GraphElement;

public class Edge extends GraphElement {

	// the abstract concept that this edge represents
	// ??? do i need to know about the transition or simply 
	// the edge and the destination node?
	private Transition t;
	
	// The bezier curve.
	// Review Lenko and Mike's curve code.
	private GeneralPath path;
	private Point[] controls; // four controls points
	
	public Edge(Transition t){
		this.t = t;
		path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		controls = new Point[4];
		update();
	}

	public void draw(Graphics g) {
		super.draw(g);
		Graphics2D g2d = (Graphics2D)g;
		
		// draw myself			    
	    path.moveTo(controls[0].x, controls[0].y);  // first point
	    // cubic (bezier) curve 
	    // (ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2) starts at current point
	    path.curveTo(controls[1].x,controls[1].y,
	    			controls[2].x,controls[2].y,
	    			controls[3].x, controls[3].y);  
	    path.closePath();
	    g2d.draw(path);
	}
	
	/**
	 * TODO Synchronize my appearance with my transition data.
	 */
	public void update() {
		// controls[0] =  
	}
}
