package userinterface;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

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
	
	private ResourceManager rm = null;
	
	
	public MainWindow(Splash splash){
		try{
			display = Display.getDefault();
			rm = new ResourceManager();
			
			initComponents();
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
			Shell error_shell = new Shell(error_display, SWT.NO_TRIM);
			MessageBox message_box = new MessageBox(error_shell, SWT.ICON_ERROR | SWT.CLOSE); 
			
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stacktrace = sw.toString();
				
			message_box.setMessage(e.getMessage() + "\n\n" + stacktrace);
			message_box.setText(FATAL_ERROR);
			message_box.open();			
				
			try {
				Runtime.getRuntime().exec ("rundll32 url.dll,FileProtocolHandler http://www.aggressivesoftware.com/research/ides/bugs/default.asp?bug=" + URLEncoder.encode(e.getMessage() + "\n\n" + stacktrace,"UTF-8")); 
			}
			catch(Exception ex) { }
						
			throw new RuntimeException(e);
		}
	}

		
	private void initComponents(){

		// Shell for showing error messages
		errorShell = new Shell(display, SWT.NO_TRIM);
		
		// the window
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText(ResourceManager.getString("window.title"));
		//shell.setImage(ResourceManager.getHotImage(rm.LOGO));
		shell.setSize(800,620);
		
		
		
	}	
	
	
	
	public static void fatalErrorPopup(String error_title, String error_message){
		MessageBox error_popup = new MessageBox(errorShell, SWT.ICON_ERROR | SWT.CLOSE); 
		error_popup.setMessage(error_message);
		error_popup.setText(error_title);
		error_popup.open();			
		throw new RuntimeException(error_title + "\n" + error_message); 
	}	
}
	
