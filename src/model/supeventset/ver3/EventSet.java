package model.supeventset.ver3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import ides.api.model.supeventset.SupEventSetMessage;
import ides.api.model.supeventset.SupEventSetPublisher;
import ides.api.model.supeventset.SupEventSetSubscriber;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.model.supeventset.SupervisoryEventSet;
import ides.api.plugin.model.DESEvent;
import ides.api.plugin.model.DESEventSet;
import ides.api.plugin.model.DESModelMessage;
import ides.api.plugin.model.DESModelSubscriber;
import ides.api.plugin.model.DESModelType;
import ides.api.plugin.model.ParentModel;

/**
 * Model of event set.
 * 
 * @author Valerie Sugarman
 */
public class EventSet implements SupervisoryEventSet, SupEventSetPublisher {
    /**
     * 
     */
    private static final long serialVersionUID = 6417657113613113965L;

    protected Hashtable<String, Object> annotations = new Hashtable<String, Object>();

    protected HashSet<SupervisoryEvent> set;

    private long maxEventId;

    private String name = null;

    public EventSet() // this is for use with ModelManager...createEmptyEventSet
    {
        set = new HashSet<SupervisoryEvent>();
        maxEventId = 0;
    }

    protected EventSet(String name) {
        set = new HashSet<SupervisoryEvent>();
        maxEventId = 0;
        this.name = name;
    }

    // Start with the actual operations on the set

    public static SupervisoryEventSet wrap(Collection<SupervisoryEvent> c) {
        SupervisoryEventSet ret = new EventSet();
        for (SupervisoryEvent e : c) {
            ret.add(e);
        }
        return ret;
    }

    public static SupervisoryEventSet wrap(DESEventSet s) {
        if (s instanceof SupervisoryEventSet) {
            return (SupervisoryEventSet) s;
        }
        SupervisoryEventSet ret = new EventSet();
        for (DESEvent de : s) {
            SupervisoryEvent e = new Event(de);
            ret.add(e);
        }
        return ret;
    }

    public SupervisoryEventSet copy() {
        SupervisoryEventSet ret = new EventSet(this.name);
        for (SupervisoryEvent e : set) {
            ret.add(new Event(e));// this way it copies all the properties, but
            // keeps the same ids (it was messing up
            // uses of intersect, subtract, in the operations to use
            // assembleCopyOf() here)
        }
        return ret;
    }

    public SupervisoryEventSet intersect(DESEventSet s) {
        SupervisoryEventSet copy = copy();
        copy.retainAll(s);
        return copy;

    }

    public SupervisoryEventSet subtract(DESEventSet s) {
        SupervisoryEventSet copy = copy();
        copy.removeAll(s);
        return copy;
    }

    /**
     * Makes a copy of the current event set and adds new assembled events of the
     * given event set to it.
     */
    public SupervisoryEventSet union(DESEventSet s) {
        SupervisoryEventSet copy = copy();
        SupervisoryEventSet copySet;
        if (s instanceof SupervisoryEventSet) {
            copySet = (SupervisoryEventSet) s;
        } else {
            SupervisoryEventSet otherSet = wrap(s);
            copySet = otherSet;
        }

        for (Iterator<SupervisoryEvent> i = copySet.iteratorSupervisory(); i.hasNext();) {
            SupervisoryEvent copyEvent = assembleCopyOf(i.next());
            copy.add(copyEvent);
        }
        return copy;

    }

    public SupervisoryEvent assembleEvent(String symbol) {
        SupervisoryEvent e = new Event(getFreeEventId());
        e.setSymbol(symbol);
        return e;
    }

    public SupervisoryEvent assembleCopyOf(DESEvent event) {
        SupervisoryEvent e = assembleEvent(event.getSymbol());
        if (event instanceof SupervisoryEvent) {
            SupervisoryEvent inputEvent = (SupervisoryEvent) event;
            e.setObservable(inputEvent.isObservable());
            e.setControllable(inputEvent.isControllable());
        }
        return e;
    }

    public SupervisoryEvent getEvent(long id) {
        for (Iterator<SupervisoryEvent> i = set.iterator(); i.hasNext();) {
            SupervisoryEvent event = i.next();
            if (event.getId() == id) {
                return event;
            }
        }
        return null;
    }

    public boolean add(DESEvent e) {
        boolean ret;
        if (e instanceof SupervisoryEvent) {
            ret = set.add((SupervisoryEvent) e);
        } else {
            ret = set.add(new Event(e)); // wrap it as a SupervisoryEvent
        }

        if (ret == true) {
            fireSupEventSetChanged(new SupEventSetMessage(SupEventSetMessage.ADD, e.getId(), this));
            maxEventId = maxEventId < e.getId() ? e.getId() : maxEventId;
            setNeedsSave(true);
        }

        return ret;
    }

    public boolean addAll(Collection<? extends DESEvent> c) {
        boolean isChanged = false;
        for (DESEvent e : c) {
            isChanged |= add(e);
        }
        return isChanged;
    }

    // calls remove so proper messaging takes place
    public void clear() {
        removeAll(this);
    }

    public boolean contains(Object o) {
        return set.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return set.containsAll(c);
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public Iterator<DESEvent> iterator() {
        return new HashSet<DESEvent>(set).iterator();
    }

    public boolean remove(Object o) {
        boolean ret;
        ret = set.remove(o);

        if (ret == true) {
            fireSupEventSetChanged(
                    new SupEventSetMessage(SupEventSetMessage.REMOVE, ((SupervisoryEvent) o).getId(), this));
            setNeedsSave(true);
        }
        return ret;
    }

    // calls remove so proper messages are sent.
    public boolean removeAll(Collection<?> c) {
        boolean isChanged = false;
        for (Object o : c) {
            isChanged |= remove(o);
        }
        return isChanged;
    }

    // calls remove so proper messages are sent
    public boolean retainAll(Collection<?> c) {
        boolean isChanged = false;
        HashSet<SupervisoryEvent> eventsToRemove = new HashSet<SupervisoryEvent>();
        for (Iterator<SupervisoryEvent> i = iteratorSupervisory(); i.hasNext();) {
            SupervisoryEvent e = i.next();
            if (!c.contains(e)) {
                eventsToRemove.add(e);
            }
        }
        for (SupervisoryEvent e : eventsToRemove) {
            isChanged |= remove(e);
        }
        return isChanged;
    }

    public int size() {
        return set.size();
    }

    public Object[] toArray() {
        return set.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return set.toArray(a);
    }

    public Iterator<SupervisoryEvent> iteratorSupervisory() {
        return set.iterator();
    }

    public Iterator<SupervisoryEvent> iteratorControllable() {
        HashSet<SupervisoryEvent> controllable = new HashSet<SupervisoryEvent>();
        for (SupervisoryEvent e : set) {
            if (e.isControllable()) {
                controllable.add(e);
            }
        }
        return controllable.iterator();
    }

    public Iterator<SupervisoryEvent> iteratorObservable() {
        HashSet<SupervisoryEvent> observable = new HashSet<SupervisoryEvent>();
        for (SupervisoryEvent e : set) {
            if (e.isObservable()) {
                observable.add(e);
            }
        }
        return observable.iterator();
    }

    public Iterator<SupervisoryEvent> iteratorUncontrollable() {
        HashSet<SupervisoryEvent> uncontrollable = new HashSet<SupervisoryEvent>();
        for (SupervisoryEvent e : set) {
            if (!e.isControllable()) {
                uncontrollable.add(e);
            }
        }
        return uncontrollable.iterator();
    }

    public Iterator<SupervisoryEvent> iteratorUnobservable() {
        HashSet<SupervisoryEvent> unobservable = new HashSet<SupervisoryEvent>();
        for (SupervisoryEvent e : set) {
            if (!e.isObservable()) {
                unobservable.add(e);
            }
        }
        return unobservable.iterator();
    }

    public long getFreeEventId() {
        return ++maxEventId;
    }

    // Next, the methods required by the DESModel interface

    private boolean needsSave;

    public static final DESModelType descriptor = new SupervisoryEventSetDescriptor();

    protected ParentModel parent = null;

    public DESEventSet getEventSet() {
        return this;
    }

    public DESModelType getModelType() {
        return descriptor;
    }

    public String getName() {
        return name;
    }

    public ParentModel getParentModel() {
        return parent;
    }

    public void metadataChanged() {
        setNeedsSave(true);
    }

    public void modelSaved() {
        setNeedsSave(false);
    }

    public boolean needsSave() {
        return needsSave;
    }

    public void setName(String name) {
        if (this.name != null && this.name.equals(name)) {
            return;
        }
        this.name = name;
        for (DESModelSubscriber s : DESModelSubscribers) {
            s.modelNameChanged(new DESModelMessage(DESModelMessage.NAME, this));
        }
        setNeedsSave(true);
    }

    public void setParentModel(ParentModel model) {
        parent = model;
    }

    public Object getAnnotation(String key) {
        return annotations.get(key);
    }

    public boolean hasAnnotation(String key) {
        return annotations.containsKey(key);
    }

    public void removeAnnotation(String key) {
        annotations.remove(key);
    }

    public void setAnnotation(String key, Object annotation) {
        if (annotation != null) {
            annotations.put(key, annotation);
        }
    }

    protected void setNeedsSave(boolean b) {
        if (needsSave != b) {
            needsSave = b;
            for (DESModelSubscriber s : DESModelSubscribers.toArray(new DESModelSubscriber[] {})) {
                s.saveStatusChanged(
                        new DESModelMessage(needsSave ? DESModelMessage.DIRTY : DESModelMessage.CLEAN, this));
            }
        }

    }

    // Publisher parts

    // The DESModelPublisher part (included in DESModel which is included in
    // SupEventSet

    private ArrayList<DESModelSubscriber> DESModelSubscribers = new ArrayList<DESModelSubscriber>();

    public void addSubscriber(DESModelSubscriber subscriber) {
        DESModelSubscribers.add(subscriber);
    }

    public DESModelSubscriber[] getDESModelSubscribers() {
        return DESModelSubscribers.toArray(new DESModelSubscriber[] {});
    }

    public void removeSubscriber(DESModelSubscriber subscriber) {
        DESModelSubscribers.remove(subscriber);
    }

    // SupEventSetPublisher part

    private ArrayList<SupEventSetSubscriber> SupEventSetSubscribers = new ArrayList<SupEventSetSubscriber>();

    public void addSubscriber(SupEventSetSubscriber subscriber) {
        SupEventSetSubscribers.add(subscriber);
    }

    public void fireSupEventSetChanged(SupEventSetMessage message) {
        for (SupEventSetSubscriber s : SupEventSetSubscribers) {
            s.supEventSetChanged(message);
        }
        setNeedsSave(true);
    }

    public SupEventSetSubscriber[] getSupEventSetSubscribers() {
        return SupEventSetSubscribers.toArray(new SupEventSetSubscriber[] {});
    }

    public void removeSubscriber(SupEventSetSubscriber subscriber) {
        SupEventSetSubscribers.remove(subscriber);
    }

}
