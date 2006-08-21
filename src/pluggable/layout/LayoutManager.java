/**
 * 
 */
package pluggable.layout;

import pluggable.layout.jung.JUNGLayouter;
import pluggable.layout.prefuse.PrefuseLayouter;

/**
 *
 * @author Lenko Grigorov
 */
public class LayoutManager {
	
	protected static FSMLayouter l=new JUNGLayouter();

	public static FSMLayouter getDefaultFSMLayouter()
	{
		return l;
	}
	
}
