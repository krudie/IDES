package ui;

/**
 * Provides a finite history of commands with undo and redo operations. 
 * 
 * @author helen
 *
 */
public class CommandHistory {

	private Command[] history;
	private int lastCommand = -1;
	
	/**
	 * Create a command history that remembers <code>n</code> commands.
	 * 
	 * @param size
	 */
	public CommandHistory(int n) {
		history = new Command[n];
	}
	
	public void undo() {
		// nothing to do
		if(lastCommand == -1) return;
		
	}
	
	public void redo() {
		// nothing to do
		if(lastCommand == -1) return;
		
	}
	
}
