/*
 * Created on Dec 15, 2004
 */
package com.aggressivesoftware.ides;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.aggressivesoftware.general.Ascii;

/**
 * This class handles the creation and management of everything that exists inside the LaTeX Tab of 
 * the main application.
 * 
 * @author Michael Wood
 */
public class TexManager 
{
	/**
     * The platform in which this TexManager will exist.
     */
	private GraphingPlatform gp = null;
	
	/**
     * The composite in which this TexManager's objects will be embedded.
     */
	private Composite parent = null;
	
	/**
     * The text area in the latex output frame
     */
	public Text text_body_area = null;	

	/**
	 * Message at bottom of screen
	 */
	private Label warning_label = null;
/*
	**
     * The control buttons
     *
	private Button tex_button = null,
				   dvips_button = null;	
*/
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// TexManager construction ////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
	
    /**
     * Construct the TexManager.
     *
     * @param	gp		The platform in which this TexManager will exist.
     * @param	parent	The composite in which this TexManager's objects will be embedded.
     */
	public TexManager(GraphingPlatform gp, Composite parent)
	{
		this.gp = gp;
		this.parent = parent;
		
		// create a layout for the content composite (for the widgits inside the composite)
		GridLayout gl_tex = new GridLayout();
		gl_tex.marginHeight = 3;
		gl_tex.marginWidth = 3;
		gl_tex.verticalSpacing = 2;
		gl_tex.horizontalSpacing = 0;
		gl_tex.numColumns = 2;
		parent.setLayout(gl_tex); // attach it to the composite
/*
		// texniccentre button
		tex_button = new Button(parent, SWT.PUSH);
		tex_button.setText(gp.rm.getString("tex.texniccentre.mtext"));
		tex_button.setToolTipText(gp.rm.getMessage("tex.texniccentre.text", gp.sv.tex_path + GraphControllerIO.tex_name));
		GridData gd_tex_button = new GridData();
		tex_button.setLayoutData(gd_tex_button);	
		tex_button.addSelectionListener ( new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { texButtonAction(); } } );

		// dvips button
		dvips_button = new Button(parent, SWT.PUSH);
		dvips_button.setText(gp.rm.getString("tex.dvips.mtext"));
		dvips_button.setToolTipText(gp.rm.getMessage("tex.dvips.text", gp.sv.tex_path + GraphControllerIO.dvi_name, gp.sv.tex_path + GraphControllerIO.page_name));
		GridData gd_dvips_button = new GridData();
		dvips_button.setLayoutData(gd_dvips_button);	
		dvips_button.addSelectionListener ( new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { dvipsButtonAction(); } } );
*/				
		// the text area		
		text_body_area = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd_text = new GridData(GridData.FILL_BOTH);
		gd_text.horizontalSpan = 2;
		text_body_area.setLayoutData(gd_text);
		
		text_body_area.addKeyListener
		(
			new KeyListener() 
			{
				public void keyReleased(KeyEvent e) {}
				public void keyPressed(KeyEvent e) 
				{
					// setup CRTL+a to handle "select all"
					if (e.stateMask == SWT.CTRL && e.keyCode == Ascii.a)
					{ text_body_area.selectAll(); }
				}
			}
		);

		warning_label = new Label(parent, SWT.WRAP);
		warning_label.setText(gp.rm.getString("tex.warning"));
		GridData gd_warning_label = new GridData(GridData.FILL_HORIZONTAL);
		gd_warning_label.horizontalSpan = 2;
		gd_warning_label.heightHint = warning_label.computeSize(gp.shell.getSize().x,SWT.DEFAULT).y;
		warning_label.setLayoutData(gd_warning_label);
		
		warning_label.addListener(SWT.Resize, new Listener() { public void handleEvent(Event event) { labelSizer(); } } );
	}
	
	private void labelSizer()
	{
		((GridData)warning_label.getLayoutData()).heightHint = warning_label.computeSize(gp.shell.getSize().x,SWT.DEFAULT).y;
	}
/*	
	private void texButtonAction()
	{
		String[] cmd = {"cmd.exe","/C",gp.sv.tex_path + GraphControllerIO.tex_name};
		SafeRuntime.execWithoutFeedback(cmd);
	}
	
	private void dvipsButtonAction()
	{
		String[] cmd1 = {"cmd.exe","/C","chdir /d " + gp.sv.tex_path + " && dvips -E " + GraphControllerIO.dvi_name + " && del " + GraphControllerIO.eps_name + " && ren " + GraphControllerIO.ps_name + " " + GraphControllerIO.eps_name};
		SafeRuntime.execWithFeedback(cmd1);
		
		generateLatex();
		
		String[] cmd2 = {"cmd.exe","/C", gp.sv.tex_path + GraphControllerIO.page_name};
		SafeRuntime.execWithoutFeedback(cmd2);
	}
	
	private void generateLatex() { gp.gc.io.generateLatex(); }
*/	
	public void resetState()
	{
		text_body_area.setText("");
//		tex_button.setEnabled(false);
//		dvips_button.setEnabled(false);
	}
	
	public void newLaTeX(String latex)
	{
		text_body_area.setText(latex);
//		tex_button.setEnabled(true);
//		dvips_button.setEnabled(true);		
	}
}
