package pluggable.ui;

import model.DESModel;
import presentation.Presentation;

/**
 * The implementations of this interface provide the required GUI elements to
 * manipulate a given type of {@link DESModel}. IDES will display these GUI
 * elements when a model of the type is activated by the user.
 * <p>
 * To handle a given {@link DESModel} type, one needs to register the toolset
 * with {@link ToolsetManager#registerToolset(Class, Toolset)}. Then, IDES will
 * use {@link ToolsetManager#getToolset(Class)} to get the toolset when needed.
 * 
 * @see ToolsetManager#registerToolset(Class, Toolset)
 * @author Lenko Grigorov
 */
public interface Toolset
{

	/**
	 * Returns a descriptor of the GUI elements which will be used to display a
	 * {@link DESModel}.
	 * 
	 * @param model
	 *            the {@link DESModel} to be displayed
	 * @return the descriptor of the GUI elements to be used
	 * @throws UnsupportedModelException
	 *             when the toolset does not support the type of the given
	 *             {@link DESModel}
	 * @see UIDescriptor
	 */
	public UIDescriptor getUIElements(DESModel model)
			throws UnsupportedModelException;

	/**
	 * Returns a {@link Presentation} element with a thumbnail view of the given
	 * {@link DESModel}.
	 * 
	 * @param model
	 *            {@link DESModel} for which to return a thumbnail
	 * @param width
	 *            the desired width of the thumbnail in pixels
	 * @param height
	 *            the desired height of the thumbnail in pixels
	 * @return a {@link Presentation} element with a thumbnail view of the
	 *         {@link DESModel}
	 * @throws UnsupportedModelException
	 *             when the toolset does not support the type of the given
	 *             {@link DESModel}
	 */
	public Presentation getModelThumbnail(DESModel model, int width, int height)
			throws UnsupportedModelException;
}
