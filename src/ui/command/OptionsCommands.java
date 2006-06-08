package ui.command;

import org.pietschy.command.ActionCommand;

import ui.SettingsWindow;

public class OptionsCommands {

	public static class MoreOptionsCommand extends ActionCommand {

		/**
		 * Default constructor; handy for exporting this command for group setup.
		 *
		 */
		public MoreOptionsCommand(){
			super("moreoptions.command");
		}
		
		public void handleExecute() {
			new SettingsWindow();		
		}
	}
}
