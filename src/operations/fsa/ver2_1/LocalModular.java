package operations.fsa.ver2_1;

import java.util.Vector;

import model.fsa.FSAModel;
import pluggable.operation.Operation;
import pluggable.operation.OperationManager;

public class LocalModular extends OperationParent {

	public LocalModular() {
		NAME = "Local Modular";
		DESCRIPTION = "Determines if the languages" +
				" produced by the two automata are locally modular."; 
		//WARNING - Ensure that input type and description always match!	
		inputType = new Class[]{FSAModel.class,FSAModel.class};
		inputDesc = new String[]{"Finite-state automaton","Finite-state automaton"};

		//WARNING - Ensure that output type and description always match!
		outputType = new Class[]{Boolean.class, String.class};
		outputDesc = new String[]{"result", "resultMessage"};
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
		
		String resultMessage = "";
		if (equal) resultMessage = "The two automata are locally modular.";
		else resultMessage = "The two automata are not locally modular.";
		
		return new Object[]{new Boolean(equal), resultMessage};
	}

}
