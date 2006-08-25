package ui.tools;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import main.Hub;
import presentation.fsa.Edge;
import presentation.fsa.GraphDrawingView;

/**
 * Selects and highlights graph elements using either single click or bounding box.
 * TODO Supports resizing of bounding box by handles on corners.
 * 
 * TODO add movement of elements
 * 
 * @author helen
 *
 */
public class SelectionTool extends DrawingTool {

	private Point startPoint, endPoint; 
	Dimension d;
	Point topLeftPt;
	Rectangle box;
	
	// TODO more cursors for resizing the bounding box
	
	private boolean resizing = false;
	private boolean moving = false;
	
	public SelectionTool(GraphDrawingView board){
		context = board;
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		cursor = toolkit.createCustomCursor(toolkit.createImage(Hub.getResource("images/cursors/modify_.gif")), new Point(0,0), "SELECT_NODES_OR_EDGES");		
		d = new Dimension();
		topLeftPt = new Point();
		box = context.getSelectionArea();
	}	

	@Override
	/**
	 * Stretch the selection rectangle.
	 */
	public void handleMouseDragged(MouseEvent me) {		
		if(moving){ return; }
		
		if(!dragging) {
			context.clearCurrentSelection();
			dragging = true;
		}

		if(startPoint == null){
			startPoint = me.getPoint();
			return;
		}
		
		endPoint = me.getPoint();
		
		if(!endPoint.equals(startPoint)){		
				// recompute the bounding rectangle
				//	figure out relative position of start and endpoint to compute top left corner, width and height.
				topLeftPt.setLocation(Math.min(startPoint.x, endPoint.x),
							  Math.min(startPoint.y, endPoint.y));
				d.setSize(Math.abs(endPoint.x - startPoint.x), 
						  Math.abs(endPoint.y - startPoint.y));											
				box.setLocation(topLeftPt);
				box.setSize(d);
				context.updateCurrentSelection(box);
				context.highlightCurrentSelection(true);
				context.repaint();	
			}
		//context.getGraphModel().notifyAllSubscribers();		
}

	@Override
	public void handleMouseMoved(MouseEvent me) {
		// TODO
		// if selection rectangle exists, figure out if hovering over a boundary
		// what kind of width is reasonable (surely 1 pixel is a bit mean)
		// SOLUTION fatten the mouse point
		// set the cursor to the appropriate resize icon
		// Easier to just allow resizing on corner handles (see MagicDraw UML)
		
	}
	
	@Override
	/**
	 * Handle mouse down events by preparing for a drag.
	 */
	public void handleMousePressed(MouseEvent me) {		
		
		// Prepare to move a group of multiple selected elements on drag event
		// only if I have intersected the group.		
		if(context.getSelectedGroup().size()>1 && context.getSelectedGroup().intersects(me.getPoint())) { 			
			context.highlightCurrentSelection(true);
			context.setTool(GraphDrawingView.MOVE);	
			moving = true;
			return;
		}
		
		// If an edge is selected and i have hit a control point handle
		// start modifying the edge		
		if(context.hasCurrentSelection() && context.getSelectedElement() instanceof Edge) { // KLUGE instanceof is evidence of poor design
			context.setTool(GraphDrawingView.MODIFY);
			context.getCurrentTool().handleMousePressed(me);
			return;
		}
		
		context.clearCurrentSelection();
		context.updateCurrentSelection(me.getPoint());				

		
		// if i have pressed the mouse on the current selection		
		if(context.hasCurrentSelection()){
			//prepare to move the selection on drag event
			context.highlightCurrentSelection(true);			
			context.setTool(GraphDrawingView.MOVE);	
			moving = true;
		}else{
			// store starting point for selection rectangle.
			startPoint = me.getPoint();
			moving = false;
			dragging = true;
		}
		
		//context.getGraphModel().notifyAllSubscribers();
		context.repaint();
	}

	@Override
	/**
	 * On mouse up, select or toggle the items under the mouse 
	 * or in the selection rectangle.
	 */
	public void handleMouseReleased(MouseEvent me) {		
		if(dragging){
			// compute the set of graph elements hit by rectangle
			context.updateCurrentSelection(box);
			context.highlightCurrentSelection(true);						
			// reset
			box.setSize(0,0);
			startPoint = null;
			endPoint = null;			
			dragging = false;
		}		
		context.repaint();
	}

	@Override
	public void handleMouseClicked(MouseEvent me) {		
		// TODO if keyboard shift or control, add currently selected item to buffer		
	}	
	
	@Override
	public void handleKeyTyped(KeyEvent ke) {
		//	escape key, clear the rectangle
		if(ke.getKeyChar() == KeyEvent.VK_ESCAPE){
			context.clearCurrentSelection();
			context.repaint();
		}	
	}

	@Override
	public void handleKeyPressed(KeyEvent ke) {}

	@Override
	public void handleKeyReleased(KeyEvent ke) {}
}
