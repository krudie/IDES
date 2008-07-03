/**
 * 
 */
package pluggable.io;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author christiansilvano
 */
public class IOPluginManager
{

	// Singleton instance:
	private static IOPluginManager instance = null;

	private Map<Class<?>, Set<FileIOPlugin>> metaSavers = null;

	private Map<Class<?>, FileIOPlugin> dataSavers = null;

	private Map<String, FileIOPlugin> dataLoaders = null;

	private Map<String, Map<String, FileIOPlugin>> metaLoaders = null;

	private Map<Class<?>, Set<ImportExportPlugin>> exporters = null;

	private Map<String, ImportExportPlugin> importers = null;

	protected Comparator<ImportExportPlugin> descriptionComparator = new Comparator<ImportExportPlugin>()
	{
		public int compare(ImportExportPlugin o1, ImportExportPlugin o2)
		{
			return o1.getDescription().compareTo(o2.getDescription());
		}
	};

	private IOPluginManager()
	{
		metaSavers = new HashMap<Class<?>, Set<FileIOPlugin>>();
		dataSavers = new HashMap<Class<?>, FileIOPlugin>();
		metaLoaders = new HashMap<String, Map<String, FileIOPlugin>>();
		dataLoaders = new HashMap<String, FileIOPlugin>();
		importers = new HashMap<String, ImportExportPlugin>();
		exporters = new HashMap<Class<?>, Set<ImportExportPlugin>>();
	}

	public static IOPluginManager getInstance()
	{
		if (instance == null)
		{
			instance = new IOPluginManager();
		}
		return instance;
	}

	/**
	 * Return a plugin which saves data
	 * 
	 * @param type -
	 *            The datatype of the model, e.g.: "FSA" or "TemplateDesign"
	 * @return
	 */
	public FileIOPlugin getDataSaver(Class<?> type)
	{
		return dataSavers.get(type);
	}

	/**
	 * Return all the plugins which saves metadata for <code>type</type>
	 * @param type - The datatype of the model, e.g.: "FSA" or "TemplateDesign"
	 * @return
	 */
	public Set<FileIOPlugin> getMetaSavers(Class<?> type)
	{
		return metaSavers.get(type);
	}

	/**
	 * Return all the plugins which loads metdata from the set (type, meta)
	 * 
	 * @param type
	 *            datatype, eg.: "FSA", "TemplateDesign"
	 * @param meta
	 *            the metadata to be loaded, eg: "layout"
	 * @return
	 */
	public FileIOPlugin getMetaLoaders(String type, String meta)
	{
		Map<String, FileIOPlugin> map = metaLoaders.get(type);
		if (map == null)
		{
			return null;
		}
		return map.get(meta);
	}

	/**
	 * Get the plugin which load data of the type <code>type</code>
	 * 
	 * @param type
	 * @return a reference to a plugin which loads data
	 */
	public FileIOPlugin getDataLoader(String type)
	{
		return dataLoaders.get(type);
	}

	/**
	 * Register a plugin which loads data of the type <code>t</code>
	 * 
	 * @param plugin
	 * @param t
	 */
	public void registerDataLoader(FileIOPlugin plugin, String t)
	{
		if (plugin != null)
		{
			dataLoaders.put(t, plugin);
		}
	}

	/**
	 * Register a plugin which saves data of the type <code>t</code>
	 * 
	 * @param plugin
	 * @param t
	 */
	public void registerDataSaver(FileIOPlugin plugin, Class<?> t)
	{
		if (plugin != null)
		{
			dataSavers.put(t, plugin);
		}
	}

	/**
	 * Register a plugin that loads metaData for the pair (data,metadata):
	 * <code>t</code>, <code>m</code>
	 * 
	 * @param plugin
	 * @param t
	 * @param m
	 */
	public void registerMetaLoader(FileIOPlugin plugin, String t, String m)
	{
		if (plugin != null)
		{
			Map<String, FileIOPlugin> map = metaLoaders.get(t);
			if (map == null)
			{
				map = new HashMap<String, FileIOPlugin>();
			}
			map.put(m, plugin);
			metaLoaders.put(t, map);
		}
	}

	/**
	 * Register a plugin that saves metaData for the pair (data,metadata):
	 * <code>t</code>, <code>m</code>
	 * 
	 * @param plugin
	 * @param t
	 * @param m
	 */
	public void registerMetaSaver(FileIOPlugin plugin, Class<?> t)
	{
		if (plugin != null)
		{
			Set<FileIOPlugin> set = metaSavers.get(t);
			if (set == null)
			{
				set = new HashSet<FileIOPlugin>();
			}
			set.add(plugin);
			metaSavers.put(t, set);
		}
	}

	/**
	 * Registers a plugin that imports a model from a kind given at
	 * <code>description</code> to the format described by :<code>importsTo</code>
	 * An example of utilization of this method would be:
	 * <code>registerImport(reference, "GRAIL", "FSA")</code>, the
	 * registration of a plugin which imports a grail file to a FSA IDES file.
	 * 
	 * @param plugin
	 * @param description
	 * @param importsTo
	 */
	public void registerImport(ImportExportPlugin plugin, String description)
	{
		if (plugin != null)
		{
			importers.put(description, plugin);
		}
	}

	/**
	 * Registers a plugin that exports a model from a IDES model given by
	 * <code>description</code> to the format described by :<code>exportsTo</code>
	 * An example of utilization of this method would be:
	 * <code>registerExport(reference, "FSA", "GRAIL")</code>, the
	 * registration of a plugin which exports a FSA IDES file to the Grail
	 * format.
	 * 
	 * @param plugin
	 * @param description
	 * @param importsTo
	 */
	public void registerExport(ImportExportPlugin plugin, Class<?> type)
	{
		if (plugin != null)
		{
			Set<ImportExportPlugin> set = exporters.get(type);
			if (set == null)
			{
				set = new TreeSet<ImportExportPlugin>(descriptionComparator);
			}
			set.add(plugin);
			exporters.put(type, set);
		}
	}

	/**
	 * Returns a set of plugins which exports a model described by
	 * <code>type</code> to a different format. Example of utilization:
	 * <code>getExporters("FSA")</code> returns a set with references to
	 * plugins that exports from "FSA" to a different format.
	 * 
	 * @param type
	 * @return
	 */
	public Set<ImportExportPlugin> getExporters(Class<?> t)
	{
		return exporters.get(t);
	}

	public Set<ImportExportPlugin> getImporters()
	{
		Set<ImportExportPlugin> returnSet = new TreeSet<ImportExportPlugin>(
				descriptionComparator);
		returnSet.addAll(importers.values());
		return returnSet;
	}

	public Set<ImportExportPlugin> getExporters()
	{
		Set<ImportExportPlugin> returnSet = new TreeSet<ImportExportPlugin>(
				descriptionComparator);
		for (Set<ImportExportPlugin> set : exporters.values())
		{
			returnSet.addAll(set);
		}
		return returnSet;
	}

	/**
	 * Returns a set of plugins which imports from model described by
	 * <code>type</code> to the IDES format. Example of utilization:
	 * <code>getImporters(".fm")</code> returns a set with references to
	 * plugins that import from ".fm" files (Grail) to the IDES format.
	 * 
	 * @param type
	 * @return
	 */
	public ImportExportPlugin getImporter(String description)
	{
		return importers.get(description);
	}
}
