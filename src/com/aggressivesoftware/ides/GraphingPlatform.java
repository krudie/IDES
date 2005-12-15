/*
 * Created on Jun 15, 2004
 */
package com.aggressivesoftware.ides;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.aggressivesoftware.ides.graphcontrol.GraphController;
import com.aggressivesoftware.ides.graphcontrol.TransitionData;
import com.aggressivesoftware.ides.menucontrol.MenuController;

/**
 * This class is the root of the entire application.
 * For ease of understanding, many separate classes were created to handle its various components.
 * Each of these objects are given a reference to this root object: the GraphingPlatform or gp.
 * They communicate to each other through this common reference: i.e. gp.other_component.action();
 * 
 * @author Michael Wood
 */
public class GraphingPlatform
{
	/**
     * Strings for error reporting before the resourse bundle can be loaded.
     */
	public static final String FATAL_ERROR = "Fatal Error: ",
	                           NULL_RESOURCE = "The resource bundle was null.  \nThis component requires the resource bundle.",
							   LOST_RESOURCE = "The resource bundle [resource_bundle.properties] did not load.  \nIt should be located at the root of the source code.";
	
	/**
     * Constants for custom listeners. This is necessary to avoid thread access errors.
     */
	public static final int LABEL_MESSAGE = 1;

	/**
     * Indicies of TabItems within the TabFolder
     */
	public static final int GRAPH_CANVAS_TAB = 0,
							TEXT_AREA_TAB = 2,
							SPECIFICATIONS_TAB =1;
				
	/**
     * The display object for this platform.
     */
	public Display display = null;

	/**
     * The shell which contains the main gui.
     */
	public Shell shell = null;

	/**
     * A shell for displaying popup error windows.
     */
	public Shell error_shell = null;

	/**
     * A class that houses all global settings and system variables, like a registry.
     */
	public SystemVariables sv = null;

	/**
     * The resource manager for this platform, contains the resource bundle, images, cursors, etc.
     */
	public ResourceManager rm = null;

	/**
     * The MenuController for the main gui.
     */
	public MenuController mc = null;

	/**
     * The graph controller manages everything to do with displaying and manipulating the graph
     */
	public GraphController gc = null;

	/**
     * This manages everything in the LaTeX tab.
     */
	public TexManager tm = null;

	/**
     * The object that contains the transition data and exists in the info in the specifications tab
     */
	public TransitionData td = null;	

	/**
     * The TabFolder that contains the body of the application
     */
	public TabFolder tab_folder = null;
	
	/**
     * The TabItems that contain the body of the application
     */
	public TabItem tbitm_graph_canvas = null,
				   tbitm_text_area = null,
				   tbitm_specifications = null;
	
	/**
     * Labels for displaying information at the bottom of the main gui.
     */
	public CLabel lbl_info1 = null,
	              lbl_info2 = null,
				  lbl_info3 = null;
	
	/**
     * Used to pass data from external threads to the UI thread
     */
	public Object ui_data = null;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// GraphingPlatform construction //////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

    /**
     * Construct the GraphingPlatform.
     * 
     * @param	splash	The splash screen.
     */
	public GraphingPlatform(Splash splash)
	{
		boolean use_error_reporting = false;
		
		try
		{
			display = Display.getDefault();
			error_shell = new Shell(display, SWT.NO_TRIM);
			sv = new SystemVariables();
			rm = new ResourceManager(this);
			initializeTheGui();
			shell.addListener 
			(
				SWT.Close, 
				new Listener () 
				{
					public void handleEvent (Event event) 
					{
						event.doit = false;
						if (mc != null && mc.file_listener != null) { mc.file_listener.exit(new SelectionEvent(event)); }
					}
				}
			);
			shell.layout();
			shell.open();
			splash.dispose();
			while (!shell.isDisposed()) { if (!display.readAndDispatch()) display.sleep(); }
			// note td and it's images are implicitly disposed whten the shell is disposed.
			sv.saveValues();			
			gc.dispose();
			mc.dispose();
			rm.dispose();
			error_shell.dispose();
			display.dispose();
		}
		catch(Exception e)
		{
			if (sv != null)
			{
				use_error_reporting = sv.use_error_reporting;
				sv.saveValues();
			}
			if (gc != null)          { gc.dispose(); }
			if (td != null)          { td.dispose(); }
			if (mc != null)          { mc.dispose(); }
			if (rm != null)          { rm.dispose(); }
			if (error_shell != null) { error_shell.dispose(); }
			if (shell != null)       { shell.dispose(); }
			if (display != null)     { display.dispose(); }
			
			if (use_error_reporting)
			{
				Display error_display = Display.getDefault();
				Shell error_shell = new Shell(error_display, SWT.NO_TRIM);
				MessageBox message_box = new MessageBox(error_shell, SWT.ICON_ERROR | SWT.CLOSE); 
				
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				String stacktrace = sw.toString();
				
				message_box.setMessage(e.getMessage() + "\n\n" + stacktrace);
				message_box.setText(GraphingPlatform.FATAL_ERROR);
				message_box.open();			
				
				try { Runtime.getRuntime().exec ("rundll32 url.dll,FileProtocolHandler http://www.aggressivesoftware.com/research/ides/bugs/default.asp?bug=" + URLEncoder.encode(e.getMessage() + "\n\n" + stacktrace,"UTF-8")); }
				catch(Exception ex) { }
			}
			
			throw new RuntimeException(e);
		}
	}

    /**
     * Create and initialize all the gui components.
     */
	private void initializeTheGui()
	{	
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// main structure /////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
		
		// the window
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText(rm.getString("window.title"));
		shell.setImage(rm.getHotImage(rm.LOGO));
		shell.setSize(800,620);
		
		// the base layout
		GridLayout gl_base = new GridLayout();
		gl_base.marginHeight = 0;
		gl_base.marginWidth = 0;
		gl_base.verticalSpacing = 0;
		gl_base.horizontalSpacing = 3;
		gl_base.numColumns = 3;
		shell.setLayout(gl_base); // attach it to the shell

		// the menu controller (this adds menu and coolbar to the shell)
		mc = new MenuController(this);

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// body ///////////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

		tab_folder = new TabFolder(shell,SWT.NONE);
		tbitm_graph_canvas = new TabItem(tab_folder,SWT.NONE);
		tbitm_specifications = new TabItem(tab_folder,SWT.NONE);
		tbitm_text_area = new TabItem(tab_folder,SWT.NONE);
		tbitm_graph_canvas.setText(rm.getString("window.graph_tab.text")); 
		tbitm_specifications.setText(rm.getString("window.specifications_tab.text")); 
		tbitm_text_area.setText(rm.getString("window.text_tab.text")); 
		
		// define the layout of the content composite (within the base layout)
		// define the layout of the TabFolder (within the base layout)
		GridData gd_tab_folder = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL);
		gd_tab_folder.horizontalSpan = 3;
		tab_folder.setLayoutData(gd_tab_folder);
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// specifications area ////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
		
		// Note: this area has to be created before the graph area, because the graph area reads values from these objects.
		
		// the transition data area		
		Composite cmp_transitions = new Composite(tab_folder, SWT.NULL);

		// add it to the TabFolder
		tbitm_specifications.setControl(cmp_transitions);

		// create a layout for the content composite (for the widgits inside the composite)
		GridLayout gl_transitions = new GridLayout();
		gl_transitions.marginHeight = 3;
		gl_transitions.marginWidth = 3;
		gl_transitions.verticalSpacing = 0;
		gl_transitions.horizontalSpacing = 0;
		cmp_transitions.setLayout(gl_transitions); // attach it to the composite

		// add the transition data object
		td = new TransitionData(this,cmp_transitions);
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// graph area /////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
		
		// the graphing area		
		Composite cmp_graphing = new Composite(tab_folder, SWT.BORDER);

		// add it to the TabFolder
		tbitm_graph_canvas.setControl(cmp_graphing);

		// create a layout for the content composite (for the widgits inside the composite)
		GridLayout gl_graphing = new GridLayout();
		gl_graphing.marginHeight = 0;
		gl_graphing.marginWidth = 0;
		gl_graphing.verticalSpacing = 0;
		gl_graphing.horizontalSpacing = 0;
		gl_graphing.numColumns = 2;
		cmp_graphing.setLayout(gl_graphing); // attach it to the composite

		// add the graph controller, and force the focus here
		gc = new GraphController(this,cmp_graphing);
		cmp_graphing.forceFocus();
				
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// TeX area ///////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

		// the tex area		
		Composite cmp_tex = new Composite(tab_folder, SWT.NULL);

		// add it to the TabFolder
		tbitm_text_area.setControl(cmp_tex);

		// create the contents of the tex tab.
		tm = new TexManager(this,cmp_tex);
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// selection between tabs /////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

		tab_folder.addSelectionListener
		(
			new SelectionAdapter() 
			{ 
				public void widgetSelected(SelectionEvent e) 
				{ 
					if (td.dirty_edges)
					{
						td.dirty_edges = false;
						gc.gm.accomodateLabels();
						gc.repaint();
					}
				}
			}
		);
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// footer area ////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

		// create the info labels
		lbl_info1 = new CLabel(shell, SWT.LEFT | SWT.SHADOW_IN);
		lbl_info2 = new CLabel(shell, SWT.LEFT | SWT.SHADOW_IN);
		lbl_info3 = new CLabel(shell, SWT.LEFT | SWT.SHADOW_IN);

		// initialize the info labels
		lbl_info1.setText(rm.getString("lbl_info1.text"));
		lbl_info2.setText(rm.getString("lbl_info2.text"));
		lbl_info3.setText(rm.getString("lbl_info3.text") + rm.getString("lbl_info3.none"));

		// define the layouts of the info labels
		GridData gd_info1 = new GridData(GridData.FILL_HORIZONTAL);  
		gd_info1.heightHint = 22;
		gd_info1.widthHint = 10;
		lbl_info1.setLayoutData(gd_info1);
		GridData gd_info2 = new GridData(); 
		gd_info2.heightHint = 22;
		gd_info2.widthHint = 80;
		lbl_info2.setLayoutData(gd_info2);
		GridData gd_info3 = new GridData(); 
		gd_info3.heightHint = 22;
		gd_info3.widthHint = 141;
		lbl_info3.setLayoutData(gd_info3);
	}

	public void updateStatusInfo()
	{
		lbl_info1.setText("States: "+gc.gm.getNodeCount()+", Transitions: "+gc.gm.getTransitionCount());
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// static methods /////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Display a popup containing fatal error information.
	 * 
	 * @param	error_title		The title of the popup box.
	 * @param	error_message	The error message to be displayed.
	 * @param	error_shell		The shell in which the popup box will exist.
	 */	
	public static void fatalErrorPopup(String error_title, String error_message, Shell error_shell)
	{
		MessageBox error_popup = new MessageBox(error_shell, SWT.ICON_ERROR | SWT.CLOSE); 
		error_popup.setMessage(error_message);
		error_popup.setText(error_title);
		error_popup.open();			
		throw new RuntimeException(error_title + "\n" + error_message); 
	}		
}