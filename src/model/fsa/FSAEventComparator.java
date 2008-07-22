/**
 * 
 */
package model.fsa;

import java.util.Comparator;

/**
 * @author Lenko Grigorov
 */
public class FSAEventComparator implements Comparator<FSAEvent>
{

	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(T, T)
	 */
	public int compare(FSAEvent arg0, FSAEvent arg1)
	{
		return arg0.getSymbol().compareTo(arg1.getSymbol());
	}

}
