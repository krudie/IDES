package presentation.fsa;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import ui.GUISettings;

/**
 * TODO implement and use in SelectionTool and GraphDrawingView
 * 
 * A rectangle used to select elements in a graph.
 * If this rectangle is selected, it can be resized by
 * the handles on its corners.
 * 
 * ??? Should it have its own hover listener and cursors?
 * If so, needs to be a component...
 * 
 * @author Helen Bretzke
 *
 */
public class BoundingBox extends Rectangle {

	// Instance variables
	// dashed Stroke
	// colour
	// corner handles
	// cursors	
	
	public void draw(Graphics g) {
		Graphics2D g2D = (Graphics2D)g;
		g2D.setStroke(GUISettings.instance().getDashedStroke());
		g2D.setColor(Color.BLACK);
		g2D.draw(this);
	}	
}
