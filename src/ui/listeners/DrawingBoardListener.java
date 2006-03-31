package ui.listeners;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import ui.DrawingBoard;

public class DrawingBoardListener extends MouseAdapter {

	public void mouseClicked(MouseEvent arg0) {
		// get coord of click		
		((DrawingBoard) arg0.getSource()).setPoint(arg0.getPoint());
	}
}
