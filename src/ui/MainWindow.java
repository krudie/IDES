/**
 * 
 */
package ui;

import io.fsa.ver1.FileOperations;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.pietschy.command.CommandGroup;
import org.pietschy.command.CommandManager;

import java.awt.BorderLayout;
import java.awt.Dimension;

import main.IDESWorkspace;
import main.SystemVariables;
import model.Subscriber;

import ui.command.*;
import ui.listeners.MenuListenerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * TODO Reimplement using gui-commands library.
 * Commands generate their own toolbuttons and menuitems.
 * Load configuration from a file.
 * 
 * @author helen bretzke
 *
 */
public class MainWindow extends JFrame implements Subscriber {

	public MainWindow() {
		super("IDES : Integrated Discrete-Event System Software 2.1");
		IDESWorkspace.instance().attach(this);  // subscribe to updates from the workspace
	
		drawingBoard = new GraphDrawingView();
		drawingBoard.setPreferredSize(new Dimension(750, 550));
		createAndAddTabbedPane();
		
		UIStateModel.instance().setGraphDrawingView(drawingBoard);		
		
		// TODO add graph spec, latex and eps views to the state model
		// UIStateModel.instance().addView(???);
		this.filmStrip = new FilmStrip();		
		getContentPane().add(filmStrip, BorderLayout.SOUTH);
		
		FileOperations.loadCommandManager("commands.xml");
		createAndAddMenuBar();
		createAndAddToolBar();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		pack();
		setSize(800, 600);
		setVisible(true);
	}
	
	 private void createAndAddTabbedPane() {
		tabbedViews = new JTabbedPane();
		drawingBoard.setName("No-name graph.");
		JScrollPane sp = new JScrollPane(drawingBoard, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		sp.setName(drawingBoard.getName());
		
		// TODO attach a listener to the tabbedPane that sets the active view in the UIStateModel
		tabbedViews.add(sp);
		tabbedViews.addTab("Events", null);
		tabbedViews.addTab("LaTeX Output", null);		
		getContentPane().add(tabbedViews, "Center");
	}

	 private void createAndAddToolBar() {
		 // Create a vertical toolbar
		 JToolBar toolbar =  CommandManager.defaultInstance().getGroup("ides.toolbar.group").createToolBar(); // new JToolBar();
		 toolbar.setRollover(true);
		 toolbar.setOrientation(JToolBar.VERTICAL);
		 this.getContentPane().add(toolbar, BorderLayout.EAST);	    
	 }
	    
	 String imagePath = SystemVariables.instance().getApplication_path() + "/src/images/icons/"; 
	 
	private void createAndAddMenuBar() {
	 	 
		 /**
		  * Menu components
		  * 
		  * FIXME 
		  * Dynamically load and export all commands in 
		  * package ui.command.
		  * ??? This is tricky for file commands since need name and reference to command manager and filter.
		  */
		
		 loadAndExportCommands();
		 JMenuBar menuBar = CommandManager.defaultInstance().getGroup("ides.menu.group").createMenuBar(); // new JMenuBar();
		 
		 /**
		  * TODO Call update to enable only the appropriate commands.
		  */
		 
//		 menuBar.add(createFileMenu());		  
//		 menuBar.add(createEditMenu());
//		 menuBar.add(createGraphMenu());
//		 menuBar.add(createOptionsMenu());
		 	 
		 // TODO assemble the help menu
		 JMenu menuHelp = new JMenu("Help");
		 menuHelp.setMnemonic(KeyEvent.VK_H);
		 
		 menuBar.add(menuHelp);
		 
		 // add menubar to this window
		 getContentPane().add(menuBar, "North");
	}
	
	/**
	 * TODO 
	 * Dynamically load and export all commands in 
	 * package ui.command.
	 * ??? This is tricky for file commands since need the 
	 * command-id and reference to command manager and filter.
	 */
	private void loadAndExportCommands() {
		
		new FileCommands.OpenAutomatonCommand(CommandManager.defaultInstance(), "open.automaton.command", null).export();
		new FileCommands.SaveAutomatonCommand().export();
		new FileCommands.OpenWorkspaceCommand().export();
		new FileCommands.SaveWorkspaceCommand().export();		
		new EditCommand().export();
		new MoveCommand().export();
		new CreateCommand(drawingBoard).export();
	}

	/**
	 * Assembles and returns the file menu.
	 * TODO add listeners
	 * 
	 * @return the file menu
	 */
	private JMenu createFileMenu(){
		 JMenu menuFile = new JMenu("File");
		 menuFile.setMnemonic(KeyEvent.VK_F);
		 
		 menuFile.add(new FileCommands.OpenAutomatonCommand(CommandManager.defaultInstance(), "open.automaton.command", null).createMenuItem());
		 menuFile.add(new FileCommands.SaveAutomatonCommand().createMenuItem());
		 menuFile.add(new FileCommands.OpenWorkspaceCommand().createMenuItem());
		 menuFile.add(new FileCommands.SaveWorkspaceCommand().createMenuItem());
		 menuFile.add(CommandManager.defaultInstance().getGroup("export.group").createMenuItem());
//		 CommandGroup fileGroup = CommandManager.defaultInstance().getGroup("file.group");
//		 menuFile.add(fileGroup.createMenuItem());
		 
//		 menuExport = new JMenu("Export");
//		 miLatex = new JMenuItem("LaTeX", new ImageIcon(imagePath + "file_export_latex.gif"));
//		 miGif = new JMenuItem("GIF", new ImageIcon(imagePath + "file_export_gif.gif"));
//		 miPng = new JMenuItem("PNG", new ImageIcon(imagePath + "file_export_png.gif"));
//		 menuExport.add(miLatex);
//		 menuExport.add(miGif);
//		 menuExport.add(miPng);		 
//		 menuFile.add(menuExport);
//		 
//		 menuFile.addSeparator();
//		 		 
//		 miNewSystem = new JMenuItem("New System", new ImageIcon(imagePath + "file_new.gif"));
//		 miNewSystem.setMnemonic(KeyEvent.VK_N);
//		 miNewSystem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
//		 menuFile.add(miNewSystem);
//		 
//		 // TODO replace this listener with a open.model.command
//		 ActionListener fileMenuListener = MenuListenerFactory.makeFileMenuListener();	
//		 
//		 miOpen = new JMenuItem("Open", new ImageIcon(imagePath + "file_open.gif"));
//		 miOpen.setMnemonic(KeyEvent.VK_O);
//		 miOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
//		 miOpen.addActionListener(fileMenuListener);
//		 menuFile.add(miOpen);
//		 
//		 miSave = new JMenuItem("Save", new ImageIcon(imagePath + "file_save.gif"));
//		 miSave.setMnemonic(KeyEvent.VK_S);
//		 miSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
//		 menuFile.add(miSave);
//		 
//		 miSaveAs = new JMenuItem("Save As...", new ImageIcon(imagePath + "file_saveas.gif"));
//		 miSaveAs.setMnemonic(KeyEvent.VK_A);		 
//		 menuFile.add(miSaveAs);
//		 
//		 miExit = new JMenuItem("Exit");
//		 miExit.setMnemonic(KeyEvent.VK_X);
//		 menuFile.add(miExit);	 
		return menuFile;		
	}
	
	/**
	 * Assembles and returns the edit menu.
	 * TODO add listeners
	 * 
	 * @return the edit menu
	 */
	private JMenu createEditMenu(){
		JMenu menuEdit = new JMenu("Edit");
		menuEdit.setMnemonic(KeyEvent.VK_E);
		 
		JMenuItem miUndo = new JMenuItem("Undo", new ImageIcon(imagePath + "edit_undo.gif"));
		miUndo.setMnemonic(KeyEvent.VK_U);
		miUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		menuEdit.add(miUndo);
		 
		JMenuItem miRedo = new JMenuItem("Redo", new ImageIcon(imagePath + "edit_redo.gif"));
		miRedo.setMnemonic(KeyEvent.VK_R);
		miRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		menuEdit.add(miRedo);
		 
		menuEdit.addSeparator();
		
		JMenuItem miCut = new JMenuItem("Cut", new ImageIcon(imagePath + "edit_cut16.gif"));
		miCut.setMnemonic(KeyEvent.VK_T);
		miCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		miCut.addActionListener(MenuListenerFactory.makeCutListener());
		menuEdit.add(miCut);
		 
		JMenuItem miCopy = new JMenuItem("Copy", new ImageIcon(imagePath + "edit_copy.gif"));
		miCopy.setMnemonic(KeyEvent.VK_C);
		miCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		miCopy.addActionListener(MenuListenerFactory.makeCopyListener());
		menuEdit.add(miCopy);
		 
		JMenuItem miPaste = new JMenuItem("Paste", new ImageIcon(imagePath + "edit_paste.gif"));
		miPaste.setMnemonic(KeyEvent.VK_V);
		miPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		menuEdit.add(miPaste);
		 
		JMenuItem miDelete = new JMenuItem("Delete", new ImageIcon(imagePath + "edit_delete.gif"));
		miDelete.setMnemonic(KeyEvent.VK_D);
		miDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		miDelete.addActionListener(MenuListenerFactory.makeDeleteListener());
		menuEdit.add(miDelete); 		 		 
		
		return menuEdit;
	}
	
	/**
	 * Assembles and returns the graph menu.
	 * 
	 * TODO This can all be done by CommandGroup in a few lines.
	 * @return the graph menu
	 */
	private JMenu createGraphMenu(){
		
		 
//		 TODO add listeners; NOT if commands are defined with a handleExecute method.
		JMenu menuGraph = new JMenu("Graph");
		 menuGraph.setMnemonic(KeyEvent.VK_G);
		 
		 JMenuItem miZoomIn = new JMenuItem("Zoom In", new ImageIcon(imagePath + "graphic_zoomin.gif"));
		 miZoomIn.setMnemonic(KeyEvent.VK_I);
		 miZoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miZoomIn);

		 JMenuItem miZoomOut = new JMenuItem("Zoom Out", new ImageIcon(imagePath + "graphic_zoomout.gif"));
		 miZoomOut.setMnemonic(KeyEvent.VK_O);
		 miZoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miZoomOut);
		 
		 JMenuItem miScaleBy = new JMenuItem("Scale By...", new ImageIcon(imagePath + "graphic_zoom.gif"));
		 miScaleBy.setMnemonic(KeyEvent.VK_S);
		 
		 // TODO Think up a memorable accelerator: ctrl+shift+S ?
		 // miScaleBy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miScaleBy);
		 CreateCommand cmd = new CreateCommand(drawingBoard);
		 cmd.export();
		 JMenuItem miCreate = cmd.createMenuItem();  // new JMenuItem("Create Nodes or Edges", new ImageIcon(imagePath + "graphic_create.gif"));		 
//		 miCreate.addActionListener(new ActionListener() {
//			 public void actionPerformed(ActionEvent arg0) {			  			
//				 // set the current drawing tool to the CreationTool
//				 drawingBoard.setTool(GraphDrawingView.CREATE);
//				 // TODO Use groups and command manager to toggle this tool selection				 
//			 }
//		 });
//		 
		 // miCreate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miCreate);

		 JMenuItem miModify = new JMenuItem("Modify Nodes, Edges or Labels", new ImageIcon(imagePath + "graphic_modify.gif"));
		 miModify.setMnemonic(KeyEvent.VK_M);
		 // miModify.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miModify);

		 JMenuItem miPrintArea = new JMenuItem("Select Print Area", new ImageIcon(imagePath + "graphic_printarea.gif"));
		 miPrintArea.setMnemonic(KeyEvent.VK_A);
		 // miPrintArea.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miPrintArea);

		 JMenuItem miMove = new JMenuItem("Move Graph", new ImageIcon(imagePath + "graphic_grab.gif")); // ??? is this the right image?
		 miMove.setMnemonic(KeyEvent.VK_V);
		 //miMove.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miMove);

		 JMenuItem miAllEdges = new JMenuItem("Select All Edges", new ImageIcon(imagePath + "graphic_alledges.gif"));
		 miAllEdges.setMnemonic(KeyEvent.VK_E);
		 //miAllEdges.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miAllEdges);

		 JMenuItem miAllLabels = new JMenuItem("Select All Labels", new ImageIcon(imagePath + "graphic_alllabels.gif"));
		 miAllLabels.setMnemonic(KeyEvent.VK_N);
		 //miAllNodes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miAllLabels);
		 return menuGraph;
	}
	
	private JMenu createOptionsMenu(){
//		 assemble the options menu
		JMenu menuOptions = new JMenu("Options");
		 menuOptions.setMnemonic(KeyEvent.VK_O);		 
		 
		 // NOTE these items are toggles, some are dependent.
		 // TODO export eps or tex should be mutually exclusive and disabled(?) if not using LaTeX for labels.
		 // TODO change the order of these items; move frequently used to top of list.
		 
//		 TODO add listeners; NOT if commands are defined with a handleExecute method.
		 JMenuItem miDrawBorder = new JCheckBoxMenuItem("Draw a border when exporting");
		 JMenuItem miStdNodeSize =  new JCheckBoxMenuItem("Use standard node size");
		 JMenuItem miUsePstricks =  new JCheckBoxMenuItem("Use pstricks in LaTeX output");
		 JMenuItem miUseLatex = new JCheckBoxMenuItem("Use LaTeX for Labels");
		 JMenuItem miExportEps = new JCheckBoxMenuItem("Export to EPS");
		 JMenuItem miExportTex = new JCheckBoxMenuItem("Export to TEX");		 
		 JMenuItem miErrReports = new JCheckBoxMenuItem("Send Error Reports");
		
		 // TODO add listeners; NOT if commands are defined with a handleExecute method.
		 menuOptions.add(miDrawBorder);
		 menuOptions.add(miStdNodeSize);
		 menuOptions.add(miUsePstricks);
		 menuOptions.add(miUseLatex);		 
		 menuOptions.add(miExportEps);
		 menuOptions.add(miExportTex);
		 menuOptions.add(miErrReports);
		 return menuOptions;
	}
	
	/**
	 * The views.
	 * TODO Load as plugins?
	 */
	private JTabbedPane tabbedViews;
	private GraphDrawingView drawingBoard;
	// TODO private JPanel filmStrip;
	private JPanel eventsView;
	private JPanel latexView;
	private FilmStrip filmStrip; // thumbnails of graphs for all open machines in the workspace

	public FilmStrip getFilmStrip() {
		return filmStrip;
	}

	/**
	 * Enables appropriate menus and tools depending on state of workspace.
	 */
	public void update() {
		// TODO Auto-generated method stub
		
		CommandManager commandManager = CommandManager.defaultInstance();
		CommandGroup graphGroup = commandManager.getGroup("graph.group");
		CommandGroup fileGroup = commandManager.getGroup("file.group");
		CommandGroup editGroup = commandManager.getGroup("edit.group");
		if(IDESWorkspace.instance().getActiveModel() == null){
			// TODO disable edit and graph command groups
			graphGroup.setEnabled(false);
			fileGroup.setEnabled(true);
			// enable file and options groups
		}else{
			// enable all commands
		}
		// TODO If active view is not the GraphDrawingView then disable the graph commands group
	}
}
