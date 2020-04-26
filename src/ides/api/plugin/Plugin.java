package ides.api.plugin;

/**
 * The main interface of a plugin for IDES.
 * <p>
 * Refer to the external documentation to learn how to create IDES plugins.
 * 
 * @author Lenko Grigorov
 */
public interface Plugin {
    /**
     * Called once when the plugin has to be initialized.
     * 
     * @throws PluginInitException can be thrown if the plugin cannot initialize
     */
    public void initialize() throws PluginInitException;

    /**
     * Called once when the plugin has to be unloaded before IDES shuts down.
     */
    public void unload();

    /**
     * Retrieve the name of the plugin.
     * 
     * @return the name of the plugin
     */
    public String getName();

    /**
     * Retrieve the version of the plugin.
     * 
     * @return the version of the plugin
     */
    public String getVersion();

    /**
     * Retrieve the authorship of the plugin.
     * 
     * @return the authorship of the plugin
     */
    public String getCredits();

    /**
     * Retrieve the license of the plugin.
     * 
     * @return the license of the plugin
     */
    public String getLicense();

    /**
     * Retrieve a short description of the plugin.
     * 
     * @return a short description of the plugin
     */
    public String getDescription();
}
