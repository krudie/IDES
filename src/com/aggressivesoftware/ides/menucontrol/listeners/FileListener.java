/*
 * Created on Jun 22, 2004
 */
package com.aggressivesoftware.ides.menucontrol.listeners;
 
import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;

import com.aggressivesoftware.general.Ascii;
import com.aggressivesoftware.general.ConvertSWT;
import com.aggressivesoftware.geometric.Box;
import com.aggressivesoftware.ides.GraphingPlatform;
import com.sun.jimi.core.Jimi;

/**
 * This class handles all events the fall under the "File" menu concept.
 * 
 * @author Michael Wood
 */
public class FileListener extends AbstractListener
{
	/**
     * The save location of the current object, if any.
     */
	private String last_good_location = null;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ListenersFile construction /////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

    /**
     * Construct the ListenersFile.
     * 
     * @param	graphing_platform		The platform in which this ListenersFile will exist.
     */
	public FileListener(GraphingPlatform graphing_platform)
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
		if (resource_handle.equals(gp.rm.FILE_EXPORT_LATEX)) { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { exportLatex(e); } }; }
		if (resource_handle.equals(gp.rm.FILE_EXPORT_GIF))   { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { exportGifPng(e,"gif"); } }; }
		if (resource_handle.equals(gp.rm.FILE_EXPORT_PNG))   { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { exportGifPng(e,"png"); } }; }
		if (resource_handle.equals(gp.rm.FILE_NEW))          { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { newSystem(e);   } }; }
		if (resource_handle.equals(gp.rm.FILE_OPEN))         { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { open(e);        } }; }
		if (resource_handle.equals(gp.rm.FILE_SAVE))         { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { save(e);        } }; }
		if (resource_handle.equals(gp.rm.FILE_SAVEAS))       { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { saveAs(e);      } }; }
		if (resource_handle.equals(gp.rm.FILE_EXIT))         { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { exit(e);        } }; }		
		System.out.println("Error: no match for resource_handle = " + resource_handle);
		return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { } };
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// listeners //////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
	
    /**
     * Export the selection area to latex.
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void exportLatex(org.eclipse.swt.events.SelectionEvent e)
	{
		if (gp.gc.gm.print_area.isVisible())
		{	
			String eps_location = "";
			String tex_location = "";
			
			if (gp.sv.export_latex_to_eps)
			{
				// output to eps
			    try { eps_location = getSaveLocation(gp.rm.getString("file_saveas.ttext"), new String[] {"*.eps"}, Ascii.getFileNameFromPathWithNewExtension(last_good_location,".eps")); }
			    catch (Exception ex) { ex.printStackTrace(); }
			}
			if (gp.sv.export_latex_to_tex)
			{
				// output to tex
			    try { tex_location = getSaveLocation(gp.rm.getString("file_saveas.ttext"), new String[] {"*.tex"}, Ascii.getFileNameFromPathWithNewExtension(last_good_location,".tex")); }
			    catch (Exception ex) { ex.printStackTrace(); }
			}
			// else we will just output to the textarea in the output tab

			gp.gc.io.exportLatex(eps_location,tex_location);
		}
		else
		{
			MessageBox print_area = new MessageBox(gp.shell, SWT.ICON_WARNING | SWT.OK); 
			print_area.setText(gp.rm.getString("file_sys.warning"));
			print_area.setMessage(gp.rm.getString("file_sys.print_area"));
			print_area.open();
		}
	}	
		
    /**
     * Export the selection area to gif or png.
     * 
     * @param	e			The SelectionEvent that initiated this action.
     * @param	extenstion	Must be "gif" or "png"
     */
	public void exportGifPng(org.eclipse.swt.events.SelectionEvent e, String extension)
	{		
		if (extension.equals("gif") || extension.equals("png"))
		{
			if (gp.gc.gm.print_area.isVisible())
			{	
			    try 
				{ 
					String save_location = getSaveLocation(gp.rm.getString("file_saveas.ttext"), new String[] {"*." + extension}, Ascii.getFileNameFromPathWithNewExtension(last_good_location,"." + extension));
					if (save_location != null)
					{ 
						Box box = gp.gc.gm.print_area.getBox();
						
						float fudge_w = 0;
						float fudge_h = 0;
						float fudge_x = 0;
						float fudge_y = 0;
						if (gp.sv.export_with_border && (gp.gc.gm.scale == 1 || gp.gc.gm.scale == 2 || gp.gc.gm.scale == 0.5))
						{
							gp.gc.gm.print_area.draw_solid = true;
							if (gp.gc.gm.scale == 1)
							{
								fudge_w = 1;
								fudge_h = 1;
								fudge_x = 0;
								fudge_y = 0;
							}
							else if (gp.gc.gm.scale == 2)
							{
								fudge_w = (float)0.5;
								fudge_h = (float)0.5;
								fudge_x = 0;
								fudge_y = 0;
							}
							else if (gp.gc.gm.scale == 0.5)
							{
								fudge_w = 1;
								fudge_h = 2;
								fudge_x = -1;
								fudge_y = -1;							
							}
						}
						else
						{ gp.gc.gm.print_area.setVisible(false); }
						gp.gc.j2dcanvas.repaint();
						gp.gc.j2dcanvas.update();
						
						GC gc = new GC(gp.gc.j2dcanvas);
						Image image = new Image(gp.display,Math.round((box.w()+fudge_w)*gp.gc.gm.scale),Math.round((box.h()+fudge_h)*gp.gc.gm.scale));
						gc.copyArea(image,Math.round((box.x1()+fudge_x)*gp.gc.gm.scale),Math.round((box.y1()+fudge_y)*gp.gc.gm.scale));
						
						if (extension.equals("png"))
						{
							try {Jimi.putImage("image/png", ConvertSWT.convertToAWT(image.getImageData()), save_location);}
							catch(Exception exx) { exx.printStackTrace(); }

							//PNG not implemented in swt
							//ImageLoader image_loader = new ImageLoader();
						 	//image_loader.data = new ImageData[] {image.getImageData()};
						 	//image_loader.save(save_location,SWT.IMAGE_PNG); 
						}
						else if (extension.equals("gif"))
						{
							// currently JIMI can't encode gifs
							//try {Jimi.putImage("image/gif", bi, save_location);}
							//catch(Exception exx) { exx.printStackTrace(); }
							// we have to use JIMI to reduce the colors to 256
							// then we use Acme to encode the gif
					 		Acme.JPM.Encoders.ImageEncoder ie =	new Acme.JPM.Encoders.GifEncoder(ConvertSWT.getReducedFromSWT(image.getImageData()), new FileOutputStream(save_location));
						 	ie.encode();
						}
						
						image.dispose();
						gc.dispose();
						
						if (gp.gc.gm.print_area.draw_solid)
						{ gp.gc.gm.print_area.draw_solid = false; }
						else
						{ gp.gc.gm.print_area.setVisible(true); }
						gp.gc.j2dcanvas.repaint();
						gp.gc.j2dcanvas.update();
					}		    	
				}
			    catch (Exception ex)	{ ex.printStackTrace(); }
			}
			else
			{
				MessageBox print_area = new MessageBox(gp.shell, SWT.ICON_WARNING | SWT.OK); 
				print_area.setText(gp.rm.getString("file_sys.warning"));
				print_area.setMessage(gp.rm.getString("file_sys.print_area"));
				print_area.open();
			}
		}
	}	
	
    /**
     * Create a new graph (essentially just reset the program)
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void newSystem(org.eclipse.swt.events.SelectionEvent e)
	{
		if (gp.gc.io.unsaved_changes)
		{
			MessageBox unsaved_changes = new MessageBox(gp.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL); 
			unsaved_changes.setText(gp.rm.getString("file_sys.warning"));
			unsaved_changes.setMessage(gp.rm.getString("file_sys.unsaved_changes"));
			int response = unsaved_changes.open();
			switch(response)
			{
				case SWT.YES: 
					// first perform the save procedure then perform the new procedure
					save(e);
					newSystem(e);
					return;
				case SWT.NO: 
					// perform new procedure
					gp.gc.resetState();
					last_good_location = null;
					refreshWindowTitle();
					break;
				case SWT.CANCEL:
					// do nothing
					break;
			}
		}
		else
		{
			gp.gc.resetState();
			last_good_location = null;
			refreshWindowTitle();
		}
	}	
	
    /**
     * Open a gml file and load the graph.
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void open(org.eclipse.swt.events.SelectionEvent e)
	{
		String open_location = null;
		
		if (gp.gc.io.unsaved_changes)
		{
			MessageBox unsaved_changes = new MessageBox(gp.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL); 
			unsaved_changes.setText(gp.rm.getString("file_sys.warning"));
			unsaved_changes.setMessage(gp.rm.getString("file_sys.unsaved_changes"));
			int response = unsaved_changes.open();
			switch(response)
			{
				case SWT.YES: 
					// first perform the save procedure then perform the open procedure
					save(e);
					open(e);
					return;
				case SWT.NO: 
					// perform open procedure
					open_location = getOpenLocation();
					break;
				case SWT.CANCEL:
					// do nothing
					break;
			}
		}
		else
		{
			open_location = getOpenLocation();
		}
		
		if (open_location != null)
		{
			boolean success = gp.gc.io.open(open_location);
			if (success)
			{ 
				last_good_location = open_location;
				refreshWindowTitle(); 
			}
		}
	}	
	
    /**
     * Save the current data.
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void save(org.eclipse.swt.events.SelectionEvent e) 
	{
		if (last_good_location == null)
		{ 
			String save_location = getSaveLocation(gp.rm.getString("file_save.ttext"), new String[] {"*.gml", "*.*"}, "");
			if (save_location != null)
			{ 
				boolean success = gp.gc.io.save(save_location);
				if (success)
				{ 
					last_good_location = save_location;
					refreshWindowTitle(); 
				}
			}
		}
		else
		{ 
			boolean success = gp.gc.io.save(last_good_location);
			if (!success)
			{ 
				last_good_location = null;
				refreshWindowTitle(); 
			}
		}
	} 
	
    /**
     * Save the current data.
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void saveAs(org.eclipse.swt.events.SelectionEvent e) 
	{
		String save_location = getSaveLocation(gp.rm.getString("file_saveas.ttext"), new String[] {"*.gml", "*.*"}, Ascii.getFileNameFromPath(last_good_location));
		if (save_location != null)
		{ 
			boolean success = gp.gc.io.save(save_location);
			if (success)
			{ 
				last_good_location = save_location;
				refreshWindowTitle(); 
			}
		}
	}

    /**
     * Exit the system.
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void exit(org.eclipse.swt.events.SelectionEvent e)
	{
		if (gp.gc.io.unsaved_changes)
		{
			MessageBox unsaved_changes = new MessageBox(gp.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL); 
			unsaved_changes.setText(gp.rm.getString("file_sys.warning"));
			unsaved_changes.setMessage(gp.rm.getString("file_sys.unsaved_changes"));
			int response = unsaved_changes.open();
			switch(response)
			{
				case SWT.YES: 
					// first perform the save procedure then perform the exit procedure
					save(e);
					exit(e);
					return;
				case SWT.NO: 
					// perform exit procedure
					gp.td.dispose();
					gp.shell.dispose();
					break;
				case SWT.CANCEL:
					// do nothing
					break;
			}
		}
		else
		{
			gp.td.dispose();
			gp.shell.dispose();
		}
	}	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// helper methods /////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////				
	
    /**
     * Display a file dialog to let the user choose the save location.
     * If the specified locaton already exists, the user must confirm they wish to overwrite it.
     * If they confirm overwrite, this method does not delete the existing file.
     *
     * @param	dialog_title		The title for the file dialog.
     * @param	filter_extensions	The extensions filter for the saveas dialogue. i.e. "*.*"
     * @return	The save location specified by the user, or null.
     */
	private String getSaveLocation(String dialog_title, String[] filter_extensions, String optional_name)
	{
		FileDialog save_dialog = new FileDialog(gp.shell, SWT.SAVE); 
		save_dialog.setText(dialog_title); 
		save_dialog.setFileName(optional_name);
		if (gp.sv.last_used_path != null && gp.sv.last_used_path.length() > 0)
		{ save_dialog.setFilterPath(gp.sv.last_used_path); }
		save_dialog.setFilterExtensions(filter_extensions); 
		String save_location = save_dialog.open();
		gp.sv.last_used_path = save_location;
		
		if (save_location != null)
		{
			File test_file = new File(save_location);
			if (test_file.exists())
			{		
				test_file = null;
				MessageBox confirm_overwrite = new MessageBox(gp.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO); 
				confirm_overwrite.setText(dialog_title);
				confirm_overwrite.setMessage(gp.rm.getMessage("file_sys.confirm_overwrite",save_location));
				int response = confirm_overwrite.open();
				switch(response)
				{
					case SWT.YES: 
						// continue with the operation
						return save_location;
					case SWT.NO: 
						// let them choose a different file
						return getSaveLocation(dialog_title, filter_extensions, optional_name);
				}
			}
		}
		
		return save_location;
	}
	
    /**
     * Display a file dialog to let the user choose the open location.
     * 
     * @return	The open location specified by the user, or null.
     */
	private String getOpenLocation()
	{
		FileDialog open_dialog = new FileDialog(gp.shell, SWT.OPEN); 
		open_dialog.setText(gp.rm.getString("file_open.ttext")); 
		if (gp.sv.last_used_path != null && gp.sv.last_used_path.length() > 0)
		{ open_dialog.setFilterPath(gp.sv.last_used_path); }
		open_dialog.setFilterExtensions(new String[] {"*.gml", "*.*"}); 
		String open_location = open_dialog.open();
		gp.sv.last_used_path = open_location;
		return open_location;
	}
	
    /**
     * Update the title of the main gui window to reflect the current file name.
     */
	private void refreshWindowTitle()
	{
		String file_name = "";
		if (last_good_location != null)
		{ file_name = Ascii.getFileNameFromPath(last_good_location); }
				
		if (file_name.length() > 0) 
		{ 
			gp.shell.setText(file_name + " - " + gp.rm.getString("window.title"));
			gp.tbitm_graph_canvas.setText(file_name); 
		}
		else 
		{ 
			gp.shell.setText(gp.rm.getString("window.title"));
			gp.tbitm_graph_canvas.setText(gp.rm.getString("window.graph_tab.text")); 
		}
	}
}