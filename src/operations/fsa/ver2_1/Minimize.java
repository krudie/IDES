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

	protected class Splitter
	{
		public Collection<FSAState> part;

		public FSAEvent event;

		public Splitter(Collection<FSAState> part, FSAEvent event)
		{
			this.part = part;
			this.event = event;
		}

		public int hashCode()
		{
			return event.hashCode() * part.size();
		}

		public boolean equals(Object o)
		{
			if (o == null || !(o instanceof Splitter))
			{
				return false;
			}
			return part.equals(((Splitter)o).part)
					&& event.equals(((Splitter)o).event);
		}
		
		public String toString()
		{
			return part.toString()+","+event.toString();
		}
	}

	protected Collection<Collection<FSAState>> split(Collection<FSAState> part,
			Splitter split)
	{
		Collection<FSAState> partIn = new HashSet<FSAState>();
		Collection<FSAState> partOut = new HashSet<FSAState>();
		for (FSAState s : part)
		{
			FSAState target = goingTo(s, split.event);
			if (split.part.contains(target))
			{
				partIn.add(s);
			}
			else
			{
				partOut.add(s);
			}
		}
		Collection<Collection<FSAState>> ret = new HashSet<Collection<FSAState>>();
		if (partIn.isEmpty())
		{
			ret.add(partOut);
		}
		else if (partOut.isEmpty())
		{
			ret.add(partIn);
		}
		else
		{
			ret.add(partIn);
			ret.add(partOut);
		}
		return ret;
	}

	protected FSAState goingTo(FSAState s, FSAEvent e)
	{
		FSAState ret = null;
		for (Iterator<FSATransition> it = s
				.getOutgoingTransitionsListIterator(); it.hasNext();)
		{
			FSATransition t = it.next();
			if (e.equals(t.getEvent()))
			{
				ret = t.getTarget();
				break;
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
		Collection<Splitter> splitters = new HashSet<Splitter>();

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
		if(f.isEmpty())
		{
			warnings.add("No final states.");
			return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
		}
		parts.add(f);
		parts.add(nf);
		for (Iterator<FSAEvent> ie = fsa.getEventIterator(); ie.hasNext();)
		{
			if (f.size() < nf.size())
			{
				splitters.add(new Splitter(f, ie.next()));
			}
			else
			{
				splitters.add(new Splitter(nf, ie.next()));
			}
		}

		// get equivalence sets
		while (!splitters.isEmpty())
		{
			Splitter split = splitters.iterator().next();
			splitters.remove(split);
			Collection<Collection<FSAState>> newParts=new HashSet<Collection<FSAState>>();
			for(Collection<FSAState> part:parts)
			{
				Collection<Collection<FSAState>> splits=split(part,split);
				newParts.addAll(splits);
				if(splits.size()>1)
				{
					Iterator<Collection<FSAState>> is=splits.iterator();
					Collection<FSAState> part1=is.next();
					Collection<FSAState> part2=is.next();
					Collection<Splitter> newSplitters=new HashSet<Splitter>();
					for(Splitter splitter:splitters)
					{
						if(splitter.part.equals(part))
						{
							newSplitters.add(new Splitter(part1,splitter.event));
							newSplitters.add(new Splitter(part2,splitter.event));
						}
						else
						{
							newSplitters.add(splitter);
						}
					}
					for (Iterator<FSAEvent> ie = fsa.getEventIterator(); ie.hasNext();)
					{
						if (part1.size() < part2.size())
						{
							newSplitters.add(new Splitter(part1, ie.next()));
						}
						else
						{
							newSplitters.add(new Splitter(part2, ie.next()));
						}
					}
					splitters=newSplitters;
				}
			}
			parts=newParts;
		}
		parts.remove(new HashSet<FSAState>());

		//construct minimal FSA
		FSAModel ret=ModelManager.instance().createModel(FSAModel.class);
		ret.setAnnotation(Annotable.COMPOSED_OF, new String[]{fsa.getName()});
		Map<FSAEvent,FSAEvent> eventMap=new HashMap<FSAEvent, FSAEvent>();
		for (Iterator<FSAEvent> ie = fsa.getEventIterator(); ie.hasNext();)
		{
			FSAEvent e=ie.next();
			FSAEvent event=ret.assembleEvent(e.getSymbol());
			event.setControllable(e.isControllable());
			event.setObservable(e.isObservable());
			ret.add(event);
			eventMap.put(e, event);
		}
		
		Map<Collection<FSAState>,FSAState> stateMap=new HashMap<Collection<FSAState>, FSAState>();
		for(Collection<FSAState> part:parts)
		{
			FSAState state=ret.assembleState();
			state.setInitial(false);
			state.setMarked(false);
			long[] ids=new long[part.size()];
			String[] names=new String[part.size()];
			int idx=0;
			for(FSAState s:part)
			{
				ids[idx]=s.getId();
				names[idx]=s.getName();
				if(s.isInitial())
				{
					state.setInitial(true);
				}
				if(s.isMarked())
				{
					state.setMarked(true);
				}
				++idx;
			}
			state.setAnnotation(Annotable.COMPOSED_OF, ids);
			state.setAnnotation(Annotable.COMPOSED_OF_NAMES, names);
			ret.add(state);
			stateMap.put(part,state);
		}
		
		for(Collection<FSAState> part:parts)
		{
			FSAState rep=part.iterator().next();
			for(Iterator<FSATransition> it=rep.getOutgoingTransitionsListIterator();it.hasNext();)
			{
				FSATransition t=it.next();
				Collection<FSAState> target=null;
				for(Collection<FSAState> p:parts)
				{
					if(p.contains(t.getTarget()))
					{
						target=p;
						break;
					}
				}
				FSATransition transition=ret.assembleTransition(stateMap.get(part).getId(), stateMap.get(target).getId(), eventMap.get(t.getEvent()).getId());
				ret.add(transition);
			}
		}
		
		return new Object[] { ret };
	}

}
