package userinterface;

import org.eclipse.swt.widgets.*;

public class Userinterface {

	private Display display = null;
	private MainWindow mw; 
	
	public Userinterface(){
		
		Splash splash = null;
		try	{ 
			display = Display.getDefault();
			splash = new Splash(display);
			mw = new MainWindow(splash);
			display.dispose();
		} catch(Exception e){
			if (splash != null) { splash.dispose(); }
			if (display != null) { display.dispose(); }
			
			System.out.println(e.getMessage() + "\n"); 
			e.printStackTrace(); 
		}
	}
}
