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
 * @author edlund
 *
 */
public class ProjectExplorerPopup {

    
    private Shell shell;
    private Menu automatonMenu, projectMenu;
    private TreeItem lastItem;
    private RenameListener rl;
    private ProjectExplorer pe;
    
    
    
    public ProjectExplorerPopup(Shell shell, ProjectExplorer pe){
        this.pe = pe;
        this.shell = shell;
        rl = new RenameListener();
        
        initAutomatonMenu();
        initProjectMenu();
    }
    
    
    private void initAutomatonMenu(){
        automatonMenu = new Menu (shell, SWT.POP_UP);
        MenuItem renameItem = new MenuItem (automatonMenu, SWT.PUSH);
        renameItem.setText (ResourceManager.getToolTipText("projectexplorer_rename"));
        renameItem.addSelectionListener(rl);
        
        MenuItem deleteItem = new MenuItem(automatonMenu, SWT.PUSH);
        deleteItem.setText(ResourceManager.getToolTipText("projectexplorer_delete"));
        
        deleteItem.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent arg0) {
                pe.delete();
            }
        });
        

    }
    
    private void initProjectMenu(){
        projectMenu = new Menu (shell, SWT.POP_UP);
        MenuItem renameItem = new MenuItem (projectMenu, SWT.PUSH);
        renameItem.setText (ResourceManager.getToolTipText("projectexplorer_rename"));
        renameItem.addSelectionListener(rl);
    }
    
    public Menu getAutomatonMenu(TreeItem last){
        this.lastItem = last;
        return automatonMenu;
    }
    
    public Menu getProjectMenu(TreeItem last){
        this.lastItem = last;
        return projectMenu;
    }
    
    
    private class RenameListener implements SelectionListener{

        public void widgetSelected(SelectionEvent arg0) {
            pe.rename(lastItem);
        }

        public void widgetDefaultSelected(SelectionEvent arg0) {           
        }     
    }
    
    
}
