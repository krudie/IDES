package model;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import services.General;

import model.fsa.FSAModel;
import model.fsa.ver2_1.Automaton;

/**
 * The manager of models available to IDES. This allows for
 * custom model types implemented by plugins.
 * @see ModelDescriptor
 * @see #registerModel(ModelDescriptor)
 * @author Lenko Grigorov
 */
public class ModelManager {

	/**
	 * Index of all registered {@link ModelDescriptor}s.
	 * The key is the DES model interface implemented by the model.
	 * The value is the set of the model descriptors of all models
	 * implementing the given interface. Note that if a model implements
	 * a number of interfaces, its model descriptor will be included
	 * in the set for each one of these interfaces.
	 * @see #registerModel(ModelDescriptor)
	 */
	protected static Hashtable<Class,Set<ModelDescriptor>> class2Model=new Hashtable<Class, Set<ModelDescriptor>>();
	
	/**
	 * Creates and returns a DES model for the supplied interface,
	 * with the given display name.
	 * @param iface the interface for which a model has to be created
	 * @param name the display name for the new model 
	 * @return a new DES model for the given interface. If no model
	 * was registered for this interface, returns <code>null</code>.
	 * @see #createModel(Class) 
	 */
	public static <T> T createModel(Class<T> iface, String name)
	{
		Set<ModelDescriptor> s=class2Model.get(iface);
		if(s.isEmpty())
			return null;
		else
		{
			String id=General.getRandomId();
			return (T)s.iterator().next().createModel(name);
		}
	}

	/**
	 * Creates and returns a DES model for the supplied interface,
	 * with an empty display name.
	 * @param iface the interface for which a model has to be created
	 * @return a new DES model for the given interface. If no model
	 * was registered for this interface, returns <code>null</code>.
	 * @see #createModel(Class, String) 
	 */
	public static <T> T createModel(Class<T> iface)
	{
		return createModel(iface,"");
	}
	
	/**
	 * Returns the model descriptors for all models that implement
	 * the given interface.
	 * @param iface the interface for which model descriptors should be returned
	 * @return the model descriptors of all models that implement the interface
	 * @see #getAllModels()
	 */
	public static ModelDescriptor[] getModelsForInterface(Class iface)
	{
		return class2Model.get(iface).toArray(new ModelDescriptor[0]);
	}
	
	/**
	 * Returns all model descriptors registered with the manager.
	 * @return all model descriptors registerd with the manager
	 * @see #getModelsForInterface(Class)
	 */
	public static ModelDescriptor[] getAllModels()
	{
		Set<ModelDescriptor> all=new TreeSet<ModelDescriptor>(
				new Comparator<ModelDescriptor>()
				{
					public int compare(ModelDescriptor md1, ModelDescriptor md2)
					{
						return md1.getTypeDescription().compareTo(md2.getTypeDescription());
					}
				});
		for(Enumeration<Set<ModelDescriptor>> i=class2Model.elements();i.hasMoreElements();)
		{
			all.addAll(i.nextElement());
		}
		return all.toArray(new ModelDescriptor[0]);
	}

	/**
	 * Registers a model descriptor so that the corresponding DES model
	 * becomes available to IDES.
	 * @param md the model descriptor for the DES model
	 */
	public static void registerModel(ModelDescriptor md)
	{
		Class[] ifaces=md.getModelInterfaces();
		for(int i=0;i<ifaces.length;++i)
		{
			Set<ModelDescriptor> set;
			if(class2Model.containsKey(ifaces[i]))
				set=class2Model.get(ifaces[i]);
			else
				set=new HashSet<ModelDescriptor>();
			set.add(md);
			class2Model.put(ifaces[i], set);
		}
	}
}
