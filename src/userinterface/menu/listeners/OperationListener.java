/**
 * 
 */
package userinterface.menu.listeners;



import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import userinterface.ResourceManager;

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
        
        return null;
    }
    
    
    
    public void accesible(){
        
    }
    

}
