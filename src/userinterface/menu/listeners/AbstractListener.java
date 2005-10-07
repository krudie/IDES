/*
 * Created on Dec 16, 2004
 */
package userinterface.menu.listeners;

import org.eclipse.swt.events.SelectionListener;

/**
 * This class is an abstract template for the Listeners classes. Each listener
 * class handles a concept group for the menu controller.
 * 
 * @author Michael Wood
 */
public abstract class AbstractListener {

    /**
     * Find the appropriate Listener for this resource.
     * 
     * @param resource_handle
     *            The constant identification for a concept in the
     *            ResourceManager.
     * @return The appropriate Listener for this resource.
     */
    public abstract SelectionListener getListener(String resource_handle);
}
