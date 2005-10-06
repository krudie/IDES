/*
 * Created on Aug 7, 2004
 */
package userinterface.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;



import userinterface.ResourceManager;
import userinterface.menu.listeners.FileListener;
import userinterface.menu.listeners.HelpListener;

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
	private ToolBar tbr_file = null;
	
	/**
     * This object houses all the listeners for the File System
     */
	public FileListener fileListener = null;
    
    /**
     * This object houses all the listeners for the Help System
     */
    public HelpListener helpListener = null;

		
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// UnifiedMenus ///////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

	/**
     * File System.
     */
	public UnifiedMenu file_new_project = null,
					   file_new_automaton = null,
					   file_open = null,
					   file_save = null,
					   file_exit = null;
    
    /**
     * Help System.
     */
    public UnifiedMenu help_helptopics = null,
                       help_about = null;
	
		
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
		
		fileListener = new FileListener(shell);
        helpListener = new HelpListener(shell);
		

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// File System ////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
						
		// create the UnifiedMenus
		file_new_project = new UnifiedMenu(ResourceManager.FILE_NEW_PROJECT, fileListener);
	    file_new_automaton = new UnifiedMenu(ResourceManager.FILE_NEW_AUTOMATON, fileListener,SWT.CTRL+'n');
	    file_open = new UnifiedMenu(ResourceManager.FILE_OPEN, fileListener,SWT.CTRL+'o');
	    file_save = new UnifiedMenu(ResourceManager.FILE_SAVE, fileListener,SWT.CTRL+'s');
	    file_exit = new UnifiedMenu(ResourceManager.FILE_EXIT, fileListener);
		
	    // set up main menu structures and add the MenuItems (order matters)
	    
		MenuItem mitm_file = new MenuItem(menu, SWT.CASCADE); 
		mitm_file.setText(ResourceManager.getString("file.mtext"));
		Menu mnu_file = new Menu(mitm_file); 
		mitm_file.setMenu(mnu_file);
		
		MenuItem mitm_new = new MenuItem(mnu_file, SWT.CASCADE);
		mitm_new.setText(ResourceManager.getString("file_new.mtext"));
		Menu mnu_new = new Menu(mitm_new);
		mitm_new.setMenu(mnu_new);
	    
		file_new_project.addMitm(mnu_new);
	    file_new_automaton.addMitm(mnu_new);
		file_open.addMitm(mnu_file);
		file_save.addMitm(mnu_file);
		new MenuItem(mnu_file, SWT.SEPARATOR);
		file_exit.addMitm(mnu_file);
						
		// setup the toolbar structures and add the ToolItems (order matters)		

		tbr_file = new ToolBar(advanced_coolbar.getCoolbar(), SWT.FLAT | SWT.WRAP);

	    file_new_project.addTitm(tbr_file);
        file_new_automaton.addTitm(tbr_file);
	    file_open.addTitm(tbr_file);
	    file_save.addTitm(tbr_file);

		advanced_coolbar.addToolBar(tbr_file);
        
        
        
        
        
        
        
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Help System ////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////         

        // create the UnifiedMenus
        help_helptopics = new UnifiedMenu(ResourceManager.HELP_HELPTOPICS, helpListener);
        help_about = new UnifiedMenu(ResourceManager.HELP_ABOUT, helpListener);

        // set up main menu structures and add the MenuItems (order matters)

        MenuItem mitm_help = new MenuItem(menu, SWT.CASCADE);
        mitm_help.setText(ResourceManager.getString("help.mtext"));
        Menu mnu_help = new Menu(mitm_help);
        mitm_help.setMenu(mnu_help);    

        help_helptopics.addMitm(mnu_help);
        new MenuItem(mnu_help, SWT.SEPARATOR);
        help_about.addMitm(mnu_help);
        
        
	}

    /**
     * Dispose the MenuController.
     */
	public void dispose()
	{

	}
	
	public FileListener getFileListener(){
		return fileListener;
	}
    
    public HelpListener getHelpListener(){
        return helpListener;
    }
	
}