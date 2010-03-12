package operations.fsa.ver2_1;

import ides.api.core.Annotable;
import ides.api.model.fsa.FSAEvent;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.operation.OperationManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * State minimization of deterministic finite state automata using Hopcroft's
 * algorithm. The implementation is inspired by the publication &quot;Describing
 * an n log n algorithm for minimizing states in deterministic finite
 * automaton&quot; by Yingjie Xu (Stellenbosch University, South Africa).
 * 
 * @author Lenko Grigorov
 */
public class Minimize extends AbstractOperation
{

	public Minimize()
	{
		NAME = "minimize";
		DESCRIPTION = "Computes an automaton that accepts the"
				+ " same language as the input automaton and has the minimum number of states.";

		// WARNING - Ensure that input type and description always match!
		inputType = new Class[] { FSAModel.class };
		inputDesc = new String[] { "Finite-state automaton" };

		// WARNING - Ensure that output type and description always match!
		outputType = new Class[] { FSAModel.class };
		outputDesc = new String[] { "Minimized automaton" };
	}

	protected Collection<FSAState> comingFrom(Collection<FSAState> part,
			FSAEvent e)
	{
		Collection<FSAState> ret = new HashSet<FSAState>();
		for (FSAState s : part)
		{
			for (Iterator<FSATransition> it = s
					.getIncomingTransitionsListIterator(); it.hasNext();)
			{
				FSATransition t = it.next();
				if (e.equals(t.getEvent()))
				{
					ret.add(t.getSource());
				}
			}

		}
		return ret;
	}

	public Object[] perform(Object[] inputs)
	{
		warnings.clear();
		FSAModel fsa = (FSAModel)OperationManager
				.instance().getOperation("accessible").perform(inputs)[0];
		Collection<Collection<FSAState>> parts = new HashSet<Collection<FSAState>>();
		Collection<Collection<FSAState>> splitters = new HashSet<Collection<FSAState>>();

		// initialize
		Collection<FSAState> nf = new HashSet<FSAState>();
		Collection<FSAState> f = new HashSet<FSAState>();
		for (Iterator<FSAState> is = fsa.getStateIterator(); is.hasNext();)
		{
			FSAState s = is.next();
			if (s.isMarked())
			{
				f.add(s);
			}
			else
			{
				nf.add(s);
			}
		}
		if (f.isEmpty())
		{
			warnings.add("No final states.");
			return new Object[] { ModelManager
					.instance().createModel(FSAModel.class) };
		}
		parts.add(f);
		parts.add(nf);
		parts.remove(new HashSet<FSAState>());
		splitters.add(f);
		splitters.add(nf);
		splitters.remove(new HashSet<FSAState>());

		// get equivalence sets
		while (!splitters.isEmpty())
		{
			Collection<FSAState> splitter = splitters.iterator().next();
			splitters.remove(splitter);
			for (Iterator<FSAEvent> ie = fsa.getEventIterator(); ie.hasNext();)
			{
				FSAEvent e = ie.next();
				Collection<FSAState> sources = comingFrom(splitter, e);
				Collection<Collection<FSAState>> newParts = new HashSet<Collection<FSAState>>();
				for (Collection<FSAState> part : parts)
				{
					if (!sources.containsAll(part))
					{
						Collection<FSAState> intersect = new HashSet<FSAState>(
								part);
						intersect.retainAll(sources);
						if (intersect.isEmpty())
						{
							newParts.add(part);
						}
						else
						{
							Collection<FSAState> part1 = intersect;
							Collection<FSAState> part2 = new HashSet<FSAState>(
									part);
							part2.removeAll(intersect);
							newParts.add(part1);
							newParts.add(part2);
							if (splitters.contains(part))
							{
								splitters.remove(part);
								splitters.add(part1);
								splitters.add(part2);
							}
							else
							{
								if (part1.size() < part2.size())
								{
									splitters.add(part1);
								}
								else
								{
									splitters.add(part2);
								}
							}
						}
					}
					else
					{
						newParts.add(part);
					}
				}
				parts = newParts;
				parts.remove(new HashSet<FSAState>());
			}
		}

		// construct minimal FSA
		FSAModel ret = ModelManager.instance().createModel(FSAModel.class);
		ret
				.setAnnotation(Annotable.COMPOSED_OF, new String[] { fsa
						.getName() });
		Map<FSAEvent, FSAEvent> eventMap = new HashMap<FSAEvent, FSAEvent>();
		for (Iterator<FSAEvent> ie = fsa.getEventIterator(); ie.hasNext();)
		{
			FSAEvent e = ie.next();
			FSAEvent event = ret.assembleEvent(e.getSymbol());
			event.setControllable(e.isControllable());
			event.setObservable(e.isObservable());
			ret.add(event);
			eventMap.put(e, event);
		}

		Map<Collection<FSAState>, FSAState> stateMap = new HashMap<Collection<FSAState>, FSAState>();
		for (Collection<FSAState> part : parts)
		{
			FSAState state = ret.assembleState();
			state.setInitial(false);
			state.setMarked(false);
			long[] ids = new long[part.size()];
			String[] names = new String[part.size()];
			int idx = 0;
			for (FSAState s : part)
			{
				ids[idx] = s.getId();
				names[idx] = s.getName();
				if (s.isInitial())
				{
					state.setInitial(true);
				}
				if (s.isMarked())
				{
					state.setMarked(true);
				}
				++idx;
			}
			state.setAnnotation(Annotable.COMPOSED_OF, ids);
			state.setAnnotation(Annotable.COMPOSED_OF_NAMES, names);
			ret.add(state);
			stateMap.put(part, state);
		}

		for (Collection<FSAState> part : parts)
		{
			FSAState rep = part.iterator().next();
			for (Iterator<FSATransition> it = rep
					.getOutgoingTransitionsListIterator(); it.hasNext();)
			{
				FSATransition t = it.next();
				Collection<FSAState> target = null;
				for (Collection<FSAState> p : parts)
				{
					if (p.contains(t.getTarget()))
					{
						target = p;
						break;
					}
				}
				FSATransition transition = ret.assembleTransition(stateMap
						.get(part).getId(),
						stateMap.get(target).getId(),
						eventMap.get(t.getEvent()).getId());
				ret.add(transition);
			}
		}

		return new Object[] { ret };
	}

}
