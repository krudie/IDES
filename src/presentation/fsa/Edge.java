package presentation.fsa;

import io.fsa.ver1.SubElement;

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

import model.fsa.FSATransition;
import model.fsa.ver1.Transition;
import presentation.Glyph;
import presentation.GraphElement;
import presentation.MathUtils;

/**
 * The graphical representation of a transition in a finite state automaton.
 * 
 * @author Helen Bretzke
 *
 */
public class Edge extends GraphElement {

	// the transition that this edge represents
	private FSATransition t;
	private TransitionLayout layout;
	
	// The bezier curve.
	// Review Lenko and Mike's curve code.
	// replace the following with Class java.awt.geom.CubicCurve2D
	private GeneralPath path;
	private Point2D.Float[] controlPoints; // four controls points	
	private ArrowHead arrow;
	
	public static final int P1 = 0;	
	public static final int CTRL1 = 1;
	public static final int CTRL2 = 2;
	public static final int P2 = 3;
	
	public Edge(FSATransition t){		
		this.t = t;
		layout = ((Transition)t).getLayout();
		path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		controlPoints = new Point2D.Float[4];		
		arrow = new ArrowHead();
		update();
	}

	public void draw(Graphics g) {
		super.draw(g);
		Graphics2D g2d = (Graphics2D)g;
		// TODO change to anti-alias and see if can get nicer looking arcs
		g2d.setRenderingHint (RenderingHints.KEY_INTERPOLATION,
                			  RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		if(isHighlight()){
			g2d.setColor(layout.getHighlightColor());
		}else{
			g2d.setColor(Color.BLACK);
		}
		g2d.setStroke(new BasicStroke(2));
		
		// draw myself as a cubic (bezier) curve 	
		path.moveTo(controlPoints[P1].x, controlPoints[P1].y);	    
	    path.curveTo(controlPoints[CTRL1].x, controlPoints[CTRL1].y,
	    			controlPoints[CTRL2].x, controlPoints[CTRL2].y,
	    			controlPoints[P2].x, controlPoints[P2].y);		    
	    g2d.draw(path);
	    
	    // draw an arrowhead
	    g2d.drawPolygon(arrow);
	    g2d.fillPolygon(arrow);
	}
	
	/**
	 * Synchronize my appearance with my transition data.	 	
	 */
	public void update() {
		
		// FIXME Transition objects will not have this information. ///////////
		controlPoints = (Point2D.Float[])layout.getCurve();
		///////////////////////////////////////////////////////////////////////
		
		// Compute and store the arrow layout
		// the direction vector from base to tip of the arrow 
	    Point2D.Float dir = new Point2D.Float(controlPoints[P2].x - controlPoints[CTRL2].x, controlPoints[P2].y - controlPoints[CTRL2].y);    	    
	    arrow = new ArrowHead(MathUtils.unit(dir), controlPoints[P2]);
	    
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
		return controlPoints[P1];
	}

	public Point2D.Float getP2() {
		return controlPoints[P2];
	}
	
	public Point2D.Float getCTRL1() {
		return controlPoints[CTRL1];		
	}

	public Point2D.Float getCTRL2() {
		return controlPoints[CTRL2];		
	}
	
}
