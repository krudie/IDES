package io.template.ver2_1;

import model.fsa.FSATransition;
import des.interfaces.Event;
import des.interfaces.State;
import des.interfaces.Transition;

public class TransitionWrapper implements Transition {
	
	protected State source;
	protected State target;
	protected Event event;
	
	public TransitionWrapper(State source, Event event, State target)
	{
		this.source=source;
		this.event=event;
		this.target=target;
	}

	public Event getEvent() {
		return event;
	}

	public State getSource() {
		return source;
	}

	public State getTarget() {
		return target;
	}

	public void setEvent(Event arg0) {
		this.event=arg0;
	}

	public void setSource(State arg0) {
		source=arg0;
	}

	public void setTarget(State arg0) {
		target=arg0;
	}
}
