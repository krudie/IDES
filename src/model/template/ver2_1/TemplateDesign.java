package model.template.ver2_1;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import main.Hub;
import model.DESModel;
import model.ModelDescriptor;
import model.fsa.FSAModel;
import model.fsa.FSAState;
import model.template.TemplateChannel;
import model.template.TemplateLink;
import model.template.TemplateModel;
import model.template.TemplateModule;

public class TemplateDesign implements TemplateModel {

	protected static class DesignDescriptor implements ModelDescriptor
	{
		public Class[] getModelInterfaces()
		{
			return new Class[]{TemplateModel.class};
		}
		public Class getPreferredModelInterface()
		{
			return TemplateModel.class;
		}
		public String getTypeDescription()
		{
			return "Template Design";
		}
		public Image getIcon()
		{
			//TODO change the icon
			return Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/model_template.gif"));
		}
		public DESModel createModel(String id)
		{
			TemplateDesign td=new TemplateDesign("");
			td.setId(id);
			return td;
		}
		public DESModel createModel(String id, String name)
		{
			TemplateDesign td=new TemplateDesign(name);
			td.setId(id);
			return td;
		}
	}
	public static final ModelDescriptor myDescriptor=new DesignDescriptor();
	
	protected String id="";
	protected String name="";
	
	protected LinkedList<TemplateModule> modules;
	protected LinkedList<TemplateChannel> channels;
	protected LinkedList<TemplateLink> links;
	
    protected Hashtable<String, Object> annotations=new Hashtable<String,Object>();

	protected TemplateDesign(String name)
	{
		this.name=name;
		modules=new LinkedList<TemplateModule>();
		channels=new LinkedList<TemplateChannel>();
		links=new LinkedList<TemplateLink>();
	}
	
	public void add(TemplateModule module) {
		modules.add(module);
	}

	public void add(TemplateChannel channel) {
		channels.add(channel);
	}

	public void add(TemplateLink link) {
		links.add(link);
		link.getBlockLeft().addLink(link);
		link.getBlockRight().addLink(link);
	}

	public TemplateChannel getChannel(long id) {
        Iterator<TemplateChannel> ci = channels.iterator();
        while(ci.hasNext()){
            TemplateChannel c = ci.next();
            if(c.getId() == id) return c;
        }
		return null;
	}

	public int getChannelCount() {
		return channels.size();
	}

	public Iterator<TemplateChannel> getChannelIterator() {
		return channels.iterator();
	}

	public TemplateLink getLink(long id) {
        Iterator<TemplateLink> li = links.iterator();
        while(li.hasNext()){
            TemplateLink l = li.next();
            if(l.getId() == id) return l;
        }
		return null;
	}

	public int getLinkCount() {
		return links.size();
	}

	public Iterator<TemplateLink> getLinkIterator() {
		return links.iterator();
	}

	public TemplateModule getModule(long id) {
        Iterator<TemplateModule> mi = modules.iterator();
        while(mi.hasNext()){
            TemplateModule m = mi.next();
            if(m.getId() == id) return m;
        }
		return null;
	}

	public int getModuleCount() {
		return modules.size();
	}

	public Iterator<TemplateModule> getModuleIterator() {
		return modules.iterator();
	}

	public void remove(TemplateModule module) {
		modules.remove(module);
		for(TemplateLink l:module.getLinks())
			remove(l);
	}

	public void remove(TemplateChannel channel) {
		channels.remove(channel);
		for(TemplateLink l:channel.getLinks())
			remove(l);
	}

	public void remove(TemplateLink connection) {
		links.remove(connection);
		connection.getBlockLeft().removeLink(connection);
		connection.getBlockRight().removeLink(connection);
	}

	public String getId() {
		return id;
	}

	public ModelDescriptor getModelDescriptor() {
		return myDescriptor;
	}

	public String getName() {
		return name;
	}

	public void setId(String id) {
		this.id=id;
	}

	public void setName(String name) {
		this.name=name;
	}

	public Object getAnnotation(String key) {
		return annotations.get(key);
	}

	public boolean hasAnnotation(String key) {
		return annotations.containsKey(key);
	}

	public void setAnnotation(String key, Object annotation) {
		annotations.put(key, annotation);
	}

}
