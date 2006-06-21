package services.latex;

import javax.swing.SwingUtilities;

import main.Hub;
import util.InterruptableProgressDialog;

public class LatexPrerenderer extends InterruptableProgressDialog {

	private int loaded;
	private boolean cancel=false;
	int i=0;
	
	public LatexPrerenderer()
	{
		super(Hub.getMainWindow(),"Happy!");
	}
	
	public void interrupt()
	{
		System.out.println("doo");
		cancel=true;
	}
	
	public void run()
	{
		setVisible(true);
		while(!cancel)
		{
			System.out.print("foo");
			SwingUtilities.invokeLater(
					new Runnable()
					{
						public void run()
						{
			progressBar.setValue(i++);
						}
					});
			try{
			Thread.sleep(1000);
			}catch(Exception e){}
		}
		dispose();
		return;
	}
}
