package presentation.fsa;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;

import ides.api.cache.NotInCacheException;
import ides.api.core.Hub;
import ides.api.latex.LatexElement;
import ides.api.latex.LatexRenderException;
import ides.api.latex.LatexUtils;
import io.fsa.ver2_1.GraphExporter;
import presentation.GraphicalLayout;
import presentation.PresentationElement;
import util.BentoBox;

/**
 * @author helen bretzke
 */

public class GraphLabel extends GraphElement implements LatexElement {
    // SJW - The bounds should be calculated on the fly
    // to make sure updates are observed
    // protected Rectangle bounds;

    protected Font font;

    protected BufferedImage rendered = null;

    // Added by SJW
    private int textMetricsWidth = 0;

    private int textMetricsHeight = 0;

    private static final double DBL_RENDERED_SCALE_WIDTH = 2.0;

    private static final double DBL_RENDERED_SCALE_HEIGHT = 2.25;

    private static final double DBL_NOT_RENDERED_SCALE_WIDTH = 2;

    private static final double DBL_NOT_RENDERED_SCALE_HEIGHT = 2.75;

    private static final int TEXT_MARGIN_WIDTH = 2;

    public static final int DEFAULT_FONT_SIZE = 12;

    public GraphLabel(String text, int fontSize) {
        setLayout(new GraphicalLayout());
        font = new Font("times", Font.ITALIC, fontSize);
        setText(text);
    }

    // public GraphLabel(GraphicalLayout layout)
    // {
    // setLayout(layout);
    // }

    /**
     * @param text     string to display in this label
     * @param location the x,y coordinates of the top left corner of this label
     */
    public GraphLabel(String text, int fontSize, Point2D location) {
        this(text, fontSize);
        getLayout().setLocation((float) location.getX(), (float) location.getY());
    }

    // /**
    // * TODO decide whether the DrawingBoard is a special kind of Glyph.
    // *
    // * @param text
    // * string to display in this label
    // * @param parent
    // * glyph in which this label is displayed
    // * @param location
    // * the x,y coordinates of the top left corner of this label
    // */
    // public GraphLabel(String text, GraphElement parent, Point2D location)
    // {
    // this(text, location);
    // setParent(parent);
    // }

    @Override
    public void draw(Graphics g) {

        if (visible) {
            if ((selected || highlighted) && !getText().equals("")) { // && parent != null && !parent.isSelected()){
                drawBorderAndTether(g);
            }
        }

        if (Hub.getLatexManager().isLatexEnabled()) {
            if (!visible || "".equals(getText()) || rendered == null) {
                return;
            }
            // try
            // {
            // renderIfNeeded();
            // }
            // catch (LatexRenderException e)
            // {
            // LatexManager.handleRenderingProblem();
            // return;
            // }
            // SJW - Mod to fiddle with positioning
            /*
             * ((Graphics2D)g).drawImage(rendered, null,
             * (int)layout.getLocation().x,(int)layout.getLocation().y);
             */
            Rectangle2D renderedBounds = bounds();
            // circumvent AWT bug (crash if drawing image on scaled down
            // graphics)
            if (getGraph().isDrawRenderedLabels()) {
                ((Graphics2D) g).drawImage(rendered, null, (int) renderedBounds.getX(), (int) renderedBounds.getY());
            }
        } else {
            if (highlighted) {
                g.setColor(getLayout().getHighlightColor());
            } else if (selected) {
                g.setColor(getLayout().getSelectionColor());
            } else {
                g.setColor(getLayout().getColor());
            }
            drawText(g);
        }
    }

    /**
     * Draws the text for this label in the given graphics context. Updates bounds
     * based on font metrics of graphics and the text to be drawn.
     * 
     * @param g
     */
    private void drawText(Graphics g) {
        // ////////////////////////////////////////////////////////////
        // TODO compute bounds and drawing string with multiple lines
        String[] lines = getText().split("\n");
        if (lines.length > 1) {
            // multiple line text

        }
        // ////////////////////////////////////////////////////////////

        // Compute bounds
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();
        /*
         * SJW - Adjusted so these values are held in member variables int width =
         * metrics.stringWidth( layout.getText() ); int height = metrics.getHeight();
         */
        updateMetrics(metrics);

        /*
         * SJW - Call bounds() instrad to compute all this bounds.setSize(width,
         * height); bounds.setLocation(new Point((int)(layout.getLocation().x -
         * width/2), (int)(layout.getLocation().y - height/2)));
         */
        // Rectangle2D textBounds = bounds();
        int x = BentoBox
                .convertDoubleToInt(getLayout().getLocation().x - (textMetricsWidth / DBL_NOT_RENDERED_SCALE_WIDTH));
        int y = BentoBox
                .convertDoubleToInt(getLayout().getLocation().y + (textMetricsHeight / DBL_NOT_RENDERED_SCALE_HEIGHT));

        g.drawString(getText(), x, y);
    }

    /**
     * @return the text in the label
     */
    public String getText() {
        return getLayout().getText();
    }

    /**
     * @param g
     */
    private void drawBorderAndTether(Graphics g) {
        if (getParent() != null && getParent() instanceof Node) {
            return;
        }

        if (selected) {
            g.setColor(getLayout().getSelectionColor());
        } else if (highlighted) {
            g.setColor(getLayout().getHighlightColor());
        } else {
            g.setColor(getLayout().getColor());
        }

        Stroke s = ((Graphics2D) g).getStroke();
        ((Graphics2D) g).setStroke(GraphicalLayout.DASHED_STROKE);

        Rectangle bounds = bounds();
        bounds.x = bounds.x - TEXT_MARGIN_WIDTH;
        bounds.y = bounds.y - TEXT_MARGIN_WIDTH;
        bounds.width = bounds.width + TEXT_MARGIN_WIDTH * 2;
        bounds.height = bounds.height + TEXT_MARGIN_WIDTH * 2;
        ((Graphics2D) g).draw(bounds); // TODO draw border for free labels too

        // FIXME only show for edge labels
        // KLUDGE instanceof, should have subclasses EdgeLabel and NodeLabel
        if (getParent() != null && getParent() instanceof Edge) { // draw the tether

            Point2D.Double corner = nearestCorner(getParent().getLocation(), bounds);

            // g.drawLine((int)bounds().getX(),
            // (int)bounds().getY(),
            // (int)getParent().getLocation().x,
            // (int)getParent().getLocation().y);
            g.drawLine((int) corner.x, (int) corner.y, (int) getParent().getLocation().x,
                    (int) getParent().getLocation().y);
        }
        ((Graphics2D) g).setStroke(s);
    }

    /**
     * Computes the corner of <code>rect</code> that is nearest to
     * <code>point</code>. FIXME Always returns the top-right corner.
     */
    private Point2D.Double nearestCorner(Point2D.Float point, Rectangle rect) {
        Point2D.Double nearest = new Point2D.Double(0, 0);

        // upper left
        Point2D.Double corner = new Point2D.Double(rect.getX(), rect.getY());

        double distance = point.distance(corner);
        double min = distance;
        nearest.x = corner.x;
        nearest.y = corner.y;

        // bottom left
        corner.y += rect.height;
        distance = point.distance(corner);

        if (distance < min) {
            min = distance;
            nearest.x = corner.x;
            nearest.y = corner.y;
        }

        // bottom right
        corner.x += rect.width;
        distance = point.distance(corner);

        if (distance < min) {
            min = distance;
            nearest.x = corner.x;
            nearest.y = corner.y;
        }

        // top right
        corner.y -= rect.height;
        distance = point.distance(corner);

        if (distance < min) {
            min = distance;
            nearest.x = corner.x;
            nearest.y = corner.y;
        }

        return nearest;
    }

    @Override
    public Rectangle bounds() {

        Rectangle labelBounds = new Rectangle();

        if (getText().length() == 0) {
            labelBounds.height = 0;
            labelBounds.width = 0;
            labelBounds.x = (int) getLayout().getLocation().x;
            labelBounds.y = (int) getLayout().getLocation().y;
        }

        if (Hub.getLatexManager().isLatexEnabled()) {
            if (rendered != null) {
                labelBounds.height = rendered.getHeight();
                labelBounds.width = rendered.getWidth();
            } else {
                // FIXME arbitrary dimensions: has to be recomputed after
                // rendering
                // NOTE if not set to values > zero, causes an update loop
                // because empty labels at location (0,0)
                // are given negative bounds below.
                labelBounds.height = 0;
                labelBounds.width = 0;
            }

            // SJW - Now, update the x and y based on the width and height
            labelBounds.x = BentoBox
                    .convertDoubleToInt(getLayout().getLocation().x - (labelBounds.width / DBL_RENDERED_SCALE_WIDTH));
            labelBounds.y = BentoBox
                    .convertDoubleToInt(getLayout().getLocation().y - (labelBounds.height / DBL_RENDERED_SCALE_HEIGHT));
        } else {
            labelBounds.width = textMetricsWidth;
            labelBounds.height = textMetricsHeight;

            // SJW - Now, update the x and y based on the width and height
            labelBounds.x = BentoBox.convertDoubleToInt(
                    getLayout().getLocation().x - (labelBounds.width / DBL_NOT_RENDERED_SCALE_WIDTH));
            labelBounds.y = BentoBox.convertDoubleToInt(
                    getLayout().getLocation().y - (labelBounds.height / DBL_NOT_RENDERED_SCALE_HEIGHT));
        }

        return labelBounds;
    }

    @Override
    public boolean intersects(Point2D p) {
        return bounds().contains(p);
    }

    @Override
    public void translate(float x, float y) {
        // KLUDGE label should store its offset from its parent,
        // parent should be oblivious. No time to fix properly.
        PresentationElement parent = getParent();
        if (parent != null) {
            Point2D.Float offset = parent.getLayout().getLabelOffset();
            offset.setLocation(offset.x + x, offset.y + y);
            parent.getLayout().setLabelOffset(offset);
        }
        super.translate(x, y);
    }

    public void insert(PresentationElement child, long index) {
    }

    public void insert(PresentationElement g) {
    }

    @Override
    public void remove(PresentationElement child) {
    }

    @Override
    public PresentationElement child(long index) {
        return null;
    }

    @Override
    public Iterator<GraphElement> children() {
        return null;
    }

    public void setText(String s) {
        if (s == null) {
            s = "";
        }
        if (!s.equals(getText())) {
            getLayout().setText(s);
            rendered = null;
            FSAGraph graph = getGraph();
            if (graph != null && graph.isRenderingOn()) {
                try {
                    renderIfNeeded();
                } catch (LatexRenderException e) {
                    Hub.getLatexManager().handleRenderingProblem();
                    rendered = null;
                }
            }
        }
        setNeedsRefresh(true);
        updateMetrics();
    }

    // public void softSetText(String s)
    // {
    // if (s == null)
    // {
    // s = "";
    // }
    // if (!s.equals(getLayout().getText()))
    // {
    // getLayout().setText(s);
    // rendered = null;
    // }
    // setNeedsRefresh(true);
    // updateMetrics();
    // }

    /**
     * Renders the label using LaTeX.
     * 
     * @throws LatexRenderException if rendering fails
     * @see #rendered
     * @see #renderIfNeeded()
     */
    private void render() throws LatexRenderException {
        // try{throw new RuntimeException();}catch(Exception
        // e){e.printStackTrace();}
        needsRefresh = true;
        String label = "\\fontsize{" + getFontSize() + "} {"
                + BentoBox.roundDouble(getFontSize() * GraphExporter.DBL_PSTRICKS_FONT_BASELINE_FACTOR, 2)
                + "} \\selectfont " + getText();

        if (label == null) {
            label = "";
        }
        byte[] data = null;
        try {
            data = (byte[]) Hub.getCache().get(getClass().getName() + label);
        } catch (NotInCacheException e) {
            data = LatexUtils.labelStringToImageBytes(label);
            Hub.getCache().put(getClass().getName() + label, data);
        }
        try {
            rendered = ImageIO.read(new ByteArrayInputStream(data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * If the label has not been rendered yet, it gets rendered using LaTeX
     * 
     * @throws LatexRenderException if rendering fails
     * @see #rendered
     * @see #render()
     */
    public void renderIfNeeded() throws LatexRenderException {
        if (needsRendering()) {
            render();
        }
    }

    public boolean needsRendering() {
        return rendered == null && !getGraph().isAvoidLayoutDrawing();
    }

    /**
     * This method is responsible for creating a string that contains an appropriate
     * (depending on the type) representation of this edge. TODO: Do a final fix on
     * the "y" issue
     * <p>
     * author Sarah-Jane Whittaker
     * 
     * @param selectionBox The area being selected or considered
     * @param exportType   The export format
     * @return String The string representation
     */
    public String createExportString(Rectangle selectionBox, int exportType) {
        String exportString = "";
        Rectangle2D b = bounds();
        Rectangle labelBounds = new Rectangle((int) b.getX(), (int) b.getY(), (int) b.getWidth(), (int) b.getHeight());

        // This is taken from Mike Wood - thanks, Mike!!!
        String safeLabel = getText();
        safeLabel = BentoBox.replaceAll(safeLabel, "\\\\" + BentoBox.STR_ASCII_STANDARD_RETURN, "\\\\ ");
        safeLabel = BentoBox.replaceAll(safeLabel, "\\\\ " + BentoBox.STR_ASCII_STANDARD_RETURN, "\\\\ ");
        safeLabel = BentoBox.replaceAll(safeLabel,
                BentoBox.STR_ASCII_STANDARD_RETURN + BentoBox.STR_ASCII_STANDARD_RETURN, "\\\\ ");
        safeLabel = BentoBox.replaceAll(safeLabel, BentoBox.STR_ASCII_STANDARD_RETURN, " ");

        // If this label is empty, ignore it
        if (safeLabel.length() == 0) {
            return "";
        }

        // Make sure this node is contained within the selection box
        if (!(selectionBox.contains(labelBounds))) {
            // System.out.println("Label " + labelBounds
            // + " outside bounds " + selectionBox);
            return exportString;
        }

        // Adjust the bounds for PSTricks export
        /*
         * if (getParent() instanceof CircleNode) { CircleNode parentNode = (CircleNode)
         * getParent(); CircleNodeLayout nodeLayout = (CircleNodeLayout)
         * parentNode.getLayout(); if (nodeLayout.getLocation().y < (labelBounds.y +
         * (labelBounds.height / 2.0))) { labelBounds.y = BentoBox.convertDoubleToInt(
         * nodeLayout.getLocation().y); } }
         */

        if (exportType == GraphExporter.INT_EXPORT_TYPE_PSTRICKS) {
            exportString = "  \\rput(" + (labelBounds.x - selectionBox.x + (labelBounds.width / 2.0)) + ","
                    + (selectionBox.y + selectionBox.height - labelBounds.y - (labelBounds.height / 2.0))
                    + "){\\parbox{" + labelBounds.width + "pt}{\\fontsize{" + getFontSize() + "}{"
                    + BentoBox.roundDouble(getFontSize() * GraphExporter.DBL_PSTRICKS_FONT_BASELINE_FACTOR, 2)
                    + "} \\selectfont \\begin{center}" + safeLabel + "\\end{center}}}\n";
        } else if (exportType == GraphExporter.INT_EXPORT_TYPE_EPS) {
            // LENKO!!!
        }

        return exportString;
    }

    public void updateLayout(String text, Point2D.Float location) {
        setText(text);
        if (!location.equals(getLayout().getLocation())) {
            getLayout().setLocation(location.x, location.y);
            setNeedsRefresh(true);
        }
    }

    /**
     * 
     */
    public void updateMetrics() {
        Frame mainWindow = Hub.getMainWindow();
        Graphics mainGraphics = mainWindow.getGraphics();
        FontMetrics mainMetrics = mainGraphics.getFontMetrics(font);
        updateMetrics(mainMetrics);
    }

    /**
     * 
     */
    private void updateMetrics(FontMetrics metrics) {
        textMetricsWidth = metrics.stringWidth(getText());
        textMetricsHeight = metrics.getHeight();
    }

    public void setFontSize(int fs) {
        int prevSize = getFontSize();
        if (fs <= 0) {
            fs = 12;
        }

        font = new Font("times", Font.ITALIC, fs);

        if (fs != prevSize) {
            rendered = null; // causes needsRendering() to return true
        }
        updateMetrics();
    }

    public int getFontSize() {
        return font.getSize();
    }

    public void refresh() {
        int size = DEFAULT_FONT_SIZE;
        if (getGraph() != null) {
            size = (int) getGraph().getFontSize();
        }

        if (size != getFontSize()) {
            setFontSize(size);
        }

    }
}