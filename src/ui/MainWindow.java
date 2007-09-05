/**
 * 
 */
package ui;

import io.fsa.ver2_1.FileOperations;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import main.Hub;
import main.Workspace;
import main.Main;
import main.WorkspaceMessage;
import main.WorkspaceSubscriber;

import org.pietschy.command.CommandManager;

import presentation.fsa.ContextAdaptorHack;
import presentation.fsa.EventView;
import presentation.fsa.FSAToolset;
import presentation.fsa.GraphDrawingView;
import ui.command.EditCommands;
import ui.command.FileCommands;
import ui.command.HelpCommands;
import ui.command.OperationsCommands;
import ui.command.OptionsCommands;
import ui.command.GraphCommands.AlignCommand;
import ui.command.GraphCommands.CreateCommand;
import ui.command.GraphCommands.DeleteCommand;
import ui.command.GraphCommands.MoveCommand;
import ui.command.GraphCommands.SelectCommand;
import ui.command.GraphCommands.TextCommand;

/**
 * The main window in which the application is displayed. Provides real estate
 * for all menus, toolbars, graph drawing and event set editing.
 * 
 * @author Helen Bretzke
 * @author Lenko Grigorov
 */
public class MainWindow extends JFrame implements WorkspaceSubscriber {

	String imagePath = "images/icons/";

	private static final int MINIMUM_WIDTH = 500;

	private static final int MINIMUM_HEIGHT = 500;

	private ZoomControl zoom = new ZoomControl();

	public MainWindow() {
		super(Hub.string("IDES_SHORT_NAME") + " " + Hub.string("IDES_VER"));
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Main.onExit();
			}
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setIconImage(new ImageIcon(Hub.getResource(imagePath + "logo.gif"))
				.getImage());
		Workspace.instance().addSubscriber(this); // subscribe to
													// notifications from the
													// workspace

		FileOperations.loadCommandManager("commands.xml");

		// drawingBoard = new GraphDrawingView();
		Hub.getWorkspace().addSubscriber(new ContextAdaptorHack());

		createAndAddMainPane();

		// TODO add graph spec, latex and eps views to the state model
		getContentPane().add(new StatusBar(), BorderLayout.SOUTH);

		loadAndExportCommands();
		createAndAddMenuBar();
		createAndAddToolBar();

		pack();
		// TODO uncomment line below before shipping
		// setExtendedState(MAXIMIZED_BOTH);

		// get the stored window size information and ensure it falls within
		// the current display
		int width = Hub.persistentData.getInt("mainWindowWidth");
		int height = Hub.persistentData.getInt("mainWindowHeight");
		int x = Hub.persistentData.getInt("mainWindowPosX");
		int y = Hub.persistentData.getInt("mainWindowPosY");

		// ensure that the stored dimensions fit on our display
		Rectangle gcRect = this.getGraphicsConfiguration().getBounds();
		width = (width > gcRect.width ? gcRect.width : width);
		height = (height > gcRect.height ? gcRect.height : height);
		width = (width < MINIMUM_WIDTH ? MINIMUM_WIDTH : width);
		height = (height < MINIMUM_HEIGHT ? MINIMUM_HEIGHT : height);

		if (x < 0)
			x = 0;
		if (y < 0)
			y = 0;
		x = (x + width > gcRect.width ? gcRect.width - width : x);
		y = (y + height > gcRect.height ? gcRect.height - height : y);
		setBounds(x, y, width, height);
	}

	private void createAndAddMainPane() {
		JPanel mainPane = new JPanel(new BorderLayout());
		tabbedViews = new JTabbedPane();
		rightView = new JTabbedPane();
		// drawingBoard.setName("No graph");
		// JScrollPane sp = new JScrollPane(drawingBoard,
		// JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		// JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		// sp.setName(drawingBoard.getName());
		//		
		// // TODO attach a listener to the tabbedPane that sets the active view
		// in the UIStateModel
		// tabbedViews.addTab("Graph",sp);
		// tabbedViews.addTab("Events", new EventView());
		tabsAndRight = new JSplitPane();
		tabsAndRight.setLeftComponent(tabbedViews);
		tabsAndRight.setRightComponent(rightView);
		mainPane.add(tabsAndRight, BorderLayout.CENTER);

		Box fsBox = Box.createHorizontalBox();
		filmStrip = new FilmStrip();
		fsBox.add(filmStrip);
		fsBox.add(Box.createHorizontalGlue());
		mainPane.add(new JScrollPane(fsBox,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), BorderLayout.SOUTH);

		getContentPane().add(mainPane, "Center");
	}

	private void createAndAddToolBar() {
		toolbar = CommandManager.defaultInstance().getGroup("ides.toolbar")
				.createToolBar();
		// toolbar.addSeparator();
		Box p = Box.createHorizontalBox();// new JPanel();
		p.add(new JLabel(" " + Hub.string("zoom") + ": "));
		p.add(zoom);
		p.add(Box.createHorizontalGlue());
		toolbar.add(p);
		getContentPane().add(toolbar, BorderLayout.PAGE_START);
	}

	private void createAndAddMenuBar() {
		JMenuBar menuBar = CommandManager.defaultInstance().getGroup(
				"ides.menu.group").createMenuBar();
		this.setJMenuBar(menuBar);
	}

	/**
	 * Dynamically loads and export all commands in package ui.command.
	 */
	private void loadAndExportCommands() {

		// Lenko: moved to constructor: needs to load for drawing board
		// FileOperations.loadCommandManager("commands.xml");

		new CreateCommand().export();
		new SelectCommand().export();
		new MoveCommand().export();
		new TextCommand().export();
		new DeleteCommand().export();
		new AlignCommand().export();

		new EditCommands.CutCommand().export();
		new EditCommands.CopyCommand().export();
		new EditCommands.PasteCommand().export();

		new FileCommands.NewCommand().export();
		new FileCommands.OpenCommand().export();
		new FileCommands.CloseCommand().export();
		new FileCommands.SaveCommand().export();
		new FileCommands.SaveAsCommand().export();
		new FileCommands.SaveAllCommand().export();

		new FileCommands.OpenWorkspaceCommand().export();
		new FileCommands.SaveWorkspaceCommand().export();
		new FileCommands.SaveWorkspaceAsCommand().export();

		new FileCommands.ImportCommand().export();
		new FileCommands.ExportCommand().export();
//		// new FileCommands.ExportToGIFCommand().export();
//		new FileCommands.ExportToEPSCommand().export();
//		new FileCommands.ExportToLatexCommand().export();
//		new FileCommands.ExportToGrailCommand().export();
//		new FileCommands.ExportToTCTCommand().export();
//		// new FileCommands.ExportToPNGCommand().export();
//		new FileCommands.ImportFromGrailCommand().export();
//		new FileCommands.ImportFromTCTCommand().export();
		new FileCommands.ExitCommand().export();

		new OptionsCommands.ShowGridCommand().export();
		new OptionsCommands.MoreOptionsCommand().export();

		new HelpCommands.AboutCommand().export();

		new OperationsCommands.ProductCommand().export();
	}

	/**
	 * The views.
	 */
	private JTabbedPane tabbedViews;

	private JTabbedPane rightView;

	private JSplitPane tabsAndRight;

	// private GraphDrawingView drawingBoard;
	private FilmStrip filmStrip; // thumbnails of graphs for all open
									// machines in the workspace

	private JToolBar toolbar;

	public FilmStrip getFilmStrip() {
		return filmStrip;
	}

	// /**
	// * TODO: fix this
	// * @return the top-left corner fo the drawing area
	// */
	// public Point getDrawingBoardDisplacement() {
	// return drawingBoard.getLocationOnScreen();
	// }
	//	
	// /**
	// * TODO: fix this
	// * @return background color of drawing board
	// */
	// public Color getDrawingBoardBGColor() {
	// return drawingBoard.getBackground();
	// }
	//	
	// //TODO: fix this
	// public GraphDrawingView getDrawingBoard() {
	// return drawingBoard;
	// }

	public ZoomControl getZoomControl() {
		return zoom;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see observer.WorkspaceSubscriber#modelCollectionChanged(observer.WorkspaceMessage)
	 */
	public void modelCollectionChanged(WorkspaceMessage message) {
		configureTools();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see observer.WorkspaceSubscriber#repaintRequired(observer.WorkspaceMessage)
	 */
	public void repaintRequired(WorkspaceMessage message) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see observer.WorkspaceSubscriber#modelSwitched(observer.WorkspaceMessage)
	 */
	public void modelSwitched(WorkspaceMessage message) {
	}

	/**
	 * Enables appropriate menus and tools depending on whether there is a model
	 * open in the workspace.
	 * 
	 */
	private void configureTools() {
		CommandManager commandManager = CommandManager.defaultInstance();

		if (Hub.getWorkspace().size() < 1) {
			// drawingBoard.setEnabled(false);
			zoom.setEnabled(false);
			toolbar.setEnabled(false);
			commandManager.getGroup("graph.group").setEnabled(false);
			// commandManager.getGroup("ides.toolbar.group").setEnabled(false);
			commandManager.getGroup("edit.group").setEnabled(false);
			commandManager.getGroup("file.group").setEnabled(true);
			// TODO disable save and close commands
			// TODO enable options groups

			// disable save commands
			// FIXME this doesn't work
			commandManager.getGroup("file.save.group").setEnabled(false);
		} else {
			// drawingBoard.setEnabled(true);
			zoom.setEnabled(true);
			// enable all commands except save commands which depend on the
			// dirty bit for the workspace and the acive s
			// commandManager.getGroup("ides.toolbar.group").setEnabled(true);
			toolbar.setEnabled(true);
			commandManager.getGroup("graph.group").setEnabled(true);
			commandManager.getGroup("edit.group").setEnabled(false);
		}
	}

	/**
	 * Store the window size with the persistent properties, then free up all
	 * screen resources used by this window.
	 * 
	 * @author Chris McAloney
	 */
	public void dispose() {
		// TODO look into setting this as a single entry in settings.ini rather
		// than four
		Rectangle r = getBounds();
		Hub.persistentData.setInt("mainWindowWidth", r.width);
		Hub.persistentData.setInt("mainWindowHeight", r.height);
		Hub.persistentData.setInt("mainWindowPosX", r.x);
		Hub.persistentData.setInt("mainWindowPosY", r.y);
		super.dispose();
	}

	public JTabbedPane getMainPane() {
		return tabbedViews;
	}

	public JTabbedPane getRightPane() {
		return rightView;
	}

	public void aboutToRearrangeViews() {
		if (rightView.getComponentCount() != 0) {
			// apply Math.ceil to avoid "creeping" of the divider
			Hub.persistentData.setInt("rightViewExt", (int) Math.ceil(1000
					* (float) (tabsAndRight.getDividerLocation() - tabsAndRight
							.getMinimumDividerLocation())
					/ (tabsAndRight.getMaximumDividerLocation() - tabsAndRight
							.getMinimumDividerLocation())));
		}
	}

	public void arrangeViews() {
		if (rightView.getComponentCount() == 0) {
			tabsAndRight.setDividerLocation(1d);
		} else {
			float ext = Hub.persistentData.getInt("rightViewExt", 750) / 1000f;
			tabsAndRight.setDividerLocation((int) (tabsAndRight
					.getMinimumDividerLocation() + ext
					* (tabsAndRight.getMaximumDividerLocation() - tabsAndRight
							.getMinimumDividerLocation())));
		}
	}
}
