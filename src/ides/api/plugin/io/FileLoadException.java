package ides.api.plugin.io;

import ides.api.plugin.model.DESModel;

import java.io.IOException;

/**
 * Used when there is a problem loading a file. Additionally, can store and
 * later provide access to the partially-loaded {@link DESModel} at the time of
 * the exception.
 * 
 * @author Lenko Grigorov
 */
public class FileLoadException extends IOException
{
	private static final long serialVersionUID = 8034841581346773517L;

	/**
	 * The partially-loaded model at the time of the exception.
	 */
	protected DESModel partialModel = null;

	public FileLoadException()
	{
		super();
	}

	public FileLoadException(String msg)
	{
		super(msg);
	}

	/**
	 * Creates an exception which will give access to the partially-loaded model
	 * at the time of the exception.
	 * 
	 * @param msg
	 *            message
	 * @param partialModel
	 *            the partially-loaded model at the time of the exception
	 */
	public FileLoadException(String msg, DESModel partialModel)
	{
		super(msg);
		this.partialModel = partialModel;
	}

	/**
	 * Provides access to the partially-loaded model at the time of the
	 * exception.
	 * 
	 * @return the partially-loaded model at the time of the exception
	 */
	public DESModel getPartialModel()
	{
		return partialModel;
	}
}
