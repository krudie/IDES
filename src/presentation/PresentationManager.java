package presentation;

import java.util.Hashtable;

import model.DESModel;
import pluggable.ui.Toolset;

/**
 * Manager of the {@link Toolset}s available to IDES. A plug-in which wants to
 * provide the GUI elements for a specific type of {@link DESModel} should
 * register itself for this specific {@link DESModel} interface. When the user
 * creates a new model or opens a model of this type, IDES will use the
 * registered {@link Toolset} to obtain the relevant GUI elements.
 * <p>
 * IDES will also use the {@link Toolset} registered for a specific
 * {@link DESModel} type to obtain {@link LayoutShell}s of {@link DESModel}s
 * of this type.
 * 
 * @see Toolset
 * @see #registerToolset(Class, Toolset)
 * @see Toolset#wrapModel(model.DESModel)
 * @author Lenko Grigorov
 */
public class PresentationManager
{

	/**
	 * The table with the registered {@link Toolset}s. The key is a
	 * {@link DESModel} interface and the value is the corresponding
	 * {@link Toolset}.
	 */
	protected static Hashtable<Class<?>, Toolset> class2Toolset = new Hashtable<Class<?>, Toolset>();

	/**
	 * Returns the {@link Toolset} registered for the given {@link DESModel}
	 * interface.
	 * 
	 * @param modelType
	 *            the {@link DESModel} for which a {@link Toolset} is needed
	 * @return the {@link Toolset} registered for the given {@link DESModel}
	 *         interface; <code>null</code> if no {@link Toolset} is
	 *         registered for this interface
	 */
	public static Toolset getToolset(Class<?> modelType)
	{
		return class2Toolset.get(modelType);
	}

	/**
	 * Registers a {@link Toolset} to handle a specific type of {@link DESModel}.
	 * 
	 * @param modelType
	 *            the {@link DESModel} interface which will be handled by the
	 *            {@link Toolset}
	 * @param ts
	 *            the {@link Toolset} to handle the given {@link DESModel}
	 *            interface
	 */
	// TODO discover the type of toolset through reflection mechanisms
	public static void registerToolset(Class<?> modelType, Toolset ts)
	{
		class2Toolset.put(modelType, ts);
	}
}
