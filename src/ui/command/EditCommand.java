package ui.command;

import org.pietschy.command.ActionCommand;

/**
 * A command to set the current drawing mode to editing mode. 
 * While in editing mode, user may select graph objects in the
 * GraphDrawingView for deleting, copying, pasting and moving.
 * 
 * @author Helen
 *
 */
public class EditCommand extends ActionCommand {

	public EditCommand(){
		super("edit.command");
	}
	@Override
	protected void handleExecute() {
		// TODO Auto-generated method stub
		System.out.println("Edit command executed (select for deletion, copy, paste and move).");
	}

}
