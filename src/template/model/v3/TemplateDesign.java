package template.model.v3;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import main.Hub;
import model.DESModelMessage;
import model.DESModelSubscriber;
import model.DESModelType;
import template.model.InconsistentModificationException;
import template.model.TemplateChannel;
import template.model.TemplateLink;
import template.model.TemplateModel;
import template.model.TemplateModule;

public class TemplateDesign implements TemplateModel
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
	
	private ArrayList<DESModelSubscriber> modelSubscribers = new ArrayList<DESModelSubscriber>();

	/**
	 * Attaches the given subscriber to this publisher. The given subscriber
	 * will receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void addSubscriber(DESModelSubscriber subscriber)
	{
		modelSubscribers.add(subscriber);
	}

	/**
	 * Removes the given subscriber to this publisher. The given subscriber will
	 * no longer receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void removeSubscriber(DESModelSubscriber subscriber)
	{
		modelSubscribers.remove(subscriber);
	}

	/**
	 * Returns all current subscribers to this publisher.
	 * 
	 * @return all current subscribers to this publisher
	 */
	public DESModelSubscriber[] getDESModelSubscribers()
	{
		return modelSubscribers.toArray(new DESModelSubscriber[] {});
	}

	protected boolean needsSave = false;

	public boolean needsSave()
	{
		return needsSave;
	}
	
	protected void setNeedsSave(boolean b)
	{
		if(b!=needsSave)
		{
			needsSave=b;
			DESModelMessage message = new DESModelMessage(
					needsSave ? DESModelMessage.DIRTY : DESModelMessage.CLEAN,
					this);
			for (DESModelSubscriber s : modelSubscribers)
			{
				s.saveStatusChanged(message);
			}
		}
	}
	
	protected static class TemplateDesignDescriptor implements DESModelType
	{

		public TemplateModel createModel(String name)
		{
			return new TemplateDesign(name);
		}

		public String getDescription()
		{
			return "Template Design";
		}

		public Image getIcon()
		{
			return Toolkit.getDefaultToolkit().createImage(Hub
					.getResource("images/icons/model_template.gif"));
		}

		public Class<TemplateModel> getMainPerspective()
		{
			return TemplateModel.class;
		}

		public Class<?>[] getModelPerspectives()
		{
			return new Class[] {TemplateModel.class};
		}
	}
	
	public static TemplateDesignDescriptor myDescriptor=new TemplateDesignDescriptor();

	public DESModelType getModelType()
	{
		return myDescriptor;
	}

	protected String name;
	protected Set<TemplateModule> modules=new HashSet<TemplateModule>();
	protected Set<TemplateChannel> channels=new HashSet<TemplateChannel>();
	protected Set<TemplateLink> links=new HashSet<TemplateLink>();
	
	protected long freeModuleId=0;
	protected long freeChannelId=0;
	protected long freeLinkId=0;
	
	public TemplateDesign(String name)
	{
		this.name=name;
	}
	
	protected boolean containsModuleId(long id)
	{
		for(TemplateModule module:modules)
		{
			if(module.getId()==id)
			{
				return true;
			}
		}
		return false;
	}

	protected boolean containsChannelId(long id)
	{
		for(TemplateChannel channel:channels)
		{
			if(channel.getId()==id)
			{
				return true;
			}
		}
		return false;
	}

	protected boolean containsLinkId(long id)
	{
		for(TemplateLink link:links)
		{
			if(link.getId()==id)
			{
				return true;
			}
		}
		return false;
	}

	public synchronized void addChannel(TemplateChannel channel)
	{
		if(containsChannelId(channel.getId()))
		{
			throw new InconsistentModificationException(Hub.string("inconsistencyChannelId"));
		}
		if(freeChannelId<=channel.getId())
		{
			freeChannelId=channel.getId()+1;
		}
		channels.add(channel);
		setNeedsSave(true);
	}

	public synchronized void addLink(TemplateLink link)
	{
		if(containsLinkId(link.getId()))
		{
			throw new InconsistentModificationException(Hub.string("inconsistencyLinkId"));
		}
		if(!modules.contains(link.getModule())||!channels.contains(link.getChannel()))
		{
			throw new InconsistentModificationException(Hub.string("inconsistencyLinking"));
		}
		Collection<TemplateLink> channelLinks=getChannelLinks(link.getChannel().getId());
		for(TemplateLink l:channelLinks)
		{
			if(l.getChannelEventName().equals(link.getChannelEventName())||
					(l.getModule()==link.getModule()&&l.getModuleEventName().equals(link.getModuleEventName())))
			{
				throw new InconsistentModificationException(Hub.string("inconsistencyLinking"));
			}
		}
		if(freeLinkId<=link.getId())
		{
			freeLinkId=link.getId()+1;
		}
		links.add(link);
		setNeedsSave(true);
	}

	public synchronized void addModule(TemplateModule module)
	{
		if(containsModuleId(module.getId()))
		{
			throw new InconsistentModificationException(Hub.string("inconsistencyModuleId"));
		}
		if(freeModuleId<=module.getId())
		{
			freeModuleId=module.getId()+1;
		}
		modules.add(module);
		setNeedsSave(true);
	}

	public synchronized TemplateChannel addNewChannel()
	{
		Channel channel=new Channel(freeChannelId);
		freeChannelId++;
		channels.add(channel);
		setNeedsSave(true);
		return channel;
	}

	public synchronized TemplateLink addNewLink(long channelId,
			long moduleId)
	{
		TemplateChannel channel=getChannel(channelId);
		TemplateModule module=getModule(moduleId);
		if(channel==null||module==null)
		{
			throw new InconsistentModificationException(Hub.string("inconsistencyLinkInit"));
		}
		Link link=new Link(freeLinkId,channel,module);
		freeLinkId++;
		links.add(link);
		setNeedsSave(true);
		return link;
	}

	public synchronized TemplateModule addNewModule()
	{
		Module module=new Module(freeModuleId);
		freeModuleId++;
		modules.add(module);
		setNeedsSave(true);
		return module;
	}

	public Collection<TemplateChannel> getChannels()
	{
		return new HashSet<TemplateChannel>(channels);
	}

	public Collection<TemplateLink> getLinks()
	{
		return new HashSet<TemplateLink>(links);
	}

	public Collection<TemplateModule> getModules()
	{
		return new HashSet<TemplateModule>(modules);
	}
	
	public TemplateModule getModule(long id)
	{
		for(TemplateModule module:modules)
		{
			if(module.getId()==id)
			{
				return module;
			}
		}
		return null;
	}
	
	public TemplateChannel getChannel(long id)
	{
		for(TemplateChannel channel:channels)
		{
			if(channel.getId()==id)
			{
				return channel;
			}
		}
		return null;
	}
	
	public TemplateLink getLink(long id)
	{
		for(TemplateLink link:links)
		{
			if(link.getId()==id)
			{
				return link;
			}
		}
		return null;		
	}

	public synchronized void removeChannel(long id)
	{
		if(!containsChannelId(id))
		{
			return;
		}
		channels.remove(getChannel(id));
		setNeedsSave(true);
	}

	public synchronized void removeLink(long id)
	{
		if(!containsLinkId(id))
		{
			return;
		}
		links.remove(getLink(id));
		setNeedsSave(true);
	}

	public synchronized void removeModule(long id)
	{
		if(!containsModuleId(id))
		{
			return;
		}
		modules.remove(getModule(id));
		setNeedsSave(true);
	}

	public String getName()
	{
		return name;
	}

	public void metadataChanged()
	{
		setNeedsSave(true);
	}

	public void modelSaved()
	{
		setNeedsSave(false);
	}

	public void setName(String name)
	{
		if(name!=null && name.equals(this.name))
		{
			return;
		}
		this.name=name;
		DESModelMessage message = new DESModelMessage(
				DESModelMessage.NAME,
				this);
		for (DESModelSubscriber s : modelSubscribers)
		{
			s.modelNameChanged(message);
		}
		metadataChanged();
	}

	public boolean existsLink(long channelId, long moduleId)
	{
		for(TemplateLink link:links)
		{
			if(link.getChannel().getId()==channelId&&link.getModule().getId()==moduleId)
			{
				return true;
			}
		}
		return false;
	}

	public Collection<TemplateLink> getChannelLinks(long channelId)
	{
		Set<TemplateLink> ret=new HashSet<TemplateLink>();
		for(TemplateLink link:links)
		{
			if(link.getChannel().getId()==channelId)
			{
				ret.add(link);
			}
		}
		return ret;
	}

	public Collection<TemplateModule> getCover(long channelId)
	{
		Set<TemplateModule> ret=new HashSet<TemplateModule>();
		for(TemplateLink link:links)
		{
			if(link.getChannel().getId()==channelId)
			{
				ret.add(link.getModule());
			}
		}
		return ret;
	}

	public Collection<TemplateLink> getLinks(long channelId, long moduleId)
	{
		Set<TemplateLink> ret=new HashSet<TemplateLink>();
		for(TemplateLink link:links)
		{
			if(link.getChannel().getId()==channelId&&link.getModule().getId()==moduleId)
			{
				ret.add(link);
			}
		}
		return ret;
	}

	public Collection<TemplateLink> getModuleLinks(long moduleId)
	{
		Set<TemplateLink> ret=new HashSet<TemplateLink>();
		for(TemplateLink link:links)
		{
			if(link.getModule().getId()==moduleId)
			{
				ret.add(link);
			}
		}
		return ret;
	}
}
