package main;

import io.fsa.ver1.CommonTasks;
import io.fsa.ver1.FileOperations;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;

import observer.Publisher;
import observer.WorkspaceMessage;
import observer.WorkspacePublisher;

import org.apache.commons.codec.digest.DigestUtils;

import presentation.fsa.FSMGraph;
import services.latex.LatexManager;
import services.latex.LatexPrerenderer;
import ui.MainWindow;

import model.fsa.FSAEventsModel;
import model.fsa.FSAModel;
import model.fsa.ver1.Automaton;
import model.fsa.ver1.EventsModel;
import model.fsa.ver1.MetaData;

public class IDESWorkspace extends WorkspacePublisher implements Workspace {

	//needed for special handling of first (automatic) add and first (user-initated) add
	private long countAdd=0;
	private boolean dirty=false; // dirty bit
	private String name;
	private File myFile = null;
	
	// index of the currently active FSAModel
	private int activeModelIdx;
		
	// A model of global events set (alphabet) and all local alphabets
//	private FSAEventsModel eventsModel;
	
	// maps name of each model to the abstract FSA model, 
	// graph representation and metadata respectively.
	private Vector<Automaton> systems;
	private Vector<FSMGraph> graphs;
	private Vector<MetaData> metadata;

	static IDESWorkspace me;
	
	public static IDESWorkspace instance(){
		if(me == null){
			me = new IDESWorkspace();
		}
		return me;
	}
	
	protected IDESWorkspace(){
		activeModelIdx=-1;
		systems=new Vector<Automaton>();
		graphs=new Vector<FSMGraph>();
		metadata=new Vector<MetaData>();
		name=Hub.string("newAutomatonName");
//		eventsModel = new EventsModel();
	}
	
	public void addFSAModel(FSAModel fsa) {
		
		// Remove Untitled model if it has not been modified
		if(countAdd==1&&getActiveGraphModel()!=null&&!getActiveGraphModel().isDirty())
			removeFSAModel(getActiveGraphModel().getName());
		
//		if(getActiveModel()!=null)
//			getActiveGraphModel().removeSubscriber(getDrawingBoard());
		
		systems.add((Automaton) fsa);
		metadata.add(new MetaData((Automaton)fsa));
		graphs.add(new FSMGraph((Automaton)fsa, metadata.lastElement()));
		activeModelIdx=systems.size()-1;
		
		//eventsModel.addLocalEvents(fsa);
		
		if(LatexManager.isLatexEnabled())
		{
			new LatexPrerenderer(getActiveGraphModel());
		}
		
//		getActiveGraphModel().addSubscriber(getDrawingBoard());
//		graphs.elementAt(activeModelIdx).notifyAllSubscribers();
		//notifyAllSubscribers();
		fireModelCollectionChanged(new WorkspaceMessage(WorkspaceMessage.FSM, 
									fsa.getId(), 
									WorkspaceMessage.ADD, 
									this));		
		if(countAdd!=0)
			dirty = true;
		countAdd++;
	}

	protected int getFSAIndex(String name)
	{
		for(int i=0;i<systems.size();++i)
			if(systems.elementAt(i).getName().equals(name))
				return i;
		return -1;
	}
	
	public FSAModel getFSAModel(String name) {
		int idx=getFSAIndex(name);
		if(idx<0)
			return null;
		return systems.elementAt(idx);
	}

	public FSMGraph getGraphModel(String name) {	
		int idx=getFSAIndex(name);
		if(idx<0)
			return null;
		return graphs.elementAt(idx);
	}

	public boolean hasFSAModel(String name) {
		return getFSAModel(name) != null;
	}
	
	public void removeFSAModel(String name) {
		FSMGraph gm=getGraphModel(name);
		if(gm==null)
			return;
		if(gm.isDirty())
			if(!CommonTasks.handleUnsavedModel(gm))
				return;
		
		// Assumes that the current model is the same as the one named.
//		if(getActiveModel()!=null)
//		{
//			((Automaton)getActiveModel()).removeSubscriber(getDrawingBoard());
//		}
		int idx=getFSAIndex(name);
		Automaton fsa = systems.get(idx);
		systems.removeElementAt(idx);	
		metadata.removeElementAt(idx);
		graphs.removeElementAt(idx);
		if(activeModelIdx>=systems.size()){
			activeModelIdx--;
//			 TODO change name to fsa.id for consistency with add and remove
			fireModelSwitched(new WorkspaceMessage(WorkspaceMessage.FSM, 
								name, 
								WorkspaceMessage.REMOVE,
								this));
		}		
		
//		if(getActiveModel()!=null)
//			((Automaton)getActiveModel()).addSubscriber(getDrawingBoard());
		
		fireModelCollectionChanged(new WorkspaceMessage(WorkspaceMessage.FSM, 
				fsa.getId(), 
				WorkspaceMessage.REMOVE, 
				this));
		
		//this.notifyAllSubscribers();
		dirty = true;
	}

	public FSAEventsModel getEventsModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getActiveModelName() {
		if(activeModelIdx<0)
			return "";
		return systems.elementAt(activeModelIdx).getName();
	}

	public FSAModel getActiveModel(){
		if(activeModelIdx<0)
			return null;
		return systems.elementAt(activeModelIdx);
	}
	
	/**
	 * Sets the active model to the FSAModel with the given name. 
	 * FIXME Should be using unique ID. 
	 * 	 
	 * @param name
	 */
	public void setActiveModel(String name) {
		if(getActiveModel()!=null)
			getActiveGraphModel().removeSubscriber(getDrawingBoard());
		activeModelIdx=getFSAIndex(name);
		if(getActiveModel()!=null)
			getActiveGraphModel().addSubscriber(getDrawingBoard());
		
		// TODO change name to fsa.id for consistency with add and remove
		fireModelSwitched(new WorkspaceMessage(WorkspaceMessage.FSM, 
							name, 
							WorkspaceMessage.MODIFY,
							this));		
	}
	
	/**
	 * 
	 * @return an iterator of all graph models in this workspace
	 */
	public Iterator<FSMGraph> getGraphModels(){
		ArrayList<FSMGraph> g = new ArrayList<FSMGraph>();
		Iterator<FSMGraph> iter = graphs.iterator();
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
		Iterator<Automaton> iter = systems.iterator();
		while(iter.hasNext()){
			g.add(iter.next());
		}
		return g.iterator();
    }
	    
    public boolean isDirty(){
        return dirty;
    }
    
    public void setDirty(boolean state){
        dirty = state;
    }

	public String getName() {		
		return name;
	}

	public boolean isEmpty() {
		return systems.isEmpty();
	}

	public FSMGraph getActiveGraphModel() {	
		if(activeModelIdx<0)
			return null;
		return graphs.elementAt(activeModelIdx);
	}
	
	/**
	 * Replaces the current workspace with the workspace given in the descriptor. 
	 * @param wd the descriptor of the replacement workspace
	 */
	public void replaceWorkspace(WorkspaceDescriptor wd)
	{
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
			// Hey LENKO! what is the nature of this change?  Everything appears to have changed...
			fireModelCollectionChanged(new WorkspaceMessage(WorkspaceMessage.FSM, 
									"everything changed?", 
									WorkspaceMessage.MODIFY, 
									this));
			//notifyAllSubscribers();
		}
		setDirty(false);
	}

	/**
	 * Returns a descriptor of the current workspace.
	 * @return descriptor of the current workspace
	 * @throws IncompleteWorkspaceDescriptorException when the descriptor can't be created due to unsaved models
	 */
	public WorkspaceDescriptor getDescriptor() throws IncompleteWorkspaceDescriptorException
	{
		WorkspaceDescriptor wd=new WorkspaceDescriptor(myFile);
		HashSet<Automaton> unsavedModels=new HashSet<Automaton>();
		for(Iterator i=getAutomata();i.hasNext();)
		{
			Automaton a=(Automaton)i.next();
			if(a.getFile()==null)
				unsavedModels.add(a);
		}
		if(!unsavedModels.isEmpty())
		{
			Hub.displayAlert(Hub.string("firstSaveUnsaved"));
			for(Automaton a:unsavedModels)
			{
				if(!FileOperations.saveAutomatonAs(a))
					throw new IncompleteWorkspaceDescriptorException();
//				getGraphModel(a.getName()).setDirty(false);
//				getGraphModel(a.getName()).notifyAllSubscribers();
			}
		}
		for(int counter=0; counter<systems.size(); ++counter)
		{
			Automaton a=(Automaton)systems.elementAt(counter);
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
	
	/**
	 * Get the number of models in the workspace
	 * @return number of models in the workspace
	 */
	public int size()
	{
		return systems.size();
	}
}
