/*
 * Created on Oct 22, 2004
 */
package com.aggressivesoftware.ides.menucontrol.listeners;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.aggressivesoftware.ides.GraphingPlatform;

/**
 * This class handles all events the fall under the "Help" menu concept.
 * 
 * @author  Michael Wood
 */
public class HelpListener extends AbstractListener 
{
	/**
     * The shell for the about window.
     */
	private Shell about_shell = null;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ListenersHelp construction /////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

    /**
     * Construct the ListenersHelp.
     * 
     * @param	graphing_platform	The platform in which this ListenersHelp will exist.
     */
	public HelpListener(GraphingPlatform graphing_platform)
	{
		gp = graphing_platform;
	}
	
    /**
     * Dispose the ListenersHelp.
     */
	public void dispose()
	{
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
		if (resource_handle.equals(gp.rm.HELP_HELPTOPICS)) { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { helpTopics(e); } }; }
		if (resource_handle.equals(gp.rm.HELP_ABOUT))      { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { about(e); } }; }
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
		MessageBox msg = new MessageBox(gp.shell);
		msg.setMessage(gp.rm.getString("help.help.message"));
		msg.setText(gp.rm.getString("help.help.title"));		
		msg.open();						
	}	
	
	/**
     * Display the about window
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void about(org.eclipse.swt.events.SelectionEvent e)
	{
		about_shell = new Shell(gp.shell, SWT.BORDER | SWT.CLOSE | SWT.TITLE);
		about_shell.setText(gp.rm.getString("help.about.title"));
		about_shell.setSize(400,250);
		
		GridLayout layout = new GridLayout();
		layout.marginHeight = 4;
		layout.marginWidth = 4;
		layout.verticalSpacing = 4;
		layout.horizontalSpacing = 4;
		layout.numColumns = 2;
		about_shell.setLayout(layout);
				
		Label logo = new Label(about_shell, SWT.NONE);
		logo.setImage(gp.rm.getImage(gp.rm.BIG_LOGO));
		GridData gd_logo = new GridData();  
		logo.setLayoutData(gd_logo);

		Label data = new Label(about_shell, SWT.WRAP);
		data.setText(gp.rm.getString("help.about.data"));
		GridData gd_data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);  
		data.setLayoutData(gd_data);

		Label message = new Label(about_shell, SWT.WRAP);
		message.setText(gp.rm.getString("help.about.message"));
		GridData gd_message = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL); 
		gd_message.widthHint = 400;
		gd_message.horizontalSpan = 2;
		message.setLayoutData(gd_message);

		about_shell.pack();
		about_shell.open();	
	}	
}
