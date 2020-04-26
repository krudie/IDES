package ides.api.model.fsa;

import ides.api.plugin.model.DESEventSet;

/**
 * An FSA supervisor is an FSA with a control map.
 * 
 * @author Lenko Grigorov
 */
public interface FSASupervisor extends FSAModel {
    /**
     * Returns the events disabled at a given state. If the control map is undefined
     * (e.g., not computed yet or out of synch with the rest of the models), this
     * method should return <code>null</code>.
     * 
     * @param state state of the supervisor
     * @return the events disabled at a given state; or <code>null</code> if the
     *         control map is undefined
     */
    public DESEventSet getDisabledEvents(FSAState state);

    /**
     * Sets the events disabled at a given state.
     * 
     * @param state state of the supervisor
     * @param set   set of disabled events for this state
     */
    public void setDisabledEvents(FSAState state, DESEventSet set);
}
