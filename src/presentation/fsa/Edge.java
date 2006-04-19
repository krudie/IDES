package presentation.fsa;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.LinkedList;

import model.DESTransition;
import model.fsa.SubElement;
import model.fsa.Transition;
import presentation.Glyph;
import presentation.GraphElement;

/**
 * The graphical representation of a transition in a finite state automaton.
 * 
 * @author Helen Bretzke
 *
 */
public class Edge extends GraphElement {

	// the transition that this edge represents
	private Transition t;
	
	// The bezier curve.
	// Review Lenko and Mike's curve code.
	// replace the following with Class java.awt.geom.CubicCurve2D
	private GeneralPath path;
	private Point2D.Float[] controls; // four controls points
	private Polygon arrow; // the triangle representing the arrow
	
	public Edge(Transition t){
		this.t = t;
		path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		controls = new Point2D.Float[4];		
		arrow = new Polygon();
		update();
	}

	public void draw(Graphics g) {
		super.draw(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(2));
		// draw myself as a cubic (bezier) curve 	
		path.moveTo(controls[0].x, controls[0].y);	    
	    path.curveTo(controls[1].x, controls[1].y,
	    			controls[2].x, controls[2].y,
	    			controls[3].x, controls[3].y);		    
	    g2d.draw(path);
	    
	    // draw the arrow and fill it
	    g2d.drawPolygon(arrow);
	    g2d.fillPolygon(arrow);
	}
	
	/**
	 * Synchronize my appearance with my transition data.
	 * FIXME all of these string constants and data structure manipulation should be hidden
	 * behind the State, Transition and Event interface model.fsa ! 
	 */
	public void update() {
		SubElement arc = t.getSubElement("graphic").getSubElement("bezier"); 
		controls[0] = new Point2D.Float(Float.parseFloat(arc.getAttribute("x1")), 
				Float.parseFloat(arc.getAttribute("y1")));
		controls[1] = new Point2D.Float(Float.parseFloat(arc.getAttribute("ctrlx1")), 
				Float.parseFloat(arc.getAttribute("ctrly1")));
		controls[2] = new Point2D.Float(Float.parseFloat(arc.getAttribute("ctrlx2")), 
				Float.parseFloat(arc.getAttribute("ctrly2")));
		controls[3] = new Point2D.Float(Float.parseFloat(arc.getAttribute("x2")), 
				Float.parseFloat(arc.getAttribute("y2")));
		
		// Compute and store the arrow layout
		// the direction vector from base to tip of the arrow 
	    Point2D.Float dir = unit(new Point2D.Float(controls[3].x - controls[2].x, controls[3].y - controls[2].y));	    
	    Point2D.Float base = controls[3];
	    // corner points of the triangle
	    arrow.addPoint((int)(base.x + dir.x), (int)(base.y + dir.y));
		arrow.addPoint((int)(base.x + dir.x/2), (int)(base.y + dir.y/2));
		arrow.addPoint((int)(base.x - dir.x/2), (int)(base.y - dir.y/2));
		
		// TODO label[s] from associated event[s]		
		
	}
	
// TODO Move the following methods to a utilities class.
	
	/**
	 * Returns the norm of the given vector.
	 * 
	 * @param vector
	 * @return norm (length) of vector
	 */
	private double norm(Point2D.Float vector) {
		return Math.sqrt(Math.pow(vector.x, 2) + Math.pow(vector.y, 2));		
	}
	
	/**
	 * 
	 * @param p
	 * @return the unit direction vector for p.
	 */
	private Point2D.Float unit(Point2D.Float p){
		float n = (float)norm(p);
		Point2D.Float p1 = new Point2D.Float(p.x/n, p.y/n);
		return p1;
	}

	/**
	 * 
	 * @param p
	 * @return the vector perpendicular to p (rotated 90 degrees clockwise)
	 */
	private Point2D.Float perp(Point2D.Float p){
		return new Point2D.Float(p.y, -p.x);		
	}
}
