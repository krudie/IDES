package model.template.ver2_1;

import model.fsa.FSAEventSet;
import model.fsa.FSAModel;
import model.template.TemplateChannel;

public class Channel extends Block implements TemplateChannel
{

	public Channel(FSAModel fsa, FSAEventSet events)
	{
		super(fsa, events);
	}

}
