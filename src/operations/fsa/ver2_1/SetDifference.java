package operations.fsa.ver2_1;

import ides.api.model.fsa.FSAEvent;
import ides.api.model.fsa.FSAModel;
import ides.api.plugin.model.DESEventSet;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.operation.CheckingToolbox;
import ides.api.plugin.operation.Operation;
import ides.api.plugin.operation.OperationManager;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class subtracts one language from the other.
 * 
 * @author Utsav Mital
 */
public class SetDifference implements Operation
{

	private LinkedList<String> warnings = new LinkedList<String>();

	public String getDescription()
	{
		return "Subtracts one language from another.";
	}

	public String[] getDescriptionOfInputs()
	{
		return new String[] { "Language to be subtracted from",
				"Language to subtract" };
	}

	public String[] getDescriptionOfOutputs()
	{
		return new String[] { "Langauge after subtraction" };

	}

	public String getName()
	{
		return "setminus";

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

	/**
	 * Performs the operation of subtracting one language from the other.
	 * 
	 * @param arg0
	 *            two inputs of type FSAModel, second input will be subtracted
	 *            from the first
	 * @returns one model in which the difference of the languages is stored
	 */
	public Object[] perform(Object[] arg0)
	{
		FSAModel differenceModel;
		warnings.clear();

		if ((arg0.length < 2) || (!FSAModel.class.isInstance(arg0[0]))
				|| (!FSAModel.class.isInstance(arg0[1])))
		{
			warnings.add(CheckingToolbox.ILLEGAL_ARGUMENT);

			return new Object[] { ModelManager
					.instance().createModel(FSAModel.class) };
		}

		FSAModel model1 = (FSAModel)arg0[0];
		FSAModel model2 = ((FSAModel)arg0[1]).clone();

		// make sure all events in model 1 are in model 2 so the complementing
		// works properly
		DESEventSet eventsInModel2 = model2.getEventSet();
		for(Iterator<FSAEvent> i = model1.getEventIterator(); i.hasNext();){
			FSAEvent origEvent = i.next();
			if(!eventsInModel2.contains(origEvent)){
				FSAEvent copyEvent = model2.assembleEvent(origEvent.getSymbol());
				copyEvent.setObservable(origEvent.isObservable());
				copyEvent.setControllable(origEvent.isControllable());
				model2.add(copyEvent);
			}
		}
		

		// Complementing the model2 to get the elements not part of model2
		model2 = (FSAModel)OperationManager
				.instance().getOperation("complement")
				.perform(new Object[] { model2 })[0];
		warnings.addAll(OperationManager
				.instance().getOperation("complement").getWarnings());

		// Finding the product of the model1 with complement of model2, this is
		// the difference
		differenceModel = (FSAModel)OperationManager
				.instance().getOperation("product").perform(new Object[] {
						model1, model2 })[0];
		warnings.addAll(OperationManager
				.instance().getOperation("product").getWarnings());

		return new Object[] { differenceModel };
	}

}
