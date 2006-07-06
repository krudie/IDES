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

import org.apache.commons.codec.digest.DigestUtils;

import presentation.fsa.GraphModel;
import services.latex.LatexManager;
import services.latex.LatexPrerenderer;
import ui.MainWindow;

import model.Publisher;
import model.fsa.FSAEventsModel;
import model.fsa.FSAModel;
import model.fsa.ver1.Automaton;
import model.fsa.ver1.EventsModel;
import model.fsa.ver1.MetaData;

public class IDESWorkspace extends Publisher implements Workspace {

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
	private Vector<GraphModel> graphs;
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
		graphs=new Vector<GraphModel>();
		metadata=new Vector<MetaData>();
		name=Hub.string("newAutomatonName");
//		eventsModel = new EventsModel();
	}
	
	public void addFSAModel(FSAModel fsa) {
		if(countAdd==1&&getActiveGraphModel()!=null&&!getActiveGraphModel().isDirty())
			removeFSAModel(getActiveGraphModel().getName());
		if(getActiveModel()!=null)
			getActiveGraphModel().detach(getDrawingBoard());
		systems.add((Automaton) fsa);
		metadata.add(new MetaData((Automaton)fsa));
		graphs.add(new GraphModel((Automaton)fsa, metadata.lastElement()));
		activeModelIdx=systems.size()-1;
		//eventsModel.addLocalEvents(fsa);
		if(LatexManager.isLatexEnabled())
		{
			new LatexPrerenderer(getActiveGraphModel());
		}
		getActiveGraphModel().attach(getDrawingBoard());
		notifyAllSubscribers();
		graphs.elementAt(activeModelIdx).notifyAllSubscribers();
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

	public GraphModel getGraphModel(String name) {	
		int idx=getFSAIndex(name);
		if(idx<0)
			return null;
		return graphs.elementAt(idx);
	}

	public boolean hasFSAModel(String name) {
		return getFSAModel(name) != null;
	}
	
	public void removeFSAModel(String name) {
		GraphModel gm=getGraphModel(name);
		if(gm==null)
			return;
		if(gm.isDirty())
			if(!CommonTasks.handleUnsavedModel(gm))
				return;
		if(getActiveModel()!=null)
		{
			((Automaton)getActiveModel()).detach(getDrawingBoard());
		}
		int idx=getFSAIndex(name);
		systems.removeElementAt(idx);	
		metadata.removeElementAt(idx);
		graphs.removeElementAt(idx);
		if(activeModelIdx>=systems.size())
				activeModelIdx--;
		if(getActiveModel()!=null)
			((Automaton)getActiveModel()).attach(getDrawingBoard());
		this.notifyAllSubscribers();
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
	
	public void setActiveModel(String name) {
		if(getActiveModel()!=null)
			getActiveGraphModel().detach(getDrawingBoard());
		activeModelIdx=getFSAIndex(name);
		if(getActiveModel()!=null)
			getActiveGraphModel().attach(getDrawingBoard());
	}
	
	/**
	 * 
	 * @return an iterator of all graph models in this workspace
	 */
	public Iterator<GraphModel> getGraphModels(){
		ArrayList<GraphModel> g = new ArrayList<GraphModel>();
		Iterator<GraphModel> iter = graphs.iterator();
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
	
    /**
     * @see projectPresentation.ProjectPresentation#hasUnsavedData()
     */
    public boolean isDirty(){
        return dirty;
    }

    /**
     * @see projectPresentation.ProjectPresentation#setUnsavedData(boolean)
     */
    public void setDirty(boolean state){
        dirty = state;
    }

	public String getName() {		
		return name;
	}

	public boolean isEmpty() {
		return systems.isEmpty();
	}

	public GraphModel getActiveGraphModel() {	
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
			notifyAllSubscribers();
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
				getGraphModel(a.getName()).setDirty(false);
				getGraphModel(a.getName()).notifyAllSubscribers();
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
