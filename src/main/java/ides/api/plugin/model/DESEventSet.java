package ides.api.plugin.model;

import java.util.Set;

/**
 * A set of {@link DESEvent}s.
 * 
 * @author Lenko Grigorov
 */
public interface DESEventSet extends Set<DESEvent> {
    /**
     * Produce a (deep) copy of this event set.
     * <p>
     * The events in the result will be copies of the original events.
     * 
     * @return a (deep) copy of this event set
     */
    public DESEventSet copy();

    /**
     * Produce an event set which contains the intersection of this event set and
     * the given event set.
     * <p>
     * The events in the intersection will be copies of the events in this event
     * set. Thus, the method may be non-commutative in certain cases.
     * 
     * @param set the event set whose content will be used for the intersection
     * @return the intersection of this event set and the given event set
     */
    public DESEventSet intersect(DESEventSet set);

    /**
     * Produce an event set which contains the union of this event set and the given
     * event set.
     * <p>
     * The events in the intersection will be copies of the events in this event set
     * and copies of all the events in the given set which are not in this set.
     * Thus, the method may be non-commutative in certain cases.
     * 
     * @param set the event set whose content will be used for the union
     * @return the union of this event set and the given event set
     */
    public DESEventSet union(DESEventSet set);

    /**
     * Produce an event set which contains the events in this event minus the events
     * in the given event set.
     * <p>
     * The events in the result will be copies of the original events.
     * 
     * @param set the event set whose content will be subtracted from this event set
     * @return a set with the events from this set minus the events in the given set
     */
    public DESEventSet subtract(DESEventSet set);
}
