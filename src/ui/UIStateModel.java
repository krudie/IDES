package ui;

import java.util.Iterator;
import java.util.LinkedList;

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
	
	public CommandHistory getCommandHistory() {
		return commandHistory;
	}

	// The singleton instance
	protected static UIStateModel me = null;
		
	private UIStateModel() {
		commandHistory = new CommandHistory();
		views = new LinkedList<Subscriber>();
		publisher = null;
	}	
	
	/**
	 * The command history for the user interface
	 */
	private CommandHistory commandHistory;

	/**
	 * Abstract data model to keep synchronized with visualModel.
	 */ 
	private Publisher publisher;
	
	/**
	 * Multiple views on the data.
	 */
	private LinkedList<Subscriber> views;
	
	/**
	 * The currently active view.
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
	public void refresh() {
		publisher.notifyAllSubscribers();
	}	

	protected Publisher getPublisher() {
		return publisher;
	}	

	/**
	 * Set the underlying data model and attaches all subscriber views 
	 * to the given Publisher.
	 * 
	 * @param model is not null
	 */
	public void setDESModel(Publisher model) {
		publisher = model;
		Iterator v = views.iterator();
		while(v.hasNext()){
			publisher.attach((Subscriber)v.next());
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
	
}
