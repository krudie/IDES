/**
 * 
 */
package ui.command;

import main.Hub;
import main.IDESWorkspace;
import model.fsa.ver1.Automaton;

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
