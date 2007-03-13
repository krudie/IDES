package presentation;

import javax.swing.JComponent;

/**
 * The interface for a class which will render a
 * {@link ModelWrap}. The model has to be rendered
 * in the {@link JComponent} returned by
 * {@link #getGUI()}.
 * @see ModelWrap
 * @see #getGUI()
 * 
 * @author Lenko Grigorov
 */
public interface Presentation {
	
	/**
	 * Returns the {@link JComponent} in which the
	 * {@link ModelWrap} is rendered. This method
	 * can be called by IDES directly or through a
	 * {@link Toolset}. 
	 * @return the {@link JComponent} in which the
	 * {@link ModelWrap} is rendered
	 */
	public JComponent getGUI();
	
	/**
	 * Returns the {@link ModelWrap} which is rendered
	 * by this presentation.
	 * @return the {@link ModelWrap} which is rendered
	 * by this presentation
	 */
	public ModelWrap getModelWrap();
	
	/**
	 * Sets if changes in the {@link ModelWrap} have to be
	 * tracked by this presentation or not.
	 * <p> The implementation of this method is optional.
	 * Some presentations may always track changes
	 * (especially if the presentations allows modifications
	 * to the underlying {@link ModelWrap}). Some
	 * presentations may always ignore changes. 
	 * @param b if <code>true</code>, the presentation
	 * should register with the underlying {@link ModelWrap}
	 * to track changes and update itself dynamically. If
	 * <code>false</code>, the presentation should unregister
	 * itself with the underlying {@link ModelWrap} and
	 * ignore any changes to it.
	 */
	public void setTrackModel(boolean b);
}
