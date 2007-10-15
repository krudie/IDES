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

/**
 * This class was created to replace the external class "GUI Commands" used in the past.
 * This class is the manager for the menus and toolbars available in IDES.
 * @author christiansilvano
 *
 */
public class CommandManager_new {
	public JToolBar toolbar;
	public UndoableEditSupport undoSupport = new UndoableEditSupport();


	/**
	 * The MultiUndoManager is the class responsible for undo and redo undoable actions
	 * for multiple files in the workspace.
	 * It will rename the Undo/Redo items in the user menu, acording to the actions to
	 * be undone/redone.
	 *
	 */
	private class MultiUndoManager implements WorkspaceSubscriber{
		UndoManager activeUndoManager;
		HashMap<String, UndoManager> undoManagers;

		public MultiUndoManager()
		{
			undoManagers = new HashMap<String, UndoManager>();
			Hub.getWorkspace().addSubscriber(this);
		}

		private UndoManager getActiveUndoManager()
		{
			if(activeUndoManager == null)
			{
				String activeModel = Hub.getWorkspace().getActiveModelName();
				if(undoManagers.get(activeModel) == null)
				{
					undoManagers.put(activeModel, new UndoManager());
				}
				activeUndoManager = undoManagers.get(activeModel);
			}
			return activeUndoManager;
		}

		public void undo()
		{
			getActiveUndoManager().undo();
			refreshUndoRedo();
			//Inform the workspace to repaint the graph.
//			Hub.getWorkspace().fireRepaintRequired();
		}

		//Makes the ActiveUndoManager redo an action
		public void redo()
		{
			getActiveUndoManager().redo();
			refreshUndoRedo();
			//Inform the workspace to repaint the graph.
//			Hub.getWorkspace().fireRepaintRequired();
		}

		//Add an action to the active manager, the action has to be always added to the
		//active model.
		public void addEdit(UndoableEdit edit)
		{
			getActiveUndoManager().addEdit(edit);
			refreshUndoRedo();
		}

		//Update the names of the undo/redo items in the user menu, reflecting
		//the action to be done.
		private void refreshUndoRedo() {
			//refresh undo
			undo.setText(undoManager.getActiveUndoManager().getUndoPresentationName());
			undo.setEnabled(undoManager.getActiveUndoManager().canUndo());
			// refresh redo 
			redo.setText(undoManager.getActiveUndoManager().getRedoPresentationName());
			redo.setEnabled(undoManager.getActiveUndoManager().canRedo()); 
		} 

		//Implementation of the WorkspaceSubscriber interface
		/**
		 * Notifies this subscriber that a model collection change 
		 * (a DES model is created or opened (added), closed (removed) 
		 * or renamed) has occurred in a <code>WorkspacePublisher</code> 
		 * to which I have subscribed.
		 *  
		 * @param message details of the change notification
		 */
		public void modelCollectionChanged(WorkspaceMessage message)
		{
		}

		/**
		 * Notifies this subscriber that a the model type has been switched 
		 * (the type of active model has changed e.g. from FSA to petri net) 
		 * in a <code>WorkspacePublisher</code> to which I have subscribed. 
		 *  
		 * @param message details of the change notification
		 */
		public void modelSwitched(WorkspaceMessage message)
		{
			//Set the activeUndoManager to reflect the currently selected model.
			activeUndoManager = undoManagers.get(Hub.getWorkspace().getActiveModelName());
			refreshUndoRedo();			
		}

		/**
		 * Notifies this subscriber that a change requiring a repaint has
		 * occurred in a <code>WorkspacePublisher</code> to which I have
		 * subscribed.
		 *  
		 * @param message details of the change notification
		 */
		/* NOTE not used here */
		public void repaintRequired(WorkspaceMessage message){}; 
	}

	//Edit listener, will inform the UndoManager everytime an undoable action is done.
	private class UndoAdaptor implements UndoableEditListener {
		public void undoableEditHappened (UndoableEditEvent evt) {
			UndoableEdit edit = evt.getEdit();
			undoManager.addEdit(edit);
		}
	}

	//Action listener for the Undoable actions, perform the undo action when
	//the user press the "Undo" item in the edit menu
	private class UndoAction extends AbstractAction{
		public void actionPerformed(ActionEvent evt ) {
			undoManager.undo();
		}
	}
	//Action listener for the Undoable actions, perform the redo action when
	//the user press the "Redo" item in the edit menu
	private class RedoAction extends AbstractAction{
		public void actionPerformed(ActionEvent evt ) {
			undoManager.redo();
		}
	}


	private MultiUndoManager undoManager = new MultiUndoManager();
	private UndoAction undoAction = new UndoAction();
	private RedoAction redoAction = new RedoAction();

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
		
//	Singleton instance:
	private static CommandManager_new instance = null;

	public static CommandManager_new getInstance()
	{
		if (instance == null)
		{
			instance = new CommandManager_new(); 
		}
		return instance;
	}

	private CommandManager_new()
	{
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
		toolbar.add(new GraphCommands.SelectAction());
		toolbar.add(new GraphCommands.CreateAction());
		toolbar.add(new GraphCommands.MoveAction());
		toolbar.addSeparator();
		toolbar.add(new GraphCommands.AlignAction());
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
		select = new JMenuItem(new GraphCommands.SelectAction());
		create = new JMenuItem(new GraphCommands.CreateAction());
		move = new JMenuItem(new GraphCommands.MoveAction());
		alignNodes = new JMenuItem(new GraphCommands.AlignAction()); 
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
		uniformNodeSize = new JCheckBoxMenuItem("Uniform node size");
		useLaTeX  = new JCheckBoxMenuItem("Use LaTeX");
		moreOptions = new JMenuItem("More options...");

		optionsMenu.add(uniformNodeSize);
		optionsMenu.add(useLaTeX);
		optionsMenu.addSeparator();
		optionsMenu.add(moreOptions);

		//adding the menu items to the "helpMenu"
		aboutIDES = new JMenuItem("About IDES...");
		helpMenu.add(aboutIDES);


		//Adding the main categories to the menu
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(graphMenu);
		menuBar.add(operationsMenu);
		menuBar.add(optionsMenu);
		menuBar.add(helpMenu);

		//Should we set the accessible descriptions?
//		fileMenu.getAccessibleContext().setAccessibleDescription(
//		"The only menu in this program that has menu items");
	}


	public JMenuBar getMenuBar()
	{
		return menuBar;
	}

	/**
	 * This is just an adaptation of a JToggleButton that will supress the 
	 * label from the action it is constructed from.
	 * @author christiansilvano
	 *
	 */
	public static class ToggleButtonAdaptor extends JToggleButton
	{
		public ToggleButtonAdaptor(AbstractAction action)
		{
			super(action);
			this.setText("");
		}
	}

	/**
	 * Accessor for the ToolBar
	 * @return
	 */
	public JToolBar getToolBar()
	{
		return toolbar;
	}
}
