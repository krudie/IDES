/**
 * 
 */
package userinterface;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

/**
 * This class handles the creation and destruction of system resources. It also provides a standardized
 * interface to resource concepts. 
 * 
 * Much of the ressource manager is stolen from the ressource manager made by Michael Wood
 * 
 * @author Edlund
 */


public class ResourceManager {

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
						OPTION_ERRORREPORT = "option_errorreport",
						OPTION_LATEX = "option_latex",
						OPTION_EPS = "option_eps",
						OPTION_TEX = "option_tex",
						OPTION_BORDER = "option_border",
						OPTION_NODE = "option_node",
						OPTION_PSTRICKS = "option_pstricks",
						HELP_HELPTOPICS = "help_helptopics",
						HELP_ABOUT = "help_about",
						LOGO = "logo",
						BIG_LOGO = "big_logo",
						DRAG = "drag";
	
	
	/**
     * This specifies which of the resources expect to have associated images.
     * Only the above String constants may be entered into this array.
     * It is okay to change the ordering.  It won't break the code.
     */
	private final String[] resources_with_images = {
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
		LOGO,
		GRAPHIC_ALLEDGES,
		GRAPHIC_ALLLABELS
	};
	
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
	private static Image images[] = null;
	private static Image icons[] = null;
	private static Image cursors[] = null;
	
	private static ResourceBundle resourceBundle = null;
	
	
	public ResourceManager(){
		resourceBundle = ResourceBundle.getBundle("resource_bundle");
	
		//preloads all the images
		ImageData iconData = null;
				
		// load the images
		images = new Image[resources_with_images.length*3 + simple_images.length];
		for (int i = 0; i < resources_with_images.length; i++) 
		{
			iconData = safeDataStream("/images/icons/" + resources_with_images[i] + ".gif");
			images[(i*3)+1] = new Image(Display.getDefault(), iconData, iconData.getTransparencyMask());
			images[(i*3)]   = new Image(Display.getDefault(), images[(i*3)+1], SWT.IMAGE_GRAY);
			images[(i*3)+2] = new Image(Display.getDefault(), images[(i*3)+1], SWT.IMAGE_DISABLE);
		}
	
	}
	
	/**
	 * Fetch an image.  If fetch fails, display a popup error window.
	 * Also throws text description of the error.
	 * 
	 * @param	location	The path to the image.
	 * @return	The requested image.
	 */	
	private ImageData safeDataStream(String location){
		try {
			return new ImageData(getClass().getResourceAsStream(location));
		}
		catch(Exception e){
			MainWindow.fatalErrorPopup(getString("error.fatal_title"), getMessage("error.image_not_found",location));
		}
		return null;
	}
	
	/**
	 * Return a string from the resource bundle.  Return the key on failure.
	 * 
	 * @param	key		The key for the message string.  
	 * @return	The message string.
	 */	
	public String getMenuText(String key) {
		try { return resourceBundle.getString(key + ".mtext"); }
		catch (Exception e) { return "[" + key + ".mtext]"; }			
	}

	/**
	 * Return a string from the resource bundle.  Return the key on failure.
	 * 
	 * @param	key		The key for the message string.  
	 * @return	The message string.
	 */	
	public String getToolTipText(String key) {
		try { return resourceBundle.getString(key + ".ttext"); }
		catch (Exception e) { return "[" + key + ".ttext]"; }			
	}
	
	/**
	 * Return a string from the resource bundle.  Return the key on failure.
	 * 
	 * @param	key		The key for the message string.  
	 * @return	The message string.
	 */	
	public static String getString(String key){
		try { return resourceBundle.getString(key); }
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
	public static String getMessage(String msg, String arg0) {
		return MessageFormat.format(getString(msg), new String[] {arg0}); 
	}
		
	
}
