package model.template.ver2_1;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import main.Hub;
import model.DESModel;
import model.DESModelSubscriber;
import model.ModelDescriptor;
import model.fsa.FSAEvent;
import model.template.TemplateChannel;
import model.template.TemplateLink;
import model.template.TemplateMessage;
import model.template.TemplateModel;
import model.template.TemplateModule;
import model.template.TemplateSubscriber;

public class TemplateDesign implements TemplateModel
{
	private boolean dirty = false;

	public boolean needsSave()
	{
		return dirty;
	}

	public void removeSubscriber(DESModelSubscriber subscriber)
	{
	}

	public DESModelSubscriber[] getDESModelSubscribers()
	{
		return null;
	}

	public void addSubscriber(DESModelSubscriber subscriber)
	{

	}

	/**
	 * Notifies the model that some associated metadata has been changed.
	 */
	public void metadataChanged()
	{

	}

	/**
	 * Notifies the model that it has been saved.
	 */
	public void modelSaved()
	{

	}

	protected Map<String, String> plcCode = new HashMap<String, String>();

	protected static class DesignDescriptor implements ModelDescriptor
	{

		public String getIOTypeDescription()
		{
			return "TemplateDesign";
		}

		public Class<?>[] getModelInterfaces()
		{
			return new Class[] { TemplateModel.class };
		}

		public Class<?> getPreferredModelInterface()
		{
			return TemplateModel.class;
		}

		public String getTypeDescription()
		{
			return "Template Design";
		}

		public Image getIcon()
		{
			// TODO change the icon
			return Toolkit.getDefaultToolkit().createImage(Hub
					.getResource("images/icons/model_template.gif"));
		}

		public DESModel createModel(String id)
		{
			TemplateDesign td = new TemplateDesign("");
			td.setName(id);
			return td;
		}

		public DESModel createModel(String id, String name)
		{
			TemplateDesign td = new TemplateDesign(name);
			td.setName(id);
			return td;
		}
	}

	public static final ModelDescriptor myDescriptor = new DesignDescriptor();

	protected String id = "";

	protected String name = "";

	protected LinkedList<TemplateModule> modules;

	protected LinkedList<TemplateChannel> channels;

	protected LinkedList<TemplateLink> links;

	private long maxModuleId = -1;

	private long maxChannelId = -1;

	private long maxLinkId = -1;

	protected Hashtable<String, Object> annotations = new Hashtable<String, Object>();

	private ArrayList<TemplateSubscriber> subscribers;

	protected TemplateDesign(String name)
	{
		subscribers = new ArrayList<TemplateSubscriber>();
		this.name = name;
		modules = new LinkedList<TemplateModule>();
		channels = new LinkedList<TemplateChannel>();
		links = new LinkedList<TemplateLink>();
	}

	public void addSubscriber(TemplateSubscriber subscriber)
	{
		subscribers.add(subscriber);
	}

	public void removeSubscriber(TemplateSubscriber subscriber)
	{
		subscribers.remove(subscriber);
	}

	public TemplateSubscriber[] getTemplateSubscribers()
	{
		return subscribers.toArray(new TemplateSubscriber[] {});
	}

	public void fireTemplateStructureChanged(TemplateMessage message)
	{
		for (TemplateSubscriber s : subscribers)
		{
			s.templateStructureChanged(message);
		}
	}

	public void fireTemplateSaved()
	{
		for (TemplateSubscriber s : subscribers)
		{
			s.modelSaved();
		}
	}

	public TemplateModel getTemplateModel()
	{
		return this;
	}

	public void add(TemplateModule module)
	{
		modules.add(module);
		if (module.getId() > maxModuleId)
		{
			maxModuleId = module.getId();
		}
		updatePLCMap();
		fireTemplateStructureChanged(new TemplateMessage(
				TemplateMessage.ADD,
				TemplateMessage.MODULE,
				module.getId(),
				this));
	}

	public void add(TemplateChannel channel)
	{
		channels.add(channel);
		if (channel.getId() > maxChannelId)
		{
			maxChannelId = channel.getId();
		}
		updatePLCMap();
		fireTemplateStructureChanged(new TemplateMessage(
				TemplateMessage.ADD,
				TemplateMessage.CHANNEL,
				channel.getId(),
				this));
	}

	public void add(TemplateLink link)
	{
		links.add(link);
		if (link.getId() > maxLinkId)
		{
			maxLinkId = link.getId();
		}
		link.getBlockLeft().addLink(link);
		link.getBlockRight().addLink(link);
		fireTemplateStructureChanged(new TemplateMessage(
				TemplateMessage.ADD,
				TemplateMessage.LINK,
				link.getId(),
				this));
	}

	public TemplateChannel getChannel(long id)
	{
		Iterator<TemplateChannel> ci = channels.iterator();
		while (ci.hasNext())
		{
			TemplateChannel c = ci.next();
			if (c.getId() == id)
			{
				return c;
			}
		}
		return null;
	}

	public int getChannelCount()
	{
		return channels.size();
	}

	public Iterator<TemplateChannel> getChannelIterator()
	{
		return channels.iterator();
	}

	public TemplateLink getLink(long id)
	{
		Iterator<TemplateLink> li = links.iterator();
		while (li.hasNext())
		{
			TemplateLink l = li.next();
			if (l.getId() == id)
			{
				return l;
			}
		}
		return null;
	}

	public int getLinkCount()
	{
		return links.size();
	}

	public Iterator<TemplateLink> getLinkIterator()
	{
		return links.iterator();
	}

	public TemplateModule getModule(long id)
	{
		Iterator<TemplateModule> mi = modules.iterator();
		while (mi.hasNext())
		{
			TemplateModule m = mi.next();
			if (m.getId() == id)
			{
				return m;
			}
		}
		return null;
	}

	public int getModuleCount()
	{
		return modules.size();
	}

	public Iterator<TemplateModule> getModuleIterator()
	{
		return modules.iterator();
	}

	public void remove(TemplateModule module)
	{
		modules.remove(module);
		HashSet<TemplateLink> linksToRemove = new HashSet<TemplateLink>();
		for (TemplateLink l : module.getLinks())
		{
			linksToRemove.add(l);
		}
		for (TemplateLink l : linksToRemove)
		{
			remove(l);
		}
		updatePLCMap();
		fireTemplateStructureChanged(new TemplateMessage(
				TemplateMessage.REMOVE,
				TemplateMessage.MODULE,
				module.getId(),
				this));
	}

	public void remove(TemplateChannel channel)
	{
		channels.remove(channel);
		HashSet<TemplateLink> linksToRemove = new HashSet<TemplateLink>();
		for (TemplateLink l : channel.getLinks())
		{
			linksToRemove.add(l);
		}
		for (TemplateLink l : linksToRemove)
		{
			remove(l);
		}
		updatePLCMap();
		fireTemplateStructureChanged(new TemplateMessage(
				TemplateMessage.REMOVE,
				TemplateMessage.CHANNEL,
				channel.getId(),
				this));
	}

	public void remove(TemplateLink connection)
	{
		links.remove(connection);
		connection.getBlockLeft().removeLink(connection);
		connection.getBlockRight().removeLink(connection);
		fireTemplateStructureChanged(new TemplateMessage(
				TemplateMessage.REMOVE,
				TemplateMessage.LINK,
				connection.getId(),
				this));
	}

	public long getFreeModuleId()
	{
		return maxModuleId + 1;
	}

	public long getFreeChannelId()
	{
		return maxChannelId + 1;
	}

	public long getFreeLinkId()
	{
		return maxLinkId + 1;
	}

	public String getId()
	{
		return id;
	}

	public ModelDescriptor getModelDescriptor()
	{
		return myDescriptor;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Object getAnnotation(String key)
	{
		return annotations.get(key);
	}

	public boolean hasAnnotation(String key)
	{
		return annotations.containsKey(key);
	}

	public void setAnnotation(String key, Object annotation)
	{
		annotations.put(key, annotation);
	}

	/**
	 * Removes the annotation for the given key.
	 * 
	 * @param key
	 *            key for the annotation
	 */
	public void removeAnnotation(String key)
	{
		annotations.remove(key);
	}

	public String getPLCCode(String event)
	{
		return plcCode.get(event);
	}

	public void setPLCCode(String event, String code)
	{
		if (code == null)
		{
			plcCode.remove(event);
		}
		else
		{
			plcCode.put(event, code);
		}
	}

	public Map<String, String> getPLCCodeMap()
	{
		return plcCode;
	}

	private void updatePLCMap()
	{
		Set<String> checkedEvents = new TreeSet<String>();
		for (Iterator<TemplateModule> i = getModuleIterator(); i.hasNext();)
		{
			for (FSAEvent e : i.next().getFSA().getEventSet())
			{
				if (!plcCode.containsKey(e.getSymbol()))
				{
					plcCode.put(e.getSymbol(), "");
				}
				checkedEvents.add(e.getSymbol());
			}
		}
		plcCode.keySet().retainAll(checkedEvents);
	}

}
