package ides.api.plugin.io;

import java.io.IOException;

/**
 * Used when there is a problem saving the data in a file.
 * 
 * @author Lenko Grigorov
 */
public class FileSaveException extends IOException
{

	private static final long serialVersionUID = 1678695993655527352L;

	public FileSaveException()
	{
		// TODO Auto-generated constructor stub
	}

	public FileSaveException(String arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public FileSaveException(Throwable arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public FileSaveException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
