package pluggable.layout;

import ides.api.core.Annotable;
import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.plugin.model.DESModel;

import java.util.Iterator;

public class CompositeStateLabeller
{

	public static void labelStates(FSAModel fsa)
	{
		if (fsa.getAnnotation(Annotable.COMPOSED_OF) == null)
		{
			return;
		}
		if (((String[])fsa.getAnnotation(Annotable.COMPOSED_OF)).length > 1)
		{
			FSAModel[] gs = new FSAModel[((String[])fsa
					.getAnnotation(Annotable.COMPOSED_OF)).length];
			for (int i = 0; i < gs.length; ++i)
			{
				DESModel m = Hub.getWorkspace().getModel(((String[])fsa
						.getAnnotation(Annotable.COMPOSED_OF))[i]);
				if (m == null || !(m instanceof FSAModel))
				{
					return;
				}
				gs[i] = (FSAModel)m;
			}
			for (Iterator<FSAState> si = fsa.getStateIterator(); si.hasNext();)
			{
				FSAState s = si.next();
				FSAState subState;
				String label = "(";
				for (int i = 0; i < gs.length - 1; ++i)
				{
					subState = gs[i].getState(((long[])s
							.getAnnotation(Annotable.COMPOSED_OF))[i]);
					if (subState == null)
					{
						return;
					}
					label += subState.getName() + ",";
				}
				subState = gs[gs.length - 1].getState(((long[])s
						.getAnnotation(Annotable.COMPOSED_OF))[gs.length - 1]);
				if (subState == null)
				{
					return;
				}
				label += subState.getName() + ")";
				s.setName(label);
			}
		}
		else if (((String[])fsa.getAnnotation(Annotable.COMPOSED_OF)).length == 1)
		{
			DESModel m = Hub.getWorkspace().getModel(((String[])fsa
					.getAnnotation(Annotable.COMPOSED_OF))[0]);
			if (m == null || !(m instanceof FSAModel))
			{
				return;
			}
			FSAModel g = (FSAModel)m;
			for (Iterator<FSAState> si = fsa.getStateIterator(); si.hasNext();)
			{
				FSAState s = si.next();
				String label = "";
				if (((long[])s.getAnnotation(Annotable.COMPOSED_OF)).length > 1)
				{
					label = "(";
					FSAState subState;
					for (int i = 0; i < ((long[])s
							.getAnnotation(Annotable.COMPOSED_OF)).length - 1; ++i)
					{
						subState = g.getState(((long[])s
								.getAnnotation(Annotable.COMPOSED_OF))[i]);
						if (subState == null)
						{
							return;
						}
						label += subState.getName() + ",";
					}
					subState = g.getState(((long[])s
							.getAnnotation(Annotable.COMPOSED_OF))[((long[])s
							.getAnnotation(Annotable.COMPOSED_OF)).length - 1]);
					if (subState == null)
					{
						return;
					}
					label += subState.getName() + ")";
				}
				else if (((long[])s.getAnnotation(Annotable.COMPOSED_OF)).length > 0)
				{
					FSAState subState = g.getState(((long[])s
							.getAnnotation(Annotable.COMPOSED_OF))[0]);
					if (subState == null)
					{
						return;
					}
					label = subState.getName();
				}
				s.setName(label);
			}
		}
	}

}
