/*
 * Created on Sep 15, 2004
 */
package com.aggressivesoftware.ides.rs232;

import java.io.InputStream;
import java.io.OutputStream;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;

/**
 * This class provides an interface for fixed (9600 Baud, 8 data-bit, 1 stop-bit, no parity, COM1) rs232 communication
 * with and external device.
 * 
 * @author Michael Wood
 */
public class RS232Connection implements SerialPortEventListener 
{
    /**
     * The port which connects the PC to the microcontroller.
     */
	private final String PORT = "COM1";

    /**
     * A name for this connection.
     */
	private final String CONNECTION_NAME = "connection_name";
	
    /**
     * The maximum number of characters this connection will receive in a single stream.
     */
	private final int INPUT_ARRAY_LENGTH = 128;
	
    /**
     * The SerialPort object for this connection.
     */
    private SerialPort serial_port = null;
    
    /**
     * The InputStream object for this connection.
     */
    private InputStream input_stream = null;
    
    /**
     * The OutputStream object for this connection.
     */
    private OutputStream output_stream = null;
    
    /**
     * The RS232EventHandler object for this connection.
     */
    private RS232EventHandler event_handler = null;
    
    /**
     * An indicatior of whether or not we believe we have established an connection.
     */
    private boolean is_connected = false;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// RS232Connection construction ///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

    /**
     * Construct the RS232Connection.
     * 
     * @param	driver_name		The identifier for the dll to be used on the OS for this connection.
     * @param	new_event_handler	The RS232EventHandler for this connection.
     */
	public RS232Connection(String driver_name, RS232EventHandler new_event_handler)
	{
		event_handler = new_event_handler;
		
    	try 
		{ 
    		System.setSecurityManager(null);
    		javax.comm.CommDriver comm_driver = (javax.comm.CommDriver)Class.forName(driver_name).newInstance();
    		comm_driver.initialize();
    		
    		CommPortIdentifier com_port_id = CommPortIdentifier.getPortIdentifier(PORT); 
    		serial_port = (SerialPort) com_port_id.open(CONNECTION_NAME, 2000);  
		    input_stream = serial_port.getInputStream(); 
		    output_stream = serial_port.getOutputStream(); 
		    serial_port.addEventListener(this);
			serial_port.notifyOnDataAvailable(true);
			serial_port.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			is_connected = true;
		} 
		catch (Exception e){ }
	}
	
    /**
     * Test if we believe we have a valid connection.
     * 
     * @return	true if we believe we have a valid connection.
     */
	public boolean isConnected() { return is_connected; }
	
    /**
     * Send a byte array to the microcontroller.
     * 
     * @param	bytes		The byte array to be sent.
     */
	public void sendBytes(byte[] bytes)
	{
		try { output_stream.write(bytes); }
	    catch (Exception e){ }
	}
	
    /**
     * Receive bytes from the microcontroller.
     * 
     * @param	event		The event delivered by the listener.
     */
    public void serialEvent(SerialPortEvent event) 
    {
    	if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) 
		{
		    byte[] readBuffer = new byte[INPUT_ARRAY_LENGTH];
		    try 
			{
		    	int num_bytes = 0;
		    	while (input_stream.available() > 0) { num_bytes = input_stream.read(readBuffer); }
		    	String input_string = new String(readBuffer);
		    	if (input_string.length() > num_bytes) { input_string = input_string.substring(0,num_bytes); }
		    	event_handler.print(input_string);
		    } 
		    catch (Exception e){ }
		}
    } 
    
    /**
     * Dispose the RS232Connection.
     */
    public void dispose()
    {
    	try { input_stream.close();  } catch (Exception e){ }
    	try { output_stream.close(); } catch (Exception e){ }
    	try { serial_port.close();   } catch (Exception e){ }
    }
}
