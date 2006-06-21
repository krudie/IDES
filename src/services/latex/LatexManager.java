package services.latex;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.pietschy.command.ToggleCommand;

import ui.OptionsWindow;
import ui.command.OptionsCommands;

import main.Hub;

/**
 * Coordinates the LaTeX rendering.
 * 
 * @author Lenko Grigorov
 */
public class LatexManager {

	private LatexManager()
	{
	}
	
	public Object clone()
	{
	    throw new RuntimeException("Cloning of "+this.getClass().toString()+" not supported."); 
	}

	/**
	 * The LaTeX renderer to be used for rendering throughout the program.
	 */
	private static Renderer renderer=null; 
	
	/**
	 * The "Use LaTeX rendering" menu item.
	 */
	static ToggleCommand menuItem=null;
	
	/**
	 * Initializes the LaTeX rendering subsystem.
	 */
	public static void init()
	{
		Hub.registerOptionsPane(new LatexOptionsPane());
		renderer=Renderer.getRenderer(new File(getLatexPath()),new File(getGSPath()));
		menuItem=new UseLatexCommand();
		menuItem.export();
	}

	/**
	 * Returns the path to the directory of the <code>latex</code> and
	 * <code>dvips</code> executables.
	 * @return path to the directory of the <code>latex</code> and <code>dvips</code> executables
	 */
	static String getLatexPath()
	{
		return Hub.persistentData.getProperty("latexPath");
	}
	
	/**
	 * Returns the path to the GhostScript executable file.
	 * @return the path to the GhostScript executable file
	 */
	static String getGSPath()
	{
		return Hub.persistentData.getProperty("gsPath");
	}

	/**
	 * Sets the path to the directory of the <code>latex</code> and
	 * <code>dvips</code> executables.
	 * @param path path to the directory of the <code>latex</code> and <code>dvips</code> executables
	 */
	static void setLatexPath(String path)
	{
		Hub.persistentData.setProperty("latexPath",path);
		renderer=Renderer.getRenderer(new File(getLatexPath()),new File(getGSPath()));
	}
	
	/**
	 * Sets the path to the GhostScript executable file.
	 * @param path path to the GhostScript executable file
	 */
	static void setGSPath(String path)
	{
		Hub.persistentData.setProperty("gsPath",path);
		renderer=Renderer.getRenderer(new File(getLatexPath()),new File(getGSPath()));
	}
	
	/**
	 * Returns <code>true</code> if LaTeX rendering of labels is on,
	 * <code>false</code> otherwise.
	 * @return <code>true</code> if LaTeX rendering of labels is on, <code>false</code> otherwise
	 */
	public static boolean isLatexEnabled()
	{
		return Hub.persistentData.getBoolean("useLatexLabels");
	}
	
	/**
	 * Switches LaTeX rendering of labels on and off.
	 * @param b <code>true</code> to turn LaTeX rendering on, <code>false</code> to turn LaTeX rendering off  
	 */
	public synchronized static void setLatexEnabled(boolean b)
	{
		Hub.persistentData.setBoolean("useLatexLabels",b);
		if(menuItem!=null)
			menuItem.setSelected(b);
	}

	/**
	 * Returns the {@link Renderer} to be used for rendering LaTeX.
	 * @return the {@link Renderer} to be used for rendering LaTeX
	 */
	public static Renderer getRenderer()
	{
		if(renderer==null)
			renderer=Renderer.getRenderer(new File(getLatexPath()),new File(getGSPath()));
		return renderer;
	}
	
	/**
	 * Handle the situation when a LaTeX rendering problem occurs. Turns off
	 * LaTeX rendering of the labels. Asks the user if they wish to verify the LaTeX settings. 
	 */
	public static void handleRenderingProblem()
	{
		setLatexEnabled(false);
		//TODO notify presentation layer of problem - or done through the command?
		SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					int choice=JOptionPane.showConfirmDialog(Hub.getMainWindow(),
							Hub.string("renderProblem"),Hub.string("renderProblemTitle"),
							JOptionPane.YES_NO_OPTION);
					if(choice==JOptionPane.YES_OPTION)
					{
						new OptionsWindow(Hub.string("latexOptionsTitle"));
					}
				}
			});
	}
}
