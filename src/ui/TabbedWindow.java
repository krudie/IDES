package ui;

import ides.api.core.UserInterface;

/**
 * Allows switching of tabs.
 * 
 * @author Lenko Grigorov
 */
public interface TabbedWindow extends UserInterface {
    /**
     * Switch to a tab in the main area.
     * 
     * @param name name of tab
     */
    public void activateMainTab(String name);

    /**
     * Switch to a tab in the right area.
     * 
     * @param name name of tab
     */
    public void activateRightTab(String name);

    /**
     * Switch to a tab in the left area.
     * 
     * @param name name of tab
     */
    public void activateLeftTab(String name);

    /**
     * Gets the name of the active tab in the main area. Returns the empty string if
     * no tab is active.
     * 
     * @return the name of the active tab
     */
    public String getActiveMainTab();

    /**
     * Gets the name of the active tab in the right area. Returns the empty string
     * if no tab is active.
     * 
     * @return the name of the active tab
     */
    public String getActiveRightTab();

    /**
     * Gets the name of the active tab in the left area. Returns the empty string if
     * no tab is active.
     * 
     * @return the name of the active tab
     */
    public String getActiveLeftTab();
}
