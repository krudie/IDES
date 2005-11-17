/*
 * Created on Sep 24, 2004
 */
package userinterface.graphcontrol;

import userinterface.general.Ascii;

/**
 * This object is used to gather data for export to latex.  It is passed around to each element of the
 * GraphModel, and each delivers data to it regarding their respective representations.  After this process
 * the object can be called upon to generate latex code describing the given graph.
 * 
 * @author Michael Wood
 */
public class LatexPrinter 
{
    /**
     * Various definition strings.
     */
	private String nodes = "",
	   	   		   curves = "",
				   arrows = "",
				   texts = "",
				   vectors = "";

	/**
     * Example for how to include the eps output in a latex document.
     */	
	private static final String epsExample = "% example document that will include any eps file\n\n" +
	    									 "% \\documentclass[12pt]{article}\n" +
											 "% \\usepackage{graphicx}\n" +
											 "% \\begin{document}\n" +
											 "%   \\includegraphics[scale=1]{yourfilename.eps}\n" +
											 "% \\end{document}\n";
	    		
	/**
     * Example for how to include the tex output in a latex document.
     */	
	private static final String texPstricksExample = "% example document that will include a PSTRICKS tex figure\n\n" +
											 "% \\documentclass[letterpaper,12pt]{report}\n" +
											 "% \\usepackage{setspace}\n" +
											 "% \\usepackage{pstricks}\n" +
											 "% \\begin{document}\n" +
											 "% \\psset{unit=1pt}\n" +
											 "% \\include{your_tex_file_name_WITHOUT_TEX_EXTENSION}\n" +
											 "% \\end{document}";

	/**
     * Example for how to include the tex output in a latex document.
     */	
	private static final String texPict2eExample = "% example document that will include a PICT2E tex figure\n\n" +
											 "% \\documentclass[letterpaper,12pt]{report}\n" +
											 "% \\usepackage{setspace}\n" +
											 "% \\usepackage{pict2e}\n" +
											 "% \\begin{document}\n" +
											 "% \\include{your_tex_file_name_WITHOUT_TEX_EXTENSION}\n" +
											 "% \\end{document}";
	
	/**
     * Constructor
     */	
	public LatexPrinter() {}
	
    /**
     * Output the gathered information in the pict2e environment wrapped with a body for export to EPS.
     * 
     * @param	w			The width of the bounding box.
     * @param	h			The height ofthe bounding box.
     * @param	draw_border	Whether or not the bounding box should be drawn.
     * @return	The formatted output.
     */	
	public String outputPict2eForEPS(int w, int h, boolean draw_border)
	{
		String output = "" +
		"% this code can be compiled into and EPS file\n\n" +
		"\\documentclass[12pt]{article}\n" +	
		"\\usepackage{pict2e}\n" +	
		"\\pagestyle{empty}\n" +
		"\\special{papersize=" + (w + 10) + "pt," + (h + 10) + "pt}\n" +
		"\\usepackage[left=5pt,top=5pt,right=5pt,nohead,nofoot]{geometry}\n" +
		"\\setlength{\\parindent}{0pt}\n" +
		"\\begin{document}\n" +	
		outputPict2e(w,h,draw_border) +
		"\\end{document}" +
		"\n\n" + epsExample;	
		return output;
	}
	
    /**
     * Output the gathered information in the pict2e environment wrapped with a body for inclusion as a TEX figure.
     * 
     * @param	w			The width of the bounding box.
     * @param	h			The height ofthe bounding box.
     * @param	draw_border	Whether or not the bounding box should be drawn.
     * @return	The formatted output.
     */	
	public String outputPict2eForTEX(int w, int h, boolean draw_border)
	{
		String output = "" +
		"% this code (saved as a tex file) can be included by a tex document\n\n" +
		"\\begin{figure}[ht]\n" +
		"\\begin{singlespacing}\n" +
		"\\centering\n" +	
		outputPict2e(w,h,draw_border) +
		"\\caption[Your caption goes here.]\n" +
		"{\n" +
		" Your caption and description go here.\n" +
		"}\n" +
		"\\label{fig:your_label_goes_here}\n" +
		"\\end{singlespacing}\n" +
		"\\end{figure}" +
		"\n\n" + texPict2eExample +	
		"\n\n" + epsExample;	
		return output;
	}
	
    /**
     * Output the gathered information in the pict2e environment.
     * 
     * @param	w			The width of the bounding box.
     * @param	h			The height ofthe bounding box.
     * @param	draw_border	Whether or not the bounding box should be drawn.
     * @return	The formatted output.
     */	
	private String outputPict2e(int w, int h, boolean draw_border)
	{
		String output = "" +
		"\\begin{picture}(" + w + "," + h + ")\n" +	
		"  %\\linethickness{2pt} % use this if just using the picture inline\n" +
		"  \\linethickness{1pt} % use this if compiling to eps" +
		arrows + "\n" + 
		"  %\\linethickness{1pt} % use this if used 2pt above\n";
		if (draw_border) 
		{
			output = output +
		"  \\put(0,0){\\line(1,0){" + w + "}}\n" +	
		"  \\put(0," + h + "){\\line(1,0){" + w + "}}\n" +	
		"  \\put(0,0){\\line(0,1){" + h + "}}\n" +	
		"  \\put(" + w + ",0){\\line(0,1){" + h + "}}";
		}
		output = output +
		vectors +
		nodes + 	
		curves + 	
		texts + "\n" +	
		"\\end{picture}\n";	
		return output;
	}

    /**
     * Output the gathered information in the pstricks environment wrapped with a body for export to EPS.
     * 
     * @param	w			The width of the bounding box.
     * @param	h			The height ofthe bounding box.
     * @param	draw_border	Whether or not the bounding box should be drawn.
     * @return	The formatted output.
     */	
	public String outputPstricksForEPS(int w, int h, boolean draw_border)
	{
		String output = "" +
						"% this code can be compiled into and EPS file\n\n" +
						"\\documentclass[12pt]{article}\n" +	
						"\\usepackage{pstricks}\n" +	
						"\\pagestyle{empty}\n" +
						"\\special{papersize=" + (w + 10) + "pt," + (h + 10) + "pt}\n" +
						"\\usepackage[left=5pt,top=5pt,right=5pt,nohead,nofoot]{geometry}\n" +
						"\\setlength{\\parindent}{0pt}\n" +
						"\\begin{document}\n" +	
						"\\psset{unit=1pt}\n" +	
						outputPstricks(w,h,draw_border) +
						"\\end{document}" +
						"\n\n" + epsExample;	
		return output;
	}
	
    /**
     * Output the gathered information in the pstricks environment wrapped with a body for export to TEX.
     * 
     * @param	w			The width of the bounding box.
     * @param	h			The height ofthe bounding box.
     * @param	draw_border	Whether or not the bounding box should be drawn.
     * @return	The formatted output.
     */	
	public String outputPstricksForTEX(int w, int h, boolean draw_border)
	{
		String output = "" +
		"% this code (saved as a tex file) can be included by a tex document\n\n" +
		"\\begin{figure}[ht]\n" +
		"\\begin{singlespacing}\n" +
		"\\centering\n" +	
		outputPstricks(w,h,draw_border) +
		"\\caption[Your caption goes here.]\n" +
		"{\n" +
		" Your caption and description go here.\n" +
		"}\n" +
		"\\label{fig:your_label_goes_here}\n" +
		"\\end{singlespacing}\n" +
		"\\end{figure}" +
		"\n\n" + texPstricksExample +	
		"\n\n" + epsExample;	
		return output;
	}
	
    /**
     * Output the gathered information in the pstricks environment
     * 
     * @param	w			The width of the bounding box.
     * @param	h			The height ofthe bounding box.
     * @param	draw_border	Whether or not the bounding box should be drawn.
     * @return	The formatted output.
     */	
	private String outputPstricks(int w, int h, boolean draw_border)
	{
		String output = "" +
						"\\begin{pspicture}(0,0)(" + w + "," + h + ")\n" +	
						"  \\psset{linewidth=1pt}\n";
		if (draw_border) { output = output + "  \\psframe(0,0)(" + w + "," + h + ")"; }
		output = output +
						nodes + 	
						arrows + 							
						curves + 	
						texts + "\n" +	
						"\\end{pspicture}\n";	
		return output;
	}
	
    /**
     * Add a node to the output collection.
     * 
     * @param	node	The node to be added.
     */	
	public void addNode(String node) { nodes = nodes + "\n" + node; }

    /**
     * Add a curve to the output collection.
     * This is for use with pict2e. 
     * Solves a non-drawing bug by inserting ~ spaces.
     * 
     * @param	curve	The curve to be added.
     */	
	public void addEscapedCurve(String curve) { curves = curves + "\n" + "  \\put(0,0){~}\n" + curve; }

    /**
     * Add a curve to the output collection.
     * 
     * @param	curve	The curve to be added.
     */	
	public void addCurve(String curve) { curves = curves + "\n" + curve; }

	/**
     * Add an arrow to the output collection.
     * 
     * @param	arrow	The arrow to be added.
     */	
	public void addArrow(String arrow) { arrows = arrows + "\n" + arrow; }	

	/**
     * Add a vector to the output collection.
     * 
     * @param	vector	The vector to be added.
     */	
	public void addVector(String vector) { vectors = vectors + "\n" + vector; }		
	
    /**
     * Add some LaTeX code to the output collection.
     * 
     * @param	text	The text to be added.
     */	
	public void addLaTeX(String text) { texts = texts + "\n" + text; }	
		
	public static String escapeLaTeX(String s)
	{
		String r = ""+s;
		r = Ascii.replaceAll(r,"\\","\\textbackslash");
		r = Ascii.replaceAll(r,"}","\\}");
		r = Ascii.replaceAll(r,"{","\\{");
		r = Ascii.replaceAll(r,"\"","''");
		r = Ascii.replaceAll(r,"#","\\#");
		r = Ascii.replaceAll(r,"$","\\$");
		r = Ascii.replaceAll(r,"%","\\%");
		r = Ascii.replaceAll(r,"&","\\&");
		r = Ascii.replaceAll(r,"<","\\textless");
		r = Ascii.replaceAll(r,">","\\textgreater");
		r = Ascii.replaceAll(r,"^","\\^{}");
		r = Ascii.replaceAll(r,"_","\\_");
		r = Ascii.replaceAll(r,"|","\\textbar");
		r = Ascii.replaceAll(r,"~","\\~{}");
		return r;
	}
}
