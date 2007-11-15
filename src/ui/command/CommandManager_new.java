/**
 * 
 */
package ui.command;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

import main.Hub;
import main.WorkspaceMessage;
import main.WorkspaceSubscriber;

import pluggable.io.IOCoordinator;
import services.latex.LatexManager;
import services.latex.UseLatexAction;

/**
 * This class is responsible to manage any menus and toolbars available in IDES.
 * The main objective is to retrieve a reference to a MenuBar and a ToolBar which will have unique
 * instances.
 * 
 * @author christiansilvano
 *
 */
public class CommandManager_new {
	/**
	 * A reference to the toolbar.
	 */
	public JToolBar toolbar;
	
	/**
	 * This object manages the UndoableEdit listeners.
	 * The listeners are the objects that are "interested" in when an undoable operation happens.
	 */
	public UndoableEditSupport undoSupport = new UndoableEditSupport();


	/**
	 * The MultiUndoManager is the class responsible for undoing and redoing undoable actions.
	 * The "Multi" prefix, comes because this class manages many models that may be opened in 
	 * a workspace.
	 * Its operation renames the items under the Edit-Undo/Redo menus, acording to the actions to
	 * be undone/redone. Example: Undo (Create node) / Redo (Delete edge)
	 *
	 */
	private class MultiUndoManager implements WorkspaceSubscriber{
		/**
		 * The active UndoManager (model related).
		 * It has a queue to all the UndoableOperations that can be called for an especific model.
		 */
		UndoManager activeUndoManager;
		
		/**
		 * A HashMap for the UndoManagers in the workspace. It is addressed by the name of the
		 * model.
		 */
		HashMap<String, UndoManager> undoManagers;

		/**
		 * Default constructor.
		 * The MultiUndoManager subscribes itself to the workspace, so it can proccess events for
		 * model switching and similar tasks. 
		 */
		public MultiUndoManager()
		{
			//Initializing the HashMap
			undoManagers = new HashMap<String, UndoManager>();
			//Subscribing as a WorkspaceSubscriber
			Hub.getWorkspace().addSubscriber(this);
		}

		/**
		 * Gets the active UndoManager.
		 * Since the active manager is dependent on the active model, this method 
		 * queries the workspace about the name of the active model and then access
		 * the HasMap for the UndoManager for that model. 
		 * If no manager was initialized before, this method will instantiate a new one
		 * and then retrieve it.
		 * @return a reference to the active UndoManager.
		 */
		private UndoManager getActiveUndoManager()
		{
			//If no UndoManager was initialized, create a new instance. 
			if(activeUndoManager == null)
			{
				String activeModelName = Hub.getWorkspace().getActiveModelName();
				if(undoManagers.get(activeModelName) == null)
				{
					//Adding the new instance to the HashMap.
					undoManagers.put(activeModelName, new UndoManager());
				}
				//Return a reference to the UndoManager related to the active model.
				activeUndoManager = undoManagers.get(activeModelName);
			}
			return activeUndoManager;
		}

		/**
		 * This method is called everytime the user clicks at the Undo action under the menu bar.
		 * It calls the undo() methos under the active UndoManager (dependent on the active model).
		 *
		 */
		public void undo()
		{
			//Makes the active UndoManager (dependent on the model) undo an operation.
			getActiveUndoManager().undo();
			//Refreshes the Undo/Redo queue.
			refreshUndoRedo();
		}

		/**
		 * This method is called everytime the user clicks at the Undo action under the menu bar.
		 * It calls the redo() methos under the active UndoManager (dependent on the active model).
		 *
		 */
		public void redo()
		{
			//Makes the active UndoManager (dependent on the model) redo an operation.
			getActiveUndoManager().redo();
			//Refreshes the Undo/Redo queue.
			refreshUndoRedo();
		}

		/**
		 * Adds an UndoableAction to the active manager, the action has to be always added to the
		*  active model. */
		public void addEdit(UndoableEdit edit)
		{
			//Adds the edit to the active UndoableManager
			getActiveUndoManager().addEdit(edit);
			//Refreshes the Undo/Redo queue
			refreshUndoRedo();
		}

		/** Updates the names under the Undo/Redo items on the user menu, for the active UndoManager,
		 *  reflecting the action to be done/undone.
		*/
		private void refreshUndoRedo() {
			//refreshes the "undo" queue
			undo.setText(undoManager.getActiveUndoManager().getUndoPresentationName());
			undo.setEnabled(undoManager.getActiveUndoManager().canUndo());
			//refreshes the "redo" queue 
			redo.setText(undoManager.getActiveUndoManager().getRedoPresentationName());
			redo.setEnabled(undoManager.getActiveUndoManager().canRedo()); 
		} 

		//The bellow methods are part of the implementation of the 
		// interface: "WorkspaceSubscriber".

		
		/**
		 * Notifies this subscriber that a model collection change 
		 * (a DES model is created or opened (added), closed (removed) 
		 * or renamed) has occurred in a <code>WorkspacePublisher</code> 
		 * to which I have subscribed.
		 *  
		 * @param message details of the change notification
		 */
		public void modelCollectionChanged(WorkspaceMessage message){/* NOTE not used here */}

		/**
		 * Notifies this subscriber that a the model type has been switched 
		 * (the type of active model has changed e.g. from FSA to petri net) 
		 * in a <code>WorkspacePublisher</code> to which I have subscribed. 
		 *  
		 * @param message details of the change notification
		 */
		public void modelSwitched(WorkspaceMessage message)
		{
			///Refreshes the UndoManager pointed by <code>activeUndoManager</code>
			///to reflect the active model.
			activeUndoManager = undoManagers.get(Hub.getWorkspace().getActiveModelName());
			//refreshes the "redo/undo" queues	
			refreshUndoRedo();			
		}

		/**
		 * Notifies this subscriber that a change requiring a repaint has
		 * occurred in a <code>WorkspacePublisher</code> to which I have
		 * subscribed.
		 *  
		 * @param message details of the change notification
		 */
		public void repaintRequired(WorkspaceMessage message){		/* NOTE not used here */}; 
	}

	/**
	 * This class is an Edit listener implementation.
	 * That means it will be informed everytime an undoable action is done.
	 */
	private class UndoAdaptor implements UndoableEditListener {
		public void undoableEditHappened (UndoableEditEvent evt) {
			UndoableEdit edit = evt.getEdit();
			undoManager.addEdit(edit);
		}
	}

	//Question: Should we move the two following classes to "EditCommands" ?
	/**Action listener for the Undoable actions, perform the undo action when
	 * the user press the "Undo" item in the edit menu
	 */
	private class UndoAction extends AbstractAction{
		public void actionPerformed(ActionEvent evt ) {
			undoManager.undo();
		}
	}
	/**Action listener for the Undoable actions, perform the undo action when
	 * the user press the "Redo" item in the edit menu
	 */
	private class RedoAction extends AbstractAction{
		public void actionPerformed(ActionEvent evt ) {
			undoManager.redo();
		}
	}

	private MultiUndoManager undoManager;
	private UndoAction undoAction;
	private RedoAction redoAction;

	//New Menu (It uses the Java API)
	private JMenuBar menuBar = new JMenuBar();
	//Defining the categories of menus in IDES
	private JMenu fileMenu, editMenu, graphMenu, operationsMenu, optionsMenu, helpMenu;
	//Defining the menu items to the "fileMenu"
	private JMenuItem newModel, openModel, saveModel,saveModelAs, saveAllModels, 
	closeModel, openWorkspace, saveWorkspace, saveWorkspaceAs, importModel, exportModel, exitIDES;
	//Defining the menu items for the "editMenu"
	private JMenuItem undo, redo;
	//Defining the menu items to the "graphMenu"
	private JMenuItem select, create, move, alignNodes, showGrid;
	//Defining the menu items to the "operationsMenu"
	private JMenuItem operations;
	//Defining the menu items to the "operationsMenu"
	private JCheckBoxMenuItem uniformNodeSize;
	private JCheckBoxMenuItem useLaTeX;
	private JMenuItem moreOptions;
	//Defining the menu items to the "helpMenu"
	private JMenuItem aboutIDES;

	//The only toggle button in the toolbar. It is here to have the rollover effect 
	//set via accessors.
	private ToggleButtonAdaptor gridButton;
	public static String TOGGLE_BUTTON="toggle", GRID_BUTTON="grid";

	public void updateToggleButton(String type, String name, boolean state)
	{
		if(type == TOGGLE_BUTTON)
		{
			if(name == GRID_BUTTON)
			{
				gridButton.setSelected(state);
			}
		}
	}
		
	//Singleton instance:
	private static CommandManager_new instance = null;

	/**
	 * Returns the Singleton instance for the CommandManager
	 */
	public static CommandManager_new getInstance()
	{
		if (instance == null)
		{
			instance = new CommandManager_new(); 
		}
		return instance;
	}

	/**
	 * Default constructor.
	 */
	private CommandManager_new()
	{
		
		undoManager = new MultiUndoManager();
		undoAction = new UndoAction();
		redoAction = new RedoAction();
		//Initializing the ToolBar
		toolbar = new JToolBar();
		toolbar.add(new FileCommands.NewAction());
		toolbar.add(new FileCommands.OpenAction());
		toolbar.add(new FileCommands.SaveAction());
		toolbar.add(new FileCommands.SaveAllAction());
		toolbar.addSeparator();
		toolbar.add(new FileCommands.OpenWorkspaceAction());
		toolbar.add(new FileCommands.SaveWorkspaceAction());
		toolbar.addSeparator();
		toolbar.add(new GraphCommands.SelectTool());
		toolbar.add(new GraphCommands.CreateTool());
		toolbar.add(new GraphCommands.MoveTool());
		toolbar.addSeparator();
		toolbar.add(new GraphCommands.AlignTool());
		toolbar.setFloatable(false);
		
		gridButton = new ToggleButtonAdaptor(new OptionsCommands.ShowGridAction());
		toolbar.add(gridButton);


		//Initializing the undoable edit support
		undoSupport.addUndoableEditListener(new UndoAdaptor());
		//Initialize the categories in the menu.
		fileMenu = new JMenu("File");
		editMenu = new JMenu("Edit");
		graphMenu = new JMenu("Graph");
		operationsMenu = new JMenu("Operations");
		optionsMenu = new JMenu("Options");
		helpMenu = new JMenu("Help");

		//Initializing the menu items in the "fileMenu"
		newModel = new JMenuItem(new FileCommands.NewAction());
		newModel.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_N, ActionEvent.CTRL_MASK));

		openModel = new JMenuItem(new FileCommands.OpenAction());
		openModel.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O, ActionEvent.CTRL_MASK));

		saveModel = new JMenuItem(new FileCommands.SaveAction());
		saveModel.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, ActionEvent.CTRL_MASK));	

		saveModelAs = new JMenuItem(new FileCommands.SaveAsAction());

		saveAllModels = new JMenuItem(new FileCommands.SaveAllAction());
		saveAllModels.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));

		closeModel = new JMenuItem(new FileCommands.CloseAction());
		closeModel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));

		openWorkspace = new JMenuItem(new FileCommands.OpenWorkspaceAction());
		openWorkspace.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));

		saveWorkspace = new JMenuItem(new FileCommands.SaveWorkspaceAction());
		saveWorkspace.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));

		saveWorkspaceAs = new JMenuItem(new FileCommands.SaveWorkspaceAsAction());

		importModel = new JMenuItem(new FileCommands.ImportAction());

		exportModel = new JMenuItem(new FileCommands.ExportAction());

		exitIDES = new JMenuItem(new FileCommands.ExitAction());

		//Adding the menu items to the "fileMenu"
		fileMenu.add(newModel);
		fileMenu.add(openModel);
		fileMenu.add(saveModel);
		fileMenu.add(saveModelAs);
		fileMenu.add(saveAllModels);
		fileMenu.add(closeModel);
		fileMenu.addSeparator();
		fileMenu.add(openWorkspace);
		fileMenu.add(saveWorkspace);
		fileMenu.add(saveWorkspaceAs);
		fileMenu.addSeparator();
		fileMenu.add(importModel);
		fileMenu.add(exportModel);
		fileMenu.addSeparator();
		fileMenu.add(exitIDES);
		//Initializing the menu items for the "editMenu"
		undo = new JMenuItem("Undo");
		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		undo.addActionListener(undoAction);

		redo = new JMenuItem("Redo");
		redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		redo.addActionListener(redoAction);
		//Adding the menu items to the "editMenu"
		editMenu.add(undo);
		editMenu.add(redo);

		//Initializing the menu items for the "graphMenu"
		select = new JMenuItem(new GraphCommands.SelectTool());
		create = new JMenuItem(new GraphCommands.CreateTool());
		move = new JMenuItem(new GraphCommands.MoveTool());
		alignNodes = new JMenuItem(new GraphCommands.AlignTool()); 
		showGrid = new JMenuItem(new OptionsCommands.ShowGridAction());
		//Adding the menu items to the "graphMenu"
		graphMenu.add(select);
		graphMenu.add(create);
		graphMenu.add(move);
		graphMenu.add(alignNodes);
		graphMenu.add(showGrid);


		//adding the menu items to the "operationsMenu"
		operations = new JMenuItem("DES operations");
		operationsMenu.add(new OperationsCommands.ProductCommand());


		//adding the menu items to the "optionsMenu"
		uniformNodeSize = new JCheckBoxMenuItem(new UniformNodesAction());
		uniformNodeSize.setSelected(Hub.persistentData.getBoolean("uniformNodeSize"));
		useLaTeX  = new JCheckBoxMenuItem(new services.latex.UseLatexAction());
		useLaTeX.setSelected(LatexManager.isLatexEnabled());
		moreOptions = new JMenuItem(new OptionsCommands.MoreOptionsAction());

		optionsMenu.add(uniformNodeSize);
		optionsMenu.add(useLaTeX);
		optionsMenu.addSeparator();
		optionsMenu.add(moreOptions);

		//adding the menu items to the "helpMenu"
		aboutIDES = new JMenuItem(new HelpCommands.AboutCommand());
		helpMenu.add(aboutIDES);


		//Adding the main categories to the menu
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(graphMenu);
		menuBar.add(operationsMenu);
		menuBar.add(optionsMenu);
		menuBar.add(helpMenu);
	}

	/**
	 * Accessor method for the menuBar
	 * @return a reference for the unique instance of menuBar
	 */
	public JMenuBar getMenuBar()
	{
		return menuBar;
	}

	/**
	 * Accessor for the ToolBar
	 * @return a referenc]e for the unique instance of menuBar
	 */
	public JToolBar getToolBar()
	{
		return toolbar;
	}
	
	//This is an adapted JToggleButton that ommits the label of the original button.
	public static class ToggleButtonAdaptor extends JToggleButton
	{
		public ToggleButtonAdaptor(AbstractAction action)
		{
			super(action);
			//No matter which label action has, the constructed button will not have a 
			//label.
			this.setText("");
		}
	}
}
