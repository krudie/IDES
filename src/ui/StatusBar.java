/**
 * 
 */
package ui;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import ides.api.core.Hub;

/**
 * @author Lenko Grigorov
 */
public class StatusBar extends JPanel {
    private static final long serialVersionUID = 8036410231866758994L;

    /**
     * label to display the name of the current model with state and transition
     * counts
     */
    protected static final JLabel emptyWorkspaceLabel = new JLabel(Hub.string("noModelOpen"));

    Box guiBox = Box.createHorizontalBox();

    public StatusBar() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(new BevelBorder(BevelBorder.LOWERED));
        add(Box.createRigidArea(new Dimension(5, 0)));
        add(guiBox);
        add(Box.createHorizontalGlue());
    }

    public void setContent(JComponent info) {
        guiBox.removeAll();
        if (info == null) {
            guiBox.add(emptyWorkspaceLabel);
        } else {
            guiBox.add(info);
        }
    }
}
