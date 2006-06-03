package presentation.fsa;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import presentation.GraphicalLayout;
import presentation.PresentationElement;

/**
 * TODO implement and use in SelectionTool and GraphDrawingView
 * 
 * A rectangle used to select elements in a graph.
 * If this rectangle is selected, it can be resized by
 * the handles on its corners.
 * 
 * ??? Should it have its own hover listener and cursors?
 * 
 * @author Helen Bretzke
 *
 */
public class BoundingBox extends Rectangle implements PresentationElement {

	// Instance variables
	// dashed Stroke
	// colour
	// corner handles
	// cursors	
	
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
	}

	public Rectangle2D bounds() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean intersects(Point2D p) {
		// TODO Auto-generated method stub
		return false;
	}

	public void insert(PresentationElement child, long index) {
		// TODO Auto-generated method stub
		
	}

	public void insert(PresentationElement child) {
		// TODO Auto-generated method stub
		
	}

	public void remove(PresentationElement child) {
		// TODO Auto-generated method stub
		
	}

	public PresentationElement child(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public PresentationElement parent() {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator children() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isHighlighted() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setHighlighted(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setSelected(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public boolean isVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setVisible(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public void setLayout(GraphicalLayout layout) {
		// TODO Auto-generated method stub
		
	}

	public GraphicalLayout getLayout() {
		// TODO Auto-generated method stub
		return null;
	}

	public void translate(float x, float y) {
		// TODO Auto-generated method stub
		
	}

	public void setLocation(Point2D p) {
		// TODO Auto-generated method stub
		
	}

	
}
