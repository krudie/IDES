package template.model;

import java.util.Collection;

import model.DESModel;

public interface TemplateModel extends DESModel
{
	public Collection<TemplateModule> getModules();
	public Collection<TemplateChannel> getChannels();
	public Collection<TemplateLink> getLinks();
	public TemplateModule getModule(long id);
	public TemplateChannel getChannel(long id);
	public TemplateLink getLink(long id);
	
	public TemplateModule addNewModule();
	public void addModule(TemplateModule module);
	public TemplateChannel addNewChannel();
	public void addChannel(TemplateChannel channel);
	public TemplateLink addNewLink(long channelId,long moduleId);
	public void addLink(TemplateLink link);
	
	public void removeModule(long id);
	public void removeChannel(long id);
	public void removeLink(long id);
	
	public Collection<TemplateLink> getChannelLinks(long channelId);
	public Collection<TemplateLink> getModuleLinks(long moduleId);
	public Collection<TemplateModule> getCover(long channelId);
	public boolean existsLink(long channelId,long moduleId);
	public Collection<TemplateLink> getLinks(long channelId, long moduleId);
}
