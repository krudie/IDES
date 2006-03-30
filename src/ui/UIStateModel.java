package ui;

import presentation.Glyph;
import ui.command.CommandHistory;

/** 
 * Captures the state of the user interface at any point in time.
 * 
 * Maintains a snapshot of 
 * * the current interaction mode,
 * * the currently selected object in the drawing area,
 * * the command history
 * * the copy and cut buffers.
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
	}
	
	
	/**
	 * The command history for the user interface
	 */
	private CommandHistory commandHistory;
	
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
	 * Presentation model (the glyph structure corresponding to the DES model.
	 */
	private Glyph visualModel;
	// TODO need a parallel abstract model to keep synchronized with visualModel
	
	/**
	 * Currently selected group or item.
	 */
	private Glyph currentSelection;
	
	/**
	 * The selected print area.
	 */
	private Glyph printArea;
	
	
//	 User interaction modes to determine mouse and keyboard responses.
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
