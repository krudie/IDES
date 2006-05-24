package ui.command;

import org.pietschy.command.ActionCommand;

/* Does copy need to be undoable?
 * 
 */
public class CopyCommand extends ActionCommand {

	private Object element; 
	private Object context;
	private Object buffer; // ???
	
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
