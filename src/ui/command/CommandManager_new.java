/**
 * 
 */
package ui.command;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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
			//TODO make the repaint be called just when needed.
			Hub.getWorkspace().fireRepaintRequired();
		}
		
		//Makes the ActiveUndoManager redo an action
		public void redo()
		{
			getActiveUndoManager().redo();
			refreshUndoRedo();
			//Inform the workspace to repaint the graph.
			//TODO make the repaint be called just when needed.
			Hub.getWorkspace().fireRepaintRequired();
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
	private JMenuItem select, create, move, alignNodes;
	//Defining the menu items to the "operationsMenu"
	private JMenuItem operations;
	//Defining the menu items to the "operationsMenu"
	private JCheckBoxMenuItem uniformNodeSize;
	private JCheckBoxMenuItem useLaTeX;
	private JMenuItem moreOptions;
	//Defining the menu items to the "helpMenu"
	private JMenuItem aboutIDES;


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
		undoSupport.addUndoableEditListener(new UndoAdaptor());
		//Initialize the categories in the menu.
		fileMenu = new JMenu("File");
		editMenu = new JMenu("Edit");
		graphMenu = new JMenu("Graph");
		operationsMenu = new JMenu("Operations");
		optionsMenu = new JMenu("Options");
		helpMenu = new JMenu("Help");

		//Initializing the menu items in the "fileMenu"
		newModel = new JMenuItem("New model");
		newModel.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		newModel.addActionListener(new ui.command.FileCommands.NewAction());

		openModel = new JMenuItem("Open model");
		openModel.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		openModel.addActionListener(new ui.command.FileCommands.OpenAction());

		saveModel = new JMenuItem("Save model");
		saveModel.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, ActionEvent.CTRL_MASK));	
		saveModel.addActionListener(new ui.command.FileCommands.SaveAction());

		saveModelAs = new JMenuItem("Save model as...");
		saveModelAs.addActionListener(new ui.command.FileCommands.SaveAsAction());

		saveAllModels = new JMenuItem("Save all model");
		saveAllModels.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
		saveAllModels.addActionListener(new ui.command.FileCommands.SaveAllAction());

		closeModel = new JMenuItem("Close model");
		closeModel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		closeModel.addActionListener(new ui.command.FileCommands.CloseAction());

		openWorkspace = new JMenuItem("Open workspace");
		openWorkspace.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
		openWorkspace.addActionListener(new ui.command.FileCommands.OpenWorkspaceAction());

		saveWorkspace = new JMenuItem("Save workspace");
		saveWorkspace.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
		saveWorkspace.addActionListener(new ui.command.FileCommands.SaveWorkspaceAction());

		saveWorkspaceAs = new JMenuItem("Save workspace as...");
		saveWorkspaceAs.addActionListener(new ui.command.FileCommands.SaveWorkspaceAsAction());

		importModel = new JMenuItem("Import");
		importModel.addActionListener(new ui.command.FileCommands.ImportAction());

		exportModel = new JMenuItem("Export");
		exportModel.addActionListener(new ui.command.FileCommands.ExportAction());

		exitIDES = new JMenuItem("Exit");
		exitIDES.addActionListener(new ui.command.FileCommands.ExitAction());
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
		select = new JMenuItem("Select");
		create = new JMenuItem("Create");
		move = new JMenuItem("Move");
		alignNodes = new JMenuItem("AlignNodes"); 

		//Adding the menu items to the "graphMenu"
		graphMenu.add(select);
		graphMenu.add(create);
		graphMenu.add(move);
		graphMenu.add(alignNodes);



		//adding the menu items to the "operationsMenu"
		operations = new JMenuItem("DES operations");
		operationsMenu.add(operations);


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
}
