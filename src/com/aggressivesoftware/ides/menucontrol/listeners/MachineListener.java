/*
 * Created on Sep 15, 2004
 */
package com.aggressivesoftware.ides.menucontrol.listeners;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import com.aggressivesoftware.ides.GraphingPlatform;
import com.aggressivesoftware.ides.rs232.RS232Connection;
import com.aggressivesoftware.ides.rs232.RS232EventHandler;

/**
 * This class handles all events the fall under the "Machine" menu concept.
 * 
 * @author Michael Wood
 */
public class MachineListener extends AbstractListener
{
	/**
     * The object that handles the rs232 communications
     */
	private RS232Connection rs232 = null;

	/**
     * The object that handles the rs232 events
     */
	private RS232EventHandler rs232_events = null;
	
	/**
	 * A pattern of events to trace.
	 * This is used in the manual display and is not connected to any external device.
	 */
	private int trace_symbols[] = null;
	
	/**
	 * A pointer for the trace_symbols array.
	 */
	private int trace_pointer = 0;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// MachineListener construction ///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

    /**
     * Construct the MachineListener.
     * 
     * @param	gp		The GraphingPlatform in which this MachineListener will exist.
     */
	public MachineListener(GraphingPlatform gp)
	{
		this.gp = gp;
		rs232_events = new RS232EventHandler(gp);
	}
	
    /**
     * Dispose the ListenersRS232.
     */
	public void dispose()
	{
		if (rs232 != null) { rs232.dispose(); }
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// adapters ///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

	/**
	 * Find the appropriate Listener for this resource.
	 * 
	 * @param   resource_handle		The constant identification for a concept in the ResourceManager.
	 * @return	The appropriate Listener for this resource.
	 */
	public SelectionListener getListener(String resource_handle)
	{
		if (resource_handle.equals(gp.rm.MACHINE_CONNECT)) { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { connect(e); } }; }
		if (resource_handle.equals(gp.rm.MACHINE_TRACE))   { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { trace(e);   } }; }
		if (resource_handle.equals(gp.rm.MACHINE_ALPHA))   { return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { alpha(e);   } }; }
		System.out.println("Error: no match for resource_handle = " + resource_handle);
		return new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { } };
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// listeners //////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
	
    /**
     * Establish or break an rs232 connection with the microcontroller.
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void connect(org.eclipse.swt.events.SelectionEvent e)
	{
		if (gp.mc.machine_connect.getSelection() == false)
		{
			String extra_msg = "";

			// i.e. the button was just deselected, hence the desire is to disconnect
			if (rs232 != null)
			{
				rs232.dispose();
				rs232 = null;
			}
			
			// make sure we also stop any tracing
			if (gp.mc.machine_trace.getSelection() == true)
			{
				gp.mc.machine_trace.setSelection(false);
				trace(e); // let the normal method close down the trace
				extra_msg = gp.lbl_info1.getText();
				if (extra_msg.length() > 0) { extra_msg = " and " + extra_msg; }
			}
			
			gp.lbl_info1.setText(gp.rm.getString("rs232.disconnected") + extra_msg);
			gp.mc.machine_connect.defaultToolTipText();
		}
		else
		{
			rs232 = new RS232Connection(gp.rm.getString("rs232.driver_name"),rs232_events);
			if (rs232.isConnected()) 
			{ 
				gp.lbl_info1.setText(gp.rm.getString("rs232.connected")); 
				gp.mc.machine_connect.alternateToolTipText();
			}
			else 
			{ 
				gp.mc.machine_connect.setSelection(false);
				gp.lbl_info1.setText(gp.rm.getString("rs232.connection_failure"));
				gp.mc.machine_connect.defaultToolTipText();
			}
		}
	}	
	
    /**
     * Begin tracing the events of the hardware, providing that a connection exists 
     * Current state defaults to the start state.
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void trace(org.eclipse.swt.events.SelectionEvent e)
	{
		if (gp.mc.machine_trace.getSelection() == false)
		{
			// i.e. the button was just deselected, hence the desire is to stop the trace
			gp.gc.stopTrace();
			gp.lbl_info1.setText(gp.rm.getString("rs232.trace_stopped"));
			gp.mc.machine_trace.defaultToolTipText();
		}
		else
		{
			if (rs232 == null || !rs232.isConnected()) 
			{ 
				gp.mc.machine_trace.setSelection(false);
				gp.lbl_info1.setText(gp.rm.getString("rs232.must_connect")); 
				gp.mc.machine_trace.defaultToolTipText();
			}
			else 
			{ 
				gp.gc.startTrace();
				gp.lbl_info1.setText(gp.rm.getString("rs232.trace_started"));
				gp.mc.machine_trace.alternateToolTipText();
				
				// load the symbols array in case we are doing a manual trace
				trace_symbols = gp.td.getTrace();
				trace_pointer = 0;
			}
		}
	}	
	
    /**
     * Pretentd that a machine sent the next symbol in the manual trace symbols array.
     * 
     * @param	e	The SelectionEvent that initiated this action.
     */
	public void alpha(org.eclipse.swt.events.SelectionEvent e)
	{
		// used for testing the machine
		//if (rs232 != null) { rs232.sendBytes(new byte[] {97}); } // ascii for 'a'
		if (trace_pointer < trace_symbols.length)
		{
			int machine_code = trace_symbols[trace_pointer];
			trace_pointer++;
			gp.lbl_info3.setText(gp.rm.getString("lbl_info3.text") + machine_code);
			gp.gc.handleEvent(machine_code);
		}
	}	
	
    /**
     * Send a byte to the microcontroller.
     * 
     * @param	machine_code	The byte to be sent to the microcontroller.
     */
	public void sendMachineCode(int machine_code)
	{
		if (rs232 != null) { rs232.sendBytes(new byte[] {(byte)machine_code}); }
	}	
}