package template.model.v3;

import java.util.Hashtable;

import model.fsa.FSAModel;
import template.model.TemplateModule;

public class Module implements TemplateModule
{
	protected Hashtable<String, Object> annotations = new Hashtable<String, Object>();

	public Object getAnnotation(String key)
	{
		return annotations.get(key);
	}

	public boolean hasAnnotation(String key)
	{
		return annotations.containsKey(key);
	}

	public void removeAnnotation(String key)
	{
		annotations.remove(key);
	}

	public void setAnnotation(String key, Object annotation)
	{
		if (annotation != null)
		{
			annotations.put(key, annotation);
		}
	}

	protected long id;
	protected FSAModel fsa=null;
	
	public Module(long id)
	{
		this.id=id;
	}
	
	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id=id;
	}

	public FSAModel getModel()
	{
		return fsa;
	}

	public boolean hasModel()
	{
		return fsa!=null;
	}

	public void setModel(FSAModel fsa)
	{
		this.fsa=fsa;
	}
}
