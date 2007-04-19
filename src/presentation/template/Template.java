package presentation.template;

import model.fsa.FSAModel;

public class Template implements Comparable<Template> {

	public static final int TYPE_MODULE=0;
	public static final int TYPE_CHANNEL=1;
	
	protected int type;
	protected String fsa;
	protected String name;
	
	public Template(String fsa,int type,String name)
	{
		this.type=type;
		this.fsa=fsa;
		this.name=name;
	}
	
	public String getFSA()
	{
		return fsa;
	}
	
	public int getType()
	{
		return type;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int compareTo(Template t)
	{
		return name.compareTo(t.getName());
	}
}
