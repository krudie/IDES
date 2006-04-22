package presentation.fsa;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
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
	
	// TODO factor out arrow code for reuse by initial state nodes
	private Polygon arrow; // the triangle representing the arrow
	
	public static final int P1 = 0;	
	public static final int CTRL1 = 1;
	public static final int CTRL2 = 2;
	public static final int P2 = 3;
	
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
		// TODO change to anti-alias and see if can get nicer looking arcs
		g2d.setRenderingHint (RenderingHints.KEY_INTERPOLATION,
                			  RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(2));
		// draw myself as a cubic (bezier) curve 	
		path.moveTo(controls[P1].x, controls[P1].y);	    
	    path.curveTo(controls[CTRL1].x, controls[CTRL1].y,
	    			controls[CTRL2].x, controls[CTRL2].y,
	    			controls[P2].x, controls[P2].y);		    
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
	    Point2D.Float dir = new Point2D.Float(controls[3].x - controls[2].x, controls[3].y - controls[2].y);	    
	    dir = scale(unit(dir), SHORT_HEAD_LENGTH);	    	    
	    Point2D.Float base = controls[3];
	    double angle = 3*Math.PI/4;
	    arrow.addPoint((int)(base.x + dir.x), (int)(base.y + dir.y));
	    Point2D.Float v = scale(rotate(dir, angle),0.75f);
		arrow.addPoint((int)(base.x + v.x), (int)(base.y + v.y));	    
	    arrow.addPoint((int)base.x, (int)base.y);
	    v = scale(rotate(dir, -angle), 0.75f);	
	    arrow.addPoint((int)(base.x + v.x), (int)(base.y + v.y));
		// TODO label[s] from associated event[s]		
		
	}
	
	
	/** 
	 * NOTE intersects with control point handles will be a different operation.
	 * 
	 * @return true iff p intersects with this edge. 
	 */
	public boolean intersects(Point p){		
		return path.contains(p) || arrow.contains(p);
	}
	
	public Point2D.Float getP1() {
		return controls[P1];
	}

	public Point2D.Float getP2() {
		return controls[P2];
	}
	
	public Point2D.Float getCTRL1() {
		return controls[CTRL1];		
	}

	public Point2D.Float getCTRL2() {
		return controls[CTRL2];		
	}

	/**
	 * From Michael Wood's code:
	 * 
     * The dimensions of the arrow head.
     * 
     *    tang
     *     \\
     *       \\\\ 
     *   nock ]>>>>> tip
     *       ////  
     *     //
     *     tang 
     * 
     * HEAD_LENGTH = nock to tip.
     * TANG_X = distance along shaft from tip to projection of tang on shaft.
     * TANG_Y = distance perpendicluar to shaft from projection of tang on shaft to tang.
     */
	public static final int HEAD_LENGTH = 9,
							TANG_X = 13,
							TANG_Y = 5,
							SHORT_HEAD_LENGTH = 7;

	
//	 TODO Move the following methods to a utilities class. ///////////////////
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
	 * @param v vector with origin at (0,0) and given direction
	 * @return the vector perpendicular to v (rotated 90 degrees clockwise)
	 */
	private Point2D.Float perp(Point2D.Float v){
		return new Point2D.Float(v.y, -v.x);		
	}
	
	/**
	 * @param v vector with origin at (0,0) and given direction
	 * @param r radians
	 * @return the vector resulting from rotating v by r radians
	 */
	private Point2D.Float rotate(Point2D.Float v, double r) {
		float c = (float)Math.cos(r);
		float s = (float)Math.sin(r);
		return new Point2D.Float(v.x*c + v.y*s, v.y*c - v.x*s);	
	}
	
	/** 
	 * @param v vector with origin at (0,0) and given direction
	 * @param s the scalar 
	 * @return the result of scaling v by s
	 */
	private Point2D.Float scale(Point2D.Float v, float s) {		
		return new Point2D.Float(Math.round(v.x * s), Math.round(v.y * s));		
	}
}
