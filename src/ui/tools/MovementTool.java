package ui.tools;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import presentation.fsa.GraphDrawingView;

import ui.command.GraphCommands.MoveCommand;

public class MovementTool extends DrawingTool {

	private Point start, end, prev, next;

	public MovementTool(GraphDrawingView context){
		this.context = context;
		this.cursor = new Cursor(Cursor.MOVE_CURSOR);
	}
	
	@Override
	public void handleMousePressed(MouseEvent me) {
		// get the object to be moved		
		start = me.getPoint();
		prev = start;
		
		// a group has been selected, move the whole thing
		if(context.hasCurrentSelection() && context.getCurrentSelection().hasMultipleElements()){		
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
		context.getCurrentSelection().translate(next.x - prev.x, next.y - prev.y);		
		prev = next;
		context.repaint();
	}	

	@Override
	public void handleMouseReleased(MouseEvent me) {
		end = me.getPoint();
		Point displacement = new Point(end.x - start.x, end.y - start.y);
		if( displacement.x != 0 || displacement.y != 0 ){
			// undo needs to know the selection of moved objects
			// and the total translation
			// save the set of selected objects for undo purposes
			// NOTE: must make COPIES of all references in the selection group		
			MoveCommand moveCmd = new MoveCommand(context, 
												context.getCurrentSelection(), 
												displacement);		
			moveCmd.execute();
		}
		dragging = false;
		start = null;
		prev = null;
		next = null;
		end = null;
		
		context.clearCurrentSelection();
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