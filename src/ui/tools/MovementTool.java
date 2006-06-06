package ui.tools;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import ui.GraphDrawingView;
import ui.command.GraphCommands.MoveCommand;

public class MovementTool extends DrawingTool {

	private Point start, end, prev, next;

	public MovementTool(GraphDrawingView context){
		this.context = context;
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		this.cursor = toolkit.createCustomCursor(toolkit.createImage("C:/Documents and Settings/helen/workspace/IDES2.1/src/images/cursors/hand.gif"), new Point(5,5), "MOVE_NODES_OR_EDGES"); //new Cursor(Cursor.MOVE_CURSOR);
	}
	
	@Override
	public void handleMousePressed(MouseEvent me) {
		// get the object to be moved
		start = me.getPoint();
		prev = start;
		context.clearCurrentSelection();
		context.updateCurrentSelection(start);
		dragging = true;
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
		context.repaint();
		prev = next;
	}	

	@Override
	public void handleMouseReleased(MouseEvent me) {
		end = me.getPoint();
		Point displacement = new Point(end.x - start.x, end.y - start.y);
		// undo needs to know the selection of moved objects
		// and the total translation
		// save the set of selected objects for undo purposes
		// NOTE: must make COPIES of all references in the selection group		
		MoveCommand moveCmd = new MoveCommand(context, 
											context.getCurrentSelection().copy(), 
											displacement);
		// finalize movement changes in graph model
		moveCmd.execute();
		dragging = false;
		start = null;
		prev = null;
		context.clearCurrentSelection();
		//context.setTool(GraphDrawingView.SELECT);
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
	public void handleKeyPressed(KeyEvent ke) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleKeyReleased(KeyEvent ke) {
		// TODO Auto-generated method stub
		
	}

	

}
