package operations.fsa.ver2_1;

import ides.api.model.fsa.FSAModel;
import ides.api.plugin.model.DESEventSet;
import ides.api.plugin.operation.CheckingToolbox;
import ides.api.plugin.operation.Operation;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Valerie Sugarman
 */
public class ProjectEpsilon implements Operation
{

	private LinkedList<String> warnings = new LinkedList<String>();

	public String getDescription()
	{

		return "Computes an automaton which recognizes the same language as the given automaton and contains"
				+ " no epsilon transitions.";
	}

	public String[] getDescriptionOfInputs()
	{

		return new String[] { "Finite-State automaton" };

	}

	public String[] getDescriptionOfOutputs()
	{

		return new String[] { "FSA with null events projected out" };
	}

	public String getName()
	{

		return "removeepsilon";
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
		FSAModel projection = ides.api.plugin.model.ModelManager
				.instance().createModel(FSAModel.class);

		if (arg0.length == 1)
		{
			if (arg0[0] instanceof FSAModel)
			{
				model = ((FSAModel)arg0[0]);
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
			String error = "Illegal number of arguments, one expected.";
			warnings.add(error);
			return new Object[] { ides.api.plugin.model.ModelManager
					.instance().createModel(FSAModel.class) };
		}

		if (CheckingToolbox.initialStateCount(model) != 1)
		{
			String error = "There should be exactly one initial State in the model";
			warnings.add(error);
			return new Object[] { ides.api.plugin.model.ModelManager
					.instance().createModel(FSAModel.class) };
		}

		DESEventSet des = ides.api.plugin.model.ModelManager
				.instance().createEmptyEventSet();

		Object doubleCheck = Project.projectCustom(model, des, true);

		if (doubleCheck instanceof FSAModel)
		{
			projection = (FSAModel)doubleCheck;
		}

		return new Object[] { projection };
	}
}
