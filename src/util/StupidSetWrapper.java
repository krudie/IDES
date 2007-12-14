package util;

import java.util.Collection;
import java.util.HashSet;

import model.fsa.FSAEvent;
import model.fsa.FSAEventSet;

// FIXME in a perfect world, we don't need a stupid wrapper...
public class StupidSetWrapper extends HashSet<FSAEvent> implements FSAEventSet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8107817319810373447L;

	public StupidSetWrapper(Collection<FSAEvent> c)
	{
		super(c);
	}

	public StupidSetWrapper()
	{
		super();
	}
}
