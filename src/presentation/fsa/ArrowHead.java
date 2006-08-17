/*
 * Created April 2006
 */
package presentation.fsa;

import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;

import presentation.Geometry;

/**
 * This is a filled polygon in the shape of an arrowhead.
 * 
 * @author Helen Bretzke
 */
@SuppressWarnings("serial")
public class ArrowHead extends Polygon {

	private Point2D.Float basePt = new Point2D.Float(0,0); // the nock where the shaft terminates
	
    public static final int HEAD_LENGTH = 9, SHORT_HEAD_LENGTH = 7;
    private static final double ANGLE = 3*Math.PI/4;
	
    /**
     * Centre axis vector of default direction. 
     */
    public static final Point2D.Float axis = new Point2D.Float(0, 1);   
    
    
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
//    public void update(Point2D.Float dir, Point2D.Float base){
//    	reset();
////    	 TODO replace magic number with the stroke width for the border of node's circle + 1
//    	dir = Geometry.scale(dir, SHORT_HEAD_LENGTH - 2);  
//	    addPoint((int)(base.x + dir.x), (int)(base.y + dir.y));
//	    
//	    // FIXME fix the length of the v vectors
//	    Point2D.Float v = Geometry.scale(Geometry.rotate(dir, ANGLE),0.75f);
//		addPoint((int)(base.x + v.x), (int)(base.y + v.y));	    
//	    addPoint((int)base.x, (int)base.y);
//	    v = Geometry.scale(Geometry.rotate(dir, -ANGLE), 0.75f);	
//	    addPoint((int)(base.x + v.x), (int)(base.y + v.y));
//    }   
 
    
    /**
     * FIXME 
     * - ArrowHead direction is oscillating as target node is moved
     * - symmetry of shape is lost due to precision problems when rotating
     *  
     * @param dir unit direction vector
     * @param base base point of the arrow head
     */
    public void update(Point2D.Float dir, Point2D.Float base){
    	reset();
//    	double alpha = Geometry.angleFrom(vert, dir);
//    	Point2D.Float temp = Geometry.rotate(vert, alpha);
//	    addPoint((int)temp.x, (int)temp.y);
//	    Point2D.Float v = Geometry.scale(Geometry.rotate(temp, ANGLE), 0.75f);
//		addPoint((int)v.x, (int)v.y);	    
//	    addPoint(0, 0);
//	    v = Geometry.scale(Geometry.rotate(temp, -ANGLE), 0.75f);	
//	    addPoint((int)v.x, (int)v.y);
    	this.basePt = base;    	
    	double alpha = Geometry.angleFrom(axis, dir);
    	for(int i=0; i<npoints; i++){
    		Point2D.Float temp = new Point2D.Float(xpoints[i], ypoints[i]);
    		temp = Geometry.rotate(temp, alpha);
    		xpoints[i] = (int)(temp.x + base.x);
    		ypoints[i] = (int)(temp.y + base.y);
    	}    	    	
    }   
    
    public void reset()
    {
    	super.reset();
    	basePt = new Point2D.Float(0,0);
    	// compute default arrowhead pointing down    	
	    addPoint(0, HEAD_LENGTH);	     
		addPoint(-6, -5);	    
		addPoint((int)basePt.x, (int)basePt.y);	   
	    addPoint(6, -5);
    }

	protected Point2D.Float getBasePt() {
		return basePt;
	}
    
    
}
