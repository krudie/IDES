package ui.tools;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import presentation.fsa.GraphDrawingView;
import presentation.fsa.GraphElement;
import ui.EdgeLabellingDialog;
import ui.tools.DrawingTool;
import ui.command.GraphCommands;

public class TextTool extends DrawingTool {
		
	private GraphDrawingView context;
	
	public TextTool(GraphDrawingView context){
		this.context = context;		
	}
	
	@Override
	public void handleMouseClicked(MouseEvent me) {
		
		// get current selection
		if(context.updateCurrentSelection(me.getPoint())){			
			new GraphCommands.TextCommand(context, (GraphElement)context.getCurrentSelection().child(0)).execute();			
		}else{
			// if nothing selected
			// TODO create a free label ...			
	
			// For now, open an input dialog and set location at mouse click
			// TODO set location of dialog close to the selected element or click location
			// Set any existing text in the dialog before showing
			// Extract text from dismissed dialog
			String inputValue = JOptionPane.showInputDialog("Enter label text: ");		
			System.out.println(inputValue);
		}
		context.clearCurrentSelection();
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

	@Override
	public void handleKeyPressed(KeyEvent ke) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleKeyReleased(KeyEvent ke) {
		// TODO Auto-generated method stub
		
	}
}
