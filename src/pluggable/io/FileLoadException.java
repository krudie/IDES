package pluggable.io;

import java.io.IOException;

import model.DESModel;

public class FileLoadException extends IOException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8034841581346773517L;

	protected DESModel partialModel = null;

	public FileLoadException()
	{
		super();
	}

	public FileLoadException(String msg)
	{
		super(msg);
	}

	public FileLoadException(String msg, DESModel partialModel)
	{
		super(msg);
		this.partialModel = partialModel;
	}

	public DESModel getPartialModel()
	{
		return partialModel;
	}
}
