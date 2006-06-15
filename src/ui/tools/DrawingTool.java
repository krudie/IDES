package ui.tools;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

import presentation.fsa.GraphDrawingView;
import presentation.fsa.SelectionGroup;


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
	
// Dragging flag -- set to true when user presses mouse button
// and cleared to false when user releases mouse button.
	protected boolean dragging = false;
		
	public Cursor getCursor() { return cursor; }
	
	public void handleRightClick(MouseEvent m){
		// TODO get intersected element and display appropriate popup menu
		context.clearCurrentSelection();
		if(context.updateCurrentSelection(m.getPoint())){
			SelectionGroup g = context.getCurrentSelection();
						
		}
		// DEBUG
		JOptionPane.showMessageDialog(context, "Right clicked!");		 
	}
	
	public abstract void handleMouseClicked(MouseEvent m);
	public abstract void handleMouseDragged(MouseEvent m);
	public abstract void handleMouseMoved(MouseEvent m);
	public abstract void handleMousePressed(MouseEvent m);	
	public abstract void handleMouseReleased(MouseEvent m);
	public abstract void handleKeyTyped(KeyEvent ke);	
	public abstract void handleKeyPressed(KeyEvent ke);
	public abstract void handleKeyReleased(KeyEvent ke);
	
}
