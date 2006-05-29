package ui.command;

import org.pietschy.command.ActionCommand;


public class MoveCommand extends ActionCommand {

	public MoveCommand() {
		super("move.command");
	}
	
	@Override
	protected void handleExecute() {
		// TODO Auto-generated method stub
		System.out.println("Move command executed.");
	}

}
