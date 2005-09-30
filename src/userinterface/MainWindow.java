package userinterface;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;
import userinterface.menu.MenuController;

public class MainWindow {

	/**
     * The display object for this platform.
     */
	private Display display = null;
	
	/**
     * The shell which contains the main gui.
     */
	private Shell shell = null;

	/**
     * A shell for displaying popup error windows.
     */
	private static Shell errorShell = null;
	
	/**
     * Strings for error reporting before the resourse bundle can be loaded.
     */
	public static final String FATAL_ERROR = "Fatal Error: ",
	                           NULL_RESOURCE = "The resource bundle was null.  \nThis component requires the resource bundle.",
							   LOST_RESOURCE = "The resource bundle [resource_bundle.properties] did not load.  \nIt should be located at the root of the source code.";
	
	private ResourceManager rm;
	private MenuController menu;

	
	
	public MainWindow(Splash splash){
		try{
			display = Display.getDefault();
			// Shell for showing error messages
			errorShell = new Shell(display, SWT.NO_TRIM);
			shell = new Shell(display, SWT.SHELL_TRIM);
			rm = new ResourceManager();
			initComponents();
			
			shell.addListener (
				SWT.Close, 
				new Listener (){
					public void handleEvent (Event event){
						event.doit = false;
						if (menu != null && menu.getFileListener() != null) { menu.getFileListener().exit(new SelectionEvent(event)); }
					}
				}
			);
			shell.layout();
			shell.open();
			splash.dispose();
			while (!shell.isDisposed()) { if (!display.readAndDispatch()) display.sleep(); }
			errorShell.dispose();
			display.dispose();
		}
		catch(Exception e){
			if (errorShell != null) { errorShell.dispose(); }
			if (shell != null)       { shell.dispose(); }
			if (display != null)     { display.dispose(); }
			
			Display error_display = Display.getDefault();
			Shell errorShell = new Shell(error_display, SWT.NO_TRIM);
			MessageBox messageBox = new MessageBox(errorShell, SWT.ICON_ERROR | SWT.CLOSE); 
			
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stacktrace = sw.toString();
				
			messageBox.setMessage(e.getMessage() + "\n\n" + stacktrace);
			messageBox.setText(FATAL_ERROR);
			messageBox.open();			
				
			try {
				Runtime.getRuntime().exec ("rundll32 url.dll,FileProtocolHandler http://www.aggressivesoftware.com/research/ides/bugs/default.asp?bug=" + URLEncoder.encode(e.getMessage() + "\n\n" + stacktrace,"UTF-8")); 
			}
			catch(Exception ex) { }
						
			throw new RuntimeException(e);
		}
	}

		
	private void initComponents(){

		// the window
		shell.setText(ResourceManager.getString("window.title"));
		shell.setImage(ResourceManager.getHotImage(ResourceManager.LOGO));
		shell.setSize(800,620);
		
		
		// the base layout
		GridLayout gl_base = new GridLayout();
		gl_base.verticalSpacing = 0;
		gl_base.marginHeight = 0;
		gl_base.marginWidth = 0;
		gl_base.numColumns = 3;
		shell.setLayout(gl_base); // attach it to the shell
		
		menu = new MenuController(shell);
		
			
	}	
	
	
	
	public static void fatalErrorPopup(String error_title, String error_message){
		MessageBox error_popup = new MessageBox(errorShell, SWT.ICON_ERROR | SWT.CLOSE); 
		error_popup.setMessage(error_message);
		error_popup.setText(error_title);
		error_popup.open();			
		throw new RuntimeException(error_title + "\n" + error_message); 
	}	
}
	
