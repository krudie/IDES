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
	Composite objectComposite;
	
	public ObjectExplorer(Composite parent){
		
		scrollComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		
		objectComposite = new Composite(scrollComposite, SWT.BORDER);
		
	

		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		objectComposite.setLayout(gridLayout);
	    
	    for(int i = 0; i < 15; i++){
			Label label = new Label(objectComposite, SWT.LEFT);
			label.setText("Property " + i + ":");
		
			Text textbox = new Text(objectComposite, SWT.SINGLE | SWT.BORDER);
			GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			gridData.grabExcessHorizontalSpace = true;
			gridData.widthHint = 150;
			textbox.setLayoutData(gridData);
		}
	    
	    objectComposite.layout();
	    
	    scrollComposite.setContent(objectComposite);
		
	    scrollComposite.setExpandHorizontal(true);
	    scrollComposite.setExpandVertical(true);
	    scrollComposite.setMinSize(objectComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT)); 
	    
		
	}
	
	
	
	
	
	
}
