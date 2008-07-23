package template.model;

import model.DESElement;
import model.fsa.FSAModel;

public interface TemplateModule extends DESElement
{
	public FSAModel getModel();
	public void setModel(FSAModel fsa);
	public boolean hasModel();
}
