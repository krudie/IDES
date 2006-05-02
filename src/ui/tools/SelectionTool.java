package ui.tools;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import presentation.Glyph;

import ui.DrawingBoard;

public class SelectionTool extends DrawingTool {

	private Point startPoint, endPoint; 
	
	public SelectionTool(DrawingBoard board){
		context = board;
		cursor = new Cursor(Cursor.DEFAULT_CURSOR);
	}
	
	@Override
	public void handleMouseClicked(MouseEvent me) {		
		// TODO if keyboard shift or control, add currently selected item to buffer		
	}

	@Override
	public void handleMouseDragged(MouseEvent me) {
		// TODO Look up java.awt.dnd

	}

	@Override
	public void handleMousePressed(MouseEvent me) {
		// TODO store starting point for selection rectangle
		startPoint = me.getPoint();
		context.updateCurrentSelection(startPoint);
		context.repaint();
	}

	@Override
	public void handleMouseReleased(MouseEvent me) {
		endPoint = me.getPoint();
		
		if(startPoint.equals(endPoint)){  // user has selected a point
			context.clearCurrentSelection();
			context.repaint();
		}else{
			// user has selected an area

//			 TODO 
			// 2. draw a dashed rectangle
			// 3. compute set of all GraphElements contained within (hit by?) the rectangle
			// 4. set the selection in the context's selection buffer
			// 5. highlight all selected elements

		}

	}

	@Override
	public void handleKeyTyped(KeyEvent ke) {}

}
