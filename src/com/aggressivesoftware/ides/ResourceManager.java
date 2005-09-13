/*
 * Created on Jun 22, 2004
 */
package com.aggressivesoftware.ides;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import com.aggressivesoftware.geometric.Point;

/**
 * This class handles the creation and destruction of system resources. It also provides a standardized
 * interface to resource concepts. 
 * 
 * When you add new menu concepts, first add their text to the resource 
 * bundle according to the obvious naming convention therein, then add the proper sub name to the ResourceHandles
 * variable set.  If there is an associated image, make sure it is properly named and located as specified
 * in the ResourceHandle comments, and add its resource handle to the resources_with_images array
 * 
 * When simply adding an image for non-menu use, create a  resource handle in the Resource Handles for simple images
 * variable set, and add that resource handle to the simple images array
 * 
 * When adding a cursor, add an appropriate handle to the cursor resource handle variable set.  If you are adding
 * a system cursor, then simply add it to the system cursors array.  If you are adding a custom cursor, add the appropriate
 * name to the custom_cursors array and add the required hotspot information to the custom_cursors_hotspots array.
 *
 * All resources can be accessed via the get methods.
 *
 * @author Michael Wood
 */
public class ResourceManager 
{
	/**
     * The platform for which this ResourceManager manages resources.
     */
	private GraphingPlatform gp = null;
	
	/**
     * The ResourceBundle used by this ResourceManager
     */
	private ResourceBundle resource_bundle = null;

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Images ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Resource Handles.
	 * If this resource has associated menu text in the resource_bundle, it must be named: "handle.mtext".
	 * If this resource has associated tool-tip-text in the resource_bundle, it must be named: "handle.ttext".
	 * If this resource has an associated image, it must exist as: "images/icons/handle.gif"
	 */
	public final String FILE_EXPORT_LATEX = "file_export_latex",
						FILE_EXPORT_GIF = "file_export_gif",
						FILE_EXPORT_PNG = "file_export_png",
						FILE_NEW = "file_new",
						FILE_OPEN = "file_open",
						FILE_SAVE = "file_save",
						FILE_SAVEAS = "file_saveas",
						FILE_EXIT = "file_exit",
						EDIT_UNDO = "edit_undo",
						EDIT_REDO = "edit_redo",
						EDIT_COPY = "edit_copy",
						EDIT_PASTE = "edit_paste",
						EDIT_DELETE = "edit_delete",
						GRAPHIC_ZOOM = "graphic_zoom",
						GRAPHIC_CREATE = "graphic_create",
						GRAPHIC_MODIFY = "graphic_modify",
						GRAPHIC_ALLEDGES = "graphic_alledges",
						GRAPHIC_ALLLABELS = "graphic_alllabels",
						GRAPHIC_PRINTAREA = "graphic_printarea",
						GRAPHIC_GRAB = "graphic_grab",
						GRAPHIC_GRID = "graphic_grid",
						MACHINE_CONNECT = "machine_connect",
						MACHINE_ALPHA = "machine_alpha",
						MACHINE_TRACE = "machine_trace",
						OPTION_ERRORREPORT = "option_errorreport",
						OPTION_LATEX = "option_latex",
						OPTION_EPS = "option_eps",
						OPTION_TEX = "option_tex",
						OPTION_BORDER = "option_border",
						OPTION_NODE = "option_node",
						OPTION_PSTRICKS = "option_pstricks",
						HELP_HELPTOPICS = "help_helptopics",
						HELP_ABOUT = "help_about",
						LOGO = "logo";
	
	/**
     * This specifies which of the resources expect to have associated images.
     * Only the above String constants may be entered into this array.
     * It is okay to change the ordering.  It won't break the code.
     */
	private final String[] resources_with_images = 
	{
		FILE_EXPORT_LATEX,
		FILE_EXPORT_GIF,
		FILE_EXPORT_PNG,
		FILE_NEW,
		FILE_OPEN,
		FILE_SAVE,
		FILE_SAVEAS,
		EDIT_UNDO,
		EDIT_REDO,
		EDIT_COPY,
		EDIT_PASTE,
		EDIT_DELETE,
		GRAPHIC_ZOOM,
		GRAPHIC_CREATE,
		GRAPHIC_MODIFY,
		GRAPHIC_PRINTAREA,
		GRAPHIC_GRAB,
		MACHINE_CONNECT,
		MACHINE_ALPHA,
		MACHINE_TRACE,
		LOGO,
		GRAPHIC_ALLEDGES,
		GRAPHIC_ALLLABELS
	};

	/**
	 * Resource Handles for simple images.
	 * If this resource has associated menu text in the resource_bundle, it must be named: "handle.mtext".
	 * If this resource has associated tool-tip-text in the resource_bundle, it must be named: "handle.ttext".
	 * If this resource has an associated image, it must exist as: "images/graphics/handle.gif"
	 */
	public final String BIG_LOGO = "big_logo",
						DRAG = "drag";
	
	/**
     * Every simple image constant declared above must be listed here.
     * It is okay to change the ordering.  It won't break the code.
     */
	private final String[] simple_images = 
	{
		BIG_LOGO,
		DRAG
	};
	
	/**
     * The array where all preloaded images (offered by this ResourceManager) are stored.
     */
	private Image images[];

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Cursors //////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	/**
     * The array where all preloaded cursors (offered by this ResourceManager) are stored.
     */
	private Cursor cursors[];
	
	/**
     * Labeled indexes for the cursors[] array.
     */
	public final int
		GRID_CURSOR = 0,
		ERROR_CURSOR = 1,
		CROSS_CURSOR = 2,
		UPDOWN_CURSOR = 3,
		LEFTRIGHT_CURSOR = 4,
		DIAGONAL_CURSOR = 5,
		WAIT_CURSOR = 6, 
		ARROW_CURSOR = 7, 
		ZOOM_CURSOR = 8,
		GRAB_CURSOR = 9,
		CREATE_CURSOR = 10,
		MODIFY_CURSOR = 11;
	
	/**
     * System cursors for preloading.
     * Their indexes here correspond to indexes in the cursors[] array, and the cursor index labels.
     */
	private final int[] system_cursors = 
	{
		SWT.CURSOR_CROSS,
		SWT.CURSOR_NO,
		SWT.CURSOR_SIZEALL,
		SWT.CURSOR_SIZENS,
		SWT.CURSOR_SIZEWE,
		SWT.CURSOR_SIZENWSE,
		SWT.CURSOR_WAIT,
		SWT.CURSOR_ARROW
	};

	/**
     * Custom cursors for preloading.
     * Their indexes here correspond to indexes in the cursors[] array offset by the number of system_cursors.
     * 
     * note: for "create" the center is at 7,10 and the arrowtip is at 7,0
     */
	private final String[] custom_cursors = 
	{
		"zoom",
		"hand",
		"create",
		"modify"
	};

	/**
     * The hotspots of the custom cursors for preloading.
     */
	private final Point[] custom_cursor_hotspots = 
	{
		new Point(9,9),
		new Point(8,8),
		new Point(7,10),
		new Point(7,19)
	};
		
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Construction and Destruction /////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Construct the ResourceManager.
	 * 
	 * @param gp	The GraphingPlatform for which this ResourceManager manages resources.
	 */	
	public ResourceManager(GraphingPlatform gp)
	{
		this.gp = gp;

		// load the resource bundle
		try	{ resource_bundle = ResourceBundle.getBundle("resource_bundle"); }
		catch (Exception e) { GraphingPlatform.fatalErrorPopup(GraphingPlatform.FATAL_ERROR, GraphingPlatform.LOST_RESOURCE, gp.error_shell);  }	
		
		try 
		{
			ImageData icon_data = null;
			int anchor = 0;
			
			// load the images
			images = new Image[resources_with_images.length*3 + simple_images.length];
			for (int i = 0; i < resources_with_images.length; i++) 
			{
				icon_data = safeDataStream("/images/icons/" + resources_with_images[i] + ".gif");
				images[(i*3)+1] = new Image(gp.display, icon_data, icon_data.getTransparencyMask());
				images[(i*3)]   = new Image(gp.display, images[(i*3)+1], SWT.IMAGE_GRAY);
				images[(i*3)+2] = new Image(gp.display, images[(i*3)+1], SWT.IMAGE_DISABLE);
			}
			anchor = resources_with_images.length*3;
			for (int i = 0; i < simple_images.length; i++) 
			{
				icon_data = safeDataStream("/images/graphics/" + simple_images[i] + ".gif");
				images[anchor+i] = new Image(gp.display, icon_data, icon_data.getTransparencyMask());
			}
						
			// create the cursors
			cursors = new Cursor[system_cursors.length + custom_cursors.length];
			for (int i = 0; i < system_cursors.length; i++) 
			{ cursors[i] = new Cursor(gp.display,system_cursors[i]); }
			for (int i = 0; i < custom_cursors.length; i++) 
			{ cursors[i + system_cursors.length] = new Cursor(gp.display,safeDataStream("/images/cursors/" + custom_cursors[i] + ".gif"),custom_cursor_hotspots[i].x,custom_cursor_hotspots[i].y); }
		} 
		catch (Exception e) 
		{  
			dispose();
			GraphingPlatform.fatalErrorPopup(getString("error.fatal_title"), getString("error.no_images"), gp.error_shell);
		}
	}

	/**
	 * Fetch an image.  If fetch fails, display a popup error window.
	 * Also throws text description of the error.
	 * 
	 * @param	location	The path to the image.
	 * @return	The requested image.
	 */	
	private ImageData safeDataStream(String location)
	{
		try { return new ImageData(getClass().getResourceAsStream(location)); }
		catch(Exception e) 
		{ GraphingPlatform.fatalErrorPopup(getString("error.fatal_title"), getMessage("error.image_not_found",location), gp.error_shell); }
		return null;
	}		
	
	/**
	 * Cleanup and dispose all images and cursors.
	 */	
	public void dispose() 
	{
		if (images != null) 
		{
			for (int i = 0; i < images.length; ++i) 
			{
				final Image image = images[i];
				if (image != null) { image.dispose(); } 
			}
			images = null;
		}
		if (cursors != null) 
		{
			for (int i = 0; i < cursors.length; ++i) 
			{
				final Cursor cursor = cursors[i];
				if (cursor != null) { cursor.dispose(); } 
			}
			cursors = null;
		}
	}		

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Images ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	/**
	 * Test if the given resource_handle is associated with an image.
	 * 
	 * @param	resource_handle		A resource handle constant.
	 * @return	true if the given resource_handle is associated with an image.
	 */	
	public boolean hasImage(String resource_handle)
	{
		for (int i=0; i<resources_with_images.length; i++)
		{ if (resource_handle.equals(resources_with_images[i])) { return true; } }
		return false;
	}		
		
	/**
	 * Return an Image from the images[] array.
	 * 
	 * @param resource_handle	The identifier for this Image.
	 * @param offset			Used to select between variants of this image.
	 * @return The desired Image.
	 */	
	private Image getImage(String resource_handle, int offset) 
	{
		// find the index of this resource within the images specifications array
		int tag=0;
		// try tristate images
		for (tag=0; tag<resources_with_images.length; tag++)
		{ if (resource_handle.equals(resources_with_images[tag])) { break; } }
		if (tag<resources_with_images.length)
		{
			// we found the index of this resource within the specifications array
			// it's first incarnation in the images array is at 3*tag because three copies of each is stored.
			tag = 3*tag + offset;
			if (tag >= 0 && tag < images.length) { if (images[tag] != null) { return images[tag]; } }
		}
		// try simple images
		for (tag=0; tag<simple_images.length; tag++)
		{ if (resource_handle.equals(simple_images[tag])) { break; } }
		if (tag<simple_images.length)
		{
			// we found the index of this resource within the specifications array
			// we need to offset for all the tristate images that came before
			tag = tag + 3*resources_with_images.length;
			if (tag >= 0 && tag < images.length) { if (images[tag] != null) { return images[tag]; } }
		}
		// if we haven't returned yet then we have failed
		//System.out.println(images.length +","+ tag + "," + offset + "," + resource_handle);
		GraphingPlatform.fatalErrorPopup(getString("error.fatal_title"), getString("error.null_image") + "\n" + resource_handle, gp.error_shell);
		return null; 
	}
	
	/**
	 * Return the normal variant of an Image from the images[] array.
	 * 
	 * @param resource_handle	The identifier for this Image.
	 * @return The desired Image.
	 */	
	public Image getImage(String resource_handle) { return getImage(resource_handle,0); }
	
	/**
	 * Return the hot variant of an Image from the images[] array.
	 * 
	 * @param resource_handle	The identifier for this Image.
	 * @return The desired Image.
	 */	
	public Image getHotImage(String resource_handle) { return getImage(resource_handle,1); }

	/**
	 * Return the disabled variant of an Image from the images[] array.
	 * 
	 * @param resource_handle	The identifier for this Image.
	 * @return The desired Image.
	 */	
	public Image getDisabledImage(String resource_handle) { return getImage(resource_handle,2); }

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Cursors //////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * Return a Cursor from the cursors[] array.
	 * 
	 * @param	tag		A label indicating the index of the cursor.
	 * @return	The desired cursor.
	 */	
	public Cursor getCursor(int tag) 
	{
		if (tag >= 0 && tag < cursors.length) { if (cursors[tag] != null) { return cursors[tag]; } }
		GraphingPlatform.fatalErrorPopup(getString("error.fatal_title"), getString("error.null_cursor"), gp.error_shell);
		return null; 
	}		
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// strings //////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	/**
	 * Return a string from the resource bundle.  Return the key on failure.
	 * 
	 * @param	key		The key for the message string.  
	 * @return	The message string.
	 */	
	public String getMenuText(String key) 
	{
		try { return resource_bundle.getString(key + ".mtext"); }
		catch (Exception e) { return "[" + key + ".mtext]"; }			
	}

	/**
	 * Return a string from the resource bundle.  Return the key on failure.
	 * 
	 * @param	key		The key for the message string.  
	 * @return	The message string.
	 */	
	public String getToolTipText(String key) 
	{
		try { return resource_bundle.getString(key + ".ttext"); }
		catch (Exception e) { return "[" + key + ".ttext]"; }			
	}
	
	/**
	 * Return a string from the resource bundle.  Return the key on failure.
	 * 
	 * @param	key		The key for the message string.  
	 * @return	The message string.
	 */	
	public String getString(String key) 
	{
		try { return resource_bundle.getString(key); }
		catch (Exception e) { return "[" + key + "]"; }			
	}

	/**
	 * Return a message from the resource bundle, with inserted arguments.
	 * 
	 * @param	msg		The key for the message string.  The message string must contain placeholders of the form "{i}"  
	 * 					[ie: (key for "hello {0}", with arg "a") --> "hello a"]
	 * @param	arg0	Inserted into the {0} placeholder. 
	 * @return	The formatted message.
	 */	
	public String getMessage(String msg, String arg0) 
	{ return MessageFormat.format(getString(msg), new String[] {arg0}); }

	/**
	 * Return a message from the resource bundle, with inserted arguments.
	 * 
	 * @param	msg		The key for the message string.  The message string must contain placeholders of the form "{i}"  
	 * 					[ie: (key for "hello {0} {1}", with args "a", "b") --> "hello a b"]
	 * @param	arg0	Inserted into the {0} placeholder. 
	 * @param	arg1	Inserted into the {1} placeholder.
	 * @return	The formatted message.
	 */	
	public String getMessage(String msg, String arg0, String arg1) 
	{ return MessageFormat.format(getString(msg), new String[] {arg0,arg1}); }

	/**
	 * Return a message from the resource bundle, with inserted arguments.
	 * 
	 * @param	msg		The key for the message string.  The message string must contain placeholders of the form "{i}"  
	 * 					[ie: (key for "hello {0} {1} {2}", with args "a", "b", "c") --> "hello a b c"]
	 * @param	arg0	Inserted into the {0} placeholder. 
	 * @param	arg1	Inserted into the {1} placeholder.
	 * @param	arg2	Inserted into the {2} placeholder.
	 * @return	The formatted message.
	 */	
	public String getMessage(String msg, String arg0, String arg1, String arg2) 
	{ return MessageFormat.format(getString(msg), new String[] {arg0,arg1,arg2}); }

	/**
	 * Return a message from the resource bundle, with inserted arguments.
	 * 
	 * @param	msg		The key for the message string.  The message string must contain placeholders of the form "{i}"  
	 * 					[ie: (key for "hello {0} {1} {2} {3}", with args "a", "b", "c", "d") --> "hello a b c d"]
	 * @param	arg0	Inserted into the {0} placeholder. 
	 * @param	arg1	Inserted into the {1} placeholder.
	 * @param	arg2	Inserted into the {2} placeholder.
	 * @param	arg3	Inserted into the {3} placeholder.
	 * @return	The formatted message.
	 */	
	public String getMessage(String msg, String arg0, String arg1, String arg2, String arg3) 
	{ return MessageFormat.format(getString(msg), new String[] {arg0,arg1,arg2,arg3}); }

	/**
	 * Return a message from the resource bundle, with inserted arguments.
	 * 
	 * @param	msg		The key for the message string.  The message string must contain placeholders of the form "{i}"  
	 * 					[ie: (key for "hello {0} {1} {2} {3} {4}", with args "a", "b", "c", "d", "e") --> "hello a b c d e"]
	 * @param	arg0	Inserted into the {0} placeholder. 
	 * @param	arg1	Inserted into the {1} placeholder.
	 * @param	arg2	Inserted into the {2} placeholder.
	 * @param	arg3	Inserted into the {3} placeholder.
	 * @param	arg4	Inserted into the {4} placeholder.
	 * @return	The formatted message.
	 */	
	public String getMessage(String msg, String arg0, String arg1, String arg2, String arg3, String arg4) 
	{ return MessageFormat.format(getString(msg), new String[] {arg0,arg1,arg2,arg3,arg4}); }	
}
