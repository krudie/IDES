/*
 * Created on Nov 2, 2004
 */
package main;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import ui.command.CommandHistory;

/**
 * This singleton class provides a simple read/write interface to a plain text file.
 * An OS independent registry.
 * 
 * @author Michael Wood
 * @author Helen Bretzke
 */
public class SystemVariables 
{
	protected static SystemVariables me = null;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// SystemVariables construction ///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

	public static SystemVariables instance() {
		if(me == null) {
		 me = new SystemVariables();
		}
		return me;
	}
	
    /**
     * Construct the SystemVariables.
     */
	private SystemVariables()	
	{
		// calcualte the application path				
		String class_path = System.getProperty("java.class.path");
		String paths[]=class_path.split(System.getProperty("path.separator"));
		class_path="";
		for(int i=0;i<paths.length;++i)
			if(paths[i].toLowerCase().endsWith("ides.jar"))
			{
				class_path=paths[i].substring(0,paths[i].length()-8);
				break;
			}
//		try{
//			if (class_paths.indexOf(System.getProperty("path.separator")) > 0) { class_paths = class_paths.substring(0,class_paths.indexOf(System.getProperty("path.separator"))); }				
//			if (class_paths.endsWith(".jar")) { class_paths = class_paths.substring(0,class_paths.lastIndexOf(File.separator)); }		
//			if (class_paths.length()>0 && !class_paths.endsWith(File.separator)) { class_paths = class_paths + File.separator; }
//		} catch(Exception e){			
//		}
		if(class_path.length()==0) class_path = System.getProperty("user.dir");
		if(!class_path.endsWith(File.separator))
			class_path+=File.separator;
		
		application_path = class_path;
//		System.out.println(application_path);
		
		// calculate and create if necessary the system and tex paths.
		system_path = application_path + "system" + File.separator;
		tex_path = application_path + "tex" + File.separator;
		(new File(system_path)).mkdir();
		(new File(tex_path)).mkdir();
		
		fetchValues();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// miscelaneous ///////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

	private  void fetchValues()
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
			            else if(this_token.equals("show_all_edges"))         { select_all_edges         = (next_token.equals("true")); }
			            else if(this_token.equals("select_all_labels"))        { select_all_labels        = (next_token.equals("true")); }
			            //else if(this_token.equals("use_error_reporting"))    { use_error_reporting    = (next_token.equals("true")); }
			            else if(this_token.equals("use_latex_labels"))       { use_latex_labels       = (next_token.equals("true")); }
			            else if(this_token.equals("export_latex_to_eps"))    { export_latex_to_eps    = (next_token.equals("true")); }
			            else if(this_token.equals("export_latex_to_tex"))    { export_latex_to_tex    = (next_token.equals("true")); }
			            else if(this_token.equals("export_with_border"))     { export_with_border     = (next_token.equals("true")); }
			            else if(this_token.equals("use_standard_node_size")) { use_standard_node_size = (next_token.equals("true")); }
			            else if(this_token.equals("use_pstricks")) 			 { use_pstricks			  = (next_token.equals("true")); }
			            else if(this_token.equals("floating_text_size"))     { floating_text_size     = parsePoint(next_token); }
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
	        out.println("select_all_edges="         + select_all_edges);
	        out.println("select_all_labels="        + select_all_labels);
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
	
	private  Point parsePoint(String coords) {
		Point p = new Point();
		try
		{
			int comma = coords.indexOf(',');
			p.setLocation(Integer.parseInt(coords.substring(1,comma)), 
					Integer.parseInt(coords.substring(comma+1,coords.length()-1)));
		}
		catch (Exception e)
		{
			p.setLocation(0,0);
		}	
		return p;
	}

	
	
	public  String getSettings_file_name() {
		return settings_file_name;
	}

	public String getApplication_path() {
		return application_path;
	}

	public boolean export_latex_to_eps() {
		return export_latex_to_eps;
	}

	public boolean export_latex_to_tex() {
		return export_latex_to_tex;
	}

	public boolean export_with_border() {
		return export_with_border;
	}

	public Point getFloating_text_size() {
		return floating_text_size;
	}

	public int getGrid() {
		return grid;
	}

	public String getLast_used_path() {
		return last_used_path;
	}

	public String getPath_to_ps() {
		return path_to_ps;
	}

	public String getPath_to_tex() {
		return path_to_tex;
	}

	public boolean isSelect_all_edges() {
		return select_all_edges;
	}

	public boolean select_all_labels() {
		return select_all_labels;
	}

	public String getSystem_path() {
		return system_path;
	}

	public String getTex_path() {
		return tex_path;
	}

	public boolean use_error_reporting() {
		return use_error_reporting;
	}

	public boolean use_latex_labels() {
		return use_latex_labels;
	}

	public boolean use_pstricks() {
		return use_pstricks;
	}

	public boolean use_standard_node_size() {
		return use_standard_node_size;
	}
	
	
	public void setApplication_path(String application_path) {
		me.application_path = application_path;
	}

	public void setExport_latex_to_eps(boolean export_latex_to_eps) {
		me.export_latex_to_eps = export_latex_to_eps;
	}

	public void setExport_latex_to_tex(boolean export_latex_to_tex) {
		me.export_latex_to_tex = export_latex_to_tex;
	}

	public void setExport_with_border(boolean export_with_border) {
		me.export_with_border = export_with_border;
	}

	public void setFloating_text_size(Point floating_text_size) {
		me.floating_text_size = floating_text_size;
	}

	public void setGrid(int grid) {
		me.grid = grid;
	}

	public void setLast_used_path(String last_used_path) {
		me.last_used_path = last_used_path;
	}

	public void setPath_to_ps(String path_to_ps) {
		me.path_to_ps = path_to_ps;
	}

	public void setPath_to_tex(String path_to_tex) {
		me.path_to_tex = path_to_tex;
	}

	public void setSelect_all_edges(boolean select_all_edges) {
		me.select_all_edges = select_all_edges;
	}

	public void setSelect_all_labels(boolean select_all_labels) {
		me.select_all_labels = select_all_labels;
	}

	public void setSystem_path(String system_path) {
		me.system_path = system_path;
	}

	public void setTex_path(String tex_path) {
		me.tex_path = tex_path;
	}

	public void setUse_error_reporting(boolean use_error_reporting) {
		me.use_error_reporting = use_error_reporting;
	}

	public void setUse_latex_labels(boolean use_latex_labels) {
		me.use_latex_labels = use_latex_labels;
	}

	public void setUse_pstricks(boolean use_pstricks) {
		me.use_pstricks = use_pstricks;
	}

	public void setUse_standard_node_size(boolean use_standard_node_size) {
		me.use_standard_node_size = use_standard_node_size;
	}
	
	///////////////////////////////////////////////////////////////////////////
	// constants //////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	
	/**
     * The name of the simple text file where the system variables will be recorded
     */
	private static final String settings_file_name = "settings.txt"; // in the application path
	
	/**
     * Paths to third party latex application.
     */
	public static final String DEFAULT_TEX_PATH = "C:" + File.separator + "texmf" + File.separator + "miktex" + File.separator + "bin",
								DEFAULT_PS_PATH = "C:" + File.separator + "gs" + File.separator + "gs8.11" + File.separator + "bin";

	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// system variables ///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
	
	
	/**
     * The physical location of the main JAR
     */
	private  String application_path = "";

	/**
     * The physical location used for system settings files and undo/redo files, etc.
     */
	private  String system_path = "";

	/**
     * The physical location used for export to latex files
     */
	private  String tex_path = "";
	
	/**
     * The last used physical location for save/load
     */
	private  String last_used_path = "";

	/**
     * Records the last "snap to grid" value.
     */
	private  int grid = 20;

	/**
     * Records the last "show_all_edges" state.
     */
	private  boolean select_all_edges = false;

	/**
     * Records the last "show_all_labels" state.
     */
	private  boolean select_all_labels = false;
	
	/**
     * Records whether or not crashing errors should automatically open a webpage to report their details.
     */
	private  boolean use_error_reporting = false;

	/**
     * Records whether or not labels should be renderd latex or just plain text
     */
	private  boolean use_latex_labels = false;

	/**
     * Records whether or not export to latex should automatically create an eps with the generated code.
     */
	private  boolean export_latex_to_eps = false;

	/**
     * Records whether or not export to latex should automatically create an tex with the generated code.
     */
	private  boolean export_latex_to_tex = true;
	
	/**
     * Optionally draw a border around export output.
     */
	private  boolean export_with_border = false;
		
	/**
     * Force all nodes to the largest used radius
     */
	private  boolean use_standard_node_size = false;

	/**
     * Use pstricks as the export to latex format (versus pict2e)
     */
	private  boolean use_pstricks = true;

	/**
     * The size for the node text eding window
     */
	private Point floating_text_size = new Point(200,100);
	
	/**
     * Path to a latex rendering tool.
     */
	private String path_to_tex = DEFAULT_TEX_PATH;
	
	/**
     * Path to a post script handling tool.
     */
	private String path_to_ps = DEFAULT_PS_PATH;


}