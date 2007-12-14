/**
 * 
 */
package ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import main.Hub;
import ui.AboutDialog;

/**
 * @author lenko
 */
public class HelpActions
{

	public static class AboutAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -2504773574491114360L;

		public AboutAction()
		{
			super(Hub.string("comAbout"));
			putValue(SHORT_DESCRIPTION, Hub.string("comHintAbout"));
		}

		public void actionPerformed(ActionEvent e)
		{
			AboutDialog about = new AboutDialog();
			about.setVisible(true);
		}
	}

}
