package ui.tools;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class CreationTool extends DrawingTool {

	public CreationTool(){
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		cursor = toolkit.createCustomCursor(toolkit.createImage("images/cursors/create.gif"), new Point(5,5), "CREATE_NODES_OR_EDGES");
	}
	
	@Override
	public void handleMouseClicked(MouseEvent me) {
		// TODO Auto-generated method stub
		
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
