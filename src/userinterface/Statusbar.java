package userinterface;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import userinterface.graphcontrol.graphparts.Edge;

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
        int transitions=0;
        for(int i=MainWindow.getGraphingPlatform().gc.gm.getEdgeSize()-1;i>=0;--i)
        {
            Edge e=MainWindow.getGraphingPlatform().gc.gm.getEdgeById(i);
            if(e.numberOfEvents()==0)
                transitions++;
            else
                transitions+=e.numberOfEvents();
        }
        status.setText("   "+ MainWindow.getGraphingPlatform().getOpenAutomatonName()+", states: "+
                MainWindow.getGraphingPlatform().gc.gm.getNodeSize() +", transitions: "+transitions);                
    }
    
}
