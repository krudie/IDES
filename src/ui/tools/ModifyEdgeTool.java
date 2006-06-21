/**
 * 
 */
package ui.tools;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import presentation.fsa.GraphDrawingView;

/**
 * @author Squirrel
 *
 */
public class ModifyEdgeTool extends DrawingTool {
	
	private Point start, end, prev, next;

	public ModifyEdgeTool(GraphDrawingView context){
		this.context = context;
		this.cursor = new Cursor(Cursor.HAND_CURSOR);
	}
	
	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleMouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void handleMouseClicked(MouseEvent m) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleMouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void handleMouseDragged(MouseEvent m) {
		// if dragging 
		
		// set the selected control point to the current location
		
		// set the edge layout object to dirty
		
		// repaint the context
		
	}

	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleMouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void handleMouseMoved(MouseEvent m) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleMousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void handleMousePressed(MouseEvent m) {
		// Did we hit an edge?
		
		// get control point selected

		// initialize start point
		
	}

	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleMouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void handleMouseReleased(MouseEvent m) {
		// TODO execute a ModifyEdgeCommand which should commit the changes to
		// the edge and its layout via the GraphModel

	}

	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleKeyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void handleKeyTyped(KeyEvent ke) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleKeyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void handleKeyPressed(KeyEvent ke) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleKeyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void handleKeyReleased(KeyEvent ke) {
		// TODO Auto-generated method stub

	}

}
