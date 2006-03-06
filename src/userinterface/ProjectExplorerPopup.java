/**
 * 
 */
package userinterface;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

/**
 * The right click popup menu used by the project explorer
 * 
 * @author Kristian edlund
 */
public class ProjectExplorerPopup {

    private Shell shell;

    private Menu automatonMenu, projectMenu;

    private TreeItem lastItem;

    private RenameListener rl;

    private ProjectExplorer pe;

    /**
     * The constructor
     * @param shell The parent shell
     * @param pe the project explorer this popup menu is attached to.
     */
    public ProjectExplorerPopup(Shell shell, ProjectExplorer pe) {
        this.pe = pe;
        this.shell = shell;
        rl = new RenameListener();

        initAutomatonMenu();
        initProjectMenu();
    }
    
    
    /**
     * Function to initialise the automaton menu. Used when user right click on an automaton in the project explorer.
     */
    private void initAutomatonMenu() {
        automatonMenu = new Menu(shell, SWT.POP_UP);
        MenuItem renameItem = new MenuItem(automatonMenu, SWT.PUSH);
        renameItem.setText(ResourceManager.getToolTipText("projectexplorer_rename"));
        renameItem.addSelectionListener(rl);

        MenuItem deleteItem = new MenuItem(automatonMenu, SWT.PUSH);
        deleteItem.setText(ResourceManager.getToolTipText("projectexplorer_delete"));

        deleteItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                pe.delete();
            }
        });

    }

    /**
     * Function to initialise the project menu. Used when user right click on the project in the project explorer.
     */
    private void initProjectMenu() {
        projectMenu = new Menu(shell, SWT.POP_UP);
        MenuItem renameItem = new MenuItem(projectMenu, SWT.PUSH);
        renameItem.setText(ResourceManager
                .getToolTipText("projectexplorer_rename"));
        renameItem.addSelectionListener(rl);
    }

    /**
     * Gets the menu used if the user right clicks on an automaton
     * @param last The item that the user clicked on last
     * @return The menu that should pop up when the user clicks on an automaton
     */
    public Menu getAutomatonMenu(TreeItem last) {
        this.lastItem = last;
        return automatonMenu;
    }
    /**
     * Gets the menu used if the user right clicks on the project
     * @param last The item that the user clicked on last
     * @return The menu that should pop up when the user clicks on the project
     */
    public Menu getProjectMenu(TreeItem last) {
        this.lastItem = last;
        return projectMenu;
    }

    /**
     * Private class used to listen for clicks on the rename menu item
     * @author Kristian Edlund
     */
    private class RenameListener implements SelectionListener {

        /**
         * Standard required function
         * calls rename
         */
        public void widgetSelected(SelectionEvent arg0) {
            pe.rename(lastItem);
        }
        
        /**
         * Standard required function
         * does nothing.
         */
        public void widgetDefaultSelected(SelectionEvent arg0) {
        }
    }

}
