package ui.command;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.Hub;

import presentation.Presentation;
import presentation.fsa.FSAToolset;
import presentation.fsa.GraphDrawingView;

import services.latex.LatexManager;
import ui.MainWindow;
import ui.OptionsWindow;

/**
 * The class with the commands from the "Options" menu.
 *  
 * @author Lenko Grigorov
 */
public class OptionsCommands {

	/**
	 * The class for the "More options..." menu item.
	 * 
	 * @author Lenko Grigorov
	 */
	public static class MoreOptionsAction extends AbstractAction {
		
		/**
		 * Default constructor; handy for exporting this command for group setup.
		 */
		public MoreOptionsAction(){
			super(Hub.string("comMoreOptions"));
			putValue(SHORT_DESCRIPTION, Hub.string("comHintMoreOptions"));
		}
		
		/**
		 * Executes the command.
		 */
		public void actionPerformed(ActionEvent e) {
			new OptionsWindow();		
		}
	}

}
