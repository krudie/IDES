/**
 * 
 */
package pluggable.operation;

import java.util.List;
import java.util.Vector;

/**
 * @author Lenko Grigorov
 *
 */
public class OperationManager {

	public static List<String> getAllOperations()
	{
		Vector<String> list=new Vector<String>();
		list.add("product");
		return list;
	}
	
}
