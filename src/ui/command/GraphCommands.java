package ui.command;

import java.awt.Point;
import java.awt.geom.Point2D.Float;

import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

import main.Hub;

import org.pietschy.command.ActionCommand;
import org.pietschy.command.undo.UndoableActionCommand;

import presentation.fsa.Edge;
import presentation.fsa.EdgeLabellingDialog;
import presentation.fsa.GraphDrawingView;
import presentation.fsa.GraphElement;
import presentation.fsa.GraphLabel;
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
		public static final int SELF_LOOP = 3;
		
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
		
		public CreateCommand(GraphDrawingView context, int elementType, Edge edge, Point location){
			setContext(context, elementType, location);
			this.edge = edge;
		}	

		
		/**
		 * @param context2
		 * @param self_loop2
		 * @param n
		 */
		public CreateCommand(GraphDrawingView context, int elementType, Node n) {
			this.context = context;			
			source = n;
		}

		/**
		 * @param context
		 * @param elementType
		 * @param edge
		 * @param target
		 */
		public CreateCommand(GraphDrawingView context, int elementType, Edge edge, Node target) {
			this.context = context;
			this.elementType = elementType;
			this.edge = edge;
			this.target = target;
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
				context.getGraphModel().addNode(new Float(location.x, location.y));
				break;
			case NODE_AND_EDGE:				
				context.getGraphModel().finishEdgeAndAddNode(edge, new Float(location.x, location.y));							
				break;
			case EDGE:
				context.getGraphModel().finishEdge(edge, target);				
				break;
			case SELF_LOOP:
				context.getGraphModel().setSelfLoop(source, true);
				break;				
			default:
				 context.setTool(GraphDrawingView.CREATE);
			}		
			 
			// TODO create and UndoableEdit object and return
			return null;
		}
	}
	
	
	public static class MoveCommand extends UndoableActionCommand {

		GraphDrawingView context;
		SelectionGroup selection = null;		
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
			this.selection = currentSelection.copy();
			this.context = context;
			this.displacement = displacement;
		}

		@Override
		protected UndoableEdit performEdit() {
			if(selection == null){
				context.setTool(GraphDrawingView.MOVE);
				return null;
			}else{
				// finalize movement of current selection in graph model
				context.getGraphModel().saveMovement(context.getCurrentSelection());
				// TODO create an UndoableEdit object using displacement and 
				// copy of currentSelection.

				// TODO return undoableEdit object
				return null;
			}
			
		}	
	}
	
	
	public static class TextCommand extends UndoableActionCommand {
		
		GraphDrawingView context;
		String text;
		GraphElement element = null;
		
		public TextCommand(GraphDrawingView context){
			super("text.command");
			this.context = context;
		}
		
		public TextCommand(GraphDrawingView context, GraphElement currentSelection, String text) {
			super("text.command");
			this.element = currentSelection;
			this.context = context;
			this.text = text;
		}
	
		public TextCommand(GraphDrawingView context, GraphElement currentSelection) {
			super("text.command");
			this.element = currentSelection;
			this.context = context;
		}

		public void setElement(GraphElement element){
			this.element = element;
		}
		
		@Override
		protected UndoableEdit performEdit() {
			if(element == null){
				context.setTool(GraphDrawingView.TEXT);
			}else{				
				if(element == null){
					// TODO create a new free label
					
				// KLUGE: instanceof is rotten style, fix this
				// FIXME Move to NodeCommands.LabelCommand
				}else if (element instanceof Node){				
					Node node = (Node)element;
					// if selection is a node				
					String text = 
					//JOptionPane.showInputDialog("Enter state name: ");
					presentation.fsa.SingleLineNodeLabellingDialog.showAndGetLabel(node.getLabel().getLayout().getText(),
							new Point((int)node.getLayout().getLocation().x+
									Hub.getWorkspace().getDrawingBoardDisplacement().x,
									(int)node.getLayout().getLocation().y+
									Hub.getWorkspace().getDrawingBoardDisplacement().y));
					if(text != null){
						context.getGraphModel().labelNode(node, text);						
					}

				}else if(element instanceof Edge){
					Edge edge = (Edge)element;			
					EdgeLabellingDialog.showDialog(context, edge);					
					// TODO accumulate set of edits that were performed in the edge labelling dialog
				}else{
					// TODO on a free label
					GraphLabel label = (GraphLabel)element;
					String inputValue = JOptionPane.showInputDialog("Enter label text: ");
					if(inputValue != null){
						context.getGraphModel().addGraphLabel(label, text);
					}
				}
				context.repaint();
			}
			element = null;
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

	/**
	 * Represent a user issued command to delete an element of the graph.
	 * ??? What about deleting elements of a text label? 
	 * 
	 * @author helen bretzke
	 *
	 */
	public static class DeleteCommand extends UndoableActionCommand {
		
		private GraphElement element;	 // TODO decide on type, GraphElement composite type?
		private GraphDrawingView context;  // Does this need to be stored?
		
		/**
		 * Default constructor; handy for exporting this command for group setup.
		 *
		 */
		public DeleteCommand(){
			super("delete.command");
		}
		
		public DeleteCommand(GraphDrawingView context){
			this(null, context);
		}
		
		/**
		 * Creates a command that, when executed, will cut 
		 * <code>element</code> from the given context.
		 * 
		 * @param element
		 * @param context
		 */
		public DeleteCommand(GraphElement element, GraphDrawingView context) {
			this();
			this.element = element;
			this.context = context;
		}		

		public void setElement(GraphElement element){
			this.element = element;
		}
		
		@Override
		protected UndoableEdit performEdit() {
			// TODO return Undoable edit containing removed element and where it should be restored to
			// the view is not enough since view changes models; need to know the model...
			context.getGraphModel().delete(element);
			context.repaint();
			return null;
		}
		
	}

}
