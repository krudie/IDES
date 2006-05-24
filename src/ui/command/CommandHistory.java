package ui.command;

import java.util.ArrayList;

import org.pietschy.command.ActionCommand;

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
	
	// Circular STACK abstract data type: Use an implementation from CSC148.
	// Never full: overwrites the oldest commands
	
	private ArrayList history;
	private int maxLength;
	private int head = -1;
	private int tail = -1;	
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
	
	public void add(ActionCommand c) {		
		history.add(++tail, c);
		if(tail >= maxLength){
			tail = maxLength - 1;
			history.remove(0);
		}
	}
	
	public void undo() {
		// nothing to do
		if(tail == -1) return;		
		try{
			ReversableCommand rc = (ReversableCommand)history.get(tail);
			rc.unexecute();
			history.add(tail, new UndoCommand(rc));
			tail--;
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
		if(tail == -1) return;
		if(undone){	
			tail++;
			Command undone = ((UndoCommand)history.get(tail)).getCommand();
			undone.execute();
			history.add(tail, undone); // replace undo with its subcommand
		}else{
			((Command)history.get(tail)).execute();
		}
	}	
}
