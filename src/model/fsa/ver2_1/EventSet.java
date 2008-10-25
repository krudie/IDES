package model.fsa.ver2_1;

import ides.api.model.fsa.FSAEvent;
import ides.api.plugin.model.DESEvent;
import ides.api.plugin.model.DESEventSet;

import java.util.Collection;
import java.util.HashSet;

public class EventSet extends HashSet<DESEvent> implements DESEventSet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6417657113613113965L;

	public static DESEventSet wrap(Collection<FSAEvent> c)
	{
		DESEventSet ret = new EventSet();
		for (FSAEvent e : c)
		{
			ret.add(e);
		}
		return ret;
	}

	public DESEventSet copy()
	{
		DESEventSet ret = new EventSet();
		for (DESEvent e : this)
		{
			ret.add(new Event(e));
		}
		return ret;
	}

	public DESEventSet intersect(DESEventSet set)
	{
		DESEventSet copy = copy();
		copy.retainAll(set);
		return copy;
	}

	public DESEventSet subtract(DESEventSet set)
	{
		DESEventSet copy = copy();
		copy.removeAll(set);
		return copy;
	}

	public DESEventSet union(DESEventSet set)
	{
		DESEventSet copy1 = copy();
		DESEventSet copy2 = set.copy();
		copy1.addAll(copy2);
		return copy1;
	}
}
