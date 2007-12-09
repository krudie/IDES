package services.latex;

import java.io.File;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.undo.UndoableEdit;

import presentation.fsa.FSAGraph;

import ui.OptionsWindow;
import ui.actions.OptionsActions;
import util.BooleanUIBinder;

import main.Hub;
import model.fsa.FSAModel;

/**
 * Coordinates the LaTeX rendering.
 * 
 * @author Lenko Grigorov
 */
public class LatexManager {

	protected final static String LATEX_OPTION="useLatexLabels";
	
	protected static BooleanUIBinder optionBinder=new BooleanUIBinder(); 
	
	private LatexManager()	{
	}
	
	/**
	 * The LaTeX renderer to be used for rendering throughout the program.
	 */
	private static Renderer renderer=null; 
	
	/**
	 * Initializes the LaTeX rendering subsystem.
	 */
	public static void init()
	{
		Hub.registerOptionsPane(new LatexOptionsPane());
		renderer=Renderer.getRenderer(new File(getLatexPath()),new File(getGSPath()));
		optionBinder.set(isLatexEnabled());
	}
	
	public static BooleanUIBinder getUIBinder()
	{
		return optionBinder;
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
		return Hub.persistentData.getBoolean(LATEX_OPTION);
	}
	
	/**
	 * A {@link Runnable} that toggles the LaTeX redering on.
	 * This is needed since the {@link LatexPrerenderer} displays its
	 * progress; thus the updating cannot be done inside the Swing
	 * event loop.
	 * @see LatexManager#setLatexEnabled(boolean)
	 *
	 * @author Lenko Grigorov
	 */
	private static class SetLatexUpdater implements Runnable
	{
		
		/**
		 * Update the LaTeX rendering setting.
		 */
		public void run()
		{
			if(new LatexPrerenderer(Hub.getWorkspace().getLayoutShellsOfType(FSAGraph.class).iterator()).waitFor())
			{
				Hub.persistentData.setBoolean(LATEX_OPTION,true);
				optionBinder.set(true);
				Hub.getWorkspace().fireRepaintRequired();
			}
		}
	}


	/**
	 * Switches LaTeX rendering of labels on and off.
	 * @param b <code>true</code> to turn LaTeX rendering on, <code>false</code> to turn LaTeX rendering off
	 */
	public static void setLatexEnabled(boolean b)
	{
		if(b)
		{
			SwingUtilities.invokeLater(new SetLatexUpdater());
		}
		else
		{
			Hub.persistentData.setBoolean(LATEX_OPTION,false);
			optionBinder.set(false);
			Hub.getWorkspace().fireRepaintRequired();
		}
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
	
	public static float getFontSize()
	{
		return 12;
	}
}
