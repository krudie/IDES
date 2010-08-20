package operations.fsa.ver2_1;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.plugin.model.DESEventSet;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.operation.FSAToolbox;
import ides.api.plugin.operation.Operation;
import ides.api.plugin.operation.OperationManager;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import util.AnnotationKeys;

/**
 * Checks to see whether a given language is normal with respect to another. See
 * Introduction to Discrete Event Systems by Christos G. Cassandras and Stephane
 * Lafortune 2nd Edition p. 198
 * 
 * @author Valerie Sugarman
 */
public class SupNorm implements Operation
{

	private LinkedList<String> warnings = new LinkedList<String>();

	public String getDescription()
	{
		return "Computes a trim automaton that accepts the supremal normal sublanguage "
				+ "of the given language with respect to the given plant. The plant will be prefix-closed with respect to "
				+ "the language generated by the automaton for use in"
				+ " the operation if it is not already.";
	}

	public String[] getDescriptionOfInputs()
	{
		return new String[] { "Plant", "Candidate language" };
	}

	public String[] getDescriptionOfOutputs()
	{
		return new String[] { "Supremal normal language" };
	}

	public String getName()
	{
		return "supnorm";
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
	 * computes the supremal normal sublanguage of the given language with
	 * respect to the language generated by the plant. See Introduction to
	 * Discrete Event Systems by Christos G. Cassandras and Stephane Lafortune
	 * 2nd Edition p. 198
	 */
	public Object[] perform(Object[] arg0)

	{

		warnings.clear();
		FSAModel plant;
		FSAModel language;

		if (arg0.length >= 2)
		{
			if (arg0[0] instanceof FSAModel && arg0[1] instanceof FSAModel)
			{
				plant = ((FSAModel)arg0[0]);
				language = ((FSAModel)arg0[1]);
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

		// don't want epsilon transitions, and want determinized for subset
		// later.
		if (!FSAToolbox.isDeterministic(plant))
		{
			plant = (FSAModel)OperationManager
					.instance().getOperation("determinize")
					.perform(new Object[] { plant })[0];
			warnings.addAll(OperationManager
					.instance().getOperation("determinize").getWarnings());
		}
		if (!FSAToolbox.isDeterministic(language))
		{
			language = (FSAModel)OperationManager
					.instance().getOperation("determinize")
					.perform(new Object[] { language })[0];
			warnings.addAll(OperationManager
					.instance().getOperation("determinize").getWarnings());
		}

		if (FSAToolbox.hasObservabilityConflict(new FSAModel[] { plant,
				language }))
		{
			warnings.add(FSAToolbox.ERROR_OBSERVE);
			return new Object[] { ModelManager
					.instance().createModel(FSAModel.class) };
		}

		// Double check that sublanguage is in fact a sublanguage of the
		// plant. (don't call the operation since that will run determinize
		// again)
		boolean isSublanguage = Subset.subset(language, plant);

		if (!isSublanguage)
		{
			warnings.add(Hub.string("errorNotSublanguage"));
			return new Object[] { ModelManager
					.instance().createModel(FSAModel.class) };
		}

		// Done checking, start the actual operation here.

		// create the set of unobservable events to be self-looped in the
		// inverse projection
		DESEventSet unobservableEvents = ModelManager
				.instance().createEmptyEventSet();
		for (Iterator<SupervisoryEvent> i = plant.getEventIterator(); i
				.hasNext();)
		{
			SupervisoryEvent e = i.next();
			if (!e.isObservable())
			{
				unobservableEvents.add(e);
			}
		}

		// create the E* that will be concatenated in each loop
		FSAModel toConcat = ModelManager.instance().createModel(FSAModel.class);
		FSAState s = toConcat.assembleState();
		s.setInitial(true);
		s.setMarked(true);
		toConcat.add(s);
		for (Iterator<SupervisoryEvent> i = plant.getEventIterator(); i
				.hasNext();)
		{
			SupervisoryEvent origEvent = i.next();
			SupervisoryEvent copyEvent = toConcat.assembleEvent(origEvent
					.getSymbol());
			copyEvent.setObservable(origEvent.isObservable());
			copyEvent.setControllable(origEvent.isControllable());
			toConcat.add(copyEvent);
			FSATransition t = toConcat.assembleTransition(s.getId(),
					s.getId(),
					copyEvent.getId());
			toConcat.add(t);
		}

		// m has to be prefix closed
		FSAModel m = (FSAModel)OperationManager
				.instance().getOperation("prefixclose")
				.perform(new Object[] { plant })[0];
		warnings.addAll(OperationManager
				.instance().getOperation("prefixclose").getWarnings());

		FSAModel k = language;
		FSAModel currK = language;
		FSAModel prevK;
		boolean checkConvergence;

		FSAModel currKBar, mMinusK, projMMinusK, invProjMMinusK, concatenation, tempSupNorm;
		// follow the iterative procedure described in the textbook
		do
		{
			prevK = currK;
			currKBar = (FSAModel)OperationManager
					.instance().getOperation("prefixclose")
					.perform(new Object[] { prevK })[0];
			warnings.addAll(OperationManager
					.instance().getOperation("prefixclose").getWarnings());

			mMinusK = (FSAModel)OperationManager
					.instance().getOperation("setminus").perform(new Object[] {
							m, currKBar })[0];
			warnings.addAll(OperationManager
					.instance().getOperation("setminus").getWarnings());

			projMMinusK = (FSAModel)OperationManager
					.instance().getOperation("observer")
					.perform(new Object[] { mMinusK })[0];
			warnings.addAll(OperationManager
					.instance().getOperation("observer").getWarnings());

			invProjMMinusK = (FSAModel)OperationManager
					.instance().getOperation("selfloop").perform(new Object[] {
							projMMinusK, unobservableEvents })[0];
			warnings.addAll(OperationManager
					.instance().getOperation("selfloop").getWarnings());

			concatenation = (FSAModel)OperationManager
					.instance().getOperation("concatenate")
					.perform(new Object[] { invProjMMinusK, toConcat })[0];
			warnings.addAll(OperationManager
					.instance().getOperation("concatenate").getWarnings());

			tempSupNorm = (FSAModel)OperationManager
					.instance().getOperation("setminus").perform(new Object[] {
							currKBar, concatenation })[0];
			warnings.addAll(OperationManager
					.instance().getOperation("setminus").getWarnings());

			currK = (FSAModel)OperationManager
					.instance().getOperation("product").perform(new Object[] {
							tempSupNorm, k })[0];
			warnings.addAll(OperationManager
					.instance().getOperation("product").getWarnings());

			checkConvergence = (Boolean)OperationManager
					.instance().getOperation("langequals").perform(new Object[] {
							currK, prevK })[0];
			warnings.addAll(OperationManager
					.instance().getOperation("langequals").getWarnings());

		}
		while (!checkConvergence);

		FSAModel supNorm = (FSAModel)OperationManager
				.instance().getOperation("trim")
				.perform(new Object[] { currK })[0];
		warnings.addAll(OperationManager
				.instance().getOperation("trim").getWarnings());

		// remove the annotations since state labels will be meaningless after
		// all of the above operations
		supNorm.removeAnnotation(AnnotationKeys.COMPOSED_OF);

		return new Object[] { supNorm };
	}

}
