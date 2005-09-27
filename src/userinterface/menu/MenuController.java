/*
 * Created on Aug 7, 2004
 */
package userinterface.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import userinterface.ResourceManager;

/**
 * This class is the top level controller for all menu related concepts.  
 * It is here that the main menu, and toolbars are created and added to the gui.
 * Each menu concept group has it's own listener class where all the actions are defined.
 * When using this class to interface with menuitems, you should always use the UnifedMenu 
 * methods. This ensures that when for example you disable the "delete" menu concept, it is
 * disabled in the main menu, as well as in the toolbar, as well as in any related popup menus.
 * 
 * When you whish to add new menu concepts, you must first add the related resources in the 
 * ResourceManager.  Then you must create a new UnifiedMenu for it here, and add associated
 * menu and tool items if desired.  You will also have to add a listener entery in the respective
 * listeners class.
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
	public ToolBar tbr_graphic = null,
				   tbr_file = null,
				   tbr_edit = null,
				   tbr_machine = null;
	
		
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// UnifiedMenus ///////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

	/**
     * File System.
     */
	public UnifiedMenu file_export_latex = null,
					   file_export_gif = null,
					   file_export_png = null,
					   file_new = null,
					   file_open = null,
					   file_save = null,
					   file_saveas = null,
					   file_exit = null;
	
	/**
     * Edit System.
     */
	public UnifiedMenu edit_undo = null,
					   edit_redo = null,
	 				   edit_copy = null,		
					   edit_paste = null,
					   edit_delete = null;		
	
	/**
     * Help System.
     */
	public UnifiedMenu help_helptopics = null,
					   help_about = null;

	/**
     * Option System.
     */
	public UnifiedMenu option_errorreport = null,
					   option_latex = null,
					   option_eps = null,	
					   option_tex = null,	
					   option_border = null,
					   option_node = null,
					   option_pstricks = null;	
	
	/**
     * Graphic System.
     */
	public UnifiedMenu graphic_zoom = null,
    				   graphic_create = null,
					   graphic_modify = null,
					   graphic_printarea = null,
					   graphic_grab = null,
					   graphic_grid = null,
					   graphic_alledges = null,
					   graphic_alllabels = null;

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Other //////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

	/**
     * Dropdown menu for the ToolItem of graphic_grid
     */
	public Menu mnu_graphic_grid = null;
	
	/**
     * MenuItems for mnu_graphic_grid
     */
	public MenuItem mitm_grid00 = null,
					mitm_grid05 = null,
					mitm_grid10 = null,
					mitm_grid20 = null,
					mitm_grid30 = null;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// MenuController construction ////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

    /**
     * Construct the MenuController.
     * 
     * @param	gp		The GraphingPlatform in which this MenuController will exist.
     */
	public MenuController(Shell shell)
	{
		// setup the container objects
		menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		advanced_coolbar = new AdvancedCoolBar(shell);
		

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// File System ////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
						
		// create the UnifiedMenus
		file_export_latex = new UnifiedMenu(ResourceManager.FILE_EXPORT_LATEX);
		file_export_gif = new UnifiedMenu(ResourceManager.FILE_EXPORT_GIF);
		file_export_png = new UnifiedMenu(ResourceManager.FILE_EXPORT_PNG);
	    file_new = new UnifiedMenu(ResourceManager.FILE_NEW,SWT.CTRL+'n');
	    file_open = new UnifiedMenu(ResourceManager.FILE_OPEN,SWT.CTRL+'o');
	    file_save = new UnifiedMenu(ResourceManager.FILE_SAVE,SWT.CTRL+'s');
	    file_saveas = new UnifiedMenu(ResourceManager.FILE_SAVEAS);
	    file_exit = new UnifiedMenu(ResourceManager.FILE_EXIT);
		
	    // set up main menu structures and add the MenuItems (order matters)
	    
		MenuItem mitm_file = new MenuItem(menu, SWT.CASCADE); 
		mitm_file.setText(ResourceManager.getString("file.mtext"));
		Menu mnu_file = new Menu(mitm_file); 
		mitm_file.setMenu(mnu_file);
		
		MenuItem mitm_file_export = new MenuItem(mnu_file, SWT.CASCADE);
		mitm_file_export.setText(ResourceManager.getString("file_export.mtext"));
		Menu mnu_file_export = new Menu(mitm_file_export);
		mitm_file_export.setMenu(mnu_file_export);
	    
	    file_export_latex.addMitm(mnu_file_export);
	    file_export_gif.addMitm(mnu_file_export);
	    file_export_png.addMitm(mnu_file_export);
	    new MenuItem(mnu_file, SWT.SEPARATOR);
	    file_new.addMitm(mnu_file);
		file_open.addMitm(mnu_file);
		file_save.addMitm(mnu_file);
		file_saveas.addMitm(mnu_file);
		new MenuItem(mnu_file, SWT.SEPARATOR);
		file_exit.addMitm(mnu_file);
						
		// setup the toolbar structures and add the ToolItems (order matters)		

		tbr_file = new ToolBar(advanced_coolbar.coolbar, SWT.FLAT | SWT.WRAP);

	    file_new.addTitm(tbr_file);
	    file_open.addTitm(tbr_file);
	    file_save.addTitm(tbr_file);
	    file_saveas.addTitm(tbr_file);
	    new ToolItem(tbr_file, SWT.SEPARATOR);
	    file_export_latex.addTitm(tbr_file);
	    file_export_gif.addTitm(tbr_file);
	    file_export_png.addTitm(tbr_file);

		advanced_coolbar.addToolBar(tbr_file);

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Edit System ////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
		
		// create the UnifiedMenus
		edit_undo = new UnifiedMenu(ResourceManager.EDIT_UNDO);
		edit_redo = new UnifiedMenu(ResourceManager.EDIT_REDO);
		edit_copy = new UnifiedMenu(ResourceManager.EDIT_COPY,SWT.CTRL+'c');
		edit_paste = new UnifiedMenu(ResourceManager.EDIT_PASTE,SWT.CTRL+'v');
		edit_delete = new UnifiedMenu(ResourceManager.EDIT_DELETE,SWT.DEL);

	    // set up main menu structures and add the MenuItems (order matters)

		MenuItem mitm_edit = new MenuItem(menu, SWT.CASCADE);
		mitm_edit.setText(ResourceManager.getString("edit.mtext"));
		Menu mnu_edit = new Menu(mitm_edit);
		mitm_edit.setMenu(mnu_edit);
						
	    edit_undo.addMitm(mnu_edit);
	    edit_redo.addMitm(mnu_edit);
	    new MenuItem(mnu_edit, SWT.SEPARATOR);
	    edit_copy.addMitm(mnu_edit);
	    edit_paste.addMitm(mnu_edit);
	    edit_delete.addMitm(mnu_edit);

		// setup the toolbar structures and add the ToolItems (order matters)		

		tbr_edit = new ToolBar(advanced_coolbar.coolbar, SWT.FLAT | SWT.WRAP);

	    edit_undo.addTitm(tbr_edit);
	    edit_redo.addTitm(tbr_edit);
	    edit_copy.addTitm(tbr_edit);
	    edit_paste.addTitm(tbr_edit);
	    edit_delete.addTitm(tbr_edit);

		advanced_coolbar.addToolBar(tbr_edit);				
		
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Graphic System /////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
		
		// create the UnifiedMenus
		graphic_zoom = new UnifiedMenu(ResourceManager.GRAPHIC_ZOOM);
		graphic_create = new UnifiedMenu(ResourceManager.GRAPHIC_CREATE);
		graphic_modify = new UnifiedMenu(ResourceManager.GRAPHIC_MODIFY);
		graphic_printarea = new UnifiedMenu(ResourceManager.GRAPHIC_PRINTAREA);
		graphic_grab = new UnifiedMenu(ResourceManager.GRAPHIC_GRAB);
		graphic_grid = new UnifiedMenu(ResourceManager.GRAPHIC_GRID);
		graphic_alledges = new UnifiedMenu(ResourceManager.GRAPHIC_ALLEDGES);
		graphic_alllabels = new UnifiedMenu(ResourceManager.GRAPHIC_ALLLABELS);
		
	    // set up main menu structures and add the MenuItems (order matters)

		MenuItem mitm_graphic = new MenuItem(menu, SWT.CASCADE);
		mitm_graphic.setText(ResourceManager.getString("graphic.mtext"));
		Menu mnu_graphic = new Menu(mitm_graphic);
		mitm_graphic.setMenu(mnu_graphic);
			
		graphic_zoom.addMitm(mnu_graphic, SWT.RADIO);
		graphic_create.addMitm(mnu_graphic, SWT.RADIO);
		graphic_modify.addMitm(mnu_graphic, SWT.RADIO);
		graphic_printarea.addMitm(mnu_graphic, SWT.RADIO);
		graphic_grab.addMitm(mnu_graphic, SWT.RADIO);
	    new MenuItem(mnu_graphic, SWT.SEPARATOR);
		graphic_alledges.addMitm(mnu_graphic, SWT.CHECK);
		graphic_alllabels.addMitm(mnu_graphic, SWT.CHECK);

		// setup the toolbar structures and add the ToolItems (order matters)		

		tbr_graphic = new ToolBar(advanced_coolbar.coolbar, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
		
		graphic_grid.addTitm(tbr_graphic, SWT.DROP_DOWN);
		graphic_alledges.addTitm(tbr_graphic, SWT.CHECK);
		graphic_alllabels.addTitm(tbr_graphic, SWT.CHECK);
	    new ToolItem(tbr_graphic, SWT.SEPARATOR);
		graphic_zoom.addTitm(tbr_graphic, SWT.RADIO);
		graphic_create.addTitm(tbr_graphic, SWT.RADIO);
		graphic_modify.addTitm(tbr_graphic, SWT.RADIO);
		graphic_printarea.addTitm(tbr_graphic, SWT.RADIO);
		graphic_grab.addTitm(tbr_graphic, SWT.RADIO);
		
		// dropdown menu
		mnu_graphic_grid = new Menu(shell, SWT.POP_UP);
		mitm_grid00 = new MenuItem(mnu_graphic_grid, SWT.RADIO);
		mitm_grid00.setText(ResourceManager.getString("graphic_grid.nogrid"));
		mitm_grid00.setData(new Integer(0));
		mitm_grid05 = new MenuItem(mnu_graphic_grid, SWT.RADIO);
		mitm_grid05.setText(ResourceManager.getString("graphic_grid.5px"));
		mitm_grid05.setData(new Integer(5));
		mitm_grid10 = new MenuItem(mnu_graphic_grid, SWT.RADIO);
		mitm_grid10.setText(ResourceManager.getString("graphic_grid.10px"));
		mitm_grid10.setData(new Integer(10));
		mitm_grid20 = new MenuItem(mnu_graphic_grid, SWT.RADIO);
		mitm_grid20.setText(ResourceManager.getString("graphic_grid.20px"));
		mitm_grid20.setData(new Integer(20));
		mitm_grid30 = new MenuItem(mnu_graphic_grid, SWT.RADIO);
		mitm_grid30.setText(ResourceManager.getString("graphic_grid.30px"));
		mitm_grid30.setData(new Integer(30));
				
		advanced_coolbar.addToolBar(tbr_graphic);
					
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Option System //////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

		// create the UnifiedMenus
		option_errorreport = new UnifiedMenu(ResourceManager.OPTION_ERRORREPORT);
		option_latex = new UnifiedMenu(ResourceManager.OPTION_LATEX);
		option_eps = new UnifiedMenu(ResourceManager.OPTION_EPS);
		option_tex = new UnifiedMenu(ResourceManager.OPTION_TEX);
		option_border = new UnifiedMenu(ResourceManager.OPTION_BORDER);
		option_node = new UnifiedMenu(ResourceManager.OPTION_NODE);
		option_pstricks = new UnifiedMenu(ResourceManager.OPTION_PSTRICKS);

	    // set up main menu structures and add the MenuItems (order matters)

		MenuItem mitm_option = new MenuItem(menu, SWT.CASCADE);
		mitm_option.setText(ResourceManager.getString("option.mtext"));
		Menu mnu_option = new Menu(mitm_option);
		mitm_option.setMenu(mnu_option);	

		option_errorreport.addMitm(mnu_option, SWT.CHECK);
		option_latex.addMitm(mnu_option, SWT.CHECK);
		option_eps.addMitm(mnu_option, SWT.CHECK);
		option_tex.addMitm(mnu_option, SWT.CHECK);
		option_border.addMitm(mnu_option, SWT.CHECK);
		option_node.addMitm(mnu_option, SWT.CHECK);
		option_pstricks.addMitm(mnu_option, SWT.CHECK);

		// setup the toolbar structures and add the ToolItems (order matters)		
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Help System ////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

		// create the UnifiedMenus
		help_helptopics = new UnifiedMenu(ResourceManager.HELP_HELPTOPICS);
		help_about = new UnifiedMenu(ResourceManager.HELP_ABOUT);

	    // set up main menu structures and add the MenuItems (order matters)

		MenuItem mitm_help = new MenuItem(menu, SWT.CASCADE);
		mitm_help.setText(ResourceManager.getString("help.mtext"));
		Menu mnu_help = new Menu(mitm_help);
		mitm_help.setMenu(mnu_help);	

		help_helptopics.addMitm(mnu_help);
		new MenuItem(mnu_help, SWT.SEPARATOR);
		help_about.addMitm(mnu_help);

		// setup the toolbar structures and add the ToolItems (order matters)		
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Initial States /////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

		file_save.disable();
		file_saveas.disable();
		edit_undo.disable();
		edit_redo.disable();
		edit_copy.disable();
		edit_paste.disable();
		edit_delete.disable();

		advanced_coolbar.setWrapIndices(new int[] {2});
	}

    /**
     * Dispose the MenuController.
     */
	public void dispose()
	{

	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// advanced features //////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Specify the state of the dropdown toolitem.
     * 
     * @param	new_state	An integer corresponding to the menuitem to be selected
     */
	public void setGridDropdownState(int new_state)
	{
		mitm_grid00.setSelection(false);
		mitm_grid05.setSelection(false);
		mitm_grid10.setSelection(false);
		mitm_grid20.setSelection(false);
		mitm_grid30.setSelection(false);

		switch (new_state)
		{
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