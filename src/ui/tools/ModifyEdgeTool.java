/**
 * 
 */
package ui.tools;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import main.Annotable;
import main.Hub;
import presentation.GraphicalLayout;
import presentation.fsa.BezierEdge;
import presentation.fsa.BezierHandler;
import presentation.fsa.ContextAdaptorHack;
import presentation.fsa.Edge;
import presentation.fsa.EdgeHandler;
import presentation.fsa.GraphDrawingView;
import presentation.fsa.GraphElement;
import presentation.fsa.InitialArrow;
import presentation.fsa.ReflexiveEdge;
import ui.command.EdgeCommands.ModifyEdgeAction;

/**
 * @author Squirrel
 *
 */
public class ModifyEdgeTool extends DrawingTool {

	private Edge edge;
	private GraphicalLayout previousLayout;  // TODO clone for undo
	private int pointType = BezierHandler.NO_INTERSECTION;  // types CTRL1 or CTRL2 are moveable	

	public ModifyEdgeTool(){
//		this.context = context;
		this.cursor =Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/cursors/move.gif")), new Point(12,12), "MOVE_EDGE_CONTROLS");
	}

	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleMousePressed(java.awt.event.MouseEvent)
	 * 
	 * TODO refactor logic
	 */
	@Override
	public void handleMousePressed(MouseEvent m) {
		super.handleMousePressed(m);

		// FIXME If an edge was just selected by SelectionTool,
		// use it, don't lose it.
		if(ContextAdaptorHack.context.hasCurrentSelection()){
			Edge temp = getEdge(ContextAdaptorHack.context.getSelectedElement());
			if( temp != null )
			{
				edge = temp;

				ByteArrayOutputStream fo = new ByteArrayOutputStream();
				try{
					ObjectOutputStream so = new ObjectOutputStream(fo);
					so.writeObject(edge.getLayout());
					so.flush();
					so.close();
					ByteArrayInputStream is = new ByteArrayInputStream(fo.toByteArray());
					ObjectInputStream objectIS = new ObjectInputStream(is);
					previousLayout = (GraphicalLayout)objectIS.readObject();
				}catch(IOException e){
					Hub.displayAlert(e.getMessage());
				}catch(ClassNotFoundException e)
				{
					Hub.displayAlert(e.getMessage());
				}
			}
		}

		// don't clear current selection since we may have 
		// intersected a control point for the current edge
		if(edge != null && edge.isSelected()){
			prepareToDrag(m.getPoint());
			if(dragging) return;
		}

		ContextAdaptorHack.context.clearCurrentSelection();
		ContextAdaptorHack.context.updateCurrentSelection(m.getPoint());
		if(ContextAdaptorHack.context.hasCurrentSelection()){
			Edge temp = getEdge(ContextAdaptorHack.context.getSelectedElement());
			if( temp != null )
			{
				edge = temp;				
			}else{				
				switchTool();
				ContextAdaptorHack.context.getCurrentTool().handleMousePressed(m);
			}
		}else{
			switchTool();
			ContextAdaptorHack.context.getCurrentTool().handleMousePressed(m);
		}
		ContextAdaptorHack.context.repaint();
	}

	private Edge getEdge(GraphElement selection){
		try{
			Edge temp = (Edge)selection;
			return temp;								
		}catch(ClassCastException cce){
			return null;
		}
	}

	private void switchTool(){
		ContextAdaptorHack.context.setTool(ContextAdaptorHack.context.getPreferredTool());
		dragging = false;
		edge = null;
	}

	private void prepareToDrag(Point point){
		if(edge.getHandler().isVisible() && edge.getHandler().intersects(point)){				
			pointType = ((EdgeHandler)edge.getHandler()).getLastIntersected();
			if(edge.isMovable(pointType)){								
				dragging = true;
			}else{														
				dragging = false;					
			}						
		}
	}

//	private boolean ready(){
////	??? do we need dragging anymore?	
//	return edge != null && pointType != BezierHandler.NO_INTERSECTION && dragging;
//	}

	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleMouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void handleMouseDragged(MouseEvent m) {
		super.handleMouseDragged(m);

		// came from selection tool
		if(edge == null && ContextAdaptorHack.context.hasCurrentSelection()){
			edge = getEdge(ContextAdaptorHack.context.getSelectedElement());
			if(edge != null){
				prepareToDrag(m.getPoint());
			}else{
				switchTool();
			}
		}

		if(dragging){
			// set the selected control point to the current location			
			edge.setPoint(new Point2D.Float(m.getPoint().x, m.getPoint().y), pointType);			
			ContextAdaptorHack.context.repaint();			
		}
	}


	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleMouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void handleMouseReleased(MouseEvent m) {
		super.handleMouseReleased(m);

		if(dragging){ // TODO check to see if edge has been changed
			ModifyEdgeAction cmd = new ModifyEdgeAction(edge, previousLayout);		
			cmd.execute();		
			ContextAdaptorHack.context.repaint();						
			dragging = false;	
			switchTool();
		}
	}



	/* (non-Javadoc)
	 * @see ui.tools.DrawingTool#handleMouseClicked(java.awt.event.MouseEvent)
	 */
//	@Override
//	public void handleMouseClicked(MouseEvent m) {}
//	public void handleKeyTyped(KeyEvent ke) {}
//	public void handleKeyPressed(KeyEvent ke) {}
//	public void handleKeyReleased(KeyEvent ke) {}
//	public void handleMouseMoved(MouseEvent m) {}
}
