/**
 * 
 */
package userinterface;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * @author edlund
 *
 */
public class ProjectExplorerPopup {

    
    private Shell shell;
    private Menu automatonMenu, projectMenu;
    
    public ProjectExplorerPopup(Shell shell){
        this.shell = shell;
        initAutomatonMenu();
        initProjectMenu();
    }
    
    
    private void initAutomatonMenu(){
        automatonMenu = new Menu (shell, SWT.POP_UP);
        MenuItem item = new MenuItem (automatonMenu, SWT.PUSH);
        item.setText ("Automaton Menu");

    }
    
    private void initProjectMenu(){
        projectMenu = new Menu (shell, SWT.POP_UP);
        MenuItem item = new MenuItem (projectMenu, SWT.PUSH);
        item.setText ("Project Menu");
    }
    
    public Menu getAutomatonMenu(){
        return automatonMenu;
    }
    
    public Menu getProjectMenu(){
        return projectMenu;
    }
    
}
