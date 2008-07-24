/**
 * 
 */
package ides.api.plugin.operation;

import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

/**
 * @author Lenko Grigorov
 */
public class OperationManager
{
	// prevent instantiation
	private OperationManager()
	{
	}

	@Override
	public Object clone()
	{
		throw new RuntimeException("Cloning of " + this.getClass().toString()
				+ " not supported.");
	}

	/**
	 * Instance for the non-static methods.
	 */
	private static OperationManager me = null;

	public static OperationManager instance()
	{
		if (me == null)
		{
			me = new OperationManager();
		}
		return me;
	}

	protected TreeMap<String, Operation> operations = new TreeMap<String, Operation>();

	public List<String> getOperationNames()
	{
		Vector<String> list = new Vector<String>(operations.size());
		list.addAll(operations.keySet());
		return list;
	}

	public void register(Operation o)
	{
		operations.put(o.getName(), o);
	}

	public Operation getOperation(String name)
	{
		return operations.get(name);
	}

	public FilterOperation getFilterOperation(String name)
	{
		if (operations.get(name) instanceof FilterOperation)
		{
			return (FilterOperation)operations.get(name);
		}
		else
		{
			return null;
		}
	}
}
