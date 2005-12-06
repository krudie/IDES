/*
 * Created on Nov 2, 2004
 */
package com.aggressivesoftware.ides;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import com.aggressivesoftware.geometric.Point;

/**
 * This class provides a simple read/write interface to a plain text file.
 * It is used by the GraphingPlatform to read/write system variables at open/close of the application.
 * It functions as an os independent registry.
 * 
 * @author Michael Wood
 */
public class SystemVariables 
{
	/**
     * The name of the simple text file where the system variables will be recorded
     */
	private static final String settings_file_name = "settings.txt"; // in the application path
	
	/**
     * Paths to thrid party latex application.
     */
	public static final String DEFAULT_TEX_PATH = "C:\\texmf\\miktex\\bin",
								DEFAULT_PS_PATH = "C:\\gs\\gs8.11\\bin";

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// system variables ///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
	
	/**
     * The physical location of the main JAR
     */
	public String application_path = "";

	/**
     * The physical location used for system settings files and undo/redo files, etc.
     */
	public String system_path = "";

	/**
     * The physical location used for export to latex files
     */
	public String tex_path = "";
	
	/**
     * The last used physical location for save/load
     */
	public String last_used_path = "";

	/**
     * Records the last "snap to grid" value.
     */
	public int grid = 20;

	/**
     * Records the last "show_all_edges" state.
     */
	public boolean show_all_edges = false;

	/**
     * Records the last "show_all_labels" state.
     */
	public boolean show_all_labels = false;
	
	/**
     * Records whether or not crashing errors should automatically open a webpage to report their details.
     */
	public boolean use_error_reporting = false;

	/**
     * Records whether or not labels should be renderd latex or just plain text
     */
	public boolean use_latex_labels = false;

	/**
     * Records whether or not export to latex should automatically create an eps with the generated code.
     */
	public boolean export_latex_to_eps = false;

	/**
     * Records whether or not export to latex should automatically create an tex with the generated code.
     */
	public boolean export_latex_to_tex = true;
	
	/**
     * Optionally draw a border around export output.
     */
	public boolean export_with_border = false;
		
	/**
     * Force all nodes to the largest used radius
     */
	public boolean use_standard_node_size = false;

	/**
     * Use pstricks as the export to latex format (versus pict2e)
     */
	public boolean use_pstricks = true;

	/**
     * The size for the node text eding window
     */
	public Point floating_text_size = new Point(200,100);
	
	/**
     * Path to a latex rendering tool.
     */
	public String path_to_tex = DEFAULT_TEX_PATH;
	
	/**
     * Path to a post script handeling tool.
     */
	public String path_to_ps = DEFAULT_PS_PATH;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// SystemVariables construction ///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

    /**
     * Construct the SystemVariables.
     */
	public SystemVariables()
	{
		// calcualte the application path				
		String class_paths = System.getProperty("java.class.path");	
		if (class_paths.indexOf(System.getProperty("path.separator")) > 0) { class_paths = class_paths.substring(0,class_paths.indexOf(System.getProperty("path.separator"))); }				
		if (class_paths.endsWith(".jar")) { class_paths = class_paths.substring(0,class_paths.lastIndexOf(File.separator)); }
		if (class_paths.length()>0 && !class_paths.endsWith(File.separator)) { class_paths = class_paths + File.separator; }
		application_path = class_paths;
		
		// calcualte and create if necessary the system and tex paths.
		system_path = application_path + "system" + File.separator;
		tex_path = application_path + "tex" + File.separator;
		(new File(system_path)).mkdir();
		(new File(tex_path)).mkdir();
		
		fetchValues();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// miscelaneous ///////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

	private void fetchValues()
	{
		try
		{ 
		    String this_token = null,
		       	   next_token = null;
	    	BufferedReader in = new BufferedReader(new FileReader(system_path + settings_file_name)); 
		    String this_line = in.readLine();
		    while (this_line != null) 
		    {
		    	StringTokenizer st = new StringTokenizer(this_line,"=");
	        	if (st.hasMoreTokens())
		        {
		        	this_token = st.nextToken().trim();
		        	if (st.hasMoreTokens())
		        	{
		        		next_token = st.nextToken().trim();
			                 if(this_token.equals("grid"))                   { grid        		      = Integer.parseInt(next_token); }
			            else if(this_token.equals("last_save_path"))         { last_used_path         = next_token; }
			            else if(this_token.equals("show_all_edges"))         { show_all_edges         = (next_token.equals("true")); }
			            else if(this_token.equals("show_all_labels"))        { show_all_labels        = (next_token.equals("true")); }
			            //else if(this_token.equals("use_error_reporting"))    { use_error_reporting    = (next_token.equals("true")); }
			            else if(this_token.equals("use_latex_labels"))       { use_latex_labels       = (next_token.equals("true")); }
			            else if(this_token.equals("export_latex_to_eps"))    { export_latex_to_eps    = (next_token.equals("true")); }
			            else if(this_token.equals("export_latex_to_tex"))    { export_latex_to_tex    = (next_token.equals("true")); }
			            else if(this_token.equals("export_with_border"))     { export_with_border     = (next_token.equals("true")); }
			            else if(this_token.equals("use_standard_node_size")) { use_standard_node_size = (next_token.equals("true")); }
			            else if(this_token.equals("use_pstricks")) 			 { use_pstricks			  = (next_token.equals("true")); }
			            else if(this_token.equals("floating_text_size"))     { floating_text_size     = new Point(next_token); }
			            else if(this_token.equals("path_to_tex"))            { path_to_tex            = next_token; }
			            else if(this_token.equals("path_to_ps"))             { path_to_ps             = next_token; }
		        	}
		        }
		    	this_line = in.readLine();
		    }
			in.close();
	    }
	    catch (Exception e)	{ }
	}
	
	public void saveValues()
	{
	    try 
		{ 
	    	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(system_path + settings_file_name))); 
	    	
	    	out.println("grid="                   + grid);
	        out.println("last_save_path="         + last_used_path);
	        out.println("show_all_edges="         + show_all_edges);
	        out.println("show_all_labels="        + show_all_labels);
	        out.println("use_error_reporting="    + use_error_reporting);
	        out.println("use_latex_labels="       + use_latex_labels);
	        out.println("export_latex_to_eps="    + export_latex_to_eps);
	        out.println("export_latex_to_tex="    + export_latex_to_tex);
	        out.println("export_with_border="     + export_with_border);
	        out.println("use_standard_node_size=" + use_standard_node_size);
	        out.println("use_pstricks=" 		  + use_pstricks);
	    	out.println("floating_text_size="     + floating_text_size);
	        out.println("path_to_tex="            + path_to_tex);
	        out.println("path_to_ps="             + path_to_ps);
		    out.close();
		}
	    catch (Exception e)	{ e.printStackTrace(); }
	}
}