package ides.api.model.supeventset;

import ides.api.plugin.model.DESEvent;

/**
 * Defines an event in a finite state automaton or event set.
 * 
 * @author Helen Bretzke
 */
public interface SupervisoryEvent extends DESEvent, Comparable<DESEvent> {
    /**
     * Returns true iff this event's controllable property is set to true.
     * 
     * @return true iff this event is controllable
     */
    public abstract boolean isControllable();

    /**
     * Sets this event's controllable property to <code>b</code>.
     * 
     * @param b the value to set
     */
    public abstract void setControllable(boolean b);

    /**
     * Returns true iff this event's observable property is set to true.
     * 
     * @return true iff this event is observable
     */
    public abstract boolean isObservable();

    /**
     * Sets this event's observable property to <code>b</code>.
     * 
     * @param b the value to set
     */
    public abstract void setObservable(boolean b);

    /**
     * Returns true iff <code>o</code> is of type DESEvent and has the same name as
     * this SupervisoryEvent.
     * 
     * @param o another object
     * @return true iff <code>o</code> is of type DESEvent and has the same name as
     *         this SupervisoryEvent.
     */
    public abstract boolean equals(Object o);

}