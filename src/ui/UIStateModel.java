package ui;

import java.util.Iterator;
import java.util.LinkedList;

import model.DESModel;
import model.DESObserver;
import presentation.Glyph;
import ui.command.CommandHistory;

/** 
 * Captures the state of the user interface at any point in time.
 * Mediates between the underlying data model and multiple concurrent views.
 * 
 * Maintains a snapshot of the 
 * * current view
 * * current interaction mode,
 * * currently selected object in the drawing area,
 * * command history
 * * copy and cut buffers.
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
		views = new LinkedList<DESObserver>();
		desModel = null;
	}	
	
	/**
	 * The command history for the user interface
	 */
	private CommandHistory commandHistory;

	/**
	 * Abstract data model to keep synchronized with visualModel.
	 */ 
	private DESModel desModel;
	
	/**
	 * Multiple views on the data data.
	 */
	private LinkedList<DESObserver> views;
	
	/**
	 * Add the given DESObserver to the set of views.
	 */
	public void addView(DESObserver view) {
		views.add(view);
	}
	
	// FIXME 
	// This class assumes that anything in the buffers will be a Glyph i.e. that we are using the DrawingBoard. 
	// ??? What about the graph specifications interface; tabular and holds only text data.
		
	/**
	 * Copy buffer
	 */
	private Glyph copyBuffer;
	
	/**
	 * Cut buffer 
	 */
	private Glyph cutBuffer;
	
	/**
	 * Delete and restore buffer
	 */
	private Glyph deleteBuffer;
	
	
	/**
	 * Currently selected group or item.
	 */
	private Glyph currentSelection;
	
	/**
	 * The selected print area.
	 */
	private Glyph printArea;
	

	public Glyph getCurrentSelection() {
		return currentSelection;
	}

	public void setCurrentSelection(Glyph currentSelection) {
		this.currentSelection = currentSelection;
	}	

	protected DESModel getDESModel() {
		return desModel;
	}
	/**
	 * Synchronize all views with the underlying data model.
	 *
	 */
	public void refresh() {
		desModel.notifyAllObservers();
	}

	/**
	 * Set the underlying data model to the given DESModel and
	 * attach all views to the model.
	 * 
	 * @param model is not null
	 */
	public void setDESModel(DESModel model) {
		desModel = model;
		Iterator v = views.iterator();
		while(v.hasNext()){
			desModel.attach((DESObserver)v.next());
		}
	}
	
	
//////////////////////////////////////////////////////////////////////	
//	 User interaction modes to determine mouse and keyboard responses.
	// TODO This will be replaced by State pattern with a different tool 
	// instance for each mode.  Listeners will just talk to the current tool.
	public final static int DEFAULT_MODE = 0;
	public final static int SELECT_AREA_MODE = 1;
	public final static int ZOOM_IN_MODE = 2;
	public final static int ZOOM_OUT_MODE = 7;
	public final static int SCALE_MODE = 8;
	public final static int CREATE_MODE = 3;
	public final static int MODIFY_MODE = 4;
	public final static int MOVE_MODE = 5;
	public final static int TEXT_MODE = 6;

	// Current user interaction mode; 
	// determines response to mouse and keyboard actions 
	// and cursor appearance
	private int interactionMode = DEFAULT_MODE;

	public int getInteractionMode() {
		return interactionMode;
	}

	public void setInteractionMode(int interactionMode) {
		this.interactionMode = interactionMode;
	}
	
}
