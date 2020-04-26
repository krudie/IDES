package ides.api.model.supeventset;

import java.util.Iterator;

import ides.api.plugin.model.DESEvent;
import ides.api.plugin.model.DESEventSet;
import ides.api.plugin.model.DESModel;

/**
 * Interface for models of SupervisoryEvent sets.
 * 
 * @author Valerie Sugarman
 */
public interface SupervisoryEventSet extends DESEventSet, DESModel, SupEventSetPublisher {
    /**
     * Assembles a new event which can be added to the model. The event is not
     * automatically added to the model.
     * 
     * @param symbol the name of the event
     * @return a new event with the given name.
     */
    public abstract SupervisoryEvent assembleEvent(String symbol);

    /**
     * Assembles a new event with the same name and properties as the given event,
     * which can then be added to the model. The event is not automatically added to
     * the model.
     * 
     * @param event the event whose properties are to be copied.
     * @return As new SupervisoryEvent with the same name as the given event. If the
     *         given event is a SupervisoryEvent, the new event will have the same
     *         controllable and observable properties as the given event.
     */
    public abstract SupervisoryEvent assembleCopyOf(DESEvent event);

    /**
     * Get an iterator for all the SupervisoryEvents in the set.
     * 
     * @return an iterator for the events.
     */
    public abstract Iterator<SupervisoryEvent> iteratorSupervisory();

    /**
     * Get an iterator for all the controllable SupervisoryEvents in the set.
     * 
     * @return an iterator for the events.
     */
    public abstract Iterator<SupervisoryEvent> iteratorControllable();

    /**
     * Get an iterator for all the uncontrollable SupervisoryEvents in the set.
     * 
     * @return an iterator for the events.
     */
    public abstract Iterator<SupervisoryEvent> iteratorUncontrollable();

    /**
     * Get an iterator for all the observable SupervisoryEvents in the set.
     * 
     * @return an iterator for the events.
     */
    public abstract Iterator<SupervisoryEvent> iteratorObservable();

    /**
     * Get an iterator for all the unobservable SupervisoryEvents in the set.
     * 
     * @return an iterator for the events.
     */
    public abstract Iterator<SupervisoryEvent> iteratorUnobservable();

    /**
     * Produce a (deep) copy of this event set.
     * <p>
     * The events in the result will be copies of the original events.
     * 
     * @return a (deep) copy of this event set
     */
    public SupervisoryEventSet copy();

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
    public SupervisoryEventSet intersect(DESEventSet set);

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
    public SupervisoryEventSet union(DESEventSet set);

    /**
     * Produce an event set which contains the events in this event minus the events
     * in the given event set.
     * <p>
     * The events in the result will be copies of the original events.
     * 
     * @param set the event set whose content will be subtracted from this event set
     * @return a set with the events from this set minus the events in the given set
     */
    public SupervisoryEventSet subtract(DESEventSet set);

    /**
     * Get the event with the given event id.
     * 
     * @param id the id of the event
     * @return the event, null if it doesn't exist
     */
    public SupervisoryEvent getEvent(long id);

}
