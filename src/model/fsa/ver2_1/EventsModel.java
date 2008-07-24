package model.fsa.ver2_1;

import ides.api.model.fsa.FSAEvent;
import ides.api.model.fsa.FSAModel;

import java.util.Set;

/**
 * A model of global events set (alphabet) and all local alphabets. TODO Design
 * and implement a publisher interface for this class to notify subscribers.
 * 
 * @author Helen Bretzke
 */
public class EventsModel implements FSAEventsModel
{

	public Set<FSAEvent> getGlobalEvents()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Set<FSAEvent> getLocalEvents(FSAModel fsa)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void addGlobalEvent(FSAEvent e)
	{
		// TODO Auto-generated method stub

	}

	public void addLocalEvents(FSAModel fsa)
	{
		// TODO Auto-generated method stub

	}

	public void pruneGlobalEvents()
	{
		// TODO Auto-generated method stub

	}

}
