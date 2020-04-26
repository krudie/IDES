package operations.fsa.ver2_1;

import java.util.Vector;

import ides.api.model.fsa.FSAModel;
import ides.api.plugin.operation.Operation;
import ides.api.plugin.operation.OperationManager;

public class LocalModular extends AbstractOperation {

    public LocalModular() {
        NAME = "localmodular";
        DESCRIPTION = "Determines if the languages" + " recognized by the given automata are locally modular.";
        // WARNING - Ensure that input type and description always match!
        inputType = new Class[] { FSAModel.class };
        inputDesc = new String[] { "Finite-state automata" };

        // WARNING - Ensure that output type and description always match!
        outputType = new Class[] { Boolean.class };
        outputDesc = new String[] { "resultMessage" };
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
        Vector<FSAModel> models = new Vector<FSAModel>();
        for (int i = 0; i < inputs.length; ++i) {
            if (inputs[i] instanceof FSAModel)
                models.add((FSAModel) inputs[i]);
        }
        Operation prefix = OperationManager.instance().getOperation("prefixclose");
        Operation sync = OperationManager.instance().getOperation("sync");
        Vector<FSAModel> pModels = new Vector<FSAModel>();
        for (FSAModel m : models) {
            pModels.add((FSAModel) prefix.perform(new Object[] { m })[0]);
            warnings.addAll(prefix.getWarnings());
        }
        FSAModel l = models.firstElement();
        for (int i = 1; i < models.size(); ++i) {
            l = (FSAModel) sync.perform(new Object[] { l, models.elementAt(i) })[0];
            warnings.addAll(sync.getWarnings());
        }
        l = (FSAModel) prefix.perform(new Object[] { l })[0];
        warnings.addAll(prefix.getWarnings());
        FSAModel r = pModels.firstElement();
        for (int i = 1; i < pModels.size(); ++i) {
            r = (FSAModel) sync.perform(new Object[] { r, pModels.elementAt(i) })[0];
            warnings.addAll(sync.getWarnings());
        }
        boolean equal = ((Boolean) OperationManager.instance().getOperation("subset").perform(new Object[] { r, l })[0])
                .booleanValue();
        warnings.addAll(OperationManager.instance().getOperation("subset").getWarnings());

        String resultMessage = "";
        if (equal) {
            resultMessage = "The two automata are locally modular.";
        } else {
            resultMessage = "The two automata are not locally modular.";
        }
        outputDesc = new String[] { resultMessage };
        return new Object[] { new Boolean(equal) };
    }

}
