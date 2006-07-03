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
 * Creates nodes and edges by drawing with mouse in a GraphDrawingView context.
 * 
 * NOTE: Can NOT just make changes to the graph model from here because must
				save each command in the command history.
 *
 * @author helen bretzke
 */
public class CreationTool extends DrawingTool {
	
	private boolean drawingEdge = false;
	private Node sourceNode, targetNode; // nodes to be source and target of created edge
	private Node startNode, endNode; // nodes intersected on mouse pressed and released respectively
	private Edge edge;
	private CreateCommand cmd;	
	private boolean aborted;
	
	public CreationTool(GraphDrawingView board){
		context = board;		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		
		// JAVA BUG: for any preferred dimension, always 32 X 32 on Windows (works on MAC, what about Linux?)!!
		//System.out.println(toolkit.getBestCursorSize(10, 10));
		
		// FIXME dynamic cursor names in UISettings class
		cursor = toolkit.createCustomCursor(toolkit.createImage(Hub.getResource("images/cursors/create.gif")), new Point(10,0), "CREATE_NODES_OR_EDGES");		
	}
	
	@Override
	public void handleMouseClicked(MouseEvent me) {	
		if(aborted){
			aborted = false;
			return;
		}
	}

	@Override
	public void handleMousePressed(MouseEvent me) {		
		context.clearCurrentSelection();
		startNode = null;
		cmd = null;

		if(context.updateCurrentSelection(me.getPoint())){
			try{		
				startNode = (Node)context.getCurrentSelection().child(0);
				if(!drawingEdge){
					startEdge(); // assume we're drawing an edge until mouse released rules otherwise.				 
					dragging = true; // assume we're dragging until mouse released rules otherwise.
				}
			}catch(ClassCastException e){
				startNode = null;
			}
		}		
	}

	@Override
		public void handleMouseReleased(MouseEvent me) {
		
			context.clearCurrentSelection();			
			endNode = null;
			if(context.updateCurrentSelection(me.getPoint())){
				try{		
					endNode = (Node)context.getCurrentSelection().child(0);
				}catch(ClassCastException e){}
			}				
			
			if(startNode == null && endNode == null && !drawingEdge){
				// TODO check to see that we're on the same location				
				// create a new node at current location
				createNode(me.getPoint());
			}else if(startNode == endNode && startNode != null){ // select source node, keep drawing edge by mouse move (not dragging)				
				dragging = false;
			}else if(startNode == endNode && startNode != null && sourceNode != null && sourceNode != startNode){ // select target node, finish drawing edge by mouse move				
				finishEdge();				
			}else if(drawingEdge && endNode == null){  // 
				// Assumption: startNode and sourceNode are non-null				
				finishEdgeAndCreateTarget(me.getPoint());							
			}else if(drawingEdge && dragging && endNode != null){  // Assumption: sourceNode != null						
				finishEdge();			
			}
						
			endNode = null;
			context.repaint();
		}

	/**
	 * @param point
	 */
	private void createNode(Point point) {
		cmd = new CreateCommand(context, CreateCommand.NODE, point);
		cmd.execute();
		dragging = false;
		drawingEdge = false;
		edge = null;		
	}

	/**
	 * @param point
	 */
	private void finishEdgeAndCreateTarget(Point point) {
		targetNode = null;
		cmd = new CreateCommand(context,
				CreateCommand.NODE_AND_EDGE, edge, point);				
		cmd.execute();
		edge = null;
		drawingEdge = false;
		dragging = false;
		sourceNode = null;
		context.clearCurrentSelection();			
	}

	/**
	 * 
	 */
	private void startEdge() {
		sourceNode = startNode;
		targetNode = null;
		edge = context.getGraphModel().beginEdge(sourceNode);				
		drawingEdge = true;		
	}
	
	/**
	 * 
	 */
	private void finishEdge() {		
		targetNode = endNode;				
		cmd = new CreateCommand(context, CreateCommand.EDGE, edge, targetNode);
		cmd.execute();
		edge = null;
		drawingEdge = false;
		dragging = false;		
		sourceNode = null;
		targetNode = null;
		context.clearCurrentSelection();
	}	

	public void handleRightClick(MouseEvent me){
		if(drawingEdge){
			context.getGraphModel().abortEdge(edge);
			drawingEdge = false;			
		}
		aborted = true;		
		context.repaint();
		super.handleRightClick(me);		
	}
	
	@Override
	public void handleMouseDragged(MouseEvent me) {
		// if drawing an edge, recompute the curve
		if(dragging && drawingEdge){
			context.getGraphModel().updateEdge(edge, new Float(me.getPoint().x, me.getPoint().y));
			context.repaint();
		}
	}

	@Override
	public void handleMouseMoved(MouseEvent me) {
		// if drawing an edge, recompute the curve
		if(!dragging && drawingEdge){			
			context.getGraphModel().updateEdge(edge, new Float(me.getPoint().x, me.getPoint().y));			
			context.repaint();
		}
	}

	@Override
	public void handleKeyTyped(KeyEvent ke) {}

	@Override
	public void handleKeyPressed(KeyEvent ke) {}

	@Override
	public void handleKeyReleased(KeyEvent ke) {}	
}
