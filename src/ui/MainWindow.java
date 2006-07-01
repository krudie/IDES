/**
 * 
 */
package ui;

import io.fsa.ver1.FileOperations;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import main.Hub;
import main.IDESWorkspace;
import main.Main;
import model.Subscriber;

import org.pietschy.command.CommandManager;

import presentation.fsa.GraphDrawingView;

import ui.command.EditCommands;
import ui.command.FileCommands;
import ui.command.OptionsCommands;
import ui.command.GraphCommands.*;

/**
 * TODO Reimplement using gui-commands library.
 * Commands generate their own toolbuttons and menuitems.
 * Load configuration from a file.
 * 
 * @author helen bretzke
 *
 */
public class MainWindow extends JFrame implements Subscriber {

	String imagePath = "images/icons/";
	
	private ZoomControl zoom=new ZoomControl();
	
	public MainWindow() {
		super(Hub.string("IDES_LONG_NAME")+" "+Hub.string("IDES_VER"));
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		    	Main.onExit();
		    }
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setIconImage(new ImageIcon(Hub.getResource(imagePath + "logo.gif")).getImage());
		IDESWorkspace.instance().attach(this);  // subscribe to updates from the workspace
	
		drawingBoard = new GraphDrawingView();		
////		 Get the screen dimensions.
//	    Toolkit tk = Toolkit.getDefaultToolkit ();
//	    Dimension screen = tk.getScreenSize();
//	    setExtendedState(MAXIMIZED_BOTH);
////	    setSize(screen.width, screen.height);
////		drawingBoard.setPreferredSize(new Dimension(screen.width, screen.height));//(int)(getSize().width * 0.7), (int) (getSize().height*0.7)));
		createAndAddTabbedPane();				
		
		// TODO add graph spec, latex and eps views to the state model		
		filmStrip = new FilmStrip();	
		filmStrip.setSize(new Dimension((int)(getSize().width * 0.9), (int)(getSize().height * 0.3)));
		getContentPane().add(filmStrip, BorderLayout.SOUTH);
	
		//FileOperations.loadAndExportCommands("commands.txt"); 
		loadAndExportCommands();
		createAndAddMenuBar();
		createAndAddToolBar();
		update();
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pack();
	    setExtendedState(MAXIMIZED_BOTH);
	}
	
	 private void createAndAddTabbedPane() {
		tabbedViews = new JTabbedPane();
		drawingBoard.setName("No graph");
		JScrollPane sp = new JScrollPane(drawingBoard, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		sp.setName(drawingBoard.getName());
		
		// TODO attach a listener to the tabbedPane that sets the active view in the UIStateModel
		tabbedViews.addTab("Graph",sp);
		tabbedViews.addTab("Events", new EventView());
		getContentPane().add(tabbedViews, "Center");
	}

	 private void createAndAddToolBar() {
		 toolbar =CommandManager.defaultInstance().getGroup("ides.toolbar").createToolBar();
		 toolbar.addSeparator();
		 Box p=Box.createHorizontalBox();//new JPanel();
		 p.add(new JLabel(Hub.string("zoom")+": "));
		 p.add(zoom);
		 p.add(Box.createHorizontalGlue());
		 toolbar.add(p);
		 getContentPane().add(toolbar, BorderLayout.PAGE_START);

		 // Create a vertical toolbar
//		 toolbar =  CommandManager.defaultInstance().getGroup("ides.toolbar.group").createToolBar(); //"ides.toolbar.group").createToolBar(); // new JToolBar();
//		 toolbar.setRollover(true);
//		 toolbar.setOrientation(JToolBar.VERTICAL);
//		 this.getContentPane().add(toolbar, BorderLayout.WEST);		 	    
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
		 this.setJMenuBar(menuBar);
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
		new MoveCommand(drawingBoard).export();
		new TextCommand(drawingBoard).export();
		new DeleteCommand(drawingBoard).export();
		
		new EditCommands.CutCommand().export();
		new EditCommands.CopyCommand().export();
		new EditCommands.PasteCommand().export();
			
		new FileCommands.NewAutomatonCommand().export();
		new FileCommands.OpenAutomatonCommand().export();
		new FileCommands.CloseAutomatonCommand().export();
		new FileCommands.SaveAutomatonCommand().export();		
		new FileCommands.SaveAutomatonAsCommand().export();
		new FileCommands.SaveAllAutomataCommand().export();
		
		new FileCommands.OpenWorkspaceCommand().export();		
		new FileCommands.SaveWorkspaceCommand().export();
		new FileCommands.SaveWorkspaceAsCommand().export();
		
		new FileCommands.ExportToGIFCommand().export();
		new FileCommands.ExportToLatexCommand().export();
		new FileCommands.ExportToPNGCommand().export();
		new FileCommands.ExitCommand().export();
		
		//moved to LatexManager
		//new OptionsCommands.UseLatexCommand().export();
		new OptionsCommands.MoreOptionsCommand().export();
			
	}
	
	/**
	 * The views.
	 */
	private JTabbedPane tabbedViews;
	private GraphDrawingView drawingBoard;
	private FilmStrip filmStrip; // thumbnails of graphs for all open machines in the workspace
	private JToolBar toolbar;
	
	
	public FilmStrip getFilmStrip() {
		return filmStrip;
	}

	/**
	 * Enables appropriate menus and tools depending on state of workspace.
	 */
	public void update() {		
		CommandManager commandManager = CommandManager.defaultInstance();
		
		if(IDESWorkspace.instance().getActiveModel() == null){
			drawingBoard.setEnabled(false);
			zoom.setEnabled(false);
			toolbar.setEnabled(false);
			commandManager.getGroup("graph.group").setEnabled(false);
			//commandManager.getGroup("ides.toolbar.group").setEnabled(false);
			commandManager.getGroup("edit.group").setEnabled(false);			
			commandManager.getGroup("file.group").setEnabled(true);
			// TODO disable save and close commands
			// TODO enable options groups
			
			// disable save commands
			// FIXME this doesn't work
			commandManager.getGroup("file.save.group").setEnabled(false);
		}else{
			drawingBoard.setEnabled(true);
			zoom.setEnabled(true);
			// enable all commands except save commands which depend on the dirty bit for the workspace and the acive automaton
			//commandManager.getGroup("ides.toolbar.group").setEnabled(true);
			toolbar.setEnabled(true);
			commandManager.getGroup("graph.group").setEnabled(true);
			commandManager.getGroup("edit.group").setEnabled(false);
//			if(IDESWorkspace.instance().isDirty()){
//				// KLUGE
//				commandManager.getCommand("save.automaton.command").setEnabled(true);
//				// FIXME this doesn't work
//				commandManager.getGroup("file.save.group").setEnabled(true);
//			}
			// set the name of the current model in the tabbed pane
			//tabbedViews.setTitleAt(0, IDESWorkspace.instance().getActiveModelName());
		}
		// TODO If active view is not the GraphDrawingView then disable the graph commands group and toolbar
		
		//pack();
	}

	/**
	 * TODO: fix this
	 * @return the top-left corner fo the drawing area
	 */
	public Point getDrawingBoardDisplacement()
	{
		return drawingBoard.getLocationOnScreen();
	}
	
	/**
	 * TODO: fix this
	 * @return background color of drawing board
	 */
	public Color getDrawingBoardBGColor()
	{
		return drawingBoard.getBackground();
	}
	
	//TODO: fix this
	public GraphDrawingView getDrawingBoard()
	{
		return drawingBoard;
	}
	
	public ZoomControl getZoomControl()
	{
		return zoom;
	}
}
