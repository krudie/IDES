package presentation.fsa;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.io.File;
import java.util.Collection;

import main.IDESWorkspace;
import model.fsa.FSAState;
import presentation.GraphicalLayout;


/**
 * This class is charged with exporting the current graph (or a 
 * selection of that graph) to one of four formats: LaTex-PSTricks, 
 * EPS, GIF and PNG.  All of the methods are static; there is no
 * need to instantiate to perform these functions.
 * 
 * @author Sarah-Jane Whittaker
 **/
public class GraphExporter 
{
	///////////////////////////////////////////////////////////////////
	// Internal Classes
	///////////////////////////////////////////////////////////////////
	/**
	 * This class is used to store boundary and offset information 
	 * about a graph.  It can also "test" its own values against any
	 * given and adjust itself to include new points.
	 */
	public static class ExportBounds extends Rectangle
	{
		public ExportBounds()
		{
			x = Integer.MAX_VALUE;
			y = Integer.MAX_VALUE;
			width = 0;
			height = 0;
		}
		
		public void checkWidth(int testWidth)
		{	width = (width > testWidth) ? width : testWidth;	}

		public void checkHeight(int testHeight)
		{	height = (height > testHeight) ? height : testHeight;	}

		public void checkXOffset(int testX)
		{	x = (x < testX) ? x : testX;	}
		
		public void checkYOffset(int testY)
		{	y = (y < testY) ? y : testY;	}
		
		public void checkRectangle(RectangularShape rectangle)
		{
			checkXOffset((int) rectangle.getMinX());
			checkYOffset((int) rectangle.getMinY());
			checkWidth((int) rectangle.getWidth());
			checkHeight((int) rectangle.getHeight());	
		}
		
		public void addBorder(int border)
		{
			width += (border * 2);
			height += (border * 2);
			x -= border;
			y -= border;
		}
		
		public String toString()
		{
			return "ExportBounds\nWidth: " + width
				+ "\n\tHeight: " + height
				+ "\n\tX Offset: " + x
				+ "\n\tY Offset: " + y;
		}
	}
	
	
	///////////////////////////////////////////////////////////////////
	// Static Variables
	///////////////////////////////////////////////////////////////////
	/** Export Types **/
	private static final int INT_EXPORT_TYPE_START = 1;
	public static final int INT_EXPORT_TYPE_PSTRICKS = INT_EXPORT_TYPE_START;
	public static final int INT_EXPORT_TYPE_EPS = INT_EXPORT_TYPE_PSTRICKS + 1;
	
	/** PSTricks Stuff **/
	public static final int INT_PSTRICKS_MARKED_STATE_RADIUS_DIFF = 4;
	private static final int INT_PSTRICKS_BORDER_SIZE = 25;
	
	// NOTE: Mike has this string in his LatexPrinter in IDES, but I 
	// don't think it does anything, so it's ignored here.
	// private static String STR_TEXT_ADDON = "\n  \\rput(0,0){\\parbox{21pt}{\\begin{center}\\end{center}}}",
	private static final String STR_PSTRICKS_BEGIN_FIGURE = 
		"% this code (saved as a tex file) can be included by a tex document\n\n" +
		"\\begin{figure}[ht]\n" +
		"\\begin{singlespacing}\n" +
		"\\centering\n";	
	
	private static final String STR_PSTRICKS_END_FIGURE = 
		"\\end{pspicture}\n" +
		"\\caption[Your caption goes here.]\n" +
		"{\n" +
		" Your caption and description go here.\n" +
		"}\n" +
		"\\label{fig:your_label_goes_here}\n" +
		"\\end{singlespacing}\n" +
		"\\end{figure}\n\n";
	
	private static final String STR_EPS_BEGIN_DOC = 
		"% this code can be compiled into and EPS file\n\n" +
		"\\documentclass[12pt]{article}\n" +	
		"\\usepackage{pstricks}\n" +	
		"\\pagestyle{empty}\n";
	
	private static final String STR_EPS_END_DOC = 
		// TODO: Put this in!!!
		// "\\special{papersize=" + (w + 10) + "pt," + (h + 10) + "pt}\n" +
		"\\usepackage[left=5pt,top=5pt,right=5pt,nohead,nofoot]{geometry}\n" +
		"\\setlength{\\parindent}{0pt}\n" +
		"\\begin{document}\n" +	
		"\\psset{unit=1pt}\n" +
		"\\end{document}";
	
	// Commented document declaration for EPS export - taken from
	// IDES LatexPrinter
	private static final String STR_EPS_COMMENTED_DOC_DECLARATION = 
		"% Sample document declaration that will include any eps file\n\n" +
	    "% \\documentclass[12pt]{article}\n" +
		"% \\usepackage{graphicx}\n" +
		"% \\begin{document}\n" +
		"%   \\includegraphics[scale=1]{yourfilename.eps}\n" +
		"% \\end{document}\n";
	    		
	// Commented document declaration for PSTRICKS export - taken from
	// IDES LatexPrinter
	private static final String STR_PSTRICKS_COMMENTED_DOC_DECLARATION = 
		"% Sample document declarationt that will include a PSTRICKS tex figure\n\n" +
		"% \\documentclass[letterpaper,12pt]{report}\n" +
		"% \\usepackage{setspace}\n" +
		"% \\usepackage{pstricks}\n" +
		"% \\begin{document}\n" +
		"% \\psset{unit=1pt}\n" +
		"% \\include{your_tex_file_name_WITHOUT_TEX_EXTENSION}\n" +
		"% \\end{document}";
	
	
	///////////////////////////////////////////////////////////////////
	// Static Methods
	///////////////////////////////////////////////////////////////////
    
	/**
     * TODO: Comment this nicely.
     * 
     */	
	public static String createPSTricksFileContents()
	{
		String contentsString = STR_PSTRICKS_BEGIN_FIGURE;
		IDESWorkspace workspace = null;
		GraphModel graphModel = null;
		
		Collection<Node> nodeCollection = null;
		Node[] nodeArray = null;
		Collection<Edge> edgeCollection = null;
		Edge[] edgeArray = null;
		Collection<GraphLabel> labelCollection = null;
		GraphLabel[] labelArray = null;

		ExportBounds exportBounds = null;

		// Step #1 - Get the GraphModel
		workspace = IDESWorkspace.instance();
		graphModel = workspace.getActiveGraphModel();
		if (graphModel == null)
		{
			System.out.println("ERROR: No graph model for ExportToLatexCommand.performSave!!!");
			return null;
		}

		// Step #2 - Get the Nodes, Edges and Labels
		nodeCollection = graphModel.getNodes();
		nodeArray = (Node[]) nodeCollection.toArray(new Node[0]);
		edgeCollection = graphModel.getEdges();
		edgeArray = (Edge[]) edgeCollection.toArray(new Edge[0]);
		labelCollection = graphModel.getLabels();
		labelArray = (GraphLabel[]) labelCollection.toArray(new GraphLabel[0]);

		// Step #3 - Figure out the dimensions
		// If there's a selection box, then use that, otherwise make 
		// a box that holds eveything
		// if (there is a selection)
		// {
		//		exportBounds = selection;
		// }		
		// else
		// {
		exportBounds = determineExportBounds(nodeArray, edgeArray, labelArray);
		// }
		
		// Step #4 - Begin with the basic picture boundary and frame
		// information
		contentsString += "\\begin{pspicture}(0,0)(" 
			+ exportBounds.width + "," 
			+ exportBounds.height + ")\n" 
			+	"  \\psset{linewidth=1pt}\n"
			+ "  \\psframe(0,0)(" 
			+ exportBounds.width + "," 
			+ exportBounds.height + ")\n\n";
		
		// Step #5 - Add the node information 
		for (int i = 0; i < nodeArray.length; i++)
		{
			contentsString += nodeArray[i].createExportString(exportBounds,
				INT_EXPORT_TYPE_PSTRICKS);
		}
		contentsString += "\n";
		
		// Step #6 - Add the edge data 
		for (int i = 0; i < edgeArray.length; i++)
		{
			contentsString += edgeArray[i].createExportString(exportBounds,
				INT_EXPORT_TYPE_PSTRICKS);
		}		
		contentsString += "\n";
		
		// Step #7 - Add the label data 
		for (int i = 0; i < labelArray.length; i++)
		{
			contentsString += labelArray[i].createExportString(exportBounds,
				INT_EXPORT_TYPE_PSTRICKS);
		}		
		contentsString += "\n";

		// Step #8 - End the pciture and add the commented LaTeX
		// document declaration
		contentsString += STR_PSTRICKS_END_FIGURE
			+ STR_PSTRICKS_COMMENTED_DOC_DECLARATION;

		System.out.println(contentsString);		
		return contentsString;
	}


	/**
	 * This method is reponsible for calculcating the size of the 
	 * bounding box necessary for the entire graph.  It goes
	 * through every node, edge and label and uss the min and
	 * max x and y values in these to create the box.
	 * 
	 * @return ExportBounds The bounding box for the graph
	 * 
	 * @author Sarah-Jane Whittaker
	 */
	private static ExportBounds determineExportBounds(Node[] nodeArray, 
			Edge[] edgeArray, GraphLabel labelArray[])
	{
		ExportBounds exportBounds = new ExportBounds();
		NodeLayout nodeLayout = null;
		Point2D.Float location = null;
		Rectangle initialArrowBounds = null;
		Rectangle labelBounds = null;
		int i = 0;
		
		FSAState nodeState = null;
		Float tempRadius = null;
		double tempX = 0;
		double tempY = 0;
		int radius = 0;
		int nodeX = 0;
		int nodeY = 0;
		
		// Start with the nodes
		for (i = 0; i < nodeArray.length; i++)
		{
			nodeLayout = nodeArray[i].getLayout();
			location = nodeLayout.getLocation();
			nodeState = nodeArray[i].getState();
			
			tempRadius = new Float(nodeLayout.getRadius());
			radius = tempRadius.intValue();
			tempX = location.getX();
			tempY = location.getY();
			
			// If the node is initial, take into account the initial
			// arrow
			if (nodeState.isInitial())
			{
				initialArrowBounds = nodeArray[i].getInitialArrowExportBounds();			
				tempX = (initialArrowBounds.getMinX() < tempX) ?
					initialArrowBounds.getMinX() : tempX;
				tempY = (initialArrowBounds.getMinY() < tempY) ? 
					initialArrowBounds.getMinY() : tempY;
			}
			
			nodeX = (int) tempX;
			nodeY = (int) tempY;
			
			// Node location is at the centre of the circle
			exportBounds.checkXOffset(nodeX - radius);
			exportBounds.checkYOffset(nodeY - radius);
			exportBounds.checkWidth(nodeX + radius);
			exportBounds.checkHeight(nodeY + radius);
		}

		for (i = 0; i < edgeArray.length; i++)
		{
			exportBounds.checkRectangle(edgeArray[i].getCurveBounds());
			// TODO: Get better edge label bounds!!!
			exportBounds.checkRectangle(edgeArray[i].getLabel().bounds());
		}

		for (i = 0; i < labelArray.length; i++)
		{
			// TODO: Get better edge label bounds!!!
			labelBounds = labelArray[i].bounds();
			
			exportBounds.checkRectangle(labelBounds);
		}
		
		exportBounds.width -= exportBounds.x;
		exportBounds.height -= exportBounds.y;
		exportBounds.addBorder(INT_PSTRICKS_BORDER_SIZE);
		
		return exportBounds;
	}

}