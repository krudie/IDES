package presentation;

import javax.swing.JComponent;

import pluggable.ui.Toolset;

/**
 * The interface for a class which will render a {@link LayoutShell}. The model
 * has to be rendered in the {@link JComponent} returned by {@link #getGUI()}.
 * 
 * @see LayoutShell
 * @see #getGUI()
 * @author Lenko Grigorov
 */
public interface Presentation
{

	/**
	 * Returns the title of this presentation.
	 * 
	 * @return the title of this presentation
	 */
	public String getName();

	/**
	 * Returns the {@link JComponent} in which the {@link LayoutShell} is
	 * rendered. This method can be called by IDES directly or through a
	 * {@link Toolset}.
	 * 
	 * @return the {@link JComponent} in which the {@link LayoutShell} is
	 *         rendered
	 */
	public JComponent getGUI();

	/**
	 * Returns the {@link LayoutShell} which is rendered by this presentation.
	 * 
	 * @return the {@link LayoutShell} which is rendered by this presentation
	 */
	public LayoutShell getLayoutShell();

	/**
	 * Sets if changes in the {@link LayoutShell} have to be tracked by this
	 * presentation or not.
	 * <p>
	 * The implementation of this method is optional. Some presentations may
	 * always track changes (especially if the presentations allows
	 * modifications to the underlying {@link LayoutShell}). Some presentations
	 * may always ignore changes.
	 * 
	 * @param b
	 *            if <code>true</code>, the presentation should register with
	 *            the underlying {@link LayoutShell} to track changes and update
	 *            itself dynamically. If <code>false</code>, the presentation
	 *            should unregister itself with the underlying
	 *            {@link LayoutShell} and ignore any changes to it.
	 */
	public void setTrackModel(boolean b);

	/**
	 * Detach from the underlying {@link LayoutShell} and releases any resources
	 * used to present it. For example, the presentation should unsubscribe from
	 * listening to changes in the {@link LayoutShell}.
	 * <p>
	 * Once this method is called, the behavior of the presentation should no
	 * longer be considered deterministic.
	 */
	public void release();

	/**
	 * Forces a repaint of the presentation.
	 */
	public void forceRepaint();
}
