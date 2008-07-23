package template.model.v3;

import java.util.Hashtable;

import main.Hub;
import model.fsa.FSAEvent;
import template.model.InconsistentModificationException;
import template.model.TemplateChannel;
import template.model.TemplateLink;
import template.model.TemplateModule;

public class Link implements TemplateLink
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
	protected TemplateModule module;
	protected TemplateChannel channel;
	protected String moduleEvent="";
	protected String channelEvent="";
	
	public Link(long id, TemplateChannel channel, TemplateModule module)
	{
		if(module==null||channel==null)
		{
			throw new InconsistentModificationException(Hub.string("inconsistencyLinkInit"));
		}
		this.id=id;
		this.module=module;
		this.channel=channel;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id=id;
	}

	public TemplateChannel getChannel()
	{
		return channel;
	}

	public FSAEvent getChannelEvent()
	{
		if(!channel.hasModel())
		{
			return null;
		}
		for(FSAEvent event:channel.getModel().getEventSet())
		{
			if(event.getSymbol().equals(channelEvent))
			{
				return event;
			}
		}
		return null;
	}

	public String getChannelEventName()
	{
		return channelEvent;
	}

	public TemplateModule getModule()
	{
		return module;
	}

	public FSAEvent getModuleEvent()
	{
		if(!module.hasModel())
		{
			return null;
		}
		for(FSAEvent event:module.getModel().getEventSet())
		{
			if(event.getSymbol().equals(moduleEvent))
			{
				return event;
			}
		}
		return null;
	}

	public String getModuleEventName()
	{
		return moduleEvent;
	}

	public boolean existsChannelEvent()
	{
		return getChannelEvent()!=null;
	}

	public boolean existsModuleEvent()
	{
		return getModuleEvent()!=null;
	}

	public void setChannelEventName(String name)
	{
		if(name==null)
		{
			name="";
		}
		channelEvent=name;
	}

	public void setModuleEventName(String name)
	{
		if(name==null)
		{
			name="";
		}
		moduleEvent=name;
	}
}
