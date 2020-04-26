package ides.api.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JComboBox;

import ides.api.core.Hub;
import ides.api.plugin.presentation.Presentation;
import ides.api.plugin.presentation.ZoomablePresentation;

/**
 * The control for the zoom level when rendering models (to be used by
 * {@link Presentation}s which support zoom).
 * 
 * @author lenko
 */
public class ZoomControl extends JComboBox implements ActionListener {
    private static final long serialVersionUID = 2215751287682688097L;

    /**
     * The zoom level.
     */
    protected int zoomValue = 100;

    /**
     * Zoom level presets.
     */
    protected static final String[] presets = { "10 %", "25 %", "50 %", "75 %", "100 %", "150 %", "200 %" };

    /**
     * Instantiate and set up the zoom control.
     */
    public ZoomControl() {
        super(presets);
        setEditable(true);
        setSelectedIndex(4);
        addActionListener(this);
        setMaximumSize(new Dimension(90, getPreferredSize().height));
        setPreferredSize(new Dimension(90, getPreferredSize().height));
    }

    /**
     * Response to the modification of the zoom level through the GUI.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String value = getEditor().getItem().toString();
        value = value.split(" ")[0];
        if (value.endsWith("%")) {
            value = value.substring(0, value.length() - 1);
        }
        try {
            int newZoomValue = (int) Float.parseFloat(value);
            if (newZoomValue <= 0) {
                newZoomValue = 100;
            }
            commitZoom(newZoomValue);
        } catch (NumberFormatException ex) {
            commitZoom(zoomValue);
        }
    }

    /**
     * Retrieve the current zoom level.
     * 
     * @return the current zoom level
     */
    public float getZoom() {
        return zoomValue / 100F;
    }

    /**
     * Set the zoom level.
     * 
     * @param z the new zoom level
     */
    public void setZoom(float z) {
        if (z < 0) {
            z = 0;
        }
        commitZoom((int) (z * 100));
    }

    /**
     * Perform the zoom level change and notify the workspace.
     * 
     * @param z the new zoom level
     */
    private void commitZoom(int z) {
        if (z != zoomValue) {
            zoomValue = z;

            Collection<Presentation> presentations = Hub.getWorkspace().getPresentations();
            for (Iterator<Presentation> i = presentations.iterator(); i.hasNext();) {
                Presentation curr = i.next();
                if (curr instanceof ZoomablePresentation) {
                    ((ZoomablePresentation) curr).setScaleFactor(getZoom());
                }
            }

            // GraphDrawingView gdv = FSAToolset.getCurrentBoard();
            // if (gdv != null)
            // {
            // gdv.setScaleFactor(getZoom());
            // }
            // // ((MainWindow)Hub.getMainWindow()).getDrawingBoard().update();
            Hub.getWorkspace().fireRepaintRequired();
        }
        setSelectedItem("" + zoomValue + " %");
    }
}
