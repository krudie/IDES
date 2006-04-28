package ui.command;

import java.util.ArrayList;

import ui.command.Command;

/**
 * Provides a finite history of commands with undo and redo operations. 
 * FIXME Note that each command is associated with a context.  
 * Only undo or redo a command if the UI is currently in the correct context.
 * 
 * @author helen
 *
 */
public class CommandHistory {

	private static final int DEFAULT_COMMAND_HISTORY_LENGTH = 20;
	
	// Use a vector and keep track of the size.
	private ArrayList history;
	private int maxLength;
	private int lastCommand = -1;
	private boolean undone = false;
	
	/**
	 * Create a command history that remembers <code>DEFAULT_COMMAND_HISTORY_LENGTH</code> commands.	
	 */
	public CommandHistory() {
		maxLength = DEFAULT_COMMAND_HISTORY_LENGTH;
		history = new ArrayList(DEFAULT_COMMAND_HISTORY_LENGTH);
	}
	
	
	/**
	 * Create a command history that remembers <code>n</code> commands.
	 * 
	 * @param size
	 */
	public CommandHistory(int n) {
		maxLength = n;
		history = new ArrayList(n);
	}
	
	public void add(Command c) {		
		history.add(++lastCommand, c);
		if(lastCommand >= maxLength){
			lastCommand = maxLength - 1;
			history.remove(0);
		}
	}
	
	public void undo() {
		// nothing to do
		if(lastCommand == -1) return;		
		try{
			ReversableCommand rc = (ReversableCommand)history.get(lastCommand);
			rc.unexecute();
			history.add(lastCommand, new UndoCommand(rc));
			lastCommand--;
		}catch(ClassCastException e){
			// command is not reversible so do nothing.
		}
	}
	
	/**
	 * If the last command was undo, then executes the command that was undone.
	 * otherwise, repeats the last command.
	 */
	public void redo() {
		// nothing to do
		if(lastCommand == -1) return;
		if(undone){	
			lastCommand++;
			Command undone = ((UndoCommand)history.get(lastCommand)).getCommand();
			undone.execute();
			history.add(lastCommand, undone); // replace undo with its subcommand
		}else{
			((Command)history.get(lastCommand)).execute();
		}
	}	
}
