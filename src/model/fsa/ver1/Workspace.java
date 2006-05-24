package model.fsa.ver1;

import java.util.HashMap;

import model.fsa.FSAEventsModel;
import model.fsa.FSAModel;
import model.fsa.FSAWorkspace;

public class Workspace implements FSAWorkspace {

	FSAEventsModel eventsModel;
	HashMap<String, FSAModel> systems;
	
	public FSAModel getFSAModel(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addFSAModel(FSAModel fsa) {
		// TODO Auto-generated method stub

	}

	public void removeFSAModel(String name) {
		// TODO Auto-generated method stub

	}

	public FSAEventsModel getEventsModel() {
		// TODO Auto-generated method stub
		return null;
	}

}
