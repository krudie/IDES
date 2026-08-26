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

    /**
     * Create a default exception.
     */
    public LatexRenderException() {
        super();
    }

    /**
     * Create an exception with a message.
     * 
     * @param arg0 the message
     */
    public LatexRenderException(String arg0) {
        super(arg0);
    }

    /**
     * Create an exception with a cause.
     * 
     * @param arg0 the cause
     */
    public LatexRenderException(Throwable arg0) {
        super(arg0);
    }

    /**
     * Create an exception with a message and a cause.
     * 
     * @param arg0 the message
     * @param arg1 the cause
     */
    public LatexRenderException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}
