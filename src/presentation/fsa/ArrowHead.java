/*
 * Created April 2006
 */
package presentation.fsa;

import java.awt.Polygon;
import java.awt.geom.Point2D;

import presentation.Geometry;

/**
 * This is a filled polygon in the shape of an arrowhead.
 * 
 * @author Helen Bretzke
 */
@SuppressWarnings("serial")
public class ArrowHead extends Polygon {

    public static final int HEAD_LENGTH = 9, SHORT_HEAD_LENGTH = 7;
    private static final double ANGLE = 3*Math.PI/4;   
    
    /**
     * Construct an invisible ArrowHead.
     */
    public ArrowHead(){}

    /**
     * Construct the ArrowHead.
     * 
     * @param dir A unit direction from nock to tip.
     * @param base the coordinates of the nock
     */
    public ArrowHead(Point2D.Float dir, Point2D.Float base){
        update(dir, base);
    } 
      
    public ArrowHead(Point2D.Float dir, Point2D base){
        update(dir, new Point2D.Float((float)base.getX(), (float)base.getY()));
    } 
    
    /**
     * TODO reimplement to use a table of points for all of 360 possible arrows.
     * Do all possible rotations on a single initialization.
     *  
     * @param dir unit direction vector
     * @param base base point of the arrow head
     */
    public void update(Point2D.Float dir, Point2D.Float base){
    	reset();
//    	 TODO replace magic number with the stroke width for the border of node's circle
    	dir = Geometry.scale(dir, SHORT_HEAD_LENGTH - 2);  
	    addPoint((int)(base.x + dir.x), (int)(base.y + dir.y));
	    Point2D.Float v = Geometry.scale(Geometry.rotate(dir, ANGLE),0.75f);
		addPoint((int)(base.x + v.x), (int)(base.y + v.y));	    
	    addPoint((int)base.x, (int)base.y);
	    v = Geometry.scale(Geometry.rotate(dir, -ANGLE), 0.75f);	
	    addPoint((int)(base.x + v.x), (int)(base.y + v.y));
    }   
    
}
