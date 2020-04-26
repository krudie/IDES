/**
 * 
 */
package ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
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
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import ides.api.core.Hub;
import ides.api.core.UserInterface;
import ides.api.core.WorkspaceMessage;
import ides.api.core.WorkspaceSubscriber;
import ides.api.plugin.presentation.CopyPastePresentation;
import ides.api.plugin.presentation.GlobalFontSizePresentation;
import ides.api.plugin.presentation.Presentation;
import ides.api.plugin.presentation.UIDescriptor;
import ides.api.plugin.presentation.ZoomablePresentation;
import ides.api.ui.FontSizeSelector;
import ides.api.ui.ZoomControl;
import main.Main;
import presentation.fsa.ContextAdaptorHack;
import services.ccp.CopyPasteAction;
import services.latex.LatexBackend;
import services.notice.NoticeBoard;
import services.notice.NoticePopup;
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
public class MainWindow extends JFrame implements WorkspaceSubscriber, UserInterface, TabbedWindow {

    /**
     * 
     */
    private static final long serialVersionUID = 3889527073050548206L;

    private String imagePath = "images/icons/";

    protected static final String UI_SETTINGS = "MainWindow settings";

    private static final int MINIMUM_WIDTH = 500;

    private static final int MINIMUM_HEIGHT = 500;

    protected Set<Action> disabledOnNoModel = new HashSet<Action>();

    protected Set<Action> disabledOnParentModel = new HashSet<Action>();

    protected Set<Action> disableOnNoParentModel = new HashSet<Action>();

    protected JMenuBar menuBar;

    private ZoomControl zoom = new ZoomControl();

    private FontSizeSelector font = new FontSizeSelector();

    private Box zoomSelector;

    private Box fontSelector;

    private JButton copyButton = new JButton();

    private JButton pasteButton = new JButton();

    private AnnotationTab at = null;

    public MainWindow() {
        super(Hub.string("IDES_SHORT_NAME") + " " + Hub.string("IDES_VER"));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.onExit();
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setIconImage(new ImageIcon(Hub.getIDESResource(imagePath + "logo.gif")).getImage());
        Hub.getWorkspace().addSubscriber(this); // subscribe to
        // notifications from the
        // workspace

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                arrangeViews();
            }
        });

        // FileOperations.loadCommandManager("commands.xml");

        // drawingBoard = new GraphDrawingView();
        Hub.getWorkspace().addSubscriber(new ContextAdaptorHack());

        createAndAddMainPane();

        // TODO add graph spec, latex and eps views to the state model
        statusBar = new StatusBar();
        getContentPane().add(statusBar, BorderLayout.SOUTH);

        setupActions();
        createAndAddMenuBar();
        createAndAddToolBar();

        pack();

        // get the stored window size information and ensure it falls within
        // the current display
        int width = Hub.getPersistentData().getInt("mainWindowWidth");
        int height = Hub.getPersistentData().getInt("mainWindowHeight");
        int x = Hub.getPersistentData().getInt("mainWindowPosX");
        int y = Hub.getPersistentData().getInt("mainWindowPosY");

        // ensure that the stored dimensions fit on our display
        Rectangle gcRect = this.getGraphicsConfiguration().getBounds();
        width = (width > gcRect.width ? gcRect.width : width);
        height = (height > gcRect.height ? gcRect.height : height);
        width = (width < MINIMUM_WIDTH ? MINIMUM_WIDTH : width);
        height = (height < MINIMUM_HEIGHT ? MINIMUM_HEIGHT : height);

        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        x = (x + width > gcRect.width ? gcRect.width - width : x);
        y = (y + height > gcRect.height ? gcRect.height - height : y);
        setBounds(x, y, width, height);
    }

    private void createAndAddMainPane() {
        JPanel mainPane = new JPanel(new BorderLayout());
        tabbedViews = new JTabbedPane();
        rightViews = new JTabbedPane();
        leftViews = new JTabbedPane();
        tabsAndRight = new JSplitPane();
        tabsAndRight.setLeftComponent(tabbedViews);
        tabsAndRight.setRightComponent(rightViews);
        mainPane.add(tabsAndRight, BorderLayout.CENTER);

        Box fsBox = Box.createHorizontalBox();
        filmStrip = new FilmStrip();
        fsBox.add(filmStrip);
        fsBox.add(Box.createHorizontalGlue());
        mainPane.add(new JScrollPane(fsBox, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS), BorderLayout.SOUTH);

        getContentPane().add(mainPane, "Center");
    }

    protected Action newAction;

    protected Action openAction;

    protected Action saveAction;

    protected Action saveasAction;

    protected Action saveallAction;

    protected Action closeAction;

    protected Action wopenAction;

    protected Action wsaveAction;

    protected Action wsaveasAction;

    protected Action importAction;

    protected Action exportAction;

    protected Action exitAction;

    protected Action undoAction;

    protected Action redoAction;

    protected Action operationsAction;

    protected Action latexAction;

    protected Action optionsAction;

    protected Action pluginsAction;

    protected Action aboutAction;

    protected Action renameAction;

    protected Action ccpAction;

    protected Action goToParentAction;

    protected JMenu helpTopics;

    private void setupActions() {
        // Create actions
        newAction = new FileActions.NewAction();
        openAction = new FileActions.OpenAction();
        saveAction = new FileActions.SaveAction();
        saveasAction = new FileActions.SaveAsAction();
        saveallAction = new FileActions.SaveAllAction();
        closeAction = new FileActions.CloseAction();
        wopenAction = new FileActions.OpenWorkspaceAction();
        wsaveAction = new FileActions.SaveWorkspaceAction();
        wsaveasAction = new FileActions.SaveWorkspaceAsAction();
        importAction = new FileActions.ImportAction();
        exportAction = new FileActions.ExportAction();
        exitAction = new FileActions.ExitAction();
        undoAction = Hub.getUndoManager().getUndoAction();
        redoAction = Hub.getUndoManager().getRedoAction();
        renameAction = new EditActions.RenameAction();
        goToParentAction = new EditActions.GoToParentAction();
        operationsAction = new OperationsActions.ShowDialogAction();
        latexAction = new services.latex.UseLatexAction();
        optionsAction = new OptionsActions.MoreOptionsAction();
        helpTopics = new HelpActions.HelpTopics();
        pluginsAction = new HelpActions.PluginsAction();
        aboutAction = new HelpActions.AboutAction();
        ccpAction = new CopyPasteAction();

        // decide which ones will be disabled when there's no model open
        disabledOnNoModel.add(saveAction);
        disabledOnNoModel.add(saveasAction);
        disabledOnNoModel.add(saveallAction);
        disabledOnNoModel.add(closeAction);
        disabledOnNoModel.add(exportAction);
        disabledOnNoModel.add(undoAction);
        disabledOnNoModel.add(redoAction);
        disabledOnNoModel.add(renameAction);
        disabledOnNoModel.add(goToParentAction);

        // decide which ones will be disabled when the active model has a parent
        // model
        disabledOnParentModel.add(renameAction);
        // decide which ones will be hidden when the active model does not have
        // a parent
        // model
        disableOnNoParentModel.add(goToParentAction);
    }

    private void createAndAddMenuBar() {

        // New implementation of the menus
        // Initialize the categories in the menu.
        JMenu fileMenu = new JMenu(Hub.string("menuFile"));
        JMenu editMenu = new JMenu(Hub.string("menuEdit"));
        JMenu operationsMenu = new JMenu(Hub.string("menuOperations"));
        JMenu optionsMenu = new JMenu(Hub.string("menuOptions"));
        JMenu helpMenu = new JMenu(Hub.string("menuHelp"));

        // Initializing the menu items in the "fileMenu"
        JMenuItem newModel = new JMenuItem(newAction);
        newModel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));

        JMenuItem openModel = new JMenuItem(openAction);
        openModel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));

        JMenuItem saveModel = new JMenuItem(saveAction);
        saveModel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));

        JMenuItem saveModelAs = new JMenuItem(saveasAction);

        JMenuItem saveAllModels = new JMenuItem(saveallAction);
        saveAllModels
                .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));

        JMenuItem closeModel = new JMenuItem(closeAction);
        closeModel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));

        JMenuItem openWorkspace = new JMenuItem(wopenAction);
        openWorkspace
                .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));

        JMenuItem saveWorkspace = new JMenuItem(wsaveAction);
        saveWorkspace
                .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));

        JMenuItem saveWorkspaceAs = new JMenuItem(wsaveasAction);

        JMenuItem importModel = new JMenuItem(importAction);

        JMenuItem exportModel = new JMenuItem(exportAction);

        JMenuItem exitIDES = new JMenuItem(exitAction);

        // Adding the menu items to the "fileMenu"
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

        // Initializing the menu items for the "editMenu"
        JMenuItem undo = new JMenuItem(Hub.string("undo"));
        undo.setIcon(new ImageIcon(
                Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/edit_undo.gif"))));
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        undo.setToolTipText(Hub.string("comHintUndo"));
        undo.addActionListener(undoAction);
        JMenuItem redo = new JMenuItem(Hub.string("redo"));
        redo.setIcon(new ImageIcon(
                Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/edit_redo.gif"))));
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        redo.setToolTipText(Hub.string("comHintRedo"));
        redo.addActionListener(redoAction);

        JMenuItem cut = new JMenuItem(Hub.string("cut"));
        cut.setIcon(new ImageIcon(
                Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/edit_cut16.gif"))));
        cut.setActionCommand(Hub.string("cut"));
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        cut.addActionListener(ccpAction);

        JMenuItem copy = new JMenuItem(Hub.string("copy"));
        copy.setIcon(new ImageIcon(
                Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/edit_copy.gif"))));
        copy.setActionCommand(Hub.string("copy"));
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        copy.addActionListener(ccpAction);

        JMenuItem paste = new JMenuItem(Hub.string("paste"));
        paste.setIcon(new ImageIcon(
                Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/edit_paste.gif"))));
        paste.setActionCommand(Hub.string("paste"));
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        paste.addActionListener(ccpAction);

        // Bind to the undo manager
        Hub.getUndoManager().bindUndo(undo);
        Hub.getUndoManager().bindRedo(redo);
        // Bind to the cut/copy/paste (CCP) manager
        Hub.getCopyPasteManager().bindCutCopy(cut);
        Hub.getCopyPasteManager().bindCutCopy(copy);
        Hub.getCopyPasteManager().bindPaste(paste);

        // Adding the menu items to the "editMenu"
        JMenuItem rename = new JMenuItem(renameAction);
        editMenu.add(undo);
        editMenu.add(redo);
        editMenu.addSeparator();
        editMenu.add(cut);
        editMenu.add(copy);
        editMenu.add(paste);
        editMenu.addSeparator();
        editMenu.add(rename);
        editMenu.add(goToParentAction);

        // adding the menu items to the "operationsMenu"
        operationsMenu.add(operationsAction);

        // Initializing the menu items for the "optionsMenu"
        JCheckBoxMenuItem useLaTeX = new JCheckBoxMenuItem(latexAction);
        LatexBackend.getUIBinder().bind(useLaTeX);
        JMenuItem moreOptions = new JMenuItem(new OptionsActions.MoreOptionsAction());
        // adding the menu items to the "optionsMenu"
        optionsMenu.add(useLaTeX);
        optionsMenu.addSeparator();
        optionsMenu.add(moreOptions);

        // adding the menu items to the "helpMenu"
        JMenuItem viewPlugins = new JMenuItem(pluginsAction);
        JMenuItem aboutIDES = new JMenuItem(aboutAction);
        helpMenu.add(helpTopics);
        helpMenu.add(viewPlugins);
        helpMenu.addSeparator();
        helpMenu.add(aboutIDES);

        menuBar = new JMenuBar();
        // Adding the main categories to the menu
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(operationsMenu);
        menuBar.add(optionsMenu);
        menuBar.add(helpMenu);
    }

    private void createAndAddToolBar() {
        // toolbar = CommandManager_new.getInstance().getToolBar();
        toolbar = new JToolBar();
        toolbar.add(newAction);
        toolbar.add(openAction);
        toolbar.add(saveAction);
        toolbar.add(saveallAction);
        toolbar.addSeparator();
        toolbar.add(wopenAction);
        toolbar.add(wsaveAction);
        toolbar.addSeparator();
        JButton undo = new JButton();
        undo.setIcon(new ImageIcon(
                Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/edit_undo.gif"))));
        undo.setToolTipText(Hub.string("comHintUndo"));
        undo.addActionListener(undoAction);
        JButton redo = new JButton();
        redo.setIcon(new ImageIcon(
                Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/edit_redo.gif"))));
        redo.setToolTipText(Hub.string("comHintRedo"));
        redo.addActionListener(redoAction);
        toolbar.add(undo);
        toolbar.add(redo);
        // Bind to the undo manager
        Hub.getUndoManager().bindNoTextUndo(undo);
        Hub.getUndoManager().bindNoTextRedo(redo);
        toolbar.addSeparator();
        toolbar.add(goToParentAction);

        // create these, but don't add until needed (see hotPlugToolbar)
        zoomSelector = Box.createHorizontalBox();// new JPanel();
        zoomSelector.add(new JLabel(" " + Hub.string("zoom") + ": "));
        zoomSelector.add(zoom);
        fontSelector = Box.createHorizontalBox();
        fontSelector.add(new JLabel(" " + Hub.string("fontSize") + ": "));
        fontSelector.add(font);
        copyButton.setIcon(new ImageIcon(
                Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/edit_copy.gif"))));
        copyButton.setActionCommand(Hub.string("copy"));
        copyButton.setToolTipText(Hub.string("copy"));
        copyButton.addActionListener(ccpAction);
        pasteButton.setIcon(new ImageIcon(
                Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/edit_paste.gif"))));
        pasteButton.setActionCommand(Hub.string("paste"));
        pasteButton.setToolTipText(Hub.string("paste"));
        pasteButton.addActionListener(ccpAction);
        Hub.getCopyPasteManager().bindCutCopy(copyButton);
        Hub.getCopyPasteManager().bindPaste(pasteButton);

        // p.add(Box.createHorizontalGlue());
        // toolbar.add(z);
    }

    private void hotPlugMenus(JMenu[] menus) {
        JMenuBar newMenu = new JMenuBar();
        newMenu.add(menuBar.getMenu(0));
        // once you add a menu to another bar, it's removed from the previous
        // bar
        newMenu.add(menuBar.getMenu(0));
        for (JMenu menu : menus) {
            newMenu.add(menu);
        }
        newMenu.add(menuBar.getMenu(menuBar.getMenuCount() - 3));
        newMenu.add(menuBar.getMenu(menuBar.getMenuCount() - 2));
        newMenu.add(menuBar.getMenu(menuBar.getMenuCount() - 1));
        menuBar = newMenu;
        this.setJMenuBar(menuBar);
    }

    private void hotPlugToolbar(JToolBar tb) {
        getContentPane().remove(toolbar);
        JToolBar newToolbar = new JToolBar();
        for (int i = 0; i < 12; ++i) {
            // once you add a button to another bar, it's removed from the
            // previous bar
            newToolbar.add(toolbar.getComponentAtIndex(0));
        }
        Collection<CopyPastePresentation> ccpp = Hub.getWorkspace().getPresentationsOfType(CopyPastePresentation.class);

        if (ccpp.size() > 0) {
            newToolbar.addSeparator();
            newToolbar.add(copyButton);
            newToolbar.add(pasteButton);
            newToolbar.addSeparator();
        }
        Collection<ZoomablePresentation> zooms = Hub.getWorkspace().getPresentationsOfType(ZoomablePresentation.class);
        if (zooms.size() > 0) {
            newToolbar.add(zoomSelector);
        }
        Collection<GlobalFontSizePresentation> fonts = Hub.getWorkspace()
                .getPresentationsOfType(GlobalFontSizePresentation.class);
        if (fonts.size() > 0) {
            newToolbar.add(fontSelector);
        }
        int tbSize = tb.getComponentCount();
        for (int i = 0; i < tbSize; ++i) {
            // once you add a button to another bar, it's removed from the
            // previous bar
            newToolbar.add(tb.getComponentAtIndex(0));
        }
        toolbar = newToolbar;
        toolbar.setFloatable(false);
        getContentPane().add(toolbar, BorderLayout.PAGE_START);
    }

    /**
     * The views.
     */
    private JTabbedPane tabbedViews;

    private JTabbedPane rightViews;

    private JTabbedPane leftViews;

    private JSplitPane tabsAndRight;

    private FilmStrip filmStrip; // thumbnails of graphs for all open machines

    // in the workspace

    private JToolBar toolbar;

    private StatusBar statusBar;

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

    public FontSizeSelector getFontSelector() {
        return font;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * observer.WorkspaceSubscriber#modelCollectionChanged(observer.WorkspaceMessage
     * )
     */
    public void modelCollectionChanged(WorkspaceMessage message) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see observer.WorkspaceSubscriber#repaintRequired(observer.WorkspaceMessage)
     */
    public void repaintRequired() {
        if (at != null) {
            at.forceRepaint();
        }
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
     */
    private void reconfigureUI() {
        tabbedViews.removeAll();
        rightViews.removeAll();
        for (Action a : disabledOnParentModel) {
            a.setEnabled(true);
        }
        for (Action a : disableOnNoParentModel) {
            a.setEnabled(false);
        }
        if (Hub.getWorkspace().getActiveModel() == null) {
            rightViews.add(NoticeBoard.instance().getName(), NoticeBoard.instance());
            zoom.setEnabled(false);
            font.setEnabled(false);
            for (Action a : disabledOnNoModel) {
                a.setEnabled(false);
            }
            hotPlugMenus(new JMenu[0]);
            hotPlugToolbar(new JToolBar());
            statusBar.setContent(null);
        } else {
            zoom.setEnabled(true);
            font.setEnabled(true);
            for (Action a : disabledOnNoModel) {
                a.setEnabled(true);
            }
            if (Hub.getWorkspace().getActiveModel().getParentModel() != null) {
                for (Action a : disabledOnParentModel) {
                    a.setEnabled(false);
                }
                for (Action a : disableOnNoParentModel) {
                    a.setEnabled(true);
                }
            } else {
                for (Action a : disableOnNoParentModel) {
                    a.setEnabled(false);
                }
            }
            // getting the tabLayout has to be before adding the tabs because it
            // will be overwritten
            UILayout tabLayout = (UILayout) Hub.getWorkspace().getActiveModel().getAnnotation(UI_SETTINGS);
            UIDescriptor uid = Hub.getWorkspace().getActiveUID();
            for (Presentation p : uid.getMainPanePresentations()) {
                tabbedViews.add(p.getName(), p.getGUI());
            }
            for (Presentation p : uid.getRightPanePresentations()) {
                rightViews.add(p.getName(), p.getGUI());
            }
            at = new AnnotationTab(Hub.getWorkspace().getActiveModel());
            rightViews.add(at.getName(), at.getGUI());
            rightViews.add(NoticeBoard.instance().getName(), NoticeBoard.instance());
            if (tabLayout != null) {
                if (tabLayout.activeMainTab > -1 && tabLayout.activeMainTab < tabbedViews.getTabCount()) {
                    tabbedViews.setSelectedIndex(tabLayout.activeMainTab);
                }
                if (tabLayout.activeRightTab > -1 && tabLayout.activeRightTab < rightViews.getTabCount()) {
                    rightViews.setSelectedIndex(tabLayout.activeRightTab);
                }
            }
            arrangeViews();
            hotPlugMenus(uid.getMenus());
            hotPlugToolbar(uid.getToolbar());
            statusBar.setContent(uid.getStatusBar().getGUI());
        }
    }

    /**
     * Store the window size with the persistent properties, then free up all screen
     * resources used by this window.
     * <p>
     * author Chris McAloney
     */
    @Override
    public void dispose() {
        // TODO look into setting this as a single entry in settings.ini rather
        // than four
        Rectangle r = getBounds();
        Hub.getPersistentData().setInt("mainWindowWidth", r.width);
        Hub.getPersistentData().setInt("mainWindowHeight", r.height);
        Hub.getPersistentData().setInt("mainWindowPosX", r.x);
        Hub.getPersistentData().setInt("mainWindowPosY", r.y);
        storeDividerLocations();
        super.dispose();
    }

    protected void storeDividerLocations() {
        if (rightViews.getComponentCount() != 0) {
            Point p = new Point(tabsAndRight.getDividerLocation(), 0);
            p = SwingUtilities.convertPoint(tabsAndRight, p, this);
            // apply Math.ceil to avoid "creeping" of the divider
            Hub.getPersistentData().setInt("rightViewExt", (int) Math.ceil(1000 * (float) (p.x) / getWidth()));
        }
    }

    public void aboutToRearrangeWorkspace() {
        storeDividerLocations();
        if (Hub.getWorkspace().getActiveModel() == null) {
            return;
        }
        if (tabbedViews.getSelectedIndex() >= 0) {
            UILayout oldLayout = (UILayout) Hub.getWorkspace().getActiveModel().getAnnotation(UI_SETTINGS);
            Hub.getWorkspace().getActiveModel().setAnnotation(UI_SETTINGS, new UILayout(tabbedViews.getSelectedIndex(),
                    oldLayout != null ? oldLayout.activeRightTab : rightViews.getSelectedIndex()));
        }
        if (rightViews.getSelectedIndex() >= 0) {
            UILayout oldLayout = (UILayout) Hub.getWorkspace().getActiveModel().getAnnotation(UI_SETTINGS);
            Hub.getWorkspace().getActiveModel().setAnnotation(UI_SETTINGS,
                    new UILayout(oldLayout != null ? oldLayout.activeMainTab : tabbedViews.getSelectedIndex(),
                            rightViews.getSelectedIndex()));
        }

    }

    public void arrangeViews() {
        if (rightViews.getComponentCount() == 0) {
            tabsAndRight.setDividerLocation(1d);
        } else {
            float ext = Hub.getPersistentData().getInt("rightViewExt", 750) / 1000f;
            Point p = new Point((int) (ext * getWidth()), 0);
            p = SwingUtilities.convertPoint(this, p, tabsAndRight);
            tabsAndRight.setDividerLocation(p.x);
        }
    }

    public void activateNotices() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int idx = -1;
                for (int i = 0; i < rightViews.getTabCount(); ++i) {
                    if (rightViews.getTitleAt(i).equals(Hub.string("noticeTab"))) {
                        idx = i;
                        break;
                    }
                }
                rightViews.setSelectedIndex(idx);
            }
        });
    }

    public void activateMainTab(String title) {
        class TabActivator implements Runnable {
            private String title;

            public TabActivator(String title) {
                this.title = title;
            }

            public void run() {
                int idx = tabbedViews.getSelectedIndex();
                for (int i = 0; i < tabbedViews.getTabCount(); ++i) {
                    if (tabbedViews.getTitleAt(i).equals(title)) {
                        idx = i;
                        break;
                    }
                }
                tabbedViews.setSelectedIndex(idx);
            }
        }
        SwingUtilities.invokeLater(new TabActivator(title));
    }

    public void activateRightTab(String title) {
        class TabActivator implements Runnable {
            private String title;

            public TabActivator(String title) {
                this.title = title;
            }

            public void run() {
                int idx = rightViews.getSelectedIndex();
                for (int i = 0; i < rightViews.getTabCount(); ++i) {
                    if (rightViews.getTitleAt(i).equals(title)) {
                        idx = i;
                        break;
                    }
                }
                rightViews.setSelectedIndex(idx);
            }
        }
        SwingUtilities.invokeLater(new TabActivator(title));
    }

    public void activateLeftTab(String title) {
        class TabActivator implements Runnable {
            private String title;

            public TabActivator(String title) {
                this.title = title;
            }

            public void run() {
                int idx = leftViews.getSelectedIndex();
                for (int i = 0; i < leftViews.getTabCount(); ++i) {
                    if (leftViews.getTitleAt(i).equals(title)) {
                        idx = i;
                        break;
                    }
                }
                leftViews.setSelectedIndex(idx);
            }
        }
        SwingUtilities.invokeLater(new TabActivator(title));
    }

    public JComponent getStatusBar() {
        return statusBar;
    }

    public Frame getWindow() {
        return this;
    }

    public String getActiveLeftTab() {
        if (leftViews.getSelectedIndex() < 0) {
            return "";
        }
        return leftViews.getTitleAt(leftViews.getSelectedIndex());
    }

    public String getActiveMainTab() {
        if (tabbedViews.getSelectedIndex() < 0) {
            return "";
        }
        return tabbedViews.getTitleAt(tabbedViews.getSelectedIndex());
    }

    public String getActiveRightTab() {
        if (rightViews.getSelectedIndex() < 0) {
            return "";
        }
        return rightViews.getTitleAt(rightViews.getSelectedIndex());
    }

    public boolean isWindowActivationAfterNoticePopup(WindowEvent we) {
        if (we.getID() == WindowEvent.WINDOW_ACTIVATED && we.getWindow() == this
                && we.getOppositeWindow() == NoticePopup.instance()) {
            return true;
        }
        return false;
    }

    public boolean isWindowDeactivationDuetoNoticePopup(WindowEvent we) {
        if (we.getID() == WindowEvent.WINDOW_DEACTIVATED && we.getWindow() == this
                && we.getOppositeWindow() == NoticePopup.instance()) {
            return true;
        }
        return false;
    }
}
