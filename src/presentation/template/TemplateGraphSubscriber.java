package presentation.template;

public interface TemplateGraphSubscriber
{

	public void templateGraphChanged(TemplateGraphMessage message);

	public void templateGraphSelectionChanged(TemplateGraphMessage message);

}
