package pluggable.io;

import java.io.IOException;

public class FormatTranslationException extends IOException {
	
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
