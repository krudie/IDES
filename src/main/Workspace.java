package main;

import io.fsa.ver2_1.CommonTasks;
import io.fsa.ver2_1.FileOperations;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JTabbedPane;


import pluggable.ui.Toolset;
import pluggable.ui.UIDescriptor;
import presentation.LayoutShell;
import presentation.Presentation;
import presentation.PresentationManager;
import presentation.fsa.FSAGraph;
import services.latex.LatexManager;
import services.latex.LatexPrerenderer;
import ui.MainWindow;

import model.DESModel;
import model.ModelManager;
import model.fsa.FSAEventsModel;
import model.fsa.FSAModel;
import model.fsa.ver2_1.Automaton;
import model.fsa.ver2_1.MetaData;

/**
 * The main manager of the open DESModels.
 * 
 * @author Lenko Grigorov
 */
public class Workspace extends WorkspacePublisherAdaptor {

	//needed for special handling of first (automatic) add and first (user-initated) add
	private long countAdd=0;
	private boolean dirty=false; // dirty bit
	private String name;
	private File myFile = null;
	
	// index of the currently active FSAModel
	private int activeModelIdx;
		
	// TODO A model of global events set (alphabet) and all local alphabets
	//private FSAEventsModel eventsModel;
	
	// maps name of each model to the abstract FSA model, 
	// graph representation and metadata respectively.
	private Vector<DESModel> systems;
	private Vector<LayoutShell> graphs;
	private Vector<MetaData> metadata;
	
	static Workspace me;
	
	protected Presentation[] activePresentations=new Presentation[0];
	
	public static Workspace instance(){
		if(me == null){
			me = new Workspace();
		}
		return me;
	}
	
	protected Workspace(){
		activeModelIdx=-1;
		systems=new Vector<DESModel>();
		graphs=new Vector<LayoutShell>();
		metadata=new Vector<MetaData>();
		name=Hub.string("newAutomatonName");
//		eventsModel = new EventsModel();
	}

	public void addLayoutShell(LayoutShell g)
	{	
		// Remove initial Untitled graph if it has not been modified
		if(countAdd==1 && getActiveLayoutShell()!=null && !getActiveLayoutShell().needsSave()){
			removeModel(getActiveLayoutShell().getModel().getName());
		}
		
		if(getModel(g.getModel().getName())!=null)
		{
			int i=1;
			while(getModel(g.getModel().getName()+" ("+i+")")!=null){
				++i;
			}
			g.getModel().setName(g.getModel().getName()+" ("+i+")");
		}
		
		systems.add(g.getModel());
		
		if(g instanceof FSAGraph)
			metadata.add(((FSAGraph)g).getMeta());
		else
			metadata.add(new FSAGraph(ModelManager.createModel(FSAModel.class)).getMeta());
		graphs.add(g);
		
		if(LatexManager.isLatexEnabled())
		{
			if(getActiveLayoutShell() instanceof FSAGraph)
				new LatexPrerenderer((FSAGraph)getActiveLayoutShell());
		}

		fireModelCollectionChanged(new WorkspaceMessage(WorkspaceMessage.MODEL, 
									g.getModel().getId(), 
									WorkspaceMessage.ADD, 
									this));		
		
//		setActiveModel(systems.elementAt(systems.size()-1).getName());

		if(countAdd!=0){
			dirty = true;
		}
		countAdd++;
	}
	
	/**
	 * Adds the given DESModel to the set of models in the workspace.
	 *  @param fsa the model to be added
	 */
	public void addModel(DESModel fsa) {
		
		// Remove initial Untitled model if it is empty
		if(countAdd==1 && getActiveLayoutShell()!=null && !getActiveLayoutShell().needsSave()){
			removeModel(getActiveLayoutShell().getModel().getName());		
		}
		
		if(getModel(fsa.getName())!=null){
			int i=1;
			while(getModel(fsa.getName()+" ("+i+")")!=null){
				++i;
			}
			fsa.setName(fsa.getName()+" ("+i+")");
		}

		systems.add(fsa);
		if(fsa instanceof FSAModel)
		{
			metadata.add(new MetaData((Automaton)fsa));
			fsa.setAnnotation("metadata", metadata.lastElement());
		}
		else
		{
			metadata.add(new MetaData((Automaton)ModelManager.createModel(FSAModel.class)));
		}
		graphs.add(PresentationManager.getToolset(fsa.getModelDescriptor().getPreferredModelInterface()).wrapModel(fsa));

		if(LatexManager.isLatexEnabled()){
			if(getActiveLayoutShell() instanceof FSAGraph)
				new LatexPrerenderer((FSAGraph)getActiveLayoutShell());
		}
		
		fireModelCollectionChanged(new WorkspaceMessage(WorkspaceMessage.MODEL, 
									fsa.getId(), 
									WorkspaceMessage.ADD, 
									this));
		
//		setActiveModel(systems.elementAt(systems.size()-1).getName());
		
		if(countAdd!=0){
			dirty = true;
		}
		
		countAdd++;
	}

	protected int getModelIndex(String name)
	{
		for(int i=0;i<systems.size();++i)
			if(systems.elementAt(i).getName().equals(name))
				return i;
		return -1;
	}
	
	public DESModel getModel(String name) {
		int idx=getModelIndex(name);
		if(idx<0)
			return null;
		return systems.elementAt(idx);
	}
	
	public DESModel getModelById(String id)
	{
		for(int i=0;i<systems.size();++i)
			if(systems.elementAt(i).getId().equals(id))
				return systems.elementAt(i);
		return null;
	}

	public LayoutShell getLayoutShellById(String id)
	{
		for(int i=0;i<systems.size();++i)
			if(systems.elementAt(i).getId().equals(id))
				return graphs.elementAt(i);
		return null;
	}

	public LayoutShell getLayoutShell(String name) {	
		int idx=getModelIndex(name);
		if(idx<0)
			return null;
		return graphs.elementAt(idx);
	}

	public boolean hasModel(String name) {
		return getModel(name) != null;
	}
	
	public void removeModel(String name) {
		LayoutShell gm=getLayoutShell(name);
		
		if(gm==null){
			return;
		}
		
		if( gm.needsSave() ){
			if(!CommonTasks.handleUnsavedModel(gm)){
				return;
			}
		}
		
		// Assumes that the current model is the same as the one named.
//		if(getActiveModel()!=null)
//		{
//			((Automaton)getActiveModel()).removeSubscriber(getDrawingBoard());
//		}

		int idx=getModelIndex(name);
		if(activeModelIdx==idx){
			if(activeModelIdx+1<systems.size())
				setActiveModel(systems.elementAt(activeModelIdx+1).getName());
			else if(activeModelIdx-1>=0)
				setActiveModel(systems.elementAt(activeModelIdx-1).getName());
			else
				setActiveModel(null);
////			 TODO change name to fsa.id for consistency with add and remove
//			fireModelSwitched(new WorkspaceMessage(WorkspaceMessage.MODEL, 
//								name, 
//								WorkspaceMessage.REMOVE,
//								this));
		}
		
		DESModel fsa = systems.get(idx);
		
		graphs.elementAt(idx).release();
		systems.removeElementAt(idx);	
		metadata.removeElementAt(idx);
		graphs.removeElementAt(idx);
		
		if(activeModelIdx>idx)
		{
			activeModelIdx--;
		}
		
		fireModelCollectionChanged(new WorkspaceMessage(WorkspaceMessage.MODEL, 
				fsa.getId(), 
				WorkspaceMessage.REMOVE, 
				this));

		dirty = true;
	}

//	public FSAEventsModel getEventsModel() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	public String getActiveModelName() {
		if(activeModelIdx<0){
			return "";
		}
		return systems.elementAt(activeModelIdx).getName();
	}

	public DESModel getActiveModel(){
		if(activeModelIdx<0){
			return null;
		}
		return systems.elementAt(activeModelIdx);
	}
	
//	public int getActiveModelIndex(){
//		return activeModelIdx;
//	}

	/**
	 * Remove all tabs from the main pane of IDES.
	 * Release the currently active {@link LayoutShell}
	 * from all presentations displayed in the tabs.
	 * Dispose of the presentations.
	 */
	protected void releaseEditPanes()
	{
		// FIXME the obtainment of the tabbed pane is ugly
		JTabbedPane tabs=((MainWindow)Hub.getMainWindow()).getMainPane();
		tabs.removeAll();
		for(int i=0;i<activePresentations.length;++i)
		{
			activePresentations[i].release();
		}
		activePresentations=new Presentation[0];
	}
	
	/**
	 * Sets the active model to the FSAModel with the given name. 
	 * FIXME Should be using unique ID. 
	 * 	 
	 * @param name
	 */
	public void setActiveModel(String name) {
		releaseEditPanes();
		if(name==null)
		{
			activeModelIdx=-1;
		}
		else
		{
			activeModelIdx=getModelIndex(name);			
			// FIXME the obtainment of the tabbed pane is ugly
			JTabbedPane tabs=((MainWindow)Hub.getMainWindow()).getMainPane();
			Toolset ts=PresentationManager.getToolset(systems.elementAt(activeModelIdx).getModelDescriptor().getPreferredModelInterface());
			UIDescriptor uid=ts.getUIElements(getActiveLayoutShell());
			activePresentations=uid.getMainPanePresentations();
			for(int i=0;i<activePresentations.length;++i)
			{
				tabs.add(activePresentations[i].getName(),activePresentations[i].getGUI());
			}
		}

		// TODO change name to fsa.id for consistency with add and remove
		fireModelSwitched(new WorkspaceMessage(WorkspaceMessage.MODEL, 
							name, 
							WorkspaceMessage.MODIFY,
							this));		
	}
	
	/**
	 * 
	 * @return an iterator of all graph models in this workspace
	 */
	public Iterator<LayoutShell> getLayoutShells(){
		ArrayList<LayoutShell> g = new ArrayList<LayoutShell>();
		Iterator<LayoutShell> iter = graphs.iterator();
		while(iter.hasNext()){
			g.add(iter.next());
		}
		return g.iterator();
	}
	            
	/**
	 * 
	 * @return an iterator of all automata in this workspace
	 */
    public Iterator<DESModel> getModels() {
    	ArrayList<DESModel> g = new ArrayList<DESModel>();
		Iterator<DESModel> iter = systems.iterator();
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

	public LayoutShell getActiveLayoutShell() {	
		if(activeModelIdx < 0) {
			return null;
		}
		return graphs.elementAt(activeModelIdx);
	}
	
	/**
	 * Replaces the current workspace with the workspace given in the descriptor. 
	 * @param wd the descriptor of the replacement workspace
	 */
	public void replaceWorkspace(WorkspaceDescriptor wd)
	{
		//deactivate all models
		setActiveModel(null);
		
		Iterator<DESModel> iter=getModels();
		while(iter.hasNext())
		{
			removeModel(iter.next().getName());
			iter=getModels();
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
			FSAModel fsa = FileOperations.openAutomaton(
					new java.io.File(files.elementAt(i)));
			if(fsa != null)
			{
				Hub.getWorkspace().addModel(fsa);
				if(i==idx)
					selectedModel=fsa.getName();
			}
		}
		// Hey LENKO! what is the nature of this change?  Everything appears to have changed...
		fireModelCollectionChanged(new WorkspaceMessage(WorkspaceMessage.MODEL, 
								"everything changed?", 
								WorkspaceMessage.MODIFY, 
								this));
		
		setActiveModel(selectedModel);
		//notifyAllSubscribers();
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
		HashSet<DESModel> unsavedModels=new HashSet<DESModel>();
		for(Iterator<DESModel> i=getModels();i.hasNext();)
		{
			DESModel a=i.next();
			if((File)a.getAnnotation(Annotable.FILE)==null)
				unsavedModels.add(a);
		}
		if(!unsavedModels.isEmpty())
		{
			Hub.displayAlert(Hub.string("firstSaveUnsaved"));
			for(DESModel a:unsavedModels)
			{
				if(!(a instanceof FSAModel))
					continue;
				if(!FileOperations.saveAutomatonAs((FSAModel)a))
					throw new IncompleteWorkspaceDescriptorException();
//				getGraphModel(a.getName()).setDirty(false);
//				getGraphModel(a.getName()).notifyAllSubscribers();
			}
		}
		for(int counter=0; counter<systems.size(); ++counter)
		{
			DESModel a=systems.elementAt(counter);
			wd.insertModel(((File)a.getAnnotation(Annotable.FILE)).getName(),counter);
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
	
//	/**
//	 * TODO: fix this
//	 * @return the top-left corner of the drawing area
//	 */
//	public Point getDrawingBoardDisplacement()
//	{
//		return ((MainWindow)Hub.getMainWindow()).getDrawingBoardDisplacement();
//	}
//
//	/**
//	 * TODO: fix this
//	 * @return the background color of the drawing area
//	 */
//	public Color getDrawingBoardBGColor()
//	{
//		return ((MainWindow)Hub.getMainWindow()).getDrawingBoardBGColor();
//	}
//	
//	//TODO this should return an interface, not GDV
//	public presentation.fsa.GraphDrawingView getDrawingBoard()
//	{
//		return ((MainWindow)Hub.getMainWindow()).getDrawingBoard();
//	}
	
	/**
	 * Get the number of models in the workspace
	 * @return number of models in the workspace
	 */
	public int size()
	{
		return systems.size();
	}
	
	public <T> Collection<T> getModelsOfType(Class<T> type)
	{
		Vector<T> models=new Vector<T>();
		for(DESModel m:systems)
		{
			Class[] ifaces=m.getModelDescriptor().getModelInterfaces();
			for(int i=0;i<ifaces.length;++i)
			{
				if(ifaces[i].equals(type))
				{
					models.add((T)m);
					break;
				}
			}
		}
		return models;
	}
	
	public <T> Collection<T> getLayoutShellsOfType(Class<T> type)
	{
		Vector<T> wraps=new Vector<T>();
		for(LayoutShell m:graphs)
		{
			if(m.getClass().equals(type))
			{
				wraps.add((T)m);
				break;
			}
		}
		return wraps;
	}
	
	public Presentation[] getPresentations()
	{
		return activePresentations;
	}
	
	public Presentation[] getPresentationsOfType(Class type)
	{
		Vector<Presentation> ps=new Vector<Presentation>();
		for(int i=0;i<activePresentations.length;++i)
		{
			if(activePresentations[i].getClass().equals(type))
			{
				ps.add(activePresentations[i]);
			}
		}
		return ps.toArray(new Presentation[0]);
	}
	
	public void fireRepaintRequired() {
		super.fireRepaintRequired();
//		((MainWindow)Hub.getMainWindow()).getMainPane().validate();
	}
}
