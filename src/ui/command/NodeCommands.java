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
 * This class holds static classes regarding actions over nodes.
 * Normally, when the user wants to explicitly execute an action (e.g.: by 
 * clicking in a button), an AbstractAction will be encapsulating an UndoableAction
 * that knows how to undo/redo the action to be done.
 * 
  * So everytime an action that can be undone is started, two steps will be executed:
 * 1- One <code>AbstractAction</code> executes an <action>UndoableAction</action> that can 
 * redo\/undo the result of the action.
 * 2- The <code>AbstractAction</code> then, notifies the UndoManager
 * in the CommandManager about a performed action.
 * 
 * One of the reasons for making the UndoableAction being called by an 
 * AbstractAction is the fact that by doing this, one AbstractAction could
 * encapsulate several UndoableActions making then a "composite" UndoableAction.
 * 
 * Also it is simpler (in my opinion (Christian)), to have to simpler classes one to
 * create an UndoableAction and talk to the CommandManager, and other which is the 
 * UndoableAction itself.
 * Having everything in just one class would make this class be too big and more 
 * difficult to write. 
 *
 * @author Christian Silvano
 *
 */
public class NodeCommands {

	/**
	 * A command that sets the value of a boolean attribute for a DES element.
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

	/**
	 * A command that sets the value of a boolean attribute for a DES element.
	 * 
	 * @author helen bretzke
	 *
	 */
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

	/**
	 * A command that creates a reflexive edge on a node.
	 * 
	 * @author helen bretzke
	 *
	 */

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

	/**
	 * An action that can create and (un)create a reflexive edge over a node.
	 * 
	 * @author Christian Silvano
	 *
	 */
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
			//Creates an edge using <code>node</code> as the source and
			//destination for it.
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
	
	/**
	 * An action that can set and (un)set a node as marked.
	 * 
	 * @author Christian Silvano
	 *
	 */
	private static class UndoableSetMarked extends AbstractUndoableEdit {
		CircleNode node;

		public UndoableSetMarked(CircleNode node) {
			this.node = node;
		}

		public void undo() throws CannotRedoException {
			//Toggles the attribute "marked" on the node
			node.getGraph().setMarked(node, !node.getState().isMarked()); 	
		}

		public void redo() throws CannotRedoException {
			//Toggles the attribute "marked" on the node
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

	/**
	 * An action that can set and (un)set a node as initial.
	 * 
	 * @author Christian Silvano
	 *
	 */
	private static class UndoableSetInitial extends AbstractUndoableEdit {
		CircleNode node;

		public UndoableSetInitial(CircleNode node) {
			this.node = node;
		}

		public void undo() throws CannotRedoException {
			//Toggles the attribute "initial" on the node
			node.getGraph().setInitial(node, !node.getState().isInitial()); 	
		}

		public void redo() throws CannotRedoException {
			//Toggles the attribute "initial" on the node
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
