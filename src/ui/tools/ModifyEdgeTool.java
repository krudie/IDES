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
	private EdgeLayout previousLayout;
	private EdgeLayout layout;
	private Float previousCtrlPt;  // store original position of control point for undo
	private int pointType = EdgeHandler.NO_INTERSECTION;  // CTRL1 or CTRL2
	
	public ModifyEdgeTool(GraphDrawingView context){
		this.context = context;
		this.cursor = new Cursor(Cursor.HAND_CURSOR);
	}
	
	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleMouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void handleMouseClicked(MouseEvent m) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleMouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void handleMouseDragged(MouseEvent m) {
		// if dragging 
		if(dragging){  // ??? Why was I using a dragging flag if I'm inside this method ?
			// set the selected control point to the current location
			layout.setPoint(new Float(m.getPoint().x, m.getPoint().y), pointType);			
			// layout.computeCurve(edge.getSource().getLayout(), edge.getTarget().getLayout());
			// repaint the context
			context.repaint();			
		}
	}

	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleMousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void handleMousePressed(MouseEvent m) {
		// Did we hit an edge? So far yes, since we set this tool from the EdgePopup.
		if(context.hasCurrentSelection()){
			// FIXME might have a class cast exception
			edge = (Edge)context.getCurrentSelection().child(0); 
			layout = (EdgeLayout) edge.getLayout();
			// TODO previousLayout = layout.clone();
		
			// get control point selected
			if(edge.getHandler().intersects(m.getPoint())){
				edge.setSelected(true);
				pointType = edge.getHandler().getLastIntersected();
				if(pointType == EdgeLayout.CTRL1 || pointType == EdgeLayout.CTRL2){
					previousCtrlPt = layout.getCurve()[pointType];			
					dragging = true;
				}else{
					pointType = EdgeHandler.NO_INTERSECTION;
					dragging = false;					
				}
			}else{
				// DEBUG
				//Hub.displayAlert("No intersection with EdgeHandler");
			}
		}
	}

	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleMouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void handleMouseReleased(MouseEvent m) {
		// TODO execute a ModifyEdgeCommand which should commit the changes to
		// the edge and its layout via the GraphModel
		ModifyEdgeCommand cmd = new ModifyEdgeCommand();
		cmd.setEdge(edge);
		cmd.setLayout(previousLayout);
		cmd.execute();
		context.repaint();
		dragging = false;
	}

	public void handleKeyTyped(KeyEvent ke) {}
	public void handleKeyPressed(KeyEvent ke) {}
	public void handleKeyReleased(KeyEvent ke) {}
	public void handleMouseMoved(MouseEvent m) {}
}
