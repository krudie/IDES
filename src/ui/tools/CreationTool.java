package ui.tools;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D.Float;

import main.Hub;

import presentation.fsa.Edge;
import presentation.fsa.GraphDrawingView;
import presentation.fsa.Node;
import ui.command.GraphCommands.CreateCommand;

/**
 * NOTE: Can NOT just make changes to the graph model from here because must
				save each command in the command history.
 *
 * @author helen bretzke
 */
public class CreationTool extends DrawingTool {
	
	private boolean drawingEdge = false;
	private Node sourceNode;
	private Edge edge;
	
	public CreationTool(GraphDrawingView board){
		context = board;		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		
		// JAVA BUG: for any preferred dimension, always 32 X 32 on Windows (works on MAC, what about Linux?)!!
		//System.out.println(toolkit.getBestCursorSize(10, 10));
		
		// FIXME dynamic cursor names in UISettings class
		cursor = toolkit.createCustomCursor(toolkit.createImage("src/images/cursors/create.gif"), new Point(10,0), "CREATE_NODES_OR_EDGES");
		
	}
	
	@Override
	public void handleMouseClicked(MouseEvent me) {
		context.clearCurrentSelection();
		
		Node n = null;
		if(context.updateCurrentSelection(me.getPoint())){
			try{		
				n = (Node)context.getCurrentSelection().child(0);
			}catch(ClassCastException e){
				n = null;
			}
		}
		CreateCommand cmd = null;
		
		if(me.getClickCount() == 1){		  
			  if (drawingEdge) {
				// if intersects with a node
				if (n != null) {
					// get target node and create an edge
					n = (Node) context.getCurrentSelection().child(0);
					
					// DEBUG
					//System.out.println("create edge from source to target");								
					cmd = new CreateCommand(context, CreateCommand.EDGE, me
							.getPoint());					
					cmd.setTargetNode(n);
					cmd.setEdge(edge);					
				} else {
					// else create edge and target node					
					// DEBUG
					//System.out.println("create target node and edge");
										
					cmd = new CreateCommand(context,
							CreateCommand.NODE_AND_EDGE, me.getPoint());
					cmd.setEdge(edge);
				}
				edge = null;
				drawingEdge = false;
				context.clearCurrentSelection();				
			} else {
				if (n != null) {// if intersects with node, start drawing an
								// edge
					
					// DEBUG
					//System.out.println("intersected target node; starting edge drawing");
					sourceNode = n;
					edge = context.getGraphModel().beginEdge(sourceNode);
					drawingEdge = true;					
				} else {
					// otherwise, just create a node
					cmd = new CreateCommand(context, CreateCommand.NODE, me
							.getPoint());
					edge = null;
					drawingEdge = false;
					context.clearCurrentSelection();
				}
			}	    
		} else if (me.getClickCount() == 2) {
			// if intersect a node, draw self-loop
			if (n != null) {
				if(edge != null){
					// Hub.displayAlert("Creating self loop while drawing an unfinished edge.");
					cmd = new CreateCommand(context, CreateCommand.EDGE, me.getPoint());					
					cmd.setTargetNode(n);
					cmd.setEdge(edge);
				}else{				
					// FIXME change this to a CreateCommand so is added to history
					context.getGraphModel().addEdge(n, n);
				}
				edge = null;
				drawingEdge = false;
				context.clearCurrentSelection();
			} else { // otherwise, create a node and start drawing an edge
				cmd = new CreateCommand(context, CreateCommand.NODE, me
						.getPoint());
				drawingEdge = true;				
			}
		}
		
		if (cmd != null) {
			cmd.execute();			
		}
		
		context.repaint();
	}

	@Override
	public void handleMouseDragged(MouseEvent me) {
		// if drawing an edge, recompute the curve
		if(drawingEdge){
			context.getGraphModel().updateEdge(edge, new Float(me.getPoint().x, me.getPoint().y));
			context.repaint();
		}
	}

	@Override
	public void handleMouseMoved(MouseEvent me) {
		// if drawing an edge, recompute the curve
		// TODO and highlight any node under the mouse
		if(drawingEdge){			
			context.getGraphModel().updateEdge(edge, new Float(me.getPoint().x, me.getPoint().y));			
			context.repaint();
		}
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
		// TODO 
		// If drawing edge and user typed ESCAPE
		// drawingEdge = false;
		// Delete the edge from it's source node's children
		
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
