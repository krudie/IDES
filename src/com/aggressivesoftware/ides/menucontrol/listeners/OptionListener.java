/*
 * Created on Jan 18, 2005
 */
package com.aggressivesoftware.ides.menucontrol.listeners;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import com.aggressivesoftware.ides.GraphingPlatform;

/**
 * @author Micahel Wood
 */
public class OptionListener extends AbstractListener 
{
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// OptionListener construction /////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

    /**
     * Construct the OptionListener.
     * 
     * @param	graphing_platform	The platform in which this OptionListener will exist.
     */
	public OptionListener(GraphingPlatform graphing_platform)
	{
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
		if (resource_handle.equals(gp.rm.OPTION_ERRORREPORT)) { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { errorReport(e); } }; }
		if (resource_handle.equals(gp.rm.OPTION_LATEX))       { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { latex(e);       } }; }
		if (resource_handle.equals(gp.rm.OPTION_EPS))         { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { eps(e);         } }; }
		if (resource_handle.equals(gp.rm.OPTION_TEX))         { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { tex(e);         } }; }
		if (resource_handle.equals(gp.rm.OPTION_BORDER))      { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { border(e);      } }; }
		if (resource_handle.equals(gp.rm.OPTION_NODE))        { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { node(e);        } }; }
		if (resource_handle.equals(gp.rm.OPTION_PSTRICKS))    { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { pstricks(e);    } }; }
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
		//gp.sv.use_error_reporting = gp.mc.option_errorreport.getSelection();
	}	
	
    /**
     * Toggle the use_latex_labels system variable
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void latex(org.eclipse.swt.events.SelectionEvent e)
	{
		if (gp.mc.option_latex.getSelection())
		{
			gp.gc.initializeRenderer(true, false);
		}
		else
		{
			gp.sv.use_latex_labels = false;
			gp.gc.gm.fillBlankLabels();
			gp.gc.gm.accomodateLabels();
			gp.gc.j2dcanvas.repaint();	
			gp.td.repaint();
		}
	}
	
    /**
     * Toggle the export_latex_to_eps system variable
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void eps(org.eclipse.swt.events.SelectionEvent e)
	{
		if (gp.mc.option_eps.getSelection())
		{
			gp.gc.initializeRenderer(false, true);
		}
		else
		{
			gp.sv.export_latex_to_eps = false;
		}
	}

    /**
     * Toggle the export_latex_to_tex system variable
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void tex(org.eclipse.swt.events.SelectionEvent e)
	{
		gp.sv.export_latex_to_tex = gp.mc.option_tex.getSelection();
	}

	/**
     * Toggle the export_with_border system varaible.
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void border(org.eclipse.swt.events.SelectionEvent e)
	{
		gp.sv.export_with_border = gp.mc.option_border.getSelection();
	}	

    /**
     * Toggle the use_standard_node_size system varaible.
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void node(org.eclipse.swt.events.SelectionEvent e)
	{
		gp.sv.use_standard_node_size = gp.mc.option_node.getSelection();
		gp.gc.gm.accomodateLabels();
		gp.gc.repaint();
	}	
	
    /**
     * Toggle the use_pstricks system varaible.
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void pstricks(org.eclipse.swt.events.SelectionEvent e)
	{
		gp.sv.use_pstricks = gp.mc.option_pstricks.getSelection();
	}	
}