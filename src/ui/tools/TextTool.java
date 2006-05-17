package ui.tools;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JTextArea;

import model.fsa.ver1.EventsModel;

import ui.EdgeLabellingDialog;
import ui.GraphDrawingView;
import ui.tools.DrawingTool;

public class TextTool extends DrawingTool {

	private JTextArea textArea;
	private EdgeLabellingDialog dialog;
	private GraphDrawingView context;
	
	public TextTool(GraphDrawingView context){
		this.context = context;
		
		// DEBUG
		dialog = new EdgeLabellingDialog();
	}
	
	@Override
	public void handleMouseClicked(MouseEvent me) {
		// TODO Implement
		int count = me.getClickCount();
		if(count == 2){
			// create new label in context of click
			// on a node
			
			// on an edge (open event dialog)
			dialog.setVisible(true);
			
			// free label			
			
		}else if(count == 1){
			// select label intersected by mouse (if any)
			
		}

	}

	@Override
	public void handleKeyTyped(KeyEvent ke) {
		// TODO Implement
		// Forward typed characters to a text entry field
		
		// If user typed escape key, dismiss the text entry field and undo any changes 
		
		// ??? If the entry field has focus, do i need to do anything?
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
}
