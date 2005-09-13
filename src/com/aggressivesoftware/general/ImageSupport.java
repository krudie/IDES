/*
 * Created on Jan 29, 2005
 */
package com.aggressivesoftware.general;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Michael Wood
 */
public class ImageSupport 
{
	/**
	 * Create a new SWT Image from the given ImageData.
	 * If the old_pointer points to an active SWT Image then dispose it.
	 * 
	 * @param	display			The Display to be used.
	 * @param	old_pointer		A possible SWT Image that should be disposed.
	 * @param	new_data		The new ImageData.
	 * @return	An Image created from the new_data.
	 */
	public static Image safeNewImage(Display display, Object old_pointer, ImageData new_data)
	{
		if (old_pointer != null)
		{ if (!((Image)old_pointer).isDisposed()) { ((Image)old_pointer).dispose(); } }
		return new Image(display, new_data);
	}
	
	/**
	 * If the old_pointer points to an active SWT Image then dispose it.
	 * 
	 * @param	old_pointer		A possible SWT Image that should be disposed.
	 */
	public static void safeDispose(Object old_pointer)
	{
		if (old_pointer != null)
		{ if (!((Image)old_pointer).isDisposed()) { ((Image)old_pointer).dispose(); } }
	}
}
