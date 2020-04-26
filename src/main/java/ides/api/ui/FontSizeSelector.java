package ides.api.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JComboBox;

import ides.api.core.Hub;
import ides.api.plugin.presentation.GlobalFontSizePresentation;
import ides.api.plugin.presentation.Presentation;
import services.latex.LatexMessages;

/**
 * The control for the font size when rendering models (to be used by
 * {@link Presentation}s which support different font sizes). Modelled after
 * ZoomControl.
 * 
 * @author Valerie Sugarman
 */
public class FontSizeSelector extends JComboBox implements ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = -6949888001861287432L;

    /**
     * The font size
     */
    protected int fontSize;

    /**
     * Font size presets.
     */
    protected static final String[] presets = { "8", "10", "12", "14", "16", "20", "24" };

    /**
     * Instantiate and set up the font selector.
     */
    public FontSizeSelector() {
        super(presets);
        setEditable(true);
        setSelectedIndex(2);
        addActionListener(this);
        setMaximumSize(new Dimension(50, getPreferredSize().height));
        setPreferredSize(new Dimension(50, getPreferredSize().height));
        setMinimumSize(new Dimension(50, getPreferredSize().height));
    }

    /**
     * Response to the modification of the font size through the GUI.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String value = getEditor().getItem().toString();
        try {
            int newSize = (int) Float.parseFloat(value);
            if (newSize <= 0) {
                newSize = (int) fontSize;
            } else if (newSize > 25 && Hub.getLatexManager().isLatexEnabled()) {
                // don't restrict the user
                // -- Lenko
                // newSize = 25;
                LatexMessages.fontSizeTooBig();
            }
            commitFontSize(newSize);
        } catch (NumberFormatException ex) {
            commitFontSize(fontSize);
        }
    }

    /**
     * Retrieve the current font size.
     * 
     * @return the current font size
     */
    public float getFontSize() {
        return fontSize;
    }

    /**
     * Set the font size.
     * 
     * @param s the new font size.
     */
    public void setFontSize(float s) {
        if (s < 0) {
            s = fontSize;
        }
        commitFontSize(s);
    }

    /**
     * Perform the font size change and notify the workspace.
     * 
     * @param z the new font size
     */
    private void commitFontSize(float s) {
        if (s != fontSize) {
            fontSize = (int) s;
            Collection<Presentation> presentations = Hub.getWorkspace().getPresentations();
            for (Iterator<Presentation> i = presentations.iterator(); i.hasNext();) {
                Presentation curr = i.next();
                if (curr instanceof GlobalFontSizePresentation) {
                    ((GlobalFontSizePresentation) curr).setFontSize(getFontSize());
                }
            }
            Hub.getWorkspace().fireRepaintRequired();
        }
        if (!getEditor().getItem().toString().equals("" + fontSize)) {
            setSelectedItem(fontSize);
        }
    }

}
