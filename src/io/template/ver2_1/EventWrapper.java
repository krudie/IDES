package io.template.ver2_1;

import model.fsa.FSAEvent;
import des.interfaces.Event;

public class EventWrapper implements Event {

	protected FSAEvent event;
	
	public EventWrapper(FSAEvent event)
	{
		this.event=event;
	}
	
	public String getSymbol() {
		String symbol=event.getSymbol();
		String ret="";
		for(int i=0;i<symbol.length();++i)
		{
			if(Character.isLetterOrDigit(symbol.charAt(i)))
				ret+=""+symbol.charAt(i);
		}
		return ret;
	}

	public boolean isControllable() {
		return event.isControllable();
	}

	public void setControllable(boolean arg0) {
		event.setControllable(arg0);
	}

	public void setSymbol(String arg0) {
		event.setSymbol(arg0);
	}

	/**
	 * TODO Also verify controllable property consistence
	 * Returns true iff <code>o</code> is of type Event
	 * and has the same SYMBOL as this Event. 
	 * 
	 * @param o another object
	 * @return true iff <code>o</code> is of type Event
	 * 	and has the same SYMBOL as this Event.
	 */
	public boolean equals(Object o)
	{
		if(!(o instanceof Event))
			return false;
		return ((Event)o).getSymbol().equals(getSymbol());
	}
	
	public int hashCode()
	{
		return getSymbol().hashCode();
	}
}
