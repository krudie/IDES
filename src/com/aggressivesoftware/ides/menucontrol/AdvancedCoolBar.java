/*
 * Created on Jul 6, 2004
 */
package com.aggressivesoftware.ides.menucontrol;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;

import com.aggressivesoftware.ides.GraphingPlatform;

/**
 * This class just simplifies the process of adding ToolBars to the GraphingPlatform.
 * 
 * @author Michael Wood
 */
public class AdvancedCoolBar 
{
	/**
     * The GraphingPlatform in which this AdvancedCoolBar exists.
     */
	private GraphingPlatform gp = null;
	
	/**
     * The horizontal span of the GridData in which the CoolBar exists.
     * This should be equal to the number of columns in the GridLayout of the Shell of the GraphingPlatform
     */
	private final int horizontal_span = 3;
	
	/**
     * The actual CoolBar object.
     */
	public CoolBar coolbar = null;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// AdvancedCoolBar construction ///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

    /**
     * Construct the AdvancedCoolBar.
     * 
     * @param	graphing_platform	The GraphingPlatform in which this AdvancedCoolBar will exist.
     */
	public AdvancedCoolBar(GraphingPlatform graphing_platform)
	{
		gp = graphing_platform;
		addHorizontalSeperator();
		coolbar = new CoolBar(gp.shell, SWT.FLAT);
		GridData griddata = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		griddata.horizontalSpan = horizontal_span;
		coolbar.setLayoutData(griddata);
		coolbar.addListener(SWT.Resize, new Listener() { public void handleEvent(Event event) { gp.shell.layout(); } });		
		addHorizontalSeperator();
	}

    /**
     * Add a horizontal seperator to the shell.
     * This is used above and below the CoolBar object.
     */
	private void addHorizontalSeperator()
	{
		Label label = new Label(gp.shell, SWT.HORIZONTAL | SWT.SEPARATOR);
		GridData griddata = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		griddata.horizontalSpan = horizontal_span;
		label.setLayoutData(griddata);
	}	
	
    /**
     * Add a ToolBar to the CoolBar
     * 
     * @param 	toolbar		The ToolBar to be added to the CoolBar
     */
	public void addToolBar(ToolBar toolbar)
	{
		CoolItem coolitem = new CoolItem(coolbar, SWT.NULL);
		Point toolsize = toolbar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		coolitem.setSize(coolitem.computeSize(toolsize.x, toolsize.y));
		coolitem.setMinimumSize(toolsize);
		coolitem.setControl(toolbar);	
	}
	
	/**
	 * Access to setWrapIndicies of the CoolBar.
	 * 
	 * @param indices	A list of CoolItem indices to wrap to the second row.
	 */
	public void setWrapIndices(int[] indices)
	{
		coolbar.setWrapIndices(indices);
	}


}