package main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import ides.api.core.Hub;
import ides.api.core.PersistentProperties;
import ides.api.core.UserInterface;
import ui.TabbedWindow;

public class HubBackend {

    private HubBackend() {
    }

    /**
     * Contains (key,value) pairs which are stored in the settings file
     * (settings.ini). Accessible to everyone.
     */
    public static final PersistentProperties persistentData = new PersistentProperties();

    /**
     * Loads the settings from the file <code>settings.ini</code> into the list of
     * settings {@link #persistentData}.
     * 
     * @throws IOException
     * @see #persistentData
     * @see #storePersistentData()
     */
    synchronized static void loadPersistentData() throws IOException {
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream("settings.ini"));
            persistentData.load(in);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * Stores the settings from the list of settings {@link #persistentData} into
     * the file <code>settings.ini</code>. If this fails, it shows a message box to
     * the user.
     */
    public synchronized static void storePersistentData() {
        String comments = Hub.string("settingsFileComments");
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream("settings.ini"));
            persistentData.store(out, comments);
        } catch (IOException e) {
            Hub.displayAlert(Hub.string("cantStoreSettings"));
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * The main window of the application.
     * 
     * @see #setUserInterface(TabbedWindow)
     */
    private static TabbedWindow mainWindow = null;

    /**
     * Gets the main UI of IDES.
     * 
     * @return the main UI of IDES
     */
    public static UserInterface getUserInterface() {
        return mainWindow;
    }

    /**
     * Sets the main UI of IDES so that it is accessible to other parts of the
     * software.
     * 
     * @param window the main UI of IDES
     */
    static void setUserInterface(TabbedWindow window) {
        mainWindow = window;
    }

    /**
     * Gets the main UI of IDES with access to its tabs.
     * 
     * @return the main UI with access to its tabs
     */
    static TabbedWindow getTabbedWindow() {
        return mainWindow;
    }
}
