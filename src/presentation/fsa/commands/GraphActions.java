package presentation.fsa.commands;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import main.Hub;
import model.fsa.FSAEvent;
import model.fsa.FSAState;
import model.fsa.FSATransition;
import model.fsa.ver2_1.Automaton;

import presentation.LayoutShell;
import presentation.fsa.BezierEdge;
import presentation.fsa.BezierLayout;
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
import presentation.fsa.tools.CreationTool;
import services.undo.UndoManager;

public class GraphActions {

	
	public static class CreateEventAction extends AbstractGraphAction
	{
		protected String eventName;
		protected boolean controllable;
		protected boolean observable;
		protected FSAGraph graph;
		protected FSAEvent[] eventBuffer;

		public CreateEventAction(FSAGraph graph, String eventName, boolean controllable, boolean observable)
		{
			this(graph,eventName,controllable,observable,null);
		}
		
		public CreateEventAction(FSAGraph graph, String eventName, boolean controllable, boolean observable, FSAEvent[] eventBuffer)
		{
			this(null,graph,eventName,controllable,observable,eventBuffer);
		}
		
		public CreateEventAction(CompoundEdit parentEdit, FSAGraph graph, String eventName, boolean controllable, boolean observable)
		{
			this(parentEdit,graph,eventName,controllable,observable,null);			
		}
		
		public CreateEventAction(CompoundEdit parentEdit, FSAGraph graph, String eventName, boolean controllable, boolean observable, FSAEvent[] eventBuffer)
		{
			this.parentEdit=parentEdit;
			this.eventName=eventName;
			this.controllable=controllable;
			this.observable=observable;
			this.graph=graph;
			this.eventBuffer=eventBuffer;
		}
		
		public void actionPerformed(ActionEvent event)
		{
			if (graph != null) {
				GraphUndoableEdits.UndoableCreateEvent action =
					new GraphUndoableEdits.UndoableCreateEvent(graph,eventName,controllable,observable);
				action.redo();
				if(eventBuffer!=null&&eventBuffer.length>0)
				{
					eventBuffer[0]=action.getEvent();
				}
				postEdit(action);
			}
		}
	}
	
	public static class RemoveEventAction extends AbstractGraphAction
	{
		protected FSAEvent event;
		protected FSAGraph graph;
		
		public RemoveEventAction(FSAGraph graph, FSAEvent event)
		{
			this(null,graph,event);
		}
		
		public RemoveEventAction(CompoundEdit parentEdit, FSAGraph graph, FSAEvent event)
		{
			this.parentEdit=parentEdit;
			this.event=event;
			this.graph=graph;
		}
		
		public void actionPerformed(ActionEvent event)
		{
			if (graph != null) {
				CompoundEdit allEdits=new CompoundEdit();
				Set<Edge> edgesToRemove=new HashSet<Edge>();
				for(Edge e:graph.getEdges())
				{
					Vector<FSAEvent> eventsToKeep=new Vector<FSAEvent>();
					for(Iterator<FSATransition> i=e.getTransitions();i.hasNext();)
					{
						FSAEvent te=i.next().getEvent();
						if(te!=this.event)
						{
							eventsToKeep.add(te);
						}
					}
					if(e.transitionCount()>0&&eventsToKeep.size()==0)
					{
						edgesToRemove.add(e);
					}
					new EdgeActions.LabelAction(allEdits,graph,e,eventsToKeep).execute();
				}
				for(Edge e:edgesToRemove)
				{
					new GraphActions.DeleteElementAction(allEdits,graph,e).execute();
				}
				GraphUndoableEdits.UndoableRemoveEvent action =
					new GraphUndoableEdits.UndoableRemoveEvent(graph,this.event);
				action.redo();
				allEdits.addEdit(action);
				allEdits.end();
				postEdit(allEdits);
			}
		}
	}

	public static class ModifyEventAction extends AbstractGraphAction
	{
		protected FSAEvent event;
		protected String eventName;
		protected boolean controllable;
		protected boolean observable;
		protected FSAGraph graph;
		
		public ModifyEventAction(FSAGraph graph, FSAEvent event, String eventName, boolean controllable, boolean observable)
		{
			this(null,graph,event,eventName,controllable,observable);
		}
		
		public ModifyEventAction(CompoundEdit parentEdit, FSAGraph graph, FSAEvent event, String eventName, boolean controllable, boolean observable)
		{
			this.parentEdit=parentEdit;
			this.event=event;
			this.eventName=eventName;
			this.controllable=controllable;
			this.observable=observable;
			this.graph=graph;
		}
		
		public void actionPerformed(ActionEvent event)
		{
			if (graph != null) {
				GraphUndoableEdits.UndoableModifyEvent action =
					new GraphUndoableEdits.UndoableModifyEvent(graph,this.event,eventName,controllable,observable);
				action.redo();
				postEdit(action);
			}
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
				GraphUndoableEdits.UndoableMove action = new GraphUndoableEdits.UndoableMove(selection, displacement);
				// There is no "perform" operation like most of the UndoableActions, 
				// the reason is that the movement is made by a user (e.g.: by dragging a node in a FSA).

				UndoManager.addEdit(action);
			}
		}

		public void execute() {
			actionPerformed(null);
		}
	}

	public static class LabelAction extends AbstractGraphAction
	{
		protected String text;
		protected GraphElement element;
		protected FSAGraph graph;
		
		public LabelAction(FSAGraph graph, GraphLabel element, String text)
		{
			this(null,graph,element,text);
		}
		
		public LabelAction(CompoundEdit parentEdit, FSAGraph graph, GraphLabel element, String text)
		{
			this.parentEdit=parentEdit;
			this.element=element;
			this.text=text;
			if(this.text==null)
			{
				this.text="";
			}
			this.graph=graph;
		}
		
		public LabelAction(FSAGraph graph, Node element, String text)
		{
			this(null,graph,element,text);
		}
		
		public LabelAction(CompoundEdit parentEdit, FSAGraph graph, Node element, String text)
		{
			this.parentEdit=parentEdit;
			this.element=element;
			this.text=text;
			if(this.text==null)
			{
				this.text="";
			}
			this.graph=graph;
		}
		
		public void actionPerformed(ActionEvent event)
		{
			if (element != null) {
				GraphUndoableEdits.UndoableLabel action = new GraphUndoableEdits.UndoableLabel(graph,element,text);
				action.redo();
				postEdit(action);
			}

		}
	}

	/**
	 * Emulates "snap to grid".
	 * 
	 * @author Lenko Grigorov, Christian Silvano
	 * 
	 */
	public static class AlignToolAction extends AbstractAction {

		// TODO: redo all of this so there's an independent grid going

		private static ImageIcon icon = new ImageIcon();

		public AlignToolAction() {
			super(Hub.string("comAlign"), icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(
					Hub.getResource("images/icons/graphic_align.gif")));
			putValue(SHORT_DESCRIPTION, Hub.string("comHintAlign"));
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
	public static class CreateNodeAction extends AbstractGraphAction
	{
		protected Point2D.Float location;
		protected FSAGraph graph;
		protected Node[] nodeBuffer;
		
		public CreateNodeAction(FSAGraph graph, Point2D.Float location)
		{
			this(graph,location,null);
		}

		public CreateNodeAction(FSAGraph graph, Point2D.Float location, Node[] nodeBuffer)
		{
			this(null,graph,location,nodeBuffer);
		}
		
		public CreateNodeAction(CompoundEdit parentEdit, FSAGraph graph, Point2D.Float location)
		{
			this(parentEdit,graph,location,null);
		}

		public CreateNodeAction(CompoundEdit parentEdit, FSAGraph graph, Point2D.Float location, Node[] nodeBuffer)
		{
			this.parentEdit=parentEdit;
			this.location=location;
			this.graph=graph;
			this.nodeBuffer=nodeBuffer;
		}
		
		public void actionPerformed(ActionEvent event)
		{
			if (graph != null) {
				GraphUndoableEdits.UndoableCreateNode edit=new GraphUndoableEdits.UndoableCreateNode(graph,location);
				edit.redo();
				if(nodeBuffer!=null&&nodeBuffer.length>0)
				{
					nodeBuffer[0]=edit.getNode();
				}
				postEdit(edit);
			}
		}
	}

	public static class CreateEdgeAction extends AbstractGraphAction
	{
		protected Node source;
		protected Node target;
		protected FSAGraph graph;
		protected Edge[] edgeBuffer;
		
		public CreateEdgeAction(FSAGraph graph, Node source, Node target)
		{
			this(graph,source,target,null);
		}
		
		public CreateEdgeAction(FSAGraph graph, Node source, Node target, Edge[] edgeBuffer)
		{
			this(null,graph,source,target,edgeBuffer);
		}
		
		public CreateEdgeAction(CompoundEdit parentEdit, FSAGraph graph, Node source, Node target)
		{
			this(parentEdit,graph,source,target,null);
		}
		
		public CreateEdgeAction(CompoundEdit parentEdit, FSAGraph graph, Node source, Node target, Edge[] edgeBuffer)
		{
			this.parentEdit=parentEdit;
			this.source=source;
			this.target=target;
			this.graph=graph;
			this.edgeBuffer=edgeBuffer;
		}
		
		public void actionPerformed(ActionEvent event)
		{
			if (graph != null) {
				CompoundEdit allEdits=new CompoundEdit();
				for(Edge e:graph.getEdgesBetween(source,target))
				{
					if(e instanceof BezierEdge)
					{
						allEdits.addEdit(new GraphUndoableEdits.UndoableModifyEdge(graph,e,((BezierLayout)e.getLayout()).clone()));
					}
				}
				GraphUndoableEdits.UndoableCreateEdge edit=new GraphUndoableEdits.UndoableCreateEdge(graph,source,target);
				edit.redo();
				allEdits.addEdit(edit);
				allEdits.end();
				postEdit(allEdits);
				if(edgeBuffer!=null&&edgeBuffer.length>0)
				{
					edgeBuffer[0]=edit.getEdge();
				}
			}
		}
	}
	
	public static class DeleteElementAction extends AbstractGraphAction
	{
		protected GraphElement element;
		protected FSAGraph graph;
		
		public DeleteElementAction(FSAGraph graph, GraphElement element)
		{
			this(null,graph,element);
		}
		
		public DeleteElementAction(CompoundEdit parentEdit, FSAGraph graph, GraphElement element)
		{
			this.parentEdit=parentEdit;
			this.element=element;
			this.graph=graph;
		}
		
		public void actionPerformed(ActionEvent event)
		{
			if (graph != null) {
				CompoundEdit allEdits=new CompoundEdit();
				if(element instanceof Edge)
				{
					UndoableEdit edit=new GraphUndoableEdits.UndoableDeleteEdge(graph,(Edge)element);
					edit.redo();
					allEdits.addEdit(edit);
				}
				else if(element instanceof Node)
				{
					Set<Edge> edgesToRemove=new HashSet<Edge>();
					for(Iterator<GraphElement> i=element.children();i.hasNext();)
					{
						GraphElement child=i.next();
						if(child instanceof Edge)
						{
							edgesToRemove.add((Edge)child);
						}
					}
					for(Edge edge:edgesToRemove)
					{
						UndoableEdit edit=new GraphUndoableEdits.UndoableDeleteEdge(graph,edge);
						edit.redo();
						allEdits.addEdit(edit);
					}
					UndoableEdit edit=new GraphUndoableEdits.UndoableDeleteNode(graph,(Node)element);
					edit.redo();
					allEdits.addEdit(edit);
				}
				allEdits.end();
				postEdit(allEdits);
			}
		}
	}

	public static class UniformNodesAction extends AbstractAction {
		
		protected FSAGraph graph;
		
		public UniformNodesAction(FSAGraph graph) {
			super(Hub.string("comUniformNodeSize"));
			putValue(SHORT_DESCRIPTION, Hub.string("comHintUniformNodeSize"));
			this.graph=graph;
		}
		
		public void actionPerformed(ActionEvent e) {
			graph.setUseUniformRadius(!graph.isUseUniformRadius());
		}
	}
}



