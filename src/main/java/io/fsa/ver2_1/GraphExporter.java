package io.fsa.ver2_1;

import java.awt.Rectangle;
import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ides.api.core.Hub;
import ides.api.core.OptionsPane;
import presentation.fsa.CircleNode;
import presentation.fsa.Edge;
import presentation.fsa.FSAGraph;
import presentation.fsa.GraphLabel;
import util.BentoBox;

/**
 * This class is charged with exporting a FSAgraph to one of four formats:
 * LaTex-PSTricks, EPS. All of the methods are static; there is no need to
 * instantiate to perform these functions.
 * 
 * @author Sarah-Jane Whittaker, Lenko Grigorov
 */
public class GraphExporter {

    public static class ExportOptionsPane implements OptionsPane {
        private JCheckBox cbFrame = new JCheckBox(Hub.string("addFrameToExport"));

        private JPanel pane = null;

        public void commitOptions() {
            Hub.getPersistentData().setBoolean(STR_EXPORT_PROP_USE_FRAME, cbFrame.isSelected());
        }

        public void disposePane() {
            pane = null;
        }

        public JPanel getPane() {
            if (pane == null) {
                pane = new JPanel();
                pane.add(cbFrame);
                cbFrame.setSelected(Hub.getPersistentData().getBoolean(STR_EXPORT_PROP_USE_FRAME));
            }
            return pane;
        }

        public String getTitle() {
            return Hub.string("optionsExportTitle");
        }

        public void resetOptions() {
            cbFrame.setSelected(Hub.getPersistentData().getBoolean(STR_EXPORT_PROP_USE_FRAME));
        }
    }

    // /////////////////////////////////////////////////////////////////
    // Static Variables
    // /////////////////////////////////////////////////////////////////
    /** Export Types * */
    private static final int INT_EXPORT_TYPE_START = 1;

    public static final int INT_EXPORT_TYPE_PSTRICKS = INT_EXPORT_TYPE_START;

    public static final int INT_EXPORT_TYPE_EPS = INT_EXPORT_TYPE_PSTRICKS + 1;

    /** PSTricks Stuff * */
    public static final int INT_PSTRICKS_MARKED_STATE_RADIUS_DIFF = 4;

    public static final double DBL_PSTRICKS_SCALE_VALUE = 0.90;

    private static final int INT_PSTRICKS_BORDER_SIZE = 10;

    private static final double DBL_PSTRICKS_MAX_WIDTH = 398.0;

    private static final double DBL_PSTRICKS_MAX_HEIGHT = 648.0;

    public static final double DBL_PSTRICKS_FONT_BASELINE_FACTOR = 1.2;

    /** Export properties * */
    public static final String STR_EXPORT_PROP_USE_FRAME = "addFrameToExport";

    // NOTE: Mike has this string in his LatexPrinter in IDES, but I
    // don't think it does anything, so it's ignored here.
    // private static String STR_TEXT_ADDON = "\n
    // \\rput(0,0){\\parbox{21pt}{\\begin{center}\\end{center}}}",
    private static final String STR_PSTRICKS_BEGIN_FIGURE = "% this code (saved as a tex file) can be included by a tex document\n\n"
            + "\\begin{figure}[ht]\n" + "\\begin{singlespacing}\n" + "\\centering\n";

    private static final String STR_PSTRICKS_END_FIGURE = "}\n" + "\\caption[Your caption goes here.]\n" + "{\n"
            + " Your caption and description go here.\n" + "}\n" + "\\label{fig:your_label_goes_here}\n"
            + "\\end{singlespacing}\n" + "\\end{figure}\n\n";

    private static final String STR_EPS_BEGIN_DOC = "% this code can be compiled into and EPS file\n\n"
            + "\\documentclass[12pt]{article}\n" + "\\usepackage{pstricks}\n" + "\\usepackage{graphics}\n"
            + "\\pagestyle{empty}\n";

    private static final String STR_EPS_END_DOC = "\\end{document}" + "\n\n";

    // Commented document declaration for EPS export - taken from
    // IDES LatexPrinter
    private static final String STR_EPS_COMMENTED_DOC_DECLARATION = "% Sample document declaration that will include any eps file\n\n"
            + "% \\documentclass[12pt]{article}\n" + "% \\usepackage{graphicx}\n" + "% \\begin{document}\n"
            + "%   \\includegraphics[scale=1]{yourfilename.eps}\n" + "% \\end{document}\n";

    // Commented document declaration for PSTRICKS export - taken from
    // IDES LatexPrinter
    private static final String STR_PSTRICKS_COMMENTED_DOC_DECLARATION = "% Sample document declarationt that will include a PSTRICKS tex figure\n\n"
            + "% \\documentclass[letterpaper,12pt]{report}\n" + "% \\usepackage{graphics}\n"
            + "% \\usepackage{setspace}\n" + "% \\usepackage{pstricks}\n" + "% \\begin{document}\n"
            + "% \\include{your_tex_file_name_WITHOUT_TEX_EXTENSION}\n" + "% \\end{document}";

    // /////////////////////////////////////////////////////////////////
    // Static Methods
    // /////////////////////////////////////////////////////////////////

    /**
     * Creates a LaTeX document which contains a PSTricks figure of a model
     * 
     * @param graphModel the graph of the model
     * @param ps         the stream where the document has to be output
     */
    public static void createPSTricksFileContents(FSAGraph graphModel, PrintStream ps) {
        if (!(graphModel instanceof FSAGraph)) {
            return;
        }

        ps.print(STR_PSTRICKS_BEGIN_FIGURE);
        Rectangle exportBounds = null;
        int border = 0;
        double scale = 1;
        boolean useFrame = Hub.getPersistentData().getBoolean(STR_EXPORT_PROP_USE_FRAME);

        // Step #3 - Figure out the dimensions
        exportBounds = graphModel.getBounds(false);

        // Step #3.5 - Add a border to the export bounds
        /*
         * exportBounds.width -= exportBounds.x; exportBounds.height -= exportBounds.y;
         */
        border = (exportBounds.x > INT_PSTRICKS_BORDER_SIZE) ? INT_PSTRICKS_BORDER_SIZE : exportBounds.x;
        exportBounds.x -= border;
        exportBounds.width += (border * 2);

        border = (exportBounds.y > INT_PSTRICKS_BORDER_SIZE) ? INT_PSTRICKS_BORDER_SIZE : exportBounds.y;
        exportBounds.y -= border;
        exportBounds.height += (border * 2);

        // Step #3.75 - Figure out the scale factor
        if (exportBounds.width > DBL_PSTRICKS_MAX_WIDTH) {
            scale = DBL_PSTRICKS_MAX_WIDTH / exportBounds.width;
        }
        if (exportBounds.height > DBL_PSTRICKS_MAX_HEIGHT) {
            scale = (scale > (DBL_PSTRICKS_MAX_HEIGHT / exportBounds.height))
                    ? DBL_PSTRICKS_MAX_HEIGHT / exportBounds.height
                    : scale;
        }
        scale = BentoBox.roundDouble(scale, 2);

        // Step #4 - Begin with the basic scaling, picture boundary
        // and frame information
        ps.print("\\scalebox{" + scale + "}\n" + "{\n" + "\\psset{unit=1pt}\n");

        // Step #5 - Get the PSTricks figure
        ps.print(createPSPicture(graphModel, exportBounds, useFrame));

        // Step #6 - End the picture and add the commented LaTeX
        // document declaration
        ps.print(STR_PSTRICKS_END_FIGURE + STR_PSTRICKS_COMMENTED_DOC_DECLARATION);
    }

    /**
     * Generate PSTricks description of a model
     * 
     * @param graphModel   the graph of the model
     * @param exportBounds the boundaries of the figure
     * @param useFrame     whether a frame should be added around the figure
     * @return PSTricks code describing the current model
     */
    public static String createPSPicture(FSAGraph graphModel, Rectangle exportBounds, boolean useFrame) {
        CircleNode[] nodeArray = null;
        Edge[] edgeArray = null;
        GraphLabel[] freelabelArray = null;

        // Step #1 - Get the Nodes, Edges and Labels
        nodeArray = graphModel.getNodes().toArray(new CircleNode[0]);
        edgeArray = graphModel.getEdges().toArray(new Edge[0]);
        freelabelArray = graphModel.getFreeLabels().toArray(new GraphLabel[0]);

        // Step #2 - Initialize the pspicture environment and set linewidth to 1
        // pt
        String contentsString = "\\begin{pspicture}(0,0)(" + exportBounds.width + "," + exportBounds.height + ")\n"
                + "  \\psset{linewidth=1pt}\n";

        // Step #3 - Create frame if needed
        if (useFrame) {
            contentsString += "  \\psframe(0,0)(" + exportBounds.width + "," + exportBounds.height + ")\n\n";
        }

        // Step #4 - Add the node information
        for (int i = 0; i < nodeArray.length; i++) {
            contentsString += nodeArray[i].createExportString(exportBounds, INT_EXPORT_TYPE_PSTRICKS);
        }
        contentsString += "\n";

        // Step #5 - Add the edge data
        for (int i = 0; i < edgeArray.length; i++) {
            contentsString += edgeArray[i].createExportString(exportBounds, INT_EXPORT_TYPE_PSTRICKS);
        }
        contentsString += "\n";

        // Step #6 - Add the label data
        for (int i = 0; i < freelabelArray.length; i++) {
            contentsString += freelabelArray[i].createExportString(exportBounds, INT_EXPORT_TYPE_PSTRICKS);
        }
        contentsString += "\n";

        // Step #7 - Close the pspicture environment
        contentsString += "\\end{pspicture}\n";

        return contentsString;
    }

    /**
     * Creates the LaTeX document which will be rendered into an EPS
     * 
     * @return LaTeX document containing a PSTricks description of the current model
     */
    public static String createEPSFileContents(FSAGraph graphModel) {
        String contentsString = STR_EPS_BEGIN_DOC;

        Rectangle exportBounds = null;
        int border = 0;
        boolean useFrame = Hub.getPersistentData().getBoolean(STR_EXPORT_PROP_USE_FRAME);

        // Step #1 - Get the GraphModel
        if (!(graphModel instanceof FSAGraph)) {
            return null;
        }

        // Step #3 - Figure out the dimensions
        exportBounds = graphModel.getBounds(false);

        // Step #3.5 - Add a border to the export bounds
        /*
         * exportBounds.width -= exportBounds.x; exportBounds.height -= exportBounds.y;
         */
        border = (exportBounds.x > INT_PSTRICKS_BORDER_SIZE) ? INT_PSTRICKS_BORDER_SIZE : exportBounds.x;
        exportBounds.x -= border;
        exportBounds.width += (border * 2);

        border = (exportBounds.y > INT_PSTRICKS_BORDER_SIZE) ? INT_PSTRICKS_BORDER_SIZE : exportBounds.y;
        exportBounds.y -= border;
        exportBounds.height += (border * 2);

        double scale = 1;

        // Step #3.75 - Figure out the scale factor
        if (exportBounds.width > DBL_PSTRICKS_MAX_WIDTH) {
            scale = DBL_PSTRICKS_MAX_WIDTH / exportBounds.width;
        }
        if (exportBounds.height > DBL_PSTRICKS_MAX_HEIGHT) {
            scale = (scale > (DBL_PSTRICKS_MAX_HEIGHT / exportBounds.height))
                    ? DBL_PSTRICKS_MAX_HEIGHT / exportBounds.height
                    : scale;
        }
        scale = BentoBox.roundDouble(scale, 2);

        // Step #4 - Begin with the basic scaling, picture boundary
        // and frame information
        // psset unit is 0.75 (72/96) to account for the shift in DPI (96 in
        // IDES to 72). See ides.api.latex.Renderer
        contentsString += "\\begin{document}\n" + "\\scalebox{" + scale + "}\n" + "{\n" + "\\psset{unit=0.75pt}\n";

        // Step #5 - Get the PSTricks figure
        contentsString += createPSPicture(graphModel, exportBounds, useFrame);

        contentsString += "}\n";
        // Step #6 - End the picture and add the commented LaTeX
        // document declaration
        contentsString += STR_EPS_END_DOC + STR_EPS_COMMENTED_DOC_DECLARATION;

        return contentsString;
    }

    /**
     * This method is reponsible for calculcating the size of the bounding box
     * necessary for the entire graph. It goes through every node, edge and label
     * and uss the min and max x and y values in these to create the box.
     * 
     * @return ExportBounds The bounding box for the graph
     * @author Sarah-Jane Whittaker
     */
    /*
     * private static ExportBounds determineExportBounds(Node[] nodeArray, Edge[]
     * edgeArray, GraphLabel labelArray[]) { ExportBounds exportBounds = new
     * ExportBounds(); NodeLayout nodeLayout = null; Point2D.Float location = null;
     * Rectangle initialArrowBounds = null; Rectangle labelBounds = null; int i = 0;
     * FSAState nodeState = null; Float tempRadius = null; double tempX = 0; double
     * tempY = 0; int radius = 0; int nodeX = 0; int nodeY = 0; // Start with the
     * nodes for (i = 0; i < nodeArray.length; i++) { nodeLayout =
     * nodeArray[i].getLayout(); location = nodeLayout.getLocation(); nodeState =
     * nodeArray[i].getState(); tempRadius = new Float(nodeLayout.getRadius());
     * radius = tempRadius.intValue(); tempX = location.getX(); tempY =
     * location.getY(); // If the node is initial, take into account the initial //
     * arrow if (nodeState.isInitial()) { initialArrowBounds =
     * nodeArray[i].getInitialArrowBounds(); tempX = (initialArrowBounds.getMinX() <
     * tempX) ? initialArrowBounds.getMinX() : tempX; tempY =
     * (initialArrowBounds.getMinY() < tempY) ? initialArrowBounds.getMinY() :
     * tempY; } nodeX = (int) tempX; nodeY = (int) tempY; // Node location is at the
     * centre of the circle exportBounds.checkXOffset(nodeX - radius);
     * exportBounds.checkYOffset(nodeY - radius); exportBounds.checkWidth(nodeX +
     * radius); exportBounds.checkHeight(nodeY + radius); } for (i = 0; i <
     * edgeArray.length; i++) {
     * exportBounds.checkRectangle(edgeArray[i].getCurveBounds()); // TODO: Get
     * better edge label bounds!!!
     * exportBounds.checkRectangle(edgeArray[i].getLabel().bounds()); } for (i = 0;
     * i < labelArray.length; i++) { // TODO: Get better edge label bounds!!!
     * labelBounds = labelArray[i].bounds();
     * exportBounds.checkRectangle(labelBounds); } exportBounds.width -=
     * exportBounds.x; exportBounds.height -= exportBounds.y;
     * exportBounds.addBorder(INT_PSTRICKS_BORDER_SIZE); return exportBounds; }
     */

}