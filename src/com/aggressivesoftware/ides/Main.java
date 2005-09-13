/*
 * Created on Dec 9, 2004
 */
package com.aggressivesoftware.ides;

import org.eclipse.swt.widgets.Display;

/**
 * The intention of this separate class was to produce a splash screen with short delay,
 * while the slow load of the Graphing Platform was underway.
 * 
 * @author Michael Wood
 */
public class Main 
{
    /**
     * The main method attempts to construct the GraphingPlatform.
     * 
     * @param	args	The command line arguments.
     */
	public static void main(String[] args)
	{
		Display display = null;
		Splash splash = null;
		try	
		{ 
			display = Display.getDefault();
			splash = new Splash(display);		
			new GraphingPlatform(splash); 
			display.dispose();
		}
		catch (Exception e) 
		{ 
			if (splash != null) { splash.dispose(); }
			if (display != null) { display.dispose(); }
			
			System.out.println(e.getMessage() + "\n"); 
			e.printStackTrace(); 
		} 
	}
}