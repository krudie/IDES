package ui.command;

public class CutCommand implements ReversableCommand {

	private Object element; 
	private Object context;
	
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

	public void execute() {
		// TODO Auto-generated method stub
		// context.remove(element);
		System.out.println("Cut " + element + " from the " + context + ".");
	}

	public void unexecute() {
		// TODO Auto-generated method stub
		// context.add(element);
		System.out.println("Replaced " + element + " into " + context + ".");
	}
}
