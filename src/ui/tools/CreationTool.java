package ui.tools;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.pietschy.command.ActionCommand;

import ui.GraphDrawingView;
import ui.UIStateModel;
import ui.command.Command;
import ui.command.CreateCommand;

public class CreationTool extends DrawingTool {
	
	private boolean oddClick = true;
	
	public CreationTool(GraphDrawingView board){
		context = board;		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		// FIXME dynamic cursor names in UISettings class
		cursor = toolkit.createCustomCursor(toolkit.createImage("C:/Documents and Settings/helen/workspace/IDES2.1/src/images/cursors/create.gif"), new Point(3,3), "CREATE_NODES_OR_EDGES");
	}
	
	@Override
	public void handleMouseClicked(MouseEvent me) {
		  ActionCommand cmd;
		  // ??? use click count??
		  
		  // if first click just created a node,
		  //if(oddClick){
			  cmd = new CreateCommand(context, CreateCommand.NODE, me.getPoint());			  
		  //}else{
		  // if second click create and attach an edge and create another node at second click point
			//  cmd = new CreateCommand(context, CreateCommand.NODE_AND_EDGE, me.getPoint());
		 // }
		  UIStateModel.instance().getCommandHistory().add(cmd);
		  cmd.execute();
		  //oddClick = !oddClick;
		  context.repaint();
	}

	@Override
	public void handleMouseDragged(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleMouseMoved(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleMousePressed(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleMouseReleased(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleKeyTyped(KeyEvent ke) {
		// TODO Auto-generated method stub
		
	}	
}
