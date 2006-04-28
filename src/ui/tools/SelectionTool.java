package ui.tools;

import java.awt.Cursor;

import ui.DrawingBoard;

public class SelectionTool extends DrawingTool {

	public SelectionTool(DrawingBoard board){
		context = board;
		cursor = new Cursor(Cursor.DEFAULT_CURSOR);
	}
	
	@Override
	public void handleMouseClicked() {
		// TODO 
		// 1. Compute graph element(s) hit by point clicked
		// 2. set the context's selection buffer

	}

	@Override
	public void handleMouseDragged() {
		// TODO Look up java.awt.dnd

	}

	@Override
	public void handleMousePressed() {
		// TODO store starting point for selection rectangle

	}

	@Override
	public void handleMouseReleased() {
//		 TODO 
		// 1. get end point for selection rectangle 
		// 2. draw a dashed rectangle
		// 3. compute set of all GraphElements contained within (hit by?) the rectangle
		// 4. set the selection in the context's selection buffer

	}

	@Override
	public void handleKeyTyped() {}

}
