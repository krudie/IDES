/*
 * Created on Dec 16, 2004
 */
package com.aggressivesoftware.ides.menucontrol.listeners;

import org.eclipse.swt.events.SelectionListener;

import com.aggressivesoftware.ides.GraphingPlatform;

/**
 * This class is an abstract template for the Listeners classes.
 * Each listener class handles a concept group for the menu controller.
 *
 * @author Michael Wood
 */
public abstract class AbstractListener 
{
	/**
     * The platform in which this AbstractListener exists.
     */
	protected GraphingPlatform gp = null;

	/**
	 * Find the appropriate Listener for this resource.
	 * 
	 * @param   resource_handle		The constant identification for a concept in the ResourceManager.
	 * @return	The appropriate Listener for this resource.
	 */
	public abstract SelectionListener getListener(String resource_handle);
}
