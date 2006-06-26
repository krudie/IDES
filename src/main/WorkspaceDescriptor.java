package main;

import java.util.Vector;

/**
 * 
 * @author Lenko Grigorov
 */
public class WorkspaceDescriptor {
	
	protected Vector<String> models=new Vector<String>();
	protected int selectedModel=0;
	protected String fileName="";
	
	public WorkspaceDescriptor(String fileName)
	{
		this.fileName=fileName;
	}

	public void insertModel(String model,int idx)
	{
		models.insertElementAt(model,idx);
	}
	
	public Vector<String> getModels()
	{
		return models;
	}
	
	public void setSelectedModel(int idx)
	{
		selectedModel=idx;
	}
	
	public int getSelectedModel()
	{
		return selectedModel;
	}

	public String getFileName()
	{
		return fileName;
	}
}
