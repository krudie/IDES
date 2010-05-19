package operations.fsa.ver2_1;

import java.util.LinkedList;
import java.util.List;

import ides.api.model.fsa.FSAModel;
import ides.api.plugin.model.DESEventSet;
import ides.api.plugin.operation.CheckingToolbox;
import ides.api.plugin.operation.Operation;
/**
 * 
 * @author Valerie Sugarman
 *
 */
public class ProjectEventSetOperation implements Operation
{

	private LinkedList<String> warnings = new LinkedList<String>();

	
	public String getDescription()
	{

		return "Computes a projection of the given automaton such that all of the specified"
				+ " events have been removed. Epsilon transitions are not removed.";
	}


	public String[] getDescriptionOfInputs()
	{

		return new String[] { "Finite-State automaton", "Events to project out" };

	}


	public String[] getDescriptionOfOutputs()
	{

		return new String[] { "Automaton with events projected out" };
	}


	public String getName()
	{

		return "project";
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

		return new Class<?>[] { FSAModel.class, DESEventSet.class };

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
		DESEventSet DESEventsToRemove;
		FSAModel projection = ides.api.plugin.model.ModelManager
				.instance().createModel(FSAModel.class);

		if (arg0.length == 2)
		{
			if ((arg0[0] instanceof FSAModel)
					&& (arg0[1] instanceof DESEventSet))
			{
				model = (FSAModel)arg0[0];
				DESEventsToRemove = (DESEventSet)arg0[1];
			}
			else
			{
				String error = "Illegal argument, FSAModel and DESEventSet expected";
				warnings.add(error);
				return new Object[] { ides.api.plugin.model.ModelManager
						.instance().createModel(FSAModel.class) };
			}
		}
		else
		{
			String error = "Illegal number of arguments, two expected.";
			warnings.add(error);
			return new Object[] { ides.api.plugin.model.ModelManager
					.instance().createModel(FSAModel.class) };
		}

		if (CheckingToolbox.initialStateCount(model) != 1)
		{
			String error = "There should be exactly one initial State in the";
			warnings.add(error);
			return new Object[] { ides.api.plugin.model.ModelManager
					.instance().createModel(FSAModel.class) };
		}

		Object doubleCheck = Project.projectCustom(model,
				DESEventsToRemove,
				false);
		if (doubleCheck instanceof FSAModel)
			projection = (FSAModel)doubleCheck;

		return new Object[] { projection };
	}

}