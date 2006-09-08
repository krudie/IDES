/**
 * 
 */
package pluggable.layout;

import pluggable.layout.jung.JUNGLayouter;

/**
 *
 * @author Lenko Grigorov
 */
public class LayoutManager {
	
	protected static FSALayouter l=new JUNGLayouter();

	public static FSALayouter getDefaultFSMLayouter()
	{
		return l;
	}
	
}
