package ides.api.plugin.presentation;

import javax.swing.JComponent;

import ides.api.plugin.model.DESModel;

/**
 * The interface for a class which will render a {@link DESModel}. The model has
 * to be rendered in the {@link JComponent} returned by {@link #getGUI()}.
 * 
 * @see #getGUI()
 * @author Lenko Grigorov
 */
public interface Presentation {

    /**
     * Returns the title of this presentation.
     * 
     * @return the title of this presentation
     */
    public String getName();

    /**
     * Returns the {@link JComponent} in which the {@link DESModel} is rendered.
     * This method can be called by IDES directly or through a {@link Toolset}.
     * 
     * @return the {@link JComponent} in which the {@link DESModel} is rendered
     */
    public JComponent getGUI();

    /**
     * Retrieve the model rendered in the presentation.
     * 
     * @return the model rendered in the presentation
     */
    public DESModel getModel();

    /**
     * Sets if changes in the {@link DESModel} have to be tracked by this
     * presentation or not.
     * <p>
     * The implementation of this method is optional. Some presentations may always
     * track changes (especially if the presentations allows modifications to the
     * underlying {@link DESModel}). Some presentations may always ignore changes.
     * 
     * @param b if <code>true</code>, the presentation should register with the
     *          underlying {@link DESModel} to track changes and update itself
     *          dynamically. If <code>false</code>, the presentation should
     *          unregister itself with the underlying {@link DESModel} and ignore
     *          any changes to it.
     */
    public void setTrackModel(boolean b);

    /**
     * Detach from the underlying {@link DESModel} and releases any resources used
     * to present it. For example, the presentation should unsubscribe from
     * listening to changes in the {@link DESModel}.
     * <p>
     * Once this method is called, the behavior of the presentation should no longer
     * be considered deterministic.
     */
    public void release();

    /**
     * Forces a repaint of the presentation.
     */
    public void forceRepaint();
}
