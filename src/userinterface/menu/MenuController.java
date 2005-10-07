/*
 * Created on Aug 7, 2004
 */
package userinterface.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

import userinterface.ResourceManager;
import userinterface.menu.listeners.EditListener;
import userinterface.menu.listeners.FileListener;
import userinterface.menu.listeners.GraphicListener;
import userinterface.menu.listeners.HelpListener;
import userinterface.menu.listeners.OptionListener;

/**
 * This class is the top level controller for all menu related concepts. It is
 * here that the main menu, and toolbars are created and added to the gui. Each
 * menu concept group has it's own listener class where all the actions are
 * defined. When using this class to interface with menuitems, you should always
 * use the UnifedMenu methods. This ensures that when for example you disable
 * the "delete" menu concept, it is disabled in the main menu, as well as in the
 * toolbar, as well as in any related popup menus.
 * 
 * When you whish to add new menu concepts, you must first add the related
 * resources in the ResourceManager. Then you must create a new UnifiedMenu for
 * it here, and add associated menu and tool items if desired. You will also
 * have to add a listener entery in the respective listeners class.
 * 
 * @author Michael Wood
 */
public class MenuController {

    /**
     * The main Menu to appear at the top of the GraphingPlatform gui.
     */
    private Menu menu = null;

    /**
     * The AdvancedCoolbar to appear at the top of the GraphingPlatform gui.
     */
    private AdvancedCoolBar advanced_coolbar = null;

    /**
     * The ToolBars
     */
    public ToolBar tbr_file = null, tbr_graphic = null, tbr_edit = null;

    /**
     * This object houses all the listeners for the File System
     */
    public FileListener fileListener = null;

    /**
     * This object houses all the listeners for the Help System
     */
    public HelpListener helpListener = null;

    public EditListener editListener = null;

    public GraphicListener graphicListener = null;

    public OptionListener optionListener = null;

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UnifiedMenus
    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * File System.
     */
    public UnifiedMenu file_new_project = null, file_new_automaton = null,
            file_open_project = null, file_save_project = null,
            file_open_automaton = null, file_save_automaton = null,
            file_exit = null;

    /**
     * Edit System.
     */
    public UnifiedMenu edit_copy = null, edit_paste = null, edit_delete = null;

    /**
     * Option System.
     */
    public UnifiedMenu option_errorreport = null, option_node = null;

    /**
     * Graphic System.
     */
    public UnifiedMenu graphic_zoom = null, graphic_create = null,
            graphic_modify = null, graphic_grab = null, graphic_grid = null,
            graphic_alledges = null, graphic_alllabels = null;

    /**
     * Help System.
     */
    public UnifiedMenu help_helptopics = null, help_about = null;

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Other
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Dropdown menu for the ToolItem of graphic_grid
     */
    public Menu mnu_graphic_grid = null;

    /**
     * MenuItems for mnu_graphic_grid
     */
    public MenuItem mitm_grid00 = null, mitm_grid05 = null, mitm_grid10 = null,
            mitm_grid20 = null, mitm_grid30 = null;

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // MenuController construction
    // ////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construct the MenuController.
     * 
     * @param gp
     *            The GraphingPlatform in which this MenuController will exist.
     */
    public MenuController(Shell shell) {
        // setup the container objects
        menu = new Menu(shell, SWT.BAR);
        shell.setMenuBar(menu);
        advanced_coolbar = new AdvancedCoolBar(shell);

        fileListener = new FileListener(shell);
        helpListener = new HelpListener(shell);
        editListener = new EditListener(shell);
        optionListener = new OptionListener(shell);
        graphicListener = new GraphicListener(shell);

        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // File PROJECT System
        // ///////////////////////////////////////////////////////////////////////////////////////////
        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // create the UnifiedMenus
        file_new_project = new UnifiedMenu(ResourceManager.FILE_NEW_PROJECT,
                fileListener);
        file_open_project = new UnifiedMenu(ResourceManager.FILE_OPEN_PROJECT,
                fileListener, SWT.CTRL + 'o');
        file_save_project = new UnifiedMenu(ResourceManager.FILE_SAVE_PROJECT,
                fileListener, SWT.CTRL + 's');
        file_exit = new UnifiedMenu(ResourceManager.FILE_EXIT, fileListener);

        file_new_automaton = new UnifiedMenu(
                ResourceManager.FILE_NEW_AUTOMATON, fileListener,
                SWT.CTRL + 'n');
        file_open_automaton = new UnifiedMenu(
                ResourceManager.FILE_OPEN_AUTOMATON, fileListener);
        file_save_automaton = new UnifiedMenu(
                ResourceManager.FILE_SAVE_AUTOMATON, fileListener);

        // set up main menu structures and add the MenuItems (order matters)
        MenuItem mitm_file = new MenuItem(menu, SWT.CASCADE);
        mitm_file.setText(ResourceManager.getString("file.mtext"));
        Menu mnu_file = new Menu(mitm_file);
        mitm_file.setMenu(mnu_file);

        file_new_project.addMitm(mnu_file);
        file_open_project.addMitm(mnu_file);
        file_save_project.addMitm(mnu_file);
        new MenuItem(mnu_file, SWT.SEPARATOR);
        file_new_automaton.addMitm(mnu_file);
        file_open_automaton.addMitm(mnu_file);
        file_save_automaton.addMitm(mnu_file);
        new MenuItem(mnu_file, SWT.SEPARATOR);
        file_exit.addMitm(mnu_file);

        // setup the toolbar structures and add the ToolItems (order matters)

        tbr_file = new ToolBar(advanced_coolbar.getCoolbar(), SWT.FLAT
                | SWT.WRAP);

        file_new_project.addTitm(tbr_file);
        file_open_project.addTitm(tbr_file);
        file_save_project.addTitm(tbr_file);
        new ToolItem(tbr_file, SWT.SEPARATOR);
        file_new_automaton.addTitm(tbr_file);
        file_open_automaton.addTitm(tbr_file);
        file_save_automaton.addTitm(tbr_file);

        advanced_coolbar.addToolBar(tbr_file);

        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Edit System
        // ////////////////////////////////////////////////////////////////////////////////////////////////////
        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // create the UnifiedMenus
        edit_copy = new UnifiedMenu(ResourceManager.EDIT_COPY, editListener,
                SWT.CTRL + 'c');
        edit_paste = new UnifiedMenu(ResourceManager.EDIT_PASTE, editListener,
                SWT.CTRL + 'v');
        edit_delete = new UnifiedMenu(ResourceManager.EDIT_DELETE,
                editListener, SWT.DEL);

        // set up main menu structures and add the MenuItems (order matters)

        MenuItem mitm_edit = new MenuItem(menu, SWT.CASCADE);
        mitm_edit.setText(ResourceManager.getString("edit.mtext"));
        Menu mnu_edit = new Menu(mitm_edit);
        mitm_edit.setMenu(mnu_edit);

        new MenuItem(mnu_edit, SWT.SEPARATOR);
        edit_copy.addMitm(mnu_edit);
        edit_paste.addMitm(mnu_edit);
        edit_delete.addMitm(mnu_edit);

        // setup the toolbar structures and add the ToolItems (order matters)

        tbr_edit = new ToolBar(advanced_coolbar.getCoolbar(), SWT.FLAT
                | SWT.WRAP);

        edit_copy.addTitm(tbr_edit);
        edit_paste.addTitm(tbr_edit);
        edit_delete.addTitm(tbr_edit);

        advanced_coolbar.addToolBar(tbr_edit);

        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Graphic System
        // /////////////////////////////////////////////////////////////////////////////////////////////////
        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // create the UnifiedMenus
        graphic_zoom = new UnifiedMenu(ResourceManager.GRAPHIC_ZOOM,
                graphicListener);
        graphic_create = new UnifiedMenu(ResourceManager.GRAPHIC_CREATE,
                graphicListener);
        graphic_modify = new UnifiedMenu(ResourceManager.GRAPHIC_MODIFY,
                graphicListener);
        graphic_grab = new UnifiedMenu(ResourceManager.GRAPHIC_GRAB,
                graphicListener);
        graphic_grid = new UnifiedMenu(ResourceManager.GRAPHIC_GRID,
                graphicListener);
        graphic_alledges = new UnifiedMenu(ResourceManager.GRAPHIC_ALLEDGES,
                graphicListener);
        graphic_alllabels = new UnifiedMenu(ResourceManager.GRAPHIC_ALLLABELS,
                graphicListener);

        // set up main menu structures and add the MenuItems (order matters)

        MenuItem mitm_graphic = new MenuItem(menu, SWT.CASCADE);
        mitm_graphic.setText(ResourceManager.getString("graphic.mtext"));
        Menu mnu_graphic = new Menu(mitm_graphic);
        mitm_graphic.setMenu(mnu_graphic);

        graphic_zoom.addMitm(mnu_graphic, SWT.RADIO);
        graphic_create.addMitm(mnu_graphic, SWT.RADIO);
        graphic_modify.addMitm(mnu_graphic, SWT.RADIO);
        graphic_grab.addMitm(mnu_graphic, SWT.RADIO);
        new MenuItem(mnu_graphic, SWT.SEPARATOR);
        graphic_alledges.addMitm(mnu_graphic, SWT.CHECK);
        graphic_alllabels.addMitm(mnu_graphic, SWT.CHECK);

        // setup the toolbar structures and add the ToolItems (order matters)

        tbr_graphic = new ToolBar(advanced_coolbar.getCoolbar(), SWT.FLAT
                | SWT.WRAP | SWT.RIGHT);

        graphic_grid.addTitm(tbr_graphic, SWT.DROP_DOWN);
        graphic_alledges.addTitm(tbr_graphic, SWT.CHECK);
        graphic_alllabels.addTitm(tbr_graphic, SWT.CHECK);
        new ToolItem(tbr_graphic, SWT.SEPARATOR);
        graphic_zoom.addTitm(tbr_graphic, SWT.RADIO);
        graphic_create.addTitm(tbr_graphic, SWT.RADIO);
        graphic_modify.addTitm(tbr_graphic, SWT.RADIO);
        graphic_grab.addTitm(tbr_graphic, SWT.RADIO);

        // dropdown menu
        mnu_graphic_grid = new Menu(shell, SWT.POP_UP);
        mitm_grid00 = new MenuItem(mnu_graphic_grid, SWT.RADIO);
        mitm_grid00.setText(ResourceManager.getString("graphic_grid.nogrid"));
        mitm_grid00.setData(new Integer(0));
        mitm_grid00.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                graphicListener.gridDropdown(mitm_grid00);
            }
        });
        mitm_grid05 = new MenuItem(mnu_graphic_grid, SWT.RADIO);
        mitm_grid05.setText(ResourceManager.getString("graphic_grid.5px"));
        mitm_grid05.setData(new Integer(5));
        mitm_grid05.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                graphicListener.gridDropdown(mitm_grid05);
            }
        });
        mitm_grid10 = new MenuItem(mnu_graphic_grid, SWT.RADIO);
        mitm_grid10.setText(ResourceManager.getString("graphic_grid.10px"));
        mitm_grid10.setData(new Integer(10));
        mitm_grid10.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                graphicListener.gridDropdown(mitm_grid10);
            }
        });
        mitm_grid20 = new MenuItem(mnu_graphic_grid, SWT.RADIO);
        mitm_grid20.setText(ResourceManager.getString("graphic_grid.20px"));
        mitm_grid20.setData(new Integer(20));
        mitm_grid20.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                graphicListener.gridDropdown(mitm_grid20);
            }
        });
        mitm_grid30 = new MenuItem(mnu_graphic_grid, SWT.RADIO);
        mitm_grid30.setText(ResourceManager.getString("graphic_grid.30px"));
        mitm_grid30.setData(new Integer(30));
        mitm_grid30.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                graphicListener.gridDropdown(mitm_grid30);
            }
        });

        advanced_coolbar.addToolBar(tbr_graphic);

        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Option System
        // //////////////////////////////////////////////////////////////////////////////////////////////////
        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // create the UnifiedMenus
        option_errorreport = new UnifiedMenu(
                ResourceManager.OPTION_ERRORREPORT, optionListener);
        option_node = new UnifiedMenu(ResourceManager.OPTION_NODE,
                optionListener);

        // set up main menu structures and add the MenuItems (order matters)

        MenuItem mitm_option = new MenuItem(menu, SWT.CASCADE);
        mitm_option.setText(ResourceManager.getString("option.mtext"));
        Menu mnu_option = new Menu(mitm_option);
        mitm_option.setMenu(mnu_option);

        option_errorreport.addMitm(mnu_option, SWT.CHECK);
        option_node.addMitm(mnu_option, SWT.CHECK);

        // setup the toolbar structures and add the ToolItems (order matters)

        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Help System
        // ////////////////////////////////////////////////////////////////////////////////////////////////////
        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // create the UnifiedMenus
        help_helptopics = new UnifiedMenu(ResourceManager.HELP_HELPTOPICS,
                helpListener);
        help_about = new UnifiedMenu(ResourceManager.HELP_ABOUT, helpListener);

        // set up main menu structures and add the MenuItems (order matters)

        MenuItem mitm_help = new MenuItem(menu, SWT.CASCADE);
        mitm_help.setText(ResourceManager.getString("help.mtext"));
        Menu mnu_help = new Menu(mitm_help);
        mitm_help.setMenu(mnu_help);

        help_helptopics.addMitm(mnu_help);
        new MenuItem(mnu_help, SWT.SEPARATOR);
        help_about.addMitm(mnu_help);

        advanced_coolbar.setWrapIndices(new int[] { 2 });

        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Initial States
        // /////////////////////////////////////////////////////////////////////////////////////////////////
        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        file_new_automaton.disable();
        file_save_automaton.disable();
        file_open_automaton.disable();
        file_save_project.disable();
        edit_copy.disable();
        edit_paste.disable();
        edit_delete.disable();

        graphic_zoom.disable();
        graphic_create.disable();
        graphic_modify.disable();
        graphic_grab.disable();
        graphic_grid.disable();
        graphic_alledges.disable();
        graphic_alllabels.disable();

    }

    /**
     * Dispose the MenuController.
     */
    public void dispose() {

    }

    public FileListener getFileListener() {
        return fileListener;
    }

    public HelpListener getHelpListener() {
        return helpListener;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // advanced features
    // //////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Specify the state of the dropdown toolitem.
     * 
     * @param new_state
     *            An integer corresponding to the menuitem to be selected
     */
    public void setGridDropdownState(int new_state) {
        mitm_grid00.setSelection(false);
        mitm_grid05.setSelection(false);
        mitm_grid10.setSelection(false);
        mitm_grid20.setSelection(false);
        mitm_grid30.setSelection(false);

        switch (new_state) {
        case 0:
            mitm_grid00.setSelection(true);
            graphic_grid.titm.setText(mitm_grid00.getText());
            break;
        case 5:
            mitm_grid05.setSelection(true);
            graphic_grid.titm.setText(mitm_grid05.getText());
            break;
        case 10:
            mitm_grid10.setSelection(true);
            graphic_grid.titm.setText(mitm_grid10.getText());
            break;
        case 20:
            mitm_grid20.setSelection(true);
            graphic_grid.titm.setText(mitm_grid20.getText());
            break;
        case 30:
            mitm_grid30.setSelection(true);
            graphic_grid.titm.setText(mitm_grid30.getText());
            break;
        }
    }

}