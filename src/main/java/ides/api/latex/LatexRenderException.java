package ides.api.latex;

/**
 * Exception used by {@link Renderer} to announce problems pertaining to LaTeX
 * rendering.
 * 
 * @author Lenko Grigorov
 */
public class LatexRenderException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 4399236169383418987L;

    public LatexRenderException() {
        super();
    }

    public LatexRenderException(String arg0) {
        super(arg0);
    }

    public LatexRenderException(Throwable arg0) {
        super(arg0);
    }

    public LatexRenderException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}
