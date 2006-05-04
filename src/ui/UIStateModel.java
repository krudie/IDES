package ui;

import java.util.Iterator;
import java.util.LinkedList;

import presentation.Glyph;
import ui.command.CommandHistory;

/** 
 * Captures the state of the user interface at any point in time.
 * Mediates between the underlying data model and multiple concurrent views.
 * Stores the command history and the currently active view.
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
		desModel = null;
	}	
	
	/**
	 * The command history for the user interface
	 */
	private CommandHistory commandHistory;

	/**
	 * Abstract data model to keep synchronized with visualModel.
	 */ 
	private Publisher desModel;
	
	/**
	 * Multiple views on the data data.
	 */
	private LinkedList<Subscriber> views;
	
	/**
	 * The currently active view.
	 */
	private Subscriber activeView;
	
	/**
	 * Add the given DESObserver to the set of views.
	 */
	public void addView(Subscriber view) {
		views.add(view);
	}
	
	/**
	 * Synchronize all views with the underlying data model.
	 *
	 */
	public void refresh() {
		desModel.notifyAllSubscribers();
	}	

	protected Publisher getDESModel() {
		return desModel;
	}	

	/**
	 * Set the underlying data model to the given DESModel and
	 * attach all views to the model.
	 * 
	 * @param model is not null
	 */
	public void setDESModel(Publisher model) {
		desModel = model;
		Iterator v = views.iterator();
		while(v.hasNext()){
			desModel.attach((Subscriber)v.next());
		}
	}

	public Subscriber getActiveView() {
		return activeView;
	}

	public void setActiveView(Subscriber activeView) {
		this.activeView = activeView;
	}
	
}
