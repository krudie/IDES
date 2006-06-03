package ui.tools;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import ui.GraphDrawingView;

/**
 * All tools used by the drawing board to handle requests forwarded from keyboard 
 * and mouse events extend this class.  
 * These tools may also update the command history and shared data model.
 *  
 * @author helen bretzke
 *
 */
public abstract class DrawingTool {

	protected GraphDrawingView context;
	protected Cursor cursor;
	
// Dragging flag -- set to true when user presses mouse button over checker
// and cleared to false when user releases mouse button.

	protected boolean dragging = false;
	
	public abstract void handleMouseClicked(MouseEvent me);
	public abstract void handleMouseDragged(MouseEvent me);
	public abstract void handleMouseMoved(MouseEvent me);
	public abstract void handleMousePressed(MouseEvent me);	
	public abstract void handleMouseReleased(MouseEvent me);
	public abstract void handleKeyTyped(KeyEvent ke);	
	
	public Cursor getCursor() { return cursor; }	
}
