package ides.api.plugin.layout;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import ides.api.model.fsa.FSAModel;
import pluggable.layout.jung.JUNGLayouter;

/**
 * The manager of classes for the layout of {@link FSAModel}s. Some layout
 * classes are included with IDES.
 * 
 * @see FSALayouter
 * @see #registerLayouter(FSALayouter)
 * @see #getDefaultFSALayouter()
 * @author Lenko Grigorov
 */
public class FSALayoutManager {
    // prevent instantiation
    private FSALayoutManager() {
        setDefaultLayouter(new JUNGLayouter());
    }

    @Override
    public Object clone() {
        throw new RuntimeException("Cloning of " + this.getClass().toString() + " not supported.");
    }

    /**
     * Instance for the non-static methods.
     */
    private static FSALayoutManager me = null;

    /**
     * Provides access to the instance of the manager.
     * 
     * @return the model manager
     */
    public static FSALayoutManager instance() {
        if (me == null) {
            me = new FSALayoutManager();
        }
        return me;
    }

    /**
     * The indexed set of all registered layouters. The key to the index is the name
     * of the corresponding layouter.
     */
    protected Map<String, FSALayouter> layouters = new TreeMap<String, FSALayouter>();

    /**
     * The default layouter to be used by IDES.
     * 
     * @see #getDefaultFSALayouter()
     * @see #setDefaultLayouter(FSALayouter)
     */
    protected FSALayouter defaultLayouter;

    /**
     * Retrieve the default FSA layouter which is used by IDES.
     * 
     * @return the default FSA layouter which is used by IDES
     */
    public FSALayouter getDefaultFSALayouter() {
        return defaultLayouter;
    }

    /**
     * Register an {@link FSALayouter} with IDES. This class will become available
     * for the layout of {@link FSAModel}s.
     * <p>
     * If a layouter with the same name has already been registered, the new
     * layouter will replace it.
     * 
     * @param layouter the layouter to be registered
     */
    public void registerLayouter(FSALayouter layouter) {
        layouters.put(layouter.getName(), layouter);
    }

    /**
     * Retrieve the names of all registered {@link FSALayouter}s.
     * 
     * @return the names of all registered {@link FSALayouter}s
     */
    public Set<String> getLayouterNames() {
        return new TreeSet<String>(layouters.keySet());
    }

    /**
     * Retrieve the layouter with the given name. If no layouter with the given name
     * has been registered, returns <code>null</code>.
     * 
     * @param name the name of the layouter
     * @return the layouter with the given name; or <code>null</code> if no layouter
     *         with the given name has been registered
     */
    public FSALayouter getLayouter(String name) {
        return layouters.get(name);
    }

    /**
     * Register the given layout and start using it as the default layouter for
     * {@link FSAModel}s.
     * 
     * @param layouter the layouter to be registered and used as a default layouter
     */
    public void setDefaultLayouter(FSALayouter layouter) {
        registerLayouter(layouter);
        defaultLayouter = layouter;
    }
}
