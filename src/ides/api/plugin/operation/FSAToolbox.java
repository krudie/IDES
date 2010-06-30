package ides.api.plugin.operation;

import ides.api.core.Annotable;
import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.plugin.model.DESEvent;
import ides.api.plugin.model.DESEventSet;
import ides.api.plugin.model.ModelManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A class of methods useful for checking FSAModels for certain criteria.
 * 
 * @author Valerie Sugarman
 */
public class FSAToolbox
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

	public static final String ERROR_CONTROL = Hub.string("errorControl");

	public static final String ERROR_OBSERVE = Hub.string("errorObserve");

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

			HashSet<SupervisoryEvent> events = new HashSet<SupervisoryEvent>();
			for (Iterator<FSATransition> ti = s
					.getOutgoingTransitionsListIterator(); ti.hasNext();)
			{
				FSATransition t = ti.next();
				SupervisoryEvent e = t.getEvent();
				if (events.contains(e))
				{
					return false;
				}
				events.add(e);
			}
		}
		return true;
	}

	/**
	 * Generates the FSA representation of all finite sequences over the event
	 * set plus the null string.
	 * 
	 * @param set
	 *            The given event set.
	 * @return FSA representation of all finite sequences over the event set
	 *         plus the null string.
	 */
	// TODO change my name? and fix my wording.
	public static FSAModel sigmaStar(DESEventSet set)
	{
		FSAModel model = ModelManager.instance().createModel(FSAModel.class);
		for (Iterator<DESEvent> i = set.iterator(); i.hasNext();)
		{
			SupervisoryEvent e = model.assembleCopyOf(i.next());
			model.add(e);
		}
		model = (FSAModel)OperationManager
				.instance().getFilterOperation("complement")
				.filter(new Object[] { model })[0];
		return model;
	}

	/**
	 * Generates the FSA representation of all finite sequences over the event
	 * set.
	 * 
	 * @param set
	 *            The given event set.
	 * @return FSA representation of all finite sequences over the event set.
	 */
	// TODO change my name? and fix my wording
	public static FSAModel sigmaPlus(DESEventSet set)
	{
		FSAModel model1 = sigmaStar(set);
		FSAModel epsilon = ModelManager.instance().createModel(FSAModel.class);
		FSAState state = epsilon.assembleState();
		state.setInitial(true);
		state.setMarked(true);
		epsilon.add(state);

		FSAModel ret = (FSAModel)OperationManager
				.instance().getOperation("setminus").perform(new Object[] {
						model1, epsilon })[0];
		ret.removeAnnotation(Annotable.COMPOSED_OF);
		return ret;
	}

	/**
	 * A check to see whether there is a conflict in the controllable status of
	 * events contained in both models.
	 * 
	 * @param model1
	 * @param model2
	 * @return <code>true</code> if an event with the same name exists in both
	 *         models but is controllable in one and uncontrollable in the
	 *         other, <code>false</code> otherwise.
	 */

	public static boolean hasControllabilityConflict(FSAModel[] models)
	{

		if (models.length <= 1)
		{
			return false;
		}

		HashMap<String, SupervisoryEvent> events = new HashMap<String, SupervisoryEvent>();

		for (FSAModel model : models)
		{
			for (Iterator<SupervisoryEvent> i = model.getEventIterator(); i
					.hasNext();)
			{
				SupervisoryEvent e = i.next();
				if (events.containsKey(e.getSymbol()))
				{
					SupervisoryEvent compareEvent = events.get(e.getSymbol());
					if (e.isControllable() != compareEvent.isControllable())
					{
						return true;
					}
				}
				else
				{
					events.put(e.getSymbol(), e);
				}
			}
		}

		return false;
	}

	/**
	 * A check to see whether there is a conflict in the observable status of
	 * events contained in both models.
	 * 
	 * @param model1
	 * @param model2
	 * @return <code>true</code> if an event with the same name exists in both
	 *         models but is observable in one and unobservable in the other,
	 *         <code>false</code> otherwise.
	 */

	public static boolean hasObservabilityConflict(FSAModel[] models)
	{

		if (models.length <= 1)
		{
			return false;
		}

		HashMap<String, SupervisoryEvent> events = new HashMap<String, SupervisoryEvent>();

		for (FSAModel model : models)
		{
			for (Iterator<SupervisoryEvent> i = model.getEventIterator(); i
					.hasNext();)
			{
				SupervisoryEvent e = i.next();
				if (events.containsKey(e.getSymbol()))
				{
					SupervisoryEvent compareEvent = events.get(e.getSymbol());
					if (e.isObservable() != compareEvent.isObservable())
					{
						return true;
					}
				}
				else
				{
					events.put(e.getSymbol(), e);
				}
			}
		}

		return false;
	}
}
