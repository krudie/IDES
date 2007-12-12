/**
 * 
 */
package pluggable.io;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
/**
 * @author christiansilvano
 *
 */
public class IOPluginManager {

//	Singleton instance:
	private static IOPluginManager instance = null;
	private Set<PluginDescription> metaSavers = null;
	private Set<PluginDescription> dataSavers = null;
	private Set<PluginDescription> dataLoaders = null;
	private Set <PluginDescription> metaLoaders = null;
	private Set <PluginDescription> exporters = null;
	private Set <PluginDescription> importers = null;
	
	private IOPluginManager()
	{
		metaSavers = new HashSet<PluginDescription>();
		dataSavers = new HashSet<PluginDescription>();
		metaLoaders = new HashSet<PluginDescription>();
		dataLoaders = new HashSet<PluginDescription>();
		importers = new HashSet<PluginDescription>();
		exporters = new HashSet<PluginDescription>();
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
	 * @param type - The datatype of the model, e.g.: "FSA" or "TemplateDesign"
	 * @return
	 */
	public FileIOPlugin getDataSaver(String type)
	{
		Iterator it = dataSavers.iterator();
		Set<FileIOPlugin> returnSet = new HashSet<FileIOPlugin>();
		while(it.hasNext())
		{
			FileIOPlugin plugin = (FileIOPlugin)((PluginDescription)it.next()).worksWithDataType(type);
			if(plugin != null)
			{
				returnSet.add(plugin);
			}
		}
		
		switch(returnSet.size()){
			case 1:
				return returnSet.iterator().next();
			case 0:
				//error: there are no plugins capable of saving this data type
				return null;
			default:
				//error: there are more than one plugin capable of loading the same data type
				return null;
			}		
	}
	
	/**
	 * Return all the plugins which saves metadata for <code>type</type>
	 * @param type - The datatype of the model, e.g.: "FSA" or "TemplateDesign"
	 * @return
	 */
	public Set<FileIOPlugin> getMetaSavers(String type)
	{
		Iterator it = metaSavers.iterator();
		Set<FileIOPlugin> returnSet = new HashSet<FileIOPlugin>();
		while(it.hasNext())
		{
			FileIOPlugin plugin = (FileIOPlugin)((PluginDescription)it.next()).worksWithDataType(type);
			if(plugin != null)
				returnSet.add(plugin);
			}
		
		
		switch(returnSet.size()){
			case 0:
				//there are no plugins capable of saving metadata for this data type
				return null;
			default:
				return returnSet;
			}		
	}
	
	/**
	 * Return all the plugins which loads metdata from the set (type, meta) 
	 * @param type datatype, eg.: "FSA", "TemplateDesign"
	 * @param meta the metadata to be loaded, eg: "layout"
	 * @return
	 */
	public Set<FileIOPlugin> getMetaLoaders(String type, String meta)
	{
		Iterator it = metaLoaders.iterator();
		Set<FileIOPlugin> returnSet = new HashSet<FileIOPlugin>();
		while(it.hasNext())
		{
			FileIOPlugin plugin = (FileIOPlugin)((PluginDescription)it.next()).worksWithMetaType(type, meta);
			if(plugin != null)
				returnSet.add(plugin);
		}		
		return returnSet;
	}
	
	/**
	 * Get the plugin which load data of the type <code>type</code>
	 * @param type
	 * @return a reference to a plugin which loads data
	 */
	public FileIOPlugin getDataLoader(String type)
	{
		Iterator it = dataLoaders.iterator();
		Set<FileIOPlugin> returnSet = new HashSet<FileIOPlugin>();
		while(it.hasNext())
		{
			FileIOPlugin plugin = (FileIOPlugin)((PluginDescription)it.next()).worksWithDataType(type);
			if(plugin != null)
			{
				returnSet.add(plugin);
			}
		}
		
		switch(returnSet.size()){
			case 1:
				return returnSet.iterator().next();
			case 0:
				//error: there are no plugins capable of saving this data type
				return null;
			default:
				//error: there are more than one plugin capable of loading the same data type
				return null;
			}	
	}
	
	/**
	 * Register a plugin which loads data of the type <code>t</code>
	 * @param plugin
	 * @param t
	 */
	public void registerDataLoader(FileIOPlugin plugin, String t)
	{
		if(plugin != null)
		{
			dataLoaders.add(new PluginDescription(plugin,t,null));
		}
	}
	
	/**
	 * Register a plugin which saves data of the type <code>t</code>
	 * @param plugin
	 * @param t
	 */
	public void registerDataSaver(FileIOPlugin plugin, String t)
	{
		if(plugin != null)
		{
			dataSavers.add(new PluginDescription(plugin,t,null));
		}
	}
	
	/**
	 * Register a plugin that loads metaData for the pair (data,metadata): <code>t</code>, <code>m</code>
	 * @param plugin
	 * @param t
	 * @param m
	 */
	public void registerMetaLoader(FileIOPlugin plugin, String t, String m)
	{
		if(plugin != null)
		{
			
			metaLoaders.add(new PluginDescription(plugin, t,m));
		}
	}
	
	/**
	 * Register a plugin that saves metaData for the pair (data,metadata): <code>t</code>, <code>m</code>
	 * @param plugin
	 * @param t
	 * @param m
	 */
	public void registerMetaSaver(FileIOPlugin plugin, String t, String m)
	{
		if(plugin != null)
		{
			metaSavers.add(new PluginDescription(plugin,t,m));
		}
	}
	
	/**
	 * Registers a plugin that imports a model from a kind given at <code>description</code>
	 * to the format described by :<code>importsTo</code>
	 * An example of utilization of this method would be:
	 * <code>registerImport(reference, "GRAIL", "FSA")</code>, the registration of a plugin which imports
	 * a grail file to a FSA IDES file.
	 * @param plugin
	 * @param description
	 * @param importsTo
	 */
	public void registerImport(ImportExportPlugin plugin, String description, String importsTo)
	{
		if(plugin != null)
		{
			importers.add(new PluginDescription(plugin, description, importsTo));
		}
	}
	
	/**
	 * Registers a plugin that exports a model from a IDES model given by <code>description</code>
	 * to the format described by :<code>exportsTo</code>
	 * An example of utilization of this method would be:
	 * <code>registerExport(reference, "FSA", "GRAIL")</code>, the registration of a plugin which exports
	 * a FSA IDES file to the Grail format.
	 * @param plugin
	 * @param description
	 * @param importsTo
	 */
	public void registerExport(ImportExportPlugin plugin, String description, String exportsTo)
	{
		if(plugin != null)
		{
			exporters.add(new PluginDescription(plugin, description, exportsTo));
		}
		
	}
	
	/**
	 * Returns a set of plugins which exports a model described by <code>type</code> to a different 
	 * format. Example of utilization: <code>getExporters("FSA")</code> returns a set with references to plugins
	 * that exports from "FSA" to a different format.
	 * @param type
	 * @return
	 */
	public Set<ImportExportPlugin> getExporters(String description)
	{
		Set<ImportExportPlugin> returnSet= new HashSet<ImportExportPlugin>();
		Iterator<PluginDescription> it = exporters.iterator();
		
		while(it.hasNext())
		{
			ImportExportPlugin plugin = it.next().exportToType(description);
			if( plugin != null)
			{
				returnSet.add(plugin);
			}
		}
		return returnSet;
	}
	
	
	public Set<ImportExportPlugin> getImporters()
	{
		Set<ImportExportPlugin> returnSet = new HashSet<ImportExportPlugin>();
		Iterator<PluginDescription> it = importers.iterator();
		while(it.hasNext())
		{
			returnSet.add((ImportExportPlugin)it.next().plugin);
		}
		
		return returnSet;
	}
	
	public Set<ImportExportPlugin> getExporters()
	{
		Set<ImportExportPlugin> returnSet = new HashSet<ImportExportPlugin>();
		Iterator<PluginDescription> it = exporters.iterator();
		while(it.hasNext())
		{
			returnSet.add((ImportExportPlugin)it.next().plugin);
		}
		return returnSet;
	}	
	
	/**
	 * Returns a set of plugins which imports from model described by <code>type</code> to the IDES 
	 * format. Example of utilization: <code>getImporters(".fm")</code> returns a set with references 
	 * to plugins that import from ".fm" files (Grail) to the IDES format.
	 * @param type
	 * @return
	 */
	public Set<ImportExportPlugin> getImporters(String descriptor)
	{
		Set<ImportExportPlugin> returnSet= new HashSet<ImportExportPlugin>();
		Iterator<PluginDescription> it = importers.iterator();
		
		while(it.hasNext())
		{
			ImportExportPlugin plugin = it.next().importFromType(descriptor);
			if( plugin != null)
			{
				returnSet.add(plugin);
			}
		}
		return returnSet;
	}
	
	/**
	 * Auxiliar class to help selecting plugins according to the descriptions passed during
	 * the plugin registration.
	 *\TODO refactor this class in order to be easier to maintain
	 * @author christiansilvano
	 *
	 */
	private class PluginDescription{
		private String tag;
		private String type;
		private Object plugin;
		
		public PluginDescription(Object reference, String t, String m)
		{
			type = t;
			tag = m;
			plugin = reference;
		}
		
		public FileIOPlugin worksWithDataType(String t)
		{
			if(t.equals(type))
			{
				return (FileIOPlugin)plugin;
			}
			return null;
		}
		
		public FileIOPlugin worksWithMetaType(String t, String m)
		{
			if(type.equals(t) & tag.equals(m))
			{
				return (FileIOPlugin)plugin;
			}
			return null;
		}
	
		public ImportExportPlugin importExportTo(String description, String workingType)
		{
			if(type.equals(description) & tag.equals(workingType))
			{
				return (ImportExportPlugin)plugin;
			}
			return null;
		}

		public ImportExportPlugin exportToType(String description)
		{
			if(((ImportExportPlugin)plugin).getDescription().equals(description))
			{
				return (ImportExportPlugin)plugin;
			}
			return null;
		}
		
		//return the plugin case the "exportExtension" is the same as the one given as paramether
		//return null, otherwise
		public ImportExportPlugin importFromType(String description)
		{
			if(((ImportExportPlugin)plugin).getDescription().equals(description))
			{
				return (ImportExportPlugin)plugin;
			}
			return null;
			
		}
		
	}
	
	

}
