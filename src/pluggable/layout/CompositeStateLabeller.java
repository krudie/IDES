package pluggable.layout;

import java.util.Iterator;

import main.Annotable;
import main.Hub;
import model.DESModel;
import model.fsa.FSAModel;
import model.fsa.FSAState;
import model.fsa.ver2_1.State;

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
				DESModel m = Hub.getWorkspace().getModelById(((String[])fsa
						.getAnnotation(Annotable.COMPOSED_OF))[i]);
				if (m == null || !(m instanceof FSAModel))
				{
					return;
				}
				gs[i] = (FSAModel)m;
			}
			for (Iterator<FSAState> si = fsa.getStateIterator(); si.hasNext();)
			{
				State s = (State)si.next();
				FSAState subState;
				String label = "(";
				for (int i = 0; i < gs.length - 1; ++i)
				{
					subState = gs[i].getState(s.getStateCompositionList()[i]);
					if (subState == null)
					{
						return;
					}
					label += subState.getName() + ",";
				}
				subState = gs[gs.length - 1].getState(s
						.getStateCompositionList()[gs.length - 1]);
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
			DESModel m = Hub.getWorkspace().getModelById(((String[])fsa
					.getAnnotation(Annotable.COMPOSED_OF))[0]);
			if (m == null || !(m instanceof FSAModel))
			{
				return;
			}
			FSAModel g = (FSAModel)m;
			for (Iterator<FSAState> si = fsa.getStateIterator(); si.hasNext();)
			{
				State s = (State)si.next();
				String label = "";
				if (s.getStateCompositionList().length > 1)
				{
					label = "(";
					FSAState subState;
					for (int i = 0; i < s.getStateCompositionList().length - 1; ++i)
					{
						subState = g.getState(s.getStateCompositionList()[i]);
						if (subState == null)
						{
							return;
						}
						label += subState.getName() + ",";
					}
					subState = g.getState(s.getStateCompositionList()[s
							.getStateCompositionList().length - 1]);
					if (subState == null)
					{
						return;
					}
					label += subState.getName() + ")";
				}
				else if (s.getStateCompositionList().length > 0)
				{
					FSAState subState = g
							.getState(s.getStateCompositionList()[0]);
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
