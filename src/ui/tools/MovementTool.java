package ui.tools;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import main.Hub;

import presentation.fsa.GraphDrawingView;

import ui.command.GraphCommands.MoveCommand;

public class MovementTool extends DrawingTool {

	private Point start, end, prev, next;

	public MovementTool(GraphDrawingView context){
		this.context = context;
		//this.cursor = new Cursor(Cursor.MOVE_CURSOR);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		cursor = toolkit.createCustomCursor(toolkit.createImage(Hub.getResource("images/cursors/move.gif")), new Point(12,12), "MOVE_NODES_OR_LABELS");		
	}
	
	@Override
	public void handleMousePressed(MouseEvent me) {
		// get the object to be moved		
		start = me.getPoint();
		prev = start;
		
		// a group has been selected, move the whole thing
		if(context.hasCurrentSelection() && context.getSelectedGroup().size()>1){
			// FIXME What if user clicks outside the selection group?
			dragging = true;
		}else{ // otherwise update the currently selected element
			context.clearCurrentSelection();
			context.updateCurrentSelection(start);			
					
			if(context.hasCurrentSelection()){
				dragging = true;
			}
			
		}
		context.repaint();
	}
	
	public void handleMouseDragged(MouseEvent me) {
		// update the location of the selected objects 
		if(start == null){
			start = me.getPoint();
			prev = start;
			return;
		}		
		next = me.getPoint();
		context.getSelectedGroup().translate(next.x - prev.x, next.y - prev.y);		
		prev = next;
		context.repaint();
	}	

	@Override
	public void handleMouseReleased(MouseEvent me) {
		end = me.getPoint();

		// Null pointer exception, hard to replicate
		if ((end != null) && (start != null))
		{	
			Point displacement = new Point(end.x - start.x, end.y - start.y);
			if( displacement.x != 0 || displacement.y != 0 ){
				// undo needs to know the selection of moved objects
				// and the total translation
				// save the set of selected objects for undo purposes
				// NOTE: must make COPIES of all references in the selection group		
				MoveCommand moveCmd = new MoveCommand(context, 
													context.getSelectedGroup(), 
													displacement);		
				moveCmd.execute();
			}			
		}
		
		dragging = false;
		start = null;
		prev = null;
		next = null;
		end = null;
		
		// don't deselect groups of multiple elements since user may wish to revise movement
		if( ! (context.getSelectedGroup().size()>1) ){
			context.clearCurrentSelection();			
			context.updateCurrentSelection(me.getPoint());
		}
		
		context.setTool(GraphDrawingView.SELECT);
		context.repaint();		
	}

	@Override
	public void handleKeyTyped(KeyEvent ke) {
		// if user types escape, switch to selection tool
		int code = ke.getKeyCode();
        if(code == KeyEvent.VK_ESCAPE){
        	context.setTool(GraphDrawingView.SELECT);
        }
	}
	
	@Override
	public void handleMouseMoved(MouseEvent me) {}
	
	@Override
	public void handleMouseClicked(MouseEvent me) {}

	@Override
	public void handleKeyPressed(KeyEvent ke) {}

	@Override
	public void handleKeyReleased(KeyEvent ke) {}
	
}