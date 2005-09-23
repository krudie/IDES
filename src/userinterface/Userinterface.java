package userinterface;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

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
			CoolBar bar = new CoolBar (shell, SWT.BORDER);
			for (int i=0; i<2; i++) {
				CoolItem item = new CoolItem (bar, SWT.NONE);
				Button button = new Button (bar, SWT.PUSH);
				button.setText ("Button " + i);
				Point size = button.computeSize (SWT.DEFAULT, SWT.DEFAULT);
				item.setPreferredSize (item.computeSize (size.x, size.y));
				item.setControl (button);
			}
			bar.pack ();
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
