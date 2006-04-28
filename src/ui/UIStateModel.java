package ui;

import java.util.Iterator;
import java.util.LinkedList;

import model.fsa.FSAModel;
import model.fsa.FSAObserver;
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
		views = new LinkedList<FSAObserver>();
		desModel = null;
	}	
	
	/**
	 * The command history for the user interface
	 */
	private CommandHistory commandHistory;

	/**
	 * Abstract data model to keep synchronized with visualModel.
	 */ 
	private FSAModel desModel;
	
	/**
	 * Multiple views on the data data.
	 */
	private LinkedList<FSAObserver> views;
	
	/**
	 * The currently active view.
	 */
	private FSAObserver activeView;
	
	/**
	 * Add the given DESObserver to the set of views.
	 */
	public void addView(FSAObserver view) {
		views.add(view);
	}
	
	/**
	 * Synchronize all views with the underlying data model.
	 *
	 */
	public void refresh() {
		desModel.notifyAllObservers();
	}	

	protected FSAModel getDESModel() {
		return desModel;
	}	

	/**
	 * Set the underlying data model to the given DESModel and
	 * attach all views to the model.
	 * 
	 * @param model is not null
	 */
	public void setDESModel(FSAModel model) {
		desModel = model;
		Iterator v = views.iterator();
		while(v.hasNext()){
			desModel.attach((FSAObserver)v.next());
		}
	}

	public FSAObserver getActiveView() {
		return activeView;
	}

	public void setActiveView(FSAObserver activeView) {
		this.activeView = activeView;
	}
	
}
