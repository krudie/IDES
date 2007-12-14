package presentation.template;

import java.util.Collection;

public class Template implements Comparable<Template>
{

	public static final int TYPE_MODULE = 0;

	public static final int TYPE_CHANNEL = 1;

	protected int type;

	protected String fsa;

	protected String name;

	protected Collection<Long> ifaceEvents;

	public Template(String fsaName, Collection<Long> events, int type,
			String name)
	{
		this.type = type;
		this.fsa = fsaName;
		this.name = name;
		this.ifaceEvents = events;
	}

	public String getFSAName()
	{
		return fsa;
	}

	public void setFSAName(String name)
	{
		fsa = name;
	}

	public int getType()
	{
		return type;
	}

	public String getName()
	{
		return name;
	}

	public Collection<Long> getInterfaceEventIds()
	{
		return ifaceEvents;
	}

	public int compareTo(Template t)
	{
		return name.compareTo(t.getName());
	}
}
