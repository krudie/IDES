package services.latex;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ides.api.core.Hub;
import ides.api.latex.LatexElement;
import ides.api.latex.LatexPresentation;
import ides.api.latex.LatexRenderException;
import util.InterruptableProgressDialog;

/**
 * This object renders all labels in a {@link LatexPresentation} which need
 * rendering and displays the progress in a dialog box with a progress bar. The
 * rendering can be interrupted by the user.
 * <p>
 * The intended use is when loading a file or when turning on LaTeX rendering
 * for the labels.
 * 
 * @author Lenko Grigorov
 */
public class LatexPrerenderer extends InterruptableProgressDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 3732429673155492650L;

    /**
     * Set to <code>true</code> if the rendering has to be interrupted.
     */
    private boolean cancel = false;

    /**
     * Variable used to track if pre-rendering has finished.
     */
    private boolean doneRendering;

    /**
     * The {@link LatexPresentation}s whose labels should be rendered. The key is
     * the {@link LatexPresentation} and the value is the set of
     * {@link LatexElement}s requiring rendering for the given
     * {@link LatexPresentation}.
     */
    protected Map<LatexPresentation, Collection<LatexElement>> toPrerender = new HashMap<LatexPresentation, Collection<LatexElement>>();

    /**
     * Displays a dialog box with a progress bar and starts rendering the labels of
     * a {@link LatexPresentation}. The user may cancel the process using the
     * controls in the dialog box.
     * 
     * @param lp the {@link LatexPresentation} whose labels have to be rendered
     */
    public LatexPrerenderer(LatexPresentation lp) {
        super(Hub.getMainWindow(), Hub.string("renderPrerenderTitle"), "");

        Collection<LatexElement> elements = lp.getUnrenderedLatexElements();
        if (elements.isEmpty()) {
            close();
            return;
        }
        toPrerender.put(lp, elements);
        doneRendering = false;
        new Thread(this).start();
        setVisible(true);
    }

    /**
     * Displays a dialog box with a progress bar and starts rendering the labels of
     * a set of {@link LatexPresentation}s. The user may cancel the process using
     * the controls in the dialog box.
     * 
     * @param lps an iterator over the set of {@link LatexPresentation}s whose
     *            labels have to be rendered
     */
    public LatexPrerenderer(Iterator<LatexPresentation> lps) {
        super(Hub.getMainWindow(), Hub.string("renderPrerenderTitle"), "");
        for (; lps.hasNext();) {
            LatexPresentation lp = lps.next();
            if (toPrerender.isEmpty()) {
                Collection<LatexElement> elements = lp.getUnrenderedLatexElements();
                if (!elements.isEmpty()) {
                    toPrerender.put(lp, elements);
                }
            } else {
                toPrerender.put(lp, null);
            }
        }
        if (toPrerender.isEmpty()) {
            close();
            return;
        }
        doneRendering = false;
        new Thread(this).start();
        setVisible(true);
    }

    /**
     * Interrupts the process of rendering.
     */
    @Override
    public void interrupt() {
        LatexBackend.instance().setLatexEnabled(false);
        cancel = true;
    }

    /**
     * The main loop where the labels are rendered. Call this method to start
     * rendering.
     */
    @Override
    public void run() {
        while (!isVisible()) {
            Thread.yield();
        }
        for (LatexPresentation model : toPrerender.keySet()) {
            if (cancel) {
                break;
            }
            label.setText(Hub.string("renderPrerender") + model.getModel().getName());
            Collection<LatexElement> labels = toPrerender.get(model);
            if (labels == null) {
                labels = model.getUnrenderedLatexElements();
            }

            progressBar.setMinimum(0);
            progressBar.setMaximum(labels.size());
            int current = 0;
            progressBar.setValue(current);

            Iterator<LatexElement> i = labels.iterator();
            while (i.hasNext()) {
                if (cancel) {
                    break;
                }
                LatexElement l = i.next();
                try {
                    // The initialArrows are amongst the normal edges, so
                    // sometimes l is null for being
                    // a result for trying to get a GraphLabel from an initial
                    // edge.
                    if (l != null) {
                        l.renderIfNeeded();
                    }
                } catch (LatexRenderException e) {
                    LatexBackend.instance().handleRenderingProblem();
                    cancel = true;
                    close();
                    return;
                }
                current++;
                progressBar.setValue(current);
            }
        }
        close();
        return;
    }

    /**
     * Performs the operations needed to be done when the rendering ends or gets
     * interrupted (such as closing the dialog box).
     */
    protected void close() {
        synchronized (this) {
            doneRendering = true;
            notifyAll();
        }
        dispose();
    }

    /**
     * Calling this method blocks until pre-rendering has finished.
     * 
     * @return <code>false</code> if pre-rendering failed or was cancelled;
     *         <code>true</code> otherwise
     */
    public boolean waitFor() {
        synchronized (this) {
            if (!doneRendering) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return !cancel;
    }
}
