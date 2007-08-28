/**
 * 
 */
package operations.fsa.ver2_1;

import model.ModelManager;
import model.fsa.FSAModel;
import model.fsa.ver2_1.Automaton;

/**
 * @author Lenko Grigorov
 * @author Chris Dragert
 *
 */
public class Meet extends AbstractOperation {
	
	public Meet() {
		NAME = "Meet";
		DESCRIPTION = "Also known as parallel" +
			" composition, meet produces an automaton that accepts the" +
			" intersection of the languages produced by the given automata.";
		
		//WARNING - Ensure that input type and description always match!	
		inputType = new Class[]{FSAModel.class,FSAModel.class};
		inputDesc = new String[]{"Finite-state automaton","Finite-state automaton"};

		//WARNING - Ensure that output type and description always match!
		outputType = new Class[]{FSAModel.class};
		outputDesc = new String[]{"composedAutomaton"};
	}
	
	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#perform(java.lang.Object[])
	 */
	public Object[] perform(Object[] inputs) {
		FSAModel meetAutomata=ModelManager.createModel(FSAModel.class,"none");
		Composition.product((FSAModel)inputs[0],(FSAModel)inputs[1],meetAutomata);
		return new Object[]{meetAutomata};
	}

}
