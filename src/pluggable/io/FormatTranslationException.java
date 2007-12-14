package pluggable.io;

import java.io.IOException;

public class FormatTranslationException extends IOException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8596085812259995633L;

	public FormatTranslationException()
	{
		super();
	}

	public FormatTranslationException(String msg)
	{
		super(msg);
	}

	public FormatTranslationException(Exception e)
	{
		super(e.getMessage());
		setStackTrace(e.getStackTrace());
	}
}
