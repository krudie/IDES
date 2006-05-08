package ui.tools;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import ui.GraphDrawingView;

/**
 * Selects and highlights graph elements using either single click or bounding box.
 * Supports resizing of bounding box by handles on borders.
 * 
 * @author helen
 *
 */
public class SelectionTool extends DrawingTool {

	private Point startPoint, endPoint; 
	// TODO more cursors for resizing the bounding box
	
	private boolean resizing = false;
	private Rectangle2D box;
	
	public SelectionTool(GraphDrawingView board){
		context = board;
		cursor = new Cursor(Cursor.DEFAULT_CURSOR);
	}
	
	@Override
	public void handleMouseClicked(MouseEvent me) {		
		// TODO if keyboard shift or control, add currently selected item to buffer
		context.updateCurrentSelection(startPoint);
		context.repaint();
	}

	@Override
	/**
	 * Stretch the selection rectangle.
	 */
	public void handleMouseDragged(MouseEvent me) {
		if (inDrag) {
			// figure out where the current point is in relation to the origin			
			endPoint = me.getPoint();
			if(!endPoint.equals(startPoint)){  // TODO check for null?
				// TODO			

				// recompute the bounding rectangle			
				
				// draw the bounding rectangle (dashed)
				
				// set the bounding box in the context so it knows what to draw
				context.repaint();	
			}												
		}
	}

	@Override
	public void handleMouseMoved(MouseEvent me) {
		// TODO
		// if selection rectangle exists, figure out if hovering over a boundary
		// what kind of width is reasonable (surely 1 pixel is a bit mean)
		// set the cursor to the appropriate resize icon
		
	}
	
	@Override
	/**
	 * Handle mouse down events by preparing for a drag.
	 */
	public void handleMousePressed(MouseEvent me) {
		
		// store starting point for selection rectangle.
		startPoint = me.getPoint();
		inDrag = true;
		
	}

	@Override
	/**
	 * On mouse up, select or toggle the items under the mouse 
	 * or in the selection rectangle.
	 */
	public void handleMouseReleased(MouseEvent me) {
		// compute the set of graph elements hit by rectangle
		
		// highlight all selected elements

		// reset flags
		startPoint = null;
		inDrag = false;
	}

	@Override
	public void handleKeyTyped(KeyEvent ke) {}

	

}
