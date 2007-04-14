package model.template;

import main.Annotable;
import model.DESElement;
import model.fsa.FSAEvent;

public interface TemplateLink extends DESElement, Annotable {
	public FSAEvent getEventLeft();
	public FSAEvent getEventRight();
	public void setEventLeft(FSAEvent e);
	public void setEventRight(FSAEvent e);
	public TemplateBlock getBlockLeft();
	public TemplateBlock getBlockRight();
	public void setBlockLeft(TemplateBlock b);
	public void setBlockRight(TemplateBlock b);
}
