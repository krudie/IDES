package model.fsa.ver1;

import java.util.List;

import model.fsa.FSAEvent;
import model.fsa.FSAEventsModel;
import model.fsa.FSAModel;

/**
 * A model of global events set (alphabet) and all local alphabets
 * 
 * @author helen bretzke
 *
 */
public class EventsModel extends model.Publisher implements FSAEventsModel {

	
	
	public List getGlobalEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	public List getLocalEvents(FSAModel fsa) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addGlobalEvent(FSAEvent e) {
		// TODO Auto-generated method stub

	}

	public void addLocalEvents(FSAModel fsa) {
		// TODO Auto-generated method stub

	}

	public void pruneGlobalEvents() {
		// TODO Auto-generated method stub

	}

}