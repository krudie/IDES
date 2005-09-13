/*
 * Created on Dec 2, 2004
 */
package com.aggressivesoftware.ides.graphcontrol;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import com.aggressivesoftware.geometric.Box;
import com.aggressivesoftware.geometric.Point;
import com.aggressivesoftware.ides.GraphingPlatform;
import com.aggressivesoftware.ides.graphcontrol.graphparts.Edge;
import com.aggressivesoftware.ides.graphcontrol.graphparts.Node;

/**
 * This class handles all the IO for the GraphController.  This includes: file save/load, edit undo/redo
 * and export to latex.
 * 
 * @author MichaelWood
 */
public class GraphControllerIO 
{
	/**
     * The platform in which this GraphControllerIO will exist.
     */
	private GraphingPlatform gp = null;

	/**
     * Counts the number of mouse-up clicks to determine when it should save state to allow undos.
     */
	private int click_count = 0;	
	
	/**
     * Records if any unsaved chanes have been made to the current graph.
     */
	public boolean unsaved_changes = false;

	/**
     * Pointers to the savestate files
     */
	private int oldest_undo_pointer = -1,
				youngest_undo_pointer = -1,
				current_undo_pointer = -1;
	
	public static final String tex_name = "graph.tex",
							   dvi_name = "graph.dvi",
							   eps_name = "graph.eps",
							   ps_name  = "graph.ps",
							   page_name = "page.tex";
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// GraphControllerIO construction /////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
	
    /**
     * Construct the GraphControllerIO.
     *
     * @param	gp	The platform in which this GraphControllerIO will exist.
     */
	public GraphControllerIO(GraphingPlatform gp)
	{
		this.gp = gp;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Miscelaneous ///////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
	
    /**
     * Increase the click count
     */
	public void click()
	{
		// save state every 5 clicks
		// keep a history of up to 15 statesaves
		click_count = (click_count + 1) % (5*15);

		gp.mc.edit_redo.disable();
		if (youngest_undo_pointer != -1) { gp.mc.edit_undo.enable(); }
		
		if (click_count % 5 == 0) 
		{ 
			saveState(); 
			current_undo_pointer = youngest_undo_pointer;
		}
	}
	
    /**
     * Notify the system that the current data structure has been changed.
     * Main use is enable/disable of save, etc buttons.
     */
	public void markUnsavedChanges()
	{
		gp.mc.file_save.enable();
		gp.mc.file_saveas.enable();
		unsaved_changes = true;
	}
	
    /**
     * Reset all variables to the initial state.
     */
	public void resetState()
	{
		unsaved_changes = false;			
		gp.mc.file_save.disable();
		gp.mc.file_saveas.disable();
		gp.mc.edit_undo.disable();
		gp.mc.edit_redo.disable();
		oldest_undo_pointer = -1;
		youngest_undo_pointer = -1;
		current_undo_pointer = -1;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// saves //////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
	
	/**
	 * Save the current graph in gml format.
	 * 
	 * @param	save_location	The file system location where it is to be saved.
	 * @return 	true if the operation was successful.
	 */	
	public boolean save(String save_location)
	{
	    try 
		{ 
	    	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(save_location))); 
	    	outputGraphData(out);
		    out.close();
			gp.mc.file_save.disable();
			unsaved_changes = false;
			return true;
		}
	    catch (Exception e)
		{
			MessageBox error_popup = new MessageBox(gp.shell, SWT.ICON_ERROR | SWT.CLOSE); 
			error_popup.setText(gp.rm.getString("file_sys.error_title"));
			error_popup.setMessage(gp.rm.getMessage("file_sys.save_failure",save_location));
			error_popup.open();	
			return false;
		}
	}

	/**
	 * Save the current state to allow undos.
	 */	
	private void saveState()
	{
	    try 
		{ 
			youngest_undo_pointer = click_count/5;
			if (oldest_undo_pointer == -1)
			{
				// this is the first savestate
				oldest_undo_pointer = youngest_undo_pointer; 
			}
			else if (oldest_undo_pointer == youngest_undo_pointer)
			{
				// we have wrapped around, therefore advance old pointer
				oldest_undo_pointer = (oldest_undo_pointer + 1) % 15;
			}
			
	    	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(gp.sv.system_path + "savestate" + youngest_undo_pointer + ".gml"))); 
	    	outputGraphData(out);
		    out.close();
		}
	    catch (Exception e)	{ }
	}	
	
	/**
	 * Write graph data to file
	 * 
	 * @param	out		An initialized PrintWriter to output the graph data.
	 */	
	private void outputGraphData(PrintWriter out) throws Exception
	{
        out.println("graph [");
        out.println("    grid " + gp.sv.grid);
        out.println("    grid_displacement " + gp.gc.gm.grid_displacement);
        out.println("    scale " + gp.gc.gm.scale);
        out.println("    trace " + gp.td.getTraceString());
        if (gp.sv.use_standard_node_size)
        { out.println("    standard_node 1"); }
        else
        { out.println("    standard_node 0"); }
        if (gp.gc.gm.print_area.isVisible()) { out.println("    print_area " + gp.gc.gm.print_area); }
        gp.td.printData(out);
        gp.gc.gm.printNodes(out);
        gp.gc.gm.printEdges(out);
        out.println("]");
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// loads //////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
	
    /**
     * Open a gml file and load the graph.
     * 
     * @param	open_location	The file system location where the data should be read from.
	 * @return 	true if the operation was successful.
     */
	public boolean open(String open_location)
	{
		boolean success = true;
	    try
		{ 
	    	BufferedReader in = new BufferedReader(new FileReader(open_location)); 
	    	gp.gc.resetState();
	    	success = inputGraphData(in);
			gp.mc.file_saveas.enable();
			if (!success)
			{
				MessageBox error_popup = new MessageBox(gp.shell, SWT.ICON_ERROR | SWT.CLOSE); 
				error_popup.setText(gp.rm.getString("file_sys.error_title"));
				error_popup.setMessage(gp.rm.getMessage("file_sys.bad_data",open_location));
				error_popup.open();					
			}
			return success;
	    }
	    catch (Exception e)
		{ 			
	    	gp.gc.repaint();
			MessageBox error_popup = new MessageBox(gp.shell, SWT.ICON_ERROR | SWT.CLOSE); 
			error_popup.setText(gp.rm.getString("file_sys.error_title"));
			error_popup.setMessage(gp.rm.getMessage("file_sys.open_failure",open_location));
			error_popup.open();	
			return false;
		}
	}
	
	public void loadState()
	{
		boolean success = true;
	    try
		{ 
	    	BufferedReader in = new BufferedReader(new FileReader(gp.sv.system_path + "savestate" + current_undo_pointer + ".gml")); 
	    	gp.gc.resetInternalState();
	    	success = inputGraphData(in);
	    	if (!success)
			{
				MessageBox error_popup = new MessageBox(gp.shell, SWT.ICON_ERROR | SWT.CLOSE); 
				error_popup.setText(gp.rm.getString("file_sys.error_title"));
				error_popup.setMessage(gp.rm.getString("file_sys.fatal_error"));
				error_popup.open();					
			}
	    }
	    catch (Exception e)
		{ 			
	    	gp.gc.repaint();
			MessageBox error_popup = new MessageBox(gp.shell, SWT.ICON_ERROR | SWT.CLOSE); 
			error_popup.setText(gp.rm.getString("file_sys.error_title"));
			error_popup.setMessage(gp.rm.getString("file_sys.fatal_error"));
			error_popup.open();	
		}
	}
	
	/**
	 * Read graph data from file
	 * 
	 * @param	in		An initialized BufferedReader to input the graph data.
	 * @return	true if the operation was successful
	 */	
	private boolean inputGraphData(BufferedReader in) throws Exception
	{
		boolean success = true;
	    String this_line = in.readLine();
	    String this_token = null;
	    while (this_line != null && success) 
	    {
	    	StringTokenizer st = new StringTokenizer(this_line);
	        while (st.hasMoreTokens() && success) 
	        {
	        	this_token = st.nextToken();
	            if(this_token.equals("scale"))
	            { gp.gc.gm.scale = Float.parseFloat(st.nextToken()); }
	            else if(this_token.equals("grid"))
	            { 
	            	gp.sv.grid = Integer.parseInt(st.nextToken()); 
	            	gp.mc.setGridDropdownState(gp.sv.grid);
	            }	            
	            else if(this_token.equals("grid_displacement"))
	            { gp.gc.gm.grid_displacement = new Point(st.nextToken()); }
	            else if(this_token.equals("print_area"))
	            { gp.gc.gm.print_area = new SelectionArea(gp,SelectionArea.MARKING_OUT_AN_AREA,st.nextToken()); }
	            else if(this_token.equals("trace"))
	            { 
	            	try { gp.td.setTraceString(this_line.substring(5+this_token.length())); } 
	            	catch (Exception e) {}
	            }
	            else if(this_token.equals("standard_node"))
	            {
	            	if (Integer.parseInt(st.nextToken()) == 1)
	            	{ gp.sv.use_standard_node_size = true; }
	            	else
	            	{ gp.sv.use_standard_node_size = false; }
            		gp.mc.option_node.setSelection(gp.sv.use_standard_node_size);	            		
	            }	            
	        	else if(this_token.startsWith("data"))
	        	{ success = gp.td.inputData(in, st, this_token, this_line); }
	            else if(this_token.equals("node"))
	            { success = Node.loadFromFile(gp, gp.gc.gm, in); }
	            else if(this_token.equals("edge"))
	            { success = Edge.loadFromFile(gp, gp.gc.gm, in, gp.td); }
	        }
	    	this_line = in.readLine();
	    }
		in.close();
		
		gp.gc.gm.fillBlankLabels();
		gp.gc.gm.accomodateLabels();
		gp.gc.repaint();
		gp.gc.refreshScrollbars();
		return success;
	}
	
	public void undo()
	{
		if (click_count % 5 != 0)
		{ 
			// they have recently made fresh changes, save the new state, and don't move the pointer.
			click_count = ((current_undo_pointer+1)%15)*5;
			saveState();
			loadState();
		}
		else
		{
			// go backwards
			current_undo_pointer--;
			if (current_undo_pointer == -1) { current_undo_pointer = 14; }
			loadState();
		}
		
		if (current_undo_pointer == oldest_undo_pointer) { gp.mc.edit_undo.disable(); }
		if (current_undo_pointer != youngest_undo_pointer) { gp.mc.edit_redo.enable(); }
	}
	
	public void redo()
	{
		current_undo_pointer = (current_undo_pointer+1)%15;
		loadState();
		
		gp.mc.edit_undo.enable();
		if (current_undo_pointer == youngest_undo_pointer) { gp.mc.edit_redo.disable(); }
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// export /////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

    /**
     * Export the selection area to a LaTeX code representation of the graph.
     * It outputs this code to the notepad tab in the main application.
     * Two versons of output can be generated.  The first (and default) is formatted for inclusion as a tex figure,
     * the second is formatted for inclusion as an eps file.
     * If eps_location is specified then it attempts to render the code to eps in the specified location.
     * If tex_location is specified then it saves the tex figure code to the specified location.
     * 
     * @param	eps_location	An optional location for attempted rendering.
     * @param	tex_location	An optional location for saving the tex figure code.
     */
	public void exportLatex(String eps_location, String tex_location)
	{		
		// generate the code
		LatexPrinter latex_printer = new LatexPrinter();
		Box box = gp.gc.gm.print_area.getBox();
		gp.gc.gm.exportLatex(box, latex_printer);		
		
		if (gp.sv.export_latex_to_eps && eps_location != null && eps_location.length() > 0)
		{
			if (gp.sv.use_pstricks)
			{ gp.tm.newLaTeX(latex_printer.outputPstricksForEPS(box.w(),box.h(),gp.sv.export_with_border)); }
			else 
			{ gp.tm.newLaTeX(latex_printer.outputPict2eForEPS(box.w(),box.h(),gp.sv.export_with_border)); }	
			
			boolean show_error_msg = false;
			
			gp.shell.setEnabled(false);
			gp.shell.setCursor(gp.rm.getCursor(gp.rm.WAIT_CURSOR));

			// save the eps code as a tex file
		    try 
			{ 
		    	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(gp.sv.tex_path + tex_name))); 
		        out.println(gp.tm.text_body_area.getText());
			    out.close();
			}
		    catch (Exception e)	{ }
			
			// attempt to render it to an eps.
			if (gp.gc.renderer != null)
			{ 
				try { gp.gc.renderer.latex2EPS(new File(gp.sv.tex_path + tex_name),new File(eps_location)); }
				catch (Exception e) 
				{ 
					e.printStackTrace(); 
					show_error_msg = true;
				}
			}

			gp.shell.setEnabled(true);
			gp.shell.setCursor(gp.rm.getCursor(gp.rm.ARROW_CURSOR));	

			if (show_error_msg)
			{
				MessageBox export_failure = new MessageBox(gp.shell, SWT.ICON_WARNING | SWT.OK); 
				export_failure.setText(gp.rm.getString("file_sys.warning"));
				export_failure.setMessage(gp.rm.getString("file_sys.export_failure"));
				export_failure.open();
			}
		}
		
		if (gp.sv.export_latex_to_tex && tex_location != null && tex_location.length() > 0)
		{
			if (gp.sv.use_pstricks)
			{ gp.tm.newLaTeX(latex_printer.outputPstricksForTEX(box.w(),box.h(),gp.sv.export_with_border)); }
			else 
			{ gp.tm.newLaTeX(latex_printer.outputPict2eForTEX(box.w(),box.h(),gp.sv.export_with_border)); }	

			// save the figure code as a tex file
		    try 
			{ 
		    	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(tex_location))); 
		        out.println(gp.tm.text_body_area.getText());
			    out.close();
			}
		    catch (Exception e)	
			{ 
		    	e.printStackTrace();
				MessageBox export_failure = new MessageBox(gp.shell, SWT.ICON_WARNING | SWT.OK); 
				export_failure.setText(gp.rm.getString("file_sys.warning"));
				export_failure.setMessage(gp.rm.getString("file_sys.export_failure"));
				export_failure.open();
		    }
		}
		
		if (!gp.sv.export_latex_to_eps && !gp.sv.export_latex_to_tex)
		{
			// tex by default if neither were specified
			if (gp.sv.use_pstricks)
			{ gp.tm.newLaTeX(latex_printer.outputPstricksForTEX(box.w(),box.h(),gp.sv.export_with_border)); }
			else 
			{ gp.tm.newLaTeX(latex_printer.outputPict2eForTEX(box.w(),box.h(),gp.sv.export_with_border)); }	
			
			// show them the output tab
			gp.tab_folder.setSelection(GraphingPlatform.TEXT_AREA_TAB);
			gp.tm.text_body_area.forceFocus();
		}
	}
}
