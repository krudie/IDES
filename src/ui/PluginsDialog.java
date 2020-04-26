package ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import ides.api.core.Hub;
import ides.api.plugin.Plugin;
import ides.api.utilities.ContractableTextArea;
import ides.api.utilities.EscapeDialog;
import main.PluginManager;

public class PluginsDialog extends EscapeDialog {
    private static final long serialVersionUID = -8468575527444937358L;

    protected class DescriptionBox extends JPanel {
        private static final long serialVersionUID = 4118702054866937475L;

        protected class ViewLicenseButton extends JButton implements ActionListener {
            private static final long serialVersionUID = -1125956722544240377L;

            protected Plugin plugin;

            protected JDialog dialog;

            public ViewLicenseButton(Plugin plugin) {
                super(Hub.string("license"));
                this.plugin = plugin;
                addActionListener(this);
            }

            public void actionPerformed(ActionEvent e) {
                dialog = new EscapeDialog(Hub.getMainWindow(),
                        Hub.string("license") + " (" + plugin.getName() + " " + plugin.getVersion() + ")", true) {
                    private static final long serialVersionUID = 1L;

                    public void onEscapeEvent() {
                        this.dispose();
                    }
                };

                JPanel mainBox = new JPanel();
                mainBox.setLayout(new BoxLayout(mainBox, BoxLayout.Y_AXIS));
                mainBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                JTextArea license = new ContractableTextArea(plugin.getLicense());
                license.setFont(new JLabel().getFont());
                license.setEditable(false);
                license.setWrapStyleWord(true);
                license.setLineWrap(true);
                JScrollPane sp = new JScrollPane(license);
                sp.setPreferredSize(new Dimension(450, 500));
                mainBox.add(sp);

                JButton closeButton = new JButton(Hub.string("close"));
                closeButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dialog.dispose();
                    }
                });
                Box buttonBox = Box.createHorizontalBox();
                buttonBox.add(Box.createHorizontalGlue());
                buttonBox.add(closeButton);
                buttonBox.add(Box.createHorizontalGlue());
                mainBox.add(buttonBox);

                dialog.getContentPane().add(mainBox);

                dialog.pack();
                dialog.setLocation(Hub.getCenteredLocationForDialog(dialog.getSize()));
                dialog.setVisible(true);
            }
        }

        public DescriptionBox(Plugin plugin) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            Box infoBox = Box.createVerticalBox();

            Box titleBox = Box.createHorizontalBox();
            JLabel title = new JLabel(plugin.getName() + " " + plugin.getVersion());
            title.setFont(title.getFont().deriveFont(Font.BOLD));
            titleBox.add(title);
            titleBox.add(Box.createHorizontalGlue());
            infoBox.add(titleBox);

            add(Box.createRigidArea(new Dimension(0, 2)));

            Box creditBox = Box.createHorizontalBox();
            creditBox.add(new JLabel(Hub.string("credits") + ": " + plugin.getCredits()));
            creditBox.add(Box.createHorizontalGlue());
            infoBox.add(creditBox);

            infoBox.add(Box.createVerticalGlue());

            Box licenseBox = Box.createVerticalBox();
            licenseBox.add(new ViewLicenseButton(plugin));
            licenseBox.add(Box.createVerticalGlue());

            Box headBox = Box.createHorizontalBox();
            headBox.add(infoBox);
            headBox.add(licenseBox);

            add(headBox);

            add(Box.createRigidArea(new Dimension(0, 2)));

            JTextArea area = new ContractableTextArea(plugin.getDescription());
            area.setFont(new JLabel().getFont());
            area.setEditable(false);
            area.setWrapStyleWord(true);
            area.setLineWrap(true);
            Box areaBox = Box.createHorizontalBox();
            areaBox.add(area);
            areaBox.add(Box.createHorizontalGlue());
            add(areaBox);
        }
    }

    public PluginsDialog() {
        super(Hub.getMainWindow(), Hub.string("viewPluginsTitle"), true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel mainBox = new JPanel();
        mainBox.setLayout(new BoxLayout(mainBox, BoxLayout.Y_AXIS));

        Box titleBox = Box.createHorizontalBox();
        if (PluginManager.getLoadedPlugins().isEmpty()) {
            titleBox.add(new JLabel(Hub.string("viewNoPluginsDesc")));
        } else {
            titleBox.add(new JLabel(Hub.string("viewPluginsDesc")));
        }
        titleBox.add(Box.createHorizontalGlue());
        mainBox.add(titleBox);

        mainBox.add(Box.createRigidArea(new Dimension(0, 5)));

        JPanel descBox = new JPanel();
        descBox.setLayout(new BoxLayout(descBox, BoxLayout.Y_AXIS));
        descBox.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        for (Plugin plugin : PluginManager.getLoadedPlugins()) {
            descBox.add(new DescriptionBox(plugin));
            descBox.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        JScrollPane sp = new JScrollPane(descBox);
        sp.setPreferredSize(new Dimension(400, 450));
        mainBox.add(sp);

        JButton closeButton = new JButton(Hub.string("close"));
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onEscapeEvent();
            }
        });
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(closeButton);
        buttonBox.add(Box.createHorizontalGlue());
        mainBox.add(buttonBox);

        mainBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        getContentPane().add(mainBox);
        pack();
        setLocation(Hub.getCenteredLocationForDialog(getSize()));
    }

    public void onEscapeEvent() {
        dispose();
    }
}
