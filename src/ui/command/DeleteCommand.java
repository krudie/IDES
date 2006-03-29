package ui.command;

/**
 * Represent a user issued command to delete an element of the graph.
 * ??? What about deleting elements of a text label? 
 * 
 * @author helen
 *
 */
public class DeleteCommand implements ReversableCommand {
	
	private Object element;	 // TODO decide on type, GraphElement composite type?
	private Object context;  // Does this need to be stored?
	
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
	
	public void execute() {
		// TODO 
		System.out.println("Delete");
		// place element in the restore buffer
	}

	public void unexecute() {
		// TODO 
		System.out.println("Restore");
	}
	
}
