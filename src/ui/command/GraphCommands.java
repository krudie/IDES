package ui.command;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

import main.Hub;

import org.pietschy.command.ActionCommand;
import org.pietschy.command.undo.UndoableActionCommand;

import presentation.fsa.BezierEdge;
import presentation.fsa.ContextAdaptorHack;
import presentation.fsa.Edge;
import presentation.fsa.EdgeLabellingDialog;
import presentation.fsa.GraphDrawingView;
import presentation.fsa.GraphElement;
import presentation.fsa.GraphLabel;
import presentation.fsa.CircleNode;
import presentation.fsa.Node;
import presentation.fsa.NodeLabellingDialog;
import presentation.fsa.SelectionGroup;

public class GraphCommands {

	/**
	 * A command to set the current drawing mode to editing mode. 
	 * While in editing mode, user may select graph objects in the
	 * GraphDrawingView for deleting, copying, pasting and moving.
	 * 
	 * @author Helen Bretzke
	 */
	public static class SelectCommand extends ActionCommand {

//		private GraphDrawingView context;
		
		public SelectCommand(){
			super("select.command");
//			this.context = context;
		}
		@Override
		protected void handleExecute() {			
			// TODO set the tool in the *currently active* drawing view
			ContextAdaptorHack.context.setTool(GraphDrawingView.SELECT);
			ContextAdaptorHack.context.setPreferredTool(GraphDrawingView.SELECT);
		}
	}
	
	/**
	 * Creates nodes and edges in a GraphDrawingView.
	 *	 
	 * @author Helen Bretzke
	 */
	public static class CreateCommand extends UndoableActionCommand {

//		private GraphDrawingView context;
		private int elementType;
		private CircleNode source, target;
		private BezierEdge edge;
		private Point location;
		
		/**
		 * Types of elements to be created.
		 */
		public static final int UNKNOWN = -1;
		public static final int NODE = 0;
		public static final int EDGE = 1;	
		public static final int NODE_AND_EDGE = 2;
		public static final int SELF_LOOP = 3;
		
		/**
		 * Default constructor.
		 */
		public CreateCommand(){		
			super("create.command");
			elementType = UNKNOWN;
		}
		
//		public CreateCommand(GraphDrawingView context){
//			super("create.command");
//			setContext(context, UNKNOWN, null);
//		}
		
		/**
		 * @param context
		 * @param elementType
		 * @param location
		 */
		public CreateCommand(int elementType, Point location){
			setContext(elementType, location);
		}	
		
		public CreateCommand(int elementType, BezierEdge edge, Point location){
			setContext(elementType, location);
			this.edge = edge;
		}	

		
		/**
		 * @param context
		 * @param elementType
		 * @param n
		 */
		public CreateCommand(int elementType, CircleNode n) {
//			this.context = context;			
			this.elementType = elementType;
			source = n;
		}

		/**
		 * @param context
		 * @param elementType
		 * @param edge
		 * @param target
		 */
		public CreateCommand(int elementType, BezierEdge edge, CircleNode target) {
//			this.context = context;
			this.elementType = elementType;
			this.edge = edge;
			this.target = target;
		}

		public void setContext(int elementType, Point location){
//			this.context = context;
			this.elementType = elementType;
			this.location = location;
		}
		
		public void setSourceNode(CircleNode s){
			source = s;
		}
		
		public void setTargetNode(CircleNode t){
			target = t;
			t.setHighlighted(true);
		}
		

		public void setEdge(BezierEdge edge) {
			this.edge = edge;
		}

		@Override
		protected UndoableEdit performEdit() {
			switch(elementType){
			case NODE:				
				ContextAdaptorHack.context.getGraphModel().createNode(new Float(location.x, location.y));
				break;
			case NODE_AND_EDGE:				
				ContextAdaptorHack.context.getGraphModel().finishEdgeAndCreateTargetNode(edge, new Float(location.x, location.y));				
				break;
			case EDGE:
				ContextAdaptorHack.context.getGraphModel().finishEdge(edge, target);				
				break;
			case SELF_LOOP:
				ContextAdaptorHack.context.getGraphModel().createEdge(source, source);
				break;				
			default:
				ContextAdaptorHack.context.setTool(GraphDrawingView.CREATE);
				ContextAdaptorHack.context.setPreferredTool(GraphDrawingView.CREATE);
			}		
			 
			// TODO create and return an UndoableEdit object
			return null;
		}
	}
	
	
	public static class MoveCommand extends UndoableActionCommand {

//		GraphDrawingView context;
		SelectionGroup selection = null;		
		Point displacement;
		
		public MoveCommand() {
			super("move.command");
//			this.context = context;
		}

		/**
		 * 
		 * @param context
		 * @param currentSelection
		 * @param displacement
		 */
		public MoveCommand(SelectionGroup currentSelection, Point displacement) {
			this.selection = currentSelection.copy();
//			this.context = context;
			this.displacement = displacement;
		}

		@Override
		protected UndoableEdit performEdit() {
			if(selection == null){
				ContextAdaptorHack.context.setTool(GraphDrawingView.MOVE);
				ContextAdaptorHack.context.setPreferredTool(GraphDrawingView.MOVE);
				return null;
			}else{
				// finalize movement of current selection in graph model
				//Christian - commitMovement removed
//				ContextAdaptorHack.context.getGraphModel().commitMovement(ContextAdaptorHack.context.getSelectedGroup());
				// TODO create an UndoableEdit object using displacement and 
				// copy of currentSelection and return undoableEdit object
				return null;
			}
			
		}	
	}
	
	
	public static class TextCommand extends UndoableActionCommand {
		
//		GraphDrawingView context;		
		String text;
		GraphElement element = null;
		Point2D.Float location = null;
		
		public TextCommand(){
			super("text.command");
//			this.context = context;
		}
		
		public TextCommand( 
							GraphElement currentSelection, String text) {
			super("text.command");
			this.element = currentSelection;
//			this.context = context;
			this.text = text;
		}
	
		public TextCommand(GraphElement currentSelection) {
			super("text.command");
			this.element = currentSelection;
//			this.context = context;
		}

		/**
		 * @param context
		 * @param location
		 */
		public TextCommand(Point location) {
//			this.context = context;
			this.location = new Point2D.Float(location.x, location.y);
		}

		public void setElement(GraphElement element) {
			this.element = element;
		}
		
		@Override
		protected UndoableEdit performEdit() {
			if(element == null){ 
				// create a new free label
				//TODO uncomment the following statement when finished implementing
				// saving and loading free labels to file.
				/*presentation.fsa.SingleLineFreeLabellingDialog.showAndLabel(
						context.getGraphModel(), location);*/				
			}else{
				// KLUGE: instanceof is rotten style, fix this				
				if (element instanceof CircleNode){				
					Node node = (Node)element;
					// if selection is a node				
					presentation.fsa.SingleLineNodeLabellingDialog.showAndLabel(
							ContextAdaptorHack.context.getGraphModel(),node);
				}else if(element instanceof BezierEdge){
					BezierEdge edge = (BezierEdge)element;			
					EdgeLabellingDialog.showDialog(ContextAdaptorHack.context, edge);					
					// TODO accumulate set of edits that were performed in the edge 
					// labelling dialog
				}else if(element instanceof GraphLabel 
						&& element.getParent() instanceof Edge){
					Edge edge = (Edge)element.getParent();
					EdgeLabellingDialog.showDialog(ContextAdaptorHack.context, edge);
				}else{
					// TODO uncomment the following statement when finished implementing
					// saving and loading free labels to file.
					/*presentation.fsa.SingleLineFreeLabellingDialog.showAndLabel(
							context.getGraphModel(), 
							(GraphLabel)element);*/									
				}
				ContextAdaptorHack.context.repaint();
			}
			element = null;
			text = null;
			// TODO create an UndoableEdit object using
			// my instance variables and return it.
			return null;
		}
	}
	
	public static class ZoomInCommand extends ActionCommand {

//		private GraphDrawingView context;
		
		public ZoomInCommand(){
			super("zoomin.command");
//			this.context = context;
		}
		@Override
		protected void handleExecute() {			
			// TODO set the tool in the *currently active* drawing view
			ContextAdaptorHack.context.setTool(GraphDrawingView.ZOOM_IN);
		}
	}
	
	public static class ZoomOutCommand extends ActionCommand {

//		private GraphDrawingView context;
		
		public ZoomOutCommand(){
			super("zoomout.command");
//			this.context = context;
		}
		@Override
		protected void handleExecute() {			
			// TODO set the tool in the *currently active* drawing view
			ContextAdaptorHack.context.setTool(GraphDrawingView.ZOOM_OUT);
		}
	}

	/**
	 * Represent a user issued command to delete an element of the graph.
	 * ??? What about deleting elements of a text label? 
	 * 
	 * @author helen bretzke
	 *
	 */
	public static class DeleteCommand extends UndoableActionCommand {
		
		private GraphElement element;	 // TODO decide on type, GraphElement composite type?
//		private GraphDrawingView context;  // Does this need to be stored?
		
		/**
		 * Default constructor; handy for exporting this command for group setup.
		 *
		 */
		public DeleteCommand(){
			super("delete.command");
		}
		
//		public DeleteCommand(GraphDrawingView context){
//			this(null, context);
//		}
		
		/**
		 * Creates a command that, when executed, will cut 
		 * <code>element</code> from the given context.
		 * 
		 * @param element
		 * @param context
		 */
		public DeleteCommand(GraphElement element) {
			this();
			this.element = element;
//			this.context = context;
		}		

		public void setElement(GraphElement element){
			this.element = element;
		}
		
		@Override
		protected UndoableEdit performEdit() {
			// TODO return Undoable edit containing removed element and where it should be restored to
			// the view is not enough since view changes models; need to know the model...
			ContextAdaptorHack.context.getGraphModel().delete(element);
			ContextAdaptorHack.context.repaint();
			return null;
		}
		
	}

	/**
	 * Emulates "snap to grid".
	 * 
	 * @author Lenko Grigorov
	 *
	 */
	public static class AlignCommand extends ActionCommand {

		//TODO: redo all of this so there's an independent grid going
		
//		private GraphDrawingView context;
		
		public AlignCommand(){
			super("align.command");
//			this.context = context;
		}
		@Override
		protected void handleExecute() {
			if(Hub.getWorkspace().getActiveModel()==null)
				return;
			Iterator i;
			if(ContextAdaptorHack.context.getSelectedGroup().size()>0)
				i=ContextAdaptorHack.context.getSelectedGroup().children();
			else
				i=ContextAdaptorHack.context.getGraphModel().children();
			while(i.hasNext())
			{
				GraphElement ge=(GraphElement)i.next();
				ge.getLayout().snapToGrid();
				ge.refresh();
			}
		
			ContextAdaptorHack.context.getGraphModel().setNeedsRefresh(true);
			Hub.getWorkspace().fireRepaintRequired();
		}
	}
}
