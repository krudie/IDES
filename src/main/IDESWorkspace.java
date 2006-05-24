package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import ui.GraphModel;
import ui.GraphView;

import model.Publisher;
import model.fsa.FSAEventsModel;
import model.fsa.FSAMetaData;
import model.fsa.FSAModel;
import model.fsa.ver1.Automaton;
import model.fsa.ver1.MetaData;

public class IDESWorkspace extends Publisher implements Workspace {

	// Unique name of the currently active FSAModel
	String activeModelName;
	
	// ??? Do I even need to store this?  is this not simply the component with the current UI focus?
	Object activeView;
	
	// A model of global events set (alphabet) and all local alphabets
	FSAEventsModel eventsModel;
	
	// maps name of each model to the abstract FSA model, 
	// graph representation and metadata respectively.
	HashMap<String, Automaton> systems;
	HashMap<String, GraphModel> graphs;
	HashMap<String, MetaData> metadata;
	
	public void addFSAModel(FSAModel fsa) {
		systems.put(fsa.getName(), (Automaton) fsa);
		metadata.put(fsa.getName(), new MetaData((Automaton)fsa));
		graphs.put(fsa.getName(), new GraphModel((Automaton)fsa, metadata.get(fsa.getName())));
		eventsModel.addLocalEvents(fsa);
		this.notifyAllSubscribers();
	}

	public FSAModel getFSAModel(String name) {	
		return systems.get(name);
	}

	public void removeFSAModel(String name) {
		// TODO Auto-generated method stub
		this.notifyAllSubscribers();
	}

	public FSAEventsModel getEventsModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getActiveModelName() {
		return activeModelName;
	}

	public FSAModel getActiveModel(){
		return systems.get(activeModelName);
	}
	
	public void setActiveModel(String name) {
		this.activeModelName = name;
	}
	
	public Iterator getGraphModels(){
		ArrayList g = new ArrayList();
		Iterator iter = graphs.entrySet().iterator();
		while(iter.hasNext()){
			g.add(((Entry)iter.next()).getValue());
		}
		return g.iterator();
	}
}
