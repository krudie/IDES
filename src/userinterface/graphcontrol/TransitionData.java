/*
 * Created on Sep 28, 2004
 */
package userinterface.graphcontrol;

import ides2.SystemVariables;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.holongate.j2d.IPaintable;
import org.holongate.j2d.J2DCanvas;

import userinterface.GraphingPlatform;
import userinterface.ResourceManager;
import userinterface.general.Ascii;
import userinterface.general.ImageSupport;

/**
 * This class handles the creation and management of everything inside the Graph Specification tab.
 * Specifically it handles the managment of all transition data, their names, descriptions, controllability, etc.
 * In the GraphModel, individual Edges maintin pointers to various entries in this class.  It is safe to change
 * and destroy information here, because the edges always check if the entries still exist before trying to access
 * any of their data.
 * 
 * @author Michael Wood
 */
public class TransitionData 
{
	/**
     * String representations of boolean combo values.
     */
	public static final String BOOLEAN_COMBO_FALSE = "No",
						       BOOLEAN_COMBO_TRUE = "Yes";
	
	/**
     * Indicies of Columns within the Specifications Table
     */
	public static final int SPEC_NAME = 0,
							SPEC_LATEX = 1,
							SPEC_SYMBOL = 2,
							SPEC_CONTROLLABLE = 3,
							SPEC_OBSERVABLE = 4,
							SPEC_MACHINE_CODE = 5,
							SPEC_DESCRIPTION = 6;

	/**
     * String identifiers for the various columns, indicies are relative to the table columns.
     */
	public final String[] spec_strings = { 
			                       "NAME",
							       "LATEX",
		        				   "SYMBOL",
								   "CONTROLLABLE",
								   "OBSERVABLE",
								   "MACHINE_CODE",
								   "DESCRIPTION" };
	
	/**
     * The platform in which this TransitionData will exist.
     */
	private GraphingPlatform gp = null;

	/**
     * The composite in which this TransitionData's objects will be embedded.
     */
	private Composite parent = null;
	
	/**
     * The table that contains the specifictions info.
     */
	public Table edges_table = null;

	/**
     * The table columns.
     */
	private TableColumn col_name = null,
						col_latex = null,
					    col_symbol = null,
						col_controllable = null,
						col_observable = null,
						col_machine_code = null,
					    col_description = null;

	/**
     * The last selected table item.
     */
	private TableItem selected_item = null;

	/**
     * The text boxes in the editable region
     */
	private Text txt_name = null,
    			 txt_symbol = null,
				 txt_machine_code = null,
				 txt_description = null,
				 txt_latex = null,
				 txt_trace = null;

	/**
     * The combo boxes in the editable region
     */
	private Combo cbo_controllable = null,
				  cbo_observable = null;
		

	/**
     * Used to display rendered LaTeX
     */
	private J2DCanvas j2dcanvas = null;
	
	/**
     * Used to remember the ideal column sizes
     */
	private int[] col_sizes = null;
	
	/**
     * A TableItem used by the input system
     */
	private TableItem input_item = null;
	
	/**
	 * Records if any changes have been made to the transition data.
	 */
	public boolean dirty_edges = false;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// TransitionData construction ////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
	
    /**
     * Construct the TransitionData.
     *
     * @param	graphing_platform	The platform in which this TransitionData will exist.
     * @param	parent				The composite in which this TransitionData's objects will be embedded.
     */
	public TransitionData (GraphingPlatform graphing_platform, Composite parent)
	{
		gp = graphing_platform;
		this.parent = parent;

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// the edit region ////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
		
		// the edit region composite		
		Composite cmp_edit_region = new Composite(parent, SWT.NULL);
		GridData gd_edit_region = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		cmp_edit_region.setLayoutData(gd_edit_region); // for the composite within the tabfolder

		// create a layout for the edit region composite (for the widgits inside the composite)
		GridLayout gl_edit_region = new GridLayout();
		gl_edit_region.verticalSpacing = 3;
		gl_edit_region.numColumns = 8;
		cmp_edit_region.setLayout(gl_edit_region); // attach it to the composite

		// row #1
		
		Label lbl_symbol = new Label(cmp_edit_region, SWT.NONE);
		lbl_symbol.setText(ResourceManager.getString("edge.symbol") + ": ");
		GridData gd_lbl_symbol = new GridData(GridData.HORIZONTAL_ALIGN_END);
		lbl_symbol.setLayoutData(gd_lbl_symbol);	
		
		txt_symbol = new Text(cmp_edit_region, SWT.BORDER);
		GridData gd_txt_symbol = new GridData();
		txt_symbol.setLayoutData(gd_txt_symbol);
				
		Label lbl_name = new Label(cmp_edit_region, SWT.NONE);
		lbl_name.setText(ResourceManager.getString("edge.name") + ": ");
		GridData gd_lbl_name = new GridData(GridData.HORIZONTAL_ALIGN_END);
		lbl_name.setLayoutData(gd_lbl_name);
		
		txt_name = new Text(cmp_edit_region, SWT.BORDER);
		GridData gd_txt_name = new GridData(GridData.FILL_HORIZONTAL);
		gd_txt_name.horizontalSpan = 5;
		txt_name.setLayoutData(gd_txt_name);

		// row #2

		Label lbl_machine_code = new Label(cmp_edit_region, SWT.NONE);
		lbl_machine_code.setText(ResourceManager.getString("edge.machine_code") + ": ");
		GridData gd_lbl_machine_code = new GridData(GridData.HORIZONTAL_ALIGN_END);
		lbl_machine_code.setLayoutData(gd_lbl_machine_code);	
		
		txt_machine_code = new Text(cmp_edit_region, SWT.BORDER);
		GridData gd_txt_machine_code = new GridData();
		txt_machine_code.setLayoutData(gd_txt_machine_code);

		Label lbl_description = new Label(cmp_edit_region, SWT.NONE);
		lbl_description.setText(ResourceManager.getString("edge.description") + ": ");
		GridData gd_lbl_description = new GridData(GridData.HORIZONTAL_ALIGN_END);
		lbl_description.setLayoutData(gd_lbl_description);	
		
		txt_description = new Text(cmp_edit_region, SWT.BORDER);
		GridData gd_txt_description = new GridData(GridData.FILL_HORIZONTAL);
		gd_txt_description.horizontalSpan = 5;
		txt_description.setLayoutData(gd_txt_description);

		// row #3
		
		Label lbl_properties = new Label(cmp_edit_region, SWT.NONE);
		lbl_properties.setText(ResourceManager.getString("edge.properties") + ": ");
		GridData gd_lbl_properties = new GridData(GridData.HORIZONTAL_ALIGN_END);
		lbl_properties.setLayoutData(gd_lbl_properties);	

		Composite cmp_properties = new Composite(cmp_edit_region, SWT.NONE);
		GridData gd_cmp_properties = new GridData();
		gd_cmp_properties.horizontalSpan = 3;
		cmp_properties.setLayoutData(gd_cmp_properties);	
		cmp_properties.setLayout(new FillLayout());
		
		cbo_controllable = new Combo(cmp_properties, SWT.DROP_DOWN | SWT.READ_ONLY);
		cbo_controllable.setItems(new String[] {ResourceManager.getString("edge.controllable"), ResourceManager.getString("edge.uncontrollable")});
		cbo_controllable.select(0);

		cbo_observable = new Combo(cmp_properties, SWT.DROP_DOWN | SWT.READ_ONLY);
		cbo_observable.setItems(new String[] {ResourceManager.getString("edge.observable"), ResourceManager.getString("edge.unobservable")});
		cbo_observable.select(0);

		Label lbl_nothing = new Label(cmp_edit_region, SWT.NONE);
		GridData gd_lbl_nothing = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		lbl_nothing.setLayoutData(gd_lbl_nothing);	
		
		Button save_button = new Button(cmp_edit_region, SWT.PUSH);
		save_button.setText(ResourceManager.getString("edge.save_button"));
		GridData gd_save_button = new GridData();
		save_button.setLayoutData(gd_save_button);	
		save_button.addSelectionListener
		(
			new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e) { saveData(); }
				public void widgetDefaultSelected(SelectionEvent e) { saveData(); }				
			}
		);
		
		Button save_as_new_button = new Button(cmp_edit_region, SWT.PUSH);
		save_as_new_button.setText(ResourceManager.getString("edge.save_as_new_button"));
		GridData gd_save_as_new_button = new GridData();
		save_as_new_button.setLayoutData(gd_save_as_new_button);	
		save_as_new_button.addSelectionListener
		(
			new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e) { saveNewData(); }
				public void widgetDefaultSelected(SelectionEvent e) { saveNewData(); }				
			}
		);
		
		Button delete_selected_button = new Button(cmp_edit_region, SWT.PUSH);
		delete_selected_button.setText(ResourceManager.getString("edge.delete_selected_button"));
		GridData gd_delete_selected_button = new GridData();
		delete_selected_button.setLayoutData(gd_delete_selected_button);	
		delete_selected_button.addSelectionListener
		(
			new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e) { deleteData(); }
				public void widgetDefaultSelected(SelectionEvent e) { deleteData(); }				
			}
		);
		
		// row #4

		Label lbl_latex = new Label(cmp_edit_region, SWT.NONE);
		lbl_latex.setText(ResourceManager.getString("edge.latex") + ": ");
		GridData gd_lbl_latex = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_BEGINNING);
		lbl_latex.setLayoutData(gd_lbl_latex);	
		
		txt_latex = new Text(cmp_edit_region, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd_txt_latex = new GridData(GridData.FILL_HORIZONTAL);
		gd_txt_latex.horizontalSpan = 4;
		gd_txt_latex.heightHint = 70;
		txt_latex.setLayoutData(gd_txt_latex);
		
		j2dcanvas = new J2DCanvas
		(
			cmp_edit_region, 
			SWT.NULL, 
			new IPaintable()
			{
				public void redraw(Control control, GC gc) { }
				public Rectangle2D getBounds(Control control) { return null; }
				public void paint(Control control, Graphics2D g2d) 
				{
				    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
				    g2d.setStroke(new BasicStroke(2));
				    draw(g2d);
				}
			}
		);
		GridData gd_j2dcanvas = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		gd_j2dcanvas.horizontalSpan = 3;
		j2dcanvas.setBackground(gp.display.getSystemColor(SWT.COLOR_WHITE));
		j2dcanvas.setLayoutData(gd_j2dcanvas);
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// the specifications table ///////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
		
		edges_table = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER);
		edges_table.setLinesVisible(true);
		edges_table.setHeaderVisible(true);
				
		GridData gd_table = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		edges_table.setLayoutData(gd_table);

		edges_table.addListener(SWT.Resize, new Listener() { public void handleEvent(Event e) { resize(); } } );
		
		edges_table.addSelectionListener
		(
			new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e) { loadData(); }
				public void widgetDefaultSelected(SelectionEvent e) { loadData(); }				
			}
		);
				
		col_name = new TableColumn(edges_table,SWT.LEFT);
		col_name.setResizable(true);
		col_name.setText(ResourceManager.getString("edge.name"));
		col_name.pack();

		col_latex = new TableColumn(edges_table,SWT.LEFT);
		col_latex.setResizable(true);
		col_latex.setText(ResourceManager.getString("edge.latex"));
		col_latex.pack();
		
		col_symbol = new TableColumn(edges_table,SWT.LEFT);
		col_symbol.setResizable(true);
		col_symbol.setText(ResourceManager.getString("edge.symbol"));
		col_symbol.pack();

		col_controllable = new TableColumn(edges_table,SWT.LEFT);
		col_controllable.setResizable(true);
		col_controllable.setText(ResourceManager.getString("edge.controllable"));
		col_controllable.pack();

		col_observable = new TableColumn(edges_table,SWT.LEFT);
		col_observable.setResizable(true);
		col_observable.setText(ResourceManager.getString("edge.observable"));
		col_observable.pack();
		
		col_machine_code = new TableColumn(edges_table,SWT.LEFT);
		col_machine_code.setResizable(true);
		col_machine_code.setText(ResourceManager.getString("edge.machine_code"));
		col_machine_code.pack();
		
		col_sizes = new int[] {col_name.getWidth(), col_latex.getWidth(), col_symbol.getWidth(), col_controllable.getWidth(), col_observable.getWidth(), col_machine_code.getWidth()};
		
		col_description = new TableColumn(edges_table,SWT.LEFT);
		col_description.setResizable(true);
		col_description.setText(ResourceManager.getString("edge.description"));
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// the trace text box /////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
		
		// the trace region composite		
		Composite cmp_trace_region = new Composite(parent, SWT.NULL);
		GridData gd_trace_region = new GridData(GridData.FILL_HORIZONTAL);
		cmp_trace_region.setLayoutData(gd_trace_region); // for the composite within the tabfolder
		
		// create a layout for the trace region composite (for the widgits inside the composite)
		GridLayout gl_trace_region = new GridLayout();
		gl_trace_region.numColumns = 2;
		gl_trace_region.marginHeight = 0;
		gl_trace_region.marginWidth = 0;
		gl_trace_region.verticalSpacing = 0;
		cmp_trace_region.setLayout(gl_trace_region); // attach it to the composite
		
		Label lbl_trace = new Label(cmp_trace_region, SWT.NONE);
		lbl_trace.setText(ResourceManager.getString("edge.trace") + ": ");
		GridData gd_lbl_trace = new GridData(GridData.HORIZONTAL_ALIGN_END);
		lbl_trace.setLayoutData(gd_lbl_trace);	
		
		txt_trace = new Text(cmp_trace_region, SWT.BORDER);
		GridData gd_txt_trace = new GridData(GridData.FILL_HORIZONTAL);
		txt_trace.setLayoutData(gd_txt_trace);
		
		txt_trace.addKeyListener( new KeyListener() { public void keyPressed(KeyEvent e) {} public void keyReleased(KeyEvent e) { gp.gc.io.markUnsavedChanges(); } } );
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Gui ////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////				
	
	public void repaint() { j2dcanvas.repaint(); }
	
	public void draw(Graphics2D g2d)
	{
	    if (selected_item != null && !selected_item.isDisposed() && selected_item.getData("awt") != null)
	    { g2d.drawImage((BufferedImage)selected_item.getData("awt"),null,5,5); }
	    else
	    { g2d.drawString(ResourceManager.getString("edge.latex_none"),5,15); }
	}
	
    /**
     * Resize the specifictions table.
     */
	public void resize()
	{
		if (gp.tabFolder.getSelectionIndex() == GraphingPlatform.SPECIFICATIONS_TAB)
		{
			Point size = parent.getSize();
			int used_width = 0;
			for (int i=0; i<col_sizes.length; i++) { used_width = used_width + col_sizes[i]; }
			col_name.setWidth(col_sizes[SPEC_NAME]);
			col_latex.setWidth(col_sizes[SPEC_LATEX]);
			col_symbol.setWidth(col_sizes[SPEC_SYMBOL]);
			col_controllable.setWidth(col_sizes[SPEC_CONTROLLABLE]);
			col_observable.setWidth(col_sizes[SPEC_OBSERVABLE]);
			col_machine_code.setWidth(col_sizes[SPEC_MACHINE_CODE]);
			col_description.setWidth(size.x - used_width - 12);
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Editing ////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////				
	
    /**
     * Load the data from the selected table item (ie row) to the editable area
     */
	public void loadData()
	{
		selected_item = edges_table.getItem(edges_table.getSelectionIndex());
		txt_name.setText(selected_item.getText(SPEC_NAME));
		txt_latex.setText(selected_item.getText(SPEC_LATEX));
		txt_symbol.setText(selected_item.getText(SPEC_SYMBOL));
		cbo_controllable.select(convertToCombo(selected_item.getText(SPEC_CONTROLLABLE)));
		cbo_observable.select(convertToCombo(selected_item.getText(SPEC_OBSERVABLE)));
		txt_machine_code.setText(selected_item.getText(SPEC_MACHINE_CODE));
		txt_description.setText(selected_item.getText(SPEC_DESCRIPTION));
		j2dcanvas.repaint();
	}
		
    /**
     * Save the data from the editable area to the last selected table item (ie row)
     */
	public void saveData()
	{
		if (selected_item != null && !selected_item.isDisposed())
		{
			selected_item.setText(SPEC_NAME,txt_name.getText());
			selected_item.setText(SPEC_LATEX,txt_latex.getText());
			selected_item.setText(SPEC_SYMBOL,txt_symbol.getText());
			selected_item.setText(SPEC_CONTROLLABLE,convertFromCombo(cbo_controllable.getSelectionIndex()));
			selected_item.setText(SPEC_OBSERVABLE,convertFromCombo(cbo_observable.getSelectionIndex()));
			selected_item.setText(SPEC_MACHINE_CODE,txt_machine_code.getText());
			selected_item.setText(SPEC_DESCRIPTION,txt_description.getText());
			dirty_edges = true;
			gp.gc.io.markUnsavedChanges();
		}
	}
	
    /**
     * Save the data from the editable area to a new table item (ie row)
     */
	public void saveNewData()
	{
		if (txt_name.getText().length() + txt_latex.getText().length() + txt_symbol.getText().length() + txt_machine_code.getText().length() + txt_description.getText().length() > 0)
		{
			selected_item = new TableItem(edges_table,SWT.NONE); 
			saveData();
			edges_table.setSelection(new TableItem[] {selected_item});
			dirty_edges = true;
			gp.gc.io.markUnsavedChanges();
		}
	}
	
    /**
     * Delete the last selected table item (ie row)
     */
	public void deleteData()
	{
		if (edges_table.getSelectionIndex() >= 0) 
		{ 
			ImageSupport.safeDispose(edges_table.getItem(edges_table.getSelectionIndex()).getData("swt"));
			edges_table.getItem(edges_table.getSelectionIndex()).setData("swt",null);
			edges_table.remove(edges_table.getSelectionIndex()); 
			dirty_edges = true;
			gp.gc.io.markUnsavedChanges();
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// File System ////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////					
	
	/**
	 * Output a representation of the data in gml format.
	 * 
	 * @param	out		An initialized PrintWriter for outputing the data to file.
	 */	
	public void printData(PrintWriter out)
	{
		TableItem[] table_items = edges_table.getItems();
		for (int i=0; i<table_items.length; i++)
		{ 
	        out.println("    " + "data" + i + "." + spec_strings[SPEC_NAME]         + " " + table_items[i].getText(SPEC_NAME));
	        out.println("    " + "data" + i + "." + spec_strings[SPEC_LATEX]        + " " + Ascii.escapeReturn(table_items[i].getText(SPEC_LATEX)));
	        out.println("    " + "data" + i + "." + spec_strings[SPEC_SYMBOL]       + " " + table_items[i].getText(SPEC_SYMBOL));
	        out.println("    " + "data" + i + "." + spec_strings[SPEC_CONTROLLABLE] + " " + convertToCombo(table_items[i].getText(SPEC_CONTROLLABLE)));
	        out.println("    " + "data" + i + "." + spec_strings[SPEC_OBSERVABLE]   + " " + convertToCombo(table_items[i].getText(SPEC_OBSERVABLE)));
	        out.println("    " + "data" + i + "." + spec_strings[SPEC_MACHINE_CODE] + " " + table_items[i].getText(SPEC_MACHINE_CODE));
	        out.println("    " + "data" + i + "." + spec_strings[SPEC_DESCRIPTION]  + " " + table_items[i].getText(SPEC_DESCRIPTION));
		}	
	}
	
	/**
	 * Input the data from the gml format.
	 * This method empties the tokenizer.
	 * 
	 * @param	in			An initialized BufferedReader for inputing the data from file.
	 * @param	st			The string tokenizer containing this_line.
	 * @param	this_token	The first token from st, hence st.nextToken will return the second token.
	 * @param	this_line	The line that st has tokenized
	 * @return 	true if the operation was successful.
	 */	
	public boolean inputData(BufferedReader in, StringTokenizer st, String this_token, String this_line)
	{
		boolean success = false;
		try
		{
			if (this_line.length() >= 5 + this_token.length())
			{
				// format is 4 spaces then the label (token1) then a space then the remainder of the line is the data
				this_line = this_line.substring(5+this_token.length());
				// the calling function needs the tokenizer to be empty else it will continue processing this line
				while (st.hasMoreTokens()) { st.nextToken(); }
			}
			else { return false; }
			
			if (this_token.endsWith("." + spec_strings[SPEC_NAME]))
			{
				input_item = new TableItem(edges_table,SWT.NONE); // this data is for a new TableItem
				input_item.setText(SPEC_NAME,this_line);
				success = true;
			}
			else if (this_token.endsWith("." + spec_strings[SPEC_SYMBOL]))
			{
				input_item.setText(SPEC_SYMBOL,this_line);
				success = true;
			}
			else if (this_token.endsWith("." + spec_strings[SPEC_CONTROLLABLE]))
			{
				input_item.setText(SPEC_CONTROLLABLE,convertFromCombo(this_line));
				success = true;
			}
			else if (this_token.endsWith("." + spec_strings[SPEC_OBSERVABLE]))
			{
				input_item.setText(SPEC_OBSERVABLE,convertFromCombo(this_line));
				success = true;
			}
			else if (this_token.endsWith("." + spec_strings[SPEC_MACHINE_CODE]))
			{
				input_item.setText(SPEC_MACHINE_CODE,this_line);
				success = true;
			}
			else if (this_token.endsWith("." + spec_strings[SPEC_DESCRIPTION]))
			{
				input_item.setText(SPEC_DESCRIPTION,this_line);
				success = true;
			}			
		}
	    catch (Exception e)	{ throw new RuntimeException(e); } 
	    return success;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Miscelaneous ///////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////					
	
    /**
     * Copy any existing information from the abandoned label type
     * into the new label type providing that the new type has an empty value.
     * Also calls renderIfNecessary()
     */
	public void fillBlankLabels()
	{
		TableItem[] table_items = edges_table.getItems();
		for (int i=0; i<table_items.length; i++)
		{ 
		    if (table_items[i].getText(SPEC_SYMBOL).length() < 1)
		    { table_items[i].setText(SPEC_SYMBOL,table_items[i].getText(SPEC_LATEX)); }				
			
		}			
	}
	
	/**
	 * Reset the input state before attempting an input.
	 */	
	public void resetInputState() 
	{ 
		dispose();
		edges_table.removeAll();
		txt_name.setText("");
		txt_latex.setText("");
		txt_symbol.setText("");
		txt_machine_code.setText("");
		txt_description.setText("");
		txt_trace.setText("");
		dirty_edges = true;
	}

	/**
	 * Translate between combo selection indicies and display string.
	 * 
	 * @param	index_meaning	The index of a binary selection combo.
	 * @return	"Yes" for the 0th index, "No" otherwise.
	 */	
	private String convertFromCombo(int index_meaning)
	{
		if (index_meaning == 0) { return BOOLEAN_COMBO_TRUE; }
		else { return BOOLEAN_COMBO_FALSE; }
	}

	/**
	 * Translate between combo selection indicies and display string.
	 * 
	 * @param	index_meaning	The index of a binary selection combo.
	 * @return	"Yes" for the 0th index, "No" otherwise.
	 */	
	private String convertFromCombo(String index_meaning)
	{
		if (index_meaning.equals("0")) { return BOOLEAN_COMBO_TRUE; }
		else { return BOOLEAN_COMBO_FALSE; }
	}
		
	/**
	 * Translate between combo selection indicies and display string.
	 * 
	 * @param	text_meaning	The text meaning of a binary selection combo.
	 * @return	0 for "Yes", 1 otherwise.
	 */	
	private int convertToCombo(String text_meaning)
	{
		if (text_meaning == BOOLEAN_COMBO_TRUE) { return 0; }
		else { return 1; }
	}
	
	public void dispose()
	{
		TableItem[] table_items = edges_table.getItems();
		for (int i=0; i<table_items.length; i++)
		{ ImageSupport.safeDispose(table_items[i].getData("swt")); }
	}
	
	public int[] getTrace()
	{
		StringTokenizer s = new StringTokenizer(txt_trace.getText(),",");
		if (s.countTokens() > 0)
		{
			int[] symbols = new int[s.countTokens()];
			int i = 0;
			while (s.hasMoreTokens())
			{
				try { symbols[i] = Integer.parseInt(s.nextToken()); }
				catch (Exception e) { symbols[i] = -1; }			 
				i++;
			}
			return symbols;
		}
		else { return null; }
	}
	
	public String getTraceString() { return txt_trace.getText(); }
	
	public void setTraceString(String new_trace) { txt_trace.setText(new_trace); }
}