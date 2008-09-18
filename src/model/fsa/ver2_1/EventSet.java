package model.fsa.ver2_1;

import ides.api.model.fsa.FSAEvent;
import ides.api.model.fsa.FSAEventSet;

import java.util.Collection;
import java.util.HashSet;

public class EventSet extends HashSet<FSAEvent> implements FSAEventSet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6417657113613113965L;

	public static FSAEventSet wrap(Collection<FSAEvent> c)
	{
		FSAEventSet ret = new EventSet();
		for (FSAEvent e : c)
		{
			ret.add(e);
		}
		return ret;
	}

	public FSAEventSet copy()
	{
		FSAEventSet ret = new EventSet();
		for (FSAEvent e : this)
		{
			ret.add(new Event(e));
		}
		return ret;
	}

	public FSAEventSet intersect(FSAEventSet set)
	{
		FSAEventSet copy = copy();
		copy.retainAll(set);
		return copy;
	}

	public FSAEventSet subtract(FSAEventSet set)
	{
		FSAEventSet copy = copy();
		copy.removeAll(set);
		return copy;
	}

	public FSAEventSet union(FSAEventSet set)
	{
		FSAEventSet copy1 = copy();
		FSAEventSet copy2 = set.copy();
		copy1.addAll(copy2);
		return copy1;
	}
}
