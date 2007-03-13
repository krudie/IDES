package model.template.ver2_1;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.Iterator;

import main.Hub;
import model.DESModel;
import model.ModelDescriptor;
import model.fsa.FSAModel;
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
			return "DES Template Design";
		}
		public Image getIcon()
		{
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
	
	protected String name="";

	protected TemplateDesign(String name)
	{
		this.name=name;
	}
	
	public void add(TemplateModule module) {
		// TODO Auto-generated method stub

	}

	public void add(TemplateChannel channel) {
		// TODO Auto-generated method stub

	}

	public void add(TemplateLink link) {
		// TODO Auto-generated method stub

	}

	public TemplateChannel getChannel(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getChannelCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Iterator<TemplateChannel> getChannelIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public TemplateLink getLink(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getLinkCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Iterator<TemplateLink> getLinkIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public TemplateModule getModule(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getModuleCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Iterator<TemplateModule> getModuleIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(TemplateModule module) {
		// TODO Auto-generated method stub

	}

	public void remove(TemplateChannel channel) {
		// TODO Auto-generated method stub

	}

	public void remove(TemplateLink connection) {
		// TODO Auto-generated method stub

	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public ModelDescriptor getModelDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setId(String id) {
		// TODO Auto-generated method stub

	}

	public void setName(String name) {
		// TODO Auto-generated method stub

	}

	public Object getAnnotation(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasAnnotation(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setAnnotation(String key, Object annotation) {
		// TODO Auto-generated method stub

	}

}
