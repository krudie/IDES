package operations.fsa.ver2_1;

import java.util.LinkedList;
import java.util.List;

import ides.api.model.fsa.FSAModel;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.operation.FSAToolbox;
import ides.api.plugin.operation.Operation;

public class InfimalObservable implements Operation
{

	protected LinkedList<String> warnings = new LinkedList<String>();

	public String getDescription()
	{
		return "Computes the infimal observable superlanguage of the given language with respect to the plant.";
	}

	public String[] getDescriptionOfInputs()
	{
		return new String[] { "Plant", "Language" };
	}

	public String[] getDescriptionOfOutputs()
	{
		return new String[] { "Infimal obsersable superlangauge" };
	}

	public String getName()
	{
		return "infobs";
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

	public Object[] perform(Object[] inputs)
	{
		warnings.clear();
		FSAModel plant;
		FSAModel language;

		if (inputs.length >= 2)
		{
			if (inputs[0] instanceof FSAModel && inputs[1] instanceof FSAModel)
			{
				plant = ((FSAModel)inputs[0]);
				language = ((FSAModel)inputs[1]);
			}
			else
			{
				warnings.add(FSAToolbox.ILLEGAL_ARGUMENT);
				return new Object[] { ModelManager
						.instance().createModel(FSAModel.class) };
			}
		}
		else
		{
			warnings.add(FSAToolbox.ILLEGAL_NUMBER_OF_ARGUMENTS);
			return new Object[] { ModelManager
					.instance().createModel(FSAModel.class) };
		}

		if (FSAToolbox.hasObservabilityConflict(new FSAModel[] { plant,
				language }))
		{
			warnings.add(FSAToolbox.ERROR_OBSERVE);
			return new Object[] { ModelManager
					.instance().createModel(FSAModel.class) };
		}
		
		
		
		
		
		
		//XXX first figure out the inverse proj of proj part
		
		
		
		

		return null;
	}
	
	

}
