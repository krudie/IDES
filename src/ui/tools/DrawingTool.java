package ui.tools;

import java.awt.Cursor;

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
		
	public abstract void handleMouseClicked();
	public abstract void handleMouseDragged();
	public abstract void handleMousePressed();	
	public abstract void handleMouseReleased();
	public abstract void handleKeyTyped();	
	
	public Cursor getCursor() { return cursor; }	
}
