/**
 * 
 */
package userinterface.menu.listeners;



import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import userinterface.MainWindow;
import userinterface.ResourceManager;
import userinterface.Userinterface;

/**
 * @author edlund
 *
 */
public class OperationListener extends AbstractListener{

    public SelectionListener getListener(String resource_handle){
        
        if (resource_handle.equals(ResourceManager.OPERATIONS_ACCESIBLE)) {
            return new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    accesible();
                }
            };
        }
        
        if (resource_handle.equals(ResourceManager.OPERATIONS_COACCESIBLE)) {
            return new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    
                }
            };
        }
        
        if (resource_handle.equals(ResourceManager.OPERATIONS_TRIM)) {
            return new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    
                }
            };
        }
        
        
        return null;
    }
    
    
    
    public void accesible(){
        
        String selectedName = MainWindow.getProjectExplorer().getSelectedAutomaton();
        if(selectedName != null){
            Userinterface.getProjectPresentation().accesible(selectedName, MainWindow.getProjectExplorer().getTitle("Accesible(" + selectedName +")"));
            MainWindow.getProjectExplorer().updateProject();
        }
    }
    

}
