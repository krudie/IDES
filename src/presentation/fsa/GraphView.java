package presentation.fsa;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.JComponent;
import javax.swing.undo.CompoundEdit;

import ides.api.core.Hub;
import ides.api.latex.LatexElement;
import ides.api.latex.LatexPresentation;
import ides.api.model.fsa.FSAModel;
import ides.api.plugin.presentation.GlobalFontSizePresentation;
import ides.api.plugin.presentation.Presentation;
import ides.api.plugin.presentation.ZoomablePresentation;
import presentation.GraphicalLayout;
import presentation.fsa.actions.GraphActions;

/**
 * The visual display of an FSAGraph. Subscribes and response to change
 * notifications from the underlying Publisher portion of the underlying graph
 * layout model. Canvas can be scaled to display the graph at any size from full
 * to thumbnail representation.
 * 
 * @see Presentation
 * @author Helen Bretzke
 * @author Lenko Grigorov
 */
@SuppressWarnings("serial")
public class GraphView extends JComponent
        implements FSAGraphSubscriber, LatexPresentation, ZoomablePresentation, GlobalFontSizePresentation {
    protected static final String FSA_LAYOUT = "presentation.fsa.FSAGraph";

    protected static final int GRAPH_BORDER_THICKNESS = 10;

    protected static final String SHIFT_GRAPH_AFTER_PRERENDER = "presentation.GraphView.shiftGraph";

    protected static final String GRAPH_ALREADY_PRESHIFTED = "presentation.GraphView.alreadyPreshifted";

    protected float scaleFactor = 0.25f;

    protected Rectangle graphBounds = new Rectangle();

    /**
     * if true, refreshView() will set the scale factor so that the whole model fits
     * in the view
     */
    protected boolean scaleToFit = true;

    /**
     * Presentation model (the composite structure that represents the DES model.)
     * which handles synchronizing FSA model with the displayed graph.
     */
    protected FSAGraph graphModel;

    public GraphView() {
        setGraphModel(null);
    }

    public GraphView(FSAModel model) {
        setGraphModel(retrieveGraph(model));
    }

    protected FSAGraph retrieveGraph(FSAModel model) {
        FSAGraph graph;
        if (model.hasAnnotation(FSA_LAYOUT)) {
            graph = (FSAGraph) model.getAnnotation(FSA_LAYOUT);
        } else {
            graph = new FSAGraph(model);
            model.setAnnotation(FSA_LAYOUT, graph);
        }
        return graph;
    }

    public JComponent getGUI() {
        return this;
    }

    // public LayoutShell getLayoutShell()
    // {
    // return graphModel;
    // }

    public void setTrackModel(boolean b) {
        if (b) {
            FSAGraphSubscriber[] listeners = graphModel.getFSAGraphSubscribers();
            boolean found = false;
            for (int i = 0; i < listeners.length; ++i) {
                if (listeners[i] == this) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                graphModel.addSubscriber(this);
            }
        } else {
            graphModel.removeSubscriber(this);
        }
    }

    public void release() {
        setTrackModel(false);
        graphModel.removeHook(this);
        if (graphModel.hookCount() == 0) {
            graphModel.release();
            graphModel.getModel().removeAnnotation(FSA_LAYOUT);
        }
    }

    public FSAModel getModel() {
        if (graphModel == null) {
            return null;
        }
        return graphModel.getModel();
    }

    @Override
    public void paint(Graphics g) {
        getGraphModel().setDrawRenderedLabels(false);
        paint(g, true);
        getGraphModel().setDrawRenderedLabels(true);
    }

    public void forceRepaint() {
        refreshView();
    }

    public void originalPaint(Graphics g) {
        super.paint(g);
    }

    public void paint(Graphics g, boolean doFill) {
        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setStroke(GraphicalLayout.WIDE_STROKE);

        Rectangle r = getBounds();
        if (doFill) {
            g2D.setColor(Color.WHITE);
            g2D.fillRect(0, 0, r.width, r.height);
            // FIXME implement better avoid layout
            if (graphModel.isAvoidLayoutDrawing()) {
                g2D.setColor(Color.BLACK);
                g2D.drawLine(0, 0, r.width, r.height);
                g2D.drawLine(0, r.height, r.width, 0);
                return;
            }
        }

        g2D.scale(scaleFactor, scaleFactor);
        if (graphModel != null) {
            graphModel.draw(g2D);
        }
    }

    protected void setGraphModel(FSAGraph graphModel) {
        if (this.graphModel != null) {
            this.graphModel.removeSubscriber(this);
            graphModel.removeHook(this);
        }
        this.graphModel = graphModel;

        if (graphModel != null) {
            graphModel.addHook(this);
            graphModel.addSubscriber(this);
            this.setName(graphModel.getName());
            refreshView();
            // shift graph if needed
            if (!graphModel.hasAnnotation(GRAPH_ALREADY_PRESHIFTED)) {
                new GraphActions.ShiftGraphInViewAction(new CompoundEdit(), graphModel).execute();
                graphModel.setAnnotation(GRAPH_ALREADY_PRESHIFTED, true);
            }
            // set up delayed shift if LaTeX is on
            if (!graphModel.hasAnnotation(SHIFT_GRAPH_AFTER_PRERENDER)) {
                graphModel.setAnnotation(SHIFT_GRAPH_AFTER_PRERENDER, Hub.getLatexManager().isLatexEnabled());
            }
        } else {
            this.setName("No automaton");
        }
    }

    public FSAGraph getGraphModel() {
        return graphModel;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(float sf) {
        scaleFactor = sf;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension((int) ((graphBounds.width + GRAPH_BORDER_THICKNESS) * scaleFactor),
                (int) ((graphBounds.height + GRAPH_BORDER_THICKNESS) * scaleFactor));
    }

    /**
     * Respond to change notification from underlying graph model.
     * 
     * @see presentation.fsa.FSAGraphSubscriber#fsaGraphChanged(presentation.fsa.FSAGraphMessage)
     */
    public void fsaGraphChanged(FSAGraphMessage message) {
        // TODO check contents of message to determine minimal response required
        refreshView();
    }

    protected void refreshView() {
        if (getGraphModel() != null) {
            if (getGraphModel().needsRefresh()) {
                getGraphModel().refresh();
            }
            graphBounds = getGraphModel().getBounds(true);
            // System.out.println(graphBounds);

            // the following fuctionality will be assumed by the GraphActions
            // if(graphBounds.x<0||graphBounds.y<0)
            // {
            // System.out.println(""+lastLatexSetting+"->"+LatexManager.
            // isLatexEnabled());
            // if(lastLatexSetting==LatexManager.isLatexEnabled())
            // {
            // new GraphActions.ShiftGraphInViewAction(new
            // CompoundEdit(),graphModel).execute();
            // }
            // else
            // {
            // }
            // }

            if (scaleToFit && getParent() != null) {
                Insets ins = getParent().getInsets();
                float xScale = (float) (getParent().getWidth() - ins.left - ins.right)
                        / (float) (graphBounds.width + graphBounds.x + GRAPH_BORDER_THICKNESS);
                float yScale = (float) (getParent().getHeight() - ins.top - ins.bottom)
                        / (float) (graphBounds.height + graphBounds.y + GRAPH_BORDER_THICKNESS);
                setScaleFactor(Math.min(xScale, yScale));
            }
        }
        revalidate();
        repaint();
    }

    /*
     * Don't need to respond to selection changes. (non-Javadoc)
     * 
     * @see
     * observer.FSMGraphSubscriber#fsmGraphSelectionChanged(observer.FSMGraphMessage
     * )
     */
    public void fsaGraphSelectionChanged(FSAGraphMessage message) {
        refreshView();
    }

    public void fsaGraphSaveStatusChanged(FSAGraphMessage message) {
    }

    protected boolean latexRendering = false;

    public Collection<LatexElement> getUnrenderedLatexElements() {
        HashSet<LatexElement> labels = new HashSet<LatexElement>();
        if (graphModel == null) {
            return labels;
        }
        Collection<Node> nodes = graphModel.getNodes();
        for (Node n : nodes) {
            if (n.getLabel().needsRendering()) {
                labels.add(n.getLabel());
            }
        }
        Collection<Edge> edges = graphModel.getEdges();
        for (Edge e : edges) {
            GraphLabel l = e.getLabel();
            if (l != null && l.needsRendering()) {
                labels.add(e.getLabel());
            }
        }
        for (GraphLabel l : graphModel.getFreeLabels()) {
            if (l.needsRendering()) {
                labels.add(l);
            }
        }
        return labels;
    }

    public boolean isAllowedRendering() {
        return latexRendering;
    }

    public void setAllowedRendering(boolean b) {
        latexRendering = b;
        // System.out.println("allowed ("+b+") "+graphModel.getAnnotation(
        // SHIFT_GRAPH_AFTER_PRERENDER));
        // shift graph if needed
        if (b && graphModel != null && graphModel.hasAnnotation(SHIFT_GRAPH_AFTER_PRERENDER)
                && (Boolean) graphModel.getAnnotation(SHIFT_GRAPH_AFTER_PRERENDER)) {
            new GraphActions.ShiftGraphInViewAction(new CompoundEdit(), graphModel).execute();
        }
        graphModel.setAnnotation(SHIFT_GRAPH_AFTER_PRERENDER, false);
    }

    public void setFontSize(float fs) {
        if (fs != getGraphModel().getFontSize()) {
            new GraphActions.ChangeFontSizeAction(getGraphModel(), fs).execute();
        }
        if (graphModel.isRenderingOn()) {
            Hub.getLatexManager().prerenderAndRepaint(this);
        }
    }
}
