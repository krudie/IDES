package services.latex;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import main.Hub;
import main.Workspace;
import ui.FilmStrip;
import ui.OptionsWindow;
import util.BooleanUIBinder;

/**
 * Coordinates the LaTeX rendering.
 * 
 * @author Lenko Grigorov
 */
public class LatexManager
{

	protected final static String LATEX_OPTION = "useLatexLabels";

	protected static BooleanUIBinder optionBinder = new BooleanUIBinder();

	private LatexManager()
	{
	}

	/**
	 * The LaTeX renderer to be used for rendering throughout the program.
	 */
	private static Renderer renderer = null;

	/**
	 * The {@link LatexPresentation}s which need to be included in the automatic
	 * prerendering when LaTeX rendering is turned on. It is the responsibility
	 * of the {@link Workspace} to maintain this list up to date. Other modules
	 * of IDES which load {@link LatexPresentation}s dynamically (such as the
	 * {@link FilmStrip}) also need to update this list.
	 * 
	 * @see LatexPresentation
	 * @see Workspace
	 * @see FilmStrip
	 */
	protected static Collection<LatexPresentation> presentations;

	/**
	 * Initializes the LaTeX rendering subsystem.
	 */
	public static void init()
	{
		Hub.registerOptionsPane(new LatexOptionsPane());
		renderer = Renderer.getRenderer(new File(getLatexPath()), new File(
				getGSPath()));
		optionBinder.set(isLatexEnabled());
		presentations = new HashSet<LatexPresentation>();
	}

	/**
	 * Returns the {@link BooleanUIBinder} which can be used by interface
	 * elements to get automatically updated with the state of the LaTeX
	 * rendering option.
	 * 
	 * @return the {@link BooleanUIBinder} which can be used by interface
	 *         elements to get automatically updated with the state of the LaTeX
	 *         rendering option
	 */
	public static BooleanUIBinder getUIBinder()
	{
		return optionBinder;
	}

	/**
	 * Adds a {@link LatexPresentation} to the list of {@link LatexPresentation}
	 * s which need to be included in the automatic prerendering when LaTeX
	 * rendering is turned on.
	 * 
	 * @param lp
	 *            the {@link LatexPresentation} to be added to the list
	 * @see LatexPresentation
	 * @see Workspace
	 * @see FilmStrip
	 */
	public static void addLatexPresentation(LatexPresentation lp)
	{
		if (!presentations.contains(lp))
		{
			presentations.add(lp);
			if (isLatexEnabled())
			{
				prerenderAndRepaint(lp);
			}
		}
	}

	/**
	 * Removes a {@link LatexPresentation} from the list of
	 * {@link LatexPresentation}s which need to be included in the automatic
	 * prerendering when LaTeX rendering is turned on.
	 * 
	 * @param lp
	 *            the {@link LatexPresentation} to be removed from the list
	 * @see LatexPresentation
	 * @see Workspace
	 * @see FilmStrip
	 */
	public static void removeLatexPresentation(LatexPresentation lp)
	{
		presentations.remove(lp);
	}

	/**
	 * Returns the path to the directory of the <code>latex</code> and
	 * <code>dvips</code> executables.
	 * 
	 * @return path to the directory of the <code>latex</code> and
	 *         <code>dvips</code> executables
	 */
	static String getLatexPath()
	{
		return Hub.persistentData.getProperty("latexPath");
	}

	/**
	 * Returns the path to the GhostScript executable file.
	 * 
	 * @return the path to the GhostScript executable file
	 */
	static String getGSPath()
	{
		return Hub.persistentData.getProperty("gsPath");
	}

	/**
	 * Sets the path to the directory of the <code>latex</code> and
	 * <code>dvips</code> executables.
	 * 
	 * @param path
	 *            path to the directory of the <code>latex</code> and
	 *            <code>dvips</code> executables
	 */
	static void setLatexPath(String path)
	{
		Hub.persistentData.setProperty("latexPath", path);
		renderer = Renderer.getRenderer(new File(getLatexPath()), new File(
				getGSPath()));
	}

	/**
	 * Sets the path to the GhostScript executable file.
	 * 
	 * @param path
	 *            path to the GhostScript executable file
	 */
	static void setGSPath(String path)
	{
		Hub.persistentData.setProperty("gsPath", path);
		renderer = Renderer.getRenderer(new File(getLatexPath()), new File(
				getGSPath()));
	}

	/**
	 * Returns <code>true</code> if LaTeX rendering of labels is on,
	 * <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if LaTeX rendering of labels is on,
	 *         <code>false</code> otherwise
	 */
	public static boolean isLatexEnabled()
	{
		return Hub.persistentData.getBoolean(LATEX_OPTION);
	}

	/**
	 * A {@link Runnable} that executes the prerendering of
	 * {@link LatexPresentation}s. This is needed since the
	 * {@link LatexPrerenderer} displays its progress; thus the updating cannot
	 * be done inside the Swing event loop.
	 * 
	 * @see LatexManager#setLatexEnabled(boolean)
	 * @author Lenko Grigorov
	 */
	private static class SetLatexUpdater implements Runnable
	{
		/**
		 * Object used for synchronization so that only one instance of this
		 * class can run at a given time.
		 */
		private static Object sync = new Object();

		/**
		 * Blocking variable so that only one instance of this class can run at
		 * a given time.
		 */
		private static boolean wait = false;

		/**
		 * Set of {@link LatexPresentation}s which need to be prerendered.
		 */
		private Collection<LatexPresentation> toPrerender;

		/**
		 * Creates an instance which will prerender all
		 * {@link LatexPresentation}s from the list for automatic prerendering.
		 * 
		 * @see LatexManager#presentations
		 */
		public SetLatexUpdater()
		{
			toPrerender = presentations;
		}

		/**
		 * Creates an instance which will prerender a given
		 * {@link LatexPresentation}.
		 * 
		 * @param lp
		 */
		public SetLatexUpdater(LatexPresentation lp)
		{
			toPrerender = new HashSet<LatexPresentation>();
			toPrerender.add(lp);
		}

		/**
		 * Prerender the given {@link LatexPresentation}s. This method will
		 * block if another instance of this class is running, until the
		 * instance is done. Only one instance can run at a time.
		 */
		public void run()
		{
			synchronized (sync)
			{
				if (wait)
				{
					try
					{
						sync.wait();
					}
					catch (InterruptedException e)
					{
					}
					wait = true;
				}
				else
				{
					wait = true;
				}
			}
			SwingUtilities.invokeLater(new Runnable()
			{

				public void run()
				{
					if (isLatexEnabled())
					{
						if (!new LatexPrerenderer(toPrerender.iterator())
								.waitFor())
						{
							setLatexEnabled(false);
						}
						else
						{
							for (LatexPresentation lp : toPrerender)
							{
								lp.setAllowedRendering(true);
								lp.forceRepaint();
							}
						}
					}
					synchronized (sync)
					{
						wait = false;
						sync.notifyAll();
					}
				}
			});
		}
	}

	/**
	 * Switches LaTeX rendering of labels on and off.
	 * 
	 * @param b
	 *            <code>true</code> to turn LaTeX rendering on,
	 *            <code>false</code> to turn LaTeX rendering off
	 */
	public static void setLatexEnabled(boolean b)
	{
		if (b && !Hub.persistentData.getBoolean(LATEX_OPTION))
		{
			for (LatexPresentation lp : presentations)
			{
				lp.setAllowedRendering(false);
			}
			Hub.persistentData.setBoolean(LATEX_OPTION, true);
			optionBinder.set(true);
			new Thread(new SetLatexUpdater()).start();
		}
		else
		{
			Hub.persistentData.setBoolean(LATEX_OPTION, false);
			optionBinder.set(false);
			Hub.getWorkspace().fireRepaintRequired();
		}
	}

	/**
	 * Starts the prerendering of a {@link LatexPresentation}. The presentation
	 * is repainted at the end.
	 * 
	 * @param lp
	 *            the {@link LatexPresentation} to prerender
	 */
	public static void prerenderAndRepaint(LatexPresentation lp)
	{
		new Thread(new SetLatexUpdater(lp)).start();
	}

	/**
	 * Returns the {@link Renderer} to be used for rendering LaTeX.
	 * 
	 * @return the {@link Renderer} to be used for rendering LaTeX
	 */
	public static Renderer getRenderer()
	{
		if (renderer == null)
		{
			renderer = Renderer.getRenderer(new File(getLatexPath()), new File(
					getGSPath()));
		}
		return renderer;
	}

	/**
	 * Handle the situation when a LaTeX rendering problem occurs. Turns off
	 * LaTeX rendering of the labels. Asks the user if they wish to verify the
	 * LaTeX settings.
	 */
	public static void handleRenderingProblem()
	{
		setLatexEnabled(false);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				int choice = JOptionPane.showConfirmDialog(Hub.getMainWindow(),
						Hub.string("renderProblem"),
						Hub.string("renderProblemTitle"),
						JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION)
				{
					new OptionsWindow(Hub.string("latexOptionsTitle"));
				}
			}
		});
	}

	/**
	 * Returns the font size used for LaTeX rendering.
	 * 
	 * @return the font size used for LaTeX rendering
	 */
	public static float getFontSize()
	{
		return 12;
	}
}
