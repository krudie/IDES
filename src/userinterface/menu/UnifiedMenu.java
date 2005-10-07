/*
 * Created on Dec 16, 2004
 */
package userinterface.menu;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import userinterface.ResourceManager;
import userinterface.menu.listeners.AbstractListener;

/**
 * This class allows unification between common menu actions that are
 * represented in various locations such as the main menu, the toolbar area, and
 * popup menus. There can be at most one main menu item and one toolbar item
 * contained in this unified menu, but there may be many associated popup menu
 * items. For the main menu items and the toolbar items, refrence is kept in
 * this class with the mitm and titm class variables. For popup items refernce
 * is kept in a vector list, and also returned to the creator. Those popup items
 * created with the custom_behaviour attribute are not added to the list and are
 * not modified by group actions such as enable/disable.
 * 
 * @author Michael Wood
 */
public class UnifiedMenu {

    /**
     * The Listener that will handle this UnifiedMenu's selection events.
     */
    private AbstractListener listener = null;

    /**
     * The constant identification for this UnifiedMenu's concept in the
     * ResourceManager.
     */
    public String resource_handle = null;

    /**
     * The MenuItem representation which would exist in the applications main
     * menu.
     */
    public MenuItem mitm = null;

    /**
     * The ToolItem representation which would exist in the applications main
     * toolbar area.
     */
    public ToolItem titm = null;

    /**
     * A list of popup MenuItem representations of this concept.
     */
    public Vector<MenuItem> popup_items = null;

    /**
     * An accelerator for use with the MenuItems. A zero value implys that no
     * accelerator is to be used.
     */
    private int accelerator = 0;

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UnifiedMenu construction
    // ///////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construct the UnifiedMenu.
     * 
     * @param resource_handle
     *            The constant identification for this UnifiedMenu's concept in
     *            the ResourceManager.
     */
    public UnifiedMenu(String resource_handle, AbstractListener listener) {
        constructUnifiedMenu(resource_handle, listener, 0);
    }

    /**
     * Construct the UnifiedMenu.
     * 
     * @param resource_handle
     *            The constant identification for this UnifiedMenu's concept in
     *            the ResourceManager.
     * @param accelerator
     *            An accelerator value for the MenuItems.
     */
    public UnifiedMenu(String resource_handle, AbstractListener listener,
            int accelerator) {
        constructUnifiedMenu(resource_handle, listener, accelerator);
    }

    /**
     * Construct the UnifiedMenu.
     * 
     * @param resource_handle
     *            The constant identification for this UnifiedMenu's concept in
     *            the ResourceManager.
     * @param accelerator
     *            An accelerator value for the MenuItems.
     */
    private void constructUnifiedMenu(String resource_handle,
            AbstractListener listener, int accelerator) {
        this.listener = listener;
        this.resource_handle = resource_handle;
        this.accelerator = accelerator;
        popup_items = new Vector<MenuItem>();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Main Menu Items
    // ////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Calls addMitm(parent, SWT.CASCADE);
     */
    public void addMitm(Menu parent) {
        addMitm(parent, SWT.CASCADE);
    }

    /**
     * Create and add a MenuItem to this UnifiedMenu. It will appear at the next
     * available slot of its parent Menu.
     * 
     * @param parent
     *            The Menu where the MenuItem will exist.
     * @param style
     *            The style byte for the MenuItem
     */
    public void addMitm(Menu parent, int style) {
        mitm = new MenuItem(parent, style);
        mitm.setText(ResourceManager.getMenuText(resource_handle));
        mitm.setData(resource_handle);
        if (accelerator != 0) {
            mitm.setAccelerator(accelerator);
        }
        mitm.addSelectionListener(listener.getListener(resource_handle));
        if (ResourceManager.hasImage(resource_handle)) {
            mitm.setImage(ResourceManager.getHotImage(resource_handle));
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Main ToolBar Items
    // /////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Calls addTitm(parent, SWT.NULL);
     */
    public void addTitm(ToolBar parent) {
        addTitm(parent, SWT.NULL);
    }

    /**
     * Create and add a ToolItem to this UnifiedMenu. It will appear at the next
     * available slot of its parent ToolBar.
     * 
     * @param parent
     *            The ToolBar where the ToolItem will exist.
     * @param style
     *            The style byte for the ToolItem
     */
    public void addTitm(ToolBar parent, int style) {
        titm = new ToolItem(parent, style);
        titm.addSelectionListener(listener.getListener(resource_handle));
        titm.setToolTipText(ResourceManager.getToolTipText(resource_handle));
        if (ResourceManager.hasImage(resource_handle)) {
            titm.setImage(ResourceManager.getImage(resource_handle));
            titm.setHotImage(ResourceManager.getHotImage(resource_handle));
            titm.setDisabledImage(ResourceManager
                    .getDisabledImage(resource_handle));
        } else {
            titm.setText(ResourceManager.getToolTipText(resource_handle));
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Popup Menu Items
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Calls addPopupMitm(parent, false, false);
     */
    public MenuItem addPopupMitm(Menu parent) {
        return addPopupMitm(parent, false, false);
    }

    /**
     * Create and add a MenuItem to this UnifiedMenu. It will appear at the next
     * available slot of its parent Menu. It is assumed that popup items are
     * created after the main items. The initial enabled/disabled state of the
     * popup items are therefore derived from their respective main menu items.
     * 
     * @param parent
     *            The Menu where the MenuItem will exist.
     * @param custom_listener
     *            True if this MenuItem will use a custom listener instead of
     *            the default one provided by its resource handle.
     * @param custom_behaviour
     *            True if this MenuItem should not be added to the Vector list
     *            and should not be affected by group actions such as
     *            enable/disable.
     */
    public MenuItem addPopupMitm(Menu parent, boolean custom_listener,
            boolean custom_behaviour) {
        MenuItem popup = new MenuItem(parent, SWT.CASCADE);
        if (!custom_listener) {
            popup.addSelectionListener(listener.getListener(resource_handle));
        }
        popup.setText(ResourceManager.getMenuText(resource_handle));
        popup.setData(resource_handle);
        if (accelerator != 0) {
            popup.setAccelerator(accelerator);
        }
        if (ResourceManager.hasImage(resource_handle)) {
            popup.setImage(ResourceManager.getHotImage(resource_handle));
        }
        if (!custom_behaviour) {
            if (mitm != null) {
                setEnabledMitm(popup, mitm.getEnabled());
            }
            popup_items.add(popup);
        }
        return popup;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Actions
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    
    public void setEnabled(boolean state){
        setEnabledMitm(mitm,state);
        if (titm != null) {
            titm.setEnabled(state);
        }
        for (int i = 0; i < popup_items.size(); i++) {
            setEnabledMitm((MenuItem) popup_items.elementAt(i), state);
        }
        
    }
    
    /**
     * Get the selection of this Object.
     */
    public boolean getSelection() {
        if (titm != null) {
            return titm.getSelection();
        } else if (mitm != null) {
            return mitm.getSelection();
        }
        return false;
    }

    /**
     * Set the selection of this Object.
     * 
     * @param state
     *            The new state for this Object.
     */
    public void setSelection(boolean state) {
        if (titm != null) {
            titm.setSelection(state);
        }
        if (mitm != null) {
            mitm.setSelection(state);
        }
    }

    /**
     * Change the tool-tip-text of this Object.
     * 
     * @param new_text
     *            The new tool-tip-text for this Object.
     */
    public void setToolTipText(String new_text) {
        if (titm != null) {
            titm.setToolTipText(new_text);
        }
    }

    /**
     * Change the tool-tip-text of this Object to it's default value.
     */
    public void defaultToolTipText() {
        if (titm != null) {
            titm.setToolTipText(ResourceManager.getToolTipText(resource_handle));
        }
    }

    /**
     * Change the tool-tip-text of this Object to it's alternate value.
     */
    public void alternateToolTipText() {
        if (titm != null) {
            titm.setToolTipText(ResourceManager.getString(resource_handle
                    + ".atext"));
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Helper Methods /////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    
    public void setEnabledMitm(MenuItem menuitem, boolean state){
        if(menuitem != null){
            menuitem.setEnabled(state);
        }
        if(ResourceManager.hasImage(resource_handle)){
            menuitem.setImage((state)? ResourceManager.getHotImage(resource_handle) : ResourceManager.getHotImage(resource_handle));            
        }
    }
}