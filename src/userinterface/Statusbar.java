package userinterface;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class Statusbar{
    
    /**
     * The status bar of the main window
     */    
    private static Label status;
    
    
    public Statusbar(Shell shell){
        status=new Label (shell, SWT.LEFT);        
        GridData statusbarGD = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        statusbarGD.horizontalSpan = 3;
        statusbarGD.heightHint = 22;
                
        status.setLayoutData(statusbarGD);
        status.pack();        
    }
    
    public void update(){               
        status.setText("   "+ MainWindow.getGraphingPlatform().getOpenAutomatonName()+", states: "+
                MainWindow.getGraphingPlatform().gc.gm.getNodeSize() +", transitions: "+MainWindow.getGraphingPlatform().gc.gm.getEdgeSize());                
    }
    
}
