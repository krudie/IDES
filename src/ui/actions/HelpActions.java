/**
 * 
 */
package ui.actions;

import ides.api.core.Hub;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ui.AboutDialog;
import ui.PluginsDialog;

/**
 * @author lenko
 */
public class HelpActions
{

	public static class PluginsAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -2504773574491114360L;

		public PluginsAction()
		{
			super(Hub.string("comViewPlugins"));
			putValue(SHORT_DESCRIPTION, Hub.string("comHintViewPlugins"));
		}

		public void actionPerformed(ActionEvent e)
		{
			PluginsDialog plugins = new PluginsDialog();
			plugins.setVisible(true);
		}
	}

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
