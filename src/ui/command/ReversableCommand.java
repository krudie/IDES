package ui.command;


public interface ReversableCommand extends Command {

	public void unexecute();
	
}
