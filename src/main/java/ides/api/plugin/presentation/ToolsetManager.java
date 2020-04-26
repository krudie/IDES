package ides.api.plugin.presentation;

import java.util.Hashtable;

import ides.api.model.fsa.FSAModel;
import ides.api.plugin.model.DESModel;

/**
 * Manager of the {@link Toolset}s available to IDES. A plug-in which wants to
 * provide the GUI elements for a specific {@link DESModel} perspective, e.g.,
 * {@link FSAModel}, should register itself for this specific perspective. When
 * the user creates a new model or opens a model, IDES will use the registered
 * {@link Toolset} for the model's "main" perspective to obtain the relevant GUI
 * elements.
 * 
 * @see Toolset
 * @see #registerToolset(Class, Toolset)
 * @author Lenko Grigorov
 */
public class ToolsetManager {

    // prevent instantiation
    private ToolsetManager() {
    }

    @Override
    public Object clone() {
        throw new RuntimeException("Cloning of " + this.getClass().toString() + " not supported.");
    }

    /**
     * Instance for the non-static methods.
     */
    private static ToolsetManager me = null;

    /**
     * Provides access to the instance of the manager.
     * 
     * @return the toolset manager
     */
    public static ToolsetManager instance() {
        if (me == null) {
            me = new ToolsetManager();
        }
        return me;
    }

    /**
     * The table with the registered {@link Toolset}s. The key is a {@link DESModel}
     * perspective and the value is the corresponding {@link Toolset}.
     */
    protected Hashtable<Class<?>, Toolset> class2Toolset = new Hashtable<Class<?>, Toolset>();

    /**
     * Returns the {@link Toolset} registered for the "main" perspective of the
     * given {@link DESModel}.
     * 
     * @param model the {@link DESModel} for which a {@link Toolset} is needed
     * @return the {@link Toolset} registered for the "main" perspective of the
     *         given {@link DESModel}; <code>null</code> if no {@link Toolset} is
     *         registered for this perspective
     */
    public Toolset getToolset(DESModel model) {
        return class2Toolset.get(model.getModelType().getMainPerspective());
    }

    /**
     * Returns the {@link Toolset} registered for the given {@link DESModel}
     * perspective.
     * 
     * @param perspective the {@link DESModel} perspective for which a
     *                    {@link Toolset} is needed
     * @return the {@link Toolset} registered for the given {@link DESModel}
     *         perspective; <code>null</code> if no {@link Toolset} is registered
     *         for this perspective
     */
    public Toolset getToolset(Class<?> perspective) {
        return class2Toolset.get(perspective);
    }

    /**
     * Registers a {@link Toolset} to handle a specific kind of {@link DESModel}
     * perspective.
     * 
     * @param perspective the {@link DESModel} perspective which will be handled by
     *                    the {@link Toolset}
     * @param ts          the {@link Toolset} to handle the given {@link DESModel}
     *                    perspective
     */
    public void registerToolset(Class<?> perspective, Toolset ts) {
        class2Toolset.put(perspective, ts);
    }
}
