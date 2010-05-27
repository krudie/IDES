package ides.api.plugin.operation;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAEvent;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A class of methods useful for checking FSAModels for certain criteria.
 * 
 * @author Valerie Sugarman
 */
public class CheckingToolbox
{
	public static final String NOT_1_INITIAL_STATE = Hub
			.string("errorNoOrManyInitStates");

	public static final String NO_MARKED_STATES = Hub
			.string("errorNoMarkedStates");

	public static final String ILLEGAL_ARGUMENT = Hub
			.string("errorIllegalArgument");

	public static final String ILLEGAL_NUMBER_OF_ARGUMENTS = Hub
			.string("errorIllegalNumberOfArguments");

	public static final String NON_DETERM = Hub.string("errorNonDeterministic");

	/**
	 * Obtains a set of the ids of all initial states in the model.
	 * 
	 * @param model
	 *            the model in which to find the initial states
	 * @return a set of the ids of all initial states in the model
	 */
	public static Set<Long> getInitialStates(FSAModel model)
	{
		Set<Long> initialStateIds = new HashSet<Long>();
		for (Iterator<FSAState> i = model.getStateIterator(); i.hasNext();)
		{
			FSAState s = i.next();
			if (s.isInitial())
			{
				initialStateIds.add(s.getId());
			}
		}
		return initialStateIds;
	}

	/**
	 * Identifies the number of initial states in the model.
	 * 
	 * @param model
	 *            the model in which to find the number of initial states
	 * @returns the number of initial states in the model
	 */
	public static int initialStateCount(FSAModel model)
	{

		return getInitialStates(model).size();
	}

	/**
	 * Obtains a set of the ids of all marked states in the model.
	 * 
	 * @param model
	 *            the model in which to find the marked states
	 * @return a set of the ids of all marked states in the model
	 */
	public static Set<Long> getMarkedStates(FSAModel model)
	{
		Set<Long> markedStateIds = new HashSet<Long>();
		for (Iterator<FSAState> i = model.getStateIterator(); i.hasNext();)
		{
			FSAState s = i.next();
			if (s.isMarked())
			{
				markedStateIds.add(s.getId());
			}
		}
		return markedStateIds;
	}

	/**
	 * Identifies the number of marked states in the model.
	 * 
	 * @param model
	 *            the model in which to find the number of marked states
	 * @returns the number of marked states in the model
	 */
	public static int markedStateCount(FSAModel model)
	{
		return getMarkedStates(model).size();
	}

	/**
	 * Obtains a set of the ids of all epsilon transitions in the model.
	 * 
	 * @param model
	 *            the model in which to find the epsilon transitions
	 * @return a set of the ids of all epsilon transitions in the model
	 */
	public static Set<Long> getEpsilonTransitions(FSAModel model)
	{
		Set<Long> epsilonTransitionIds = new HashSet<Long>();
		for (Iterator<FSATransition> i = model.getTransitionIterator(); i
				.hasNext();)
		{
			FSATransition t = i.next();
			if (t.isEpsilonTransition())
			{
				epsilonTransitionIds.add(t.getId());
			}
		}
		return epsilonTransitionIds;
	}

	/**
	 * Determines if there are any epsilon transitions in the model.
	 * 
	 * @param model
	 *            the model in which to determine if there are epsilon
	 *            transitions
	 * @return <code>true</code> if there is at least one epsilon transition in
	 *         the model, <code>false</code> otherwise.
	 */
	public static boolean containsEpsilonTransitions(FSAModel model)
	{
		for (Iterator<FSATransition> i = model.getTransitionIterator(); i
				.hasNext();)
		{
			FSATransition t = i.next();
			if (t.isEpsilonTransition())
				return true;
		}
		return false;
	}

	/**
	 * Determines whether the given model is empty.
	 * 
	 * @param model
	 *            the model under consideration
	 * @return <code>true</code> if the model contains no states,
	 *         <code>false</code> otherwise.
	 */
	public static boolean isEmptyModel(FSAModel model)
	{
		if (model.getStateCount() == 0)
			return true;
		else
			return false;
	}

	/**
	 * Determines whether the language represented by the automaton is empty.
	 * 
	 * @param model
	 *            the model under consideration
	 * @return <code>true</code> if the language represented by the automaton is
	 *         empty, <code>false</code> otherwise.
	 */
	public static boolean isEmptyLanguage(FSAModel model)
	{
		FSAModel compareTo = ides.api.plugin.model.ModelManager
				.instance().createModel(FSAModel.class);
		FSAState s = compareTo.assembleState();
		compareTo.add(s);

		boolean isEmpty = (Boolean)OperationManager
				.instance().getOperation("equals").perform(new Object[] {
						model, compareTo })[0];

		return isEmpty;
	}

	/**
	 * Determines whether the language represented by the automaton is the
	 * epsilon language.
	 * 
	 * @param model
	 *            the model under consideration
	 * @return <code>true</code> if the language represented by the automaton is
	 *         the epsilon language, <code>false</code> otherwise.
	 */
	public static boolean isEpsilonLanguage(FSAModel model)
	{

		FSAModel compareTo = ides.api.plugin.model.ModelManager
				.instance().createModel(FSAModel.class);
		FSAState s = compareTo.assembleState();
		s.setInitial(true);
		s.setMarked(true);
		compareTo.add(s);

		boolean isEpsilonLanguage = (Boolean)OperationManager
				.instance().getOperation("equals").perform(new Object[] {
						model, compareTo })[0];

		return isEpsilonLanguage;
	}

	/**
	 * Determines whether the model is deterministic, i.e. contains no epsilon
	 * transitions, has no more than one initial state (but could have none for
	 * our purposes), and from each state there is not more than one transition
	 * with a given event.
	 * 
	 * @param model
	 *            the model under consideration
	 * @return <code>false</code> if model contains epsilon transitions, has
	 *         more than one initial state or has a state with more than one
	 *         transition firing on the same event, <code>true</code> otherwise.
	 */
	public static boolean isDeterministic(FSAModel model)
	{
		int initialStateCount = 0;
		if (containsEpsilonTransitions(model))
			return false;

		for (Iterator<FSAState> si = model.getStateIterator(); si.hasNext();)
		{
			FSAState s = si.next();
			if (s.isInitial())
				initialStateCount++;
			if (initialStateCount > 1)
				return false;

			HashSet<FSAEvent> events = new HashSet<FSAEvent>();
			for (Iterator<FSATransition> ti = s
					.getOutgoingTransitionsListIterator(); ti.hasNext();)
			{
				FSATransition t = ti.next();
				FSAEvent e = t.getEvent();
				if (events.contains(e))
				{
					return false;
				}
				events.add(e);
			}
		}
		return true;
	}
}
