package ides.api.latex;

/**
 * An element of a {@link LatexPresentation} for which LaTeX rendering is used.
 * 
 * @author Lenko Grigorov
 */
public interface LatexElement {
    /**
     * Request for the element to render itself if it needs to get rendered.
     * 
     * @throws LatexRenderException if LaTeX rendering fails
     */
    public void renderIfNeeded() throws LatexRenderException;
}
