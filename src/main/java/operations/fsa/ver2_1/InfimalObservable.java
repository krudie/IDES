package operations.fsa.ver2_1;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.model.supeventset.SupervisoryEventSet;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.operation.FSAToolbox;
import ides.api.plugin.operation.Operation;
import ides.api.plugin.operation.OperationManager;
import util.AnnotationKeys;

/**
 * Calculates the infimal observable prefix-closed superlanguage of the given
 * language with respect to the plant. Refer to "The infimal prefix-closed and
 * observable superlanguage of a given language" by Karen Rudie and W. Murray
 * Wonham 1990.
 * 
 * @author Valerie Sugarman
 */
public class InfimalObservable implements Operation {

    protected LinkedList<String> warnings = new LinkedList<String>();

    public String getDescription() {
        return "Computes the infimal observable superlanguage of the given language with respect to the plant.";
    }

    public String[] getDescriptionOfInputs() {
        return new String[] { "Plant", "Language" };
    }

    public String[] getDescriptionOfOutputs() {
        return new String[] { "Infimal obsersable superlangauge" };
    }

    public String getName() {
        return "infobs";
    }

    public int getNumberOfInputs() {
        return 2;
    }

    public int getNumberOfOutputs() {
        return 1;
    }

    public Class<?>[] getTypeOfInputs() {
        return new Class<?>[] { FSAModel.class, FSAModel.class };
    }

    public Class<?>[] getTypeOfOutputs() {
        return new Class<?>[] { FSAModel.class };
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public Object[] perform(Object[] inputs) {
        warnings.clear();
        FSAModel plant;
        FSAModel language;

        if (inputs.length >= 2) {
            if (inputs[0] instanceof FSAModel && inputs[1] instanceof FSAModel) {
                plant = ((FSAModel) inputs[0]).clone();
                language = ((FSAModel) inputs[1]);
            } else {
                warnings.add(FSAToolbox.ILLEGAL_ARGUMENT);
                return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
            }
        } else {
            warnings.add(FSAToolbox.ILLEGAL_NUMBER_OF_ARGUMENTS);
            return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
        }

        if (FSAToolbox.hasObservabilityConflict(new FSAModel[] { plant, language })) {
            warnings.add(FSAToolbox.ERROR_OBSERVE);
            return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
        }

        if (FSAToolbox.isEmptyLanguage(language)) {
            warnings.add("Operation not defined for the empty language.");
            return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
        }

        // Double check that sublanguage is in fact a sublanguage of the
        // plant.
        boolean isSublanguage = (Boolean) OperationManager.instance().getOperation("subset")
                .perform(new Object[] { language, plant })[0];
        warnings.addAll(OperationManager.instance().getOperation("subset").getWarnings());

        if (!isSublanguage) {
            warnings.add(Hub.string("errorNotSublanguage"));
            return new Object[] { ModelManager.instance().createModel(FSAModel.class) };
        }

        // start the inverse of the projection of l-bar (which involves lots of
        // union-ing) with epsilon
        FSAModel invproj = ModelManager.instance().createModel(FSAModel.class);
        FSAState s = invproj.assembleState();
        s.setInitial(true);
        s.setMarked(true);
        invproj.add(s);

        FSAModel temp, temp2;
        FSAModel lBar = (FSAModel) OperationManager.instance().getOperation("prefixclose")
                .perform(new Object[] { language })[0];
        warnings.addAll(OperationManager.instance().getOperation("prefixclose").getWarnings());

        // treat the event set of the plant as the alphabet
        FSAModel kleeneClosure = (FSAModel) OperationManager.instance().getOperation("kleeneclosure")
                .perform(new Object[] { plant.getEventSet() })[0];
        warnings.addAll(OperationManager.instance().getOperation("kleeneclosure").getWarnings());

        SupervisoryEventSet unobservableEvents = ModelManager.instance().createModel(SupervisoryEventSet.class);

        for (Iterator<SupervisoryEvent> i = plant.getEventIterator(); i.hasNext();) {
            SupervisoryEvent e = i.next();
            if (!e.isObservable()) {
                unobservableEvents.add(e);
            }
        }

        // iterate over plant events - take these as the alphabet
        for (Iterator<SupervisoryEvent> i = plant.getEventIterator(); i.hasNext();) {
            SupervisoryEvent e = i.next();
            FSAModel currEventModel = eventToModel(e);
            temp = (FSAModel) OperationManager.instance().getOperation("concatenate")
                    .perform(new Object[] { lBar, currEventModel })[0];
            warnings.addAll(OperationManager.instance().getOperation("concatenate").getWarnings());

            temp = (FSAModel) OperationManager.instance().getOperation("product")
                    .perform(new Object[] { temp, lBar })[0];
            warnings.addAll(OperationManager.instance().getOperation("product").getWarnings());

            temp = (FSAModel) OperationManager.instance().getOperation("observer").perform(new Object[] { temp })[0];
            warnings.addAll(OperationManager.instance().getOperation("observer").getWarnings());

            temp = (FSAModel) OperationManager.instance().getOperation("selfloop")
                    .perform(new Object[] { temp, unobservableEvents })[0];
            warnings.addAll(OperationManager.instance().getOperation("selfloop").getWarnings());

            temp2 = (FSAModel) OperationManager.instance().getOperation("concatenate")
                    .perform(new Object[] { kleeneClosure, currEventModel })[0];
            warnings.addAll(OperationManager.instance().getOperation("concatenate").getWarnings());

            temp = (FSAModel) OperationManager.instance().getOperation("product")
                    .perform(new Object[] { temp, temp2 })[0];
            warnings.addAll(OperationManager.instance().getOperation("product").getWarnings());

            invproj = (FSAModel) OperationManager.instance().getOperation("union")
                    .perform(new Object[] { invproj, temp })[0];
            warnings.addAll(OperationManager.instance().getOperation("union").getWarnings());
        }

        temp = (FSAModel) OperationManager.instance().getOperation("+closure")
                .perform(new Object[] { plant.getEventSet() })[0];
        warnings.addAll(OperationManager.instance().getOperation("+closure").getWarnings());

        temp = (FSAModel) OperationManager.instance().getOperation("setminus")
                .perform(new Object[] { temp, invproj })[0];
        warnings.addAll(OperationManager.instance().getOperation("setminus").getWarnings());

        temp = (FSAModel) OperationManager.instance().getOperation("concatenate")
                .perform(new Object[] { temp, kleeneClosure })[0];
        warnings.addAll(OperationManager.instance().getOperation("concatenate").getWarnings());

        // turn plant into L(G) (mark all the states).
        for (Iterator<FSAState> i = plant.getStateIterator(); i.hasNext();) {
            FSAState state = i.next();
            state.setMarked(true);
        }

        FSAModel infO = (FSAModel) OperationManager.instance().getOperation("setminus")
                .perform(new Object[] { plant, temp })[0];
        warnings.addAll(OperationManager.instance().getOperation("setminus").getWarnings());

        // trim to avoid unnecessarily complicated output
        infO = (FSAModel) OperationManager.instance().getFilterOperation("trim").filter(new Object[] { infO })[0];
        warnings.addAll(OperationManager.instance().getOperation("trim").getWarnings());

        // remove the now meaningless state annotations
        infO.removeAnnotation(AnnotationKeys.COMPOSED_OF);

        return new Object[] { infO };
    }

    /**
     * Creates a model that represents the given event as a string
     * 
     * @param e
     * @return
     */
    private FSAModel eventToModel(SupervisoryEvent e) {
        FSAModel model = ModelManager.instance().createModel(FSAModel.class);
        model.add(e);
        FSAState initial = model.assembleState();
        initial.setInitial(true);
        model.add(initial);
        FSAState marked = model.assembleState();
        marked.setMarked(true);
        model.add(marked);
        model.add(model.assembleTransition(initial.getId(), marked.getId(), e.getId()));
        return model;
    }

}
