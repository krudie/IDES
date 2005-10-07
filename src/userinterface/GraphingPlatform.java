package userinterface;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;


import userinterface.graphcontrol.GraphController;
import userinterface.graphcontrol.TransitionData;
import userinterface.menu.MenuController;



public class GraphingPlatform {

    //private Composite da;
    public TabFolder tabFolder;
    
    private TabItem graphFolderItem,
                    languageSpec;
    
    public GraphController gc;
    public Display display;
    public Shell shell;
    public MenuController mc;
    
    /**
     * The object that contains the transition data and exists in the info in the specifications tab
     */
    public TransitionData td = null;
    
    /**
     * Indicies of TabItems within the TabFolder
     */
    public static final int GRAPH_CANVAS_TAB = 0,
                            SPECIFICATIONS_TAB =1;
      
    public GraphingPlatform(Composite parent, Shell shell, MenuController mc){
       
        this.mc = mc;
        this.shell = shell;
        display = Display.getDefault();
        
   
        //tabfolders
        tabFolder = new TabFolder(parent, SWT.NONE);
              
        graphFolderItem = new TabItem(tabFolder, SWT.NONE);
        graphFolderItem.setText(ResourceManager.getString("window.graph_tab.text"));
        
        languageSpec = new TabItem(tabFolder, SWT.NONE);
        languageSpec.setText(ResourceManager.getString("window.specifications_tab.text"));
        
        
        // define the layout of the content composite (within the base layout)
        // define the layout of the TabFolder (within the base layout)
        GridData gd_tab_folder = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL);
        gd_tab_folder.horizontalSpan = 3;
        tabFolder.setLayoutData(gd_tab_folder);
            
               
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // specifications area ////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////         
        
        // Note: this area has to be created before the graph area, because the graph area reads values from these objects.

        // the transition data area     
        Composite cmp_transitions = new Composite(tabFolder, SWT.NULL);

        // add it to the TabFolder
        languageSpec.setControl(cmp_transitions);

        // create a layout for the content composite (for the widgits inside the composite)
        GridLayout gl_transitions = new GridLayout();
        gl_transitions.marginHeight = 3;
        gl_transitions.marginWidth = 3;
        gl_transitions.verticalSpacing = 0;
        gl_transitions.horizontalSpacing = 0;
        cmp_transitions.setLayout(gl_transitions); // attach it to the composite

        // add the transition data object
        td = new TransitionData(this,cmp_transitions);
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // graph area /////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////         
        
        
        // the graphing area        
        Composite cmpGraphing = new Composite(tabFolder, SWT.BORDER);

        // add it to the TabFolder
        graphFolderItem.setControl(cmpGraphing);
        
        // create a layout for the content composite (for the widgits inside the composite)
        GridLayout gl_graphing = new GridLayout();
        gl_graphing.marginHeight = 0;
        gl_graphing.marginWidth = 0;
        gl_graphing.verticalSpacing = 0;
        gl_graphing.horizontalSpacing = 0;
        gl_graphing.numColumns = 2;
        cmpGraphing.setLayout(gl_graphing); // attach it to the composite
        
        gc = new GraphController(this , cmpGraphing);
        
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // selection between tabs /////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////         

        tabFolder.addSelectionListener
        (
            new SelectionAdapter() 
            { 
                public void widgetSelected(SelectionEvent e) 
                { 
                    if (td.dirty_edges)
                    {
                        td.dirty_edges = false;
                        gc.gm.accomodateLabels();
                        gc.repaint();
                    }
                }
            }
        );    
        

    }
    
    
    public void open(){
        
        
        
    }
    
    
}
