/**
 * 
 */
package ui.command;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

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

	public static class AboutCommand extends AbstractAction {
		
		public AboutCommand(){
			super(Hub.string("aboutIDES"));
		}
		
		public void actionPerformed(ActionEvent e) {
			AboutDialog about = new AboutDialog();
			about.setVisible(true);
		}	
	}

}
