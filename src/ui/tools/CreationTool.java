package ui.tools;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;

import main.Hub;

import presentation.fsa.BezierEdge;
import presentation.fsa.BezierLayout;
import presentation.fsa.ContextAdaptorHack;
import presentation.fsa.Edge;
import presentation.fsa.Node;
import presentation.fsa.InitialArrow;
import presentation.fsa.ReflexiveEdge;
import presentation.fsa.GraphDrawingView;
import presentation.fsa.CircleNode;
import presentation.fsa.CircleNodeLayout;
import ui.command.GraphCommands.CreateCommand;
import presentation.fsa.GraphElement;
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
	private CircleNode sourceNode, targetNode; // nodes to be source and target of created edge
	private CircleNode startNode, endNode; // nodes intersected on mouse pressed and released respectively
	private BezierEdge edge;
	private CreateCommand cmd;	
	private boolean aborted;
	private boolean firstClick;
	
	public CreationTool(){
//		context = board;		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		
		// JAVA BUG: for any preferred dimension, always 32 X 32 on Windows (works on MAC, what about Linux?)!!
		//System.out.println(toolkit.getBestCursorSize(10, 10));
		
		// FIXME dynamic cursor names in UISettings class
		cursor = toolkit.createCustomCursor(toolkit.createImage(Hub.getResource("images/cursors/create.gif")), new Point(0,0), "CREATE_NODES_OR_EDGES");		
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
		super.handleMouseClicked(me);
		
		if(aborted){
			aborted = false;
			return;
		}
		
		if(drawingSelfLoop()){
			if( !firstClick && me.getClickCount() != 2 ){
				// second click on same node so make a self-loop			
				finishSelfLoop();				
			}else{ // double click should activate text (labelling) tool
				abortEdge();
				ContextAdaptorHack.context.setTool(GraphDrawingView.TEXT);
				ContextAdaptorHack.context.getCurrentTool().handleMouseClicked(me);
			}	
		}
	}

	@Override
	public void handleMousePressed(MouseEvent me) {
		super.handleMousePressed(me);

		startNode = null;
		cmd = null;

	
		//Do not clear the selection if the selected element is a BezierEdge
		//Reason: let the user modify the control points of the bezier curve without
		//need to change the curve.
		if(!(ContextAdaptorHack.context.getSelectedElement() instanceof BezierEdge))
		{
			ContextAdaptorHack.context.clearCurrentSelection();
		}
		//Refresh the selection
		ContextAdaptorHack.context.updateCurrentSelection(me.getPoint());
		ContextAdaptorHack.context.repaint();
		GraphElement selectedElement = ContextAdaptorHack.context.getSelectedElement(); 
		if(selectedElement instanceof CircleNode || ContextAdaptorHack.context.getSelectedElement() == null)
			{	//If a node or an empty space is clicked
				startNode = (CircleNode)ContextAdaptorHack.context.getSelectedElement();
				if(!drawingEdge)
				{
					if(startNode != null)
					{
						startEdge(); // assume we're drawing an edge until mouse released decides otherwise.				 
						dragging = true; // assume we're dragging until mouse released decides otherwise.
						return;
					}
				}
			}
			else{//If an edge or label is selected:
				startNode = null;
				ContextAdaptorHack.context.setAvoidNextDraw(true);
				//Let the SELECTION tool work on the edge or label modification
				ContextAdaptorHack.context.setTool(GraphDrawingView.SELECT);
				ContextAdaptorHack.context.getCurrentTool().handleMousePressed(me);
				return;
			}

		}		


	@Override
		public void handleMouseReleased(MouseEvent me) {
			super.handleMouseReleased(me);
			if(!(me.getButton() == MouseEvent.BUTTON1))
			{
				return;
			}
			//Cleaning the selection
			ContextAdaptorHack.context.clearCurrentSelection();
			ContextAdaptorHack.context.updateCurrentSelection(me.getPoint());
			ContextAdaptorHack.context.repaint();

			//Avoiding a new node to be created based in some of the context flags.
			//These flags may be initially set by interface elements which can interrupt the
			//"normal" interaction with the software.
			//Example: 1 - the user open a popup. 
			//         2 - the user click at a point in the canvas to cancel the popup. 
			//		A new node should not be created in this case because the user just
			//      wanted to destroy the popup.
			if(ContextAdaptorHack.context.getAvoidNextDraw() == true)
			{
				ContextAdaptorHack.context.setAvoidNextDraw(false);
				return;
			}

			//Creating a new node:
			endNode = null;
			if(ContextAdaptorHack.context.updateCurrentSelection(me.getPoint())){
				try{		
					endNode = (CircleNode)ContextAdaptorHack.context.getSelectedElement();
				}catch(ClassCastException e){}
			}				
			
			if(startNode == endNode && endNode == sourceNode && drawingEdge && !dragging && firstClick){ // drawing edge by not dragging
				// IDEA To fix conflict with TextTool, delay creation of self loops until we know if user has double clicked.
				// Don't finish edge on mouse released if target == source.
				//// second click on same node so make a self-loop
				//finishSelfLoop();				
				firstClick = false;
				return;
				
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
			}else{
				try{
					finishEdge();
					}catch(Exception e){};
			}
						
			endNode = null;
			ContextAdaptorHack.context.repaint();
		}

	private boolean drawingSelfLoop(){
		return startNode == endNode && endNode == sourceNode && drawingEdge && !dragging;
	}
	
	/**
	 * 
	 */
	private void finishSelfLoop() {
		targetNode = endNode;
		abortEdge();
		cmd = new CreateCommand(CreateCommand.SELF_LOOP, targetNode);
		cmd.execute();
		sourceNode = null;
		targetNode = null;
		ContextAdaptorHack.context.clearCurrentSelection();
	}

	/**
	 * @param point
	 */
	private void createNode(Point point) {
		cmd = new CreateCommand(CreateCommand.NODE, point);
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
		cmd = new CreateCommand(
				CreateCommand.NODE_AND_EDGE, edge, point);				
		cmd.execute();
		// IDEA Don't keep a copy of the temp edge in this class, just use the get and set in context.
		// TODO call abortEdge here and have it do all the work (duplicate code here and in finishEdge).
		edge = null;
		ContextAdaptorHack.context.setTempEdge(null);
		drawingEdge = false;
		dragging = false;
		sourceNode = null;
		targetNode = null;	
		ContextAdaptorHack.context.clearCurrentSelection();			
	}

	/**
	 * 
	 */
	private void startEdge() {
		sourceNode = startNode;
		targetNode = null;
		edge = beginEdge(sourceNode);				
		drawingEdge = true;		
	}
	
	/**
	 * Creates and returns an Edge with source node <code>n1</code>, 
	 * undefined target node, and terminating at the centre of node <code>n1</code>.
	 * 
	 * @param n1 source node
	 * @return a new Edge with source node n1
	 */
	private BezierEdge beginEdge(CircleNode n1){
		BezierLayout layout = new BezierLayout();
		BezierEdge e = new BezierEdge(layout, n1);
		layout.computeCurve((CircleNodeLayout)n1.getLayout(), n1.getLayout().getLocation());
		ContextAdaptorHack.context.setTempEdge(e);
		return e;
	}
	
	
	/**
	 * Updates the layout for the given edge so it extends to the given target point.
	 * 
	 * @param e the Edge to be updated
	 * @param p the target point
	 */
	private void updateEdge(BezierEdge e, Point2D.Float p){		
		CircleNodeLayout s = (CircleNodeLayout)e.getSourceNode().getLayout();
		// only draw the edge if the point is outside the bounds of the source node
		if( ! e.getSourceNode().intersects(p) ){
			e.computeCurve(s, p);
			e.setVisible(true);
		}else{
			e.setVisible(false);
		}
	}
	
	
	private void finishEdge() {		
		targetNode = endNode;
		// DEBUG there are some circumstances where we make it to this method with edge==null
		// ... don't know what they are yet ...
		if (edge != null) {
			cmd = new CreateCommand(CreateCommand.EDGE, edge, targetNode);
			cmd.execute();
		}
		edge = null;
		ContextAdaptorHack.context.setTempEdge(null);
		drawingEdge = false;
		dragging = false;		
		sourceNode = null;
		targetNode = null;
		ContextAdaptorHack.context.clearCurrentSelection();
	}	

	public void handleRightClick(MouseEvent me){
		super.handleRightClick(me);
		abortEdge();
		ContextAdaptorHack.context.repaint();
		//super.handleRightClick(me);		
	}
	
	public boolean isDrawingEdge()
	{
		return drawingEdge;
	}
	
	public void abortEdge() {
		if(drawingEdge){			
			// context.getGraphModel().abortEdge(edge);
			// TODO garbage collect edge
			ContextAdaptorHack.context.setTempEdge(null);
			drawingEdge = false;			
		}
		aborted = true;
		ContextAdaptorHack.context.repaint();
	}

	@Override
	public void handleMouseDragged(MouseEvent me) {
		super.handleMouseDragged(me);
		
		// if drawing an edge, recompute the curve
		if(dragging && drawingEdge){
			updateEdge(edge, new Float(me.getPoint().x, me.getPoint().y));
			//context.getGraphModel().updateEdge(edge, new Float(me.getPoint().x, me.getPoint().y));
			ContextAdaptorHack.context.repaint();
		}
	}

	@Override
	public void handleMouseMoved(MouseEvent me) {
		// if drawing an edge, recompute the curve
		if(!dragging && drawingEdge){
			updateEdge(edge, new Float(me.getPoint().x, me.getPoint().y));
			//context.getGraphModel().updateEdge(edge, new Float(me.getPoint().x, me.getPoint().y));			
			ContextAdaptorHack.context.repaint();
		}
	}

	@Override
	public void handleKeyTyped(KeyEvent ke) {}

	@Override
	public void handleKeyPressed(KeyEvent ke) {}

	@Override
	public void handleKeyReleased(KeyEvent ke) {}	
}
