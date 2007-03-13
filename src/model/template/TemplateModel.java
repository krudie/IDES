package model.template;

import java.util.Iterator;
import java.util.Set;

import model.DESModel;

public interface TemplateModel extends DESModel {
	public void add(TemplateModule module);
	public void add(TemplateChannel channel);
	public void add(TemplateLink link);
	public void remove(TemplateModule module);
	public void remove(TemplateChannel channel);
	public void remove(TemplateLink connection);
	public TemplateModule getModule(long id);
	public Iterator<TemplateModule> getModuleIterator();
	public int getModuleCount();
	public TemplateChannel getChannel(long id);
	public Iterator<TemplateChannel> getChannelIterator();
	public int getChannelCount();
	public TemplateLink getLink(long id);
	public Iterator<TemplateLink> getLinkIterator();
	public int getLinkCount();
}
