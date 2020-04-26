package services.latex;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ides.api.core.Hub;
import ides.api.core.Workspace;
import ides.api.latex.LatexManager;
import ides.api.latex.LatexPresentation;
import ides.api.latex.Renderer;
import ides.api.plugin.presentation.GlobalFontSizePresentation;
import ides.api.utilities.GeneralUtils;
import ui.FilmStrip;
import ui.OptionsWindow;
import util.BooleanUIBinder;

public class LatexBackend implements LatexManager {
    protected final static String LATEX_OPTION = "useLatexLabels";

    // prevent instantiation
    private LatexBackend() {
    }

    @Override
    public Object clone() {
        throw new RuntimeException("Cloning of " + this.getClass().toString() + " not supported.");
    }

    /**
     * Instance for the non-static methods.
     */
    private static LatexBackend me = null;

    public static LatexBackend instance() {
        if (me == null) {
            me = new LatexBackend();
        }
        return me;
    }

    /**
     * The LaTeX renderer to be used for rendering throughout the program.
     */
    private static Renderer renderer = null;

    /**
     * Returns the path to the directory of the <code>latex</code> and
     * <code>dvips</code> executables.
     * 
     * @return path to the directory of the <code>latex</code> and
     *         <code>dvips</code> executables
     */
    static String getLatexPath() {
        return Hub.getPersistentData().getProperty("latexPath");
    }

    /**
     * Returns the path to the GhostScript executable file.
     * 
     * @return the path to the GhostScript executable file
     */
    static String getGSPath() {
        return Hub.getPersistentData().getProperty("gsPath");
    }

    /**
     * Sets the path to the directory of the <code>latex</code> and
     * <code>dvips</code> executables.
     * 
     * @param path path to the directory of the <code>latex</code> and
     *             <code>dvips</code> executables
     */
    static void setLatexPath(String path) {
        Hub.getPersistentData().setProperty("latexPath", path);
        renderer = Renderer.getRenderer(new File(getLatexPath()), new File(getGSPath()));
    }

    /**
     * Sets the path to the GhostScript executable file.
     * 
     * @param path path to the GhostScript executable file
     */
    static void setGSPath(String path) {
        Hub.getPersistentData().setProperty("gsPath", path);
        renderer = Renderer.getRenderer(new File(getLatexPath()), new File(getGSPath()));
    }

    /**
     * Returns the {@link Renderer} to be used for rendering LaTeX.
     * 
     * @return the {@link Renderer} to be used for rendering LaTeX
     */
    public Renderer getRenderer() {
        if (renderer == null) {
            renderer = Renderer.getRenderer(new File(getLatexPath()), new File(getGSPath()));
        }
        return renderer;
    }

    protected static BooleanUIBinder optionBinder = new BooleanUIBinder();

    /**
     * Returns the {@link BooleanUIBinder} which can be used by interface elements
     * to get automatically updated with the state of the LaTeX rendering option.
     * 
     * @return the {@link BooleanUIBinder} which can be used by interface elements
     *         to get automatically updated with the state of the LaTeX rendering
     *         option
     */
    public static BooleanUIBinder getUIBinder() {
        return optionBinder;
    }

    /**
     * Initializes the LaTeX rendering subsystem.
     */
    public static void init() {
        Hub.registerOptionsPane(new LatexOptionsPane());
        renderer = Renderer.getRenderer(new File(getLatexPath()), new File(getGSPath()));
        optionBinder.set(instance().isLatexEnabled());
    }

    /**
     * The {@link LatexPresentation}s which need to be included in the automatic
     * prerendering when LaTeX rendering is turned on. It is the responsibility of
     * the {@link Workspace} to maintain this list up to date. Other modules of IDES
     * which load {@link LatexPresentation}s dynamically (such as the
     * {@link FilmStrip}) also need to update this list.
     * 
     * @see LatexPresentation
     * @see Workspace
     * @see FilmStrip
     */
    protected static Collection<LatexPresentation> presentations = new HashSet<LatexPresentation>();

    /**
     * Adds a {@link LatexPresentation} to the list of {@link LatexPresentation} s
     * which need to be included in the automatic prerendering when LaTeX rendering
     * is turned on.
     * 
     * @param lp the {@link LatexPresentation} to be added to the list
     * @see LatexPresentation
     * @see Workspace
     * @see FilmStrip
     */
    public void addLatexPresentation(LatexPresentation lp) {
        if (!presentations.contains(lp)) {
            presentations.add(lp);
            if (isLatexEnabled()) {
                prerenderAndRepaint(lp);
            }
        }
    }

    /**
     * Removes a {@link LatexPresentation} from the list of
     * {@link LatexPresentation}s which need to be included in the automatic
     * prerendering when LaTeX rendering is turned on.
     * 
     * @param lp the {@link LatexPresentation} to be removed from the list
     * @see LatexPresentation
     * @see Workspace
     * @see FilmStrip
     */
    public void removeLatexPresentation(LatexPresentation lp) {
        presentations.remove(lp);
    }

    /**
     * Returns <code>true</code> if LaTeX rendering of labels is on,
     * <code>false</code> otherwise.
     * 
     * @return <code>true</code> if LaTeX rendering of labels is on,
     *         <code>false</code> otherwise
     */
    public boolean isLatexEnabled() {
        return Hub.getPersistentData().getBoolean(LATEX_OPTION);
    }

    /**
     * A {@link Runnable} that executes the prerendering of
     * {@link LatexPresentation}s. This is needed since the {@link LatexPrerenderer}
     * displays its progress; thus the updating cannot be done inside the Swing
     * event loop.
     * 
     * @see LatexManager#setLatexEnabled(boolean)
     * @author Lenko Grigorov
     */
    private static class SetLatexUpdater implements Runnable {
        /**
         * Object used for synchronization so that only one instance of this class can
         * run at a given time.
         */
        private static Object sync = new Object();

        /**
         * Blocking variable so that only one instance of this class can run at a given
         * time.
         */
        private static boolean wait = false;

        /**
         * Set of {@link LatexPresentation}s which need to be prerendered.
         */
        private Collection<LatexPresentation> toPrerender;

        /**
         * Creates an instance which will prerender all {@link LatexPresentation}s from
         * the list for automatic prerendering.
         * 
         * @see LatexManager#presentations
         */
        public SetLatexUpdater() {
            toPrerender = presentations;
        }

        /**
         * Creates an instance which will prerender a given {@link LatexPresentation}.
         * 
         * @param lp
         */
        public SetLatexUpdater(LatexPresentation lp) {
            toPrerender = new HashSet<LatexPresentation>();
            toPrerender.add(lp);
        }

        /**
         * Prerender the given {@link LatexPresentation}s. This method will block if
         * another instance of this class is running, until the instance is done. Only
         * one instance can run at a time.
         */
        public void run() {
            synchronized (sync) {
                if (wait) {
                    try {
                        sync.wait();
                    } catch (InterruptedException e) {
                    }
                    wait = true;
                } else {
                    wait = true;
                }
            }
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    if (instance().isLatexEnabled()) {
                        if (!new LatexPrerenderer(toPrerender.iterator()).waitFor()) {
                            instance().setLatexEnabled(false);
                        } else {
                            for (LatexPresentation lp : toPrerender) {
                                lp.setAllowedRendering(true);
                                lp.forceRepaint();
                            }
                        }
                    }
                    synchronized (sync) {
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
     * @param b <code>true</code> to turn LaTeX rendering on, <code>false</code> to
     *          turn LaTeX rendering off
     */
    public void setLatexEnabled(boolean b) {
        if (b && !Hub.getPersistentData().getBoolean(LATEX_OPTION)) {
            for (LatexPresentation lp : presentations) {
                lp.setAllowedRendering(false);
            }
            Hub.getPersistentData().setBoolean(LATEX_OPTION, true);
            LatexBackend.getUIBinder().set(true);
            new Thread(new SetLatexUpdater()).start();
            // currently latex won't render font sizes above 25. Give a warning
            // if turning on latex and font size is currently bigger.
            if (Hub.getWorkspace().getPresentationsOfType(GlobalFontSizePresentation.class).size() > 0) {
                if (Hub.getUserInterface().getFontSelector().getFontSize() > 25) {
                    // don't restrict the user
                    // -- Lenko
                    // Hub.getUserInterface().getFontSelector().setFontSize(25);
                    LatexMessages.fontSizeTooBig();
                }
            }

        } else {
            Hub.getPersistentData().setBoolean(LATEX_OPTION, false);
            LatexBackend.getUIBinder().set(false);
            Hub.getWorkspace().fireRepaintRequired();
        }
    }

    /**
     * Starts the prerendering of a {@link LatexPresentation}. The presentation is
     * repainted at the end.
     * 
     * @param lp the {@link LatexPresentation} to prerender
     */
    public void prerenderAndRepaint(LatexPresentation lp) {
        new Thread(new SetLatexUpdater(lp)).start();
    }

    /**
     * Handle the situation when a LaTeX rendering problem occurs. Turns off LaTeX
     * rendering of the labels. Asks the user if they wish to verify the LaTeX
     * settings.
     */
    public void handleRenderingProblem() {
        setLatexEnabled(false);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int choice = JOptionPane.showConfirmDialog(Hub.getMainWindow(),
                        GeneralUtils.JOptionPaneKeyBinder.messageLabel(Hub.string("renderProblem")),
                        Hub.string("renderProblemTitle"), JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    new OptionsWindow(Hub.string("latexOptionsTitle"));
                }
            }
        });
    }
}
