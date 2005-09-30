/**
 * 
 */
package userinterface;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author edlund
 *
 */
public class ObjectExplorer {

	ScrolledComposite scrollComposite;
	Composite objectPanel;
	
	public ObjectExplorer(Composite parent){
		scrollComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL| SWT.BORDER);
				
		objectPanel = new Composite(scrollComposite, SWT.NONE);
		
		
		scrollComposite.setContent(objectPanel);
		

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		objectPanel.setLayout(layout);
		
		for(int i = 0; i < 15; i++){
			Label label = new Label(objectPanel, SWT.LEFT);
			label.setText("Property " + i + ":");
		
			Text textbox = new Text(objectPanel, SWT.SINGLE | SWT.BORDER);
			GridData gridData = new GridData(GridData.
					HORIZONTAL_ALIGN_FILL);
			gridData.grabExcessHorizontalSpace = true;
			gridData.widthHint = 200;
			textbox.setLayoutData(gridData);
			
		}
		objectPanel.setSize(objectPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
	}
	
	
	
	
	
}
