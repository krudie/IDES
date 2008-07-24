package model.fsa.ver2_1;

// import io.fsa.ver2_1.SubElement;

import ides.api.core.Annotable;
import ides.api.core.Hub;
import ides.api.model.fsa.FSAEvent;
import ides.api.model.fsa.FSAEventSet;
import ides.api.model.fsa.FSAMessage;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAPublisherAdaptor;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSASupervisor;
import ides.api.model.fsa.FSATransition;
import ides.api.plugin.model.DESModel;
import ides.api.plugin.model.DESModelMessage;
import ides.api.plugin.model.DESModelSubscriber;
import ides.api.plugin.model.DESModelType;
import ides.api.plugin.model.ModelManager;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import services.General;
import util.StupidSetWrapper;

/**
 * This class is the topmost class in the automaton hierarchy. It serves as the
 * datastructure for states, transitions and events, and gives access to data
 * iterators that are customized to maintain data integrity of the automaton at
 * all times.
 * 
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 * @author Lenko Grigorov
 */
public class Automaton extends FSAPublisherAdaptor implements Cloneable,
		FSASupervisor
{

	/**
	 * DESModelPublisher part which maintains a collection of, and sends change
	 * notifications to, all interested observers (subscribers).
	 */
	private ArrayList<DESModelSubscriber> mwSubscribers = new ArrayList<DESModelSubscriber>();

	private boolean needsSave = false;

	public boolean needsSave()
	{
		return needsSave;
	}

	/**
	 * Attaches the given subscriber to this publisher. The given subscriber
	 * will receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void addSubscriber(DESModelSubscriber subscriber)
	{
		mwSubscribers.add(subscriber);
	}

	/**
	 * Removes the given subscriber to this publisher. The given subscriber will
	 * no longer receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void removeSubscriber(DESModelSubscriber subscriber)
	{
		mwSubscribers.remove(subscriber);
	}

	/**
	 * Returns all current subscribers to this publisher.
	 * 
	 * @return all current subscribers to this publisher
	 */
	public DESModelSubscriber[] getDESModelSubscribers()
	{
		return mwSubscribers.toArray(new DESModelSubscriber[] {});
	}

	/**
	 * Notifies the model that some associated metadata has been changed.
	 */
	public void metadataChanged()
	{
		setNeedsSave(true);
	}

	protected void setNeedsSave(boolean b)
	{
		if (needsSave != b)
		{
			needsSave = b;
			DESModelMessage message = new DESModelMessage(
					needsSave ? DESModelMessage.DIRTY : DESModelMessage.CLEAN,
					this);
			for (DESModelSubscriber s : mwSubscribers)
			{
				s.saveStatusChanged(message);
			}
		}

	}

	/**
	 * Notifies the model that it has been saved.
	 */
	public void modelSaved()
	{
		this.setNeedsSave(false);
	}

	protected static class AutomatonDescriptor implements DESModelType
	{
		public Class<?>[] getModelPerspectives()
		{
			return new Class[] { FSAModel.class, FSASupervisor.class };
		}

		public Class<?> getMainPerspective()
		{
			return FSAModel.class;
		}

		public String getIOTypeDescription()
		{
			return "FSA";
		}

		public String getDescription()
		{
			return "Finite State Automaton";
		}

		public Image getIcon()
		{
			return Toolkit.getDefaultToolkit().createImage(Hub
					.getResource("images/icons/model_fsa.gif"));
		}

		// public DESModel createModel(String id)
		// {
		// Automaton a=new Automaton("");
		// a.setId(id);
		// return a;
		// }
		public DESModel createModel(String name)
		{
			Automaton a = new Automaton(name);
			a.setName(name);
			return a;
		}
	}

	public static final DESModelType myDescriptor = new AutomatonDescriptor();

	private LinkedList<FSAState> states;

	private LinkedList<FSATransition> transitions;

	private LinkedList<FSAEvent> events;

	private String name = null;

	protected Hashtable<String, Object> annotations = new Hashtable<String, Object>();

	// /**
	// * If this automaton represents the composition of other automata,
	// * this will contain a list the ids of these other automata.
	// */
	// protected String[] composedOf = new String[0];

	// private SubElement meta = null;

	private long maxStateId;

	private long maxEventId;

	private long maxTransitionId;

	/**
	 * constructs a nem automaton with the name name.
	 * <p>
	 * Do not instantiate directly. Use
	 * {@link ModelManager#createModel(Class, String)} instead.
	 * 
	 * @param name
	 *            the name of the automaton
	 * @see ModelManager#createModel(Class)
	 * @see ModelManager#createModel(Class, String)
	 */
	protected Automaton(String name)
	{
		states = new LinkedList<FSAState>();
		transitions = new LinkedList<FSATransition>();
		events = new LinkedList<FSAEvent>();
		this.name = name;
		// The first ID for model elements will be (maxId + 1);
		maxStateId = 0;
		maxTransitionId = 0;
		maxEventId = 0;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public FSAModel clone()
	{
		Automaton clone = new Automaton(this.name);
		clone.setName(General.getRandomId());
		// Cloning the events:
		ListIterator<FSAEvent> ei = getEventIterator();
		while (ei.hasNext())
		{
			clone.add(new Event((Event)ei.next()));
		}
		// Cloning the states:
		ListIterator<FSAState> si = getStateIterator();
		while (si.hasNext())
		{
			FSAState tmpState = si.next();
			try
			{
				ByteArrayOutputStream fo = new ByteArrayOutputStream();
				ObjectOutputStream so = new ObjectOutputStream(fo);
				so.writeObject(tmpState.getAnnotation(Annotable.LAYOUT));
				so.flush();
				so.close();
				ByteArrayInputStream is = new ByteArrayInputStream(fo
						.toByteArray());
				ObjectInputStream objectIS = new ObjectInputStream(is);
				Object layout = objectIS.readObject();
				is.close();
				State s = new State(tmpState);
				s.setId(tmpState.getId());
				s.setMarked(tmpState.isMarked());
				s.setInitial(tmpState.isInitial());
				s.setName(tmpState.getName());
				s.setAnnotation(Annotable.LAYOUT, layout);
				clone.add(s);
			}
			catch (Exception e)
			{
			}
		}

		// Cloning the transitions
		ListIterator<FSATransition> ti = getTransitionIterator();
		while (ti.hasNext())
		{
			Transition oldt = (Transition)ti.next();
			try
			{
				State source = (State)clone.getState(oldt.getSource().getId());
				State target = (State)clone.getState(oldt.getTarget().getId());
				ByteArrayOutputStream fo = new ByteArrayOutputStream();
				ObjectOutputStream so = new ObjectOutputStream(fo);
				so.writeObject(oldt.getAnnotation(Annotable.LAYOUT));
				so.flush();
				ByteArrayInputStream is = new ByteArrayInputStream(fo
						.toByteArray());
				ObjectInputStream objectIS = new ObjectInputStream(is);
				Object layout = objectIS.readObject();
				is.close();
				Transition t = new Transition(oldt, source, target);
				t.setAnnotation(Annotable.LAYOUT, layout);
				clone.add(t);
				if (oldt.getEvent() != null)
				{
					Event event = new Event((Event)clone.getEvent(oldt
							.getEvent().getId()));
					t.setEvent(event);
				}
			}
			catch (Exception e)
			{
			}
		}
		return clone;
	}

	/*
	 * (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#getName()
	 */
	public String getName()
	{
		return name;
	}

	// public FSAModel getFSAModel()
	// {
	// return this;
	// }

	public DESModelType getModelType()
	{
		return myDescriptor;
	}

	/*
	 * (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#setName(java.lang.String)
	 */
	public void setName(String name)
	{
		if (this.name != null && this.name.equals(name))
		{
			return;
		}
		this.name = name;
		DESModelMessage message = new DESModelMessage(
				DESModelMessage.NAME,
				this);
		for (DESModelSubscriber s : mwSubscribers)
		{
			s.modelNameChanged(message);
		}
		this.metadataChanged();
	}

	/*
	 * (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#getStateCount()
	 */
	public long getStateCount()
	{
		return states.size();
	}

	/*
	 * (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#getTransitionCount()
	 */
	public long getTransitionCount()
	{
		return transitions.size();
	}

	public long getEventCount()
	{
		return events.size();
	}

	/**
	 * @return event id which is not in use
	 */
	public long getFreeEventId()
	{
		return ++maxEventId;
	}

	/**
	 * @return transition id which is not in use
	 */
	public long getFreeTransitionId()
	{
		return ++maxTransitionId;
	}

	/**
	 * @return state id which is not in use
	 */
	public long getFreeStateId()
	{
		return ++maxStateId;
	}

	/*
	 * (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#add(model.fsa.FSAState)
	 */
	public void add(FSAState s)
	{
		states.add(s);
		maxStateId = maxStateId < s.getId() ? s.getId() : maxStateId;
		fireFSAStructureChanged(new FSAMessage(
				FSAMessage.ADD,
				FSAMessage.STATE,
				s.getId(),
				this));
		this.metadataChanged();

	}

	/*
	 * (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#remove(model.fsa.FSAState)
	 */
	public void remove(FSAState s)
	{
		ListIterator<FSATransition> sources = s
				.getOutgoingTransitionsListIterator();
		while (sources.hasNext())
		{
			FSATransition t = sources.next();
			sources.remove();
			t.getSource().removeOutgoingTransition(t);
			t.getTarget().removeIncomingTransition(t);
		}
		ListIterator<FSATransition> targets = s
				.getIncomingTransitionListIterator();
		while (targets.hasNext())
		{
			FSATransition t = targets.next();
			targets.remove();
			t.getSource().removeOutgoingTransition(t);
			t.getTarget().removeIncomingTransition(t);
		}
		states.remove(s);
		fireFSAStructureChanged(new FSAMessage(
				FSAMessage.REMOVE,
				FSAMessage.STATE,
				s.getId(),
				this));
	}

	/*
	 * (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#getStateIterator()
	 */
	public ListIterator<FSAState> getStateIterator()
	{
		return new StateIterator(states.listIterator(), this);
	}

	/*
	 * (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#getState(int)
	 */
	public FSAState getState(long id)
	{
		ListIterator<FSAState> si = states.listIterator();
		while (si.hasNext())
		{
			FSAState s = si.next();
			if (s.getId() == id)
			{
				return s;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#add(model.fsa.FSATransition)
	 */
	public void add(FSATransition t)
	{
		t.getSource().addOutgoingTransition(t);
		t.getTarget().addIncomingTransition(t);
		transitions.add(t);
		maxTransitionId = maxTransitionId < t.getId() ? t.getId()
				: maxTransitionId;
		fireFSAStructureChanged(new FSAMessage(
				FSAMessage.ADD,
				FSAMessage.TRANSITION,
				t.getId(),
				this));
		this.metadataChanged();
	}

	/*
	 * (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#remove(model.fsa.FSATransition)
	 */
	public void remove(FSATransition t)
	{
		t.getSource().removeOutgoingTransition(t);
		t.getTarget().removeIncomingTransition(t);
		transitions.remove(t);
		fireFSAStructureChanged(new FSAMessage(
				FSAMessage.REMOVE,
				FSAMessage.TRANSITION,
				t.getId(),
				this));
	}

	/*
	 * (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#getTransition(int)
	 */
	public FSATransition getTransition(long id)
	{
		ListIterator<FSATransition> tli = transitions.listIterator();
		while (tli.hasNext())
		{
			FSATransition t = tli.next();
			if (t.getId() == id)
			{
				return t;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#getTransitionIterator()
	 */
	public ListIterator<FSATransition> getTransitionIterator()
	{
		return new TransitionIterator(transitions.listIterator());
	}

	/*
	 * (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#add(model.fsa.FSAEvent)
	 */
	public void add(FSAEvent e)
	{
		events.add(e);
		maxEventId = maxEventId < e.getId() ? e.getId() : maxEventId;
		fireFSAEventSetChanged(new FSAMessage(
				FSAMessage.ADD,
				FSAMessage.EVENT,
				e.getId(),
				this));
		this.metadataChanged();
	}

	/**
	 * Removes <code>event</code> and all transitions fired by it.
	 * 
	 * @param event
	 *            the event to be removed
	 */
	public void remove(FSAEvent event)
	{

		Iterator<FSATransition> trans = getTransitionIterator();
		ArrayList<FSATransition> toRemove = new ArrayList<FSATransition>();

		while (trans.hasNext())
		{
			FSATransition t = trans.next();
			FSAEvent e = t.getEvent();
			if (event.equals(e))
			{
				toRemove.add(t);
			}
		}

		for (FSATransition t : toRemove)
		{
			transitions.remove(t);
			fireFSAStructureChanged(new FSAMessage(
					FSAMessage.REMOVE,
					FSAMessage.TRANSITION,
					t.getId(),
					this));
		}

		events.remove(event);

		fireFSAEventSetChanged(new FSAMessage(
				FSAMessage.REMOVE,
				FSAMessage.EVENT,
				event.getId(),
				this));
	}

	/*
	 * (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#getEventIterator()
	 */
	public ListIterator<FSAEvent> getEventIterator()
	{
		return new EventIterator(events.listIterator(), this);
	}

	/**
	 * TODO Comment!
	 */
	public FSAEventSet getEventSet()
	{
		return new StupidSetWrapper(events);
	}

	/*
	 * (non-Javadoc)
	 * @see model.fsa.ver2_1.FSAModel#getEvent(int)
	 */
	public FSAEvent getEvent(long id)
	{
		ListIterator<FSAEvent> ei = events.listIterator();
		while (ei.hasNext())
		{
			FSAEvent e = ei.next();
			if (e.getId() == id)
			{
				return e;
			}
		}
		return null;
	}

	/**
	 * A custom list iterator for states. Preserves data integrity.
	 * 
	 * @author agmi02
	 */
	private class StateIterator implements ListIterator<FSAState>
	{

		private ListIterator<FSAState> sli;

		private FSAState current;

		private FSAModel a;

		/**
		 * constructs a stateIterator
		 * 
		 * @param sli
		 *            the listiterator this listiterator shall encapsulate
		 * @param a
		 *            the automaton that is backing the listiterator
		 */
		public StateIterator(ListIterator<FSAState> sli, FSAModel a)
		{
			this.a = a;
			this.sli = sli;
		}

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			return sli.hasNext();
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		public FSAState next()
		{
			current = sli.next();
			return current;
		}

		/**
		 * @see java.util.ListIterator#hasPrevious()
		 */
		public boolean hasPrevious()
		{
			return sli.hasPrevious();
		}

		/**
		 * @see java.util.ListIterator#previous()
		 */
		public FSAState previous()
		{
			current = sli.previous();
			return current;
		}

		/**
		 * @see java.util.ListIterator#nextIndex()
		 */
		public int nextIndex()
		{
			return sli.nextIndex();
		}

		/**
		 * @see java.util.ListIterator#previousIndex()
		 */
		public int previousIndex()
		{
			return sli.previousIndex();
		}

		/**
		 * Removes the element last returned by either next or previous. Removes
		 * the transitions originating from this state and leading to this state
		 * in order to maintain data integrity.
		 */
		public void remove()
		{
			ListIterator<FSATransition> sources = current
					.getOutgoingTransitionsListIterator();
			while (sources.hasNext())
			{
				FSATransition t = sources.next();
				sources.remove();
				a.remove(t);
				t.getSource().removeOutgoingTransition(t);
				t.getTarget().removeIncomingTransition(t);
			}

			ListIterator<FSATransition> targets = current
					.getIncomingTransitionListIterator();
			while (targets.hasNext())
			{
				FSATransition t = targets.next();
				targets.remove();
				a.remove(t);
				t.getSource().removeOutgoingTransition(t);
				t.getTarget().removeIncomingTransition(t);
			}
			sli.remove();
		}

		public void set(FSAState s)
		{
			ListIterator<FSATransition> sources = current
					.getOutgoingTransitionsListIterator();
			while (sources.hasNext())
			{
				FSATransition t = sources.next();
				t.setSource(s);
				s.addOutgoingTransition(t);
			}

			ListIterator<FSATransition> targets = current
					.getIncomingTransitionListIterator();
			while (targets.hasNext())
			{
				FSATransition t = targets.next();
				t.setTarget(s);
				s.addIncomingTransition(t);
			}
			sli.set(s);
		}

		public void add(FSAState s)
		{
			sli.add(s);
		}
	}

	/**
	 * A custom list iterator for transitions. Conserves data integrity.
	 * 
	 * @author Axel Gottlieb Michelsen
	 * @author Kristian Edlund
	 */
	private class TransitionIterator implements ListIterator<FSATransition>
	{
		private ListIterator<FSATransition> tli;

		private FSATransition current;

		/**
		 * Constructor for the customized transition iterator
		 * 
		 * @param tli
		 *            the original transition iterator that is going to be
		 *            wrapped
		 */
		public TransitionIterator(ListIterator<FSATransition> tli)
		{
			this.tli = tli;
		}

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			return tli.hasNext();
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		public FSATransition next()
		{
			current = tli.next();
			return current;
		}

		/**
		 * @see java.util.ListIterator#hasPrevious()
		 */
		public boolean hasPrevious()
		{
			return tli.hasPrevious();
		}

		/**
		 * @see java.util.ListIterator#previous()
		 */
		public FSATransition previous()
		{
			current = tli.previous();
			return current;
		}

		/**
		 * @see java.util.ListIterator#nextIndex()
		 */
		public int nextIndex()
		{
			return tli.nextIndex();
		}

		/**
		 * @see java.util.ListIterator#previousIndex()
		 */
		public int previousIndex()
		{
			return tli.previousIndex();
		}

		/**
		 * Removes the transition from the states it is attached to and then
		 * removes it from the transition list
		 * 
		 * @see java.util.Iterator#remove()
		 */
		public void remove()
		{
			current.getTarget().removeIncomingTransition(current);
			current.getSource().removeOutgoingTransition(current);
			tli.remove();
		}

		/**
		 * Removes the last returned transition and replaces it with the new one
		 * 
		 * @param t
		 *            the transition to replace the old one
		 */
		public void set(FSATransition t)
		{
			remove();
			add(t);
		}

		/**
		 * Adds a new transition to the correct states and to the list of
		 * transitions
		 * 
		 * @param t
		 *            the transition to be added
		 */
		public void add(FSATransition t)
		{
			t.getSource().addOutgoingTransition(t);
			t.getTarget().addIncomingTransition(t);
			transitions.add(t);
		}
	}

	/**
	 * A custom list iterator for events. Conserves data integrity.
	 * 
	 * @author Axel Gottlieb Michelsen
	 * @author Kristian Edlund
	 */
	private class EventIterator implements ListIterator<FSAEvent>
	{
		private FSAEvent current;

		private ListIterator<FSAEvent> eli;

		private FSAModel a;

		/**
		 * The constructor for the event iterator
		 * 
		 * @param eli
		 *            the event iterator to be wrapped
		 * @param a
		 *            the automaton the event list is connected to
		 */
		public EventIterator(ListIterator<FSAEvent> eli, FSAModel a)
		{
			this.eli = eli;
			this.a = a;
		}

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			return eli.hasNext();
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		public FSAEvent next()
		{
			current = eli.next();
			return current;
		}

		/**
		 * @see java.util.ListIterator#hasPrevious()
		 */
		public boolean hasPrevious()
		{
			return eli.hasPrevious();
		}

		/**
		 * @see java.util.ListIterator#previous()
		 */
		public FSAEvent previous()
		{
			current = eli.previous();
			return current;
		}

		/**
		 * @see java.util.ListIterator#nextIndex()
		 */
		public int nextIndex()
		{
			return eli.nextIndex();
		}

		/**
		 * @see java.util.ListIterator#previousIndex()
		 */
		public int previousIndex()
		{
			return eli.previousIndex();
		}

		/**
		 * removes the current event from all the transitions it was involved in
		 * 
		 * @see java.util.Iterator#remove()
		 */
		public void remove()
		{
			ListIterator<FSATransition> tli = a.getTransitionIterator();
			while (tli.hasNext())
			{
				FSATransition t = tli.next();
				if (t.getEvent() == current)
				{
					t.setEvent(null);
				}
			}
			eli.remove();
		}

		/**
		 * Replaces the current event with the given event
		 * 
		 * @param e
		 *            the event to replace the current event
		 */
		public void set(FSAEvent e)
		{
			ListIterator<FSATransition> tli = a.getTransitionIterator();
			while (tli.hasNext())
			{
				FSATransition t = tli.next();
				if (t.getEvent() == current)
				{
					t.setEvent(e);
				}
			}
			eli.set(e);
		}

		/**
		 * Adds an event to the event list
		 * 
		 * @param e
		 *            the event to add
		 */
		public void add(FSAEvent e)
		{
			eli.add(e);
		}
	}

	// /**
	// * Set the file for this automaton.
	// * @param f the file
	// */
	// public void setFile(File f)
	// {
	// myFile=f;
	// }

	// /**
	// * Get this automaton's file.
	// * @return
	// */
	// public File getFile()
	// {
	// return myFile;
	// }

	// /**
	// * Gets the list of ids of the automata of which this automaton is a
	// composition.
	// * @return the list of ids of the automata of which this automaton is a
	// composition
	// */
	// public String[] getAutomataCompositionList()
	// {
	// return composedOf;
	// }

	// /**
	// * Sets the list of ids of the automata of which this automaton is a
	// composition.
	// * @param list the list of ids of the automata of which this automaton is
	// a composition
	// */
	// public void setAutomataCompositionList(String[] list)
	// {
	// composedOf=list;
	// }

	// public SubElement getMeta()
	// {
	// // return meta;
	// return null
	// }

	// public void setMeta(SubElement m)
	// {
	// meta=m;
	// }

	public Object getAnnotation(String key)
	{
		return annotations.get(key);
	}

	public void setAnnotation(String key, Object annotation)
	{
		if (annotation != null)
		{
			annotations.put(key, annotation);
		}
	}

	public void removeAnnotation(String key)
	{
		annotations.remove(key);
	}

	public boolean hasAnnotation(String key)
	{
		return annotations.containsKey(key);
	}

	/**
	 * Returns the events disabled at a given state. If the control map is
	 * undefined (e.g., not computed yet or out of synch with the rest of the
	 * models), this method will return <code>null</code>.
	 * 
	 * @param state
	 *            state of the supervisor
	 * @return the events disabled at a given state; or <code>null</code> if the
	 *         control map is undefined
	 */
	public FSAEventSet getDisabledEvents(FSAState state)
	{
		if (!states.contains(state))
		{
			return null;
		}
		else
		{
			return (FSAEventSet)state.getAnnotation(Annotable.CONTROL_MAP);
		}
	}

	/**
	 * Sets the events disabled at a given state.
	 * 
	 * @param state
	 *            state of the supervisor
	 * @param set
	 *            set of disabled events for this state
	 */
	public void setDisabledEvents(FSAState state, FSAEventSet set)
	{
		if (!states.contains(state))
		{
			return;
		}
		else
		{
			state.setAnnotation(Annotable.CONTROL_MAP, set);
		}
	}

}
