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
	 * A command to set the current drawing mode to editing mode. 
	 * While in editing mode, user may select graph objects in the
	 * GraphDrawingView for deleting, copying, pasting and moving.
	 * 
	 * @author Helen Bretzke, Christian Silvano
	 */
	public static class SelectAction extends AbstractAction {

		private static String text = "Select";
		private static ImageIcon icon = new ImageIcon();

		public SelectAction()
		{
			super(text,icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/graphic_modify.gif")));
		}		

		public void actionPerformed(ActionEvent event)
		{			
			// TODO set the tool in the *currently active* drawing view
			ContextAdaptorHack.context.setTool(GraphDrawingView.SELECT);
			ContextAdaptorHack.context.setPreferredTool(GraphDrawingView.SELECT);
		}
	}


//	/**
//	* A command to set the current drawing mode to editing mode. 
//	* While in editing mode, user may select graph objects in the
//	* GraphDrawingView for deleting, copying, pasting and moving.
//	* 
//	* @author Helen Bretzke
//	*/
//	public static class SelectAction extends ActionCommand {

////	private GraphDrawingView context;

//	public SelectAction(){
//	super("select.command");
////	this.context = context;
//	}
//	@Override
//	protected void handleExecute() {			
//	// TODO set the tool in the *currently active* drawing view
//	ContextAdaptorHack.context.setTool(GraphDrawingView.SELECT);
//	ContextAdaptorHack.context.setPreferredTool(GraphDrawingView.SELECT);
//	}
//	}





	/**
	 * Creates nodes and edges in a GraphDrawingView.
	 *	 
	 * @author Helen Bretzke, Christian Silvano
	 */
	public static class CreateAction extends AbstractAction {

		private static String text = "Create";
		private static ImageIcon icon = new ImageIcon();

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
		public CreateAction(){		
			super(text,icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/graphic_create.gif")));
			elementType = UNKNOWN;
		}

		/**
		 * @param context
		 * @param elementType
		 * @param location
		 */
		public CreateAction(int elementType, Point location){
			super(text,icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/graphic_create.gif")));
			setContext(elementType, location);
		}	

		public CreateAction(int elementType, BezierEdge edge, Point location){
			super(text,icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/graphic_create.gif")));
			setContext(elementType, location);
			this.edge = edge;
		}	


		/**
		 * @param context
		 * @param elementType
		 * @param n
		 */
		public CreateAction(int elementType, CircleNode n) {
			super(text,icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/graphic_create.gif")));
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
		public CreateAction(int elementType, BezierEdge edge, CircleNode target) {
			super(text,icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/graphic_create.gif")));
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

		public void actionPerformed(ActionEvent event)
		{
			UndoableCreate action = new UndoableCreate(elementType,source,target,edge,location);
			//There is no "perform" operation, since the movement was done by the user
			action.redo();
			// notify the listeners
			CommandManager_new.getInstance().undoSupport.postEdit(action);
		}

		public void execute()
		{
			actionPerformed(null);
		}
	}
//	/**
//	* Creates nodes and edges in a GraphDrawingView.
//	*	 
//	* @author Helen Bretzke
//	*/
//	public static class CreateAction extends UndoableActionCommand {

//	}


	public static class MoveAction extends AbstractAction {

//		GraphDrawingView context;
		SelectionGroup selection = null;		
		Point displacement;
		private static String text = "Move";
		private static ImageIcon icon = new ImageIcon();

		public MoveAction()
		{
			super(text,icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/graphic_move.gif")));
		}		


		/**
		 * 
		 * @param context
		 * @param currentSelection
		 * @param displacement
		 */
		public MoveAction(SelectionGroup currentSelection, Point displacement) {
			super(text,icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/graphic_modify.gif")));
			this.selection = currentSelection.copy();
//			this.context = context;
			this.displacement = displacement;
		}

		public void actionPerformed(ActionEvent event)
		{
			if(selection == null){
				ContextAdaptorHack.context.setTool(GraphDrawingView.MOVE);
				ContextAdaptorHack.context.setPreferredTool(GraphDrawingView.MOVE);
			}
			if(displacement !=null)
			{
				UndoableMove action = new UndoableMove(selection, displacement);
				//There is no "perform" operation, since the movement was done by the user
				// notify the listeners
				CommandManager_new.getInstance().undoSupport.postEdit(action);
			}
		}	

		public void execute()
		{
			actionPerformed(null);
		}
	}



//	public static class MoveCommand extends UndoableActionCommand {

////	GraphDrawingView context;
//	SelectionGroup selection = null;		
//	Point displacement;

//	public MoveCommand() {
//	super("move.command");
////	this.context = context;
//	}

//	/**
//	* 
//	* @param context
//	* @param currentSelection
//	* @param displacement
//	*/
//	public MoveCommand(SelectionGroup currentSelection, Point displacement) {
//	this.selection = currentSelection.copy();
////	this.context = context;
//	this.displacement = displacement;
//	}

//	@Override
//	protected UndoableEdit performEdit() {
//	if(selection == null){
//	ContextAdaptorHack.context.setTool(GraphDrawingView.MOVE);
//	ContextAdaptorHack.context.setPreferredTool(GraphDrawingView.MOVE);
//	return null;
//	}else{
//	// finalize movement of current selection in graph model
//	//Christian - commitMovement removed
////	ContextAdaptorHack.context.getGraphModel().commitMovement(ContextAdaptorHack.context.getSelectedGroup());
//	// TODO create an UndoableEdit object using displacement and 
//	// copy of currentSelection and return undoableEdit object
//	return null;
//	}

//	}	
//	}


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
//		this(null, context);
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
	 * @author Lenko Grigorov, Christian Silvano
	 *
	 */
	public static class AlignAction extends AbstractAction {

		//TODO: redo all of this so there's an independent grid going

//		private GraphDrawingView context;

		private static String text = "Align nodes";
		private static ImageIcon icon = new ImageIcon();

		public AlignAction()
		{
			super(text,icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/graphic_align.gif")));
		}		


		public void actionPerformed(ActionEvent event)
		{
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


	private static class UndoableMove extends AbstractUndoableEdit{
		SelectionGroup selection = null;		
		Point displacement;

		SelectionGroup backup, group;
		GraphDrawingView graph;
		public UndoableMove(SelectionGroup g, Point d)
		{
			selection = g;
			displacement = d;
		}

		public void undo() throws CannotRedoException{
			Iterator<GraphElement> it = selection.children();
			while(it.hasNext())
			{
				GraphElement ge = it.next();
				ge.setLocation(new Point2D.Float(ge.getLocation().x - displacement.x, ge.getLocation().y - displacement.y));
			}
			ContextAdaptorHack.context.getGraphModel().commitLayoutModified();
		}

		public void redo() throws CannotRedoException{
			Iterator<GraphElement> it = selection.children();
			while(it.hasNext())
			{
				GraphElement ge = it.next();
				ge.setLocation(new Point2D.Float(ge.getLocation().x + displacement.x, ge.getLocation().y + displacement.y));
			}
			ContextAdaptorHack.context.getGraphModel().commitLayoutModified();
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
			return Hub.string("moveSelection");
		}

	}




	private static class UndoableCreate extends AbstractUndoableEdit{
		SelectionGroup selection = null;		
		GraphElement bkpNode, bkpEdge;
		private int elementType;
		private CircleNode source, target;
		private BezierEdge edge;
		private Point location;

		public UndoableCreate(int type, CircleNode s, CircleNode t, BezierEdge e, Point l)
		{
			elementType = type;
			source = s;
			target = t;
			edge = e;
			location = l;
		}

		public void undo() throws CannotRedoException{
			FSAState s;
			Automaton model = (Automaton)Hub.getWorkspace().getActiveModel();
			FSATransition t;
			switch(elementType){
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
//			ContextAdaptorHack.context.getGraphModel().commitLayoutModified();
		}

		public void redo() throws CannotRedoException{
			switch(elementType){
			case CreateAction.NODE:				
				if(bkpNode == null){
					bkpNode = ContextAdaptorHack.context.getGraphModel().createNode(new Float(location.x, location.y));
				}else{
					ContextAdaptorHack.context.getGraphModel().reCreateNode((CircleNode)bkpNode);
				}
				break;
			case CreateAction.NODE_AND_EDGE:				
				if(bkpNode == null & bkpEdge == null)
				{
					SelectionGroup nodeAndEdge = ContextAdaptorHack.context.getGraphModel().finishEdgeAndCreateTargetNode(edge, new Float(location.x, location.y));				
					Iterator<GraphElement> it = nodeAndEdge.children();
					while(it.hasNext())
					{
						GraphElement ge = it.next();
						if(ge instanceof CircleNode)
						{
							bkpNode = ge;
						}
						if(ge instanceof BezierEdge)
						{
							bkpEdge = ge;
						}
					}
				}else{
					ContextAdaptorHack.context.getGraphModel().reCreateNode((CircleNode)bkpNode);
					ContextAdaptorHack.context.getGraphModel().reCreateEdge((BezierEdge)bkpEdge);
				}

				break;
			case CreateAction.EDGE:
				bkpEdge = ContextAdaptorHack.context.getGraphModel().finishEdge(edge, target);				
				break;
			case CreateAction.SELF_LOOP:
				
				bkpEdge = ContextAdaptorHack.context.getGraphModel().createEdge(source, source);
				break;				
			default:
				ContextAdaptorHack.context.setTool(GraphDrawingView.CREATE);
			ContextAdaptorHack.context.setPreferredTool(GraphDrawingView.CREATE);
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
			switch(elementType){
			case CreateAction.NODE:	
				return Hub.string("createNode");
			case CreateAction.NODE_AND_EDGE:				
				return Hub.string("nodeAndEdge");
			case CreateAction.EDGE:
				return Hub.string("createEdge");
			case CreateAction.SELF_LOOP:
				return Hub.string("createSelfLoop");
			default:
				return Hub.string("createElement");
			}	
		}

	}




}
