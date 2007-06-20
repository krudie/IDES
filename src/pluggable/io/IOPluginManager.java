/**
 * 
 */
package pluggable.io;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * @author christiansilvano
 *
 */
public class IOPluginManager {

//	Singleton instance:
	private static IOPluginManager instance = null;
	private Set<FileIOPlugin> metaSavers = null;
	private Set<FileIOPlugin> dataSavers = null;
	private Set<FileIOPlugin> dataLoaders = null;
	private Set <FileIOPlugin> metaLoaders = null;
	
	
	
	private IOPluginManager()
	{
		metaSavers = new HashSet<FileIOPlugin>();
		dataSavers = new HashSet<FileIOPlugin>();
		metaLoaders = new HashSet<FileIOPlugin>();
		dataLoaders = new HashSet<FileIOPlugin>();
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
			FileIOPlugin plugin = (FileIOPlugin)it.next();
			if(plugin.getIOTypeDescriptor().equals(type))
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
			FileIOPlugin plugin = (FileIOPlugin)it.next();
			if(plugin.getIOTypeDescriptor().equals(type))
			{
				returnSet.add(plugin);
			}
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
			FileIOPlugin plugin = (FileIOPlugin)it.next();
			if(plugin.getIOTypeDescriptor().equals(type))
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
	
	public void registerDataLoader(FileIOPlugin plugin)
	{
		if(plugin != null)
		{
			dataLoaders.add(plugin);
		}
	}
	
	public void registerDataSaver(FileIOPlugin plugin)
	{
		if(plugin != null)
		{
			dataSavers.add(plugin);
		}
	}
	
	public void registerMetaLoader(FileIOPlugin plugin)
	{
		if(plugin != null)
		{
			metaLoaders.add(plugin);
		}
	}
	
	public void registerMetaSaver(FileIOPlugin plugin)
	{
		if(plugin != null)
		{
			metaSavers.add(plugin);
		}
	}

}
