package ui.tools;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import presentation.fsa.Node;
import ui.GraphDrawingView;
import ui.UIStateModel;
import ui.command.CreateCommand;

/**
 * NOTE: Can NOT just make changes to the graph model from here because must
				save each command in the command history.
 *
 * @author helen bretzke
 */
public class CreationTool extends DrawingTool {
	
	private boolean drawingEdge = false;
	private Node sourceNode;
	private Point startPoint;
	
	public CreationTool(GraphDrawingView board){
		context = board;		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		// FIXME dynamic cursor names in UISettings class
		cursor = toolkit.createCustomCursor(toolkit.createImage("C:/Documents and Settings/helen/workspace/IDES2.1/src/images/cursors/create.gif"), new Point(3,3), "CREATE_NODES_OR_EDGES");
	}
	
	@Override
	public void handleMouseClicked(MouseEvent me) {
		context.updateCurrentSelection(me.getPoint());
		Node n;
		try{
			n = (Node)context.getCurrentSelection();
		}catch(ClassCastException e){
			n = null;
		}
		
		CreateCommand cmd = null;
		
		if(me.getClickCount() == 1){		  
			  if (drawingEdge) {
				// if intersects with a node
				if (n != null) {
					// get target node and create an edge
					n = (Node) context.getCurrentSelection();
					
					// DEBUG
					System.out.println("create edge from source to target");
					// TODO pass source and target nodes to CreateCommand
					cmd = new CreateCommand(context, CreateCommand.EDGE, me
							.getPoint());
					cmd.setSourceNode(sourceNode);
					cmd.setTargetNode(n);
				} else {
					// else create target node and create an edge
					
					// DEBUG
					System.out.println("create target node and edge");
										
					cmd = new CreateCommand(context,
							CreateCommand.NODE_AND_EDGE, me.getPoint());
					cmd.setSourceNode(sourceNode);
				}
				drawingEdge = false;
			} else {
				if (n != null) {// if intersects with node, start drawing an
								// edge
					
					// DEBUG
					System.out.println("intersected target node; starting edge drawing");
					sourceNode = n;
					drawingEdge = true;
				} else {
					// otherwise, just create a node
					cmd = new CreateCommand(context, CreateCommand.NODE, me
							.getPoint());
					drawingEdge = false;
				}
			}
			  // FIXME this will never happen since context always forwards dblclick to TextTool
		} else if (me.getClickCount() == 2) {
			// if intersect a node, draw self-loop
			if (n != null) {
				// FIXME change this to a CreateCommand so is added to history
				context.getGraphModel().addEdge(n, n);
				drawingEdge = false;
				
			} else { // otherwise, create a node and start drawing an edge
				cmd = new CreateCommand(context, CreateCommand.NODE, me
						.getPoint());
				drawingEdge = true;
			}
		}
		if (cmd != null) {
			cmd.execute();
			UIStateModel.instance().getCommandHistory().add(cmd);
		}
		// oddClick = !oddClick;
		context.repaint();
	}

	@Override
	public void handleMouseDragged(MouseEvent me) {
		// TODO if drawing an edge, set P2, recompute curve and repaint
		
	}

	@Override
	public void handleMouseMoved(MouseEvent me) {
		// TODO if drawing an edge, set P2, recompute curve and repaint
		
	}

	@Override
	public void handleMousePressed(MouseEvent me) {
		// TODO if intersect with node, start drawing an edge
		
	}

	@Override
	public void handleMouseReleased(MouseEvent me) {
		// TODO	if drawing an edge
		// TODO factor out duplicate code from handleMouseClicked
		// if intersect with node, 
		// get the target node and create the edge 
		
		// else create the target node and the edge
		
		// if not drawing an edge, just create a node
		
	}

	@Override
	public void handleKeyTyped(KeyEvent ke) {
		// TODO Auto-generated method stub
		
	}	
}
