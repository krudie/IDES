package operations.fsa.ver2_1;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import ides.api.model.fsa.FSAEvent;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.plugin.model.DESEventSet;
import ides.api.plugin.operation.CheckingToolbox;
import ides.api.plugin.operation.Operation;

/** algorithm taken from "A Course in Formal Languages, Automata and Groups" by Ian M. Chiswell
 * Springer London, 2009.
 * 
 * Construct a new FSA, whose transition diagram is the union of the 
 * transition diagrams for M1 and M2, modified as follows. There is one 
 * extra vertex as initial state and two extra edges from this new 
 * vertex to the initial states of M1 and M2, having label "epsilon". The final 
 * states are those of M1 and M2.
 * 
 * @author Valerie Sugarman
 */
public class UnionOperation implements Operation
{

	private LinkedList<String> warnings = new LinkedList<String>();


	public String getDescription()
	{

		return "Computes the union of the languages represented by the given automata.";
	}


	public String[] getDescriptionOfInputs()
	{

		return new String[] { "Finite-State automata" };

	}


	public String[] getDescriptionOfOutputs()
	{

		return new String[] { "Union of the automata" };
	}


	public String getName()
	{

		return "union";
	}


	public int getNumberOfInputs()
	{

		return -1;
	}


	public int getNumberOfOutputs()
	{

		return 1;
	}


	public Class<?>[] getTypeOfInputs()
	{

		return new Class<?>[] { FSAModel.class };

	}

	
	public Class<?>[] getTypeOfOutputs()
	{

		return new Class<?>[] { FSAModel.class };
	}

	public List<String> getWarnings()
	{
		return warnings;
	}

	public Object[] perform(Object[] arg0)
	{

		warnings.clear();
		FSAModel model1;
		FSAModel model2;
		Stack<FSAModel> models = new Stack<FSAModel>();

		// Verify validity of parameters
		if (arg0.length >= 1)
		{
			for (int i = 0; i < arg0.length; i++)
			{
				if (arg0[i] instanceof FSAModel)
				{
					if (i == arg0.length - 1)
					{
						models.push(((FSAModel)arg0[i]).clone());
					}
					else
					{
						models.push(((FSAModel)arg0[i]));
					}
				}
				else
				{
					String error = "Illegal argument, FSAModels expected for union operation";
					warnings.add(error);
					return new Object[] { ides.api.plugin.model.ModelManager
							.instance().createModel(FSAModel.class) };
				}
			}
		}
		else
		// if input array is empty
		{
			return new Object[] { ides.api.plugin.model.ModelManager
					.instance().createModel(FSAModel.class) };
		}

		while (models.size() >= 2)
		{

			/*
			 * In the following hash table, the original state is the key, copy
			 * is the value. This is used to avoid duplication when copying
			 * states from one model to another
			 */
			Hashtable<FSAState, FSAState> states = new Hashtable<FSAState, FSAState>();
			Hashtable<String, FSAEvent> events = new Hashtable<String, FSAEvent>();

			model1 = models.pop();
			model2 = models.pop();

			// Make sure there is exactly one initial state in each model and
			// inform the user and return and empty model if not
			if ((CheckingToolbox.initialStateCount(model1) != 1)
					|| (CheckingToolbox.initialStateCount(model2) != 1))
			{
				String error = CheckingToolbox.NOT_1_INITIAL_STATE;
				warnings.add(error);
				return new Object[] { ides.api.plugin.model.ModelManager
						.instance().createModel(FSAModel.class) };
			}

			/* Identify the initial states of the model1s */
			FSAState model1initial = DuplicationToolbox.getInitial(model1);
			FSAState model2initial = DuplicationToolbox.getInitial(model2);

			/*
			 * Add the events in model1 to the hashtable "events". Later on, if
			 * an event with the same name exists in model2, it will not be
			 * added, but the event (with the same name) in model1 will be
			 * retrieved.
			 */
			for (ListIterator<FSAEvent> b = model1.getEventIterator(); b
					.hasNext();)
			{
				FSAEvent currEvent = b.next();
				events.put(currEvent.getSymbol(), currEvent);
			}

			if (model2.getTransitionCount() > 0)
			{
				for (ListIterator<FSATransition> d = model2
						.getTransitionIterator(); d.hasNext();)
				{
					FSATransition origTransition = d.next();

					FSAState origSource = origTransition.getSource();
					FSAState copySource = DuplicationToolbox.copyStateInto(model1,
							origSource,
							states,
							false);

					FSAState origTarget = origTransition.getTarget();
					FSAState copyTarget = DuplicationToolbox.copyStateInto(model1,
							origTarget,
							states,
							false);

					FSAEvent origEvent = origTransition.getEvent();

					FSATransition copyTransition;
					if (origEvent == null)
					{
						copyTransition = model1
								.assembleEpsilonTransition(copySource.getId(),
										copyTarget.getId());
					}
					else
					{
						FSAEvent copyEvent = DuplicationToolbox
								.copyEventInto(model1, origEvent, events, false);
						copyTransition = model1
								.assembleTransition(copySource.getId(),
										copyTarget.getId(),
										copyEvent.getId());
					}

					model1.add(copyTransition);
				}
			}
			else
			{ // no transitions in model2

				DuplicationToolbox.copyAllStates(model1, model2, states, false);
				DuplicationToolbox.copyAllEvents(model1, model2, events, false);

				/*
				 * special case of union of two states with no transitions in
				 * either just join them by a null transition. since there are
				 * no transitions there is no real language anyways, so joining
				 * no language by no words is the same thing (I got this from
				 * testing with fsm-3.7 (from
				 * http://www2.research.att.com/~fsmtools/fsm/)
				 */
				if (model1.getTransitionCount() == 0)
				{
					model2initial = states.get(model2initial);
					model2initial.setInitial(false);

					FSATransition toModel2 = model1
							.assembleEpsilonTransition(model1initial.getId(),
									model2initial.getId());
					model1.add(toModel2);

					DESEventSet des = ides.api.plugin.model.ModelManager
							.instance().createEmptyEventSet();
					model1 = (FSAModel)ides.api.plugin.operation.OperationManager
							.instance().getOperation("projectcustom")
							.perform(new Object[] { model1, des })[0];

					return new Object[] { model1 };
				}

			}
			// get the corresponding copy of the original initial event, now
			// that the components
			// have been copied over
			model2initial = states.get(model2initial);

			model1initial.setInitial(false);
			model2initial.setInitial(false);

			// Create the dummy initial state.
			FSAState newInitial = model1.assembleState();
			newInitial.setInitial(true);
			newInitial.setName("");
			model1.add(newInitial);

			FSATransition toModel1 = model1
					.assembleEpsilonTransition(newInitial.getId(),
							model1initial.getId());
			model1.add(toModel1);
			FSATransition toModel2 = model1
					.assembleEpsilonTransition(newInitial.getId(),
							model2initial.getId());
			model1.add(toModel2);

			/* project out the null string transitions (no associated event) */
			model1 = (FSAModel)ides.api.plugin.operation.OperationManager
					.instance().getOperation("removeepsilon")
					.perform(new Object[] { model1 })[0];

			models.push(model1);

		}

		model1 = models.pop();

		return new Object[] { model1 };
	}

}