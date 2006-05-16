package ui.tools;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
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
	Dimension d;
	Point p;
	Rectangle box;
	// TODO more cursors for resizing the bounding box
	
	private boolean resizing = false;	
	
	public SelectionTool(GraphDrawingView board){
		context = board;
		cursor = new Cursor(Cursor.DEFAULT_CURSOR);
		d = new Dimension();
		p = new Point();
		box = context.getSelectionArea();
	}
	
	@Override
	public void handleMouseClicked(MouseEvent me) {		
		// TODO if keyboard shift or control, add currently selected item to buffer
		
	}

	@Override
	/**
	 * Stretch the selection rectangle.
	 */
	public void handleMouseDragged(MouseEvent me) {		
		if(!inDrag) {
			context.clearCurrentSelection();
			inDrag = true;
		}

		endPoint = me.getPoint();
		
		if(startPoint == null){
			startPoint = endPoint;
			return;
		}

		if(!endPoint.equals(startPoint)){		
		
				// recompute the bounding rectangle
				//	figure out relative position of start and endpoint to compute top left corner, width and height.
				p.setLocation(Math.min(startPoint.x, endPoint.x),
							  Math.min(startPoint.y, endPoint.y));
				d.setSize(Math.abs(endPoint.x - startPoint.x), 
						  Math.abs(endPoint.y - startPoint.y));											
				box.setLocation(p);
				box.setSize(d);			
				context.repaint();	
			}else{
				// ?
			}
		//}
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
//		 store starting point for selection rectangle.
		startPoint = me.getPoint();						
		context.clearCurrentSelection();
		context.updateCurrentSelection(startPoint);
		context.highlightCurrentSelection(true);
		context.repaint();	
	}

	@Override
	/**
	 * On mouse up, select or toggle the items under the mouse 
	 * or in the selection rectangle.
	 */
	public void handleMouseReleased(MouseEvent me) {		
		if(inDrag){
			// compute the set of graph elements hit by rectangle
			context.updateCurrentSelection(box);
			context.highlightCurrentSelection(false);
					
			// reset flags
			startPoint = null;
			endPoint = null; 
			inDrag = false;
		}else{
			// TODO don't clear the selection, just turn off the highlighting
			// and turn on the selection colour
			context.highlightCurrentSelection(false);			
		}
		context.repaint();
	}

	
	@Override
	public void handleKeyTyped(KeyEvent ke) {
		//	escape key, clear the rectangle
		if(ke.getKeyChar() == KeyEvent.VK_ESCAPE){
			context.clearCurrentSelection();
			context.repaint();
		}		
	}
}
