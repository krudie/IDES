package ui.tools;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

import presentation.fsa.ContextAdaptorHack;
import presentation.fsa.GraphDrawingView;
import presentation.fsa.GraphElement;
import presentation.fsa.SelectionGroup;
import presentation.fsa.ToolPopup;


/**
 * All tools used by the drawing board to handle requests forwarded from keyboard 
 * and mouse events extend this class.  
 * These tools may also update the command history and shared data model.
 *  
 * @author helen bretzke
 *
 */
public abstract class DrawingTool {

//	protected GraphDrawingView context;
	protected Cursor cursor;
	
// Dragging flag -- set to true when user presses mouse button
// and cleared to false when user releases mouse button.
	protected boolean dragging = false;
		
	public Cursor getCursor() { return cursor; }
	
	public void handleRightClick(MouseEvent m){
		if(ContextAdaptorHack.context!=null)
			ContextAdaptorHack.context.requestFocus();
		
		// get intersected element and display appropriate popup menu
		ContextAdaptorHack.context.clearCurrentSelection();
		if(ContextAdaptorHack.context.updateCurrentSelection(m.getPoint())){
			ContextAdaptorHack.context.getSelectedElement().showPopup(ContextAdaptorHack.context);			
		}else{
			ToolPopup.showPopup(ContextAdaptorHack.context, m);
		}
	}
	
	public void handleMouseClicked(MouseEvent m)
	{
		if(ContextAdaptorHack.context!=null)
			ContextAdaptorHack.context.requestFocus();
	}
	
	public void handleMouseDragged(MouseEvent m)
	{
		if(ContextAdaptorHack.context!=null)
			ContextAdaptorHack.context.requestFocus();
	}
	
	public void handleMouseMoved(MouseEvent m){}
	
	public void handleMousePressed(MouseEvent m)	
	{
		if(ContextAdaptorHack.context!=null)
			ContextAdaptorHack.context.requestFocus();
	}
	
	public void handleMouseReleased(MouseEvent m)
	{
		if(ContextAdaptorHack.context!=null)
			ContextAdaptorHack.context.requestFocus();
	}
	
	public void handleKeyTyped(KeyEvent ke){}	
	public void handleKeyPressed(KeyEvent ke){}
	public void handleKeyReleased(KeyEvent ke){}
	
}
