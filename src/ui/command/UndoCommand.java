package ui.command;

/**
 * @deprecated
 * Using gui-commands library instead of reinventing the wheel.
 * 
 * @author Helen Bretzke
 *
 */
public class UndoCommand implements Command {

	ReversableCommand rc;
		
	public UndoCommand(ReversableCommand rc){
		this.rc = rc;
	}
	
	// This is not being used.
	public void execute() {
		rc.unexecute();
	}
	
	public ReversableCommand getCommand() {
		return rc;
	}
}
