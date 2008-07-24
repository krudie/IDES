package ides.api.plugin;

public interface Plugin
{
	public void initialize() throws PluginInitException;

	public void unload();

	public String getName();

	public String getVersion();

	public String getCredits();

	public String getLicense();

	public String getDescription();
}
