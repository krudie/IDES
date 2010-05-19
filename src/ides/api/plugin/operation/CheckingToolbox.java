package ides.api.plugin.operation;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;

import java.util.ListIterator;
/**
 * A class of methods useful for checking FSAModels for certain criteria
 * 
 *
 */
public class CheckingToolbox
{
	public static final String NOT_1_INITIAL_STATE = Hub.string("errorNoOrManyInitStates");
	
	/*
	 * Identifies the number of initial states in the model
	 * 
	 * @returns the number of initial states in the model
	 */
	public static int initialStateCount(FSAModel model)
	{
		int initialStateCount = 0;
		for (ListIterator<FSAState> a = model.getStateIterator(); a.hasNext();)
		{
			FSAState currState = a.next();
			if (currState.isInitial())
			{
				initialStateCount++;
			}
		}
		return initialStateCount;
	}
}
