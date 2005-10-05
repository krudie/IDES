/*
 * Created on Oct 22, 2004
 */
package userinterface.menu.listeners;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import userinterface.ResourceManager;



/**
 * This class handles all events the fall under the "Help" menu concept.
 * 
 * @author  Michael Wood
 */
public class HelpListener extends AbstractListener {
	/**
     * The shell for the about window.
     */
	private Shell about_shell = null;
    private Shell shell;
    
    /**
     * Construct the ListenersFile.
     * 
     * @param shell The main shell
     */
    public HelpListener(Shell shell){
        this.shell = shell;
    }
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ListenersHelp construction /////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

    /**
     * Dispose the ListenersHelp.
     */
	public void dispose(){
		if (about_shell != null && !about_shell.isDisposed()) { about_shell.dispose(); }
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// adapters ///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

	/**
	 * Find the appropriate Listener for this resource.
	 * 
	 * @param   resource_handle		The constant identification for a concept in the ResourceManager.
	 * @return	The appropriate Listener for this resource.
	 */
	public SelectionListener getListener(String resource_handle)
	{
		if (resource_handle.equals(ResourceManager.HELP_HELPTOPICS)) { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { helpTopics(e); } }; }
		if (resource_handle.equals(ResourceManager.HELP_ABOUT))      { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { about(e); } }; }
		System.out.println("Error: no match for resource_handle = " + resource_handle);
		return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { } };
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// listeners //////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
	
    /**
     * Display the help topics
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void helpTopics(org.eclipse.swt.events.SelectionEvent e)
	{
		try { Runtime.getRuntime().exec ("rundll32 url.dll,FileProtocolHandler " + ResourceManager.getString("help.topics.url")); }
		catch(Exception ex) { ex.printStackTrace(); }
	}	
	
	/**
     * Display the about window
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void about(org.eclipse.swt.events.SelectionEvent e)
	{
		about_shell = new Shell(shell, SWT.BORDER | SWT.CLOSE | SWT.TITLE);
		about_shell.setText(ResourceManager.getString("help.about.title"));
		about_shell.setSize(400,250);
		
		GridLayout layout = new GridLayout();
		layout.marginHeight = 4;
		layout.marginWidth = 4;
		layout.verticalSpacing = 4;
		layout.horizontalSpacing = 4;
		layout.numColumns = 2;
		about_shell.setLayout(layout);
				
		Label logo = new Label(about_shell, SWT.NONE);
		logo.setImage(ResourceManager.getImage(ResourceManager.BIG_LOGO));
		GridData gd_logo = new GridData();  
		logo.setLayoutData(gd_logo);

		Label data = new Label(about_shell, SWT.WRAP);
		data.setText(ResourceManager.getString("help.about.data"));
		GridData gd_data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);  
		data.setLayoutData(gd_data);

		Label message = new Label(about_shell, SWT.WRAP);
		message.setText(ResourceManager.getString("help.about.message"));
		GridData gd_message = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL);  
		gd_message.horizontalSpan = 2;
		message.setLayoutData(gd_message);
		about_shell.pack();
		about_shell.open();	
	}	
}
