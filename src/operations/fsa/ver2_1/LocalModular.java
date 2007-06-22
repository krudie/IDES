package operations.fsa.ver2_1;

import java.util.Vector;

import model.fsa.FSAModel;
import pluggable.operation.Operation;
import pluggable.operation.OperationManager;

public class LocalModular implements Operation {

	public final static String NAME="local modular";
	public final static String DESCRIPTION="temp";

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getName()
	 */
	public String getName() {
		return NAME;
	}
	
	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getDescription()
	 */
	public String getDescription() {
		return DESCRIPTION;
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getNumberOfInputs()
	 */
	public int getNumberOfInputs() {
		return -1;
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getTypeOfInputs()
	 */
	public Class[] getTypeOfInputs() {
		return new Class[]{FSAModel.class};
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getDescriptionOfInputs()
	 */
	public String[] getDescriptionOfInputs() {
		return new String[]{"Finite-state automaton"};
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getNumberOfOutputs()
	 */
	public int getNumberOfOutputs() {
		return 1;
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getTypeOfOutputs()
	 */
	public Class[] getTypeOfOutputs() {
		return new Class[]{Boolean.class};
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getDescriptionOfOutputs()
	 */
	public String[] getDescriptionOfOutputs() {
		return new String[]{"are the two languages locally modular"};
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#perform(java.lang.Object[])
	 */
	public Object[] perform(Object[] inputs) {
		Vector<FSAModel> models=new Vector<FSAModel>();
		for(int i=0;i<inputs.length;++i)
		{
			models.add((FSAModel)inputs[i]);
		}
		Operation prefix=OperationManager.getOperation("prefix closure");
		Operation sync=OperationManager.getOperation("synchronous product");
		Vector<FSAModel> pModels=new Vector<FSAModel>();
		for(FSAModel m:models)
		{
			pModels.add((FSAModel)prefix.perform(new Object[]{m})[0]);
		}
		FSAModel l=models.firstElement();
		for(int i=1;i<models.size();++i)
		{
			l=(FSAModel)sync.perform(new Object[]{l,models.elementAt(i)})[0];
		}
		l=(FSAModel)prefix.perform(new Object[]{l})[0];
		FSAModel r=pModels.firstElement();
		for(int i=1;i<pModels.size();++i)
		{
			r=(FSAModel)sync.perform(new Object[]{r,pModels.elementAt(i)})[0];
		}
		boolean equal=((Boolean)OperationManager.getOperation("containment").perform(new Object[]{
				r,l})[0]).booleanValue();
		return new Object[]{new Boolean(equal)};
	}

}
