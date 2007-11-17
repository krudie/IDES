package ui.command;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoableEdit;

import main.Hub;
import model.fsa.FSAState;
import model.fsa.FSATransition;
import model.fsa.ver2_1.Automaton;

import org.pietschy.command.ActionCommand;
import org.pietschy.command.undo.UndoableActionCommand;

import presentation.fsa.BezierEdge;
import presentation.fsa.ContextAdaptorHack;
import presentation.fsa.Edge;
import presentation.fsa.EdgeLabellingDialog;
import presentation.fsa.FSAGraph;
import presentation.fsa.FSAToolset;
import presentation.fsa.GraphDrawingView;
import presentation.fsa.GraphElement;
import presentation.fsa.GraphLabel;
import presentation.fsa.CircleNode;
import presentation.fsa.Node;
import presentation.fsa.NodeLabellingDialog;
import presentation.fsa.SelectionGroup;
import ui.tools.CreationTool;

public class GraphCommands {

	/**
	 * A command to set the current drawing mode to editing mode. While in
	 * editing mode, user may select graph objects in the GraphDrawingView for
	 * deleting, copying, pasting and moving.
	 * 
	 * @author Helen Bretzke
	 */
	public static class SelectTool extends AbstractAction {

		//The label that can be used to describe this action
		private static String text = Hub.string("select");
		//An icon that can be used to describe this action
		private static ImageIcon icon = new ImageIcon();

		//Default constructor
		public SelectTool() {
			super(text, icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(
					Hub.getResource("images/icons/graphic_modify.gif")));
		}

		//Switches the tool to Selecting Tool.
		public void actionPerformed(ActionEvent event) {
			// TODO set the tool in the *currently active* drawing view
			ContextAdaptorHack.context.setTool(GraphDrawingView.SELECT);
			ContextAdaptorHack.context
			.setPreferredTool(GraphDrawingView.SELECT);
		}
	}

	/**
	 * A command to set the current drawing mode to creation mode. While in
	 * creating mode, user may create new objects in the GraphDrawingView.
	 * 
	 * @author Helen Bretzke
	 */
	public static class CreateTool extends AbstractAction {
		//The label that can be used to describe this action
		private static String text = Hub.string("create");
		//An icon that can be used to describe this action
		private static ImageIcon icon = new ImageIcon();


		// Default constructor.	 
		public CreateTool() {
			super(text, icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(
					Hub.getResource("images/icons/graphic_create.gif")));
		}

		//Switches the tool to Creating tool
		public void actionPerformed(ActionEvent event) {
			ContextAdaptorHack.context.setTool(GraphDrawingView.CREATE);
			ContextAdaptorHack.context
			.setPreferredTool(GraphDrawingView.CREATE);
		}
	}


	/**
	 * A command to set the current drawing mode to creating mode. While in
	 * creating mode, user may create new objects in the GraphDrawingView.
	 * 
	 * @author Helen Bretzke
	 */
	public static class MoveTool extends AbstractAction {
		private static String text = Hub.string("move");
		private static ImageIcon icon = new ImageIcon();

		public MoveTool() {
			super(text, icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(
					Hub.getResource("images/icons/graphic_move.gif")));
		}

		//Switches the tool to Moving Tool
		public void actionPerformed(ActionEvent event) {
			ContextAdaptorHack.context.setTool(GraphDrawingView.MOVE);
			ContextAdaptorHack.context
			.setPreferredTool(GraphDrawingView.MOVE);
		}
	}

	/**
	 * This action is executed every time a graph element is moved in a Graph.
	 * The objective is to make undo/redo actions possible. 
	 * According to the chosen design for undoable actions, there are two static
	 * classes involved on any UndoableAction, one to construct the UndoableAction(s),
	 * perform it(or them), and inform the CommandManager
	 * about this(these) action. The other class is the UndoableAction itself.
	 *
	 * This class constructs an UndoableMovement, informing the GraphElement of interest and
	 * its displacement over the Graph. 
	 * @author Christian Silvano
	 *
	 */
	public static class MoveAction extends AbstractAction {
		//The set of elements that are being moved.
		SelectionGroup selection = null;
		//The displacement of the selection, it is a vector where the direction
		//of the displacement can be inferred by the signals of its coordinates.
		Point displacement;

		/**
		 * 
		 * @param currentSelection
		 * @param displacement
		 */
		public MoveAction(SelectionGroup currentSelection, Point displacement) {
			this.selection = currentSelection.copy();
			this.displacement = displacement;
		}

		//Creates an UndoableMove (an object capable of undoing/redoing the movement)
		//and informs CommandManager about a new UndoableAction.
		public void actionPerformed(ActionEvent event) {
			if (displacement != null) {
				UndoableMove action = new UndoableMove(selection, displacement);
				// There is no "perform" operation like most of the UndoableActions, 
				// the reason is that the movement is made by a user (e.g.: by dragging a node in a FSA).

				// Notify the listeners
				CommandManager_new.getInstance().undoSupport.postEdit(action);
			}
		}

		public void execute() {
			actionPerformed(null);
		}
	}

	public static class TextCommand extends UndoableActionCommand {

		// GraphDrawingView context;
		String text;

		GraphElement element = null;

		Point2D.Float location = null;

		public TextCommand() {
			super("text.command");
		}

		public TextCommand(GraphElement currentSelection, String text) {
			super("text.command");
			this.element = currentSelection;
			// this.context = context;
			this.text = text;
		}

		public TextCommand(GraphElement currentSelection) {
			super("text.command");
			this.element = currentSelection;
			// this.context = context;
		}

		/**
		 * @param context
		 * @param location
		 */
		public TextCommand(Point location) {
			// this.context = context;
			this.location = new Point2D.Float(location.x, location.y);
		}

		public void setElement(GraphElement element) {
			this.element = element;
		}

		@Override
		protected UndoableEdit performEdit() {
			if (element == null) {
				// create a new free label
				// TODO uncomment the following statement when finished
				// implementing
				// saving and loading free labels to file.
				/*
				 * presentation.fsa.SingleLineFreeLabellingDialog.showAndLabel(
				 * context.getGraphModel(), location);
				 */
			} else {
				// KLUGE: instanceof is rotten style, fix this
				if (element instanceof CircleNode) {
					Node node = (Node) element;
					// if selection is a node
					presentation.fsa.SingleLineNodeLabellingDialog
					.showAndLabel(ContextAdaptorHack.context
							.getGraphModel(), node);
				} else if (element instanceof BezierEdge) {
					BezierEdge edge = (BezierEdge) element;
					EdgeLabellingDialog.showDialog(ContextAdaptorHack.context,
							edge);
					// TODO accumulate set of edits that were performed in the
					// edge
					// labelling dialog
				} else if (element instanceof GraphLabel
						&& element.getParent() instanceof Edge) {
					Edge edge = (Edge) element.getParent();
					EdgeLabellingDialog.showDialog(ContextAdaptorHack.context,
							edge);
				} else {
					// TODO uncomment the following statement when finished
					// implementing
					// saving and loading free labels to file.
					/*
					 * presentation.fsa.SingleLineFreeLabellingDialog.showAndLabel(
					 * context.getGraphModel(), (GraphLabel)element);
					 */
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

	public static class ZoomInAction extends AbstractAction {

		// private GraphDrawingView context;

		public ZoomInAction() {
			super("zoomin.command");
			// this.context = context;
		}

		public void actionPerformed(ActionEvent evt) {
			// TODO set the tool in the *currently active* drawing view
			ContextAdaptorHack.context.setTool(GraphDrawingView.ZOOM_IN);
		}
	}

	public static class ZoomOutAction extends AbstractAction {

		// private GraphDrawingView context;

		public ZoomOutAction() {
			super("zoomout.command");
			// this.context = context;
		}

		public void actionPerformed(ActionEvent evt) {
			// TODO set the tool in the *currently active* drawing view
			ContextAdaptorHack.context.setTool(GraphDrawingView.ZOOM_OUT);
		}
	}

	/**
	 * Emulates "snap to grid".
	 * 
	 * @author Lenko Grigorov, Christian Silvano
	 * 
	 */
	public static class AlignTool extends AbstractAction {

		// TODO: redo all of this so there's an independent grid going

		// private GraphDrawingView context;

		private static String text = "Align nodes";

		private static ImageIcon icon = new ImageIcon();

		public AlignTool() {
			super(text, icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(
					Hub.getResource("images/icons/graphic_align.gif")));
		}

		public void actionPerformed(ActionEvent event) {
			if (Hub.getWorkspace().getActiveModel() == null)
				return;
			Iterator i;
			if (ContextAdaptorHack.context.getSelectedGroup().size() > 0)
				i = ContextAdaptorHack.context.getSelectedGroup().children();
			else
				i = ContextAdaptorHack.context.getGraphModel().children();
			while (i.hasNext()) {
				GraphElement ge = (GraphElement) i.next();
				ge.getLayout().snapToGrid();
				ge.refresh();
			}

			ContextAdaptorHack.context.getGraphModel().setNeedsRefresh(true);
			Hub.getWorkspace().fireRepaintRequired();
		}
	}


	/**
	 * Undo/Redo a movement of a SelectionGroup (collection of graph elements)
	 * over a Graph.
	 * In order to do that, the SelectionGroup and a vector representing the displacement
	 * are sent in the class constructor. 
	 * @author Christian Silvano
	 *
	 */
	private static class UndoableMove extends AbstractUndoableEdit {

		//A collection of graph elements
		SelectionGroup selection = null;

		//A vector meaning the displacement of the selection
		Point displacement;

		/**
		 * Default constructor
		 * @param g collection of graph elements
		 * @param d displacement of the elements
		 */
		public UndoableMove(SelectionGroup g, Point d) {
			selection = g;
			displacement = d;
		}

		/**
		 * Undoes a movement by applying a vector opposite to <code>displacement</code>
		 * over <code>collection</code>
		 */
		public void undo() throws CannotRedoException {
			Iterator<GraphElement> it = selection.children();

			//Applies a displacement over every element under <code>selection</selection>
			while (it.hasNext()) {
				GraphElement ge = it.next();
				ge.setLocation(new Point2D.Float(ge.getLocation().x
						- displacement.x, ge.getLocation().y - displacement.y));
			}
			//TODO Stop using ContextAdaptorHack!!
			ContextAdaptorHack.context.getGraphModel().commitLayoutModified();
		}

		/**
		 * Redoes a movement by applying a <code>displacement</code>
		 * over <code>collection</code>
		 */
		public void redo() throws CannotRedoException {
			Iterator<GraphElement> it = selection.children();

			//Applies a displacement over every element under <code>selection</selection>
			while (it.hasNext()) {
				GraphElement ge = it.next();
				ge.setLocation(new Point2D.Float(ge.getLocation().x
						+ displacement.x, ge.getLocation().y + displacement.y));
			}
			//TODO Stop using ContextAdaptorHack!!
			ContextAdaptorHack.context.getGraphModel().commitLayoutModified();
		}

		public boolean canUndo() {
			return true;
		}

		public boolean canRedo() {
			return true;
		}

		/**
		 * Returns the name that should be displayed besides the Undo/Redo menu items, so the user knows
		 * which action will be undone/redone.
		 */
		public String getPresentationName() {
			return Hub.string("moveSelection");
		}

	}

	/**
	 * This action is executed every time graph elements are created in a Graph.
	 * The objective is to make undo/redo actions possible. 
	 * According to the chosen design for undoable actions, there are two static
	 * classes involved on any UndoableAction, one to construct the undoable actions and
	 * perform them and inform the CommandManager about the action. 
	 * Other that is the UndoableAction itself.
	 * 
	 * The class bellow creates an UndoableCreate object and informs the CommandManager
	 * about its existence.
	 * 
	 * @author Helen Bretzke, Christian Silvano
	 */
	public static class CreateAction extends AbstractAction {
		// private GraphDrawingView context;
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
		 * @param context
		 * @param elementType
		 * @param location
		 */
		public CreateAction(int elementType, Point location) {
			setContext(elementType, location);
		}

		public CreateAction(int elementType, BezierEdge edge, Point location) {
			setContext(elementType, location);
			this.edge = edge;
		}

		/**
		 * @param context
		 * @param elementType
		 * @param n
		 */
		public CreateAction(int elementType, CircleNode n) {
			// this.context = context;
			this.elementType = elementType;
			source = n;
		}

		/**
		 * @param context
		 * @param elementType
		 * @param edge
		 * @param target
		 */
		public CreateAction(int elementType, BezierEdge edge, CircleNode target) {
			// this.context = context;
			this.elementType = elementType;
			this.edge = edge;
			this.target = target;
		}

		public void setContext(int elementType, Point location) {
			// this.context = context;
			this.elementType = elementType;
			this.location = location;
		}

		public void setSourceNode(CircleNode s) {
			source = s;
		}

		public void setTargetNode(CircleNode t) {
			target = t;
			t.setHighlighted(true);
		}

		public void setEdge(BezierEdge edge) {
			this.edge = edge;
		}

		public void actionPerformed(ActionEvent event) {
			UndoableCreate action = new UndoableCreate(elementType, source,
					target, edge, location);
			// There is no "perform" operation, since the movement was done by
			// the user
			action.redo();
			// notify the listeners
			CommandManager_new.getInstance().undoSupport.postEdit(action);
		}

		public void execute() {
			actionPerformed(null);
		}
	}

	/**
	 * This class is responsible for undoing/redoing the creation of elements
	 * on a graph.
	 * @author Christian Silvano
	 *
	 */
	private static class UndoableCreate extends AbstractUndoableEdit {
		SelectionGroup selection = null;
		GraphElement bkpNode, bkpEdge;
		private int elementType;
		private CircleNode source, target;
		private BezierEdge edge;
		private Point location;

		public UndoableCreate(int type, CircleNode s, CircleNode t,
				BezierEdge e, Point l) {
			elementType = type;
			source = s;
			target = t;
			edge = e;
			location = l;
		}

		public void undo() throws CannotRedoException {
			FSAState s;
			Automaton model = (Automaton) Hub.getWorkspace().getActiveModel();
			FSATransition t;
			switch (elementType) {
			case CreateAction.NODE:
				ContextAdaptorHack.context.getGraphModel().delete(bkpNode);
				break;
			case CreateAction.NODE_AND_EDGE:
				ContextAdaptorHack.context.getGraphModel().delete(bkpEdge);
				ContextAdaptorHack.context.getGraphModel().delete(bkpNode);
				break;
			case CreateAction.EDGE:
				ContextAdaptorHack.context.getGraphModel().delete(bkpEdge);
				break;
			case CreateAction.SELF_LOOP:
				ContextAdaptorHack.context.getGraphModel().delete(bkpEdge);
				break;
			}
			// ContextAdaptorHack.context.getGraphModel().commitLayoutModified();
		}

		public void redo() throws CannotRedoException {
			switch (elementType) {
			case CreateAction.NODE:
				if (bkpNode == null) {
					bkpNode = ContextAdaptorHack.context.getGraphModel()
					.createNode(new Float(location.x, location.y));
				} else {
					ContextAdaptorHack.context.getGraphModel().reCreateNode(
							(CircleNode) bkpNode);
				}
				break;
			case CreateAction.NODE_AND_EDGE:
				if (bkpNode == null & bkpEdge == null) {
					SelectionGroup nodeAndEdge = ContextAdaptorHack.context
					.getGraphModel().finishEdgeAndCreateTargetNode(
							edge, new Float(location.x, location.y));
					Iterator<GraphElement> it = nodeAndEdge.children();
					while (it.hasNext()) {
						GraphElement ge = it.next();
						if (ge instanceof CircleNode) {
							bkpNode = ge;
						}
						if (ge instanceof BezierEdge) {
							bkpEdge = ge;
						}
					}
				} else {
					ContextAdaptorHack.context.getGraphModel().reCreateNode(
							(CircleNode) bkpNode);
					ContextAdaptorHack.context.getGraphModel().reCreateEdge(
							(BezierEdge) bkpEdge);
				}

				break;
			case CreateAction.EDGE:
				bkpEdge = ContextAdaptorHack.context.getGraphModel()
				.finishEdge(edge, target);
				break;
			case CreateAction.SELF_LOOP:

				bkpEdge = ContextAdaptorHack.context.getGraphModel()
				.createEdge(source, source);
				break;
			}
		}

		public boolean canUndo() {
			return true;
		}

		public boolean canRedo() {
			return true;
		}

		public String getPresentationName() {
			switch (elementType) {
			case CreateAction.NODE:
				return Hub.string("createNode");
			case CreateAction.NODE_AND_EDGE:
				return Hub.string("nodeAndEdge");
			case CreateAction.EDGE:
				return Hub.string("createEdge");
			case CreateAction.SELF_LOOP:
				return Hub.string("createSelfLoop");
			}
			return Hub.string("createElement");
		}

	}

	/**
	 * Represent a user issued command to delete an element of the graph.
	 * What about deleting elements of a text label?
	 * 
	 * @author helen bretzke
	 * 
	 */
	public static class DeleteAction extends AbstractAction{
		private static String text = "Delete";
		private static ImageIcon icon = new ImageIcon();
		private GraphDrawingView context;
		private GraphElement element; // TODO decide on type, GraphElement
		// composite type?
		// private GraphDrawingView context; // Does this need to be stored?


		/**
		 * Default constructor; handy for exporting this command for group
		 * setup.
		 * 
		 */
		public DeleteAction() {
			super(text, icon);
		}

		/**
		 * Default constructor; handy for exporting this command for group
		 * setup.
		 * 
		 */
		public DeleteAction(GraphDrawingView c) {
			super(text, icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(
					Hub.getResource("images/icons/edit_delete.gif")));
			context = c;
		}

		// public DeleteCommand(GraphDrawingView context){
		// this(null, context);
		// }

		/**
		 * Creates a command that, when executed, will cut <code>element</code>
		 * from the given context.
		 * 
		 * @param element
		 * @param context
		 */
		public DeleteAction(GraphDrawingView c, GraphElement element) {
			element = element;
			context = c;
			// this.context = context;
		}

		public void setElement(GraphElement element) {
			this.element = element;

		}

		public void setContext(GraphDrawingView g) {
			this.context = g;
		}

		public void actionPerformed(ActionEvent evt) {
			if(((CreationTool)context.getTools()[GraphDrawingView.CREATE]).isDrawingEdge())
				((CreationTool)context.getTools()[GraphDrawingView.CREATE]).abortEdge();
			UndoableDelete action = new UndoableDelete(context,context.getSelectedGroup());
			//perform the action
			action.redo();
			// notify the listeners
			CommandManager_new.getInstance().undoSupport.postEdit(action);
			context.setAvoidNextDraw(false);
			context.repaint();
			context.setAvoidNextDraw(false);
			context.setTool(context.getPreferredTool());

		}

	}
//	TODO Rewrite undoable Delete. It is very buggy. Sorry Lenko ;-)
	//If one needs any help with it, please talk to Christian.
	private static class UndoableDelete extends AbstractUndoableEdit{
		SelectionGroup group;
		GraphDrawingView graph;
		int elementType;
		static final int EDGE = 0, NODE = 1, SELECTION=2;
		public UndoableDelete(GraphDrawingView w, SelectionGroup g)
		{
			graph = w;
			group = g;
			if(group.size() == 1)
			{
				//Add the children of the selected element to the selection group, so 
				//they can be backed up case the user wants to undo the deletion
				Iterator<GraphElement> it = (group.children().next()).children();
				while(it.hasNext())
				{
					GraphElement ge = it.next();
					if(!(group.contains(ge)))
					{
						//Backing up the children of the selected element
						group.insert(ge);
					}
				}
			}
		}

		public void undo() throws CannotRedoException{
			//Make the FSAGraph recreate the children of the selection one by one:
			if(group!=null & graph !=null)
			{
				FSAGraph fsagraph = graph.getGraphModel();
				//Recreate all the nodes:
				Iterator<GraphElement> itNodes = group.children();
				while(itNodes.hasNext())
				{
					GraphElement ge=itNodes.next();
					if(ge instanceof CircleNode)
					{
						fsagraph.reCreateNode((CircleNode)ge);
					}
				}
				//Recreate all the edges:
				Iterator<GraphElement> itEdges = group.children();
				while(itEdges.hasNext())
				{
					GraphElement ge= itEdges.next();
					if(ge instanceof BezierEdge)
					{
						fsagraph.reCreateEdge((BezierEdge)ge);
					}
				}
			}
		}


		public void redo() throws CannotRedoException{
			//Make the FSAGraph delete the children of the selection one by one:
			if(group!=null & graph !=null)
			{
				FSAGraph fsagraph = graph.getGraphModel();
				for(Iterator i=group.children();i.hasNext();)
				{
					GraphElement ge=(GraphElement)i.next();
					fsagraph.delete(ge);
				}
			}
		}

		public boolean canUndo()
		{
			return true;
		}

		public boolean canRedo()
		{
			return true;
		}

		public String getPresentationName()
		{
			if(group.size() == 1)
			{GraphElement ge = group.children().next();
			if(ge instanceof BezierEdge)
			{
				return Hub.string("deleteEdge");
			}
			if(ge instanceof CircleNode)
			{
				return Hub.string("deleteNode");
			}
			}
			else{
				return Hub.string("deleteSelection");
			}
			return null;
		}
	}

}



