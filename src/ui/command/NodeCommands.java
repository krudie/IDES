/**
 * 
 */
package ui.command;

import javax.swing.undo.UndoableEdit;

import main.IDESWorkspace;
import model.DESElement;
import model.fsa.ver1.State;

import org.pietschy.command.ToggleVetoException;
import org.pietschy.command.undo.UndoableActionCommand;

import presentation.fsa.Node;

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
	public static class SetMarkedCommand extends org.pietschy.command.ToggleCommand {
		private Node node;
		private boolean previousValue; // for undoable edit		

		public SetMarkedCommand(){
			super("set.marked.command");
		}	
		
		@Override
		protected void handleSelection(boolean arg0) throws ToggleVetoException {
			// FIXME do this in GraphModel and split this command into 
			// mark.node.command and initial.node.command
			previousValue = !arg0;
			IDESWorkspace.instance().getActiveGraphModel().setMarked(node, arg0); 			
		}		
		
		public void setNode(Node node){
			this.node = node;
		}
	}
		
	public static class SetInitialCommand extends org.pietschy.command.ToggleCommand {

		private Node node;				
		private boolean previousValue;		
		
		public SetInitialCommand(){
			super("set.initial.command");
		}

		/* (non-Javadoc)
		 * @see org.pietschy.command.ToggleCommand#handleSelection(boolean)
		 */
		@Override
		protected void handleSelection(boolean arg0) throws ToggleVetoException {
			previousValue = !arg0;
			IDESWorkspace.instance().getActiveGraphModel().setInitial(node, arg0);
		}
				
		public void setNode(Node node){
			this.node = node;
		}
	}
	
	public static class SelfLoopCommand extends org.pietschy.command.undo.UndoableActionCommand {

		private Node node;
		
		public SelfLoopCommand(){
			super("set.selfloop.command");
		}
		
		/* (non-Javadoc)
		 * @see org.pietschy.command.ToggleCommand#handleSelection(boolean)
		 */
		@Override
		protected UndoableEdit performEdit() {
			
			IDESWorkspace.instance().getActiveGraphModel().setSelfLoop(node, true);
			return null;
		}
	
		public void setNode(Node node){
			this.node = node;
		}
	}
}
