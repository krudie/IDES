/**
 * 
 */
package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

import javax.swing.Action;
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
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.Hub;
import main.Workspace;
import main.Main;
import main.WorkspaceMessage;
import main.WorkspaceSubscriber;

import pluggable.ui.Toolset;
import pluggable.ui.UIDescriptor;
import presentation.Presentation;
import presentation.PresentationManager;
import presentation.fsa.ContextAdaptorHack;
import presentation.fsa.EventView;
import presentation.fsa.FSAToolset;
import presentation.fsa.GraphDrawingView;
import presentation.fsa.actions.GraphActions;
import services.latex.LatexManager;
import services.undo.UndoManager;
import ui.actions.EditActions;
import ui.actions.FileActions;
import ui.actions.HelpActions;
import ui.actions.OperationsActions;
import ui.actions.OptionsActions;

/**
 * The main window in which the application is displayed. Provides real estate
 * for all menus, toolbars, graph drawing and event set editing.
 * 
 * @author Helen Bretzke
 * @author Lenko Grigorov
 */
public class MainWindow extends JFrame implements WorkspaceSubscriber {

	String imagePath = "images/icons/";

	protected static final String UI_SETTINGS="MainWindow settings";
	
	private static final int MINIMUM_WIDTH = 500;

	private static final int MINIMUM_HEIGHT = 500;
	
	protected Set<Action> disabledOnNoProject=new HashSet<Action>();
	
	protected JMenuBar menuBar;

	private ZoomControl zoom = new ZoomControl();
	
	Box zoomSelector;

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

//		FileOperations.loadCommandManager("commands.xml");

		// drawingBoard = new GraphDrawingView();
		Hub.getWorkspace().addSubscriber(new ContextAdaptorHack());

		createAndAddMainPane();

		// TODO add graph spec, latex and eps views to the state model
		getContentPane().add(new StatusBar(), BorderLayout.SOUTH);

		setupActions();
		createAndAddMenuBar();
		createAndAddToolBar();

		pack();

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
		ChangeListener tabIdxRecorder=new ChangeListener(){
			public void stateChanged(ChangeEvent e)
			{
				if((e.getSource() instanceof JTabbedPane)&&((JTabbedPane)e.getSource()).getSelectedIndex()<0)
				{
					return;
				}
				Hub.getWorkspace().getActiveModel().setAnnotation(UI_SETTINGS,new UILayout(tabbedViews.getSelectedIndex(),rightViews.getSelectedIndex()));
			}
		};
		tabbedViews = new JTabbedPane();
		tabbedViews.addChangeListener(tabIdxRecorder);
		rightViews = new JTabbedPane();
		rightViews.addChangeListener(tabIdxRecorder);
		tabsAndRight = new JSplitPane();
		tabsAndRight.setLeftComponent(tabbedViews);
		tabsAndRight.setRightComponent(rightViews);
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

	Action newAction;
	Action openAction;
	Action saveAction;
	Action saveasAction;
	Action saveallAction;
	Action closeAction;
	Action wopenAction;
	Action wsaveAction;
	Action wsaveasAction;
	Action importAction;
	Action exportAction;
	Action exitAction;
	Action undoAction;
	Action redoAction;
	Action operationsAction;
	Action latexAction;
	Action optionsAction;
	Action aboutAction;
	
	private void setupActions()
	{
		//Create actions
		newAction=new FileActions.NewAction();
		openAction=new FileActions.OpenAction();
		saveAction=new FileActions.SaveAction();
		saveasAction=new FileActions.SaveAsAction();
		saveallAction=new FileActions.SaveAllAction();
		closeAction=new FileActions.CloseAction();
		wopenAction=new FileActions.OpenWorkspaceAction();
		wsaveAction=new FileActions.SaveWorkspaceAction();
		wsaveasAction=new FileActions.SaveWorkspaceAsAction();
		importAction=new FileActions.ImportAction();
		exportAction=new FileActions.ExportAction();
		exitAction=new FileActions.ExitAction();
		undoAction=new UndoManager.UndoAction();
		redoAction=new UndoManager.RedoAction();
		operationsAction=new OperationsActions.ShowDialogAction();
		latexAction=new services.latex.UseLatexAction();
		optionsAction=new OptionsActions.MoreOptionsAction();
		aboutAction=new HelpActions.AboutAction();

		//decide which ones will be disabled when there's no model open
		disabledOnNoProject.add(saveAction);
		disabledOnNoProject.add(saveasAction);
		disabledOnNoProject.add(saveallAction);
		disabledOnNoProject.add(closeAction);
		disabledOnNoProject.add(exportAction);
		disabledOnNoProject.add(undoAction);
		disabledOnNoProject.add(redoAction);
	}
	
	private void createAndAddMenuBar() {
		
		//New implementation of the menus
		//Initialize the categories in the menu.
		JMenu fileMenu = new JMenu(Hub.string("menuFile"));
		JMenu editMenu = new JMenu(Hub.string("menuEdit"));
		JMenu operationsMenu=new JMenu(Hub.string("menuOperations"));
		JMenu optionsMenu = new JMenu(Hub.string("menuOptions"));
		JMenu helpMenu = new JMenu(Hub.string("menuHelp"));

		//Initializing the menu items in the "fileMenu"
		JMenuItem newModel = new JMenuItem(newAction);
		newModel.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_N, ActionEvent.CTRL_MASK));

		JMenuItem openModel = new JMenuItem(openAction);
		openModel.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O, ActionEvent.CTRL_MASK));

		JMenuItem saveModel = new JMenuItem(saveAction);
		saveModel.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, ActionEvent.CTRL_MASK));

		JMenuItem saveModelAs = new JMenuItem(saveasAction);

		JMenuItem saveAllModels = new JMenuItem(saveallAction);
		saveAllModels.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));

		JMenuItem closeModel = new JMenuItem(closeAction);
		closeModel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));

		JMenuItem openWorkspace = new JMenuItem(wopenAction);
		openWorkspace.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));

		JMenuItem saveWorkspace = new JMenuItem(wsaveAction);
		saveWorkspace.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));

		JMenuItem saveWorkspaceAs = new JMenuItem(wsaveasAction);

		JMenuItem importModel = new JMenuItem(importAction);

		JMenuItem exportModel = new JMenuItem(exportAction);

		JMenuItem exitIDES = new JMenuItem(exitAction);

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
		JMenuItem undo = new JMenuItem(Hub.string("undo"));
		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		undo.addActionListener(undoAction);
		//CommandManager_new.getInstance().setUndoMenu(undo);
		JMenuItem redo = new JMenuItem(Hub.string("redo"));
		redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		redo.addActionListener(redoAction);
		//CommandManager_new.getInstance().setRedoAction(redo);
		//Initialize the undo manager
		UndoManager.init(undo, redo);
		//Adding the menu items to the "editMenu"
		editMenu.add(undo);
		editMenu.add(redo);

		//adding the menu items to the "operationsMenu"
		operationsMenu.add(operationsAction);
		
		//Initializing the menu items for the "optionsMenu"
		JCheckBoxMenuItem useLaTeX  = new JCheckBoxMenuItem(latexAction);
		LatexManager.getUIBinder().bind(useLaTeX);
		JMenuItem moreOptions = new JMenuItem(new OptionsActions.MoreOptionsAction());
		//adding the menu items to the "optionsMenu"
		optionsMenu.add(useLaTeX);
		optionsMenu.addSeparator();
		optionsMenu.add(moreOptions);

		//adding the menu items to the "helpMenu"
		JMenuItem aboutIDES = new JMenuItem(aboutAction);
		helpMenu.add(aboutIDES);


		menuBar = new JMenuBar(); 
		//Adding the main categories to the menu
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(operationsMenu);
		menuBar.add(optionsMenu);
		menuBar.add(helpMenu);
	}

	private void createAndAddToolBar() {
//		toolbar = CommandManager_new.getInstance().getToolBar();
		toolbar = new JToolBar();
		toolbar.add(newAction);
		toolbar.add(openAction);
		toolbar.add(saveAction);
		toolbar.add(saveallAction);
		toolbar.addSeparator();
		toolbar.add(wopenAction);
		toolbar.add(wsaveAction);
		toolbar.addSeparator();
//		toolbar.add(new GraphCommands.SelectTool());
//		toolbar.add(new GraphCommands.CreateTool());
//		toolbar.add(new GraphCommands.MoveTool());
//		toolbar.addSeparator();
//		toolbar.add(new GraphCommands.AlignTool());
		zoomSelector = Box.createHorizontalBox();// new JPanel();
		zoomSelector.add(new JLabel(" " + Hub.string("zoom") + ": "));
		zoomSelector.add(zoom);
//		p.add(Box.createHorizontalGlue());
//		toolbar.add(z);
	}
	
	private void hotPlugMenus(JMenu[] menus)
	{
		JMenuBar newMenu=new JMenuBar();
		newMenu.add(menuBar.getMenu(0));
		// once you add a menu to another bar, it's removed from the previous bar
		newMenu.add(menuBar.getMenu(0));
		for(JMenu menu:menus)
		{
			newMenu.add(menu);
		}
		newMenu.add(menuBar.getMenu(menuBar.getMenuCount()-3));
		newMenu.add(menuBar.getMenu(menuBar.getMenuCount()-2));
		newMenu.add(menuBar.getMenu(menuBar.getMenuCount()-1));
		menuBar=newMenu;
		this.setJMenuBar(menuBar);
	}
	
	private void hotPlugToolbar(JToolBar tb, boolean showZoomControl)
	{
		getContentPane().remove(toolbar);
		JToolBar newToolbar=new JToolBar();
		for(int i=0;i<8;++i)
		{
			// once you add a button to another bar, it's removed from the previous bar
			newToolbar.add(toolbar.getComponentAtIndex(0));
		}
		if(showZoomControl)
		{
			newToolbar.add(zoomSelector);
			newToolbar.addSeparator();
		}
		int tbSize=tb.getComponentCount();
		for(int i=0;i<tbSize;++i)
		{
			// once you add a button to another bar, it's removed from the previous bar
			newToolbar.add(tb.getComponentAtIndex(0));
		}
		toolbar=newToolbar;
		toolbar.setFloatable(false);
		getContentPane().add(toolbar, BorderLayout.PAGE_START);
	}

	/**
	 * The views.
	 */
	private JTabbedPane tabbedViews;

	private JTabbedPane rightViews;

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
//		reconfigureUI();
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
		reconfigureUI();
	}

	/**
	 * Enables appropriate menus and tools depending on whether there is a model
	 * open in the workspace.
	 * 
	 */
	private void reconfigureUI() {
		if (Hub.getWorkspace().getActiveModel()==null) {
			zoom.setEnabled(false);
			for(Action a:disabledOnNoProject)
			{
				a.setEnabled(false);
			}
			hotPlugMenus(new JMenu[0]);
			hotPlugToolbar(new JToolBar(),false);
		} else {
			zoom.setEnabled(true);
			for(Action a:disabledOnNoProject)
			{
				a.setEnabled(true);
			}
			//getting the tabLayout has to be before adding the tabs because it will be overwritten
			UILayout tabLayout=(UILayout)Hub.getWorkspace().getActiveModel().getAnnotation(UI_SETTINGS);
			UIDescriptor uid=Hub.getWorkspace().getActiveUID();
			for(Presentation p:uid.getMainPanePresentations())
			{
				tabbedViews.add(p.getName(),p.getGUI());
			}
			for(Presentation p:uid.getRightPanePresentations())
			{
				rightViews.add(p.getName(),p.getGUI());
			}
			if(tabLayout!=null)
			{
				if(tabLayout.activeMainTab>-1&&tabLayout.activeMainTab<tabbedViews.getTabCount())
				{
					tabbedViews.setSelectedIndex(tabLayout.activeMainTab);
				}
				if(tabLayout.activeRightTab>-1&&tabLayout.activeRightTab<rightViews.getTabCount())
				{
					rightViews.setSelectedIndex(tabLayout.activeRightTab);
				}
			}
			arrangeViews();
			hotPlugMenus(uid.getMenus());
			hotPlugToolbar(uid.getToolbar(),uid.showZoomControl());
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
		return rightViews;
	}

	public void aboutToRearrangeViews() {
		if (rightViews.getComponentCount() != 0) {
			// apply Math.ceil to avoid "creeping" of the divider
			Hub.persistentData.setInt("rightViewExt", (int) Math.ceil(1000
					* (float) (tabsAndRight.getDividerLocation() - tabsAndRight
							.getMinimumDividerLocation())
							/ (tabsAndRight.getMaximumDividerLocation() - tabsAndRight
									.getMinimumDividerLocation())));
		}
	}

	public void arrangeViews() {
		if (rightViews.getComponentCount() == 0) {
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
