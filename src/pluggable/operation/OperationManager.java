/**
 * 
 */
package pluggable.operation;

import java.util.Hashtable;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

/**
 * @author Lenko Grigorov
 *
 */
public class OperationManager {

	protected static TreeMap<String,Operation> operations=new TreeMap<String,Operation>();
	
	public static List<String> getOperationNames()
	{
		Vector<String> list=new Vector<String>(operations.size());
		list.addAll(operations.keySet());
		return list;
	}
	
	public static void register(Operation o)
	{
		operations.put(o.getName(),o);
	}
	
	public static Operation getOperation(String name)
	{
		return operations.get(name);
	}
}
