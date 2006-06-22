package main;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import presentation.fsa.GraphModel;
import presentation.fsa.GraphView;
import util.InterruptableProgressDialog;


import model.Publisher;
import model.fsa.FSAEventsModel;
import model.fsa.FSAMetaData;
import model.fsa.FSAModel;
import model.fsa.ver1.Automaton;
import model.fsa.ver1.EventsModel;
import model.fsa.ver1.MetaData;

public class IDESWorkspace extends Publisher implements Workspace {

	private boolean unsaved; // dirty bit
	private String name = "New Project";
	
	// Unique name of the currently active FSAModel
	private String activeModelName;
		
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
	
	protected IDESWorkspace(){
		systems = new HashMap<String, Automaton>();
		graphs = new HashMap<String, GraphModel>();
		metadata = new HashMap<String, MetaData>();
		eventsModel = new EventsModel();
	}
	
	
	public void addFSAModel(FSAModel fsa) {
		activeModelName = fsa.getName();
		systems.put(activeModelName, (Automaton) fsa);
		metadata.put(activeModelName, new MetaData((Automaton)fsa));
		graphs.put(activeModelName, new GraphModel((Automaton)fsa, metadata.get(activeModelName)));
		eventsModel.addLocalEvents(fsa);		
		notifyAllSubscribers();
		unsaved = true;
	}

	public FSAModel getFSAModel(String name) {	
		return systems.get(name);
	}

	public boolean hasFSAModel(String name) {
		return getFSAModel(name) != null;
	}
	
	public void removeFSAModel(String name) {		
		systems.remove(name);	
		metadata.remove(name);
		graphs.remove(name);
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

	public GraphModel getActiveGraphModel() {		
		return graphs.get(activeModelName);
	}
}
