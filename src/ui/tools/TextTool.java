package ui.tools;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
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
		
		// get current selection
		context.updateCurrentSelection(me.getPoint());
		
		// TODO on a node ...
		
			
		// TODO on an edge (open event dialog)

		
		// TODO on a free label ...
		
		// if nothing selected
		// TODO create a free label ...			

		// For now, open an input dialog and set location at mouse click
		// TODO set location of dialog close to the selected element or click location
		// Set any existing text in the dialog before showing
		// Extract text from dismissed dialog
		String inputValue = JOptionPane.showInputDialog("Enter label text: ");		
		System.out.println(inputValue);
		 
		
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