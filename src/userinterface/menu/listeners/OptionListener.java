/*
 * Created on Jan 18, 2005
 */
package userinterface.menu.listeners;

import ides2.SystemVariables;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;

import userinterface.ResourceManager;
import userinterface.drawingArea.GraphingPlatform;


/**
 * @author Micahel Wood
 */
public class OptionListener extends AbstractListener 
{
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// OptionListener construction /////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

    GraphingPlatform gp = null;
    Shell shell = null;
    
    /**
     * Construct the OptionListener.
     * 
     * @param	graphing_platform	The platform in which this OptionListener will exist.
     */
	public OptionListener(Shell shell, GraphingPlatform graphing_platform)
	{
	    this.shell = shell;
        gp = graphing_platform;
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
		if (resource_handle.equals(ResourceManager.OPTION_ERRORREPORT)) { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { errorReport(e); } }; }
		if (resource_handle.equals(ResourceManager.OPTION_NODE))        { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { node(e);        } }; }
		System.out.println("Error: no match for resource_handle = " + resource_handle);
		return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { } };
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// listeners //////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
	
    /**
     * Toggle the use_error_reporting system varaible.
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void errorReport(org.eclipse.swt.events.SelectionEvent e)
	{
		SystemVariables.use_error_reporting = gp.mc.option_errorreport.getSelection();
	}	
	
	
    /**
     * Toggle the use_standard_node_size system varaible.
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void node(org.eclipse.swt.events.SelectionEvent e)
	{
		SystemVariables.use_standard_node_size = gp.mc.option_node.getSelection();
		gp.gc.gm.accomodateLabels();
		gp.gc.repaint();
	}	

}