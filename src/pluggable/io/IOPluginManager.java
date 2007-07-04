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
	
	public Set<FileIOPlugin> getMetLoaders(String type, String meta)
	{
		Iterator it = metaLoaders.iterator();
		Set<FileIOPlugin> returnSet = new HashSet<FileIOPlugin>();
		while(it.hasNext())
		{
			FileIOPlugin plugin = (FileIOPlugin)((PluginDescription)it.next()).worksWithMetaType(type, meta);
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
	
	
	public ImportExportPlugin getExporter(String description, String exportsTo)
	{
		Iterator<PluginDescription> it = exporters.iterator();
		Set<ImportExportPlugin> returnSet = new HashSet<ImportExportPlugin>();
		
		while(it.hasNext())
		{
			ImportExportPlugin plugin = it.next().importExportTo(description, exportsTo);
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
			//error: there are more than one plugin capable of working with the same pair
			return null;
		}	
	}
	
	
	public ImportExportPlugin getImporter(String description, String ImportFrom)
	{
		Iterator<PluginDescription> it = importers.iterator();
		Set<ImportExportPlugin> returnSet = new HashSet<ImportExportPlugin>();
		
		while(it.hasNext())
		{
			ImportExportPlugin plugin = it.next().importExportTo(description, ImportFrom);
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
			//error: there are more than one plugin capable of working with the same pair
			return null;
		}	
	}
	
	public void registerDataLoader(FileIOPlugin plugin, String t)
	{
		if(plugin != null)
		{
			dataLoaders.add(new PluginDescription(plugin,t,null));
		}
	}
	
	public void registerDataSaver(FileIOPlugin plugin, String t)
	{
		if(plugin != null)
		{
			dataSavers.add(new PluginDescription(plugin,t,null));
		}
	}
	
	public void registerMetaLoader(FileIOPlugin plugin, String t, String m)
	{
		if(plugin != null)
		{
			
			metaLoaders.add(new PluginDescription(plugin, t,m));
		}
	}
	
	public void registerMetaSaver(FileIOPlugin plugin, String t, String m)
	{
		if(plugin != null)
		{
			metaSavers.add(new PluginDescription(plugin,t,m));
		}
	}
	
	public void registerImport(ImportExportPlugin plugin, String description, String importsTo)
	{
		if(plugin != null)
		{
			importers.add(new PluginDescription(plugin, description, importsTo));
		}
	}
	
	public void registerExport(ImportExportPlugin plugin, String description, String exportsTo)
	{
		if(plugin != null)
		{
			exporters.add(new PluginDescription(plugin, description, exportsTo));
		}
		
	}
	
	public Set<ImportExportPlugin> getExporters(String type)
	{
		Set<ImportExportPlugin> returnSet= new HashSet<ImportExportPlugin>();
		Iterator<PluginDescription> it = exporters.iterator();
		
		while(it.hasNext())
		{
			ImportExportPlugin plugin = it.next().exportFromType(type);
			if( plugin != null)
			{
				returnSet.add(plugin);
			}
		}
		return returnSet;
	}
	
	
	public Set<ImportExportPlugin> getImporters(String type)
	{
		Set<ImportExportPlugin> returnSet= new HashSet<ImportExportPlugin>();
		Iterator<PluginDescription> it = importers.iterator();
		
		while(it.hasNext())
		{
			ImportExportPlugin plugin = it.next().importToType(type);
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

		public ImportExportPlugin exportFromType(String description)
		{
			if(tag.equals(description))
			{
				return (ImportExportPlugin)plugin;
			}
			return null;
		}
		
		//return the plugin case the "exportExtension" is the same as the one given as paramether
		//return null, otherwise
		public ImportExportPlugin importToType(String description)
		{
			if(((ImportExportPlugin)plugin).getExportExtension().equals(description))
			{
				return (ImportExportPlugin)plugin;
			}
			return null;
			
		}
		
	}
	
	

}
