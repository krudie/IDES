package ides.api.latex;

import java.util.Collection;

import ides.api.plugin.presentation.Presentation;

/**
 * A {@link Presentation} which contains elements to be rendered with LaTeX.
 * <p>
 * The use of this interface is optional, if one would like to take advantage of
 * some automatic services offered by {@link LatexManager}.
 * <p>
 * The intention for this interface is to allow {@link LatexManager} to manage
 * the prerendering of LaTeX labels when LaTeX rendering is turned on or when a
 * new model is loaded into IDES. However, this management is only supplemental
 * to that of the LaTeX on/off setting. Every presentation should respect the
 * on/off setting by checking {@link LatexManager#isLatexEnabled()} before
 * rendering LaTeX elements.
 * <p>
 * The intended procedure for newly opened models when LaTeX rendering is on is
 * as follows:
 * <ol>
 * <li>A {@link LatexPresentation} is instantiated by IDES to display a model.
 * The presentation does not render its elements until
 * {@link #setAllowedRendering(boolean)} is called with a <code>true</code>
 * argument (by default, newly instantiated {@link LatexPresentation}s do not
 * render their LaTeX elements).
 * <li>IDES requests a list of elements which need LaTeX rendering (
 * {@link #getUnrenderedLatexElements()}).
 * <li>In a separate thread, IDES prerenders all elements from the list. In the
 * meantime, the {@link LatexPresentation} may be already visible on the screen.
 * <li>If the user cancels the prerendering process, LaTeX rendering is turned
 * off and the next step is skipped.
 * <li>When the prerendering is done, the {@link LatexPresentation} gets
 * notified that it can now manage its own rendering (
 * {@link #setAllowedRendering(boolean)} with a <code>true</code> argument).
 * Allowing rendering does not override the LaTeX on/off setting.
 * </ol>
 * <p>
 * The intended procedure when turning LaTeX rendering on is as follows:
 * <ol>
 * <li>All {@link LatexPresentation}s in the workspace are notified to stop
 * rendering LaTeX elements ({@link #setAllowedRendering(boolean)} with a
 * <code>false</code> argument).
 * <li>The LaTeX setting is turned on.
 * <li>In a separate thread, IDES prerenders all LaTeX elements from all
 * {@link LatexPresentation}s in the workspace.
 * <li>If the user cancels the prerendering process, LaTeX rendering is turned
 * off and the next step is skipped.
 * <li>When the prerendering is done, the {@link LatexPresentation}s get
 * notified that they can now manage their own rendering (
 * {@link #setAllowedRendering(boolean)} with a <code>true</code> argument).
 * Allowing rendering does not override the LaTeX on/off setting.
 * </ol>
 * 
 * @see LatexManager#isLatexEnabled()
 * @author Lenko Grigorov
 */
public interface LatexPresentation extends Presentation {
    /**
     * Returns a list of all {@link LatexElement}s in the presentation which need to
     * be rendered. This list does not include elements whose rendered versions have
     * been cached by the presentation.
     * 
     * @return list of all {@link LatexElement}s in the presentation which need to
     *         be rendered
     */
    public Collection<LatexElement> getUnrenderedLatexElements();

    /**
     * Allows or disallows rendering of LaTeX elements by the presentation. This
     * control is supplemental to the LaTeX on/off setting (
     * {@link LatexManager#isLatexEnabled()}).
     * <p>
     * A {@link LatexPresentation} must not render LaTeX elements unless <b>both</b>
     * LaTeX rendering is on and rendering is allowed via this method.
     * 
     * @param b <code>true</code> to allow LaTeX rendering by the presentation,
     *          given that LaTeX rendering is on; <code>false</code> to disallow
     *          LaTeX rendering by the presentation, regardless of the LaTeX
     *          rendering setting
     * @see LatexManager#isLatexEnabled()
     */
    public void setAllowedRendering(boolean b);

    /**
     * Returns what is the permission for rendering LaTeX elements by the
     * presentation.
     * 
     * @return <code>true</code> if LaTeX rendering is allowed for this
     *         presentation; <code>false</code> if LaTeX rendering is disallowed for
     *         this presentation
     * @see #setAllowedRendering(boolean)
     */
    public boolean isAllowedRendering();
}
