package presentation.fsa.actions;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;

import main.Hub;
import model.fsa.FSAEvent;
import model.fsa.FSAMessage;
import model.fsa.FSAState;
import model.fsa.FSATransition;
import model.fsa.ver2_1.Automaton;
import presentation.CubicParamCurve2D;
import presentation.GraphicalLayout;
import presentation.PresentationElement;
import presentation.fsa.BezierEdge;
import presentation.fsa.BezierLayout;
import presentation.fsa.CircleNode;
import presentation.fsa.ContextAdaptorHack;
import presentation.fsa.Edge;
import presentation.fsa.FSAGraph;
import presentation.fsa.FSAGraphMessage;
import presentation.fsa.GraphDrawingView;
import presentation.fsa.GraphElement;
import presentation.fsa.GraphLabel;
import presentation.fsa.InitialArrow;
import presentation.fsa.Node;
import presentation.fsa.SelectionGroup;

public class GraphUndoableEdits {
	
	public static class UndoableDummyLabel extends AbstractUndoableEdit
	{
		String label="";
		public UndoableDummyLabel(String label)
		{
			this.label=label;
		}
		/**
		 * Returns the name that should be displayed besides the Undo/Redo menu items, so the user knows
		 * which action will be undone/redone.
		 */
		public String getPresentationName() {
			return label;
		}		
	}


	public static class UndoableLabel extends AbstractGraphUndoableEdit
	{
		protected String text=null;
		protected String originalText=null;
		protected GraphElement element;
		protected FSAGraph graph;
		
		public UndoableLabel(FSAGraph graph, GraphElement element, String text)
		{
			this.element=element;
			this.text=text;
			this.graph=graph;
		}
		
		public void undo() throws CannotUndoException {
			if(originalText==null)
			{
				throw new CannotUndoException();
			}
			if(element instanceof Node)
			{
				text=((Node)element).getLabel().getText();
				graph.labelNode((Node)element, originalText);
			}
			else if(element instanceof GraphLabel)
			{
				text=((GraphLabel)element).getText();
				graph.setLabelText((GraphLabel)element, originalText);
			}
//			else
//			{
//				//TODO add modification for free labels 
//			}
			originalText=null;
		}

		public void redo() throws CannotRedoException {
			if(text==null)
			{
				throw new CannotRedoException();
			}
			if(element instanceof Node)
			{
				originalText=((Node)element).getLabel().getText();
				graph.labelNode((Node)element, text);
			}
			else if(element instanceof GraphLabel)
			{
				originalText=((GraphLabel)element).getText();
				graph.setLabelText((GraphLabel)element, text);
			}
//			else
//			{
//				//TODO add modification for free labels 
//			}
			text=null;
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
			if(element instanceof Node)
			{
				return Hub.string("undoLabelNode");
			}
			else
			{
				return Hub.string("undoLabel");
			}
		}		
	}

	public static class UndoableEdgeLabel extends AbstractUndoableEdit
	{
		protected Vector<FSATransition> newTransitions=new Vector<FSATransition>();
		protected Vector<FSATransition> originalTransitions=new Vector<FSATransition>();
		protected Edge edge;
		protected FSAGraph graph;
		protected Vector<FSAEvent> assignedEvents;
		
		public UndoableEdgeLabel(FSAGraph graph, Edge edge, Vector<FSAEvent> assignedEvents)
		{
			this.graph=graph;
			this.edge=edge;
			this.assignedEvents=assignedEvents;
		}
		
		public void undo() throws CannotUndoException {
			if(originalTransitions==null)
			{
				throw new CannotUndoException();
			}
			graph.replaceTransitionsOnEdge(originalTransitions, edge);
		}

		public void redo() throws CannotRedoException {
			if(assignedEvents!=null)
			{
				for(Iterator<FSATransition> i=edge.getTransitions();i.hasNext();)
				{
					originalTransitions.add(i.next());
				}
				graph.replaceEventsOnEdge(assignedEvents.toArray(new FSAEvent[0]), edge);
				for(Iterator<FSATransition> i=edge.getTransitions();i.hasNext();)
				{
					newTransitions.add(i.next());
				}
				assignedEvents=null;
			}
			else
			{
				graph.replaceTransitionsOnEdge(newTransitions, edge);
			}
		}

		public boolean canUndo() {
			return originalTransitions!=null;
		}

		public boolean canRedo() {
			return true;
		}

		/**
		 * Returns the name that should be displayed besides the Undo/Redo menu items, so the user knows
		 * which action will be undone/redone.
		 */
		public String getPresentationName() {
			return Hub.string("undoLabelEdge");
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
	public static class UndoableMove extends AbstractGraphUndoableEdit {

		//A collection of graph elements
		protected GraphElement element = null;

		//A vector meaning the displacement of the selection
		protected Point2D.Float displacement;
		
		protected FSAGraph graph;

		/**
		 * Default constructor
		 * @param g collection of graph elements
		 * @param d displacement of the elements
		 */
		public UndoableMove(FSAGraph graph, GraphElement element, Point2D.Float d) {
			this.graph=graph;
			this.element=element;
			displacement = (Point2D.Float)d.clone();
			if(displacement==null)
			{
				displacement=new Point2D.Float();
			}
		}

		/**
		 * Undoes a movement by applying a vector opposite to <code>displacement</code>
		 * over <code>collection</code>
		 */
		public void undo() throws CannotUndoException {
			if(element==null)
			{
				throw new CannotUndoException();
			}
			element.translate(-displacement.x, -displacement.y);
			graph.commitLayoutModified();
		}

		/**
		 * Redoes a movement by applying a <code>displacement</code>
		 * over <code>collection</code>
		 */
		public void redo() throws CannotRedoException {
			if(element==null)
			{
				throw new CannotRedoException();
			}
			element.translate(displacement.x, displacement.y);
			graph.commitLayoutModified();
		}

		public boolean canUndo() {
			return element!=null;
		}

		public boolean canRedo() {
			return element!=null;
		}

		/**
		 * Returns the name that should be displayed besides the Undo/Redo menu items, so the user knows
		 * which action will be undone/redone.
		 */
		public String getPresentationName() {
			if(element instanceof Node)
			{
				if(usePluralDescription)
				{
					return Hub.string("undoMoveNodes");
				}
				else
				{
					return Hub.string("undoMoveNode");
				}
			}
			else if(element instanceof GraphLabel)
			{
				if(usePluralDescription)
				{
					return Hub.string("undoMoveLabels");
				}
				else
				{
					return Hub.string("undoMoveLabel");
				}
			}
			else
			{
				if(usePluralDescription)
				{
					return Hub.string("undoMoveElements");
				}
				else
				{
					return Hub.string("undoMoveElement");
				}				
			}
		}
	}

	public static class UndoableCreateEvent extends AbstractGraphUndoableEdit
	{
		protected FSAEvent event;
		protected String eventName;
		protected boolean controllable;
		protected boolean observable;
		protected FSAGraph graph;
		
		public UndoableCreateEvent(FSAGraph graph, String eventName, boolean controllable, boolean observable)
		{
			this.eventName=eventName;
			this.controllable=controllable;
			this.observable=observable;
			this.graph=graph;
		}
		
		public FSAEvent getEvent()
		{
			return event;
		}
		
		public void redo() throws CannotRedoException {
			if(eventName==null)
			{
				throw new CannotRedoException();
			}
			if(event==null)
			{
				event=graph.createAndAddEvent(eventName, controllable, observable);
			}
			else
			{
				graph.getModel().add(event);
			}
			eventName=null;
		}

		public void undo() throws CannotUndoException {
			if(event==null)
			{
				throw new CannotUndoException();
			}
			graph.getModel().remove(event);
			eventName=event.getSymbol();
			controllable=event.isControllable();
			observable=event.isObservable();
		}

		public boolean canUndo() {
			return (event!=null);
		}

		public boolean canRedo() {
			return (eventName!=null);
		}

		/**
		 * Returns the name that should be displayed besides the Undo/Redo menu items, so the user knows
		 * which action will be undone/redone.
		 */
		public String getPresentationName() {
			if(usePluralDescription)
			{
				return Hub.string("undoCreateEvents");
			}
			else
			{
				return Hub.string("undoCreateEvent");
			}
		}		
	}

	public static class UndoableRemoveEvent extends AbstractGraphUndoableEdit
	{
		protected FSAEvent event;
		protected FSAGraph graph;
		
		public UndoableRemoveEvent(FSAGraph graph, FSAEvent event)
		{
			this.graph=graph;
			this.event=event;
		}
		
		public void redo() throws CannotRedoException {
			if(event==null) //if the event didn't exist in the model
			{
				return;
			}
			if(!graph.getModel().getEventSet().contains(event))
			{
				event=null; //won't do anythin on Undo/Redo
			}
			else
			{
				graph.getModel().remove(event);
			}
		}

		public void undo() throws CannotUndoException {
			if(event==null) //if the event didn't exist in the model, don't introduce it
			{
				return;
			}
			graph.getModel().add(event);
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
			if(usePluralDescription)
			{
				return Hub.string("undoRemoveEvents");
			}
			else
			{
				return Hub.string("undoRemoveEvent");
			}
		}		
	}


	public static class UndoableModifyEvent extends AbstractGraphUndoableEdit
	{
		protected FSAEvent event;
		protected String alternateName;
		protected boolean alternateControllable;
		protected boolean alternateObservable;
		protected FSAGraph graph;
		
		public UndoableModifyEvent(FSAGraph graph, FSAEvent event, String newName, boolean newControllable, boolean newObservable)
		{
			this.graph=graph;
			this.event=event;
			alternateName=newName;
			alternateControllable=newControllable;
			alternateObservable=newObservable;
		}
		
		public void redo() throws CannotRedoException {
			if(event==null)
			{
				throw new CannotRedoException();
			}
			swapEventInfo();
		}

		public void undo() throws CannotUndoException {
			if(event==null)
			{
				throw new CannotUndoException();
			}
			swapEventInfo();
		}
		
		protected void swapEventInfo()
		{
			String prevName=event.getSymbol();
			boolean prevControllable=event.isControllable();
			boolean prevObservable=event.isObservable();
			event.setSymbol(alternateName);
			event.setControllable(alternateControllable);
			event.setObservable(alternateObservable);
			alternateName=prevName;
			alternateControllable=prevControllable;
			alternateObservable=prevObservable;
			graph.getModel().fireFSAEventSetChanged(new FSAMessage(FSAMessage.MODIFY,FSAMessage.EVENT,event.getId(),graph.getModel()));			
		}

		public boolean canUndo() {
			return event!=null;
		}

		public boolean canRedo() {
			return event!=null;
		}

		/**
		 * Returns the name that should be displayed besides the Undo/Redo menu items, so the user knows
		 * which action will be undone/redone.
		 */
		public String getPresentationName() {
			if(usePluralDescription)
			{
				return Hub.string("undoModifyEvents");
			}
			else
			{
				return Hub.string("undoModifyEvent");
			}
		}	
	}

	public static class UndoableCreateEdge extends AbstractGraphUndoableEdit
	{
		protected FSAGraph graph;
		protected Node source;
		protected Node target;
		protected Edge edge;
		
		public UndoableCreateEdge(FSAGraph graph, Node source, Node target)
		{
			this.graph=graph;
			this.source=source;
			this.target=target;
		}
		
		public Edge getEdge()
		{
			return edge;
		}
		
		public void redo() throws CannotRedoException {
			if(source==null||target==null)
			{
				throw new CannotRedoException();
			}
			if(edge==null)
			{
				edge=graph.createEdge(source,target);
			}
			else
			{
				graph.reviveEdge(edge);
			}
		}

		public void undo() throws CannotUndoException {
			if(edge==null)
			{
				throw new CannotUndoException();
			}
			graph.delete(edge);
		}

		public boolean canUndo() {
			return (edge!=null);
		}

		public boolean canRedo() {
			return (source!=null&&target!=null);
		}

		/**
		 * Returns the name that should be displayed besides the Undo/Redo menu items, so the user knows
		 * which action will be undone/redone.
		 */
		public String getPresentationName() {
			if(usePluralDescription)
			{
				if(source==target)
				{
					return Hub.string("undoCreateSelfloops");
				}
				else
				{
					return Hub.string("undoCreateEdges");
				}
			}
			else
			{
				if(source==target)
				{
					return Hub.string("undoCreateSelfloop");
				}
				else
				{
					return Hub.string("undoCreateEdge");
				}
			}
		}		
	}

	public static class UndoableDeleteEdge extends AbstractGraphUndoableEdit
	{
		protected FSAGraph graph;
		protected Edge edge;
		
		public UndoableDeleteEdge(FSAGraph graph, Edge edge)
		{
			this.graph=graph;
			this.edge=edge;
		}
		
		public void redo() throws CannotRedoException {
			graph.delete(edge);
		}

		public void undo() throws CannotUndoException {
			graph.reviveEdge(edge);
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
			if(usePluralDescription)
			{
				return Hub.string("undoDeleteEdges");
			}
			else
			{
				return Hub.string("undoDeleteEdge");
			}
		}		
	}
	
	public static class UndoableModifyEdge extends AbstractGraphUndoableEdit
	{
		protected FSAGraph graph;
		protected Edge edge;
		protected GraphicalLayout altLayout;
		
		public UndoableModifyEdge(FSAGraph graph, Edge edge,GraphicalLayout originalLayout)
		{
			this.graph=graph;
			this.edge=edge;
			altLayout=originalLayout;
		}
		
		public void redo() throws CannotRedoException {
			if(edge==null)
			{
				throw new CannotRedoException();
			}
			swapLayout();
		}

		public void undo() throws CannotUndoException {
			if(edge==null)
			{
				throw new CannotUndoException();
			}
			swapLayout();
		}

		protected void swapLayout()
		{
			GraphicalLayout tLayout=edge.getLayout();
			edge.setLayout(altLayout);
			altLayout=tLayout;
			edge.refresh();
			graph.fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY,
					FSAGraphMessage.EDGE,edge.getId(),edge.bounds(),graph));
		}
		
		public boolean canUndo() {
			return (edge!=null);
		}

		public boolean canRedo() {
			return (edge!=null);
		}

		/**
		 * Returns the name that should be displayed besides the Undo/Redo menu items, so the user knows
		 * which action will be undone/redone.
		 */
		public String getPresentationName() {
			if(usePluralDescription)
			{
				if(edge.getSourceNode()==edge.getTargetNode())
				{
					return Hub.string("undoModifySelfloops");
				}
				else
				{
					return Hub.string("undoModifyEdges");
				}
			}
			else
			{
				if(edge.getSourceNode()==edge.getTargetNode())
				{
					return Hub.string("undoModifySelfloop");
				}
				else
				{
					return Hub.string("undoModifyEdge");
				}
			}
		}		
	}
	
	public static class UndoableModifyInitialArrow extends AbstractGraphUndoableEdit
	{
		protected FSAGraph graph;
		protected InitialArrow arrow;
		protected Point2D.Float altDirection;
		
		public UndoableModifyInitialArrow(FSAGraph graph, InitialArrow arrow, Point2D.Float originalDirection)
		{
			this.graph=graph;
			this.arrow=arrow;
			altDirection=originalDirection;
		}
		
		public void undo() throws CannotUndoException {
			if(arrow==null)
			{
				throw new CannotUndoException();
			}
			swapDirection();
		}

		public void redo() throws CannotRedoException {
			if(arrow==null)
			{
				throw new CannotRedoException();
			}
			swapDirection();
		}
		
		protected void swapDirection()
		{
			Point2D.Float tDirection=(Point2D.Float)arrow.getDirection().clone();
			arrow.setDirection(altDirection);
			altDirection=tDirection;
			if(arrow.getParent()!=null&&arrow.getParent() instanceof Node)
			{
				arrow.getParent().refresh();
				graph.fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY,
					FSAGraphMessage.NODE,arrow.getParent().getId(),arrow.getParent().bounds(),graph));
			}
		}

		public boolean canUndo() {
			return arrow!=null;
		}

		public boolean canRedo() {
			return arrow!=null;
		}

		/**
		 * Returns the name that should be displayed besides the Undo/Redo menu items, so the user knows
		 * which action will be undone/redone.
		 */
		public String getPresentationName() {
			return Hub.string("undoModifyInitialArrow");
		}		
	}
	
	public static class UndoableCreateNode extends AbstractGraphUndoableEdit
	{
		protected FSAGraph graph;
		protected Point2D.Float location;
		protected Node node;
		
		public UndoableCreateNode(FSAGraph graph, Point2D.Float location)
		{
			this.graph=graph;
			this.location=location;
		}
		
		public Node getNode()
		{
			return node;
		}
		
		public void redo() throws CannotRedoException {
			if(location==null)
			{
				throw new CannotRedoException();
			}
			if(node==null)
			{
				node=graph.createNode(location);
			}
			else
			{
				graph.reviveNode(node);
			}
		}

		public void undo() throws CannotUndoException {
			if(node==null)
			{
				throw new CannotUndoException();
			}
			graph.delete(node);
		}

		public boolean canUndo() {
			return (node!=null);
		}

		public boolean canRedo() {
			return (location!=null);
		}

		/**
		 * Returns the name that should be displayed besides the Undo/Redo menu items, so the user knows
		 * which action will be undone/redone.
		 */
		public String getPresentationName() {
			if(usePluralDescription)
			{
				return Hub.string("undoCreateNodes");
			}
			else
			{
				return Hub.string("undoCreateNode");
			}
		}		
	}
	
	public static class UndoableDeleteNode extends AbstractGraphUndoableEdit
	{
		protected FSAGraph graph;
		protected Node node;
		
		public UndoableDeleteNode(FSAGraph graph, Node node)
		{
			this.graph=graph;
			this.node=node;
		}
		
		public void redo() throws CannotRedoException {
			graph.delete(node);
		}

		public void undo() throws CannotUndoException {
			graph.reviveNode(node);
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
			if(usePluralDescription)
			{
				return Hub.string("undoDeleteNodes");
			}
			else
			{
				return Hub.string("undoDeleteNode");
			}
		}	
	}

	public static class UndoableTranslateGraph extends AbstractGraphUndoableEdit
	{
		protected FSAGraph graph;
		protected Point2D.Float displacement;
		
		public UndoableTranslateGraph(FSAGraph graph, Point2D.Float displacement)
		{
			this.graph=graph;
			this.displacement=displacement;
		}
		
		public void redo() throws CannotRedoException {
			graph.translate(displacement.x, displacement.y);
		}

		public void undo() throws CannotUndoException {
			graph.translate(-displacement.x, -displacement.y);
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
			return Hub.string("undoTranslateGraph");
		}	
	}

	public static class UndoableSetInitial extends AbstractGraphUndoableEdit
	{
		protected Node node;
		protected boolean state;
		protected String desc;
		protected FSAGraph graph;
		
		public UndoableSetInitial(FSAGraph graph, Node node, boolean state)
		{
			this.graph=graph;
			this.node=node;
			this.state=state;
			if(state)
			{
				desc=Hub.string("undoMakeInitial");
			}
			else
			{
				desc=Hub.string("undoRemoveInitial");
			}
		}
		
		public void redo() throws CannotRedoException {
			if(node==null)
			{
				throw new CannotRedoException();
			}
			graph.setInitial(node,state);
			state=!state;
		}

		public void undo() throws CannotUndoException {
			if(node==null)
			{
				throw new CannotUndoException();
			}
			graph.setInitial(node,state);
			state=!state;
		}

		public boolean canUndo() {
			return node!=null;
		}

		public boolean canRedo() {
			return node!=null;
		}

		/**
		 * Returns the name that should be displayed besides the Undo/Redo menu items, so the user knows
		 * which action will be undone/redone.
		 */
		public String getPresentationName() {
			return desc;
		}
	}
	
	public static class UndoableSetMarking extends AbstractGraphUndoableEdit
	{
		protected Node node;
		protected boolean state;
		protected String desc;
		protected FSAGraph graph;
		
		public UndoableSetMarking(FSAGraph graph, Node node, boolean state)
		{
			this.graph=graph;
			this.node=node;
			this.state=state;
			if(state)
			{
				desc=Hub.string("undoMarkNode");
			}
			else
			{
				desc=Hub.string("undoUnmarkNode");
			}
		}
		
		public void redo() throws CannotRedoException {
			if(node==null)
			{
				throw new CannotRedoException();
			}
			graph.setMarked(node,state);
			state=!state;
		}

		public void undo() throws CannotUndoException {
			if(node==null)
			{
				throw new CannotUndoException();
			}
			graph.setMarked(node,state);
			state=!state;
		}

		public boolean canUndo() {
			return node!=null;
		}

		public boolean canRedo() {
			return node!=null;
		}

		/**
		 * Returns the name that should be displayed besides the Undo/Redo menu items, so the user knows
		 * which action will be undone/redone.
		 */
		public String getPresentationName() {
			return desc;
		}	
	}

	public static class UndoableUniformNodeSize extends AbstractGraphUndoableEdit
	{
		protected boolean state;
		protected FSAGraph graph;
		
		public UndoableUniformNodeSize(FSAGraph graph, boolean state)
		{
			this.graph=graph;
			this.state=state;
		}
		
		public void redo() throws CannotRedoException {
			if(graph==null)
			{
				throw new CannotRedoException();
			}
			graph.setUseUniformRadius(state);
			state=!state;
		}

		public void undo() throws CannotUndoException {
			if(graph==null)
			{
				throw new CannotUndoException();
			}
			graph.setUseUniformRadius(state);
			state=!state;
		}

		public boolean canUndo() {
			return graph!=null;
		}

		public boolean canRedo() {
			return graph!=null;
		}

		/**
		 * Returns the name that should be displayed besides the Undo/Redo menu items, so the user knows
		 * which action will be undone/redone.
		 */
		public String getPresentationName() {
			return Hub.string("undoUniformNodeSize");
		}
	}
}