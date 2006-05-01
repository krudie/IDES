package ui.tools;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import presentation.Glyph;

import ui.DrawingBoard;

public class SelectionTool extends DrawingTool {

	public SelectionTool(DrawingBoard board){
		context = board;
		cursor = new Cursor(Cursor.DEFAULT_CURSOR);
	}
	
	@Override
	public void handleMouseClicked(MouseEvent me) {		
		// if keyboard holds shift or control down, add currently selected item to buffer		
	}

	@Override
	public void handleMouseDragged(MouseEvent me) {
		// TODO Look up java.awt.dnd

	}

	@Override
	public void handleMousePressed(MouseEvent me) {
		// TODO store starting point for selection rectangle		
		context.updateCurrentSelection(me.getPoint());
		context.repaint();
	}

	@Override
	public void handleMouseReleased(MouseEvent me) {
		context.clearCurrentSelection();
		context.repaint();
//		 TODO 
		// 1. get end point for selection rectangle 
		// 2. draw a dashed rectangle
		// 3. compute set of all GraphElements contained within (hit by?) the rectangle
		// 4. set the selection in the context's selection buffer

	}

	@Override
	public void handleKeyTyped(KeyEvent ke) {}

}
