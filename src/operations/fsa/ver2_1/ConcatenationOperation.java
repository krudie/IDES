package operations.fsa.ver2_1;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import ides.api.model.fsa.FSAEvent;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.plugin.operation.CheckingToolbox;
import ides.api.plugin.operation.Operation;

/**
 * @author Valerie Sugarman
 *  
 *  
 *  algorithm taken from "A Course in Formal Languages, Automata and Groups" by Ian M. Chiswell
 * Springer London, 2009.
 * 
 * Take the union of the transition diagrams of M1 and M2, with 
 * new null string edges from the final states of M1 to the initial state of M2.
 *  The new initial state is that of M1, and the
 *  final states are those of M2.
 *  
 *  The null string transitions are then projected out of the result.
 */
public class ConcatenationOperation implements Operation
{

	private LinkedList<String> warnings = new LinkedList<String>();

	public String getDescription()
	{

		return "Computes an automaton representing the concatenation of the languages of two automata.";
	}

	public String[] getDescriptionOfInputs()
	{

		return new String[] { "Prefix finite-state automaton",
				"Suffix finite-state automaton" };
	}

	public String[] getDescriptionOfOutputs()
	{

		return new String[] { "Automaton with the concatenation" };
	}

	public String getName()
	{

		return "concat";
	}

	public int getNumberOfInputs()
	{

		return 2;
	}

	public int getNumberOfOutputs()
	{

		return 1;
	}

	public Class<?>[] getTypeOfInputs()
	{

		return new Class<?>[] { FSAModel.class, FSAModel.class };
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

		/*
		 * In the following hash table, the original state is the key, copy is
		 * the value. This is used to avoid duplication when copying states from
		 * one model to another and calling copyStatInto
		 */
		Hashtable<FSAState, FSAState> states = new Hashtable<FSAState, FSAState>();
		Hashtable<String, FSAEvent> events = new Hashtable<String, FSAEvent>();

		// Verify validity of parameters
		if (arg0.length == 2)
		{
			if ((arg0[0] instanceof FSAModel) && (arg0[1] instanceof FSAModel))
			{
				model1 = ((FSAModel)arg0[0]).clone();
				model2 = ((FSAModel)arg0[1]);
			}
			else
			{
				String error = "Illegal argument, FSAModel expected for concatenation operation";
				warnings.add(error);
				return new Object[] { ides.api.plugin.model.ModelManager
						.instance().createModel(FSAModel.class) };
			}
		}
		else
		{
			String error = "Illegal number of arguments, two expected for concatenation operation";
			warnings.add(error);
			return new Object[] { ides.api.plugin.model.ModelManager
					.instance().createModel(FSAModel.class) };
		}
		// Special case: model2 is empty and there is nothing to add to model1
		if (model2.getStateCount() == 0)
		{
			return new Object[] { model1 };
		}

		// in this operation really only important that there is an initial
		// state in model2, but FSAs representing languages don't really make
		// sense unless they both have initials states
		if ((CheckingToolbox.initialStateCount(model2) != 1)
				|| (CheckingToolbox.initialStateCount(model1) != 1))
		{
			String error = "There should be exactly one initial State in each automata";
			warnings.add(error);
			return new Object[] { ides.api.plugin.model.ModelManager
					.instance().createModel(FSAModel.class) };
		}

		// Identify the initial state of model2
		FSAState model2Initial = DuplicationToolbox.getInitial(model2);

		/*
		 * Identify the marked states of model1 and stop (and warn the user) if
		 * there is not at least 1
		 */
		Collection<FSAState> model1MarkedStates = new HashSet<FSAState>();
		for (ListIterator<FSAState> a = model1.getStateIterator(); a.hasNext();)
		{
			FSAState currState = a.next();
			if (currState.isMarked())
				model1MarkedStates.add(currState);
		}
		if (model1MarkedStates.isEmpty())
		{
			String error = "There needs to be at least one marked state in Finite-State Automaton 1";
			warnings.add(error);
			return new Object[] { ides.api.plugin.model.ModelManager
					.instance().createModel(FSAModel.class) };
		}

		/*
		 * Add the events in model1 to the hashtable "events". Later on, if an
		 * event with the same name exists in model2, it will not be added, but
		 * the event (with the same name) in model1 will be retrieved.
		 */
		for (ListIterator<FSAEvent> b = model1.getEventIterator(); b.hasNext();)
		{
			FSAEvent currEvent = b.next();
			events.put(currEvent.getSymbol(), currEvent);
		}

		if (model2.getTransitionCount() > 0)
		{
			for (ListIterator<FSATransition> d = model2.getTransitionIterator(); d
					.hasNext();)
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
					FSAEvent copyEvent = DuplicationToolbox.copyEventInto(model1,
							origEvent,
							events,
							false);
					copyTransition = model1.assembleTransition(copySource
							.getId(), copyTarget.getId(), copyEvent.getId());
				}

				model1.add(copyTransition);
			}
		}
		else
		{ // if there are no transitions in model2
			DuplicationToolbox.copyAllEvents(model1, model2, events, false);
			DuplicationToolbox.copyAllStates(model1, model2, states, false);
		}

		model2Initial = states.get(model2Initial);
		model2Initial.setInitial(false);

		for (FSAState s : model1MarkedStates)
		{
			s.setMarked(false);
			FSATransition t = model1.assembleEpsilonTransition(s.getId(),
					model2Initial.getId());
			model1.add(t);
		}

		/* project out the null string transitions (no associated event) */
		model1 = (FSAModel)ides.api.plugin.operation.OperationManager
				.instance().getOperation("removeepsilon")
				.perform(new Object[] { model1 })[0];

		return new Object[] { model1 };
	}

}
