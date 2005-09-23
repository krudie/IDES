package userinterface;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;

public class Userinterface {

	private Display display = null;
	private Shell shell = null;
	
	public Userinterface(){
		
		Splash splash = null;
		try	{ 
			display = Display.getDefault();
			splash = new Splash(display);
		} catch(Exception e){
			if (splash != null) { splash.dispose(); }
			if (display != null) { display.dispose(); }
			
			System.out.println(e.getMessage() + "\n"); 
			e.printStackTrace(); 
		}
		
		try{
			display = Display.getCurrent();
			shell = new Shell(display, SWT.SHELL_TRIM);
			shell.setText("IDES2");
			shell.setSize(800,620);
			shell.layout();
			shell.open();
			splash.dispose();
			while (!shell.isDisposed()) { if (!display.readAndDispatch()) display.sleep(); }
			display.dispose();
		}
		catch(Exception e){
			System.out.println(e.getStackTrace());
		}
	}
	
}
