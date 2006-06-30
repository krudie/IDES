package main;

import io.ParsingToolbox;
import io.PrintUtilities;
import io.fsa.ver1.FileOperations;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import org.apache.commons.codec.digest.DigestUtils;

import presentation.fsa.GraphModel;
import presentation.fsa.GraphView;
import services.latex.LatexManager;
import services.latex.LatexPrerenderer;
import ui.MainWindow;
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
	private File myFile = null;
	
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
		if(LatexManager.isLatexEnabled())
		{
			new LatexPrerenderer(getActiveGraphModel());
		}
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
		if(getActiveModel()!=null)
		{
			((Automaton)getActiveModel()).detach(getDrawingBoard());
		}
		this.activeModelName = name;
		((Automaton)getActiveModel()).attach(getDrawingBoard());
		unsaved = true;
	}
	
	/**
	 * 
	 * @return an iterator of all graph models in this workspace
	 */
	public Iterator<GraphModel> getGraphModels(){
		ArrayList<GraphModel> g = new ArrayList<GraphModel>();
		Iterator<GraphModel> iter = graphs.values().iterator();
		while(iter.hasNext()){
			g.add(iter.next());
		}
		return g.iterator();
	}
	            
	/**
	 * 
	 * @return an iterator of all automata in this workspace
	 */
    public Iterator<FSAModel> getAutomata() {
    	ArrayList<FSAModel> g = new ArrayList<FSAModel>();
		Iterator<Automaton> iter = systems.values().iterator();
		while(iter.hasNext()){
			g.add(iter.next());
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
	
	/**
	 * Replaces the current workspace with the workspace given in the descriptor. 
	 * @param wd the descriptor of the replacement workspace
	 */
	public void replaceWorkspace(WorkspaceDescriptor wd)
	{
		if(hasUnsavedData())
		{
			//TODO call save
		}
		
		Iterator<FSAModel> iter=getAutomata();
		while(iter.hasNext())
		{
			removeFSAModel(iter.next().getName());
			iter=getAutomata();
		}
		
		myFile=wd.getFile();
		name=myFile.getName();
		Hub.getMainWindow().setTitle(Hub.string("IDES_SHORT_NAME")+" "+
				Hub.string("IDES_VER")+": "+name);
		Vector<String> files=wd.getModels();
		int idx=wd.getSelectedModel();
		String selectedModel=null;
		for(int i=0;i<files.size();++i)
		{
			Automaton fsa = (Automaton)FileOperations.openAutomaton(
					new java.io.File(files.elementAt(i)));
			if(fsa != null)
			{
				Hub.getWorkspace().addFSAModel(fsa);
				if(i==idx)
					selectedModel=fsa.getName();
			}
		}
		if(selectedModel!=null)
		{
			setActiveModel(selectedModel);
			notifyAllSubscribers();
		}
	}

	/**
	 * Returns a descriptor of the current workspace.
	 * @return descriptor of the current workspace
	 * @throws IncompleteWorkspaceDescriptorException when the descriptor can't be created due to unsaved models
	 */
	public WorkspaceDescriptor getDescriptor() throws IncompleteWorkspaceDescriptorException
	{
		WorkspaceDescriptor wd=new WorkspaceDescriptor(myFile);
		int counter=0;
		for(Iterator i=getAutomata();i.hasNext();++counter)
		{
			Automaton a=(Automaton)i.next();
			if(a.getFile()==null)
			{
				FileOperations.saveAutomatonAs(a);
				if(a.getFile()==null)
					throw new IncompleteWorkspaceDescriptorException();
			}
			wd.insertModel(a.getFile().getName(),counter);
			if(a.getName().equals(getActiveModelName()))
				wd.setSelectedModel(counter);
		}
		return wd;
	}
	
	/**
	 * Sets the file for the current workspace. This file will be
	 * used when creating descriptors of the workspace.
	 * @param f the new file for the workspace
	 * @see #getDescriptor()
	 */
	public void setFile(File f)
	{
		myFile=f;
	}
	
	public String getRandomId()
	{
		String data=new Double(Math.random()).toString()+
			new Long(System.currentTimeMillis()).toString()+
			systems.size()+System.getProperty("user.name");
		for(Iterator<FSAModel> i=getAutomata();i.hasNext();)
		{
			FSAModel a=i.next();
			data+=a.getEventCount();
			data+=a.getStateCount();
			data+=a.getTransitionCount();
		}
		return DigestUtils.md5Hex(data);
	}
	
	/**
	 * TODO: fix this
	 * @return the top-left corner of the drawing area
	 */
	public Point getDrawingBoardDisplacement()
	{
		return ((MainWindow)Hub.getMainWindow()).getDrawingBoardDisplacement();
	}

	/**
	 * TODO: fix this
	 * @return the background color of the drawing area
	 */
	public Color getDrawingBoardBGColor()
	{
		return ((MainWindow)Hub.getMainWindow()).getDrawingBoardBGColor();
	}
	
	//TODO this should return an interface, not GDV
	protected presentation.fsa.GraphDrawingView getDrawingBoard()
	{
		return ((MainWindow)Hub.getMainWindow()).getDrawingBoard();
	}
}
