package ui.tools;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import ui.GraphDrawingView;
import ui.command.GraphCommands;

public class MovementTool extends DrawingTool {

	private Point start, end, prev, next;

	public MovementTool(GraphDrawingView context){
		this.context = context;
		this.cursor = new Cursor(Cursor.HAND_CURSOR);
	}
	
	@Override
	public void handleMousePressed(MouseEvent me) {
		// TODO Auto-generated method stub
		// get the object to be moved
		// save the set of selected objects for undo purposes
		// NOTE: must make COPIES of all references in the selection group
		start = me.getPoint();
		dragging = true;
	}
	
	public void handleMouseDragged(MouseEvent me) {
		// update the location of the selected objects 
		if(start == null || prev == null){
			start = me.getPoint();
			prev = start;
			return;
		}		
		next = me.getPoint();
		context.getCurrentSelection().translate(next.x - prev.x, next.y - prev.y);
		context.repaint();
		prev = next;
	}	

	@Override
	public void handleMouseReleased(MouseEvent me) {
		end = me.getPoint();
		Point displacement = new Point(end.x - start.x, end.y - start.y);
		// undo needs to know the selection of moved objects
		// and the total translation
		GraphCommands.MoveCommand moveCmd = new GraphCommands.MoveCommand(context, 
												context.getCurrentSelection(), 
												displacement);
		// finalize movement changes in graph model by calling
		moveCmd.execute();
		dragging = false;
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

	

}
