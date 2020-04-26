package ides.api.plugin.model;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

import ides.api.model.fsa.FSAModel;
import model.supeventset.ver3.EventSet;

/**
 * The manager of model types available to IDES. This allows for custom model
 * types implemented by plugins.
 * 
 * @see DESModelType
 * @see #registerModel(DESModelType)
 * @author Lenko Grigorov
 */
public class ModelManager {
    // prevent instantiation
    private ModelManager() {
    }

    @Override
    public Object clone() {
        throw new RuntimeException("Cloning of " + this.getClass().toString() + " not supported.");
    }

    /**
     * Instance for the non-static methods.
     */
    private static ModelManager me = null;

    /**
     * Provides access to the instance of the manager.
     * 
     * @return the model manager
     */
    public static ModelManager instance() {
        if (me == null) {
            me = new ModelManager();
        }
        return me;
    }

    /**
     * Index of all registered {@link DESModelType}s. The key is a DES model
     * perspective. The value is the set of the model types implementing the given
     * perspective. Note that if a model type implements a number of perspectives,
     * its model type will be included in the set for each one of these
     * perspectives.
     * 
     * @see #registerModel(DESModelType)
     */
    protected Hashtable<Class<?>, Set<DESModelType>> class2Type = new Hashtable<Class<?>, Set<DESModelType>>();

    /**
     * Creates and returns a DES model for the supplied perspective, e.g.,
     * {@link FSAModel}, with the given display name.
     * 
     * @param perspective the perspective for which a model has to be created
     * @param name        the display name for the new model
     * @return a new DES model for the given perspective. If no model was registered
     *         for this perspective, returns <code>null</code>.
     * @see #createModel(Class)
     */
    public <T> T createModel(Class<T> perspective, String name) {
        Set<DESModelType> s = class2Type.get(perspective);
        if (s.isEmpty()) {
            return null;
        } else {
            for (DESModelType type : s) {
                if (type.getMainPerspective().equals(perspective)) {
                    return perspective.cast(type.createModel(name));
                }
            }
            return perspective.cast(s.iterator().next().createModel(name));
        }
    }

    /**
     * Creates and returns a DES model for the supplied perspective, e.g.,
     * {@link FSAModel}, with an empty display name.
     * 
     * @param perspective the perspectivee for which a model has to be created
     * @return a new DES model for the given perspective. If no model was registered
     *         for this perspective, returns <code>null</code>.
     * @see #createModel(Class, String)
     */
    public <T> T createModel(Class<T> perspective) {
        return createModel(perspective, "");
    }

    /**
     * Returns all model types that implement the given perspective.
     * 
     * @param perspective the perspective for which model types should be returned
     * @return the model types that implement the perspective
     * @see #getAllTypes()
     */
    public DESModelType[] getModelsForPerspective(Class<?> perspective) {
        return class2Type.get(perspective).toArray(new DESModelType[0]);
    }

    /**
     * Returns all model types registered with the manager.
     * 
     * @return all model types registerd with the manager
     * @see #getModelsForPerspective(Class)
     */
    public DESModelType[] getAllTypes() {
        Set<DESModelType> all = new TreeSet<DESModelType>(new Comparator<DESModelType>() {
            public int compare(DESModelType md1, DESModelType md2) {
                return md1.getDescription().compareTo(md2.getDescription());
            }
        });
        for (Enumeration<Set<DESModelType>> i = class2Type.elements(); i.hasMoreElements();) {
            all.addAll(i.nextElement());
        }
        return all.toArray(new DESModelType[0]);
    }

    /**
     * Registers a model type so that the corresponding DES model type becomes
     * available to IDES.
     * 
     * @param mt the model type for the DES model
     */
    public void registerModel(DESModelType mt) {
        Class<?>[] perspectives = mt.getModelPerspectives();
        for (int i = 0; i < perspectives.length; ++i) {
            Set<DESModelType> set;
            if (class2Type.containsKey(perspectives[i])) {
                set = class2Type.get(perspectives[i]);
            } else {
                set = new HashSet<DESModelType>();
            }
            set.add(mt);
            class2Type.put(perspectives[i], set);
        }
    }

    /**
     * Creates an empty event set.
     * 
     * @return an empty event set
     */
    public DESEventSet createEmptyEventSet() {
        return new EventSet();
    }
}
