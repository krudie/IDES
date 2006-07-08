/**
 * 
 */
package ui.tools;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Point2D.Float;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import main.Hub;

import presentation.PresentationElement;
import presentation.fsa.Edge;
import presentation.fsa.EdgeHandler;
import presentation.fsa.EdgeLayout;
import presentation.fsa.GraphDrawingView;
import presentation.fsa.SelectionGroup;

import ui.command.EdgeCommands.ModifyEdgeCommand;

/**
 * @author Squirrel
 *
 */
public class ModifyEdgeTool extends DrawingTool {
	
	private Edge edge;
	private EdgeLayout previousLayout;  // TODO clone for undo
	private int pointType = EdgeHandler.NO_INTERSECTION;  // types CTRL1 or CTRL2 are moveable	
	
	public ModifyEdgeTool(GraphDrawingView context){
		this.context = context;
		this.cursor =Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/cursors/move.gif")), new Point(12,12), "MOVE_EDGE_CONTROLS");
	}
	
	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleMousePressed(java.awt.event.MouseEvent)
	 * 
	 * TODO refactor logic
	 */
	@Override
	public void handleMousePressed(MouseEvent m) {
		
		// don't clear current selection if we've intersected
		// control point for the current edge
		if(edge != null && edge.isSelected()){
			prepareToDrag(m.getPoint());
			if(dragging) return;
		}
		
		context.clearCurrentSelection();
		context.updateCurrentSelection(m.getPoint());
		if(context.hasCurrentSelection()){
			Edge temp = getEdge(context.getCurrentSelection());
			if( temp != null )
			{
				edge = temp;
				prepareToDrag(m.getPoint());
			}else{				
				switchTool();
			}
		}else{
			switchTool();
		}
		context.repaint();
	}
		
	private Edge getEdge(SelectionGroup selection){
		try{
			Edge temp = (Edge)selection.child(0);
			return temp;								
		}catch(ClassCastException cce){
			return null;
		}
	}
	
	private void switchTool(){
		context.setTool(GraphDrawingView.SELECT);
		dragging = false;
		edge = null;
	}
	
	private void prepareToDrag(Point point){
		if(edge.getHandler().isVisible() && edge.getHandler().intersects(point)){				
			pointType = edge.getHandler().getLastIntersected();
			if(pointType == EdgeLayout.CTRL1 || pointType == EdgeLayout.CTRL2){								
				dragging = true;
			}else{														
				dragging = false;					
			}						
		}
	}
		
	private boolean ready(){
//		 ??? do we need dragging anymore?	
		return edge != null && pointType != EdgeHandler.NO_INTERSECTION && dragging;
	}
	
	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleMouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void handleMouseDragged(MouseEvent m) {
		// came from selection tool
		if(edge == null && context.hasCurrentSelection()){
			edge = getEdge(context.getCurrentSelection());
			if(edge != null){
				prepareToDrag(m.getPoint());
			}else{
				switchTool();
			}
		}
		
		if(dragging){
			// set the selected control point to the current location			
			edge.getLayout().setPoint(new Float(m.getPoint().x, m.getPoint().y), pointType);			
			context.repaint();			
		}
	}

	
	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleMouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void handleMouseReleased(MouseEvent m) {
		if(dragging){ // TODO check to see if edge has been changed
			ModifyEdgeCommand cmd = new ModifyEdgeCommand(edge, previousLayout);		
			cmd.execute();		
			context.repaint();
			switchTool();
		}
		dragging = false;
	}

	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleMouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void handleMouseClicked(MouseEvent m) {}
	public void handleKeyTyped(KeyEvent ke) {}
	public void handleKeyPressed(KeyEvent ke) {}
	public void handleKeyReleased(KeyEvent ke) {}
	public void handleMouseMoved(MouseEvent m) {}
}
