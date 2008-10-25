package operations.fsa.ver2_1;

import ides.api.model.fsa.FSAModel;
import ides.api.plugin.operation.Operation;
import ides.api.plugin.operation.OperationManager;

import java.util.Vector;

public class LocalModular extends AbstractOperation
{

	public LocalModular()
	{
		NAME = "localmodular";
		DESCRIPTION = "Determines if the languages"
				+ " produced by the two automata are locally modular.";
		// WARNING - Ensure that input type and description always match!
		inputType = new Class[] { FSAModel.class };
		inputDesc = new String[] { "Finite-state automata" };

		// WARNING - Ensure that output type and description always match!
		outputType = new Class[] { Boolean.class };
		outputDesc = new String[] { "resultMessage" };
	}

	public int getNumberOfInputs()
	{
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see pluggable.operation.Operation#perform(java.lang.Object[])
	 */
	@Override
	public Object[] perform(Object[] inputs)
	{
		Vector<FSAModel> models = new Vector<FSAModel>();
		for (int i = 0; i < inputs.length; ++i)
		{
			models.add((FSAModel)inputs[i]);
		}
		Operation prefix = OperationManager
				.instance().getOperation("prefixclose");
		Operation sync = OperationManager.instance().getOperation("sync");
		Vector<FSAModel> pModels = new Vector<FSAModel>();
		for (FSAModel m : models)
		{
			pModels.add((FSAModel)prefix.perform(new Object[] { m })[0]);
		}
		FSAModel l = models.firstElement();
		for (int i = 1; i < models.size(); ++i)
		{
			l = (FSAModel)sync.perform(new Object[] { l, models.elementAt(i) })[0];
		}
		l = (FSAModel)prefix.perform(new Object[] { l })[0];
		FSAModel r = pModels.firstElement();
		for (int i = 1; i < pModels.size(); ++i)
		{
			r = (FSAModel)sync
					.perform(new Object[] { r, pModels.elementAt(i) })[0];
		}
		boolean equal = ((Boolean)OperationManager
				.instance().getOperation("subset")
				.perform(new Object[] { r, l })[0]).booleanValue();

		String resultMessage = "";
		if (equal)
		{
			resultMessage = "The two automata are locally modular.";
		}
		else
		{
			resultMessage = "The two automata are not locally modular.";
		}
		outputDesc = new String[] { resultMessage };
		return new Object[] { new Boolean(equal) };
	}

}
