/**
 * 
 */
package ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;

import ides.api.core.Hub;
import ides.api.utilities.GeneralUtils;
import ui.AboutDialog;
import ui.HelpDirLister;
import ui.PluginsDialog;

/**
 * @author lenko
 */
public class HelpActions {

    public static class PluginsAction extends AbstractAction {
        private static final long serialVersionUID = -2504773574491114360L;

        public PluginsAction() {
            super(Hub.string("comViewPlugins"));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintViewPlugins"));
        }

        public void actionPerformed(ActionEvent e) {
            PluginsDialog plugins = new PluginsDialog();
            plugins.setVisible(true);
        }
    }

    public static class AboutAction extends AbstractAction {
        private static final long serialVersionUID = -2504773574491114360L;

        public AboutAction() {
            super(Hub.string("comAbout"));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintAbout"));
        }

        public void actionPerformed(ActionEvent e) {
            AboutDialog about = new AboutDialog();
            about.setVisible(true);
        }
    }

    public static class HelpTopics extends JMenu {
        private static final long serialVersionUID = 5485831308098748914L;

        public HelpTopics() {
            super(Hub.string("comHelpTopics"));
            for (String topic : HelpDirLister.instance().keySet()) {
                add(new HelpTopic(topic));
            }
        }
    }

    public static class HelpTopic extends AbstractAction {
        private static final long serialVersionUID = -303139850666828651L;

        public HelpTopic(String topic) {
            super(topic);
            putValue(SHORT_DESCRIPTION, Hub.string("comHintHelpTopic") + topic);
        }

        public void actionPerformed(ActionEvent e) {
            GeneralUtils.launchBrowser(HelpDirLister.instance().get(getValue(NAME)));
        }

    }

}
