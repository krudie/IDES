package presentation.fsa.actions;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import main.Hub;
import model.fsa.FSAEvent;
import model.fsa.FSAModel;
import model.fsa.FSATransition;
import presentation.fsa.BezierEdge;
import presentation.fsa.BezierLayout;
import presentation.fsa.Edge;
import presentation.fsa.FSAGraph;
import presentation.fsa.GraphElement;
import presentation.fsa.GraphLabel;
import presentation.fsa.InitialArrow;
import presentation.fsa.Node;
import presentation.fsa.SelectionGroup;
import services.undo.UndoManager;

public class GraphActions
{

	public static class CreateEventAction extends AbstractGraphAction
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -6892165635585940404L;

		protected String eventName;

		protected boolean controllable;

		protected boolean observable;

		protected FSAGraph graph;

		protected FSAEvent[] eventBuffer;

		public CreateEventAction(FSAGraph graph, String eventName,
				boolean controllable, boolean observable)
		{
			this(graph, eventName, controllable, observable, null);
		}

		public CreateEventAction(FSAGraph graph, String eventName,
				boolean controllable, boolean observable, FSAEvent[] eventBuffer)
		{
			this(null, graph, eventName, controllable, observable, eventBuffer);
		}

		public CreateEventAction(CompoundEdit parentEdit, FSAGraph graph,
				String eventName, boolean controllable, boolean observable)
		{
			this(parentEdit, graph, eventName, controllable, observable, null);
		}

		public CreateEventAction(CompoundEdit parentEdit, FSAGraph graph,
				String eventName, boolean controllable, boolean observable,
				FSAEvent[] eventBuffer)
		{
			this.parentEdit = parentEdit;
			this.eventName = eventName;
			this.controllable = controllable;
			this.observable = observable;
			this.graph = graph;
			this.eventBuffer = eventBuffer;
		}

		public void actionPerformed(ActionEvent event)
		{
			if (graph != null)
			{
				GraphUndoableEdits.UndoableCreateEvent action = new GraphUndoableEdits.UndoableCreateEvent(
						graph,
						eventName,
						controllable,
						observable);
				action.redo();
				if (eventBuffer != null && eventBuffer.length > 0)
				{
					eventBuffer[0] = action.getEvent();
				}
				postEdit(action);
			}
		}
	}

	public static class RemoveEventAction extends AbstractGraphAction
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 3384021428481069994L;

		protected FSAEvent event;

		protected FSAGraph graph;

		public RemoveEventAction(FSAGraph graph, FSAEvent event)
		{
			this(null, graph, event);
		}

		public RemoveEventAction(CompoundEdit parentEdit, FSAGraph graph,
				FSAEvent event)
		{
			this.parentEdit = parentEdit;
			this.event = event;
			this.graph = graph;
		}

		public void actionPerformed(ActionEvent event)
		{
			if (graph != null)
			{
				CompoundEdit allEdits = new CompoundEdit();
				Set<Edge> edgesToRemove = new HashSet<Edge>();
				for (Edge e : graph.getEdges())
				{
					if (e instanceof InitialArrow)
					{
						continue;
					}
					Vector<FSAEvent> eventsToKeep = new Vector<FSAEvent>();
					for (Iterator<FSATransition> i = e.getTransitions(); i
							.hasNext();)
					{
						FSAEvent te = i.next().getEvent();
						if (te != this.event)
						{
							eventsToKeep.add(te);
						}
					}
					if (e.transitionCount() > 0 && eventsToKeep.size() == 0)
					{
						edgesToRemove.add(e);
					}
					new EdgeActions.LabelAction(
							allEdits,
							graph,
							e,
							eventsToKeep).execute();
				}
				SelectionGroup group = new SelectionGroup();
				for (Edge e : edgesToRemove)
				{
					group.insert(e);
				}
				new GraphActions.RemoveAction(allEdits, graph, group).execute();
				GraphUndoableEdits.UndoableRemoveEvent action = new GraphUndoableEdits.UndoableRemoveEvent(
						graph,
						this.event);
				action.redo();
				allEdits.addEdit(action);
				allEdits.end();
				postEdit(allEdits);
			}
		}
	}

	public static class ModifyEventAction extends AbstractGraphAction
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -1173808761820564447L;

		protected FSAEvent event;

		protected String eventName;

		protected boolean controllable;

		protected boolean observable;

		protected FSAModel model;

		public ModifyEventAction(FSAModel model, FSAEvent event,
				String eventName, boolean controllable, boolean observable)
		{
			this(null, model, event, eventName, controllable, observable);
		}

		public ModifyEventAction(CompoundEdit parentEdit, FSAModel model,
				FSAEvent event, String eventName, boolean controllable,
				boolean observable)
		{
			this.parentEdit = parentEdit;
			this.event = event;
			this.eventName = eventName;
			this.controllable = controllable;
			this.observable = observable;
			this.model = model;
		}

		public void actionPerformed(ActionEvent event)
		{
			if (model != null)
			{
				GraphUndoableEdits.UndoableModifyEvent action = new GraphUndoableEdits.UndoableModifyEvent(
						model,
						this.event,
						eventName,
						controllable,
						observable);
				action.redo();
				postEdit(action);
			}
		}
	}

	/**
	 * This action is executed every time a graph element is moved in a Graph.
	 * The objective is to make undo/redo actions possible. According to the
	 * chosen design for undoable actions, there are two static classes involved
	 * on any UndoableAction, one to construct the UndoableAction(s), perform
	 * it(or them), and inform the CommandManager about this(these) action. The
	 * other class is the UndoableAction itself. This class constructs an
	 * UndoableMovement, informing the GraphElement of interest and its
	 * displacement over the Graph.
	 * 
	 * @author Christian Silvano
	 */
	public static class MoveAction extends AbstractGraphAction
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -6469239456200190095L;

		// The set of elements that are being moved.
		protected SelectionGroup selection = null;

		// The displacement of the selection, it is a vector where the direction
		// of the displacement can be inferred by the signals of its
		// coordinates.
		protected Point2D.Float displacement;

		protected FSAGraph graph;

		public MoveAction(FSAGraph graph, SelectionGroup currentSelection,
				Point2D.Float displacement)
		{
			this(null, graph, currentSelection, displacement);
		}

		/**
		 * @param currentSelection
		 * @param displacement
		 */
		public MoveAction(CompoundEdit parentEdit, FSAGraph graph,
				SelectionGroup selection, Point2D.Float displacement)
		{
			this.parentEdit = parentEdit;
			if (selection == null)
			{
				selection = new SelectionGroup();
			}
			this.selection = selection.copy();
			this.displacement = (Point2D.Float)displacement.clone();
			this.graph = graph;
		}

		public MoveAction(FSAGraph graph, GraphElement element,
				Point2D.Float displacement)
		{
			this(null, graph, element, displacement);
		}

		public MoveAction(CompoundEdit parentEdit, FSAGraph graph,
				GraphElement element, Point2D.Float displacement)
		{
			this(parentEdit, graph, new SelectionGroup(element), displacement);
		}

		// Creates an UndoableMove (an object capable of undoing/redoing the
		// movement)
		// and informs CommandManager about a new UndoableAction.
		public void actionPerformed(ActionEvent event)
		{
			if (graph != null)
			{
				if (selection.size() < 1)
				{
					return;
				}
				if (selection.size() == 1)
				{
					postEditAdjustCanvas(graph,
							new GraphUndoableEdits.UndoableMove(
									graph,
									selection.children().next(),
									displacement));
				}
				else
				{
					CompoundEdit allEdits = new CompoundEdit();
					for (Iterator<GraphElement> i = selection.children(); i
							.hasNext();)
					{
						GraphElement element = i.next();
						if ((element instanceof Node)
								|| (element instanceof GraphLabel))
						{
							UndoableEdit edit = new GraphUndoableEdits.UndoableMove(
									graph,
									element,
									displacement);
							// no need to "redo" the edit since the element has
							// already been moved
							allEdits.addEdit(edit);
						}
					}
					allEdits.addEdit(new GraphUndoableEdits.UndoableDummyLabel(
							Hub.string("undoMoveElements")));
					allEdits.end();
					postEditAdjustCanvas(graph, allEdits);
				}
			}
		}
	}

	public static class LabelAction extends AbstractGraphAction
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -3976668220431694563L;

		protected String text;

		protected GraphElement element;

		protected FSAGraph graph;

		public LabelAction(FSAGraph graph, GraphLabel element, String text)
		{
			this(null, graph, element, text);
		}

		public LabelAction(CompoundEdit parentEdit, FSAGraph graph,
				GraphLabel element, String text)
		{
			this.parentEdit = parentEdit;
			this.element = element;
			this.text = text;
			if (this.text == null)
			{
				this.text = "";
			}
			this.graph = graph;
		}

		public LabelAction(FSAGraph graph, Node element, String text)
		{
			this(null, graph, element, text);
		}

		public LabelAction(CompoundEdit parentEdit, FSAGraph graph,
				Node element, String text)
		{
			this.parentEdit = parentEdit;
			this.element = element;
			this.text = text;
			if (this.text == null)
			{
				this.text = "";
			}
			this.graph = graph;
		}

		public void actionPerformed(ActionEvent event)
		{
			if (element != null)
			{
				GraphUndoableEdits.UndoableLabel action = new GraphUndoableEdits.UndoableLabel(
						graph,
						element,
						text);
				action.redo();
				postEditAdjustCanvas(graph, action);
			}

		}
	}

	/**
	 * Emulates "snap to grid".
	 * 
	 * @author Lenko Grigorov, Christian Silvano
	 */
	public static class AlignNodesAction extends AbstractGraphAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -8359658501096690473L;

		protected FSAGraph graph;

		protected SelectionGroup group;

		public AlignNodesAction(FSAGraph graph, SelectionGroup group)
		{
			this(null, graph, group);
		}

		public AlignNodesAction(CompoundEdit parentEdit, FSAGraph graph,
				SelectionGroup group)
		{
			this.parentEdit = parentEdit;
			this.graph = graph;
			if (group == null)
			{
				group = new SelectionGroup();
			}
			this.group = group.copy();
		}

		public void actionPerformed(ActionEvent event)
		{
			if (graph != null)
			{
				if (group.size() < 1)
				{
					return;
				}
				CompoundEdit allEdits = new CompoundEdit();
				for (Iterator<GraphElement> i = group.children(); i.hasNext();)
				{
					GraphElement element = i.next();
					if (element instanceof Node)
					{
						Point2D.Float displacement = element.snapToGrid();
						new MoveAction(allEdits, graph, element, displacement)
								.execute();
					}
				}
				graph.commitLayoutModified();
				allEdits.addEdit(new GraphUndoableEdits.UndoableDummyLabel(Hub
						.string("undoMoveNodes")));
				allEdits.end();
				postEditAdjustCanvas(graph, allEdits);
			}
		}
	}

	/**
	 * This action is executed every time graph elements are created in a Graph.
	 * The objective is to make undo/redo actions possible. According to the
	 * chosen design for undoable actions, there are two static classes involved
	 * on any UndoableAction, one to construct the undoable actions and perform
	 * them and inform the CommandManager about the action. Other that is the
	 * UndoableAction itself. The class bellow creates an UndoableCreate object
	 * and informs the CommandManager about its existence.
	 * 
	 * @author Helen Bretzke, Christian Silvano
	 */
	public static class CreateNodeAction extends AbstractGraphAction
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -5635655934590985770L;

		protected Point2D.Float location;

		protected FSAGraph graph;

		protected Node[] nodeBuffer;

		public CreateNodeAction(FSAGraph graph, Point2D.Float location)
		{
			this(graph, location, null);
		}

		public CreateNodeAction(FSAGraph graph, Point2D.Float location,
				Node[] nodeBuffer)
		{
			this(null, graph, location, nodeBuffer);
		}

		public CreateNodeAction(CompoundEdit parentEdit, FSAGraph graph,
				Point2D.Float location)
		{
			this(parentEdit, graph, location, null);
		}

		public CreateNodeAction(CompoundEdit parentEdit, FSAGraph graph,
				Point2D.Float location, Node[] nodeBuffer)
		{
			this.parentEdit = parentEdit;
			this.location = (Point2D.Float)location.clone();
			this.graph = graph;
			this.nodeBuffer = nodeBuffer;
		}

		public void actionPerformed(ActionEvent event)
		{
			if (graph != null)
			{
				GraphUndoableEdits.UndoableCreateNode edit = new GraphUndoableEdits.UndoableCreateNode(
						graph,
						location);
				edit.redo();
				if (nodeBuffer != null && nodeBuffer.length > 0)
				{
					nodeBuffer[0] = edit.getNode();
				}
				postEditAdjustCanvas(graph, edit);
			}
		}
	}

	public static class CreateEdgeAction extends AbstractGraphAction
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 2023269558543526739L;

		protected Node source;

		protected Node target;

		protected FSAGraph graph;

		protected Edge[] edgeBuffer;

		public CreateEdgeAction(FSAGraph graph, Node source, Node target)
		{
			this(graph, source, target, null);
		}

		public CreateEdgeAction(FSAGraph graph, Node source, Node target,
				Edge[] edgeBuffer)
		{
			this(null, graph, source, target, edgeBuffer);
		}

		public CreateEdgeAction(CompoundEdit parentEdit, FSAGraph graph,
				Node source, Node target)
		{
			this(parentEdit, graph, source, target, null);
		}

		public CreateEdgeAction(CompoundEdit parentEdit, FSAGraph graph,
				Node source, Node target, Edge[] edgeBuffer)
		{
			this.parentEdit = parentEdit;
			this.source = source;
			this.target = target;
			this.graph = graph;
			this.edgeBuffer = edgeBuffer;
		}

		public void actionPerformed(ActionEvent event)
		{
			if (graph != null)
			{
				CompoundEdit allEdits = new CompoundEdit();
				for (Edge e : graph.getEdgesBetween(source, target))
				{
					if (e instanceof BezierEdge)
					{
						allEdits
								.addEdit(new GraphUndoableEdits.UndoableModifyEdge(
										graph,
										e,
										((BezierLayout)e.getLayout()).clone()));
					}
				}
				GraphUndoableEdits.UndoableCreateEdge edit = new GraphUndoableEdits.UndoableCreateEdge(
						graph,
						source,
						target);
				edit.redo();
				allEdits.addEdit(edit);
				allEdits.end();
				postEditAdjustCanvas(graph, allEdits);
				if (edgeBuffer != null && edgeBuffer.length > 0)
				{
					edgeBuffer[0] = edit.getEdge();
				}
			}
		}
	}

	public static class RemoveAction extends AbstractGraphAction
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -1845072786432848671L;

		protected SelectionGroup selection;

		protected FSAGraph graph;

		public RemoveAction(FSAGraph graph, SelectionGroup selection)
		{
			this(null, graph, selection);
		}

		public RemoveAction(CompoundEdit parentEdit, FSAGraph graph,
				SelectionGroup selection)
		{
			this.parentEdit = parentEdit;
			if (selection == null)
			{
				selection = new SelectionGroup();
			}
			this.selection = selection.copy();
			this.graph = graph;
		}

		public RemoveAction(FSAGraph graph, GraphElement element)
		{
			this(null, graph, element);
		}

		public RemoveAction(CompoundEdit parentEdit, FSAGraph graph,
				GraphElement element)
		{
			this(parentEdit, graph, new SelectionGroup(element));
		}

		public void actionPerformed(ActionEvent event)
		{
			if (graph != null)
			{
				if (selection.size() < 1)
				{
					return;
				}
				if (selection.size() == 1)
				{
					GraphElement element = selection.children().next();
					if (!(element instanceof InitialArrow))
					{
						postEdit(constructUndoableEdit(element));
					}
				}
				else
				{
					CompoundEdit allEdits = new CompoundEdit();
					// first delete all edges
					for (Iterator<GraphElement> i = selection.children(); i
							.hasNext();)
					{
						GraphElement element = i.next();
						if (element instanceof Edge
								&& !(element instanceof InitialArrow))
						{
							allEdits.addEdit(constructUndoableEdit(element));
						}
					}
					// then delete everything else - otherwise an edge may be
					// undone before its nodes
					for (Iterator<GraphElement> i = selection.children(); i
							.hasNext();)
					{
						GraphElement element = i.next();
						if (!(element instanceof Edge))
						{
							allEdits.addEdit(constructUndoableEdit(element));
						}
					}
					allEdits.addEdit(new GraphUndoableEdits.UndoableDummyLabel(
							Hub.string("undoDeleteElements")));
					allEdits.end();
					postEdit(allEdits);
				}
			}
		}

		protected UndoableEdit constructUndoableEdit(GraphElement element)
		{
			CompoundEdit allEdits = new CompoundEdit();
			if (element instanceof Edge)
			{
				UndoableEdit edit = new GraphUndoableEdits.UndoableDeleteEdge(
						graph,
						(Edge)element);
				edit.redo();
				allEdits.addEdit(edit);
			}
			else if (element instanceof Node)
			{
				Set<Edge> edgesToRemove = new HashSet<Edge>();
				for (Iterator<GraphElement> i = element.children(); i.hasNext();)
				{
					GraphElement child = i.next();
					if (child instanceof Edge
							&& !(child instanceof InitialArrow))
					{
						edgesToRemove.add((Edge)child);
					}
				}
				for (Edge edge : edgesToRemove)
				{
					UndoableEdit edit = new GraphUndoableEdits.UndoableDeleteEdge(
							graph,
							edge);
					edit.redo();
					allEdits.addEdit(edit);
				}
				UndoableEdit edit = new GraphUndoableEdits.UndoableDeleteNode(
						graph,
						(Node)element);
				edit.redo();
				allEdits.addEdit(edit);
			}
			allEdits.end();
			return allEdits;
		}
	}

	public static class UniformNodesAction extends AbstractGraphAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -5405840159479166562L;

		protected FSAGraph graph;

		public UniformNodesAction(FSAGraph graph)
		{
			super(Hub.string("comUniformNodeSize"));
			putValue(SHORT_DESCRIPTION, Hub.string("comHintUniformNodeSize"));
			this.graph = graph;
		}

		public void actionPerformed(ActionEvent e)
		{
			UndoableEdit edit = new GraphUndoableEdits.UndoableUniformNodeSize(
					graph,
					!graph.isUseUniformRadius());
			edit.redo();
			postEditAdjustCanvas(graph, edit);
		}
	}

	public static class ShiftGraphInViewAction extends AbstractAction
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 2907001062138002843L;

		protected static final int GRAPH_BORDER_THICKNESS = 10;

		protected FSAGraph graph;

		protected CompoundEdit parentEdit = null;

		public ShiftGraphInViewAction(FSAGraph graph)
		{
			this(null, graph);
		}

		public ShiftGraphInViewAction(CompoundEdit parentEdit, FSAGraph graph)
		{
			this.parentEdit = parentEdit;
			this.graph = graph;
		}

		public void actionPerformed(ActionEvent event)
		{
			if (graph != null)
			{
				Rectangle graphBounds = graph.getBounds(true);
				if (graphBounds.x < 0 || graphBounds.y < 0)
				{
					UndoableEdit translation = new GraphUndoableEdits.UndoableTranslateGraph(
							graph,
							new Point2D.Float(-graphBounds.x
									+ GRAPH_BORDER_THICKNESS, -graphBounds.y
									+ GRAPH_BORDER_THICKNESS));
					translation.redo();
					if (parentEdit != null)
					{
						parentEdit.addEdit(translation);
					}
					else
					{
						UndoManager.addEdit(translation);
					}
				}
			}
		}

		public void execute()
		{
			actionPerformed(null);
		}
	}
}
