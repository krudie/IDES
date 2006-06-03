package ui.command;

import java.awt.Point;
import java.awt.geom.Point2D.Float;

import javax.swing.undo.UndoableEdit;

import org.pietschy.command.ActionCommand;
import org.pietschy.command.undo.UndoableActionCommand;

import presentation.PresentationElement;
import presentation.fsa.Edge;
import presentation.fsa.Node;
import ui.GraphDrawingView;

public class GraphCommands {

	/**
	 * A command to set the current drawing mode to editing mode. 
	 * While in editing mode, user may select graph objects in the
	 * GraphDrawingView for deleting, copying, pasting and moving.
	 * 
	 * @author Helen Bretzke
	 *
	 */
	public static class SelectCommand extends ActionCommand {

		private GraphDrawingView context;
		
		public SelectCommand(GraphDrawingView context){
			super("select.command");
			this.context = context;
		}
		@Override
		protected void handleExecute() {			
			// TODO set the tool in the *currently active* drawing view
			context.setTool(GraphDrawingView.SELECT);
		}
	}
	
	/**
	 * Creates nodes and edges in a GraphDrawingView.
	 * 
	 * TODO change to undoable command and 
	 * figure out how to delete the edge or node that was created.
	 * 
	 * @author Helen Bretzke
	 *
	 */
	public static class CreateCommand extends ActionCommand {

		private GraphDrawingView context;
		private int elementType;
		private Node source, target;
		private Edge edge;
		private Point location;
		
		/**
		 * Types of elements to be created.
		 */
		public static final int UNKNOWN = -1;
		public static final int NODE = 0;
		public static final int EDGE = 1;	
		public static final int NODE_AND_EDGE = 2;
		
		/**
		 * Default constructor.
		 */
		public CreateCommand(){		
			super("create.command");
			elementType = UNKNOWN;
		}
		
		public CreateCommand(GraphDrawingView context){
			super("create.command");
			setContext(context, UNKNOWN, null);
		}
		
		/**
		 * @param context
		 * @param elementType
		 * @param location
		 */
		public CreateCommand(GraphDrawingView context, int elementType, Point location){
			setContext(context, elementType, location);
		}	
		
		public void setContext(GraphDrawingView context,  int elementType, Point location){
			this.context = context;
			this.elementType = elementType;
			this.location = location;
		}
		
		public void setSourceNode(Node s){
			source = s;
		}
		
		public void setTargetNode(Node t){
			target = t;
			t.setHighlighted(true);
		}
		
		@Override
		protected void handleExecute() {		
			switch(elementType){
			case NODE:
				// TODO store the new node
				context.getGraphModel().addNode(new Float(location.x, location.y));
				break;
			case NODE_AND_EDGE:
				// TODO store the new node
				context.getGraphModel().finishEdgeAndAddNode(edge, new Float(location.x, location.y));							
				break;
			case EDGE:
				context.getGraphModel().finishEdge(edge, target);				
				break;
			default:
				// TODO set the tool in the *currently active* drawing view
				// set the current drawing tool to the CreationTool
				 context.setTool(GraphDrawingView.CREATE);
			}		
			// Only AFTER element has been created and added, add this event to the command history
			// NOTE: this must be a reversible command to be entered in the history		
		}

		public void setEdge(Edge edge) {
			this.edge = edge;
		}
	}
	
	
	public static class MoveCommand extends UndoableActionCommand {

		GraphDrawingView context;
		PresentationElement currentSelection;
		Point displacement;
		
		public MoveCommand() {
			super("move.command");
		}

		/**
		 * 
		 * @param context
		 * @param currentSelection
		 * @param displacement
		 */
		public MoveCommand(GraphDrawingView context, PresentationElement currentSelection, Point displacement) {
			this.currentSelection = currentSelection;
			this.context = context;
			this.displacement = displacement;
		}

		@Override
		protected UndoableEdit performEdit() {
			// TODO finalize movement of current selection in graph model
			
			// TODO create an UndoableEdit object using
			// my instance variables and return it.
			
			return null;
		}	
	}
}
