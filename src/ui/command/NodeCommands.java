/**
 * 
 */
package ui.command;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoableEdit;

import main.Hub;
import main.Workspace;
import model.DESElement;
import model.fsa.ver2_1.State;

import org.pietschy.command.ToggleVetoException;
import org.pietschy.command.undo.UndoableActionCommand;

import presentation.fsa.BezierEdge;
import presentation.fsa.CircleNode;
import presentation.fsa.ContextAdaptorHack;
import presentation.fsa.FSAGraph;
import presentation.fsa.GraphDrawingView;
import presentation.fsa.GraphElement;
import presentation.fsa.SelectionGroup;

/**
 * @author Helen Bretzke
 *
 */
public class NodeCommands {

	/**
	 * A command that sets the value of a boolean attribute for a DES element.
	 * 
	 * TODO figure out how to make this an undoable edit.
	 * 
	 * @author helen bretzke
	 *
	 */
	public static class SetMarkedAction extends AbstractAction {
		private CircleNode node;

		public SetMarkedAction(CircleNode node){
			super("Marked");
			this.node = node;
		}	

		public void actionPerformed(ActionEvent e){
			UndoableSetMarked action = new UndoableSetMarked(node);
			//perform the action
			action.redo();
			// notify the listeners
			CommandManager_new.getInstance().undoSupport.postEdit(action);		
		}		

	}

	public static class SetInitialAction extends AbstractAction {

		private CircleNode node;				

		public SetInitialAction(CircleNode node){
			super("Initial");
			this.node = node;
		}

		public void actionPerformed(ActionEvent e) 
		{
			UndoableSetInitial action = new UndoableSetInitial(node);
			//perform the action
			action.redo();
			// notify the listeners
			CommandManager_new.getInstance().undoSupport.postEdit(action);		
		}

	}

	public static class SelfLoopAction extends AbstractAction {

		private CircleNode node;

		public SelfLoopAction(CircleNode node){
			super("Add self-loop");
			this.node = node;
		}

		public void actionPerformed(ActionEvent e){
			UndoableSelfLoop action = new UndoableSelfLoop(node);
			//perform the action
			action.redo();
			// notify the listeners
			CommandManager_new.getInstance().undoSupport.postEdit(action);
		}
	}

	private static class UndoableSelfLoop extends AbstractUndoableEdit {
		CircleNode node;
		BezierEdge edge;

		public UndoableSelfLoop(CircleNode node) {
			this.node = node;
		}

		public void undo() throws CannotRedoException {
			if(edge != null)
			{
				node.getGraph().delete(edge);
			}
		}

		public void redo() throws CannotRedoException {
			edge = node.getGraph().createEdge(node, node);
		}

		public boolean canUndo() {
			return true;
		}

		public boolean canRedo() {
			return true;
		}

		public String getPresentationName() {
			return Hub.string("createSelfLoop");
		}

	}
	
	private static class UndoableSetMarked extends AbstractUndoableEdit {
		CircleNode node;

		public UndoableSetMarked(CircleNode node) {
			this.node = node;
		}

		public void undo() throws CannotRedoException {
			node.getGraph().setMarked(node, !node.getState().isMarked()); 	
		}

		public void redo() throws CannotRedoException {
			node.getGraph().setMarked(node, !node.getState().isMarked()); 	
		}

		public boolean canUndo() {
			return true;
		}

		public boolean canRedo() {
			return true;
		}

		public String getPresentationName() {
			return Hub.string("setMarked");
		}

	}

	private static class UndoableSetInitial extends AbstractUndoableEdit {
		CircleNode node;

		public UndoableSetInitial(CircleNode node) {
			this.node = node;
		}

		public void undo() throws CannotRedoException {
			node.getGraph().setInitial(node, !node.getState().isInitial()); 	
		}

		public void redo() throws CannotRedoException {
			node.getGraph().setInitial(node, !node.getState().isInitial()); 	
		}

		public boolean canUndo() {
			return true;
		}

		public boolean canRedo() {
			return true;
		}

		public String getPresentationName() {
			return Hub.string("setInitial");
		}

	}

}
