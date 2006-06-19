package ui.command;

import javax.swing.undo.UndoableEdit;

import main.IDESWorkspace;
import model.DESElement;

import org.pietschy.command.ActionCommand;
import org.pietschy.command.ToggleVetoException;
import org.pietschy.command.undo.UndoableActionCommand;

public class EditCommands {

	/* Does copy need to be undoable?
	 * 
	 */
	public static class CopyCommand extends ActionCommand {

		private Object element; 
		private Object context;
		private Object buffer; // ???
		
		/**
		 * Default constructor; handy for exporting this command for group setup.
		 *
		 */
		public CopyCommand(){
			super("copy.command");
		}
		/**
		 * Creates a command that, when executed, will copy 
		 * <code>element</code> from the given context.
		 * 
		 * The given element could be a group of elements.
		 * 
		 * @param element
		 * @param context
		 */
		public CopyCommand(Object element, Object context) {
			super("copy.command");
			this.element = element;
			this.context = context;
		}
		
		public void handleExecute() {
			// TODO Auto-generated method stub
			// context.remove(element);
			System.out.println("Copy acts as its own buffer, \n but where should paste look for the buffer?");		
		}
	}

	
	public static class PasteCommand extends UndoableActionCommand {

		public PasteCommand(){
			super("paste.command");
		}
		
		@Override
		protected UndoableEdit performEdit() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	
	
	public static class CutCommand extends UndoableActionCommand {

		private Object element; 
		private Object context;
		
		/**
		 * Default constructor; handy for exporting this command for group setup.
		 *
		 */
		public CutCommand(){
			super("cut.command");
		}
		/**
		 * Creates a command that, when executed, will cut 
		 * <code>element</code> from the given context.
		 * 
		 * @param element
		 * @param context
		 */
		public CutCommand(Object element, Object context) {
			this.element = element;
			this.context = context;
		}

		@Override
		protected UndoableEdit performEdit() {
			// TODO Auto-generated method stub
			System.out.println("Cut " + element + " from the " + context + ".");
			return null;
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
		
		private Object element;	 // TODO decide on type, GraphElement composite type?
		private Object context;  // Does this need to be stored?
		
		/**
		 * Default constructor; handy for exporting this command for group setup.
		 *
		 */
		public DeleteCommand(){
			super("delete.command");
		}
		
		/**
		 * Creates a command that, when executed, will cut 
		 * <code>element</code> from the given context.
		 * 
		 * @param element
		 * @param context
		 */
		public DeleteCommand(Object element, Object context) {
			this.element = element;
			this.context = context;
		}		

		@Override
		protected UndoableEdit performEdit() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	/**
	 * A command that sets the value of a boolean attribute for a DES element.
	 * 
	 * TODO figure out how to make this an undoable edit.
	 * 
	 * @author helen bretzke
	 *
	 */
	public static class SetBooleanAttributeCommand extends org.pietschy.command.ToggleCommand {

		private DESElement element;
		private String attributeName;
		private boolean previousValue; // for undoable edit		
		
		public SetBooleanAttributeCommand(DESElement element, String attributeName) {
			super("set.boolean.attribute.command");			
			this.element = element;
			this.attributeName = attributeName;					
		}

		public SetBooleanAttributeCommand(){
			super("set.boolean.attribute.command");
		}	

		public void setElement(DESElement element){
			this.element = element;
		}
		
		public void setAttributeName(String name){
			attributeName = name;
		}		
		
		@Override
		protected void handleSelection(boolean arg0) throws ToggleVetoException {
			// FIXME do this in GraphModel and split this command into 
			// mark.node.command and initial.node.command
			
			previousValue = Boolean.parseBoolean(element.get(attributeName));
			element.set(attributeName, Boolean.toString(arg0));
			
			// FIXME
//			node.update();
//			view.repaint();
//			view.getGraphModel().notifyAllSubscribers(); 
			
			IDESWorkspace.instance().getActiveGraphModel().notifyAllSubscribers(); 			
		}	
	}
		
	public static class SetAttributeCommand extends UndoableActionCommand {

		private DESElement element;
		private String attributeName;
		private String previousValue;
		private String value;
		
		public SetAttributeCommand(DESElement element, String attributeName, String value) {
			super("set.attribute.command");			
			this.element = element;
			this.attributeName = attributeName;
			this.value = value;			
		}

		public SetAttributeCommand(){
			super("set.attribute.command");
		}
		
		@Override
		protected UndoableEdit performEdit() {
			// TODO save previous value and store in undoable edit
			previousValue = element.get(attributeName);
			element.set(attributeName, value);
			IDESWorkspace.instance().getActiveGraphModel().notifyAllSubscribers();
			// TODO return undoable edit object
			return null;
		}		
	}
}