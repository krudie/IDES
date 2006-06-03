/**
 * 
 */
package ui;

import io.fsa.ver1.FileOperations;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

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

import main.IDESWorkspace;
import main.SystemVariables;
import model.Subscriber;

import org.pietschy.command.CommandManager;

import ui.command.EditCommands;
import ui.command.FileCommands;
import ui.command.GraphCommands.CreateCommand;
import ui.command.GraphCommands.MoveCommand;
import ui.command.GraphCommands.SelectCommand;

/**
 * TODO Reimplement using gui-commands library.
 * Commands generate their own toolbuttons and menuitems.
 * Load configuration from a file.
 * 
 * @author helen bretzke
 *
 */
public class MainWindow extends JFrame implements Subscriber {

	String imagePath = SystemVariables.instance().getApplication_path() + "/src/images/icons/";
	
	public MainWindow() {
		super("Integrated Discrete-Event System Software 2.1");
		setIconImage(new ImageIcon(imagePath + "logo.gif").getImage());
		IDESWorkspace.instance().attach(this);  // subscribe to updates from the workspace
	
		drawingBoard = new GraphDrawingView();		
//		 Get the screen dimensions.
	    Toolkit tk = Toolkit.getDefaultToolkit ();
	    Dimension screen = tk.getScreenSize();
	    setSize(screen.width, screen.height);
		drawingBoard.setPreferredSize(new Dimension((int)(getSize().width * 0.8), (int) (getSize().height*0.7)));
		createAndAddTabbedPane();
				
		
		// TODO add graph spec, latex and eps views to the state model		
		filmStrip = new FilmStrip();		
		getContentPane().add(filmStrip, BorderLayout.SOUTH);
	
		//FileOperations.loadAndExportCommands("commands.txt"); 
		loadAndExportCommands();
		createAndAddMenuBar();
		createAndAddToolBar();
		update();
		
		// TODO fire exit.command to make sure that system variables are saved
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();		
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
		 JToolBar toolbar =  CommandManager.defaultInstance().getGroup("graph.group").createToolBar(); //"ides.toolbar.group").createToolBar(); // new JToolBar();
		 toolbar.setRollover(true);
		 toolbar.setOrientation(JToolBar.VERTICAL);
		 this.getContentPane().add(toolbar, BorderLayout.EAST);	    
	 } 
	 
	private void createAndAddMenuBar() {
	 	 
		 /**
		  * Menu components
		  * 
		  * FIXME 
		  * Dynamically load and export all commands in 
		  * package ui.command.
		  * ??? This is tricky for file commands since need name and reference to command manager and filter.
		  */
				 
		 JMenuBar menuBar = CommandManager.defaultInstance().getGroup("ides.menu.group").createMenuBar(); // new JMenuBar();
	 	 
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

		FileOperations.loadCommandManager("commands.xml");
		
		new CreateCommand(drawingBoard).export();
		new SelectCommand(drawingBoard).export();
		new MoveCommand().export();
		
		new EditCommands.DeleteCommand().export();
		new EditCommands.CutCommand().export();
		new EditCommands.CopyCommand().export();
		new EditCommands.PasteCommand().export();
		
		new FileCommands.OpenAutomatonCommand().export();
		new FileCommands.CloseAutomatonCommand().export();
		new FileCommands.SaveAutomatonCommand().export();		
		new FileCommands.OpenWorkspaceCommand().export();		
		new FileCommands.SaveWorkspaceCommand().export();
		new FileCommands.ExportToGIFCommand().export();
		new FileCommands.ExportToLatexCommand().export();
		new FileCommands.ExportToPNGCommand().export();
		new FileCommands.ExitCommand().export();
			
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
	
	/**
	 * TODO Move these into commands.xml and load into menu.
	 * 
	 * @return
	 */
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
		CommandManager commandManager = CommandManager.defaultInstance();
		
		if(IDESWorkspace.instance().getActiveModel() == null){
			// TODO disable edit command group
			commandManager.getGroup("graph.group").setEnabled(false);
			commandManager.getGroup("ides.toolbar.group").setEnabled(false);
			commandManager.getGroup("edit.group").setEnabled(false);
			
			commandManager.getGroup("file.group").setEnabled(true);
			// TODO enable options groups
			
			// disable save commands
			// FIXME this doesn't work
			commandManager.getGroup("file.save.group").setEnabled(false);
		}else{
			// enable all commands except save commands which depend on the dirty bit for the workspace and the acive automaton
			commandManager.getGroup("ides.toolbar.group").setEnabled(true);
			commandManager.getGroup("graph.group").setEnabled(true);
			commandManager.getGroup("edit.group").setEnabled(true);
		}
		// TODO If active view is not the GraphDrawingView then disable the graph commands group and toolbar
		
		pack();
	}
}
