package template.model;

/**
 * Exception to be thrown when there is a modification to a template model
 * which breaks the consistency of the model.
 * @author Lenko Grigorov
 */
public class InconsistentModificationException extends RuntimeException
{
	private static final long serialVersionUID = -2749639510223654829L;

	public InconsistentModificationException()
	{
	}

	public InconsistentModificationException(String arg0)
	{
		super(arg0);
	}

	public InconsistentModificationException(Throwable arg0)
	{
		super(arg0);
	}

	public InconsistentModificationException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}
}
