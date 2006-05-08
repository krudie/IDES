package ui;

import java.util.Iterator;
import java.util.LinkedList;

import model.fsa.ver1.Automaton;
import model.fsa.ver1.MetaData;

import ui.command.CommandHistory;

/** 
 * Captures the state of the user interface at any point in time.
 * Mediates between the underlying data model and multiple concurrent views.
 * Stores the command history and the currently active view.
 * 
 * TODO change this to support a workspace of multiple publishers (DES models).
 * 
 * @author Helen Bretzke
 *
 */
public class UIStateModel {
		
	public static UIStateModel instance() {
		if(me == null) {
			me = new UIStateModel();			
		}
		return me;
	}
	// The singleton instance
	protected static UIStateModel me = null;
		
	private UIStateModel() {
		commandHistory = new CommandHistory();
		views = new LinkedList<Subscriber>();
		automaton = null;
		metadata = null;
		graphDrawingView = null;
	}	
	
	/**
	 * The command history for the user interface
	 */
	private CommandHistory commandHistory;

	/**
	 * Abstract data model to keep synchronized with visualModel.
	 * TODO Change to a set of publishers to support multiple automata in workspace.
	 */ 
	private Automaton automaton;
	private MetaData metadata;  // extra data, e.g. visualization details for the data model
	private GraphModel graphModel;
	private GraphDrawingView graphDrawingView;
	
	/**
	 * Multiple views on the data.
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
	 * 
	 * @param activeView
	 */
	public void setActiveView(Subscriber activeView) {
		this.activeView = activeView;
	}

	public CommandHistory getCommandHistory() {
		return commandHistory;
	}

	public MetaData getMetadata() {
		return metadata;
	}

	public void setMetadata(MetaData metadata) {
		this.metadata = metadata;
	}

	public GraphModel getGraphModel() {
		return graphModel;
	}

	public void setGraphModel(GraphModel graphModel) {
		this.graphModel = graphModel;
		addView(graphModel);
		graphDrawingView.setGraphModel(graphModel);	
	}

	public GraphDrawingView getGraphDrawingView() {
		return graphDrawingView;
	}

	public void setGraphDrawingView(GraphDrawingView graphDrawingView) {
		this.graphDrawingView = graphDrawingView;
	}	
}
