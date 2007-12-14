package pluggable.ui;

import model.DESModel;
import presentation.LayoutShell;
import presentation.Presentation;
import presentation.PresentationManager;

/**
 * For a given type of {@link DESModel}, an implementation of the interface
 * will provide the required GUI elements. IDES will display these GUI elements
 * when a model of the type is activated by the user.
 * <p>
 * A toolset is also used to wrap raw {@link DESModel}s into
 * {@link LayoutShell}s so that they can be visualized.
 * <p>
 * To handle a given {@link DESModel} type, one needs to register the toolset
 * with {@link PresentationManager#registerToolset(Class, Toolset)}. Then, IDES
 * will use {@link PresentationManager#getToolset(Class)} to get the toolset
 * when needed.
 * 
 * @see PresentationManager#registerToolset(Class, Toolset)
 * @see #wrapModel(DESModel)
 * @see LayoutShell
 * @author Lenko Grigorov
 */
public interface Toolset
{

	/**
	 * Returns a descriptor of the GUI elements which will be used to display a
	 * {@link LayoutShell}.
	 * 
	 * @param mw
	 *            the {@link LayoutShell} to be displayed
	 * @return the descriptor of the GUI elements to be used
	 * @see UIDescriptor
	 */
	public UIDescriptor getUIElements(LayoutShell mw);

	/**
	 * Returns a {@link Presentation} element with a thumbnail view of the given
	 * {@link LayoutShell}.
	 * 
	 * @param mw
	 *            {@link LayoutShell} for which to return a thumbnail
	 * @param width
	 *            the desired width of the thumbnail in pixels
	 * @param height
	 *            the desired height of the thumbnail in pixels
	 * @return a {@link Presentation} element with a thumbnail view of the
	 *         {@link LayoutShell}
	 * @throws UnsupportedModelException
	 *             when the toolset does not support the type of the given
	 *             {@link LayoutShell}
	 */
	public Presentation getModelThumbnail(LayoutShell mw, int width, int height)
			throws UnsupportedModelException;

	/**
	 * Wraps a {@link DESModel} into a presentation-ready shell. The toolset may
	 * return different implementations of {@link LayoutShell} depending on
	 * specific properties of the {@link DESModel}.
	 * 
	 * @param model
	 *            {@link DESModel} which will be presented
	 * @return {@link LayoutShell} which contains the given model
	 * @throws UnsupportedModelException
	 *             when the toolset does not support the type of the given
	 *             {@link DESModel}
	 */
	public LayoutShell wrapModel(DESModel model)
			throws UnsupportedModelException;
}
