package model;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import model.fsa.FSAModel;
import model.fsa.ver1.Automaton;

/**
 * A factory for constructing models. This isolates
 * the rest of the program from the specific implementations.
 * 
 * @author Lenko Grigorov
 */
public class ModelManager {

	protected static Hashtable<Class,Set<ModelDescriptor>> class2Model=new Hashtable<Class, Set<ModelDescriptor>>();
	
	/**
	 * Constructs an FSA model with an empty name.
	 * @return a new FSA model
	 */
	public static FSAModel getFSA()
	{
		return new Automaton("");
	}

	/**
	 * Constructs an FSA model with a given name.
	 * @param name the name for the new FSA model
	 * @return the new FSA model
	 */
	public static FSAModel getFSA(String name)
	{
		return new Automaton(name);
	}
	
	public static ModelDescriptor[] getModelsForInterface(Class iface)
	{
		return class2Model.get(iface).toArray(new ModelDescriptor[0]);
	}
	
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
