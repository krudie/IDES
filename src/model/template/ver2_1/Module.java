package model.template.ver2_1;

import model.fsa.FSAEventSet;
import model.fsa.FSAModel;
import model.template.TemplateModule;

public class Module extends Block implements TemplateModule
{

	public Module(FSAModel fsa, FSAEventSet events)
	{
		super(fsa, events);
	}

}
