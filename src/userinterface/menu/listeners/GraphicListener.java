/*
 * Created on Jun 22, 2004
 */
package userinterface.menu.listeners;


import ides2.SystemVariables;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import userinterface.GraphingPlatform;
import userinterface.MainWindow;
import userinterface.ResourceManager;


/**
 * This class handles all events the fall under the "Graphic" menu concept.
 * 
 * @author Michael Wood
 */
public class GraphicListener extends AbstractListener
{
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ListenersGraph construction ////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

    Shell shell;
    
    /**
     * Construct the ListenersGraph.
     * 
     * @param	graphing_platform		The platform in which this ListenersGraph will exist.
     */
	public GraphicListener(Shell shell){
        this.shell = shell;
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
		if (resource_handle.equals(ResourceManager.GRAPHIC_ZOOM))       { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { zoom(e); } }; }
		if (resource_handle.equals(ResourceManager.GRAPHIC_CREATE))     { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { create(e); } }; }
		if (resource_handle.equals(ResourceManager.GRAPHIC_MODIFY))     { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { modify(e); } }; }
		if (resource_handle.equals(ResourceManager.GRAPHIC_GRAB))       { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { grab(e); } }; }
		if (resource_handle.equals(ResourceManager.GRAPHIC_GRID))       { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { grid(e); } }; }
		if (resource_handle.equals(ResourceManager.GRAPHIC_ALLEDGES))   { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { toggleAllEdges(e); } }; }
		if (resource_handle.equals(ResourceManager.GRAPHIC_ALLLABELS))  { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { toggleAllLabels(e); } }; }
		System.out.println("Error: no match for resource_handle = " + resource_handle);
		return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { } };
	}

	public void deselect()
	{
		if      (MainWindow.getGraphingPlatform().gc.selected_tool == MainWindow.getGraphingPlatform().gc.ZOOM_TOOL)       { MainWindow.getGraphingPlatform().mc.graphic_zoom.setSelection(false); }
		else if (MainWindow.getGraphingPlatform().gc.selected_tool == MainWindow.getGraphingPlatform().gc.CREATE_TOOL)     { MainWindow.getGraphingPlatform().mc.graphic_create.setSelection(false); }
		else if (MainWindow.getGraphingPlatform().gc.selected_tool == MainWindow.getGraphingPlatform().gc.MODIFY_TOOL)     { MainWindow.getGraphingPlatform().mc.graphic_modify.setSelection(false); }
		else if (MainWindow.getGraphingPlatform().gc.selected_tool == MainWindow.getGraphingPlatform().gc.GRAB_TOOL)       { MainWindow.getGraphingPlatform().mc.graphic_grab.setSelection(false); }
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// listeners //////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

	
    /**
     * Configure the system for action with the selected Tool.
     *
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void zoom(org.eclipse.swt.events.SelectionEvent e)
	{
		deselect();
        MainWindow.getGraphingPlatform().mc.graphic_zoom.setSelection(true);
        MainWindow.getGraphingPlatform().gc.selected_tool = MainWindow.getGraphingPlatform().gc.ZOOM_TOOL;
        MainWindow.getGraphingPlatform().gc.abandonGefTool();
        MainWindow.getGraphingPlatform().gc.j2dcanvas.setCursor(ResourceManager.getCursor(ResourceManager.ZOOM_CURSOR));
	}	
	
    /**
     * Configure the system for action with the selected Tool.
     *
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void create(org.eclipse.swt.events.SelectionEvent e)
	{
		deselect();
        MainWindow.getGraphingPlatform().mc.graphic_create.setSelection(true);
        MainWindow.getGraphingPlatform().gc.selected_tool = MainWindow.getGraphingPlatform().gc.CREATE_TOOL;
        MainWindow.getGraphingPlatform().gc.abandonGefTool();
        MainWindow.getGraphingPlatform().gc.j2dcanvas.setCursor(ResourceManager.getCursor(ResourceManager.CREATE_CURSOR));
	}

    /**
     * Configure the system for action with the selected Tool.
     *
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void modify(org.eclipse.swt.events.SelectionEvent e)
	{
		deselect();
        MainWindow.getGraphingPlatform().mc.graphic_modify.setSelection(true);
        MainWindow.getGraphingPlatform().gc.selected_tool = MainWindow.getGraphingPlatform().gc.MODIFY_TOOL;
        MainWindow.getGraphingPlatform().gc.abandonGefTool();
        MainWindow.getGraphingPlatform().gc.j2dcanvas.setCursor(ResourceManager.getCursor(ResourceManager.MODIFY_CURSOR));
	}	


    /**
     * Configure the system for action with the selected Tool.
     *
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void grab(org.eclipse.swt.events.SelectionEvent e)
	{
		deselect();
        MainWindow.getGraphingPlatform().mc.graphic_grab.setSelection(true);
        MainWindow.getGraphingPlatform().gc.selected_tool = MainWindow.getGraphingPlatform().gc.GRAB_TOOL;
        MainWindow.getGraphingPlatform().gc.abandonGefTool();
        MainWindow.getGraphingPlatform().gc.j2dcanvas.setCursor(ResourceManager.getCursor(ResourceManager.GRAB_CURSOR));
	}	

    /**
     * If the grid dropdown value has changed, then the graphs grid value will be updated.
     * If the grid button has been pressed, then the grid will toggle between visible and invisible.
     *
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void grid(org.eclipse.swt.events.SelectionEvent e)
	{
		if (e.detail == SWT.ARROW)
		{
			Rectangle rectangle = MainWindow.getGraphingPlatform().mc.graphic_grid.titm.getBounds();
			Point point = new Point(rectangle.x, rectangle.y + rectangle.height);
			point = MainWindow.getGraphingPlatform().mc.tbr_graphic.toDisplay(point);
            MainWindow.getGraphingPlatform().mc.mnu_graphic_grid.setLocation(point);
            MainWindow.getGraphingPlatform().mc.mnu_graphic_grid.setVisible(true);
		}
		else
		{
            MainWindow.getGraphingPlatform().gc.toggleGrid();						
		}
	}	

    /**
     * Change the snap-to-grid value.
     *
     * @param	mitm	The Dropdown MenuItem that has the selected grid size.
     */
	public void gridDropdown(MenuItem mitm)
	{
        MainWindow.getGraphingPlatform().mc.graphic_grid.titm.setText(mitm.getText());
        SystemVariables.grid = ((Integer)mitm.getData()).intValue();
        MainWindow.getGraphingPlatform().gc.repaint();
	}
	
    /**
     * Update the system setting.
     *
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void toggleAllEdges(org.eclipse.swt.events.SelectionEvent e)
	{
        SystemVariables.show_all_edges = !SystemVariables.show_all_edges;
        MainWindow.getGraphingPlatform().mc.graphic_alledges.setSelection(SystemVariables.show_all_edges);
        MainWindow.getGraphingPlatform().gc.repaint();
	}	
	
    /**
     * Update the system setting.
     *
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void toggleAllLabels(org.eclipse.swt.events.SelectionEvent e)
	{
        SystemVariables.show_all_labels = !SystemVariables.show_all_labels;
        MainWindow.getGraphingPlatform().mc.graphic_alllabels.setSelection(!SystemVariables.show_all_labels);
        MainWindow.getGraphingPlatform().gc.repaint();
	}
}
