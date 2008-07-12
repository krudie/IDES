package model;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

/**
 * The manager of model types available to IDES. This allows for custom model types
 * implemented by plugins.
 * 
 * @see DESModelType
 * @see #registerModel(DESModelType)
 * @author Lenko Grigorov
 */
public class ModelManager
{

	/**
	 * Index of all registered {@link DESModelType}s. The key is the DES
	 * model interface implemented by the model type. The value is the set of the
	 * model types of all models implementing the given interface. Note
	 * that if a model type implements a number of interfaces, its model type
	 * will be included in the set for each one of these interfaces.
	 * 
	 * @see #registerModel(DESModelType)
	 */
	protected static Hashtable<Class<?>, Set<DESModelType>> class2Model = new Hashtable<Class<?>, Set<DESModelType>>();

	/**
	 * Creates and returns a DES model for the supplied interface, with the
	 * given display name.
	 * 
	 * @param iface
	 *            the interface for which a model has to be created
	 * @param name
	 *            the display name for the new model
	 * @return a new DES model for the given interface. If no model was
	 *         registered for this interface, returns <code>null</code>.
	 * @see #createModel(Class)
	 */
	public static <T> T createModel(Class<T> iface, String name)
	{
		Set<DESModelType> s = class2Model.get(iface);
		if (s.isEmpty())
		{
			return null;
		}
		else
		{
			return iface.cast(s.iterator().next().createModel(name));
		}
	}

	/**
	 * Creates and returns a DES model for the supplied interface, with an empty
	 * display name.
	 * 
	 * @param iface
	 *            the interface for which a model has to be created
	 * @return a new DES model for the given interface. If no model was
	 *         registered for this interface, returns <code>null</code>.
	 * @see #createModel(Class, String)
	 */
	public static <T> T createModel(Class<T> iface)
	{
		return createModel(iface, "");
	}

	/**
	 * Returns all model types that implement the given
	 * interface.
	 * 
	 * @param iface
	 *            the interface for which model types should be returned
	 * @return the model types that implement the interface
	 * @see #getAllTypes()
	 */
	public static DESModelType[] getModelsForInterface(Class<?> iface)
	{
		return class2Model.get(iface).toArray(new DESModelType[0]);
	}

	/**
	 * Returns all model types registered with the manager.
	 * 
	 * @return all model types registerd with the manager
	 * @see #getModelsForInterface(Class)
	 */
	public static DESModelType[] getAllTypes()
	{
		Set<DESModelType> all = new TreeSet<DESModelType>(
				new Comparator<DESModelType>()
				{
					public int compare(DESModelType md1, DESModelType md2)
					{
						return md1.getDescription().compareTo(md2
								.getDescription());
					}
				});
		for (Enumeration<Set<DESModelType>> i = class2Model.elements(); i
				.hasMoreElements();)
		{
			all.addAll(i.nextElement());
		}
		return all.toArray(new DESModelType[0]);
	}

	/**
	 * Registers a model type so that the corresponding DES model type becomes
	 * available to IDES.
	 * 
	 * @param mt
	 *            the model type for the DES model
	 */
	public static void registerModel(DESModelType mt)
	{
		Class<?>[] ifaces = mt.getModelInterfaces();
		for (int i = 0; i < ifaces.length; ++i)
		{
			Set<DESModelType> set;
			if (class2Model.containsKey(ifaces[i]))
			{
				set = class2Model.get(ifaces[i]);
			}
			else
			{
				set = new HashSet<DESModelType>();
			}
			set.add(mt);
			class2Model.put(ifaces[i], set);
		}
	}
}
