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
	private boolean firstClick;
	
	public CreationTool(GraphDrawingView board){
		context = board;		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		
		// JAVA BUG: for any preferred dimension, always 32 X 32 on Windows (works on MAC, what about Linux?)!!
		//System.out.println(toolkit.getBestCursorSize(10, 10));
		
		// FIXME dynamic cursor names in UISettings class
		cursor = toolkit.createCustomCursor(toolkit.createImage(Hub.getResource("images/cursors/create__.gif")), new Point(0,0), "CREATE_NODES_OR_EDGES");		
	}
	
	public void init(){
		cmd = null;
		startNode = null;
		endNode = null;
		sourceNode = null;
		endNode = null;
		drawingEdge = false;
		if(edge != null){
			aborted = true;
		}
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
					startEdge(); // assume we're drawing an edge until mouse released decides otherwise.				 
					dragging = true; // assume we're dragging until mouse released decides otherwise.
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
			
			if(startNode == endNode && endNode == sourceNode && drawingEdge && !dragging && firstClick){ // drawing edge by not dragging				
				// second click on same node so make a self-loop
				finishSelfLoop();				
				firstClick = false;
			}else if(startNode != null && startNode == endNode && startNode == sourceNode && dragging){ // select source node, keep drawing edge by mouse move (not dragging)				
				dragging = false;
				firstClick = true;
			}else if(startNode == null && endNode == null && !drawingEdge){								
				// create a new node at current location
				createNode(me.getPoint());
				firstClick = false;
			}else if(startNode == endNode && startNode != sourceNode && endNode != null){ // select target node, finish drawing edge by mouse move				
				finishEdge();		
				firstClick = false;
			}else if(drawingEdge && endNode == null){  // 
				// Assumption: startNode and sourceNode are non-null				
				finishEdgeAndCreateTarget(me.getPoint());
				firstClick = false;
			}else if(drawingEdge && dragging && endNode != null){  // Assumption: sourceNode != null						
				finishEdge();			
			}
						
			endNode = null;
			context.repaint();
		}

	/**
	 * 
	 */
	private void finishSelfLoop() {
		targetNode = endNode;
		abortEdge();
		cmd = new CreateCommand(context, CreateCommand.SELF_LOOP, targetNode);
		cmd.execute();
		sourceNode = null;
		targetNode = null;
		context.clearCurrentSelection();
	}

	/**
	 * @param point
	 */
	private void createNode(Point point) {
		cmd = new CreateCommand(context, CreateCommand.NODE, point);
		cmd.execute();
		abortEdge();
		//dragging = false;		
		sourceNode = null;
		targetNode = null;
		//context.clearCurrentSelection();
	}

	/**
	 * @param point
	 */
	private void finishEdgeAndCreateTarget(Point point) {		
		cmd = new CreateCommand(context,
				CreateCommand.NODE_AND_EDGE, edge, point);				
		cmd.execute();
		edge = null;
		drawingEdge = false;
		dragging = false;
		sourceNode = null;
		targetNode = null;	
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
		abortEdge();
		context.repaint();
		super.handleRightClick(me);		
	}
	
	public boolean isDrawingEdge()
	{
		return drawingEdge;
	}
	
	/**
	 * 
	 */
	public void abortEdge() {
		if(drawingEdge){
			context.getGraphModel().abortEdge(edge);
			drawingEdge = false;			
		}
		aborted = true;		
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
