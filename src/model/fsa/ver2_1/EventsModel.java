package model.fsa.ver2_1;

import java.util.Set;

import model.fsa.FSAEvent;
import model.fsa.FSAEventsModel;
import model.fsa.FSAModel;

/**
 * A model of global events set (alphabet) and all local alphabets. TODO Design
 * and implement a publisher interface for this class to notify subscribers.
 * 
 * @author Helen Bretzke
 */
public class EventsModel implements FSAEventsModel
{

	public Set getGlobalEvents()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Set getLocalEvents(FSAModel fsa)
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
