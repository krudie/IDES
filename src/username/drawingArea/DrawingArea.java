package username.drawingArea;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import userinterface.ResourceManager;



public class DrawingArea {

    //private Composite da;
    private TabFolder tabFolder;
    
    private TabItem graphFolderItem,
                    languageSpec;
    
      
    public DrawingArea(Composite parent, Shell shell){
       
        tabFolder = new TabFolder(parent, SWT.NONE);
        graphFolderItem = new TabItem(tabFolder, SWT.NONE);
        graphFolderItem.setText(ResourceManager.getString("window.graph_tab.text"));
        
        languageSpec = new TabItem(tabFolder, SWT.NONE);
        languageSpec.setText(ResourceManager.getString("window.specifications_tab.text"));
        
        
        // the graphing area        
        Composite cmpGraphing = new Composite(tabFolder, SWT.BORDER);

        // add it to the TabFolder
        graphFolderItem.setControl(cmpGraphing);
    }
    
    
    public void open(){
        
        
        
    }
    
    
}
