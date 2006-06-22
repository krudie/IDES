package ui.command;

import java.awt.Point;
import java.awt.geom.Point2D.Float;

import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

import org.pietschy.command.ActionCommand;
import org.pietschy.command.undo.UndoableActionCommand;

import presentation.fsa.Edge;
import presentation.fsa.EdgeLabellingDialog;
import presentation.fsa.GraphDrawingView;
import presentation.fsa.GraphElement;
import presentation.fsa.GraphLabel;
import presentation.fsa.Node;
import presentation.fsa.SelectionGroup;

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
	public static class CreateCommand extends UndoableActionCommand {

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
		

		public void setEdge(Edge edge) {
			this.edge = edge;
		}

		@Override
		protected UndoableEdit performEdit() {
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
			 
			// TODO create and UndoableEdit object and return
			return null;
		}
	}
	
	
	public static class MoveCommand extends UndoableActionCommand {

		GraphDrawingView context;
		SelectionGroup currentSelection = null;
		Point displacement;
		
		public MoveCommand(GraphDrawingView context) {
			super("move.command");
			this.context = context;
		}

		/**
		 * 
		 * @param context
		 * @param currentSelection
		 * @param displacement
		 */
		public MoveCommand(GraphDrawingView context, SelectionGroup currentSelection, Point displacement) {
			this.currentSelection = currentSelection;
			this.context = context;
			this.displacement = displacement;
		}

		@Override
		protected UndoableEdit performEdit() {
			if(currentSelection == null){
				context.setTool(GraphDrawingView.MOVE);
				return null;
			}else{
				// finalize movement of current selection in graph model
				context.getGraphModel().saveMovement(currentSelection);
				// TODO create an UndoableEdit object using
				// my instance variables and return it.
				
				currentSelection = null;
				// TODO return undoableEdit object
				return null;
			}
			
		}	
	}
	
	
	public static class TextCommand extends UndoableActionCommand {
		
		GraphDrawingView context;
		String text;
		GraphElement currentSelection = null;
		
		public TextCommand(GraphDrawingView context){
			super("text.command");
			this.context = context;
		}
		
		public TextCommand(GraphDrawingView context, GraphElement currentSelection, String text) {
			super("text.command");
			this.currentSelection = currentSelection;
			this.context = context;
			this.text = text;
		}
	
		public TextCommand(GraphDrawingView context, GraphElement currentSelection) {
			super("text.command");
			this.currentSelection = currentSelection;
			this.context = context;
		}

		@Override
		protected UndoableEdit performEdit() {
			if(currentSelection == null){
				context.setTool(GraphDrawingView.TEXT);
			}else{				
				if(currentSelection == null){
					// TODO create a new free label
					
				// KLUGE: instanceof is rotten style, fix this
				// FIXME Move to NodeCommands.LabelCommand
				}else if (currentSelection instanceof Node){				
					Node node = (Node)currentSelection;
					// if selection is a node				
					String text = JOptionPane.showInputDialog("Enter state name: ");
					if(text != null){
						context.getGraphModel().labelNode(node, text);						
					}

				// TODO if selection is an edge, open the edge-labelling dialog
				}else if(currentSelection instanceof Edge){
					Edge edge = (Edge)currentSelection;
					// FIXME: Don't show a new one; set an existing one visible
					EdgeLabellingDialog.showDialog(context, edge);
					// NOTE don't need an undoable edit since dialog has ok, apply and cancel buttons
					
				}else{
					// TODO on a free label
					GraphLabel label = (GraphLabel)currentSelection;
					String inputValue = JOptionPane.showInputDialog("Enter label text: ");
					if(inputValue != null){
						context.getGraphModel().addGraphLabel(label, text);
					}
				}
				context.repaint();
			}			
			// DEBUG
			//System.out.println("TextCommand.performEdit(): text = " + text);
			currentSelection = null;
			text = null;
			// TODO create an UndoableEdit object using
			// my instance variables and return it.
			return null;
		}
	}
	
	public static class ZoomInCommand extends ActionCommand {

		private GraphDrawingView context;
		
		public ZoomInCommand(GraphDrawingView context){
			super("zoomin.command");
			this.context = context;
		}
		@Override
		protected void handleExecute() {			
			// TODO set the tool in the *currently active* drawing view
			context.setTool(GraphDrawingView.ZOOM_IN);
		}
	}
	
	public static class ZoomOutCommand extends ActionCommand {

		private GraphDrawingView context;
		
		public ZoomOutCommand(GraphDrawingView context){
			super("zoomout.command");
			this.context = context;
		}
		@Override
		protected void handleExecute() {			
			// TODO set the tool in the *currently active* drawing view
			context.setTool(GraphDrawingView.ZOOM_OUT);
		}
	}
}
