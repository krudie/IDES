/**
 * 
 */
package ui.command;

import main.Hub;
import main.Workspace;
import model.fsa.ver2_1.Automaton;

import org.pietschy.command.ActionCommand;

import ui.AboutDialog;

/**
 * @author lenko
 *
 */
public class HelpCommands {

	public static class AboutCommand extends ActionCommand {
		
		public AboutCommand(){
			super("about.command");
		}
		
		@Override
		protected void handleExecute() {
			AboutDialog about = new AboutDialog();
			about.setVisible(true);
		}	
	}

}
