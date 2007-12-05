package presentation.fsa.commands;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.undo.CompoundEdit;

import main.Hub;

import presentation.GraphicalLayout;
import presentation.fsa.BezierEdge;
import presentation.fsa.CircleNode;
import presentation.fsa.ContextAdaptorHack;
import presentation.fsa.Edge;
import presentation.fsa.EdgeLabellingDialog;
import presentation.fsa.FSAGraph;
import presentation.fsa.GraphDrawingView;
import presentation.fsa.GraphElement;
import presentation.fsa.GraphLabel;
import presentation.fsa.Node;
import presentation.fsa.tools.CreationTool;
import services.undo.UndoManager;

public class UIActions {

	/**
		 * The class for that toggles grid display.
		 * 
		 * @author Lenko Grigorov
		 */
		public static class ShowGridAction extends AbstractAction{
	
			private static ImageIcon icon = new ImageIcon();
			public boolean state = 	false;
			protected GraphDrawingView gdv=null;
			
			public ShowGridAction(GraphDrawingView gdv)
			{
				super(Hub.string("comGrid"),icon);
				icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/view_grid.gif")));
				putValue(SHORT_DESCRIPTION, Hub.string("comHintGrid"));
				this.gdv=gdv;
			}	
	
					
			/**
			 * Changes the property state.
			 */
			public void actionPerformed(ActionEvent e) {
				gdv.setShowGrid(!gdv.getShowGrid());
			}
		}

		/**
		 * A command to set the current drawing mode to creation mode. While in
		 * creating mode, user may create new objects in the GraphDrawingView.
		 * 
		 * @author Helen Bretzke
		 */
		public static class CreateTool extends AbstractAction {

			//An icon that can be used to describe this action
			private static ImageIcon icon = new ImageIcon();


			// Default constructor.	 
			public CreateTool() {
				super(Hub.string("comCreateTool"), icon);
				icon.setImage(Toolkit.getDefaultToolkit().createImage(
						Hub.getResource("images/icons/graphic_create.gif")));
				putValue(SHORT_DESCRIPTION, Hub.string("comHintCreateTool"));
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

			private static ImageIcon icon = new ImageIcon();

			public MoveTool() {
				super(Hub.string("comMoveTool"), icon);
				icon.setImage(Toolkit.getDefaultToolkit().createImage(
						Hub.getResource("images/icons/graphic_move.gif")));
				putValue(SHORT_DESCRIPTION, Hub.string("comHintMoveTool"));
			}

			//Switches the tool to Moving Tool
			public void actionPerformed(ActionEvent event) {
				ContextAdaptorHack.context.setTool(GraphDrawingView.MOVE);
				ContextAdaptorHack.context
				.setPreferredTool(GraphDrawingView.MOVE);
			}
		}
		
		/**
		 * A command to set the current drawing mode to editing mode. While in
		 * editing mode, user may select graph objects in the GraphDrawingView for
		 * deleting, copying, pasting and moving.
		 * 
		 * @author Helen Bretzke
		 */
		public static class SelectTool extends AbstractAction {

			//An icon that can be used to describe this action
			private static ImageIcon icon = new ImageIcon();

			//Default constructor
			public SelectTool() {
				super(Hub.string("comSelectTool"), icon);
				icon.setImage(Toolkit.getDefaultToolkit().createImage(
						Hub.getResource("images/icons/graphic_modify.gif")));
				putValue(SHORT_DESCRIPTION, Hub.string("comHintSelectTool"));
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
		 * A command to set the current drawing mode to labelling mode. While in
		 * labelling mode, user may label nodes and edges or create free labels.
		 * 
		 * @author Lenko Grigorov
		 */
		public static class TextTool extends AbstractAction {

			//An icon that can be used to describe this action
			private static ImageIcon icon = new ImageIcon();

			//Default constructor
			public TextTool() {
				super(Hub.string("comTextTool"), icon);
				icon.setImage(Toolkit.getDefaultToolkit().createImage(
						Hub.getResource("images/icons/machine_alpha.gif")));
				putValue(SHORT_DESCRIPTION, Hub.string("comHintTextTool"));
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
		 * Represent a user issued command to delete an element of the graph.
		 * What about deleting elements of a text label?
		 * 
		 * @author helen bretzke
		 * 
		 */
		public static class DeleteAction extends AbstractAction{
			private static ImageIcon icon = new ImageIcon();
			protected GraphDrawingView context;
			private GraphElement selection;

			public DeleteAction(GraphDrawingView context) {
				super(Hub.string("comDelete"), icon);
				icon.setImage(Toolkit.getDefaultToolkit().createImage(
						Hub.getResource("images/icons/edit_delete.gif")));
				putValue(SHORT_DESCRIPTION, Hub.string("comHintDelete"));
				this.context = context;
			}

			public void actionPerformed(ActionEvent evt) {
				if(((CreationTool)context.getTools()[GraphDrawingView.CREATE]).isDrawingEdge())
					((CreationTool)context.getTools()[GraphDrawingView.CREATE]).abortEdge();
				selection=context.getSelectedGroup();
				if(selection.size()<1)
				{
					return;
				}
				if(selection.size()==1)
				{
					new GraphActions.DeleteElementAction((FSAGraph)context.getLayoutShell(),selection.children().next()).execute();
				}
				else
				{
					CompoundEdit allEdits=new CompoundEdit();
					//first delete all edges
					for(Iterator<GraphElement> i=selection.children();i.hasNext();)
					{
						GraphElement element=i.next();
						if(element instanceof Edge)
						{
							new GraphActions.DeleteElementAction(allEdits,(FSAGraph)context.getLayoutShell(),element).execute();
						}
					}
					//then delete everything else - otherwise an edge may be undone before its nodes
					for(Iterator<GraphElement> i=selection.children();i.hasNext();)
					{
						GraphElement element=i.next();
						if(!(element instanceof Edge))
						{
							new GraphActions.DeleteElementAction(allEdits,(FSAGraph)context.getLayoutShell(),element).execute();
						}
					}
					allEdits.addEdit(new GraphUndoableEdits.UndoableDummyLabel(Hub.string("undoDeleteElements")));
					allEdits.end();
					UndoManager.addEdit(allEdits);
				}
				context.setTool(context.getPreferredTool());
			}

		}
		
		public static class TextAction extends AbstractAction {

			GraphElement element = null;

			Point2D.Float location = null;

			//An icon that can be used to describe this action
			private static ImageIcon icon = new ImageIcon();

			//Default constructor
			protected TextAction() {
				super(Hub.string("comLabel"), icon);
				icon.setImage(Toolkit.getDefaultToolkit().createImage(
						Hub.getResource("images/icons/machine_alpha.gif")));
				putValue(SHORT_DESCRIPTION, Hub.string("comHintLabel"));
			}

			public TextAction(Node n) {
				super(Hub.string("comLabelNode"), icon);
				icon.setImage(Toolkit.getDefaultToolkit().createImage(
						Hub.getResource("images/icons/machine_alpha.gif")));
				putValue(SHORT_DESCRIPTION, Hub.string("comHintLabelNode"));
				element=n;
			}

			public TextAction(Edge e) {
				super(Hub.string("comLabelEdge"), icon);
				icon.setImage(Toolkit.getDefaultToolkit().createImage(
						Hub.getResource("images/icons/machine_alpha.gif")));
				putValue(SHORT_DESCRIPTION, Hub.string("comHintLabelEdge"));
				element=e;
			}

			public TextAction(GraphElement currentSelection) {
				this();
				this.element = currentSelection;
			}

			/**
			 * @param context
			 * @param location
			 */
			public TextAction(Point location) {
				this();
				this.location = new Point2D.Float(location.x, location.y);
			}

			public void setElement(GraphElement element) {
				this.element = element;
			}

			public void actionPerformed(ActionEvent event){
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
					if (element instanceof Node) {
						Node node = (Node) element;
						// if selection is a node
						presentation.fsa.SingleLineNodeLabellingDialog
						.showAndLabel(ContextAdaptorHack.context,ContextAdaptorHack.context
								.getGraphModel(), node);
					} else if (element instanceof Edge) {
						Edge edge = (Edge) element;
						EdgeLabellingDialog.showDialog(ContextAdaptorHack.context, edge);
//						new EdgeCommands.CreateEventCommand(,edge).execute();
//						EdgeLabellingDialog.showDialog(ContextAdaptorHack.context,
//								edge);
						// TODO accumulate set of edits that were performed in the
						// edge
						// labelling dialog
					} else if (element instanceof GraphLabel
							&& element.getParent() instanceof Edge) {
						Edge edge = (Edge) element.getParent();
						EdgeLabellingDialog.showDialog(ContextAdaptorHack.context, edge);
//						new EdgeCommands.CreateEventCommand(ContextAdaptorHack.context,edge).execute();
//						EdgeLabellingDialog.showDialog(ContextAdaptorHack.context,
//								edge);
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
			}
			
			public void execute()
			{
				actionPerformed(null);
			}
		}
		
		/**
		 * A command that creates a reflexive edge on a node.
		 * 
		 * @author helen bretzke
		 *
		 */

		public static class SelfLoopAction extends AbstractAction {
			protected Node node;
			protected FSAGraph graph;

			public SelfLoopAction(FSAGraph graph,Node node){
				super(Hub.string("comAddSelfloop"));
				putValue(SHORT_DESCRIPTION, Hub.string("comHintAddSelfloop"));
				this.node = node;
				this.graph=graph;
			}

			public void actionPerformed(ActionEvent e){
				new GraphActions.CreateEdgeAction(graph,node,node).execute();
			}
		}
}
