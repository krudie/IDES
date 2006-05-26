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

	private boolean unsaved; // dirty bit
	private String name = "New Project";
	
	// Unique name of the currently active FSAModel
	private String activeModelName;
	
	// ??? Do I even need to store this?  
	// is this not simply the component with the current UI focus?
	private Object activeView;
	
	// A model of global events set (alphabet) and all local alphabets
	private FSAEventsModel eventsModel;
	
	// maps name of each model to the abstract FSA model, 
	// graph representation and metadata respectively.
	private HashMap<String, Automaton> systems;
	private HashMap<String, GraphModel> graphs;
	private HashMap<String, MetaData> metadata;
	
	static IDESWorkspace me;
	
	public static IDESWorkspace instance(){
		if(me == null){
			me = new IDESWorkspace();
		}
		return me;
	}
	
	private IDESWorkspace(){
		systems = new HashMap<String, Automaton>();
		graphs = new HashMap<String, GraphModel>();
		metadata = new HashMap<String, MetaData>();	
	}
	
	/**
	 * Adds the given FSAModel to the set of models in this workspace.
	 */
	public void addFSAModel(FSAModel fsa) {
		systems.put(fsa.getName(), (Automaton) fsa);
		metadata.put(fsa.getName(), new MetaData((Automaton)fsa));
		graphs.put(fsa.getName(), new GraphModel((Automaton)fsa, metadata.get(fsa.getName())));
		eventsModel.addLocalEvents(fsa);
		this.notifyAllSubscribers();
		unsaved = true;
	}

	public FSAModel getFSAModel(String name) {	
		return systems.get(name);
	}

	public void removeFSAModel(String name) {
		// TODO Auto-generated method stub
		if(systems.isEmpty()){
			activeModelName = null;
		}
		this.notifyAllSubscribers();
		unsaved = true;
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
		unsaved = true;
	}
	
	/**
	 * 
	 * @return an iterator of all graph models in this workspace
	 */
	public Iterator getGraphModels(){
		ArrayList g = new ArrayList();
		Iterator iter = graphs.entrySet().iterator();
		while(iter.hasNext()){
			g.add(((Entry)iter.next()).getValue());
		}
		return g.iterator();
	}
	            
	/**
	 * 
	 * @return an iterator of all automata in this workspace
	 */
    public Iterator getAutomata() {
    	ArrayList g = new ArrayList();
		Iterator iter = systems.entrySet().iterator();
		while(iter.hasNext()){
			g.add(((Entry)iter.next()).getValue());
		}
		return g.iterator();
    }
	
    /**
     * @see projectPresentation.ProjectPresentation#hasUnsavedData()
     */
    public boolean hasUnsavedData(){
        return unsaved;
    }

    /**
     * @see projectPresentation.ProjectPresentation#setUnsavedData(boolean)
     */
    public void setUnsavedData(boolean state){
        unsaved = state;
    }

	public String getName() {		
		return name;
	}

	public boolean isEmpty() {
		return systems.isEmpty();
	}
}
