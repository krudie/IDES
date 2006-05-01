package ui.tools;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import ui.DrawingBoard;

/**
 * All tools used by the drawing board to handle requests forwarded from keyboard 
 * and mouse events extend this class.  
 * These tools may also update the command history and shared data model.
 *  
 * @author helen bretzke
 *
 */
public abstract class DrawingTool {

	protected DrawingBoard context;
	protected Cursor cursor;
	
	public abstract void handleMouseClicked(MouseEvent me);
	public abstract void handleMouseDragged(MouseEvent me);
	public abstract void handleMousePressed(MouseEvent me);	
	public abstract void handleMouseReleased(MouseEvent me);
	public abstract void handleKeyTyped(KeyEvent ke);	
	
	public Cursor getCursor() { return cursor; }	
}
