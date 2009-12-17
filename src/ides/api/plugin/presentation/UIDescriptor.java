package ides.api.plugin.presentation;

import ides.api.core.Hub;
import ides.api.core.UserInterface;
import ides.api.ui.ZoomControl;

import javax.swing.JMenu;
import javax.swing.JToolBar;

/**
 * The descriptor of what UI elements will be available to the user when a model
 * is activated. This descriptor is constructed by the {@link Toolset} for the
 * specific type of model.
 * 
 * @see Toolset
 * @author Lenko Grigorov
 */
public interface UIDescriptor
{
	/**
	 * Retrieve the list of {@link Presentation}s to be added to the main pane
	 * of the IDES interface when the model is activated. Each
	 * {@link Presentation} will be added in a separate tab.
	 * <p>
	 * Note: This method may be called multiple times for the same model, so new
	 * {@link Presentation}s should not be instantiated at each call.
	 * 
	 * @return the list of {@link Presentation}s to be added to the main pane of
	 *         the IDES interface
	 */
	public Presentation[] getMainPanePresentations();

	/**
	 * <b>As of IDES version 3, this method does nothing.</b>
	 * <p>
	 * Retrieve the list of {@link Presentation}s to appear on the left side of
	 * the main pane of IDES when the model is activated. Each
	 * {@link Presentation} will be added in a separate tab.
	 * <p>
	 * Note: This method may be called multiple times for the same model, so new
	 * {@link Presentation}s should not be instantiated at each call.
	 * 
	 * @return the list of {@link Presentation}s to appear on the left side of
	 *         the main pane of IDES
	 */
	public Presentation[] getLeftPanePresentations();

	/**
	 * Retrieve the list of {@link Presentation}s to appear on the right side of
	 * the main pane of IDES when the model is activated. Each
	 * {@link Presentation} will be added in a separate tab.
	 * <p>
	 * Note: This method may be called multiple times for the same model, so new
	 * {@link Presentation}s should not be instantiated at each call.
	 * 
	 * @return the list of {@link Presentation}s to appear on the right side of
	 *         the main pane of IDES
	 */
	public Presentation[] getRightPanePresentations();

	/**
	 * Retrieve the menus to be added to the IDES menu when the model is
	 * activated.
	 * <p>
	 * Note: This method may be called multiple times for the same model, so new
	 * menus should not be instantiated at each call.
	 * 
	 * @return the menus to be added to the IDES menu
	 */
	public JMenu[] getMenus();

	/**
	 * Retrieve a toolbar with icons to be added to the IDES toolbar when the
	 * model is activated.
	 * <p>
	 * Note: This method may be called multiple times for the same model, so a
	 * new toolbar should not be instantiated at each call.
	 * 
	 * @return the toolbar with icons to be added to the IDES toolbar
	 */
	public JToolBar getToolbar();

	/**
	 * Retrieve the {@link Presentation} which will be added to the status bar
	 * of IDES when the model is activated.
	 * <p>
	 * Note: This method may be called multiple times for the same model, so a
	 * new {@link Presentation} should not be instantiated at each call.
	 * 
	 * @return the {@link Presentation} which will be added to the status bar of
	 *         IDES
	 * @see Hub#getUserInterface()
	 * @see UserInterface#getStatusBar()
	 */
	public Presentation getStatusBar();

	/**
	 * Determine if at least one of the {@link Presentation}s rendering the
	 * model supports different levels of zoom. This is used by IDES to
	 * determine if the zoom control should be visible.
	 * 
	 * @return <code>true</code> if at least one of the {@link Presentation}s
	 *         rendering the model supports different levels of zoom;
	 *         <code>false</code> otherwise
	 * @see Hub#getUserInterface()
	 * @see UserInterface#getZoomControl()
	 * @see ZoomControl
	 */
	public boolean supportsZoom();
}
