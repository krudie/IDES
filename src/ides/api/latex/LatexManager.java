package ides.api.latex;

import ides.api.core.Workspace;
import ui.FilmStrip;

/**
 * Interface for the coordinator of LaTeX rendering in IDES.
 * 
 * @author Lenko Grigorov
 */
public interface LatexManager {
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
    public void addLatexPresentation(LatexPresentation lp);

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
    public void removeLatexPresentation(LatexPresentation lp);

    /**
     * Returns <code>true</code> if LaTeX rendering of labels is on,
     * <code>false</code> otherwise.
     * 
     * @return <code>true</code> if LaTeX rendering of labels is on,
     *         <code>false</code> otherwise
     */
    public boolean isLatexEnabled();

    /**
     * Switches LaTeX rendering of labels on and off.
     * 
     * @param b <code>true</code> to turn LaTeX rendering on, <code>false</code> to
     *          turn LaTeX rendering off
     */
    public void setLatexEnabled(boolean b);

    /**
     * Starts the prerendering of a {@link LatexPresentation}. The presentation is
     * repainted at the end.
     * 
     * @param lp the {@link LatexPresentation} to prerender
     */
    public void prerenderAndRepaint(LatexPresentation lp);

    /**
     * Returns the {@link Renderer} to be used for rendering LaTeX.
     * 
     * @return the {@link Renderer} to be used for rendering LaTeX
     */
    public Renderer getRenderer();

    /**
     * Handle the situation when a LaTeX rendering problem occurs. Turns off LaTeX
     * rendering of the labels. Asks the user if they wish to verify the LaTeX
     * settings.
     */
    public void handleRenderingProblem();
}
