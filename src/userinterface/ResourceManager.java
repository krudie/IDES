/**
 * 
 */
package userinterface;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import userinterface.geometric.Point;

/**
 * This class handles the creation and destruction of system resources. It also
 * provides a standardized interface to resource concepts.
 * 
 * Much of the ressource manager is stolen from the ressource manager made by
 * Michael Wood
 * 
 * @author Edlund
 */

public class ResourceManager {

    /**
     * Resource Handles. If this resource has associated menu text in the
     * resource_bundle, it must be named: "handle.mtext". If this resource has
     * associated tool-tip-text in the resource_bundle, it must be named:
     * "handle.ttext". If this resource has an associated image, it must exist
     * as: "images/icons/handle.gif"
     */
    public final static String FILE_NEW_PROJECT = "file_new_project",
            FILE_OPEN_PROJECT = "file_open_project",
            FILE_SAVE_PROJECT = "file_save_project",
            FILE_NEW_AUTOMATON = "file_new_automaton",
            FILE_OPEN_AUTOMATON = "file_open_automaton",
            FILE_SAVE_AUTOMATON = "file_save_automaton",
            FILE_EXIT = "file_exit", EDIT_COPY = "edit_copy",
            EDIT_PASTE = "edit_paste", EDIT_DELETE = "edit_delete",
            GRAPHIC_ZOOM = "graphic_zoom", GRAPHIC_CREATE = "graphic_create",
            GRAPHIC_MODIFY = "graphic_modify",
            GRAPHIC_ALLEDGES = "graphic_alledges",
            GRAPHIC_ALLLABELS = "graphic_alllabels",
            GRAPHIC_PRINTAREA = "graphic_printarea",
            GRAPHIC_GRAB = "graphic_grab", GRAPHIC_GRID = "graphic_grid",
            OPTION_ERRORREPORT = "option_errorreport",
            OPTION_NODE = "option_node", HELP_HELPTOPICS = "help_helptopics",
            HELP_ABOUT = "help_about", LOGO = "logo", BIG_LOGO = "big_logo",
            DRAG = "drag";

    /**
     * This specifies which of the resources expect to have associated images.
     * Only the above String constants may be entered into this array. It is
     * okay to change the ordering. It won't break the code.
     */
    private final static String[] resourcesWithImages = { FILE_NEW_PROJECT,
            FILE_OPEN_PROJECT, FILE_SAVE_PROJECT, FILE_NEW_AUTOMATON,
            FILE_OPEN_AUTOMATON, FILE_SAVE_AUTOMATON, EDIT_COPY, EDIT_PASTE,
            EDIT_DELETE, GRAPHIC_ZOOM, GRAPHIC_CREATE, GRAPHIC_MODIFY,
            GRAPHIC_PRINTAREA, GRAPHIC_GRAB, LOGO, GRAPHIC_ALLEDGES,
            GRAPHIC_ALLLABELS };

    /**
     * Every simple image constant declared above must be listed here. It is
     * okay to change the ordering. It won't break the code.
     */
    private final static String[] simpleImages = { BIG_LOGO, DRAG };

    /**
     * The array where all preloaded cursors (offered by this ResourceManager)
     * are stored.
     */
    private static Cursor cursors[];

    /**
     * Labeled indexes for the cursors[] array.
     */
    public final static int GRID_CURSOR = 0, ERROR_CURSOR = 1,
            CROSS_CURSOR = 2, UPDOWN_CURSOR = 3, LEFTRIGHT_CURSOR = 4,
            DIAGONAL_CURSOR = 5, WAIT_CURSOR = 6, ARROW_CURSOR = 7,
            ZOOM_CURSOR = 8, GRAB_CURSOR = 9, CREATE_CURSOR = 10,
            MODIFY_CURSOR = 11;

    /**
     * System cursors for preloading. Their indexes here correspond to indexes
     * in the cursors[] array, and the cursor index labels.
     */
    private final static int[] systemCursors = { SWT.CURSOR_CROSS,
            SWT.CURSOR_NO, SWT.CURSOR_SIZEALL, SWT.CURSOR_SIZENS,
            SWT.CURSOR_SIZEWE, SWT.CURSOR_SIZENWSE, SWT.CURSOR_WAIT,
            SWT.CURSOR_ARROW };

    /**
     * Custom cursors for preloading. Their indexes here correspond to indexes
     * in the cursors[] array offset by the number of system_cursors.
     * 
     * note: for "create" the center is at 7,10 and the arrowtip is at 7,0
     */
    private static final String[] customCursors = { "zoom", "hand", "create",
            "modify" };

    /**
     * The hotspots of the custom cursors for preloading.
     */
    private final static Point[] customCursorHotspots = { new Point(9, 9),
            new Point(8, 8), new Point(7, 10), new Point(7, 19) };

    /**
     * The array where all preloaded images (offered by this ResourceManager)
     * are stored.
     */
    private static Image images[] = null;

    private static ResourceBundle resourceBundle = null;

    public ResourceManager() {
        resourceBundle = ResourceBundle.getBundle("resource_bundle");

        // preloads all the images
        ImageData iconData = null;

        // load the images
        images = new Image[resourcesWithImages.length * 3 + simpleImages.length];
        for (int i = 0; i < resourcesWithImages.length; i++) {
            iconData = safeDataStream("/images/icons/" + resourcesWithImages[i]
                    + ".gif");
            images[(i * 3) + 1] = new Image(Display.getDefault(), iconData,
                    iconData.getTransparencyMask());
            images[(i * 3)] = new Image(Display.getDefault(),
                    images[(i * 3) + 1], SWT.IMAGE_GRAY);
            images[(i * 3) + 2] = new Image(Display.getDefault(),
                    images[(i * 3) + 1], SWT.IMAGE_DISABLE);
        }

        int anchor = resourcesWithImages.length * 3;
        for (int i = 0; i < simpleImages.length; i++) {
            iconData = safeDataStream("/images/graphics/" + simpleImages[i]
                    + ".gif");
            images[anchor + i] = new Image(Display.getDefault(), iconData,
                    iconData.getTransparencyMask());
        }
        // create the cursors
        cursors = new Cursor[systemCursors.length + customCursors.length];
        for (int i = 0; i < systemCursors.length; i++) {
            cursors[i] = new Cursor(Display.getDefault(), systemCursors[i]);
        }
        for (int i = 0; i < customCursors.length; i++) {
            cursors[i + systemCursors.length] = new Cursor(
                    Display.getDefault(), safeDataStream("/images/cursors/"
                            + customCursors[i] + ".gif"),
                    customCursorHotspots[i].getX(), customCursorHotspots[i].getY());
        }

    }

    /**
     * Fetch an image. If fetch fails, display a popup error window. Also throws
     * text description of the error.
     * 
     * @param location
     *            The path to the image.
     * @return The requested image.
     */
    private ImageData safeDataStream(String location) {
        try {
            return new ImageData(getClass().getResourceAsStream(location));
        } catch (Exception e) {
            MainWindow.fatalErrorPopup(getString("error.fatal_title"),
                    getMessage("error.image_not_found", location));
        }
        return null;
    }

    /**
     * Cleanup and dispose all images and cursors.
     */
    public void dispose() {
        if (images != null) {
            for (int i = 0; i < images.length; ++i) {
                final Image image = images[i];
                if (image != null) {
                    image.dispose();
                }
            }
            images = null;
        }
        if (cursors != null) {
            for (int i = 0; i < cursors.length; ++i) {
                final Cursor cursor = cursors[i];
                if (cursor != null) {
                    cursor.dispose();
                }
            }
            cursors = null;
        }
    }

    /**
     * Return a string from the resource bundle. Return the key on failure.
     * 
     * @param key
     *            The key for the message string.
     * @return The message string.
     */
    public static String getMenuText(String key) {
        try {
            return resourceBundle.getString(key + ".mtext");
        } catch (Exception e) {
            return "[" + key + ".mtext]";
        }
    }

    /**
     * Return a string from the resource bundle. Return the key on failure.
     * 
     * @param key
     *            The key for the message string.
     * @return The message string.
     */
    public static String getToolTipText(String key) {
        try {
            return resourceBundle.getString(key + ".ttext");
        } catch (Exception e) {
            return "[" + key + ".ttext]";
        }
    }

    /**
     * Return a string from the resource bundle. Return the key on failure.
     * 
     * @param key
     *            The key for the message string.
     * @return The message string.
     */
    public static String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (Exception e) {
            return "[" + key + "]";
        }
    }

    /**
     * Return a message from the resource bundle, with inserted arguments.
     * 
     * @param msg
     *            The key for the message string. The message string must
     *            contain placeholders of the form "{i}" [ie: (key for "hello
     *            {0}", with arg "a") --> "hello a"]
     * @param arg0
     *            Inserted into the {0} placeholder.
     * @return The formatted message.
     */
    public static String getMessage(String msg, String arg0) {
        return MessageFormat.format(getString(msg),
                (Object[]) new String[] { arg0 });
    }

    public static boolean hasImage(String resourceHandle) {
        for (int i = 0; i < resourcesWithImages.length; i++) {
            if (resourceHandle.equals(resourcesWithImages[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return an Image from the images[] array.
     * 
     * @param resourceHandle
     *            The identifier for this Image.
     * @param offset
     *            Used to select between variants of this image.
     * @return The desired Image.
     */
    private static Image getImage(String resourceHandle, int offset) {
        try {
            // find the index of this resource within the images specifications
            // array
            int tag;
            // try tristate images
            for (tag = 0; tag < resourcesWithImages.length; tag++) {
                if (resourceHandle.equals(resourcesWithImages[tag])) {
                    tag = 3 * tag + offset;
                    return images[tag];
                }
            }

            // try simple images
            for (tag = 0; tag < simpleImages.length; tag++) {
                if (resourceHandle.equals(simpleImages[tag])) {
                    tag = tag + 3 * resourcesWithImages.length;
                    return images[tag];
                }
            }
        } catch (Exception e) {
            // Ignore that we have an error an continue to the final part of the
            // method
        }
        // if we haven't returned yet then we have failed
        MainWindow.fatalErrorPopup(getString("error.fatal_title"),
                getString("error.null_image") + "\n" + resourceHandle);
        return null;
    }

    /**
     * Return the normal variant of an Image from the images[] array.
     * 
     * @param resourceHandle
     *            The identifier for this Image.
     * @return The desired Image.
     */
    public static Image getImage(String resourceHandle) {
        return getImage(resourceHandle, 0);
    }

    /**
     * Return the hot variant of an Image from the images[] array.
     * 
     * @param resourceHandle
     *            The identifier for this Image.
     * @return The desired Image.
     */
    public static Image getHotImage(String resourceHandle) {
        return getImage(resourceHandle, 1);
    }

    /**
     * Return the disabled variant of an Image from the images[] array.
     * 
     * @param resourceHandle
     *            The identifier for this Image.
     * @return The desired Image.
     */
    public static Image getDisabledImage(String resourceHandle) {
        return getImage(resourceHandle, 2);
    }

    /**
     * Return a Cursor from the cursors[] array.
     * 
     * @param tag
     *            A label indicating the index of the cursor.
     * @return The desired cursor.
     */
    public static Cursor getCursor(int tag) {
        if (tag >= 0 && tag < cursors.length) {
            if (cursors[tag] != null) {
                return cursors[tag];
            }
        }
        MainWindow.fatalErrorPopup(getString("error.fatal_title"),
                getString("error.null_cursor"));
        return null;
    }
}
