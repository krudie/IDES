/*
 * Created on Jan 18, 2005
 */
package com.aggressivesoftware.ides.graphcontrol;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aggressivesoftware.general.Ascii;
import com.aggressivesoftware.geometric.Point;
import com.aggressivesoftware.ides.GraphingPlatform;
import com.aggressivesoftware.ides.graphcontrol.graphparts.Label;

/**
 * @author Michael Wood
 */
public class FloatingText 
{	
	/**
     * The platform in which this FloatingText will exist.
     */
	private GraphingPlatform gp = null;

	/**
     * The Shell that displays this FloatingText.
     */
	private Shell shell = null;	

	/**
     * The Text in which the latex code will be typed.
     */
	private Text text = null;	
	
	/**
     * The current label being edited.
     */
	private Label label = null;			
				
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// FloatingText construction //////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
	
    /**
     * Construct the FloatingText.
     *
     * @param	gp	The platform in which this FloatingText will exist.
     */
	public FloatingText(GraphingPlatform gp)
	{		
		this.gp = gp;
		
		shell = new Shell(gp.shell, SWT.ON_TOP | SWT.RESIZE);
		shell.setLayout(new FillLayout());
		shell.setSize(gp.sv.floating_text_size.x,gp.sv.floating_text_size.y);
		
		Composite cmp = new Composite(shell, SWT.NULL);
		
		GridLayout grid_layout = new GridLayout();
		grid_layout.numColumns = 2;
		grid_layout.marginWidth = 0;
		grid_layout.marginHeight = 0;
		grid_layout.verticalSpacing = 0;
		grid_layout.horizontalSpacing = 0;
		cmp.setLayout(grid_layout);

		text = new Text(cmp, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd_text = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		gd_text.horizontalSpan = 2;
		text.setLayoutData(gd_text);
		
		CLabel msg = new CLabel(cmp, SWT.LEFT);
		msg.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		msg.setText(gp.rm.getString("floating.usage.text"));

		CLabel sizer = new CLabel(cmp, SWT.RIGHT);
		sizer.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		sizer.setImage(gp.rm.getImage(gp.rm.DRAG));
						
		shell.layout();
		
		shell.addShellListener
		(
			new ShellListener()
			{
		        public void	shellDeactivated(ShellEvent e) { shellDeactivatedAction(); }
				public void	shellActivated(ShellEvent e) { }
		        public void	shellClosed(ShellEvent e) {}
		        public void	shellDeiconified(ShellEvent e) {}
		        public void	shellIconified(ShellEvent e)  {}
			}
		);
		
		text.addKeyListener
		(
				new KeyListener() 
				{
					public void keyReleased(KeyEvent e) {}
					public void keyPressed(KeyEvent e) 
					{
						if ((e.character == (char)Ascii.RETURN) && !((e.stateMask & SWT.CTRL) == SWT.CTRL))
						{
							// save changes
							shell.setVisible(false);
						}
					}
				}
			);
	}
	
	private void shellDeactivatedAction()
	{
		// if they click on MAIN or EXTERNAL then we need to hide FLOAT	        	
		if (shell.getVisible()) { shell.setVisible(false); }

		if (label != null)
		{				
			gp.sv.floating_text_size = new Point(shell.getSize());

			// whenever we hide float, we also save changes
			if (!label.string_representation.equals(text.getText()))
			{
				label.string_representation = text.getText();
				label.render();
				
				// prevent doing it twice due to the double call due the the setVisible(false)
				label = null;
			}
		}
	}
	
	public void initialize(Point scaled_origin, Point origin, Label label)
	{
		Rectangle display_bounds = shell.getDisplay().getBounds();
		Rectangle shell_bounds = shell.getBounds();
		shell_bounds.x = Math.max(Math.min(gp.gc.j2dcanvas.toDisplay(scaled_origin.x,scaled_origin.y).x, display_bounds.width - shell_bounds.width), 0);
		shell_bounds.y = Math.max(Math.min(gp.gc.j2dcanvas.toDisplay(scaled_origin.x,scaled_origin.y).y, display_bounds.height - shell_bounds.height), 0);
		shell.setBounds(shell_bounds);
		
		this.label = label;
		label.setAnchor(origin,Label.CENTER);
		text.setText(label.string_representation);
	}

	public void setVisible(boolean visibility) 
	{ 
		shell.setVisible(visibility); 
		if (visibility) { text.forceFocus(); }
	}
	
	public void dispose() { shell.dispose(); }
}