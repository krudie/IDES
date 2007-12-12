package pluggable.io;

import java.io.IOException;

import model.DESModel;

public class FileLoadException extends IOException {

	protected DESModel partialModel=null;
	
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
		this.partialModel=partialModel;
	}
	
	public DESModel getPartialModel()
	{
		return partialModel;
	}
}
