package pluggable.ui;

import javax.swing.JComponent;

import model.DESModel;

import presentation.ModelWrap;
import presentation.Presentation;
import presentation.PresentationManager;

/**
 * For a given type of {@link DESModel}, an implementation
 * of the interface will provide the required GUI elements.
 * IDES will display these GUI elements when a model of the
 * type is activated by the user.
 * 
 * <p>A toolset is also used to wrap raw {@link DESModel}s
 * into {@link ModelWrap}s so that they can be visualized.
 *  
 * <p>To handle a given {@link DESModel} type, one needs to
 * register the toolset with {@link PresentationManager#registerToolset(Class, Toolset)}.
 * Then, IDES will use {@link PresentationManager#getToolset(Class)}
 * to get the toolset when needed.
 * 
 * @see PresentationManager#registerToolset(Class, Toolset)
 * @see #wrapModel(DESModel)
 * @see ModelWrap
 * 
 * @author Lenko Grigorov
 */
public interface Toolset {
	
	/**
	 * Returns an array with the GUI elements which will be used to
	 * manipulate the given {@link ModelWrap}. The GUI elements
	 * will be displayed in separate tabs by IDES. The captions
	 * for the tabs have to be provided by {@link #getEditPanesCaptions(ModelWrap)}. 
	 * @param mw the {@link ModelWrap} which will be manipulated by the user
	 * @return an array of GUI elements through which the user will
	 * manipulate the {@link ModelWrap}. These have to agree in number with
	 * the captions returned by {@link #getEditPanesCaptions(ModelWrap)} for the
	 * same {@link ModelWrap}
	 * @throws UnsupportedModelException when the toolset does not
	 * support the type of the given {@link ModelWrap}
	 * @see #getEditPanesCaptions(ModelWrap)
	 */
	public JComponent[] getEditPanes(ModelWrap mw) throws UnsupportedModelException;
	
	/**
	 * Returns an array with the captions for the GUI elements
	 * returned by {@link #getEditPanes(ModelWrap)}. These captions
	 * will be used for the tabs in which the GUI elements are displayed.
	 * @param mw the {@link ModelWrap} which will be manipulated by the user
	 * @return an array of captions for the GUI elements through which the user will
	 * manipulate the {@link ModelWrap}. These have to agree in number with
	 * the GUI elements returned by {@link #getEditPanes(ModelWrap)} for the
	 * same {@link ModelWrap}
	 * @throws UnsupportedModelException when the toolset does not
	 * support the type of the given {@link ModelWrap}
	 * @see #getEditPanes(ModelWrap)
	 */
	public String[] getEditPanesCaptions(ModelWrap mw) throws UnsupportedModelException;
	
	/**
	 * Returns a {@link Presentation} element with a thumbnail view
	 * of the given {@link ModelWrap}.
	 * @param mw {@link ModelWrap} for which to return a thumbnail
	 * @param width the desired width of the thumbnail in pixels
	 * @param height the desired height of the thumbnail in pixels
	 * @return a {@link Presentation} element with a thumbnail view of the {@link ModelWrap}
	 * @throws UnsupportedModelException when the toolset does not
	 * support the type of the given {@link ModelWrap}
	 */
	public Presentation getModelThumbnail(ModelWrap mw, int width, int height) throws UnsupportedModelException;
	
	/**
	 * Wraps a {@link DESModel} into a presentation-ready shell.
	 * The toolset may return different implementations of {@link ModelWrap}
	 * depending on specific properties of the {@link DESModel}.
	 * @param model {@link DESModel} which will be presented
	 * @return {@link ModelWrap} which contains the given model
	 * @throws UnsupportedModelException when the toolset does
	 * not support type of the given {@link DESModel}
	 */
	public ModelWrap wrapModel(DESModel model) throws UnsupportedModelException;
}
