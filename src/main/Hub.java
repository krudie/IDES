package main;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JFrame;

import pluggable.ui.OptionsPane;
import ui.MainWindow;
import ui.OptionsWindow;

/**
 * The main hub of the program. Serves to get references to all objects of
 * interest (such as settings, main window, loaded resources, etc.) Plugins are
 * encouraged to make use of these services.
 * 
 * @author Lenko Grigorov
 */
public class Hub
{

	private Hub()
	{
	}

	/**
	 * Contains (key,value) pairs which are stored in the settings file
	 * (settings.ini). Accessible to everyone.
	 */
	public static final PersistentProperties persistentData = new PersistentProperties();

	/**
	 * The system-wide bundle with strings. Should be used for any messages and
	 * strings to appear in IDES (FIXME: save for the commmands which are
	 * managed by an external library?). The native IDES bundle occupies the
	 * first element of the array. Plugins register their own bundles with
	 * {@link #addResouceBundle(ResourceBundle)}; these bundles get added to
	 * the array. The strings are accessible through the {@link #string(String)}
	 * method which scans all elements of the array. For examples of use, see
	 * {@link #storePersistentData()}.
	 * 
	 * @see #addResouceBundle(ResourceBundle)
	 * @see #string(String)
	 */
	private static ResourceBundle stringResource[] = new ResourceBundle[0];

	/**
	 * The main window of the application.
	 * 
	 * @see #setUIShell(UIShell)
	 */
	private static UIShell mainWindow = null;

	/**
	 * Gets from the resource bundle {@link #stringResource} the string which
	 * corresponds to the given key. Should be used for any messages and strings
	 * to appear in IDES (FIXME: save for the commmands which are managed by an
	 * external library?).
	 * 
	 * @param key
	 *            lookup key for the string
	 * @return the string corresponding to the key
	 * @see #stringResource
	 */
	public synchronized static String string(String key)
	{
		for (int i = 0; i < stringResource.length; ++i)
		{
			try
			{
				return stringResource[i].getString(key);
			}
			catch (MissingResourceException e)
			{
			}
		}
		displayAlert(string("missingResourceKey"));
		throw new MissingResourceException(
				"Cannot look up the text string requested by a module of the program.",
				"main.Main",
				key);
	}

	/**
	 * Adds a string resource bundle to the set of bundles which are used to
	 * look up strings. Can be used by plugins to add their own string bundles
	 * at startup.
	 * 
	 * @param bundle
	 *            the string resource bundle to be added
	 * @see #stringResource
	 * @see #string(String)
	 */
	public synchronized static void addResouceBundle(ResourceBundle bundle)
	{
		ResourceBundle[] temp = stringResource;
		stringResource = new ResourceBundle[stringResource.length + 1];
		System.arraycopy(temp, 0, stringResource, 0, temp.length);
		stringResource[temp.length] = bundle;
	}

	/**
	 * Loads the settings from the file <code>settings.ini</code> into the
	 * list of settings {@link #persistentData}.
	 * 
	 * @throws IOException
	 * @see #persistentData
	 * @see #storePersistentData()
	 */
	synchronized static void loadPersistentData() throws IOException
	{
		BufferedInputStream in = null;
		try
		{
			in = new BufferedInputStream(new FileInputStream("settings.ini"));
			Hub.persistentData.load(in);
		}
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
				}
			}
			catch (IOException e)
			{
			}
		}
	}

	/**
	 * Stores the settings from the list of settings {@link #persistentData}
	 * into the file <code>settings.ini</code>. If this fails, it shows a
	 * message box to the user.
	 */
	public synchronized static void storePersistentData()
	{
		String comments = Hub.string("settingsFileComments");
		BufferedOutputStream out = null;
		try
		{
			out = new BufferedOutputStream(new FileOutputStream("settings.ini"));
			Hub.persistentData.store(out, comments);
		}
		catch (IOException e)
		{
			displayAlert(string("cantStoreSettings"));
		}
		finally
		{
			try
			{
				if (out != null)
				{
					out.close();
				}
			}
			catch (IOException e)
			{
			}
		}
	}

	/**
	 * Sets the main window of the application so that it is accessible
	 * to other parts of the software.
	 * 
	 * @param window
	 *            the main window
	 */
	static void setUIShell(UIShell window)
	{
		mainWindow = window;
	}

	/**
	 * Gets the main window of the application.
	 * 
	 * @return the main window
	 */
	public static Frame getMainWindow()
	{
		return mainWindow.getWindow();
	}

	/**
	 * Gets the UI services provided by the main window of the application.
	 * 
	 * @return the UI services of the main window
	 */
	public static UIShell getUIShell()
	{
		return mainWindow;
	}

	/**
	 * Register an options pane with the options dialog box. To be called just
	 * once per session for each options pane (e.g., when loading IDES or a
	 * plugin).
	 * 
	 * @param pane
	 *            the {@link pluggable.ui.OptionsPane} that will be registered
	 * @see pluggable.ui.OptionsPane
	 * @see ui.OptionsWindow
	 */
	public static void registerOptionsPane(OptionsPane pane)
	{
		OptionsWindow.registerOptionsPane(pane);
	}

	/**
	 * Request that the options dialog box opens up on the screen and shows the
	 * options for the {@link pluggable.ui.OptionsPane} with the given title.
	 * 
	 * @param title
	 *            the title of the {@link pluggable.ui.OptionsPane} to be
	 *            displayed
	 * @see pluggable.ui.OptionsPane
	 * @see ui.OptionsWindow
	 */
	public static void openOptionsPane(String title)
	{
		new OptionsWindow(title);
	}

	/**
	 * Displays a dialog box that shows an alert message to the user. Intended
	 * use is to announce errors and problems in an aesthetically-pleasing way.
	 * 
	 * @param message
	 *            message to be displayed
	 */
	public static void displayAlert(String message)
	{
		// SwingUtilities.invokeLater(new Runnable()
		// {
		// public void run()
		// {
		javax.swing.JOptionPane.showMessageDialog(getMainWindow(),
				message,
				string("message"),
				javax.swing.JOptionPane.WARNING_MESSAGE);
		// }
		// });
	}

	/**
	 * Suggests the position of the top-left corner for a dialog box with the
	 * given dimensions so that it is located in the center of the main window
	 * of the application.
	 * 
	 * @param d
	 *            the dimension of the dialog box
	 * @return the suggested position of the top-left corner of the dialog box
	 */
	public static Point getCenteredLocationForDialog(Dimension d)
	{
		Point p = new Point();
		p.x = (getMainWindow().getWidth() - d.width) / 2
				+ getMainWindow().getLocation().x;
		p.y = (getMainWindow().getHeight() - d.height) / 3
				+ getMainWindow().getLocation().y;
		if (p.x < 0)
		{
			p.x = 0;
		}
		if (p.y < 0)
		{
			p.y = 0;
		}
		return p;
	}

	/**
	 * Gets the main workspace.
	 * 
	 * @return the main workspace
	 */
	public static Workspace getWorkspace()
	{
		return Workspace.instance();
	}

	/**
	 * Gets a resource that may be in the JAR file (icons and such).
	 * 
	 * @param name
	 *            the name of the resource
	 * @return URL that points to the resource
	 */
	public static URL getResource(String name)
	{
		return ClassLoader.getSystemResource(name);
	}

}
