/*
 * Created on Sep 16, 2004
 */
package com.aggressivesoftware.ides.rs232;

import com.aggressivesoftware.ides.GraphingPlatform;

/**
 * This class provides the seperate thread necessary to receive asynchronous data from the external device.
 * 
 * @author Michael Wood
 */
public class RS232EventHandler 
{
	/**
     * The platform in which this RS232EventHandler exists.
     */
	private GraphingPlatform gp = null;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// RS232EventHandler construction /////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

    /**
     * Construct the RS232EventHandler.
     * 
     * @param	graphing_platform		The platform in which this RS232EventHandler will exist.
     */
	public RS232EventHandler(GraphingPlatform graphing_platform)
	{
		gp = graphing_platform;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//             ////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
    /**
     * Print the received input
     * 
     * @param	input_string		The received input.
     */		
	public void print(String input_string)
	{
		gp.ui_data = input_string;
		gp.display.asyncExec 
		(
			new Runnable () 
			{
				public void run () 
				{
					String s = (String)gp.ui_data;
					int machine_code = -1;
					if (s != null && s.length() == 1)
					{
						try { machine_code = (int)s.charAt(0); }
						catch (Exception e) { machine_code = -1; }
						gp.lbl_info3.setText(gp.rm.getString("lbl_info3.text") + machine_code);
						gp.gc.handleEvent(machine_code);
					}
					else { gp.lbl_info1.setText(s); }
				}
			}
		);
	}
}
