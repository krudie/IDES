/**
 * 
 */
package ides.api.plugin.io;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ides.api.model.fsa.FSAModel;
import ides.api.plugin.model.DESModel;
import ides.api.plugin.model.DESModelType;

/**
 * The manager of I/O plugins. Plugins which provide services for the
 * saving/loading of DES models or related meta-data or for the import/export of
 * different file formats must register with this manager in order to be
 * available to the user.
 * 
 * @see FileIOPlugin
 * @see ImportExportPlugin
 * @author christiansilvano
 * @author Lenko Grigorov
 */
public class IOPluginManager {

    // Singleton instance:
    private static IOPluginManager me = null;

    private Map<Class<?>, Set<FileIOPlugin>> metaSavers = null;

    private Set<FileIOPlugin> metaSaversGeneric = null;

    private Map<Class<?>, FileIOPlugin> dataSavers = null;

    private Map<String, FileIOPlugin> dataLoaders = null;

    private Map<String, Map<String, FileIOPlugin>> metaLoaders = null;

    private Map<String, FileIOPlugin> metaLoadersGeneric = null;

    private Map<Class<?>, Set<ImportExportPlugin>> exporters = null;

    private Map<String, ImportExportPlugin> importers = null;

    /**
     * Comparator which can be used to sort {@link ImportExportPlugin}s lexically
     * according to their file format descriptions.
     * 
     * @see ImportExportPlugin#getFileDescription()
     */
    protected Comparator<ImportExportPlugin> descriptionComparator = new Comparator<ImportExportPlugin>() {
        public int compare(ImportExportPlugin o1, ImportExportPlugin o2) {
            return o1.getFileDescription().compareTo(o2.getFileDescription());
        }
    };

    private IOPluginManager() {
        metaSavers = new HashMap<Class<?>, Set<FileIOPlugin>>();
        metaSaversGeneric = new HashSet<FileIOPlugin>();
        dataSavers = new HashMap<Class<?>, FileIOPlugin>();
        metaLoaders = new HashMap<String, Map<String, FileIOPlugin>>();
        metaLoadersGeneric = new HashMap<String, FileIOPlugin>();
        dataLoaders = new HashMap<String, FileIOPlugin>();
        importers = new HashMap<String, ImportExportPlugin>();
        exporters = new HashMap<Class<?>, Set<ImportExportPlugin>>();
    }

    /**
     * Throws {@link RuntimeException} to prevent cloning.
     */
    public Object clone() {
        throw new RuntimeException("Cloning of " + this.getClass().toString() + " not supported.");
    }

    /**
     * Provides access to the instance of the manager.
     * 
     * @return the instance of the manager
     */
    public static IOPluginManager instance() {
        if (me == null) {
            me = new IOPluginManager();
        }
        return me;
    }

    /**
     * Returns a plugin which is capable of saving {@link DESModel}s of the given
     * perspective (e.g. {@link FSAModel}). If no registered plugin is capable,
     * returns <code>null</code>.
     * 
     * @param type the {@link DESModel} perspective which the plugin should handle
     * @return a plugin which can handle the given perspective, or <code>null</code>
     *         if no such plugin is registered
     * @see DESModelType
     */
    public FileIOPlugin getDataSaver(Class<?> type) {
        return dataSavers.get(type);
    }

    /**
     * Returns the set of plugins registered to save meta-data when saving
     * {@link DESModel}s of the given perspective (e.g. {@link FSAModel}). If no
     * such plugin is registered, returns <code>null</code>.
     * 
     * @param type the {@link DESModel} perspective for which the plugins want to
     *             save meta-data
     * @return the set of plugins registered to save meta-data when saving
     *         {@link DESModel}s of the given perspective, or <code>null</code> if
     *         no such plugin is registered
     * @see DESModelType
     */
    public Set<FileIOPlugin> getMetaSavers(Class<?> type) {
        Set<FileIOPlugin> set = new HashSet<FileIOPlugin>(metaSavers.get(type));
        set.addAll(metaSaversGeneric);
        return set;
    }

    /**
     * Returns the plugin registered to load meta-data with the given tag when
     * loading {@link DESModel}s of the given type (e.g., "FSA"). If no such plugin
     * is registered, returns <code>null</code>.
     * 
     * @param type string with the type of model being loaded
     * @param tag  the "tag" for the meta-data section
     * @return the plugin registered to load meta-data with the given tag for the
     *         given type of models, or <code>null</code> if no such plugin is
     *         registered
     */
    public FileIOPlugin getMetaLoaders(String type, String tag) {
        Map<String, FileIOPlugin> map = metaLoaders.get(type);
        if (map == null) {
            return metaLoadersGeneric.get(tag);
        }
        FileIOPlugin plugin = map.get(tag);
        if (plugin == null) {
            return metaLoadersGeneric.get(tag);
        }
        return plugin;
    }

    /**
     * Returns a plugin which is capable of loading a {@link DESModel} of the given
     * type (for example, the type of Finite-State Machines is "FSA"). If no such
     * plugin is registered, returns <code>null</code>.
     * 
     * @param type string with the type of model being loaded
     * @return a plugin which can load the given type, or <code>null</code> if no
     *         such plugin is registered
     */
    public FileIOPlugin getDataLoader(String type) {
        return dataLoaders.get(type);
    }

    /**
     * Registers a plugin which can load models of the given type (for example, the
     * type of Finite-State Machines is "FSA"). The type of model is read from the
     * IDES model files.
     * 
     * @param plugin the plugin to be registered
     * @param type   the type of model which the plugin can load
     */
    public void registerDataLoader(FileIOPlugin plugin, String type) {
        if (plugin != null) {
            dataLoaders.put(type, plugin);
        }
    }

    /**
     * Registers a plugin which can save {@link DESModel}s of the given perspective
     * (e.g., {@link FSAModel}).
     * 
     * @param plugin the plugin to be registered
     * @param type   the {@link DESModel} perspective which the plugin can save
     * @see DESModelType
     */
    public void registerDataSaver(FileIOPlugin plugin, Class<?> type) {
        if (plugin != null) {
            dataSavers.put(type, plugin);
        }
    }

    /**
     * Registers a plugin which can load meta-data with the given "tag" for the
     * given type of DES models. The type of model and the "tag" are read from the
     * IDES model files.
     * 
     * @param plugin the plugin to be registered
     * @param type   the type of model for which the plugin can load meta-data
     * @param tag    the "tag" of the meta-data section which the plugin can load
     */
    public void registerMetaLoader(FileIOPlugin plugin, String type, String tag) {
        if (plugin != null) {
            Map<String, FileIOPlugin> map = metaLoaders.get(type);
            if (map == null) {
                map = new HashMap<String, FileIOPlugin>();
            }
            map.put(tag, plugin);
            metaLoaders.put(type, map);
        }
    }

    /**
     * Registers a plugin which can load meta-data with the given "tag" for any type
     * of DES models (a generic plugin). The "tag" is read from the IDES model
     * files.
     * 
     * @param plugin the plugin to be registered
     * @param tag    the "tag" of the meta-data section which the plugin can load
     */
    public void registerMetaLoader(FileIOPlugin plugin, String tag) {
        if (plugin != null) {
            metaLoadersGeneric.put(tag, plugin);
        }
    }

    /**
     * Registers a plugin which can save meta-data for the given {@link DESModel}
     * perspective.
     * 
     * @param plugin the plugin to be registered
     * @param type   the {@link DESModel} perspective for which the plugin can save
     *               meta-data
     * @see DESModelType
     */
    public void registerMetaSaver(FileIOPlugin plugin, Class<?> type) {
        if (plugin != null) {
            Set<FileIOPlugin> set = metaSavers.get(type);
            if (set == null) {
                set = new HashSet<FileIOPlugin>();
            }
            set.add(plugin);
            metaSavers.put(type, set);
        }
    }

    /**
     * Registers a plugin which can save meta-data for any {@link DESModel}
     * perspective (a generic plugin).
     * 
     * @param plugin the plugin to be registered
     * @see DESModelType
     */
    public void registerMetaSaver(FileIOPlugin plugin) {
        if (plugin != null) {
            metaSaversGeneric.add(plugin);
        }
    }

    /**
     * Registers a plugin which can import models from other file formats.
     * 
     * @param plugin the plugin to be registered
     */
    public void registerImport(ImportExportPlugin plugin) {
        if (plugin != null) {
            importers.put(plugin.getFileDescription(), plugin);
        }
    }

    /**
     * Registers a plugin which can export {@link DESModel}s of the given
     * perspective (e.g., {@link FSAModel}) to another file format.
     * 
     * @param plugin the plugin to be registered
     * @param type   the {@link DESModel} perspective which the plugin can export
     * @see DESModelType
     */
    public void registerExport(ImportExportPlugin plugin, Class<?> type) {
        if (plugin != null) {
            Set<ImportExportPlugin> set = exporters.get(type);
            if (set == null) {
                set = new TreeSet<ImportExportPlugin>(descriptionComparator);
            }
            set.add(plugin);
            exporters.put(type, set);
        }
    }

    /**
     * Returns the set of plugins registered to export {@link DESModel}s of the
     * given perspective (e.g., {@link FSAModel}) to another file format. If no such
     * plugin is registered, returns <code>null</code>.
     * 
     * @param type the {@link DESModel} perspective which the plugins should be able
     *             to export
     * @return the set of plugins registered to export {@link DESModel}s of the
     *         given perspective, or <code>null</code> if no such plugin is
     *         registered
     * @see DESModelType
     */
    public Set<ImportExportPlugin> getExporters(Class<?> type) {
        return exporters.get(type);
    }

    /**
     * Returns the set of plugins registered to import from other file formats. If
     * no such plugin is registered, returns and empty set.
     * 
     * @return the set of plugins registered to import from other file formats
     */
    public Set<ImportExportPlugin> getImporters() {
        Set<ImportExportPlugin> returnSet = new TreeSet<ImportExportPlugin>(descriptionComparator);
        returnSet.addAll(importers.values());
        return returnSet;
    }

    /**
     * Returns the set of plugins registered to export to other file formats. If no
     * such plugin is registered, returns and empty set.
     * 
     * @return the set of plugins registered to export to other file formats
     */
    public Set<ImportExportPlugin> getExporters() {
        Set<ImportExportPlugin> returnSet = new TreeSet<ImportExportPlugin>(descriptionComparator);
        for (Set<ImportExportPlugin> set : exporters.values()) {
            returnSet.addAll(set);
        }
        return returnSet;
    }

    /**
     * Returns the importing plugin which has the given file-format description. If
     * no such plugin is registered, returns <code>null</code>.
     * 
     * @param description the file format description of the plugin
     * @return the importing plugin which has the given file-format description, or
     *         <code>null</code> if no such plugin is registered
     * @see ImportExportPlugin#getFileDescription()
     */
    public ImportExportPlugin getImporter(String description) {
        return importers.get(description);
    }
}
