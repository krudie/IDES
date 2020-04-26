package ides.api.presentation.fsa;

import java.util.Iterator;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.plugin.model.DESModel;
import util.AnnotationKeys;

/**
 * Provides state labelling services for {@link FSAModel}s.
 * 
 * @author Lenko Grigorov
 */
public class FSAStateLabeller {

    /**
     * Labels in a human-readable way the states in models obtained by composing a
     * number of other models (e.g., by calling the "product" or "sync" operations).
     * <p>
     * The {@link FSAModel} whose states will be labelled has to have the
     * {@link AnnotationKeys#COMPOSED_OF} annotation with {@link String}[] array of
     * the names of the models used in the composition. For example, if the
     * "product" operation has been called to create the model by intersecting the
     * models "A" and "Untitled", the annotation should be<br>
     * <code> String[]{"A", "Untitled"} </code>
     * <p>
     * Each state in the model also has to have the
     * {@link AnnotationKeys#COMPOSED_OF} annotation with an array of the IDs of the
     * states out of which it is composed. For example, if a state is the
     * composition of the state with ID=34 from "A" and the state with ID=43523 from
     * "Untitled", the annotation should be<br>
     * <code> long[]{34, 43523} </code><br>
     * Furthermore, states can have the {@link AnnotationKeys#COMPOSED_OF_NAMES}
     * annotation with an array of the names of the states out of which it is
     * composed. For examples, if a state is the composition of the state called "1"
     * from "A" and the state called "opened" from "Untitled", the annotation should
     * be<br>
     * <code> String[]{"1", "open"} </code>
     * <p>
     * Most built-in operations that come with IDES annotate properly composite
     * models and states.
     * 
     * @param fsa the model whose states have to be labelled.
     */
    public static void labelCompositeStates(FSAModel fsa) {
        if (fsa.getAnnotation(AnnotationKeys.COMPOSED_OF) == null
                || !(fsa.getAnnotation(AnnotationKeys.COMPOSED_OF) instanceof String[])) {
            // no "composed of" info
            return;
        }
        if (((String[]) fsa.getAnnotation(AnnotationKeys.COMPOSED_OF)).length > 1) {
            // "composed of" more than one models
            FSAModel[] gs = new FSAModel[((String[]) fsa.getAnnotation(AnnotationKeys.COMPOSED_OF)).length];
            for (int i = 0; i < gs.length; ++i) {
                DESModel m = Hub.getWorkspace().getModel(((String[]) fsa.getAnnotation(AnnotationKeys.COMPOSED_OF))[i]);
                if (m == null || !(m instanceof FSAModel)) {
                    gs[i] = null;
                } else {
                    gs[i] = (FSAModel) m;
                }
            }
            for (Iterator<FSAState> si = fsa.getStateIterator(); si.hasNext();) {
                FSAState s = si.next();
                String label = "(";
                String[] names = (String[]) s.getAnnotation(AnnotationKeys.COMPOSED_OF_NAMES);
                if (names != null && names.length > 0) {
                    // "composed of names" is available
                    for (int i = 0; i < names.length - 1; ++i) {
                        label += names[i] + ",";
                    }
                    label += names[names.length - 1];
                } else {
                    // "composed of names" is not available; try to recover
                    // state names from original models
                    long[] composition = (long[]) s.getAnnotation(AnnotationKeys.COMPOSED_OF);
                    if (composition == null) {
                        nameError(s);
                        continue;
                    }
                    FSAState subState;
                    boolean error = false;
                    for (int i = 0; i < gs.length - 1; ++i) {
                        if (gs[i] == null) {
                            error = true;
                            break;
                        }
                        subState = gs[i].getState(composition[i]);
                        if (subState == null) {
                            error = true;
                            break;
                        }
                        label += subState.getName() + ",";
                    }
                    if (error) {
                        nameError(s);
                        continue;
                    }
                    if (gs[gs.length - 1] == null) {
                        nameError(s);
                        continue;
                    }
                    subState = gs[gs.length - 1].getState(composition[gs.length - 1]);
                    if (subState == null) {
                        nameError(s);
                        continue;
                    }
                    label += subState.getName();
                }
                label += ")";
                s.setName(label);
            }
        } else if (((String[]) fsa.getAnnotation(AnnotationKeys.COMPOSED_OF)).length == 1) {
            // "composed of" only one model (e.g. when using "projection")
            DESModel m = Hub.getWorkspace().getModel(((String[]) fsa.getAnnotation(AnnotationKeys.COMPOSED_OF))[0]);
            if (m == null || !(m instanceof FSAModel)) {
                m = null;
            }
            FSAModel g = (FSAModel) m;
            for (Iterator<FSAState> si = fsa.getStateIterator(); si.hasNext();) {
                FSAState s = si.next();
                String label = "";
                String[] names = (String[]) s.getAnnotation(AnnotationKeys.COMPOSED_OF_NAMES);
                if (names != null && names.length > 0) {
                    // "composed of names" available
                    if (names.length > 1) {
                        label = "(";
                        for (int i = 0; i < names.length - 1; ++i) {
                            label += names[i] + ",";
                        }
                        label += names[names.length - 1];
                        label += ")";
                    } else {
                        label += names[0];
                    }
                } else {
                    // "composed of names" not available; try to recover state
                    // names from original models
                    long[] composition = (long[]) s.getAnnotation(AnnotationKeys.COMPOSED_OF);
                    if (composition == null || g == null) {
                        nameError(s);
                        continue;
                    }
                    if (composition.length > 1) {
                        label = "(";
                        FSAState subState;
                        boolean error = false;
                        for (int i = 0; i < composition.length - 1; ++i) {
                            subState = g.getState(composition[i]);
                            if (subState == null) {
                                error = true;
                                break;
                            }
                            label += subState.getName() + ",";
                        }
                        if (error) {
                            nameError(s);
                            continue;
                        }
                        subState = g.getState(composition[composition.length - 1]);
                        if (subState == null) {
                            nameError(s);
                            continue;
                        }
                        label += subState.getName() + ")";
                    } else if (composition.length > 0) {
                        FSAState subState = g.getState(composition[0]);
                        if (subState == null) {
                            nameError(s);
                            continue;
                        }
                        label = subState.getName();
                    }
                }
                s.setName(label);
            }
        }
    }

    private static void nameError(FSAState s) {
        s.setName("E" + s.getId());
    }
}
