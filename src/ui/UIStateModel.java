package ui;

import java.util.Iterator;
import java.util.LinkedList;

import main.IDESWorkspace;
import main.Workspace;
import model.Publisher;
import model.Subscriber;
import model.fsa.ver1.Automaton;
import model.fsa.ver1.MetaData;

/** 
 * Captures the state of the user interface at any point in time.
 * Mediates between the underlying data model and multiple concurrent views.
 * Stores the command history and the currently active view.
 * 
 * TODO change this to support a workspace of multiple publishers (DES models).
 * Mediate between the Workspace (set of FSA) and the MainWindow, need access to the filmstrip
 * so can add, remove and update thumbnails of each graph.
 * 
 * TODO support set of multiple graph models, one for each FSA.
 * 
 * @author Helen Bretzke
 *
 */
public class UIStateModel  {
		
	public static UIStateModel instance() {
		if(me == null) {
			me = new UIStateModel();			
		}
		return me;
	}
	// The singleton instance
	protected static UIStateModel me = null;
		
	private UIStateModel() {		
		views = new LinkedList<Subscriber>();
		automaton = null;
		metadata = null;
		graphDrawingView = null;		
	}
	
	/**
	 * Abstract data model for the currently active FSA.
	 * to keep synchronized with visualModel. 
	 */
	private Automaton automaton;
	private MetaData metadata;  // extra data, e.g. visualization details for the data model
	
	/**
	 * Visual model and display for currently active FSA.
	 */
	private GraphModel graphModel;
	private GraphDrawingView graphDrawingView;		
	private MainWindow window;
	
	/**
	 * Multiple views on current FSA.
	 * TODO Change to set of sets: views for each automaton model. 
	 */
	private LinkedList<Subscriber> views;
	
	/**
	 * The currently active view.
	 * TODO change to a set: could be more than one active view.
	 */
	private Subscriber activeView;
	
	/**
	 * Add the given Subscriber to the set of views.
	 */
	public void addView(Subscriber view) {
		views.add(view);
	}
	
	/**
	 * Synchronize all views with the underlying data model.
	 *
	 */
	public void refreshViews() {
		automaton.notifyAllSubscribers();
	}	

	protected Publisher getAutomaton() {
		return automaton;
	}	

	/**
	 * Set the underlying data model and attaches all subscriber views 
	 * to the given Publisher.
	 * 
	 * @param model is not null
	 */
	public void setAutomaton(Automaton model) {
		automaton = model;
		Iterator v = views.iterator();
		while(v.hasNext()){
			automaton.attach((Subscriber)v.next());
		}
		
		// TODO the filmstrip of thumbnails will now toggle highlighting the given model.
		
	}

	/**
	 * FIXME Change to support muliple active views.
	 * 
	 * @return
	 */
	public Subscriber getActiveView() {
		return activeView;
	}

	/**
	 * TODO Change to activate/deactivate multiple views.
	 * To be called by the main window when a different document or tab is selected.
	 *  
	 * @param activeView
	 */
	public void setActiveView(Subscriber activeView) {
		this.activeView = activeView;
	}
	public MetaData getMetadata() {
		return metadata;
	}

	/**
	 * @param name
	 * @return metadata for the FSA with the given name
	 */
	public MetaData getMetadata(String name) {
		return metadata;
	}

	
	public void setMetadata(MetaData metadata) {
		this.metadata = metadata;
	}

/**
 * Sets the metadata for the FSA with the given name.
 * 
 * @param metadata
 * @param name
 */
	public void setMetadata(MetaData metadata, String name) {
		this.metadata = metadata;
	}

	public GraphModel getGraphModel() {
		return graphModel;
	}
	
	/** 
	 * TODO implement set of more than one graph model
	 * 
	 * @param name
	 * @return the graph model for the FSA with the given name.
	 */
	public GraphModel getGraphModel(String name) {
		return graphModel;
	}

	///////////////////////////////////////////////////////
	
	/** 
	 * Sets the active graph model for the interface to the given model.  
	 * Attaches the graph drawing view as a subscriber to the graph model.
	 * Attaches the given graph model model as a subscriber to the FSA model.
	 * 
	 * @param graphModel
	 */
	public void setGraphModel(GraphModel graphModel) {
		this.graphModel = graphModel;
		// graphModel is treated as a view of the (single) automaton
		addView(graphModel);
		graphDrawingView.setGraphModel(graphModel);
		graphModel.attach(graphDrawingView);
		// DEBUG
		// FilmStrip will do all of this itself when Workspace is notified of change.
		GraphView thumbNail = new GraphView();
		thumbNail.setGraphModel(graphModel);
		graphModel.attach(thumbNail);		
		window.getFilmStrip().add(thumbNail);
		
		window.pack();
		/////////////////////////////////////
		graphModel.notifyAllSubscribers();
	}

	public GraphDrawingView getGraphDrawingView() {
		return graphDrawingView;
	}

	public void setGraphDrawingView(GraphDrawingView graphDrawingView) {
		this.graphDrawingView = graphDrawingView;
	}

	public void setWindow(MainWindow window) {
		this.window = window;
	}
}
