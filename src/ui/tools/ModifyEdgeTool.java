/**
 * 
 */
package ui.tools;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.geom.Point2D.Float;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import main.Hub;

import presentation.fsa.Edge;
import presentation.fsa.EdgeHandler;
import presentation.fsa.EdgeLayout;
import presentation.fsa.GraphDrawingView;

import ui.command.EdgeCommands.ModifyEdgeCommand;

/**
 * @author Squirrel
 *
 */
public class ModifyEdgeTool extends DrawingTool {
	
	private Edge edge;
	private EdgeLayout previousLayout;  // TODO clone for undo
	private int pointType = EdgeHandler.NO_INTERSECTION;  // CTRL1 or CTRL2
	
	public ModifyEdgeTool(GraphDrawingView context){
		this.context = context;
		this.cursor = new Cursor(Cursor.HAND_CURSOR);
	}
	
	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleMousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void handleMousePressed(MouseEvent m) {
		
		// FIXME what about case where EdgePopup sets this to the current tool
		// and sets the handler to visible, but the edge is still null here ...
		
		if(edge != null){
			if(edge.intersects(m.getPoint())){ // have clicked on selected edge
				// get control point to be moved
				if(edge.getHandler().intersects(m.getPoint())){				
					pointType = edge.getHandler().getLastIntersected();
					if(pointType == EdgeLayout.CTRL1 || pointType == EdgeLayout.CTRL2){								
						dragging = true;
					}else{
						pointType = EdgeHandler.NO_INTERSECTION;
						// context.clearCurrentSelection();
						dragging = false;					
					}
				}
			}else{ // see if we've clicked on a different edge
				Edge oldEdge = edge;
				context.clearCurrentSelection();
				context.updateCurrentSelection(m.getPoint());
				if(context.hasCurrentSelection()){
					try{
						edge = (Edge)context.getCurrentSelection().child(0);
						edge.getHandler().setVisible(true);						
					}catch(ClassCastException cce){
						// clicked on some other kind of graph element
						edge = null;
					}
					oldEdge.getHandler().setVisible(false);
				}
			}
		}else{
			if( ! context.hasCurrentSelection() ){			
				context.updateCurrentSelection(m.getPoint());				
			}
			
			if( context.hasCurrentSelection() ){
				try{
					edge = (Edge)context.getCurrentSelection().child(0);				
					edge.getHandler().setVisible(true);
				}catch(ClassCastException cce){
					// clicked on some other kind of graph element
					edge = null;
				}
			}
		}		
	}
	

	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleMouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void handleMouseDragged(MouseEvent m) { 
		if(dragging){			
			// set the selected control point to the current location
			((EdgeLayout)edge.getLayout()).setPoint(new Float(m.getPoint().x, m.getPoint().y), pointType);			
			// repaint the context
			context.repaint();			
		}
	}


	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleMouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void handleMouseReleased(MouseEvent m) {
		if(edge != null){ // TODO check to see if edge has been changed
			ModifyEdgeCommand cmd = new ModifyEdgeCommand(edge, previousLayout);		
			cmd.execute();		
			context.repaint();
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
