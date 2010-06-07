package operations.fsa.ver2_1;

import ides.api.model.fsa.FSAEvent;
import ides.api.model.fsa.FSAModel;
import ides.api.plugin.model.DESEvent;
import ides.api.plugin.model.DESEventSet;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.operation.CheckingToolbox;
import ides.api.plugin.operation.Operation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Valerie Sugarman
 */
public class ProjectUnobservable implements Operation
{

	private LinkedList<String> warnings = new LinkedList<String>();

	public String getDescription()
	{
		return "Computes a projection of the given automaton such that all of the unobservable events"
				+ " have been removed. Epsilon transitions are not removed.";
	}

	public String[] getDescriptionOfInputs()
	{
		return new String[] { "Finite-State automaton" };
	}

	public String[] getDescriptionOfOutputs()
	{
		return new String[] { "Observer automaton" };
	}

	public String getName()
	{
		return "observer";
	}

	public int getNumberOfInputs()
	{
		return 1;
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

		FSAModel model;
		FSAModel projection = ModelManager
				.instance().createModel(FSAModel.class);

		if (arg0.length == 1)
		{
			if (arg0[0] instanceof FSAModel)
			{
				model = (FSAModel)arg0[0];
			}
			else
			{
				warnings.add(CheckingToolbox.ILLEGAL_ARGUMENT);
				return new Object[] { ModelManager
						.instance().createModel(FSAModel.class) };
			}
		}
		else
		{
			warnings.add(CheckingToolbox.ILLEGAL_NUMBER_OF_ARGUMENTS);
			return new Object[] { ModelManager
					.instance().createModel(FSAModel.class) };
		}

		// create a DESEventSet with only the unobservable events to remove
		DESEventSet eventsToRemove = ModelManager
				.instance().createEmptyEventSet();
		for (Iterator<FSAEvent> i = model.getEventIterator(); i.hasNext();)
		{
			FSAEvent e = i.next();
			if (!e.isObservable())
			{
				eventsToRemove.add((DESEvent)e);
			}
		}

		projection = Project.projectCustom(model, eventsToRemove, false);

		return new Object[] { projection };
	}
}
