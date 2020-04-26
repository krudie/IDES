package ides.api.core;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import ides.api.cache.Cache;
import ides.api.copypaste.CopyPasteManager;
import ides.api.latex.LatexManager;
import ides.api.notice.NoticeManager;
import ides.api.plugin.io.IOSubsytem;
import ides.api.plugin.model.DESModel;
import ides.api.undo.UndoManager;
import io.IOCoordinator;
import main.HubBackend;
import main.WorkspaceBackend;
import services.cache.CacheBackend;
import services.ccp.CopyPasteBackend;
import services.latex.LatexBackend;
import services.notice.NoticeBackend;
import services.undo.UndoBackend;
import ui.OptionsWindow;
import util.AnnotationKeys;

/**
 * The main hub of the program. Serves to get references to all objects of
 * interest (such as settings, main window, loaded resources, etc.) Plugins are
 * encouraged to make use of these services.
 * 
 * @author Lenko Grigorov
 */
public class Hub {

    private Hub() {
    }

    /**
     * The system-wide bundle with strings. Should be used for any messages and
     * strings to appear in IDES (FIXME: save for the commmands which are managed by
     * an external library?). The native IDES bundle occupies the first element of
     * the array. Plugins register their own bundles with
     * {@link #addResouceBundle(ResourceBundle)}; these bundles get added to the
     * array. The strings are accessible through the {@link #string(String)} method
     * which scans all elements of the array. For examples of use, see
     * {@link #storePersistentData()}.
     * 
     * @see #addResouceBundle(ResourceBundle)
     * @see #string(String)
     */
    private static ResourceBundle stringResource[] = new ResourceBundle[0];

    /**
     * Gets from the resource bundle {@link #stringResource} the string which
     * corresponds to the given key. Should be used for any messages and strings to
     * appear in IDES (FIXME: save for the commmands which are managed by an
     * external library?).
     * 
     * @param key lookup key for the string
     * @return the string corresponding to the key
     * @see #stringResource
     */
    public synchronized static String string(String key) {
        for (int i = 0; i < stringResource.length; ++i) {
            try {
                return stringResource[i].getString(key);
            } catch (MissingResourceException e) {
            }
        }
        displayAlert(string("missingResourceKey"));
        throw new MissingResourceException("Cannot look up the text string requested by a module of the program.",
                "main.Main", key);
    }

    /**
     * Adds a string resource bundle to the set of bundles which are used to look up
     * strings. Can be used by plugins to add their own string bundles at startup.
     * 
     * @param bundle the string resource bundle to be added
     * @see #stringResource
     * @see #string(String)
     */
    public synchronized static void addResouceBundle(ResourceBundle bundle) {
        ResourceBundle[] temp = stringResource;
        stringResource = new ResourceBundle[stringResource.length + 1];
        System.arraycopy(temp, 0, stringResource, 0, temp.length);
        stringResource[temp.length] = bundle;
    }

    /**
     * Provides access to a list of settings which persist over sessions.
     * 
     * @return the list of persistent settings
     */
    public static PersistentProperties getPersistentData() {
        return HubBackend.persistentData;
    }

    /**
     * Stores the settings from the list of settings {@link #getPersistentData()}
     * into the file <code>settings.ini</code>. If this fails, it shows a message
     * box to the user.
     */
    public synchronized static void storePersistentData() {
        HubBackend.storePersistentData();
    }

    /**
     * Gets the main window of IDES.
     * 
     * @return the main window
     */
    public static Frame getMainWindow() {
        return Hub.getUserInterface().getWindow();
    }

    /**
     * Gets the main UI of IDES.
     * 
     * @return the main UI of IDES
     */
    public static UserInterface getUserInterface() {
        return HubBackend.getUserInterface();
    }

    /**
     * Register an options pane with the options dialog box. To be called just once
     * per session for each options pane (e.g., when loading IDES or a plugin).
     * 
     * @param pane the {@link ides.api.core.OptionsPane} that will be registered
     * @see ides.api.core.OptionsPane
     * @see ui.OptionsWindow
     */
    public static void registerOptionsPane(OptionsPane pane) {
        OptionsWindow.registerOptionsPane(pane);
    }

    /**
     * Request that the options dialog box opens up on the screen and shows the
     * options for the {@link ides.api.core.OptionsPane} with the given title.
     * 
     * @param title the title of the {@link ides.api.core.OptionsPane} to be
     *              displayed
     * @see ides.api.core.OptionsPane
     * @see ui.OptionsWindow
     */
    public static void openOptionsPane(String title) {
        new OptionsWindow(title);
    }

    /**
     * Displays a dialog box that shows an alert message to the user. Intended use
     * is to announce errors and problems in an aesthetically-pleasing way.
     * 
     * @param message message to be displayed
     */
    public static void displayAlert(String message) {
        // SwingUtilities.invokeLater(new Runnable()
        // {
        // public void run()
        // {
        javax.swing.JOptionPane.showMessageDialog(getMainWindow(), message, string("message"),
                javax.swing.JOptionPane.WARNING_MESSAGE);
        // }
        // });
    }

    /**
     * Suggests the position of the top-left corner for a dialog box with the given
     * dimensions so that it is located in the center of the main window of the
     * application.
     * 
     * @param d the dimension of the dialog box
     * @return the suggested position of the top-left corner of the dialog box
     */
    public static Point getCenteredLocationForDialog(Dimension d) {
        Point p = new Point();
        p.x = (getMainWindow().getWidth() - d.width) / 2 + getMainWindow().getLocation().x;
        p.y = (getMainWindow().getHeight() - d.height) / 3 + getMainWindow().getLocation().y;
        if (p.x < 0) {
            p.x = 0;
        }
        if (p.y < 0) {
            p.y = 0;
        }
        return p;
    }

    /**
     * Gets the main workspace.
     * 
     * @return the main workspace
     */
    public static Workspace getWorkspace() {
        return WorkspaceBackend.instance();
    }

    /**
     * Gets a resource from the IDES JAR file (icons and such).
     * 
     * @param name the name of the resource
     * @return URL that points to the resource
     */
    public static URL getIDESResource(String name) {
        return Hub.class.getClassLoader().getResource(name);
    }

    /**
     * Gets a resource from the JAR file of a plugin (icons and such).
     * 
     * @param context the context for the local resource (e.g., a class from the
     *                plugin)
     * @param name    the name of the resource
     * @return URL that points to the resource
     */
    public static URL getLocalResource(Class<?> context, String name) {
        return context.getClassLoader().getResource(name);
    }

    /**
     * Provides access to the {@link Cache} maintained by IDES.
     * 
     * @return the {@link Cache} maintained by IDES
     */
    public static Cache getCache() {
        return CacheBackend.instance();
    }

    /**
     * Provides access to the LaTeX rendering services in IDES.
     * 
     * @return the IDES {@link LatexManager}
     */
    public static LatexManager getLatexManager() {
        return LatexBackend.instance();
    }

    /**
     * Provides access to the notice posting services in IDES.
     * 
     * @return the IDES {@link NoticeManager}
     */
    public static NoticeManager getNoticeManager() {
        return NoticeBackend.instance();
    }

    /**
     * Provides access to the Undo facility in IDES.
     * 
     * @return the IDES {@link UndoManager}
     */
    public static UndoManager getUndoManager() {
        return UndoBackend.instance();
    }

    /**
     * Provides access to the IO subsystem of IDES.
     * 
     * @return the IDES IO subsystem
     */
    public static IOSubsytem getIOSubsystem() {
        return IOCoordinator.getInstance();
    }

    /**
     * Provides access to the cut, copy and paste facility in IDES.
     * 
     * @return the IDES {@link CopyPasteManager}
     */
    public static CopyPasteManager getCopyPasteManager() {
        return CopyPasteBackend.instance();
    }

    /**
     * Retrieve the user-defined text annotation of the given model. When the model
     * is opened, the text appears in the "Annotations" tab.
     * 
     * @param model the model
     * @return the user-defined text annotation of the given model
     */
    public static String getUserTextAnnotation(DESModel model) {
        Object o = model.getAnnotation(AnnotationKeys.TEXT_ANNOTATION);
        if (o == null || !(o instanceof String)) {
            return "";
        }
        return (String) o;
    }

    /**
     * Set the user-defined text annotation of the given model. When the model is
     * opened, the text appears in the "Annotations" tab.
     * 
     * @param model the model
     * @param text  the text of the annotation
     */
    public static void setUserTextAnnotation(DESModel model, String text) {
        model.setAnnotation(AnnotationKeys.TEXT_ANNOTATION, text);
        if (getWorkspace().getActiveModel() == model) {
            getWorkspace().fireRepaintRequired();
        }
    }
}