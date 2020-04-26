package ides.api.utilities;

import java.awt.Dimension;

import javax.swing.JTextArea;

/**
 * This text area can be used inside scroll panes that may be sized down. The
 * text area will automatically get more narrow as needed.
 * 
 * @author Lenko Grigorov
 */
public class ContractableTextArea extends JTextArea {
    private static final long serialVersionUID = 6803315264387461529L;

    public ContractableTextArea(String s) {
        super(s);
    }

    public Dimension getPreferredSize() {
        return new Dimension(10, super.getPreferredSize().height);
    }
}
