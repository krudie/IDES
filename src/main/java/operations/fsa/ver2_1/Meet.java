/**
 * 
 */
package operations.fsa.ver2_1;

import java.util.LinkedList;

import ides.api.model.fsa.FSAModel;
import ides.api.plugin.operation.FSAToolbox;
import ides.api.plugin.operation.OperationManager;
import util.AnnotationKeys;

/**
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class Meet extends AbstractOperation {

    public Meet() {
        NAME = "product";
        DESCRIPTION = "Computes an automaton that accepts the"
                + " intersection of the languages of the given automata. " + "Also known as intersection or meet.";

        // WARNING - Ensure that input type and description always match!
        inputType = new Class[] { FSAModel.class };
        inputDesc = new String[] { "Finite-state automata" };

        // WARNING - Ensure that output type and description always match!
        outputType = new Class[] { FSAModel.class };
        outputDesc = new String[] { "Product automaton" };
    }

    public int getNumberOfInputs() {
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pluggable.operation.Operation#perform(java.lang.Object[])
     */
    @Override
    public Object[] perform(Object[] inputs) {
        warnings.clear();
        FSAModel newInput;
        LinkedList<FSAModel> newInputs = new LinkedList<FSAModel>();
        FSAModel[] models = new FSAModel[inputs.length];
        boolean epsilonsRemoved = false;

        for (int i = 0; i < inputs.length; ++i) {
            if (inputs[i] instanceof FSAModel) {
                newInput = (FSAModel) inputs[i];

                if (FSAToolbox.containsEpsilonTransitions(newInput)) {

                    newInput = (FSAModel) OperationManager.instance().getOperation("removeepsilon")
                            .perform(new Object[] { newInput })[0];
                    warnings.addAll(OperationManager.instance().getOperation("removeepsilon").getWarnings());
                    epsilonsRemoved = true;
                }

                newInputs.add(newInput);
            }
        }

        models = newInputs.toArray(new FSAModel[0]);
        FSAModel model = Composition.product(models, "none");

        // if epsilon transitions were removed above, state labels no longer
        // make sense in product
        if (epsilonsRemoved) {
            model.removeAnnotation(AnnotationKeys.COMPOSED_OF);
        }

        return new Object[] { model };
        // FSAModel meetAutomata = ModelManager
        // .instance().createModel(FSAModel.class, "none");
        // Composition.product((FSAModel)inputs[0],
        // (FSAModel)inputs[1],
        // meetAutomata);
        // return new Object[] { meetAutomata };
    }

}
